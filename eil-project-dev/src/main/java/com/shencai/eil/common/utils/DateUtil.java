package com.shencai.eil.common.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    public static Timestamp getNowTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    public static String formatDateToStr(Date date, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }
}
