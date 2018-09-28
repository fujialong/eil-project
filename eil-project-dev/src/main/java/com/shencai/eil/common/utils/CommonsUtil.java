package com.shencai.eil.common.utils;

import com.shencai.eil.grading.model.CodeAndValueUseDouble;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: eil-project
 * @description:
 * @author: fujl
 * @create: 2018-09-28 19:34
 **/
public class CommonsUtil {


    /**
     * cast list to map
     *
     * @return
     */
    public static Map<String, Double> castListToMap(List<CodeAndValueUseDouble> list) {
        if (CollectionUtils.isEmpty(list)) {
            return new HashMap(1);
        }
        Map<String, Double> map = new HashMap<>(list.size());
        for (CodeAndValueUseDouble codeValue : list) {
            map.put(codeValue.getCodeResult(), codeValue.getValueResult());
        }
        return map;
    }


}
