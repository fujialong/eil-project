package com.shencai.eil.gis.mapper;

import com.shencai.eil.gis.entity.GisValueClass;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author fujl
 * @since 2018-09-27
 */
public interface GisValueClassMapper extends BaseMapper<GisValueClass> {

    /**
     * Query classCode by code
     *
     * @param codes
     * @return
     */
    List<String> getClassCodesByCodes(List<String> codes);
}
