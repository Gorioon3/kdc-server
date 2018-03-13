package com.gorio.acs.kdcserver.tools.compare;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Gorio
 */
@Log4j
public class CompareTime {
    private static final long MAX_MESSAGE_TIME_INTERVAL = 60;
    private static final long MIN_MESSAGE_TIME_INTERVAL = 0;

    /**
     * 判断时间合法性
     * @param instant String 当前时间 不可为空
     * @param sendtime String 发送方发送得时间戳
     * @return boolean 是否合法
     */
    @SneakyThrows
    public static boolean compareTime(@NonNull String instant , @NonNull String sendtime){
        long second = 0;
        try {
            second = getDistanceDays(instant,sendtime);
        }
        catch (Exception e){
            log.error("时间比较出错"+e.getMessage(),e);
        }
        return isLegitimateTime(second);
    }

    /**
     * 计算两个时间戳之间得时间间隔
     * @param instant String 当前时间 不可为空
     * @param sendtime String 发送方发送得时间戳
     * @return long 毫秒数
     */
    @SneakyThrows
    private static long getDistanceDays(@NonNull String instant, @NonNull String sendtime) {
        DateFormat dateFormat = new SimpleDateFormat("yyMMddHHmm");
        Date instantdate;
        Date sendtimedate;
        long days=0;
        try {
            instantdate = dateFormat.parse(instant);
            sendtimedate = dateFormat.parse(sendtime);
            long time1 = instantdate.getTime();
            long time2 = sendtimedate.getTime();
            long diff ;
            if(time1<time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            days = diff / (1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return days;
    }

    /**
     * 判断时间间隔是否在合法间隔之内
     * @param second long 时间间隔秒数 不可为空
     * @return boolean
     */
    private static boolean isLegitimateTime(long second){
        return second <= MAX_MESSAGE_TIME_INTERVAL && second >= MIN_MESSAGE_TIME_INTERVAL;
    }
}
