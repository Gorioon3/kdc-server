package com.gorio.acs.kdcserver.controller;

import com.gorio.acs.kdcserver.core.servercore.NewMasterKeyToServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

/**
 * Class Name ServerController
 * Created by Gorio
 *
 * @author Gorio
 * @date 2018/2/26
 */
@RestController
@RequestMapping(value = "/servers")
@Scope(value = "session",  proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ServerController {
    //310011 新服务器
    //310001 更新主密钥

    /**
     * 更新主密钥
     * @param args
     * @return
     */
    @RequestMapping(value = "/310001/{args}",method = RequestMethod.POST)
    public String updateMasterKey(@PathVariable String args, @Autowired NewMasterKeyToServer newMasterKeyToServer) throws SQLException {
        newMasterKeyToServer.setIncomingmessage(args);
        newMasterKeyToServer.initializationdata();
        return newMasterKeyToServer.getResult();
    }
}
