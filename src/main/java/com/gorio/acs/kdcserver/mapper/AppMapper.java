package com.gorio.acs.kdcserver.mapper;

import com.gorio.acs.kdcserver.entity.App;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Class Name AppMapper
 * Created by Gorio
 *
 * @author Gorio
 * @date 2018/2/25
 */
public class AppMapper implements RowMapper<App> {
    @Override
    public App mapRow(ResultSet resultSet, int i) throws SQLException {
        App app = new App();
        app.setApplicationIdentification(resultSet.getInt("ApplicationIdentification"));
        app.setApplicationUniqueIdentifier(resultSet.getString("ApplicationUniqueIdentifier"));
        app.setRsaPublicKey(resultSet.getString("RsaPublicKey"));
        return app;
    }
}
