package com.gorio.acs.kdcserver.tools.compare;

import com.gorio.acs.kdcserver.exception.MessageProcessingException;
import lombok.NonNull;
import lombok.extern.log4j.Log4j;

/**
 * @author Gorio
 */
@Log4j
public class CompareHmac {
    /**
     * HMAC比较函数
     * @param hmacget String 获得的HMAC 不可为空
     * @param hmacmake String 计算的到的HMAC 不可为空
     * @return boolean 是否相等
     */
    public static boolean compareHMAC(@NonNull String hmacget, @NonNull String hmacmake){
        if("".equals(hmacget)){
            log.error("比较Hmac时出错，获得到的Hmac为空",new MessageProcessingException());
        }
        if("".equals(hmacmake)){
            log.error("比较Hmac时出错，计算得到的Hmac为空",new MessageProcessingException());
        }
        return hmacget.equals(hmacmake);
    }


}
