package com.gorio.acs.kdcserver.core.appcore;

import com.gorio.acs.kdcserver.entity.App;
import com.gorio.acs.kdcserver.exception.DataAccessException;
import com.gorio.acs.kdcserver.service.AppDAOImpl;

import com.gorio.acs.kdcserver.tools.cipher.rsa.RSAUtils;
import com.gorio.acs.kdcserver.tools.cipher.sha1.Sha1;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.transaction.jdbc.JdbcTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

/**
 * Class Name CreateAppRsaMessageDecomposition
 * Created by Gorio
 *
 * @author Gorio
 * @date 2018/1/25
 * New App
 * 300000 +HMAC(MAC，RSA()+AES())[40位]+ AES(MAC,[TIME])[64位]+RSA(KDC,[MAC,APP公钥])
 * return 300000 +HMAC(MAC，RSA())[40位]+ RSA(APP公钥,[BOOLEAN,TIME])
 * BOOLEAN->[ Accept, Refuse]
 */
@Slf4j
@Getter
@Setter
@NoArgsConstructor
@Component
public class CreateNewAppRsaMessageDecomposition extends RsaMessageDecomposition {
    private String returnresult;
    public CreateNewAppRsaMessageDecomposition(String a ){
        super(a);
    }
    @Autowired
    public CreateNewAppRsaMessageDecomposition(JdbcTransaction jdbcTransaction, AppDAOImpl appDao) {
        super(jdbcTransaction, appDao);
    }
    @Override
    public void initializationdata() throws SQLException {
        super.initializationdata();
        this.setTag("300000");
        this.setApprsaPublicKey(this.getRemainingMessage());
        this.setApp(new App());
        this.getApp().setApplicationUniqueIdentifier(this.getApplicationUniqueIdentifier());
        this.getApp().setRsaPublicKey(this.getApprsaPublicKey());

        try {
            this.setReturnresult(this.getAppDao().saveApp(getApp())?"Accept":"Refuse");
            this.getJdbcTransaction().commit();
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        this.setRsadecryptstringtosend(this.getReturnresult()+this.getTimebynow());
        try {
            this.setRsaencryptstringtosend(new String( RSAUtils.encryptByPublicKey(this.getRsadecryptstringtosend().getBytes(),this.getApprsaPublicKey())));
        } catch (Exception e) {
            e.printStackTrace();
            this.getJdbcTransaction().rollback();
        }
        this.setHmactosend(Sha1.HmacSHA1Encrypt(this.getRsaencryptstringtosend(),this.getApplicationUniqueIdentifierSummaryValue()+this.getApplicationUniqueIdentifierSummaryValue()));
        this.setResult();
    }
}
