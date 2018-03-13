package com.gorio.acs.kdcserver.tools.cipher.aes;



import com.gorio.acs.kdcserver.exception.CipherException;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @author Gorio
 */
@Log4j
public class Aes{
    private static final int LEGITIMATE_KEY_LENGTH = 16;
    /**
     * AES解密
     * @param sSrc String 密文 不可为空
     * @param sKey String 密钥 不可为空，长度必须为16位
     * @throws Exception
     * @return String 明文
     */
    @SneakyThrows
    public static String Decrypt(@NonNull String sSrc, @NonNull String sKey){

        try {
            //判断密文是否为空
            if (sSrc.length() == 0||sSrc.equals("")||sSrc==null){
                log.error("Decrypt1 error the sSrc is null",new CipherException());
                return null;
            }
            //判断key是否为空
            if (sKey == null){
                log.error("AES-KEY为空",new CipherException());
                return null;
            }
            //判断key是否为16位长{
            if (sKey.length()!=LEGITIMATE_KEY_LENGTH){
                log.error("AES-KEY长度错误，当前长度为"+sKey.length(),new CipherException());
                return null;
            }
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateNowStr = sdf.format(d);
            log.info(dateNowStr+"解密开始,密文："+sSrc+"密钥为："+sKey);
            byte[] raw = sKey.getBytes("UTF-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] encrypted1 = hex2byte(sSrc);
            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original);
            return originalString;
        }
        catch (Exception e){
            log.error(e.getMessage());
            return null;
        }
    }

    /**
     * AES加密
     * @param sSrc String 明文
     * @param sKey String 不可为空，长度必须为16位
     * @throws Exception
     * @return String 密文
     */
    @SneakyThrows
    public static String Encrypt(String sSrc,@NonNull String sKey) {
        if (sKey == null || sKey.length()==0){
            log.error("AES-Key为空",new CipherException());
            return null;
        }
        if (sKey.length()!=LEGITIMATE_KEY_LENGTH){
            log.error("AES-Key长度错误，当前长度为："+sKey.length());
            return null;
        }
        byte[] raw = sKey.getBytes("UTF-8");
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes());
        return byte2hex(encrypted).toLowerCase();
    }

    /**
     * 十六进制转二进制
     * @param strhex String 十六进制字符串
     * @throws Exception
     * @return byte[] 二进制比特串
     */
    @SneakyThrows
    private static byte[] hex2byte(String strhex){
        if (strhex == null) {
            return null;
        }
        int l = strhex.length();
        if (l % 2 == 1) {
            log.error("字符串转换时出错，字符串长度为："+l,new CipherException());
            return null;
        }
        byte[] b = new byte[l / 2];
        for (int i = 0; i != l / 2; i++) {
            b[i] = (byte) Integer.parseInt(strhex.substring(i * 2, i * 2 + 2), 16);
        }
        return b;
    }

    /**
     * 二进制字节串转十六进制字符串
     * @param b byte[] 二进制字节串
     * @return String  十六进制字符串
     */
    private static String byte2hex(byte[] b) {

        StringBuffer hs = new StringBuffer();
        String stmp;
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1) {
                hs.append("0");
                hs.append(stmp);
            } else {
                hs.append(stmp);
            }
        }
        return hs.toString().toUpperCase();
    }


}
