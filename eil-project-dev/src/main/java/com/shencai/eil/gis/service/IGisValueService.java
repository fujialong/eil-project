package com.shencai.eil.gis.service;

import com.shencai.eil.gis.entity.GisValue;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shencai.eil.gis.model.GisValueParam;

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
}
