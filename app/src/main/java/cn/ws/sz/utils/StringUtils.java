package cn.ws.sz.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by chenjianliang on 2018/1/10.
 */

public class StringUtils {

    public static String getRandomFileName(String key) {
        return new SimpleDateFormat("yyyyMMddHHmmssS", Locale.CHINA).format(new Date()) + key;
    }
}
