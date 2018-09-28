package com.shencai.eil.gis.mapper;

import com.shencai.eil.gis.entity.GisValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shencai.eil.grading.model.CodeAndValueUseDouble;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author fujl
 * @since 2018-09-22
 */
public interface GisValueMapper extends BaseMapper<GisValue> {

    /**
     * Find the gis data codes that the enterprise has uploaded
     *
     * @param entId
     * @return
     */
    List<String> getCodesByEntId(String entId);

    /**
     * Get gisValue by enterpriseId and gisValueCode
     *
     * @param entId   enterpriseId
     * @param code gisValueCode
     * @return
     */
    double getValueByEntIdAndCode(@Param("entId") String entId, @Param("code") String code);

    /**
     * Get code value by enterprise id
     *
     * @param entId
     * @return
     */
    List<CodeAndValueUseDouble> getCodeValueByEntId(String entId);
}
