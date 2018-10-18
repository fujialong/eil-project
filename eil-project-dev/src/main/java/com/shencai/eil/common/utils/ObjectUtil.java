package com.shencai.eil.common.utils;

import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * @program: eil-project
 * @description:
 * @author: fujl
 * @create: 2018-10-17 17:21
 **/
public class ObjectUtil extends ObjectUtils {

    public static boolean isEmpty(@Nullable Object obj) {
        if (obj == null) {
            return true;
        }
        if (obj instanceof Optional) {
            return !((Optional) obj).isPresent();
        }
        if (obj instanceof CharSequence) {
            return ((CharSequence) obj).length() == 0;
        }
        if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0;
        }
        if (obj instanceof Collection) {
            return ((Collection) obj).isEmpty();
        }
        if (obj instanceof Map) {
            return ((Map) obj).isEmpty();
        }
        Class objCla = obj.getClass();
        Field[] fields = objCla.getDeclaredFields();
        boolean flag = true;
        for (Field field : fields) {
            field.setAccessible(true);
            Object fieldValue = null;
            try {
                fieldValue = field.get(obj);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (!ObjectUtils.isEmpty(fieldValue)) {
                flag = false;
                break;
            }
        }
        if (flag) {
            return true;
        }
        return false;
    }

}
