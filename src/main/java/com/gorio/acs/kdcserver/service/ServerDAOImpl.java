package com.gorio.acs.kdcserver.service;

import com.gorio.acs.kdcserver.dao.ServerDAO;
import com.gorio.acs.kdcserver.entity.Server;
import com.gorio.acs.kdcserver.exception.DataAccessException;
import com.gorio.acs.kdcserver.mapper.ServerMapper;
import com.gorio.acs.kdcserver.tools.KeyUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class Name ServerDAOImpl
 * Created by Gorio
 *
 * @author Gorio
 * @date 2018/2/26
 */
@Slf4j
public class ServerDAOImpl implements ServerDAO{
    private JdbcTemplate jdbcTemplateObject;
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

    public ServerDAOImpl() {
        super();
    }

    /**
     * 保存服务器信息
     *
     * @param server
     * @return 成功true ，失败 false
     * @throws DataAccessException
     */
    @Override
    public boolean saveServer(Server server) throws DataAccessException {

        if (!"".equals(findServer(server.getServerIdentification()).getMasterSharedKey())){
            log.error("save client failed.the client is already in database");
            return false;
        }
        String sql="INSERT INTO server (masterSharedKey, serverIP) VALUES (?,?)";
        jdbcTemplateObject.update(sql,
                "".equals(server.getMasterSharedKey())?KeyUpdate.getRandomString(LEGAL_KEY_LENGTH)
                        :server.getMasterSharedKey(),
                server.getServerIP());
        return true;
    }

    /**
     * 删除服务器信息
     *
     * @param serverIdentification
     * @return 成功true ，失败 false
     * @throws DataAccessException
     */
    @Override
    public boolean deleteServer(int serverIdentification) throws DataAccessException {
        if ("".equals(findServer(serverIdentification).getMasterSharedKey())){
            log.info("Delete Server success because the client(serverIdentification={}) is not in the database",serverIdentification);
            return true;
        }
        String sql = "DELETE FROM server WHERE serverIdentification=?";
        jdbcTemplateObject.update(sql,serverIdentification);
        return true;
    }

    /**
     * 更新服务器信息
     *
     * @param server
     * @return 成功true ，失败 false
     * @throws DataAccessException
     */
    @Override
    public boolean updateServer(Server server) throws DataAccessException {
        if (findServer(server.getServerIdentification()).getMasterSharedKey().equals("")){
            log.error("update server failed . the server is new one",new DataAccessException());
            return false;
        }
        String  sql = "UPDATE server SET masterSharedKey=? WHERE serverIdentification=?";
        jdbcTemplateObject.update(sql, KeyUpdate.getRandomString(LEGAL_KEY_LENGTH),server.getServerIdentification());
        return true;
    }

    /**
     * 查找服务器信息
     *
     * @param serverIP
     * @return 成功用户服务器对象 ，失败NULL
     * @throws DataAccessException
     */
    @Override
    public Server findServer(String serverIP) throws DataAccessException {
        String ip = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        Pattern pattern = Pattern.compile(ip);
        Matcher matcher = pattern.matcher(serverIP);
        String sql ="";
        if (matcher.matches()){
            sql = "SELECT * FROM server WHERE serverIP=?";
            return jdbcTemplateObject.queryForObject(sql,new Object[]{serverIP},new ServerMapper());
        }
        log.error("Find Server failed because sql = {},caused by serverIP is {}",sql,serverIP,new DataAccessException());
        return null;
    }

    /**
     * 查找服务器信息
     *
     * @param serverIdentification
     * @return 成功用户服务器对象 ，失败NULL
     * @throws DataAccessException
     */
    @Override
    public Server findServer(int serverIdentification) throws DataAccessException {
        String sql = "SELECT * FROM server WHERE serverIdentification=?";
        jdbcTemplateObject.queryForObject(sql,new Object[]{serverIdentification},new ServerMapper());
        return null;
    }

    /**
     * 查找所有用户服务器
     *
     * @return 返回用户服务器链表
     * @throws DataAccessException
     */
    @Override
    public List<Server> findAllServer() throws DataAccessException {
        String sql ="SELECT * FROM server";
        return  jdbcTemplateObject.query(sql,new  ServerMapper());
    }
}
