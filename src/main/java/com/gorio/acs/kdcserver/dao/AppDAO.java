package com.gorio.acs.kdcserver.dao;

import com.gorio.acs.kdcserver.entity.App;
import com.gorio.acs.kdcserver.exception.DataAccessException;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.util.List;

/**
 * Class Name AppDAO
 * Created by Gorio
 *
 * @author Gorio
 * @date 2018/2/25
 */
public interface AppDAO extends SuperDAO {

    /**
     * 保存APP信息
     * @param app
     * @return 成功true ，失败 false
     * @throws DataAccessException
     */
    boolean saveApp(App app)throws DataAccessException;

    /**
     * 删除应用信息
     * @param applicationUniqueIdentifier
     * @return 成功true ，失败 false
     * @throws DataAccessException
     */
    boolean deleteApp(String applicationUniqueIdentifier)throws DataAccessException;

    /**
     * 更新应用信息
     * @param app
     * @return 成功true ，失败 false
     * @throws DataAccessException
     */
    boolean updateApp(App app)throws DataAccessException;

    /**
     * 查找应用信息
     * @param applicationUniqueIdentifier

     * @return 成功应用对象 ，失败NULL
     * @throws DataAccessException
     */
    App findApp(String applicationUniqueIdentifier)throws DataAccessException;

    /**
     * 查找所有应用信息

     * @return 应用列表
     * @throws DataAccessException
     */
    List<App> findAllApp()throws DataAccessException;


}
