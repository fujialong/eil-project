package com.shencai.eil.policy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shencai.eil.common.constants.BaseEnum;
import com.shencai.eil.common.constants.StatusEnum;
import com.shencai.eil.common.utils.DateUtil;
import com.shencai.eil.common.utils.StringUtil;
import com.shencai.eil.exception.BusinessException;
import com.shencai.eil.policy.entity.EnterpriseAccident;
import com.shencai.eil.policy.entity.EnterpriseInfo;
import com.shencai.eil.policy.entity.EnterpriseMappingProduct;
import com.shencai.eil.policy.entity.EnterpriseMappingTechnique;
import com.shencai.eil.policy.mapper.EnterpriseInfoMapper;
import com.shencai.eil.policy.mapper.EnterpriseMappingTechniqueMapper;
import com.shencai.eil.policy.model.*;
import com.shencai.eil.policy.service.IEnterpriseAccidentService;
import com.shencai.eil.policy.service.IEnterpriseMappingProductService;
import com.shencai.eil.policy.service.IPolicyAdministrateService;
import com.shencai.eil.system.entity.BaseFileupload;
import com.shencai.eil.system.entity.EnterpriseScaleDivision;
import com.shencai.eil.system.mapper.EnterpriseScaleDivisionMapper;
import com.shencai.eil.system.service.IBaseFileuploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhoujx on 2018/9/13.
 */
@Service
public class PolicyAdministrateServiceImpl implements IPolicyAdministrateService {

    @Autowired
    private EnterpriseInfoMapper enterpriseInfoMapper;
    @Autowired
    private IEnterpriseMappingProductService enterpriseMappingProductService;
    @Autowired
    private EnterpriseMappingTechniqueMapper enterpriseMappingTechniqueMapper;
    @Autowired
    private EnterpriseScaleDivisionMapper enterpriseScaleDivisionMapper;
    @Autowired
    private IEnterpriseAccidentService enterpriseAccidentService;
    @Autowired
    private IBaseFileuploadService fileuploadService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void savePolicy(PolicyParam param) {
        checkData(param);
        computeEnterpriseScale(param);
        EnterpriseInfo enterpriseInfo = insertEnterpriseInfo(param);
        insertFileupload(param, enterpriseInfo);
        insertEnterpriseMappingTechnique(param, enterpriseInfo);
        insertEnterpriseMappingProducts(param, enterpriseInfo);
        insertEnterpriseAccidents(param, enterpriseInfo);
    }

    private void insertFileupload(PolicyParam param, EnterpriseInfo enterpriseInfo) {
        List<BaseFileupload> attachmentList = param.getAttachmentList();
        for (BaseFileupload attachment : attachmentList) {
            attachment.setId(StringUtil.getUUID());
            attachment.setSourceId(enterpriseInfo.getId());
            attachment.setCreateTime(DateUtil.getNowTimestamp());
            attachment.setUpdateTime(DateUtil.getNowTimestamp());
            attachment.setValid((Integer) BaseEnum.VALID_YES.getCode());
        }
        if (!CollectionUtils.isEmpty(attachmentList)) {
            fileuploadService.saveBatch(attachmentList);
        }
    }

    private void insertEnterpriseAccidents(PolicyParam param, EnterpriseInfo enterpriseInfo) {
        List<EnterpriseAccident> enterpriseAccidentList = param.getEnterpriseAccidentList();
        for (EnterpriseAccident enterpriseAccident : enterpriseAccidentList) {
            enterpriseAccident.setId(StringUtil.getUUID());
            enterpriseAccident.setEntId(enterpriseInfo.getId());
            enterpriseAccident.setCreateTime(DateUtil.getNowTimestamp());
            enterpriseAccident.setUpdateTime(DateUtil.getNowTimestamp());
            enterpriseAccident.setValid((Integer) BaseEnum.VALID_YES.getCode());
        }
        if (!CollectionUtils.isEmpty(enterpriseAccidentList)) {
            enterpriseAccidentService.saveBatch(enterpriseAccidentList);
        }
    }

    private void computeEnterpriseScale(PolicyParam param) {
        List<EnterpriseScaleDivision> enterpriseScaleDivisionList = enterpriseScaleDivisionMapper.selectList(
                new QueryWrapper<EnterpriseScaleDivision>()
                        .eq("industry_code", param.getIndustryLargeCategoryCode())
                        .eq("valid", BaseEnum.VALID_YES.getCode()));
        for (EnterpriseScaleDivision enterpriseScaleDivision : enterpriseScaleDivisionList) {
            if (param.getEmployeesNumber() >= enterpriseScaleDivision.getEmployeesNumberLowerLimit()
                    && param.getIncome() >= enterpriseScaleDivision.getIncomeLowerLimit()) {
                param.setScale(enterpriseScaleDivision.getScaleCode());
                break;
            }
        }
    }

    private void checkData(PolicyParam param) {
        Integer count = enterpriseInfoMapper.selectCount(new QueryWrapper<EnterpriseInfo>()
                .eq("social_credit_code", param.getSocialCreditCode())
                .eq("valid", BaseEnum.VALID_YES.getCode()));
        if (count > 0) {
            throw new BusinessException("The enterprise organization code already exists!");
        }
    }

    private void insertEnterpriseMappingTechnique(PolicyParam param, EnterpriseInfo enterpriseInfo) {
        EnterpriseMappingTechnique enterpriseMappingTechnique = new EnterpriseMappingTechnique();
        enterpriseMappingTechnique.setId(StringUtil.getUUID());
        enterpriseMappingTechnique.setEntId(enterpriseInfo.getId());
        enterpriseMappingTechnique.setTechId(param.getTechniqueId());
        enterpriseMappingTechnique.setCreateTime(DateUtil.getNowTimestamp());
        enterpriseMappingTechnique.setUpdateTime(DateUtil.getNowTimestamp());
        enterpriseMappingTechnique.setValid((Integer) BaseEnum.VALID_YES.getCode());
        enterpriseMappingTechniqueMapper.insert(enterpriseMappingTechnique);
    }

    private void insertEnterpriseMappingProducts(PolicyParam param, EnterpriseInfo enterpriseInfo) {
        List<String> productionIdList = param.getProductionIdList();
        List<EnterpriseMappingProduct> enterpriseMappingProductList = new ArrayList<>();
        for (String productionId : productionIdList) {
            EnterpriseMappingProduct enterpriseMappingProduct = new EnterpriseMappingProduct();
            enterpriseMappingProduct.setId(StringUtil.getUUID());
            enterpriseMappingProduct.setEntId(enterpriseInfo.getId());
            enterpriseMappingProduct.setProdId(productionId);
            if (ObjectUtils.isEmpty(enterpriseMappingProductList)) {
                enterpriseMappingProduct.setIsMainProduct((Integer) BaseEnum.IS_MAIN_PRODUCT_YES.getCode());
            } else {
                enterpriseMappingProduct.setIsMainProduct((Integer) BaseEnum.IS_MAIN_PRODUCT_NO.getCode());
            }
            enterpriseMappingProduct.setCreateTime(DateUtil.getNowTimestamp());
            enterpriseMappingProduct.setUpdateTime(DateUtil.getNowTimestamp());
            enterpriseMappingProduct.setValid((Integer) BaseEnum.VALID_YES.getCode());
            enterpriseMappingProductList.add(enterpriseMappingProduct);
        }
        if (!CollectionUtils.isEmpty(enterpriseMappingProductList)) {
            enterpriseMappingProductService.saveBatch(enterpriseMappingProductList);
        }
    }

    private EnterpriseInfo insertEnterpriseInfo(PolicyParam param) {
        EnterpriseInfo enterpriseInfo = new EnterpriseInfo();
        enterpriseInfo.setId(StringUtil.getUUID());
        enterpriseInfo.setName(param.getEnterpriseName());
        enterpriseInfo.setIndustryId(param.getIndustryId());
        enterpriseInfo.setSocialCreditCode(param.getSocialCreditCode());
        enterpriseInfo.setPostcode(param.getPostcode());
        enterpriseInfo.setCantonCode(param.getCantonCode());
        enterpriseInfo.setAddress(param.getAddress());
        enterpriseInfo.setLongitude(param.getLongitude());
        enterpriseInfo.setLatitude(param.getLatitude());
        enterpriseInfo.setHasRisksReport(param.getHasRisksReport());
        enterpriseInfo.setLinkPhone(param.getLinkPhone());
        enterpriseInfo.setYield(param.getYield());
        enterpriseInfo.setIncome(param.getIncome());
        enterpriseInfo.setEmployeesNumber(param.getEmployeesNumber());
        enterpriseInfo.setCreateTime(DateUtil.getNowTimestamp());
        enterpriseInfo.setUpdateTime(DateUtil.getNowTimestamp());
        enterpriseInfo.setValid((Integer) BaseEnum.VALID_YES.getCode());
        enterpriseInfo.setScale(param.getScale());
        enterpriseInfo.setEmissionModeId(param.getEmissionModeId());
        enterpriseInfo.setStartTime(param.getStartTime());
        enterpriseInfo.setStatus(StatusEnum.PROVIDED.getCode());
        enterpriseInfoMapper.insert(enterpriseInfo);
        return enterpriseInfo;
    }

    @Override
    public Page<PolicyVO> pagePolicy(PolicyQueryParam queryParam) {
        Page<PolicyVO> page = getPageParam(queryParam);
        List<PolicyVO> policyVOList = pageEnterprise(queryParam, page);
        setReturnData(page, policyVOList);
        return page;
    }

    @Override
    public PolicyVO getPolicy(PolicyQueryParam queryParam) {
        PolicyVO policyVO = getPolicyVO(queryParam);
        setIndustryName(policyVO);
        setProductNameList(queryParam, policyVO);
        setCantonName(policyVO);
        setEnterpriseAccidentList(queryParam, policyVO);
        setAttachmentList(queryParam, policyVO);
        return policyVO;
    }

    private void setAttachmentList(PolicyQueryParam queryParam, PolicyVO policyVO) {
        List<BaseFileupload> attachmentList = fileuploadService.list(new QueryWrapper<BaseFileupload>()
                .eq("source_id", queryParam.getEnterpriseId())
                .eq("valid", BaseEnum.VALID_YES.getCode()));
        policyVO.setAttachmentList(attachmentList);
    }

    private void setEnterpriseAccidentList(PolicyQueryParam queryParam, PolicyVO policyVO) {
        EnterpriseAccidentQueryParam enterpriseAccidentQueryParam = new EnterpriseAccidentQueryParam();
        enterpriseAccidentQueryParam.setEnterpriseId(queryParam.getEnterpriseId());
        List<EnterpriseAccidentVO> enterpriseAccidentList =
                enterpriseAccidentService.listEnterpriseAccident(enterpriseAccidentQueryParam);
        policyVO.setEnterpriseAccidentList(enterpriseAccidentList);
    }

    private void setCantonName(PolicyVO policyVO) {
        policyVO.setCantonName(policyVO.getProvinceName() + "/" + policyVO.getCityName());
    }

    private void setIndustryName(PolicyVO policyVO) {
        policyVO.setIndustryName(policyVO.getIndustryCategoryName() + "/"
                + policyVO.getIndustryLargeCategoryName() + "/"
                + policyVO.getIndustrySmallCategoryName() + "/"
                + policyVO.getIndustryName());
    }

    private void setProductNameList(PolicyQueryParam queryParam, PolicyVO policyVO) {
        EnterpriseMappingProductQueryParam qParam = new EnterpriseMappingProductQueryParam();
        qParam.setEnterpriseId(queryParam.getEnterpriseId());
        List<ProductVO> productList = enterpriseMappingProductService.listProduct(qParam);
        List<String> productNameList = new ArrayList<>();
        for (ProductVO productVO : productList) {
            productNameList.add(productVO.getLargeClassName() + "/"
                    + productVO.getSmallClassName() + "/"
                    + productVO.getName());
        }
        policyVO.setProductNameList(productNameList);
    }

    private PolicyVO getPolicyVO(PolicyQueryParam queryParam) {
        return enterpriseInfoMapper.getPolicy(queryParam);
    }

    private void setReturnData(Page<PolicyVO> page, List<PolicyVO> policyVOList) {
        List<String> enterpriseIdList = new ArrayList<>();
        for (PolicyVO vo : policyVOList) {
            enterpriseIdList.add(vo.getId());
        }
        EnterpriseMappingProductQueryParam queryParam = new EnterpriseMappingProductQueryParam();
        queryParam.setEnterpriseIdList(enterpriseIdList);
        List<ProductVO> productVOList = enterpriseMappingProductService.listProduct(queryParam);
        for (PolicyVO policyVO : policyVOList) {
            List<ProductVO> productList = new ArrayList<>();
            for (ProductVO productVO : productVOList) {
                if (policyVO.getId().equals(productVO.getEnterpriseId())) {
                    productList.add(productVO);
                }
            }
            policyVO.setProductList(productList);
        }
        page.setRecords(policyVOList);
    }

    private List<PolicyVO> pageEnterprise(PolicyQueryParam queryParam, Page<PolicyVO> page) {
        List<PolicyVO> policyVOList = enterpriseInfoMapper.pageEnterpriseInfo(page, queryParam);
        return policyVOList;
    }

    private Page<PolicyVO> getPageParam(PolicyQueryParam queryParam) {
        Page<PolicyVO> page = new Page<>();
        page.setCurrent(queryParam.getCurrent());
        page.setSize(queryParam.getSize());
        return page;
    }
}
