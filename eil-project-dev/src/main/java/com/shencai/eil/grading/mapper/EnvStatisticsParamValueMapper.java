package com.shencai.eil.grading.mapper;

import com.shencai.eil.grading.entity.EnvStatisticsParamValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shencai.eil.grading.model.EnvStatisticsParamValueQueryParam;
import com.shencai.eil.grading.model.EnvStatisticsParamValueVO;

import java.util.List;

/**
 * @author fujl
 * @since 2018-09-19
 */
public interface EnvStatisticsParamValueMapper extends BaseMapper<EnvStatisticsParamValue> {

    List<EnvStatisticsParamValueVO> listEnvStatisticsParamValue(EnvStatisticsParamValueQueryParam queryParam);
}
