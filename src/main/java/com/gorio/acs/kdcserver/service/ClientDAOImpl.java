package com.gorio.acs.kdcserver.service;

import com.gorio.acs.kdcserver.dao.ClientDAO;
import com.gorio.acs.kdcserver.entity.Client;
import com.gorio.acs.kdcserver.exception.DataAccessException;
import com.gorio.acs.kdcserver.mapper.ClientMapper;
import com.gorio.acs.kdcserver.tools.KeyUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

/**
 * Class Name ClientDAOImpl
 * Created by Gorio
 *
 * @author Gorio
 * @date 2018/2/26
 */@Slf4j
public class ClientDAOImpl implements ClientDAO{
    private JdbcTemplate jdbcTemplateObject;
    public ClientDAOImpl() {
        super();
    }

    /**
     * 保存用户
     * @return 新的用户的信息
     * @throws DataAccessException
     */
    @Override
    public Client saveNewClient() throws DataAccessException {
        Client client = new Client();
        client.setSessionKey(KeyUpdate.getRandomString(LEGAL_KEY_LENGTH));
        client.setMasterSharedKey(KeyUpdate.getRandomString(LEGAL_KEY_LENGTH));
        String sql ="INSERT INTO client (masterSharedKey, sessionKey) VALUES (?,?)";
        jdbcTemplateObject.update(sql,client.getMasterSharedKey(),client.getSessionKey());
        sql = "SELECT userIdentification FROM client WHERE masterSharedKey=?";
        Integer userIdentification = jdbcTemplateObject.queryForObject(sql,new Object[]{client.getMasterSharedKey()},Integer.TYPE);
        client.setUserIdentification(userIdentification);
        return client;
    }

    /**
     * 保存用户
     *
     * @param client
     * @return 成功true ，失败 false
     * @throws DataAccessException
     */
    @Override
    public boolean saveClient(Client client) throws DataAccessException {

        if (!"".equals(findClient(client.getUserIdentification()).getMasterSharedKey())){
            log.error("save client failed.the client is already in database");
            return false;
        }
        String sql="INSERT INTO client (masterSharedKey, sessionKey) VALUES (?,?)";
        jdbcTemplateObject.update(sql,
                "".equals(client.getMasterSharedKey())?KeyUpdate.getRandomString(LEGAL_KEY_LENGTH)
                        :client.getMasterSharedKey(),
                "".equals(client.getSessionKey())?KeyUpdate.getRandomString(LEGAL_KEY_LENGTH):client.getSessionKey());
        return true;
    }

    /**
     * 删除指定用户
     *
     * @param userIdentification
     * @return 成功true ，失败 false
     * @throws DataAccessException
     */
    @Override
    public boolean deleteClient(int userIdentification) throws DataAccessException {
        if ("".equals(findClient(userIdentification).getMasterSharedKey())){
            log.info("Delete Client success because the client(userIdentification={}) is not in the database",userIdentification);
            return true;
        }
        String sql = "DELETE FROM client WHERE userIdentification=?";
        jdbcTemplateObject.update(sql,userIdentification);
        return true;
    }

    /**
     * 更新指定用户信息
     *
     * @param client
     * @return 成功true ，失败 false
     * @throws DataAccessException
     */
    @Override
    public boolean updateClient(Client client) throws DataAccessException {
        if ("".equals(findClient(client.getUserIdentification()).getMasterSharedKey())){
            log.error("Update Client failed the client is new one" ,new DataAccessException());
            return false;
        }
        String sql ="UPDATE client SET masterSharedKey=?,sessionKey=? WHERE userIdentification=?";
        jdbcTemplateObject.update(sql,KeyUpdate.getRandomString(LEGAL_KEY_LENGTH),KeyUpdate.getRandomString(LEGAL_KEY_LENGTH),client.getUserIdentification());
        return true;
    }

    /**
     * 查找用户
     *
     * @param userIdentification
     * @return 成功返回此用户对象，失败返回NULL
     * @throws DataAccessException
     */
    @Override
    public Client findClient(int userIdentification) throws DataAccessException {
        String sql ="SELECT * FROM client WHERE userIdentification=?";
        return jdbcTemplateObject.queryForObject(sql,new Object[]{userIdentification},new ClientMapper());
    }

    /**
     * 查找所有用户
     *
     * @return 用户链表
     * @throws DataAccessException
     */
    @Override
    public List<Client> findAllClient() throws DataAccessException {
        String sql = "SELECT * FROM client";
        return jdbcTemplateObject.query(sql,new ClientMapper());
    }

    /**
     * 修改所有用户的共享主密钥
     *
     * @return 成功true ，失败 false ，并输出错误信息
     * @throws DataAccessException
     */
    @Override
    public boolean changeAllMasterSharedKey() throws DataAccessException {
        List<Client> list = findAllClient();
        list.forEach(e->e.setMasterSharedKey(KeyUpdate.getRandomString(LEGAL_KEY_LENGTH)));
        return updateAllClient(list);
    }

    /**
     * 更新所提供的链表中的所有用户
     *
     * @param list
     * @return true or false
     * @throws DataAccessException
     */
    @Override
    public boolean updateAllClient(List<Client> list) throws DataAccessException {
        for (Client c :list) {
            if (!updateClient(c)){
                return false;
            }
        }
        return true;
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
}
