package com.shencai.eil.gis.mapper;

import com.shencai.eil.gis.entity.GisValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shencai.eil.gis.model.GisValueQueryParam;
import com.shencai.eil.gis.model.GisValueVO;
import com.shencai.eil.grading.model.CodeAndValueUseDouble;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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
     * @param entId enterpriseId
     * @param code  gisValueCode
     * @return
     */
    Double getValueByEntIdAndCode(@Param("entId") String entId, @Param("code") String code);

    /**
     * Get code value by enterprise id
     *
     * @param entId
     * @return
     */
    List<CodeAndValueUseDouble> getCodeValueByEntId(String entId);

    /**
     * get gisValue code and value
     *
     * @param entId
     * @param selfGisCodes
     * @return
     */
    List<CodeAndValueUseDouble> getGisValueCodeAndValue(@Param("entId") String entId, @Param("list") List<String> selfGisCodes);

    List<GisValueVO> getGisValueByEntIdAndClassCode(GisValueQueryParam param);
}
