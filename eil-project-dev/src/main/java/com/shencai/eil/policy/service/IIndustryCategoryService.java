package com.shencai.eil.policy.service;

import com.shencai.eil.policy.entity.IndustryCategory;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shencai.eil.policy.model.IndustryCategoryQueryParam;
import com.shencai.eil.policy.model.IndustryCategoryVO;

import java.util.List;

/**
 * @author zhoujx
 * @since 2018-09-13
 */
public interface IIndustryCategoryService extends IService<IndustryCategory> {

    List<IndustryCategoryVO> listIndustryCategory(IndustryCategoryQueryParam queryParam);
}
