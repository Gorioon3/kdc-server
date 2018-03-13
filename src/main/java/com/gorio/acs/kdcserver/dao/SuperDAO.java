package com.gorio.acs.kdcserver.dao;

import javax.sql.DataSource;

/**
 * Class Name SuperDAO
 * Created by Gorio
 *
 * @author Gorio
 * @date 2018/2/25
 */
public interface SuperDAO {
    static final Integer LEGAL_KEY_LENGTH =16;
    /**
     * This is the method to be used to initialize
     * database resources ie.connection;
     * @param ds DataSource
     */
    void setDataSource(DataSource ds);
}
