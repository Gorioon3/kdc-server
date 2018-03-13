package com.gorio.acs.kdcserver.core.appcore;

import com.gorio.acs.kdcserver.dao.ClientDAO;
import com.gorio.acs.kdcserver.entity.Client;
import com.gorio.acs.kdcserver.exception.DataAccessException;
import com.gorio.acs.kdcserver.service.AppDAOImpl;
import com.gorio.acs.kdcserver.service.ClientDAOImpl;
import com.gorio.acs.kdcserver.tools.KeyUpdate;
import com.gorio.acs.kdcserver.tools.MessageDecomposition;
import com.gorio.acs.kdcserver.tools.cipher.rsa.RSAUtils;
import com.gorio.acs.kdcserver.tools.cipher.sha1.Sha1;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.ibatis.transaction.jdbc.JdbcTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

/**
 * Class Name UpdateUserMasterSharedKeyMessageDecomposition
 * Created by Gorio
 *
 * @author Gorio
 * @date 2018/1/25
 * Update user key
 * 300100 +HMAC(MAC，RSA()+AES())[40位]+ AES(MAC,[TIME])[64位]+RSA(KDC,[MAC,UID])
 *  return 300100 +HMAC(MAC，RSA())[40位]+ RSA(APP公钥,[KC,TIME])
 */
@Setter
@Getter
@Log4j
@Component
public class UpdateUserMasterSharedKeyMessageDecomposition extends RsaMessageDecomposition {
    private String userIdentification;
    private Client client;
    private ClientDAO clientDao;
    public UpdateUserMasterSharedKeyMessageDecomposition(String a){
        super(a);
    }
    @Autowired
    public UpdateUserMasterSharedKeyMessageDecomposition(JdbcTransaction jdbcTransaction, AppDAOImpl appDao
            , ClientDAOImpl clientDao) {
        super(jdbcTransaction, appDao);
        this.clientDao=clientDao;
    }

    @Override
    public void initializationdata() throws SQLException {
        this.setTag("300100");
        super.initializationdata();
        try {
            this.setApp(this.getAppDao().findApp(this.getApplicationUniqueIdentifier()));
            this.setClient(this.getClientDao().findClient(Integer.getInteger(this.getUserIdentification())));
            this.getClient().setMasterSharedKey(KeyUpdate.getRandomString(16));
            this.getClientDao().updateClient(this.getClient());
            this.getJdbcTransaction().commit();
        } catch (DataAccessException | SQLException e) {
            e.printStackTrace();
            this.getJdbcTransaction().rollback();
        }
        this.setApprsaPublicKey(this.getApp().getRsaPublicKey());
        this.setRsadecryptstringtosend(this.getClient().getMasterSharedKey()+ MessageDecomposition.getTime());
        try {
            this.setRsaencryptstringtosend(new String( RSAUtils.encryptByPublicKey(
                    this.getRsadecryptstringtosend().getBytes(),this.getApprsaPublicKey())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.setHmactosend(Sha1.HmacSHA1Encrypt(this.getRsaencryptstringtosend(),
                this.getApplicationUniqueIdentifierSummaryValue()
                        +this.getApplicationUniqueIdentifierSummaryValue()));
        this.setResult();
    }
}
