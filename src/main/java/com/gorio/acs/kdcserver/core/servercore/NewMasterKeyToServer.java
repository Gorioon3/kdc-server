package com.gorio.acs.kdcserver.core.servercore;

import com.gorio.acs.kdcserver.dao.ServerDAO;
import com.gorio.acs.kdcserver.entity.Server;
import com.gorio.acs.kdcserver.exception.DataAccessException;
import com.gorio.acs.kdcserver.exception.MessageProcessingException;
import com.gorio.acs.kdcserver.service.ServerDAOImpl;
import com.gorio.acs.kdcserver.tools.KeyUpdate;
import com.gorio.acs.kdcserver.tools.MessageDecomposition;
import com.gorio.acs.kdcserver.tools.cipher.aes.Aes;
import com.gorio.acs.kdcserver.tools.cipher.sha1.Sha1;
import com.gorio.acs.kdcserver.tools.compare.CompareHmac;
import com.gorio.acs.kdcserver.tools.compare.CompareTime;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.transaction.jdbc.JdbcTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @author Gorio
 * 310001 更新主密钥
 * server -> kdc 请求更新主密钥(MAC ->生成的16位随机字符串）
 * 310001+HMAC(MAC，RSA()+AES())[40位]+ AES(MACsv,[TIME])[64位]+RSA(KDC,[MAC(16),SID(6)])
 * kdc -> server 发放主密钥 nKs= NEW KEY OF SERVER
 * 310001+HMAC(MACsv，RSA())[40位]+ AES(MACsv,[TIME,nKS])
 * */
@Getter@Slf4j@Component@Setter
public class NewMasterKeyToServer extends MessageToServer {
    private String serverNewMasterKey;
    private String aesEncodingStringToSend;
    @Autowired
    public NewMasterKeyToServer(JdbcTransaction jdbcTransaction, ServerDAOImpl serverDAO) {
        super(jdbcTransaction, serverDAO);
        serverNewMasterKey = KeyUpdate.getRandomString(16);
    }
    private void setResult(){
        this.setResult(this.getTag()+this.getHmactosend()+this.getAesEncodingStringToSend());
    }
    @Override
    public void initializationdata() throws SQLException {
        this.setTag("310001");
        super.initializationdata();
        Server server=null;
        try {
            server = this.getServerDAO().findServer(Integer.parseInt(this.getRemainingMessage()));
            getJdbcTransaction().commit();
        } catch (DataAccessException | SQLException e) {
            e.printStackTrace();
            getJdbcTransaction().rollback();
        }
        this.setServer(server);
        this.getServer().setMasterSharedKey(this.getServerNewMasterKey());
        //将新密钥保存
        try {
            this.getServerDAO().updateServer(this.getServer());
            this.getJdbcTransaction().commit();
        } catch (DataAccessException | SQLException e) {
            e.printStackTrace();
            getJdbcTransaction().rollback();
        }
        //310001+HMAC(MACsv，AES())[40位]+ AES(MACsv,[TIME,nKS])
        this.setAesEncodingStringToSend(Aes.Encrypt(MessageDecomposition.getTime()+
                this.getServerNewMasterKey(),this.getServerMacSummaryValue()));
        this.setHmactosend(Sha1.HmacSHA1Encrypt(this.getAesEncodingStringToSend(),
                this.getServerMacSummaryValue()+this.getServerMacSummaryValue()));
        this.setResult();
    }
}
