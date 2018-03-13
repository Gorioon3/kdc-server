package com.gorio.acs.kdcserver.core.appcore;

import com.gorio.acs.kdcserver.service.ClientDAOImpl;
import com.gorio.acs.kdcserver.tools.MessageDecomposition;
import com.gorio.acs.kdcserver.tools.cipher.dh.Dh;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.transaction.jdbc.JdbcTransaction;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.Map;

/**
 * Class Name DhKeyAesMessageDecomposition
 * Created by Gorio
 *
 * @author Gorio
 * @date 2018/1/25
 */
@Slf4j
@Getter
@Setter
@Component
public class DhKeyAesMessageDecomposition extends AesMessageDecomposition{
    /**
     * DH协议公钥
     */
    private byte[] publicKey;
    /**
     * DH协议私钥
     */
    private byte[] privateKey;
    /**
     *H协议协商结果，会话密钥
     */
    private byte[] key2;
    /**
     *APP公钥
     */
    private String apubkey ="";

    private  String kpubkey ="";


    /**
     * APP公钥对
     */
    private Map<String, Object> keyMap;

    public DhKeyAesMessageDecomposition(String args){
        super(args);
    }
    @Autowired
    public DhKeyAesMessageDecomposition(JdbcTransaction jdbcTransactionObject, ClientDAOImpl clientDao) {
        super(jdbcTransactionObject, clientDao);
    }

    private void setDecrypttosend() {
        this.setDecrypttosend(MessageDecomposition.getTime()+this.getKpubkey()) ;
    }


    /**
     * 300010+user_id{6}+hmac(kc,aes()){40}+AES(kc,[time{10},APP_public_key])
     * return 300010+user_id{6}+hmac(kc,aes()){40}+AES(kc,[time{10},ks,KDC_public_key]);
     */
    @Override
    public void initializationdata() throws SQLException {
        this.setTag("300010");
        super.initializationdata();
        this.setApubkey(this.getRemainingMessage());
        this.setKeyMap(Dh.initKey(Base64.decodeBase64(this.getApubkey())));
        this.setPublicKey(Dh.getPublicKey(this.getKeyMap()));
        this.setPrivateKey(Dh.getPrivateKey(this.getKeyMap()));

        this.setKey2(Dh.getSecretKey(Base64.decodeBase64(this.getApubkey()),this.getPrivateKey()));
        this.setKpubkey(Base64.encodeBase64String(this.getPublicKey()));

        this.setDecrypttosend();
        this.setEncrypttosend();
        this.setHmactosend();
        this.setResult();
    }
}
