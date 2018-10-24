package com.shencai.eil.assessment.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shencai.eil.assessment.entity.EntDiffusionModelInfo;
import com.shencai.eil.assessment.model.EntDiffusionModelInfoQueryParam;
import com.shencai.eil.assessment.model.EntDiffusionModelInfoVO;

/**
 * @author zhoujx
 * @since 2018-10-20
 */
public interface IEntDiffusionModelInfoService extends IService<EntDiffusionModelInfo> {

    void matchingWaterQualityModel(String enterpriseId);

    Page<EntDiffusionModelInfoVO> pageDiffusionModelInfo(EntDiffusionModelInfoQueryParam queryParam);
}
