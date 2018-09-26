package com.shencai.eil.resolver;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @program: eil-project
 * @description:
 * @author: fujialong
 * @create: 2018-09-13 00:17
 **/

public class DateConverter implements Converter<String,Date> {
    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @SneakyThrows({ParseException.class})
    @Override
    public Date convert(String source) {
        if (StringUtils.isNotEmpty(source)) {
            dateFormat.setLenient(false);
            dateFormat.parse(source);
        }

        return null;
    }
}
