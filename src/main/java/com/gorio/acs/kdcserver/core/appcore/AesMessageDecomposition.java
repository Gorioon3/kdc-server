package com.gorio.acs.kdcserver.core.appcore;

import com.gorio.acs.kdcserver.dao.ClientDAO;
import com.gorio.acs.kdcserver.entity.Client;
import com.gorio.acs.kdcserver.exception.DataAccessException;
import com.gorio.acs.kdcserver.exception.MessageProcessingException;
import com.gorio.acs.kdcserver.service.ClientDAOImpl;

import com.gorio.acs.kdcserver.tools.MessageDecomposition;
import com.gorio.acs.kdcserver.tools.cipher.aes.Aes;
import com.gorio.acs.kdcserver.tools.cipher.sha1.Sha1;
import com.gorio.acs.kdcserver.tools.compare.CompareHmac;
import com.gorio.acs.kdcserver.tools.compare.CompareTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.transaction.jdbc.JdbcTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;


/**
 * Class Name AesMessageDecomposition
 * Created by Gorio
 *
 * @author Gorio
 * @date 2018/1/25
 */

@Slf4j
@Getter
@NoArgsConstructor
@Setter
public class AesMessageDecomposition {

    /*
        variable
     */

    private String incomingmessage;

    private String userIdentification;
    private String usermasterSharedKey;

    private String timebyget;
    private String timebynow;

    private String remainingMessage;

    private String encryptstring;
    private String decryptstring;

    private String hmacbyget;
    private String hmacbymake;

    private Client client;
    private ClientDAO clientDao;
    private String tag ;
    /**
     *要发送的密文字符串
     */
    private String encrypttosend ;
    /**
     *要发送的明文字符串
     */
    private String decrypttosend ;
    /**
     *要发送的HMAC
     */
    private String hmactosend ;
    private String result ;
    public static final int LEGAL_MESSAGE_LENGTH = 52;
    public static final int LEGAL_KEY_LENGTH =16;
    private JdbcTransaction jdbcTransactionObject;


    public AesMessageDecomposition(JdbcTransaction jdbcTransaction, ClientDAOImpl clientDao) {
        this.jdbcTransactionObject = jdbcTransaction;
        this.clientDao = clientDao;
    }

    /*
    method
    */



    void setResult() {
        this.setResult(this.getTag()+this.getUserIdentification()+this.getHmactosend()+this.getEncrypttosend());
    }
    AesMessageDecomposition(String incomingmessage){
        if ("".equals(incomingmessage)){
            log.error("消息分解时出错，输入指令为空",new MessageProcessingException());
        }
        else {
            this.setIncomingmessage(incomingmessage);
        }
        clientDao = new ClientDAOImpl();
    }
    private void setUserIdentification(String userIdentification) throws SQLException {
        if ("".equals(userIdentification)){
            log.error("消息分解时出错，IDC为空",new MessageProcessingException());
            return;
        }
        this.userIdentification = userIdentification;
        try {
            this.setClient(clientDao.findClient(Integer.getInteger(userIdentification)));
            jdbcTransactionObject.commit();
        }
        catch (DataAccessException | SQLException e){
            log.error(e.getMessage(),e);
            jdbcTransactionObject.rollback();
        }
        this.setUsermasterSharedKey(this.getClient().getMasterSharedKey());

    }
    private void setTimebyget(String timebyget){
        SimpleDateFormat format=new SimpleDateFormat("yyMMddHHmm");
        try {
            format.parse(timebyget);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.timebyget=timebyget;
    }
    private void setTimebynow() {
        this.timebynow = MessageDecomposition.getTime();
    }
    private void setEncryptstring(String encryptstring) {
        this.encryptstring = encryptstring.length()%2 == 1?encryptstring.substring(0,encryptstring.length()-1):encryptstring;
    }
    private void setDecryptstring(String encryptstring, String usermasterSharedKey) {
        if ("".equals(usermasterSharedKey) ||usermasterSharedKey.length()!=LEGAL_KEY_LENGTH)
        {
            log.error("setDeCryptStr error , the ks is wrong",new DataAccessException());
            return;
        }
        this.decryptstring = Aes.Decrypt(encryptstring,usermasterSharedKey);

    }
    private void setHmacbymake(String encryptstring, String usermasterSharedKey) {
        int doubled =2;
        if ("".equals(usermasterSharedKey) ||usermasterSharedKey.length()!=LEGAL_KEY_LENGTH){
            log.error("setHmacByMAke error",new MessageProcessingException());
            return;
        }
        if (encryptstring.length()%doubled == 0){
            this.hmacbymake = Sha1.HmacSHA1Encrypt(encryptstring,usermasterSharedKey+usermasterSharedKey);
        }
    }
    void setHmactosend() {
        this.setHmactosend(Sha1.HmacSHA1Encrypt(this.getEncrypttosend(),this.getUsermasterSharedKey()+this.getUsermasterSharedKey()));
    }
    void setEncrypttosend() {
        this.setEncrypttosend(Aes.Encrypt(this.getDecrypttosend(),this.getUsermasterSharedKey()));
    }
    /**
     * TAG+userid[6]+hmac(kc,aes())[40]+AES(kc,[time,...])
     */
    public void initializationdata() throws SQLException {
        if ("".equals(this.getIncomingmessage()) ||this.getIncomingmessage().length()<=LEGAL_MESSAGE_LENGTH){
            log.error("initialization data error",new MessageProcessingException());
        }
        else {
            this.setUserIdentification(this.getIncomingmessage().substring(6,12));
            this.setHmacbyget(this.getIncomingmessage().substring(12,52));
            this.setEncryptstring(this.getIncomingmessage().substring(52));
            this.setHmacbymake(this.getEncryptstring(),this.getUsermasterSharedKey());
            boolean b = CompareHmac.compareHMAC(this.getHmacbyget(),this.getHmacbymake());
            if (b){
                this.setDecryptstring(this.getEncryptstring(),this.getUsermasterSharedKey());
                this.setTimebyget(this.getDecryptstring().substring(0,10));
                this.setTimebynow();
                b= CompareTime.compareTime(this.getTimebyget(),this.getTimebynow());
                if (b){
                    this.setRemainingMessage(this.getDecryptstring().substring(10));
                }
                else {
                    log.error("CompareTime error , the get_one is not equal the make_one",new MessageProcessingException());
                }
            }
            else {
                log.error("CompareHMAc error , the get_one is not equal the make_one",new MessageProcessingException());
            }
        }
    }
}
