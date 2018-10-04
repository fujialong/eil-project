package com.shencai.eil.survey.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shencai.eil.common.constants.*;
import com.shencai.eil.common.utils.DateUtil;
import com.shencai.eil.common.utils.StringUtil;
import com.shencai.eil.exception.BusinessException;
import com.shencai.eil.grading.mapper.EntRiskAssessResultMapper;
import com.shencai.eil.grading.mapper.ParamMapper;
import com.shencai.eil.grading.model.EntRiskAssessResultQueryParam;
import com.shencai.eil.grading.model.EntRiskAssessResultVO;
import com.shencai.eil.grading.model.ParamQueryParam;
import com.shencai.eil.grading.model.ParamVO;
import com.shencai.eil.policy.entity.EnterpriseInfo;
import com.shencai.eil.policy.mapper.EnterpriseInfoMapper;
import com.shencai.eil.policy.model.EnterpriseParam;
import com.shencai.eil.policy.model.EnterpriseQueryParam;
import com.shencai.eil.policy.model.EnterpriseVO;
import com.shencai.eil.survey.entity.EntSurveyPlan;
import com.shencai.eil.survey.mapper.EntSurveyPlanMapper;
import com.shencai.eil.survey.mapper.SurveyItemMapper;
import com.shencai.eil.survey.model.EntSurveyPlanQueryParam;
import com.shencai.eil.survey.model.EntSurveyPlanVO;
import com.shencai.eil.survey.model.SurveyItemQueryParam;
import com.shencai.eil.survey.model.SurveyItemVO;
import com.shencai.eil.survey.service.IEntSurveyPlanService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shencai.eil.system.entity.BaseFileupload;
import com.shencai.eil.system.mapper.BaseFileuploadMapper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author fujl
 * @since 2018-09-27
 */
@Service
public class EntSurveyPlanServiceImpl extends ServiceImpl<EntSurveyPlanMapper, EntSurveyPlan> implements IEntSurveyPlanService {

    @Autowired
    private EntSurveyPlanMapper entSurveyPlanMapper;
    @Autowired
    private SurveyItemMapper surveyItemMapper;
    @Autowired
    private ParamMapper paramMapper;
    @Autowired
    private EnterpriseInfoMapper enterpriseInfoMapper;
    @Autowired
    private EntRiskAssessResultMapper entRiskAssessResultMapper;
    @Autowired
    private BaseFileuploadMapper baseFileuploadMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EnterpriseVO getEntSurveyInfo(String enterpriseId) {
        EnterpriseVO enterprise = getEnterpriseOfSurvey(enterpriseId);
        if (ObjectUtils.isNull(enterprise)) {
            throw new BusinessException("The enterprise had been removed!");
        }
        if (StringUtil.isBlank(enterprise.getRiskLevel())) {
            judgeRiskLevel(enterpriseId);
        }
        return getEnterpriseOfSurvey(enterpriseId);
    }

    private void judgeRiskLevel(String enterpriseId) {
        List<EntRiskAssessResultVO> riskResultList = listEnterpriseGeneralCommentLevel(enterpriseId);
        if (CollectionUtils.isEmpty(riskResultList)) {
            throw new BusinessException("general comment did not been calculate!");
        }
        generateBasicSurveyPlan(enterpriseId);
        EnterpriseInfo enterpriseInfo = new EnterpriseInfo();
        for (EntRiskAssessResultVO entRiskAssessResultVO: riskResultList) {
            if (!GradeLineResultCode.LOW.getCode().equals(entRiskAssessResultVO.getGradeLineCode())) {
                enterpriseInfo.setRiskLevel(RiskLevel.HIGH.getCode());
                generateIntensiveSurveyPlan(enterpriseId);
                break;
            }
        }
        enterpriseInfo.setStatus(StatusEnum.I_SURVEY.getCode());
        enterpriseInfo.setRiskLevel(RiskLevel.LOW.getCode());
        enterpriseInfo.setId(enterpriseId);
        enterpriseInfo.setUpdateTime(DateUtil.getNowTimestamp());
        enterpriseInfoMapper.updateById(enterpriseInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void surveyUpgrade(EnterpriseParam param) {
        EnterpriseInfo enterpriseInfo = new EnterpriseInfo();
        enterpriseInfo.setId(param.getId());
        enterpriseInfo.setNeeSurveyUpgrade(param.getNeeSurveyUpgrade());
        if (BaseEnum.VALID_YES.getCode() == param.getNeeSurveyUpgrade()) {
            enterpriseInfo.setRiskLevel(RiskLevel.HIGH.getCode());
            generateIntensiveSurveyPlan(param.getId());
        }
        enterpriseInfo.setUpdateTime(DateUtil.getNowTimestamp());
        enterpriseInfoMapper.updateById(enterpriseInfo);
    }

    private EnterpriseVO getEnterpriseOfSurvey(String enterpriseId) {
        EnterpriseQueryParam queryParam = new EnterpriseQueryParam();
        queryParam.setEnterpriseId(enterpriseId);
        return enterpriseInfoMapper.getEnterpriseInfoOfSurvey(queryParam);
    }

    private List<EntRiskAssessResultVO> listEnterpriseGeneralCommentLevel(String enterpriseId) {
        EntRiskAssessResultQueryParam riskQueryParam = new EntRiskAssessResultQueryParam();
        riskQueryParam.setTargetCode(TargetEnum.GENERAL_COMMENT.getCode());
        riskQueryParam.setEnterpriseId(enterpriseId);
        return entRiskAssessResultMapper.listEntRiskAssessResultLevel(riskQueryParam);
    }

    @Override
    public Page<EntSurveyPlanVO> pageBasicSurveyPlan(EntSurveyPlanQueryParam queryParam) {
        Page<EntSurveyPlanVO> page = new Page<>();
        page.setCurrent(queryParam.getCurrent());
        page.setSize(queryParam.getSize());
        page.setRecords(entSurveyPlanMapper.pageBasicSurveyPlan(page, queryParam));
        return page;
    }

    @Override
    public Page<EntSurveyPlanVO> pageIntensiveSurveyPlan(EntSurveyPlanQueryParam queryParam) {
        Page<EntSurveyPlanVO> page = new Page<>();
        page.setCurrent(queryParam.getCurrent());
        page.setSize(queryParam.getSize());
        List<EntSurveyPlanVO> surveyPlanList = entSurveyPlanMapper.pageIntensiveSurveyPlan(page, queryParam);
        setAttachment(surveyPlanList);
        page.setRecords(surveyPlanList);
        return page;
    }

    private void setAttachment(List<EntSurveyPlanVO> surveyPlanList) {
        List<String> sourceIds = new ArrayList<>();
        for (EntSurveyPlanVO planVO: surveyPlanList) {
            if (ObjectUtils.isNotNull(planVO.getHasAttachment())
                    && planVO.getHasAttachment() ==  BaseEnum.VALID_YES.getCode()) {
                sourceIds.add(planVO.getId());
            }
        }
        if (CollectionUtils.isNotEmpty(sourceIds)) {
            List<BaseFileupload> files = baseFileuploadMapper
                    .selectList(new QueryWrapper<BaseFileupload>()
                            .in("source_id", sourceIds)
                            .eq("valid", BaseEnum.VALID_YES.getCode()));
            if (CollectionUtils.isNotEmpty(files)) {
                for (BaseFileupload file: files) {
                    for (EntSurveyPlanVO planVO: surveyPlanList) {
                        if (ObjectUtils.isNotNull(planVO.getHasAttachment())
                                && planVO.getHasAttachment() ==  BaseEnum.VALID_YES.getCode()
                                && planVO.getId().equals(file.getSourceId())) {
                            planVO.getAttachmentList().add(file);
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void generateBasicSurveyPlan(String enterpriseId) {
        EnterpriseVO enterprise = getEnterpriseInfo(enterpriseId);
        List<SurveyItemVO> surveyItemList = listSurveyItem(SurveyItemType.BASIC.getCode());
        List<EntSurveyPlan> planList = generateSurveyPlanList(enterprise, surveyItemList);
        saveBatch(planList);
    }

    private List<EntSurveyPlan> generateSurveyPlanList(EnterpriseVO enterprise, List<SurveyItemVO> surveyItemList) {
        List<EntSurveyPlan> planList = new ArrayList<>();
        for (SurveyItemVO vo: surveyItemList) {
            EntSurveyPlan plan = new EntSurveyPlan();
            plan.setId(StringUtil.getUUID());
            plan.setEntId(enterprise.getId());
            plan.setSurveyItemId(vo.getId());
            if (ObjectUtils.isNotNull(vo.getTargetBisCode())) {
                setDefaultResult(enterprise, vo, plan);
            }
            Date now = DateUtil.getNowTimestamp();
            plan.setCreateTime(now);
            plan.setUpdateTime(now);
            plan.setValid((Integer) BaseEnum.VALID_YES.getCode());
            planList.add(plan);
        }
        return planList;
    }

    private List<SurveyItemVO> listSurveyItem(String type) {
        SurveyItemQueryParam queryParam = new SurveyItemQueryParam();
        queryParam.setType(type);
        return surveyItemMapper.listSurveyItem(queryParam);
    }

    private void setDefaultResult(EnterpriseVO enterprise, SurveyItemVO vo, EntSurveyPlan plan) {
        ParamQueryParam paramQueryParam = new ParamQueryParam();
        paramQueryParam.setTemplateCategoryType(vo.getTargetBisCode());
        paramQueryParam.setEnterpriseId(enterprise.getId());
        List<ParamVO> paramList = paramMapper.listEntParamValue(paramQueryParam);
        if (CollectionUtils.isNotEmpty(paramList)) {
            if (TemplateCategory.RAW_MATERIAL.getCode().equals(vo.getTargetBisCode())
                    || TemplateCategory.EMISSION_INTENSITY.getCode().equals(vo.getTargetBisCode())
                    || TemplateCategory.EFFLUENT_INTENSITY.getCode().equals(vo.getTargetBisCode())
                    || TemplateCategory.HEAVY_METAL_INTENSITY.getCode().equals(vo.getTargetBisCode())) {
                combineDefaultResult(enterprise, plan, paramList);
            }
            if (TemplateCategory.PRODUCTION.getCode().equals(vo.getTargetBisCode())) {
                plan.setDefaultResult(enterprise.getMainProductName() + ":" + paramList.get(0).getValue());
            }
        }
    }

    private void combineDefaultResult(EnterpriseVO enterprise, EntSurveyPlan plan, List<ParamVO> paramList) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < paramList.size(); i++) {
            builder.append(paramList.get(i).getName()
                    + ":"
                    + Double.valueOf(paramList.get(i).getValue()) * enterprise.getYield());
            if (i < paramList.size() - 1) {
                builder.append("\n");
            }
        }
        plan.setDefaultResult(builder.toString());
    }

    @Override
    public void generateIntensiveSurveyPlan(String enterpriseId) {
        EnterpriseVO enterprise = getEnterpriseInfo(enterpriseId);
        List<EntRiskAssessResultVO> riskAssessResultList = listEntRiskAssessResult(enterpriseId);
        List<SurveyItemVO> surveyItemList = listSurveyItem(SurveyItemType.INTENSIVE.getCode());
        HashMap<String, EntRiskAssessResultVO> map = compareAssessResult(riskAssessResultList);
        List<EntSurveyPlan> surveyPlanList = generateSurveyPlanList(enterpriseId, surveyItemList, map);
        saveBatch(surveyPlanList);
    }

    private List<EntSurveyPlan> generateSurveyPlanList(String enterpriseId, List<SurveyItemVO> surveyItemList, HashMap<String, EntRiskAssessResultVO> map) {
        List<EntSurveyPlan> surveyPlanList = new ArrayList<>();
        Iterator iterator= map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            EntRiskAssessResultVO assessResult = (EntRiskAssessResultVO) entry.getValue();
            if (GradeLineResultCode.LOW.getCode().equals(assessResult.getGradeLineCode())) {
                for (SurveyItemVO vo: surveyItemList) {
                    if (assessResult.getTargetCode().equals(vo.getTargetWeightCode())
                            && vo.getNeedSelected() == 1) {
                        addSurveyPlan(enterpriseId, surveyPlanList, assessResult, vo);
                    }
                }
            } else {
                if (TargetEnum.RISK_FACTOR.getCode().equals(assessResult.getTargetCode())) {
                    for (SurveyItemVO vo: surveyItemList) {
                        if (assessResult.getTargetCode().equals(vo.getTargetWeightCode())
                                && vo.getNeedSelected() == 0) {
                            addSurveyPlan(enterpriseId, surveyPlanList, assessResult, vo);
                        }
                    }
                } else {
                    for (SurveyItemVO vo: surveyItemList) {
                        if (assessResult.getTargetCode().equals(vo.getTargetWeightCode())) {
                            addSurveyPlan(enterpriseId, surveyPlanList, assessResult, vo);
                        }
                    }
                }
            }
        }
        return surveyPlanList;
    }

    private HashMap<String, EntRiskAssessResultVO> compareAssessResult(List<EntRiskAssessResultVO> riskAssessResultList) {
        HashMap<String, EntRiskAssessResultVO> map = new HashMap<>();
        for (EntRiskAssessResultVO vo: riskAssessResultList) {
            if (ObjectUtils.isNotNull(map.get(vo.getTargetCode()))) {
                if (Double.valueOf(vo.getTargetResult()) > Double.valueOf(map.get(vo.getTargetCode()).getTargetResult())) {
                    map.put(vo.getTargetCode(), vo);
                }
            } else {
                map.put(vo.getTargetCode(), vo);
            }
        }
        return map;
    }

    private List<EntRiskAssessResultVO> listEntRiskAssessResult(String enterpriseId) {
        List<String> targetCodes = new ArrayList<>();
        targetCodes.add(TargetEnum.RISK_FACTOR.getCode());
        targetCodes.add(TargetEnum.PRIMARY_CONTROL_MECHANISM.getCode());
        targetCodes.add(TargetEnum.SECONDARY_CONTROL_MECHANISM.getCode());
        targetCodes.add(TargetEnum.R_FOUR_ONE.getCode());
        targetCodes.add(TargetEnum.R_FOUR_TWO.getCode());
        targetCodes.add(TargetEnum.R_FOUR_THREE.getCode());
        targetCodes.add(TargetEnum.R_FOUR_FOUR.getCode());
        targetCodes.add(TargetEnum.R_FOUR_FIVE.getCode());
        EntRiskAssessResultQueryParam queryParam = new EntRiskAssessResultQueryParam();
        queryParam.setEnterpriseId(enterpriseId);
        queryParam.setTargetCodeList(targetCodes);
        return entRiskAssessResultMapper.listEntRiskAssessResultLevel(queryParam);
    }

    private void addSurveyPlan(String enterpriseId, List<EntSurveyPlan> surveyPlanList, EntRiskAssessResultVO assessResult, SurveyItemVO vo) {
        EntSurveyPlan surveyPlan = new EntSurveyPlan();
        surveyPlan.setId(StringUtil.getUUID());
        surveyPlan.setEntId(enterpriseId);
        surveyPlan.setSurveyItemId(vo.getId());
        surveyPlan.setEntTargetResultId(assessResult.getId());
        Date now = DateUtil.getNowTimestamp();
        surveyPlan.setCreateTime(now);
        surveyPlan.setUpdateTime(now);
        surveyPlan.setValid((Integer) BaseEnum.VALID_YES.getCode());
        surveyPlanList.add(surveyPlan);
    }

    private EnterpriseVO getEnterpriseInfo(String enterpriseId) {
        EnterpriseQueryParam queryParam = new EnterpriseQueryParam();
        queryParam.setEnterpriseId(enterpriseId);
        EnterpriseVO enterprise = enterpriseInfoMapper.getEnterprise(queryParam);
        if (org.springframework.util.ObjectUtils.isEmpty(enterprise)) {
            throw new BusinessException("The enterprise has been removed!");
        }
        return enterprise;
    }
}
