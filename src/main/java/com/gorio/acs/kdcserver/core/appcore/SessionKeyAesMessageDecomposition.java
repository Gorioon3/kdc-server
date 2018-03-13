package com.gorio.acs.kdcserver.core.appcore;

import com.gorio.acs.kdcserver.dao.ServerDAO;
import com.gorio.acs.kdcserver.entity.Server;
import com.gorio.acs.kdcserver.exception.DataAccessException;
import com.gorio.acs.kdcserver.exception.MessageProcessingException;
import com.gorio.acs.kdcserver.service.ClientDAOImpl;
import com.gorio.acs.kdcserver.service.ServerDAOImpl;
import com.gorio.acs.kdcserver.tools.KeyUpdate;
import com.gorio.acs.kdcserver.tools.MessageDecomposition;

import com.gorio.acs.kdcserver.tools.cipher.aes.Aes;
import lombok.Getter;
import lombok.Setter;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.transaction.jdbc.JdbcTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

/**
 * Class Name SessionKeyAesMessageDecomposition
 * Created by Gorio
 *
 * @author Gorio
 * @date 2018/1/25
 */
@Component
@Slf4j
@Getter
public class SessionKeyAesMessageDecomposition extends AesMessageDecomposition {
    private String serverIdentification;
    private @Setter String serverMasterSharedKey;
    private String sessionKey;
    private @Setter Server server ;
    private ServerDAO serverDao;


    private void setServerIdentification() throws SQLException {
        if ("".equals(this.getRemainingMessage())){
            log.error("setIDS error , the DefaultStr is null",new MessageProcessingException());
        }
        this.serverIdentification = this.getRemainingMessage().substring(0,6);
        try{
            this.setServer(this.getServerDao().findServer(Integer.getInteger(serverIdentification)));
            this.getJdbcTransactionObject().commit();
        }
        catch (DataAccessException |SQLException e){
            log.error(e.getMessage(),e);
            this.getJdbcTransactionObject().rollback();
        }
        this.setServerMasterSharedKey(this.getServer().getMasterSharedKey());
    }

    private void setSessionKey() throws SQLException {
        this.sessionKey = KeyUpdate.getRandomString(16);
        this.getClient().setSessionKey(sessionKey);
        try {
            this.getClientDao().updateClient(this.getClient());
            this.getJdbcTransactionObject().commit();
        } catch (DataAccessException |SQLException e) {
            log.error(e.getMessage(),e);
            this.getJdbcTransactionObject().rollback();
        }
    }
    @Autowired
    public SessionKeyAesMessageDecomposition(JdbcTransaction jdbcTransactionObject, ClientDAOImpl clientDao, ServerDAOImpl serverDao) {
        super(jdbcTransactionObject, clientDao);
        this.serverDao = serverDao;
    }

    public SessionKeyAesMessageDecomposition(String args){
        super(args);

    }

    private void setDecrypttosend() {
        String timetosend = MessageDecomposition.getTime();
        this.setDecrypttosend(this.getSessionKey()+timetosend + Aes.Encrypt(this.getSessionKey()+timetosend ,this.getServerMasterSharedKey()));
    }

    @Override
    public void initializationdata() throws SQLException {
        this.setTag("300011");
        super.initializationdata();
        this.setServerIdentification();
        this.setSessionKey();
        this.setDecrypttosend();
        this.setEncrypttosend();
        this.setHmactosend();
        this.setResult();
    }
}
