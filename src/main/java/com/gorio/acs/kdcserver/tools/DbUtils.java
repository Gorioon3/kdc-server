package com.gorio.acs.kdcserver.tools;

import lombok.extern.log4j.Log4j;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class Name Db_Utils
 * Created by Gorio
 *
 * @author Gorio
 * @date 2018/1/24
 */
@Log4j
public class DbUtils {
    public static void close(ResultSet resultSet, Statement statement, Connection connection){
        close(resultSet);
        close(statement);
        close(connection);
    }

    public static void close(Connection connection){
        if (connection!=null){
            try {
                connection.close();
            }
            catch (SQLException s){
                log.error(s.getMessage(),s);
            }
        }
    }
    public static void close(ResultSet resultSet){
        if (resultSet != null){
            try {
                resultSet.close();
            }
            catch (Exception e){
                log.error(e.getMessage(),e);
            }
        }

    }
    public static void close(Statement statement){
        if (statement != null){
            try {
                statement.close();
            }
            catch (SQLException e){
                log.error(e.getMessage(),e);
            }
        }
    }
    public static void close(ResultSet resultSet, Connection connection){
        close(resultSet,null,connection);
    }
    public static void close(Statement statement,Connection connection){
        close(null,statement,connection);
    }
    public static void close( ResultSet resultSet,  Statement statement){
        close(resultSet,statement,null);
    }
}
