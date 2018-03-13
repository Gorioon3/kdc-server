package com.gorio.acs.kdcserver.service;

import com.gorio.acs.kdcserver.dao.AppDAO;
import com.gorio.acs.kdcserver.entity.App;
import com.gorio.acs.kdcserver.exception.DataAccessException;
import com.gorio.acs.kdcserver.mapper.AppMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;
import java.util.List;

/**
 * Class Name AppDAOImpl
 * Created by Gorio
 *
 * @author Gorio
 * @date 2018/2/25
 */
@Slf4j
public class AppDAOImpl implements AppDAO{
    private JdbcTemplate jdbcTemplateObject;
    public AppDAOImpl() {
        super();
    }


    /**
     * This is the method to be used to initialize
     * database resources ie.connection;
     *
     * @param ds DataSource
     */
    @Override
    public void setDataSource(DataSource ds) {
        this.jdbcTemplateObject = new JdbcTemplate(ds);
    }

    /**
     * 保存APP信息
     *
     * @param app App对象
     * @return 成功true ，失败 false
     * @throws DataAccessException
     */
    @Override
    public boolean saveApp(App app) throws DataAccessException {
        if(findApp(app.getApplicationUniqueIdentifier()).getApplicationIdentification()!=0){
            log.error("saveapp failed.the app is already in database",new DataAccessException());
            return false;
        }
        String sql = "insert into app (applicationUniqueIdentifier,rsaPublicKey) values (?,?)";
        jdbcTemplateObject.update(sql,app.getApplicationUniqueIdentifier(),app.getRsaPublicKey());
        log.info("Save New App,applicationUniqueIdentifier = {},rsaPublicKey = {}",
                app.getApplicationUniqueIdentifier(),app.getRsaPublicKey());
        return true;
    }

    /**
     * 删除应用信息
     * @param applicationUniqueIdentifier 应用唯一识别符
     * @return 成功true ，失败 false
     * @throws DataAccessException
     */
    @Override
    public boolean deleteApp(String applicationUniqueIdentifier) throws DataAccessException {
        if (findApp(applicationUniqueIdentifier).getApplicationIdentification()==0){
            log.info("Delete App success,because applicationUniqueIdentifier={} is not in the Database",
                    applicationUniqueIdentifier);
            return true;
        }
        String sql = "delete from app where applicationUniqueIdentifier=?";
        jdbcTemplateObject.update(sql,applicationUniqueIdentifier);
        return true;
    }

    /**
     * 更新应用信息
     *
     * @param app App对象
     * @return 成功true ，失败 false
     * @throws DataAccessException
     */
    @Override
    public boolean updateApp(App app) throws DataAccessException {
        if (findApp(app.getApplicationUniqueIdentifier()).getApplicationIdentification()==0){
            log.error("Update App failed",new DataAccessException());
        }
        String sql = "update app set applicationUniqueIdentifier=?,rsaPublicKey=? where applicationIdentification=?";
        jdbcTemplateObject.update(sql,app.getApplicationUniqueIdentifier()
            ,app.getRsaPublicKey(),app.getApplicationIdentification());
        log.info("Update App success,applicationIdentification ={},applicationUniqueIdentifier = {},rsaPublicKey={}",
                app.getApplicationIdentification(),
                app.getApplicationUniqueIdentifier(),
                app.getRsaPublicKey());
        return true;
    }

    /**
     * 查找应用信息
     *
     * @param applicationUniqueIdentifier 应用唯一识别符
     * @return 成功应用对象 ，失败NULL
     * @throws DataAccessException
     */
    @Override
    public App findApp(String applicationUniqueIdentifier) throws DataAccessException {
        int intApplicationUniqueIdentifier = Integer.parseInt(applicationUniqueIdentifier);
        String sql = "select * from app where applicationUniqueIdentifier =?";
        App app = jdbcTemplateObject.queryForObject(sql,new Object[]{intApplicationUniqueIdentifier},new AppMapper());
        return app;
    }

    /**
     * 查找所有应用信息
     *
     * @return 应用列表
     * @throws DataAccessException
     */
    @Override
    public List<App> findAllApp() throws DataAccessException {
        String sql = "select * from app";
        return jdbcTemplateObject.query(sql,new AppMapper());
    }
}
