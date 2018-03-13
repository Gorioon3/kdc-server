package com.gorio.acs.kdcserver.core.appcore;

import com.gorio.acs.kdcserver.dao.ClientDAO;
import com.gorio.acs.kdcserver.entity.Client;
import com.gorio.acs.kdcserver.exception.DataAccessException;
import com.gorio.acs.kdcserver.service.AppDAOImpl;
import com.gorio.acs.kdcserver.service.ClientDAOImpl;
import com.gorio.acs.kdcserver.tools.MessageDecomposition;
import com.gorio.acs.kdcserver.tools.cipher.rsa.RSAUtils;
import com.gorio.acs.kdcserver.tools.cipher.sha1.Sha1;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.transaction.jdbc.JdbcTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

/**
 * Class Name CreateNewClientRsaMessageDecomposition
 * Created by Gorio
 *
 * @author Gorio
 * @date 2018/1/25
 *
 * New User
 * 300001 +HMAC(MAC，RSA()+AES())[40位]+ AES(MAC,[TIME])[64位]+RSA(KDC,[MAC])
 * return 300001 +HMAC(MAC，RSA())[40位]+ RSA(APP公钥,[BOOLEAN,TIME])
 * BOOLEAN->[ 用户ID, Refuse]
 */
@Setter
@Getter
@Component
public class CreateNewClientRsaMessageDecomposition extends RsaMessageDecomposition{
    private String returnresult;
    private Client client;
    private  ClientDAO clientDao ;
    private String clientid;
    private final static int LEGAL_USERNAME_LENGTH = 6;


    public CreateNewClientRsaMessageDecomposition(String a){
        super(a);
    }
    @Autowired
    public CreateNewClientRsaMessageDecomposition(JdbcTransaction jdbcTransaction, AppDAOImpl appDao, ClientDAOImpl clientDao) {
        super(jdbcTransaction, appDao);
        this.clientDao = clientDao;
    }
    private String stringzorepadding(int useridentification){
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(useridentification);
        int lenth = stringBuffer.length();
        stringBuffer = new StringBuffer();
        for (int i = lenth; i <LEGAL_USERNAME_LENGTH ; i++) {
            stringBuffer.append(0);
        }
        stringBuffer.append(useridentification);
        return stringBuffer.toString();
    }
    @Override
    public void initializationdata() throws SQLException {
        super.initializationdata();
        this.setTag("300001");
        try {
            this.setApp(this.getAppDao().findApp(this.getApplicationUniqueIdentifier()));
            this.setClient(this.getClientDao().saveNewClient());
            this.getJdbcTransaction().commit();
        } catch (DataAccessException e) {
            e.printStackTrace();
            this.getJdbcTransaction().rollback();
        }
        this.setApprsaPublicKey(this.getApp().getRsaPublicKey());
        this.setClientid(this.stringzorepadding(this.getClient().getUserIdentification()));
        this.setRsadecryptstringtosend("000000".equals(this.getClientid()) ?"Refuse":this.getClientid()+ MessageDecomposition.getTime());
        try {
            this.setRsaencryptstringtosend(new String( RSAUtils.encryptByPublicKey(this.getRsadecryptstringtosend().getBytes(),this.getApprsaPublicKey())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.setHmactosend(Sha1.HmacSHA1Encrypt(this.getRsaencryptstringtosend(),this.getApplicationUniqueIdentifierSummaryValue()+this.getApplicationUniqueIdentifierSummaryValue()));
        this.setResult();
    }
}
