package com.gorio.acs.kdcserver.mapper;

import com.gorio.acs.kdcserver.entity.Client;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Class Name ClientMapper
 * Created by Gorio
 *
 * @author Gorio
 * @date 2018/2/25
 */
public class ClientMapper implements RowMapper<Client> {
    @Override
    public Client mapRow(ResultSet resultSet, int i) throws SQLException {
        Client client = new Client();
        client.setUserIdentification(resultSet.getInt("UserIdentification"));
        client.setMasterSharedKey(resultSet.getString("MasterSharedKey"));
        client.setSessionKey(resultSet.getString("SessionKey"));
        return client;
    }
}
