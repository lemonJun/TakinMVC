package com.takin.mvc.util;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ParamUtil {

    private static String getString(HttpServletRequest request, String s) {
        String temp = null;
        try {
            temp = request.getParameter(s).trim();
            temp = DropHtmlTags(temp);
        } catch (Exception e) {
        }
        return temp;
    }

    public static String DropHtmlTags(String HtmlText) {
        if (StringUtil.isNullOrEmpty(HtmlText)) {
            return "";
        } else {
            String regxpForHtml = "<([^>]*)>";//"<.*?>"
            Pattern pattern = Pattern.compile(regxpForHtml);
            Matcher matcher = pattern.matcher(HtmlText);
            StringBuffer sb = new StringBuffer();
            boolean result1 = matcher.find();
            while (result1) {
                matcher.appendReplacement(sb, "");
                result1 = matcher.find();
            }
            matcher.appendTail(sb);
            return sb.toString();
        }
    }

    public static String getString(HttpServletRequest request, String s, String defaultString) {
        String s1 = getString(request, s);
        if (StringUtil.isNullOrEmpty(s1))
            return defaultString;
        return s1;
    }

    public static int getInt(HttpServletRequest request, String s, int defaultInt) {
        try {
            String temp = getString(request, s);
            if (StringUtil.isNullOrEmpty(temp))
                return defaultInt;
            else
                return Integer.parseInt(temp);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static long getLong(HttpServletRequest request, String s, long defaultLong) {
        try {
            String temp = getString(request, s);
            if (StringUtil.isNullOrEmpty(temp))
                return defaultLong;
            else
                return Long.parseLong(temp);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static short getShort(HttpServletRequest request, String s, short defaultShort) {
        try {
            String temp = getString(request, s);
            if (StringUtil.isNullOrEmpty(temp))
                return defaultShort;
            else
                return Short.parseShort(temp);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    //    public static Date getDate(HttpServletRequest request, String s) {
    //        String value = getString(request, s, "");
    //        if (StringUtil.isNullOrEmpty(value)) {
    //            Long m = DateUtil.parseDate(value);
    //            return new Date(m);
    //        }
    //        return null;
    //    }

    public static BigDecimal getBigDecimal(HttpServletRequest request, String s) {
        String value = getString(request, s, "");
        if (StringUtil.isNullOrEmpty(value)) {
            return new BigDecimal(value);
        }
        return null;
    }

    public static String getHeader(HttpServletRequest request, String s, String defaultString) {
        String s1 = request.getHeader(s);
        if (s1 == null)
            return defaultString;
        return s1;
    }

    public static boolean IsMobileUser(HttpServletRequest request, HttpServletResponse response) {
        boolean flag = false;
        String via = getHeader(request, "Via", "");// 取VIA
        String accept = getHeader(request, "accept", "");// 取accept
        if (via.length() != 0 && via.trim().toLowerCase().indexOf("wap") > -1 && accept.length() != 0 && accept.trim().toLowerCase().indexOf("vnd.wap.wml") > -1) {
            flag = true;
        }
        flag = true;
        return flag;
    }

}
