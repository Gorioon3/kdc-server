package com.gorio.acs.kdcserver.dao;

import com.gorio.acs.kdcserver.entity.Client;
import com.gorio.acs.kdcserver.exception.DataAccessException;

import java.sql.ResultSet;
import java.util.List;

/**
 * Class Name ClientDao
 * Created by Gorio
 *
 * @author Gorio
 * @date 2018/2/25
 */
public interface ClientDAO extends SuperDAO{
    /**
     * 保存用户
     * @return 新的用户的信息
     * @throws DataAccessException
     */
    Client saveNewClient()throws DataAccessException;
    /**
     * 保存用户
     * @param client
     * @return 成功true ，失败 false
     * @throws DataAccessException
     */
    boolean saveClient(Client client)throws DataAccessException;

    /**
     * 删除指定用户
     * @param userIdentification
     * @return 成功true ，失败 false
     * @throws DataAccessException
     */
    boolean deleteClient(int userIdentification)throws DataAccessException;

    /**
     * 更新指定用户信息
     * @param client
     * @return 成功true ，失败 false
     * @throws DataAccessException
     */
    boolean updateClient(Client client)throws DataAccessException;

    /**
     * 查找用户
     * @param userIdentification
     * @return 成功返回此用户对象，失败返回NULL
     * @throws DataAccessException
     */
    Client findClient(int userIdentification)throws  DataAccessException;

    /**
     * 查找所有用户
     * @return 用户链表
     * @throws DataAccessException
     */
    List<Client> findAllClient()throws DataAccessException;

    /**
     * 修改所有用户的共享主密钥
     * @return 成功true ，失败 false ，并输出错误信息
     * @throws DataAccessException
     */
    boolean changeAllMasterSharedKey()throws DataAccessException;


    /**
     * 更新所提供的链表中的所有用户
     * @param list
     * @return true or false
     * @throws DataAccessException
     */
    boolean updateAllClient(List<Client> list)throws DataAccessException;

}
