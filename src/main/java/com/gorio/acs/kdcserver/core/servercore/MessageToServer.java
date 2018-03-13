package com.gorio.acs.kdcserver.core.servercore;

import com.gorio.acs.kdcserver.dao.AppDAO;
import com.gorio.acs.kdcserver.dao.ServerDAO;
import com.gorio.acs.kdcserver.entity.App;
import com.gorio.acs.kdcserver.entity.Server;
import com.gorio.acs.kdcserver.exception.MessageProcessingException;
import com.gorio.acs.kdcserver.service.AppDAOImpl;
import com.gorio.acs.kdcserver.service.ServerDAOImpl;
import com.gorio.acs.kdcserver.tools.MessageDecomposition;
import com.gorio.acs.kdcserver.tools.cipher.aes.Aes;
import com.gorio.acs.kdcserver.tools.cipher.rsa.RSAUtils;
import com.gorio.acs.kdcserver.tools.cipher.sha1.Sha1;
import com.gorio.acs.kdcserver.tools.compare.CompareHmac;
import com.gorio.acs.kdcserver.tools.compare.CompareTime;
import com.gorio.acs.kdcserver.tools.messageverification.MessageVerification;
import lombok.*;
import lombok.extern.log4j.Log4j;
import org.apache.ibatis.transaction.jdbc.JdbcTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

/**
 * @author Gorio
 *    310011 新服务器
 *    310001 更新主密钥
 */
@Log4j
@Setter
@Getter
@NoArgsConstructor
@Component
@PropertySource(value= {"classpath:key.properties"},ignoreResourceNotFound = true)
class MessageToServer {
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
    /**
     * 随机字符串16位
     */
    private String serverMac;
    private String serverMacSummaryValue;
    private String timebyget;
    private String timebynow;

    private String hmactosend;
    private String rsadecryptstringtosend;
    private String rsaencryptstringtosend;

    private Server server;
    private ServerDAO serverDAO ;

    private JdbcTransaction jdbcTransaction ;
    private String result;

    private static final int LEGAL_MESSAGE_LENGTH=110;


    @Autowired
    public MessageToServer(JdbcTransaction jdbcTransaction, ServerDAOImpl serverDAO) {
        this.jdbcTransaction = jdbcTransaction;
        this.serverDAO = serverDAO;
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
                this.getServerMacSummaryValue()
                        +this.getServerMacSummaryValue()));
    }
    private void setAesdecryptstring(){
        this.setAesdecryptstring(Aes.Decrypt(this.getAesencryptstring(),
                this.getServerMacSummaryValue()));
    }

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
            this.setServerMac(this.getRsadecryptstring().substring(0,16));
            this.setServerMacSummaryValue(MessageVerification.messagecompression(this.getServerMac()).toString());
            this.setHmacbymake();
            boolean b = CompareHmac.compareHMAC(this.getHmacbymake(),this.getHmacbyget());
            if (b){
                this.setAesdecryptstring();
                this.setTimebyget(this.getAesdecryptstring().substring(0,10));
                b= CompareTime.compareTime(this.getTimebyget(),MessageDecomposition.getTime());
                if (b){
                    //RemainingMessage == Server ID
                    this.setRemainingMessage(this.getRsadecryptstring().substring(16));
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
