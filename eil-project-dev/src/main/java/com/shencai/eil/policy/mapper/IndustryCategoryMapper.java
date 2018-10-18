package com.shencai.eil.policy.mapper;

import com.shencai.eil.policy.entity.IndustryCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shencai.eil.policy.model.IndustryCategoryQueryParam;
import com.shencai.eil.policy.model.IndustryCategoryVO;
import com.shencai.eil.survey.model.IndustryCategoryClassifyVO;

import java.util.List;

/**
 * @author zhoujx
 * @since 2018-09-13
 */
public interface IndustryCategoryMapper extends BaseMapper<IndustryCategory> {

    List<IndustryCategoryVO> listIndustryCategory(IndustryCategoryQueryParam queryParam);

    IndustryCategoryClassifyVO getClassificationById(String industryId);
}
