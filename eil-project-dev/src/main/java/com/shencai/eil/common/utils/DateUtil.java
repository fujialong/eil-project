package com.shencai.eil.common.utils;

import java.sql.Timestamp;

public class DateUtil {

    public static Timestamp getNowTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

}
