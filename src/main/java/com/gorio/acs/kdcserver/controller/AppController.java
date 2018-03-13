package com.gorio.acs.kdcserver.controller;
import com.gorio.acs.kdcserver.core.appcore.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

/**
 * Class Name AppController
 * Created by Gorio
 *
 * @author Gorio
 * @date 2018/2/26
 */
@Slf4j
@RestController
@RequestMapping(value = "/apps")
@Scope(value = "session",  proxyMode = ScopedProxyMode.TARGET_CLASS)
public class AppController {


    /**
     * 创建新的APP
     * @param args 命令行
     * @return 处理结果
     */
    @RequestMapping(value = "/300000/{args}",method = RequestMethod.POST)
    public String newApp(@PathVariable String args,@Autowired CreateNewAppRsaMessageDecomposition newAppRsaMessageDecomposition) throws SQLException {
        newAppRsaMessageDecomposition.setIncomingmessage(args);
        newAppRsaMessageDecomposition.initializationdata();
        return newAppRsaMessageDecomposition.getResult();
    }

    /**
     * 通过当前APP 获取当前用户的共享主密钥
     * @param args 命令行
     * @return 处理结果
     */
    @RequestMapping(value = "/300011/{args}",method = RequestMethod.POST)
    public String makeNewClient(@PathVariable String args, @Autowired SessionKeyAesMessageDecomposition sessionKeyAesMessageDecomposition) throws SQLException {
        sessionKeyAesMessageDecomposition.setIncomingmessage(args);
        sessionKeyAesMessageDecomposition.initializationdata();
        return sessionKeyAesMessageDecomposition.getResult();
    }

    /**
     *DH 协议
     * @param args 命令行
     * @return 处理结果
     */
    @RequestMapping(value = "/300010/{args}",method = RequestMethod.GET)
    public String dhMethod(@PathVariable String args,@Autowired DhKeyAesMessageDecomposition dhKeyAesMessageDecomposition) throws SQLException {
        dhKeyAesMessageDecomposition.setIncomingmessage(args);
        dhKeyAesMessageDecomposition.initializationdata();
        return dhKeyAesMessageDecomposition.getResult();
    }

    /**
     * ks 协议
     * @param args 命令行
     * @return 处理结果
     */
    @RequestMapping(value = "/300001/{args}",method = RequestMethod.GET)
    public String ksMethod(@PathVariable String args,@Autowired CreateNewClientRsaMessageDecomposition newClientRsaMessageDecomposition) throws SQLException {
        newClientRsaMessageDecomposition.setIncomingmessage(args);
        newClientRsaMessageDecomposition.initializationdata();
        return newClientRsaMessageDecomposition.getResult();
    }

    /**
     * kc协议
     * @param args 命令行
     * @return 处理结果
     */
    @RequestMapping(value = "/300100/{args}",method = RequestMethod.GET)
    public String kcMethod(@PathVariable String args, @Autowired UpdateUserMasterSharedKeyMessageDecomposition updateUserMasterSharedKeyMessageDecomposition) throws SQLException {
        updateUserMasterSharedKeyMessageDecomposition.setIncomingmessage(args);
        updateUserMasterSharedKeyMessageDecomposition.initializationdata();
        return updateUserMasterSharedKeyMessageDecomposition.getResult();
    }
}
