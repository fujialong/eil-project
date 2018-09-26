package com.shencai.eil.policy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shencai.eil.common.constants.BaseEnum;
import com.shencai.eil.policy.entity.IndustryCategory;
import com.shencai.eil.policy.mapper.IndustryCategoryMapper;
import com.shencai.eil.policy.model.IndustryCategoryQueryParam;
import com.shencai.eil.policy.model.IndustryCategoryVO;
import com.shencai.eil.policy.service.IIndustryCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhoujx
 * @since 2018-09-13
 */
@Service
public class IndustryCategoryServiceImpl extends ServiceImpl<IndustryCategoryMapper, IndustryCategory> implements IIndustryCategoryService {

    @Autowired
    private IndustryCategoryMapper industryCategoryMapper;

    @Override
    public List<IndustryCategoryVO> listIndustryCategory(IndustryCategoryQueryParam queryParam) {
        List<IndustryCategoryVO> industryCategoryList = listAllIndustryCategory(queryParam);
        List<IndustryCategoryVO> returnList = getReturnData(industryCategoryList);
        return returnList;
    }

    private List<IndustryCategoryVO> listAllIndustryCategory(IndustryCategoryQueryParam queryParam) {
        return industryCategoryMapper.listIndustryCategory(queryParam);
    }

    private List<IndustryCategoryVO> getReturnData(List<IndustryCategoryVO> industryCategoryList) {
        List<IndustryCategoryVO> returnList = getReturnList(industryCategoryList);
        setReturnListChild(industryCategoryList, returnList);
        return returnList;
    }

    private void setReturnListChild(List<IndustryCategoryVO> industryCategoryList, List<IndustryCategoryVO> returnList) {
        for (IndustryCategoryVO industryCategoryVO : returnList) {
            setChildren(industryCategoryList, industryCategoryVO);
        }
    }

    private List<IndustryCategoryVO> getReturnList(List<IndustryCategoryVO> industryCategoryList) {
        List<IndustryCategoryVO> returnList = new ArrayList<>();
        for (IndustryCategoryVO industryCategoryVO : industryCategoryList) {
            if (industryCategoryVO.getLevel() == BaseEnum.LEVEL_ONE.getCode()) {
                returnList.add(industryCategoryVO);
            }
        }
        return returnList;
    }

    private void setChildren(List<IndustryCategoryVO> industryCategoryList, IndustryCategoryVO categoryVO) {
        List<IndustryCategoryVO> child = new ArrayList<>();
        for (IndustryCategoryVO vo : industryCategoryList) {
            if (categoryVO.getId().equals(vo.getParentId())) {
                setChildren(industryCategoryList, vo);
                child.add(vo);
            }
        }
        if (!CollectionUtils.isEmpty(child)) {
            categoryVO.setChildren(child);
        }
    }
}
