package com.gorio.acs.kdcserver.core.appcore;

import com.gorio.acs.kdcserver.dao.AppDAO;
import com.gorio.acs.kdcserver.entity.App;
import com.gorio.acs.kdcserver.exception.MessageProcessingException;
import com.gorio.acs.kdcserver.service.AppDAOImpl;
import com.gorio.acs.kdcserver.tools.MessageDecomposition;

import com.gorio.acs.kdcserver.tools.cipher.aes.Aes;
import com.gorio.acs.kdcserver.tools.cipher.rsa.RSAUtils;
import com.gorio.acs.kdcserver.tools.cipher.sha1.Sha1;
import com.gorio.acs.kdcserver.tools.compare.CompareHmac;
import com.gorio.acs.kdcserver.tools.compare.CompareTime;
import com.gorio.acs.kdcserver.tools.messageverification.MessageVerification;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.ibatis.transaction.jdbc.JdbcTransaction;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.sql.SQLException;


/**
 * Class Name RsaMessageDecomposition
 * Created by Gorio
 *
 * @author Gorio
 * @date 2018/1/25
 *
 */

@Log4j
@Setter
@Getter
@NoArgsConstructor
@PropertySource(value= {"classpath:key.properties"},ignoreResourceNotFound = true)
public class RsaMessageDecomposition {
    @Value("${key.public_key}")
    private static String kdcrsapublickey;
    @Value("${key.private_key}")
    private static String kdcrsaprivatekey;
    private String incomingmessage;
    private String remainingMessage;

    private String tag;
    private String hmacbyget;
    private String encryptstring;
    private String aesencryptstring;
    private String rsaencryptstring;

    private String hmacbymake;
    private String aesdecryptstring;
    private String rsadecryptstring;
    private String applicationUniqueIdentifier;
    private String applicationUniqueIdentifierSummaryValue;
    private String timebyget;
    private String timebynow;

    private String hmactosend;
    private String rsadecryptstringtosend;
    private String rsaencryptstringtosend;

    private App app;
    private AppDAO appDao ;

    private JdbcTransaction jdbcTransaction ;
    private String result;
    private String apprsaPublicKey;
    private static final int LEGAL_MESSAGE_LENGTH=110;


    public RsaMessageDecomposition(JdbcTransaction jdbcTransaction, AppDAOImpl appDao) {
        this.jdbcTransaction = jdbcTransaction;
        this.appDao = appDao;
    }

    void setResult(){
        this.result = this.getTag()+this.getHmactosend()+this.getRsaencryptstringtosend();
    }
    public RsaMessageDecomposition(String a){
        this.incomingmessage =a;
    }
    private void setRsadecryptstring(){
        try {
            byte[] bytes= RSAUtils.decryptByPrivateKey(this.getRsaencryptstring().getBytes(),kdcrsaprivatekey);
            this.setRsadecryptstring(new String(bytes));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void setHmacbymake(){
        this.setHmacbymake(Sha1.HmacSHA1Encrypt(this.getEncryptstring(),
                this.getApplicationUniqueIdentifierSummaryValue()
                        +this.getApplicationUniqueIdentifierSummaryValue()));
    }
    private void setAesdecryptstring(){
        this.setAesdecryptstring(Aes.Decrypt(this.getAesencryptstring(),
                this.getApplicationUniqueIdentifierSummaryValue()));
    }
    String getTimebynow() {
        return  MessageDecomposition.getTime();
    }
//300000[6] +HMAC(MAC，RSA()+AES())[40位]+ AES(MAC,[TIME])[64位]+RSA(KDC,[MAC,APP公钥])

    public void initializationdata() throws SQLException {
        if ("".equals(this.getIncomingmessage()) ||this.getIncomingmessage().length()<=LEGAL_MESSAGE_LENGTH){
            log.error("initialization data error",new MessageProcessingException());
        }
        else {
            this.setHmacbyget(this.getIncomingmessage().substring(6,46));
            this.setAesencryptstring(this.getIncomingmessage().substring(46,110));
            this.setRsaencryptstring(this.getIncomingmessage().substring(110));
            this.setEncryptstring(this.getIncomingmessage().substring(46));
            this.setRsadecryptstring();
            this.setApplicationUniqueIdentifier(this.getRsadecryptstring().substring(0,32));
            this.setApplicationUniqueIdentifierSummaryValue(MessageVerification.messagecompression(this.getApplicationUniqueIdentifier()).toString());
            this.setHmacbymake();
            boolean b = CompareHmac.compareHMAC(this.getHmacbymake(),this.getHmacbyget());
            if (b){
                this.setAesdecryptstring();
                this.setTimebyget(this.getAesdecryptstring().substring(0,10));
                b= CompareTime.compareTime(this.getTimebyget(),this.getTimebynow());
                if (b){
                    //公钥
                    this.setRemainingMessage(this.getRsadecryptstring().substring(32));
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
