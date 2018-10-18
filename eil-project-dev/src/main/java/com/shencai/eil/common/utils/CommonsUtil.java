package com.shencai.eil.common.utils;

import com.shencai.eil.common.constants.TargetEnum;
import com.shencai.eil.gis.model.GisValueVO;
import com.shencai.eil.grading.model.CodeAndValueUseDouble;
import com.shencai.eil.grading.model.TargetMaxValueVO;
import com.shencai.eil.grading.model.TargetResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @program: eil-project
 * @description:
 * @author: fujl
 * @create: 2018-09-28 19:34
 **/
@Slf4j
public class CommonsUtil {

    /**
     * cast list to map
     *
     * @return
     */
    public static Map<String, Double> castListToMap(List<CodeAndValueUseDouble> list) {
        Map<String, Double> map = new HashMap<>(list.size());

        if (CollectionUtils.isEmpty(list)) {
            return map;
        }

        for (CodeAndValueUseDouble codeValue : list) {
            map.put(codeValue.getCodeResult(), codeValue.getValueResult());
        }
        return map;
    }

    /**
     * cast list to map
     *
     * @return
     */
    public static Map<String, Double> castListToMapAppendKey(List<TargetResultVO> list) {
        Map<String, Double> map = new HashMap<>(list.size());

        if (CollectionUtils.isEmpty(list)) {
            return map;
        }

        for (TargetResultVO codeValue : list) {
            map.put(codeValue.getTargetCode() + codeValue.getTargetType(), codeValue.getTargetResult());
        }
        return map;
    }

    /**
     * cast list to map
     *
     * @return
     */
    public static Map<String, Double> castGisValueListToMap(List<GisValueVO> list) {
        Map<String, Double> map = new HashMap<>(list.size());
        if (CollectionUtils.isEmpty(list)) {
            return map;
        }
        for (GisValueVO gisValue : list) {
            map.put(gisValue.getCode(), gisValue.getValue());
        }
        return map;
    }


    public static double calculateStandard(double value, String weightIdAppendCode, Map<String, TargetMaxValueVO> targetMaxMinMap) {
        double result;
        TargetMaxValueVO targetMaxMin = targetMaxMinMap.get(weightIdAppendCode);
        log.info("CommonsUtil->calculateStandard->weightIdAppendCode->" + weightIdAppendCode + ",targetMaxMin" + targetMaxMin);
        if (ObjectUtil.isEmpty(targetMaxMin)) {
            targetMaxMin = new TargetMaxValueVO();
            targetMaxMin.setMaxParamValue(1.0);
        }
        if (weightIdAppendCode.contains(TargetEnum.R_THREE_THREE_ONE.getCode()) ||
                weightIdAppendCode.contains(TargetEnum.R_THREE_THREE_TWO.getCode())) {
            result = (value - targetMaxMin.getMinParamValue()) * 100 / (targetMaxMin.getMaxParamValue() - targetMaxMin.getMinParamValue());
            return result;
        }
        result = value * 100 / targetMaxMin.getMaxParamValue();
        return result;
    }

    /**
     * clone same properties of object
     *
     * @param obj
     * @param toResult
     * @return
     */
    public static Object cloneObj(Object obj, Object toResult) {
        if (obj == null) {
            return toResult;
        }
        try {
            List<Field> sameFields = getInvokeFields(obj, toResult);
            for (Field field : sameFields) {
                field.setAccessible(true);
                if (Modifier.isFinal(field.getModifiers()))
                    continue;
                if (isWrapType(field)) {
                    String firstLetter = field.getName().substring(0, 1).toUpperCase();
                    String getMethodName = "get" + firstLetter + field.getName().substring(1);
                    String setMethodName = "set" + firstLetter + field.getName().substring(1);
                    Method getMethod = obj.getClass().getMethod(getMethodName);
                    Method setMethod = toResult.getClass().getMethod(setMethodName, new Class[]{field.getType()});
                    Object value = getMethod.invoke(obj);
                    if (!ObjectUtil.isEmpty(value)) {
                        setMethod.invoke(toResult, new Object[]{value});
                    }
                }
            }
            return toResult;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return toResult;
    }

    private static List<Field> getInvokeFields(Object obj, Object toResult) {
        Field[] fields = toResult.getClass().getDeclaredFields();
        Field[] objFields = obj.getClass().getDeclaredFields();
        List<Field> sameFields = new ArrayList<>();
        for (Field field : fields) {
            for (Field objField : objFields) {
                if (field.getName().equals(objField.getName())) {
                    sameFields.add(field);
                    break;
                }
            }
        }
        return sameFields;
    }

    private static boolean isWrapType(Field field) {
        String[] types = {"java.lang.Integer", "java.lang.Double", "java.lang.Float", "java.lang.Long",
                "java.lang.Short", "java.lang.Byte", "java.lang.Boolean", "java.lang.Char", "java.lang.String", "int",
                "double", "long", "short", "byte", "boolean", "char", "float", "java.util.Date"};
        List<String> typeList = Arrays.asList(types);
        return typeList.contains(field.getType().getName()) ? true : false;
    }
}
