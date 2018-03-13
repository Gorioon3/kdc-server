package com.gorio.acs.kdcserver.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class Name MessageDecomposition
 * Created by Gorio
 *
 * @author Gorio
 * @date 2018/1/25
 */
public class MessageDecomposition {
    public static String getTime(){
        return new SimpleDateFormat("yyMMddHHmm").format(new Date());
    }
}
