package com.shencai.eil.gis.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shencai.eil.gis.entity.GisValue;
import com.shencai.eil.gis.model.GisValueParam;
import com.shencai.eil.grading.model.CodeAndValueUseDouble;

import java.util.List;

/**
 * <p>
 * Gis service interface
 * </p>
 *
 * @author fujl
 * @since 2018-09-22
 */
public interface IGisValueService extends IService<GisValue> {

    /**
     * Save gis Value to DB
     *
     * @param requestParam request param
     */
    void saveGisValue(GisValueParam requestParam);

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
     * @param code gisValueCode
     * @return
     */
    double getValueByEntIdAndCode(String entId, String code);

    /**
     * Get code value by enterprise id
     *
     * @param entId
     * @return
     */
    List<CodeAndValueUseDouble> getCodeValueByEntId(String entId);
}
