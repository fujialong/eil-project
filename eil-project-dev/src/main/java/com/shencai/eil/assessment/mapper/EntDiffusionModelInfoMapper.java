package com.shencai.eil.assessment.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shencai.eil.assessment.entity.EntDiffusionModelInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shencai.eil.assessment.model.EntDiffusionModelInfoQueryParam;
import com.shencai.eil.assessment.model.EntDiffusionModelInfoVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhoujx
 * @since 2018-10-20
 */
public interface EntDiffusionModelInfoMapper extends BaseMapper<EntDiffusionModelInfo> {
    List<String> listMaterialName(String enterpriseId);

    List<EntDiffusionModelInfoVO> pageEntDiffusionModelInfo(Page<EntDiffusionModelInfoVO> page, @Param("queryParam") EntDiffusionModelInfoQueryParam queryParam);

    List<EntDiffusionModelInfoVO> listModelOfInfo(EntDiffusionModelInfoQueryParam queryParam);
}
