package com.takin.mvc.util;

import java.text.SimpleDateFormat;

/**
 * 时间工具类
 *
 * @author howsun(zjh@58.com)
 * @Date 2010-10-26
 * @version v0.1
 */
public abstract class DateUtil {

    public static String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * Get the previous time, from how many days to now.
     * 
     * @param days How many days.
     * @return The new previous time.
     */
    public static long previous(int days) {
        return System.currentTimeMillis() - days * 3600000L * 24L;
    }

    /**
     * Convert date and time to string like "yyyy-MM-dd HH:mm:ss".
     */
    public static String formatDateTime(long d) {
        return new SimpleDateFormat(DATETIME_FORMAT).format(d);
    }

    /**
     * Convert date to String like "yyyy-MM-dd".
     */
    public static String formatDate(long d) {
        return new SimpleDateFormat(DATE_FORMAT).format(d);
    }

    /**
     * Parse date like "yyyy-MM-dd".
     */
    public static long parseDate(String d) {
        try {
            return new SimpleDateFormat(DATE_FORMAT).parse(d).getTime();
        } catch (Exception e) {
        }
        return 0;
    }

    /**
     * Parse date and time like "yyyy-MM-dd hh:mm:ss".
     */
    public static long parseDateTime(String dt) {
        try {
            return new SimpleDateFormat(DATETIME_FORMAT).parse(dt).getTime();
        } catch (Exception e) {
        }
        return 0;
    }
}
