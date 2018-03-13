package com.gorio.acs.kdcserver.mapper;

import com.gorio.acs.kdcserver.entity.Server;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Class Name ServerMapper
 * Created by Gorio
 *
 * @author Gorio
 * @date 2018/2/25
 */
public class ServerMapper implements RowMapper<Server> {

    @Override
    public Server mapRow(ResultSet resultSet, int i) throws SQLException {
        Server server = new Server();
        server.setServerIdentification(resultSet.getInt("serverIdentification"));
        server.setMasterSharedKey(resultSet.getString("masterSharedKey"));
        server.setServerIP(resultSet.getString("serverIP"));
        return server;
    }
}
