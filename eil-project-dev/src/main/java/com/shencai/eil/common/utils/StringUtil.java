package com.shencai.eil.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

public class StringUtil {

    public static String getUUID() {
        String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");
        return uuid;
    }

    public static boolean isBlank(String param) {
        return StringUtils.isBlank(param);
    }

    public static boolean isNotBlank(String param) {
        return StringUtils.isNotBlank(param);
    }
}
