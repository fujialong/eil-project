package com.shencai.eil.common.utils;

import com.shencai.eil.grading.model.CodeAndValueUseDouble;
import com.shencai.eil.grading.model.TargetResultVO;
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


}
