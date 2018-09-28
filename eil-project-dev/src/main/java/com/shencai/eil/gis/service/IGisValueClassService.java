package com.shencai.eil.gis.service;

import com.shencai.eil.gis.entity.GisValueClass;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author fujl
 * @since 2018-09-27
 */
public interface IGisValueClassService extends IService<GisValueClass> {

    /**
     * Query classCode by code
     *
     * @param codes
     * @return
     */
    List<String> getClassCodesByCodes(List<String> codes);
}
