package com.nekonade.common.utils;

import org.apache.commons.lang3.time.DateFormatUtils;

public class GameTimeUtils {
    /**
     * 日期格式：yyyy-MM-dd HH:mm:ss
     */
    public final static String NORMAL_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static String getStringDate(long millTime) {
        String value = DateFormatUtils.format(millTime, NORMAL_DATE_FORMAT);
        return value;
    }
}
