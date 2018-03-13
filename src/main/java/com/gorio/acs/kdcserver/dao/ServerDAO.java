package com.gorio.acs.kdcserver.dao;

import com.gorio.acs.kdcserver.entity.Server;
import com.gorio.acs.kdcserver.exception.DataAccessException;

import javax.sql.DataSource;
import java.util.List;

/**
 * Class Name ServerDao
 * Created by Gorio
 *
 * @author Gorio
 * @date 2018/2/25
 */
public interface ServerDAO extends SuperDAO{
    /**
     * 保存服务器信息
     * @param server
     * @return 成功true ，失败 false
     * @throws DataAccessException
     */
    boolean saveServer(Server server)throws DataAccessException;

    /**
     * 删除服务器信息
     * @param serverIdentification
     * @return 成功true ，失败 false
     * @throws DataAccessException
     */
    boolean deleteServer(int serverIdentification)throws DataAccessException;

    /**
     * 更新服务器信息
     * @param server
     * @return 成功true ，失败 false
     * @throws DataAccessException
     */
    boolean updateServer(Server server)throws DataAccessException;
    /**
     * 查找服务器信息
     * @param serverIP
     * @return 成功用户服务器对象 ，失败NULL
     * @throws DataAccessException
     */
    Server findServer(String serverIP)throws DataAccessException;

    /**
     * 查找服务器信息
     * @return 成功用户服务器对象 ，失败NULL
     * @throws DataAccessException
     */
    Server findServer(int serverIdentification )throws DataAccessException;

    /**
     * 查找所有用户服务器
     * @return 返回用户服务器链表
     * @throws DataAccessException
     */
    List<Server> findAllServer ()throws DataAccessException;
}
