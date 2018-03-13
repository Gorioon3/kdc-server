package com.gorio.acs.kdcserver.config;

import com.gorio.acs.kdcserver.core.appcore.*;
import com.gorio.acs.kdcserver.core.servercore.NewMasterKeyToServer;
import com.gorio.acs.kdcserver.service.AppDAOImpl;
import com.gorio.acs.kdcserver.service.ClientDAOImpl;
import com.gorio.acs.kdcserver.service.ServerDAOImpl;
import org.apache.ibatis.transaction.jdbc.JdbcTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Class Name SpringConfig
 * Created by Gorio
 *
 * @author Gorio
 * @date 2018/2/26
 */
@Configuration
@ComponentScan(basePackages = "com.gorio.acs.kdcserver")
@PropertySource(value= {"classpath:jdbc.properties"},ignoreResourceNotFound = true)
@ImportResource(locations={"classpath*:*bean*.xml"})
public class SpringConfig {


    @Bean(destroyMethod = "close")@Scope(value = "prototype")@Autowired@Qualifier("dataSource")
    public Connection connection(DataSource dataSource) throws SQLException {
        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);
        return connection;
    }
    @Bean@Scope(value = "prototype")@Autowired
    public JdbcTransaction jdbcTransaction(Connection connection){
        return new JdbcTransaction(connection);
    }
    @Bean@Scope(value = "prototype")@Autowired
    public CreateNewAppRsaMessageDecomposition newAppRsaMessageDecomposition
            (JdbcTransaction jdbcTransaction, AppDAOImpl appDAO){
        return new CreateNewAppRsaMessageDecomposition(jdbcTransaction,appDAO);
    }
    @Bean@Scope(value = "prototype")@Autowired
    public CreateNewClientRsaMessageDecomposition newClientRsaMessageDecomposition
            (JdbcTransaction jdbcTransaction, AppDAOImpl appDao, ClientDAOImpl clientDao){
        return new CreateNewClientRsaMessageDecomposition(jdbcTransaction,appDao,clientDao);
    }
    @Bean@Scope(value = "prototype")@Autowired
    public DhKeyAesMessageDecomposition dhKeyAesMessageDecomposition
            (JdbcTransaction jdbcTransactionObject, ClientDAOImpl clientDao){
        return new DhKeyAesMessageDecomposition(jdbcTransactionObject,clientDao);
    }
    @Bean@Scope(value = "prototype")@Autowired
    public SessionKeyAesMessageDecomposition sessionKeyAesMessageDecomposition
            (JdbcTransaction jdbcTransactionObject, ClientDAOImpl clientDao, ServerDAOImpl serverDao){
        return new SessionKeyAesMessageDecomposition(jdbcTransactionObject,clientDao,serverDao);
    }
    @Scope(value = "prototype")@Autowired@Bean
    public UpdateUserMasterSharedKeyMessageDecomposition updateUserMasterSharedKeyMessageDecomposition
            (JdbcTransaction jdbcTransaction, AppDAOImpl appDao, ClientDAOImpl clientDao){
        return new UpdateUserMasterSharedKeyMessageDecomposition(jdbcTransaction,appDao,clientDao);
    }
    @Bean@Scope(value = "prototype")@Autowired
    public NewMasterKeyToServer newMasterKeyToServer(JdbcTransaction jdbcTransaction, ServerDAOImpl serverDAO){
        return new NewMasterKeyToServer(jdbcTransaction,serverDAO);
    }
}

