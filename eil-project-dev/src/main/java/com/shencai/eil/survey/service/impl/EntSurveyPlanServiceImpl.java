package com.shencai.eil.survey.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shencai.eil.common.constants.*;
import com.shencai.eil.common.utils.DateUtil;
import com.shencai.eil.common.utils.ObjectUtil;
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
import com.shencai.eil.scenario.service.IScenarioSelectionInfoService;
import com.shencai.eil.survey.constants.DefaultValueMapping;
import com.shencai.eil.survey.constants.ExcelImportStatus;
import com.shencai.eil.survey.entity.EntSurveyExtendInfo;
import com.shencai.eil.survey.entity.EntSurveyPlan;
import com.shencai.eil.survey.entity.EntSurveyResult;
import com.shencai.eil.survey.mapper.EntSurveyExtendInfoMapper;
import com.shencai.eil.survey.mapper.EntSurveyPlanMapper;
import com.shencai.eil.survey.mapper.SurveyItemMapper;
import com.shencai.eil.survey.model.*;
import com.shencai.eil.survey.service.IEntSurveyPlanService;
import com.shencai.eil.survey.service.IEntSurveyResultService;
import com.shencai.eil.system.entity.BaseFileupload;
import com.shencai.eil.system.mapper.BaseFileuploadMapper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
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
    @Autowired
    private IScenarioSelectionInfoService scenarioSelectionInfoService;
    @Autowired
    private EntSurveyExtendInfoMapper entSurveyExtendInfoMapper;
    @Autowired
    private IEntSurveyResultService entSurveyResultService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EnterpriseVO getEntSurveyInfo(String enterpriseId) {
        EnterpriseVO enterprise = getEnterpriseOfSurvey(enterpriseId);
        if (ObjectUtil.isEmpty(enterprise)) {
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
            throw new BusinessException("general comment did not been calculated!");
        }
        generateBasicSurveyPlan(enterpriseId);
        updateRiskLevel(enterpriseId, riskResultList);
    }

    private void updateRiskLevel(String enterpriseId, List<EntRiskAssessResultVO> riskResultList) {
        EnterpriseInfo enterpriseInfo = new EnterpriseInfo();
        enterpriseInfo.setRiskLevel(RiskLevel.LOW.getCode());
        for (EntRiskAssessResultVO entRiskAssessResultVO : riskResultList) {
            if (!GradeLineResultCode.LOW.getCode().equals(entRiskAssessResultVO.getGradeLineCode())) {
                enterpriseInfo.setRiskLevel(RiskLevel.HIGH.getCode());
                generateIntensiveSurveyPlan(enterpriseId);
                break;
            }
        }
        enterpriseInfo.setStatus(StatusEnum.I_SURVEY.getCode());
        enterpriseInfo.setId(enterpriseId);
        enterpriseInfo.setUpdateTime(DateUtil.getNowTimestamp());
        enterpriseInfoMapper.updateById(enterpriseInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void surveyUpgrade(EnterpriseParam param) {
        EnterpriseInfo enterpriseInfo = new EnterpriseInfo();
        enterpriseInfo.setId(param.getId());
        enterpriseInfo.setNeedSurveyUpgrade(param.getNeedSurveyUpgrade());
        if (BaseEnum.VALID_YES.getCode() == param.getNeedSurveyUpgrade()) {
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
        queryParam.setCategoryCode(SurveyItemCategoryCode.WEB_VIEW.getCode());
        page.setRecords(entSurveyPlanMapper.pageBasicSurveyPlan(page, queryParam));
        return page;
    }

    @Override
    public Page<EntSurveyPlanVO> pageIntensiveSurveyPlan(EntSurveyPlanQueryParam queryParam) {
        Page<EntSurveyPlanVO> page = new Page<>();
        page.setCurrent(queryParam.getCurrent());
        page.setSize(queryParam.getSize());
        queryParam.setCategoryCode(SurveyItemCategoryCode.WEB_VIEW.getCode());
        List<EntSurveyPlanVO> surveyPlanList = entSurveyPlanMapper.pageIntensiveSurveyPlan(page, queryParam);
        setAttachment(surveyPlanList);
        page.setRecords(surveyPlanList);
        return page;
    }

    @Override
    public SurveyFileVO getSurveyUploadFileStatus(String enterpriseId) {
        SurveyFileVO resultVO = new SurveyFileVO();
        List<BaseFileupload> fileList = listIntensiveUploadFile(enterpriseId);
        if (CollectionUtils.isEmpty(fileList)) {
            resultVO.setStatus(ExcelImportStatus.NOT_UPLOAD.getCode());
        } else {
            resultVO.setFileId(fileList.get(0).getId());
            resultVO.setFileName(fileList.get(0).getFileName());
            List<EntSurveyExtendInfo> surveyExtendInfoList = entSurveyExtendInfoMapper
                    .selectList(new QueryWrapper<EntSurveyExtendInfo>()
                            .eq("ent_id", enterpriseId)
                            .eq("valid", BaseEnum.VALID_YES.getCode()));
            if (CollectionUtils.isNotEmpty(surveyExtendInfoList)) {
                resultVO.setStatus(ExcelImportStatus.COMPLETED.getCode());
            } else {
                resultVO.setStatus(ExcelImportStatus.UPLOADED.getCode());
            }
        }
        return resultVO;
    }

    private List<BaseFileupload> listIntensiveUploadFile(String enterpriseId) {
        return baseFileuploadMapper
                .selectList(new QueryWrapper<BaseFileupload>()
                        .eq("source_id", enterpriseId)
                        .eq("stype", FileSourceType.INTENSIVE_SURVEY_PLAN_UPLOAD.getCode())
                        .eq("valid", BaseEnum.VALID_YES.getCode()));
    }

    private void setAttachment(List<EntSurveyPlanVO> surveyPlanList) {
        List<String> sourceIds = new ArrayList<>();
        for (EntSurveyPlanVO planVO : surveyPlanList) {
            if (!ObjectUtil.isEmpty(planVO.getHasAttachment())
                    && planVO.getHasAttachment() == BaseEnum.VALID_YES.getCode()) {
                sourceIds.add(planVO.getId());
            }
        }
        if (CollectionUtils.isNotEmpty(sourceIds)) {
            List<BaseFileupload> files = baseFileuploadMapper
                    .selectList(new QueryWrapper<BaseFileupload>()
                            .in("source_id", sourceIds)
                            .eq("valid", BaseEnum.VALID_YES.getCode()));
            if (CollectionUtils.isNotEmpty(files)) {
                for (BaseFileupload file : files) {
                    for (EntSurveyPlanVO planVO : surveyPlanList) {
                        List fileList = new ArrayList();
                        if (!ObjectUtil.isEmpty(planVO.getHasAttachment())
                                && planVO.getHasAttachment() == BaseEnum.VALID_YES.getCode()
                                && planVO.getId().equals(file.getSourceId())) {
                            fileList.add(file);
                            planVO.setAttachmentList(fileList);
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * bind relation of basic survey item
     *
     * @param enterpriseId
     */
    private void generateBasicSurveyPlan(String enterpriseId) {
        EnterpriseVO enterprise = getEnterpriseInfo(enterpriseId);
        List<SurveyItemVO> surveyItemList = listBasicSurveyItem();
        List<EntSurveyPlan> planList = generateSurveyPlanList(enterprise, surveyItemList);
        saveBatch(planList);
    }

    private List<SurveyItemVO> listBasicSurveyItem() {
        List<String> categoryCodeList = new ArrayList<>();
        categoryCodeList.add(SurveyItemCategoryCode.DEFAULT_VALUE.getCode());
        return listSurveyItem(SurveyItemType.BASIC.getCode(), categoryCodeList);
    }

    private List<EntSurveyPlan> generateSurveyPlanList(EnterpriseVO enterprise, List<SurveyItemVO> surveyItemList) {
        List<EntSurveyPlan> planList = new ArrayList<>();
        List<ParamVO> paramList = listDefaultValueParam(enterprise);
        List<SurveyItemVO> exportItems = listExportItem(SurveyItemCategoryCode.EXCEL_VIEW_BASIC_SURVEY_TABLE.getCode());
        int index = 0;
        for (int i = 0; i < surveyItemList.size(); i++) {
            SurveyItemVO vo = surveyItemList.get(i);
            EntSurveyPlan plan = new EntSurveyPlan();
            plan.setId(StringUtil.getUUID());
            plan.setEntId(enterprise.getId());
            plan.setSurveyItemId(vo.getId());
            for (SurveyItemVO item : exportItems) {
                if (vo.getId().equals(item.getId())) {
                    plan.setExcelColIndex(index);
                    index++;
                    break;
                }
            }
            if (!ObjectUtil.isEmpty(vo.getCategoryCode())) {
                setDefaultResult(enterprise, vo, plan, paramList);
            }
            Date now = DateUtil.getNowTimestamp();
            plan.setCreateTime(now);
            plan.setUpdateTime(now);
            plan.setValid((Integer) BaseEnum.VALID_YES.getCode());
            planList.add(plan);
        }
        return planList;
    }

    private List<SurveyItemVO> listExportItem(String categoryCode) {
        SurveyItemQueryParam queryParam = new SurveyItemQueryParam();
        queryParam.setCategoryCode(categoryCode);
        return surveyItemMapper.listSurveyItemByCategory(queryParam);
    }

    private List<ParamVO> listDefaultValueParam(EnterpriseVO enterprise) {
        ParamQueryParam paramQueryParam = new ParamQueryParam();
        paramQueryParam.setTemplateCategoryType(SurveyItemCategoryCode.DEFAULT_VALUE.getCode());
        paramQueryParam.setEnterpriseId(enterprise.getId());
        return paramMapper.listEntParamValue(paramQueryParam);
    }

    private List<SurveyItemVO> listSurveyItem(String type, List<String> categoryCodeList) {
        SurveyItemQueryParam queryParam = new SurveyItemQueryParam();
        queryParam.setType(type);
        queryParam.setCategoryCodeList(categoryCodeList);
        return surveyItemMapper.listSurveyItem(queryParam);
    }

    private void setDefaultResult(EnterpriseVO enterprise, SurveyItemVO item, EntSurveyPlan plan, List<ParamVO> allDefaultParams) {
        List<ParamVO> paramList = new ArrayList<>();
        String[] templateArray = DefaultValueMapping.getTemplatesByCode(item.getCode());
        List<String> templateList = Arrays.asList(templateArray);
        for (ParamVO vo : allDefaultParams) {
            if (templateList.indexOf(vo.getTemplateCode()) > -1) {
                paramList.add(vo);
            }
        }
        if (CollectionUtils.isNotEmpty(paramList)) {
            if (templateList.indexOf(TemplateEnum.RAW_OR_AUXILIARY_MATERIALS.getCode()) > -1
                    || templateList.indexOf(TemplateEnum.OTHER_EMISSION_INTENSITY.getCode()) > -1
                    || templateList.indexOf(TemplateEnum.OTHER_EFFLUENT_INTENSITY.getCode()) > -1
                    || templateList.indexOf(TemplateEnum.EFFLUENT_INTENSITY.getCode()) > -1
                    || templateList.indexOf(TemplateEnum.EMISSION_INTENSITY.getCode()) > -1) {
                combineDefaultResult(enterprise.getYield(), plan, paramList);
            }
            if (templateList.indexOf(TemplateEnum.PRODUCTION_STATUS_OF_PRODUCTS.getCode()) > -1) {
                combineDefaultResult(null, plan, paramList);
            }
        }
    }

    private void combineDefaultResult(Double yield, EntSurveyPlan plan, List<ParamVO> paramList) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < paramList.size(); i++) {
            if (!ObjectUtil.isEmpty(yield)) {
                Double result = Double.valueOf(paramList.get(i).getValue()) * yield;
                String[] splitArray = paramList.get(i).getRemark().split("/");
                String name;
                if (splitArray.length > 1) {
                    name = splitArray[0] + "）";
                } else {
                    name = paramList.get(i).getName();
                }

                builder.append(name
                        + ":"
                        + BigDecimal.valueOf(result).setScale(2, BigDecimal.ROUND_HALF_UP));
            } else {
                builder.append(paramList.get(i).getName()
                        + ":"
                        + BigDecimal.valueOf(Double.valueOf(paramList.get(i).getValue()))
                        .setScale(2, BigDecimal.ROUND_HALF_UP));
            }

            if (i < paramList.size() - 1) {
                builder.append("\n");
            }
        }
        plan.setDefaultResult(builder.toString());
    }

    private void generateIntensiveSurveyPlan(String enterpriseId) {
        List<EntRiskAssessResultVO> riskAssessResultList = listEntRiskAssessResult(enterpriseId);
        List<SurveyItemVO> surveyItemList = listIntensiveSurveyItem();
        HashMap<String, EntRiskAssessResultVO> map = compareAssessResult(riskAssessResultList);
        List<EntSurveyPlan> surveyPlanList = generateSurveyPlanList(enterpriseId, surveyItemList, map);
        saveBatch(surveyPlanList);
    }

    private List<SurveyItemVO> listIntensiveSurveyItem() {
        List<String> categoryCodeList = new ArrayList<>();
        categoryCodeList.add(SurveyItemCategoryCode.EXCEL_VIEW_INTENSIVE_TOP5.getCode());
        categoryCodeList.add(SurveyItemCategoryCode.EXCEL_VIEW_INTENSIVE_TOP10.getCode());
        categoryCodeList.add(SurveyItemCategoryCode.EXCEL_VIEW_INTENSIVE_S1.getCode());
        categoryCodeList.add(SurveyItemCategoryCode.EXCEL_VIEW_INTENSIVE_S2.getCode());
        categoryCodeList.add(SurveyItemCategoryCode.EXCEL_VIEW_INTENSIVE_S3.getCode());
        return listSurveyItem(SurveyItemType.INTENSIVE.getCode(), categoryCodeList);
    }

    private List<EntSurveyPlan> generateSurveyPlanList(String enterpriseId, List<SurveyItemVO> surveyItemList, HashMap<String, EntRiskAssessResultVO> map) {
        List<EntSurveyPlan> surveyPlanList = new ArrayList<>();
        HashMap<String, Integer> indexMap = new HashMap<>();
        for (SurveyItemVO vo : surveyItemList) {
            EntRiskAssessResultVO assessResult = map.get(vo.getTargetWeightCode());
            if (GradeLineResultCode.LOW.getCode().equals(assessResult.getGradeLineCode())) {
                if (assessResult.getTargetCode().equals(vo.getTargetWeightCode())
                        && vo.getNeedSelected() == 1) {
                    addSurveyPlan(enterpriseId, surveyPlanList, assessResult, vo, indexMap);
                }
            } else {
                if (TargetEnum.RISK_FACTOR.getCode().equals(assessResult.getTargetCode())) {
                    if (assessResult.getTargetCode().equals(vo.getTargetWeightCode())
                            && vo.getNeedSelected() == 0) {
                        addSurveyPlan(enterpriseId, surveyPlanList, assessResult, vo, indexMap);
                    }
                } else {
                    if (assessResult.getTargetCode().equals(vo.getTargetWeightCode())) {
                        addSurveyPlan(enterpriseId, surveyPlanList, assessResult, vo, indexMap);
                    }
                }
            }
        }
        return surveyPlanList;
    }

    private HashMap<String, EntRiskAssessResultVO> compareAssessResult(List<EntRiskAssessResultVO> riskAssessResultList) {
        HashMap<String, EntRiskAssessResultVO> map = new HashMap<>();
        for (EntRiskAssessResultVO vo : riskAssessResultList) {
            if (!ObjectUtil.isEmpty(map.get(vo.getTargetCode()))) {
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

    private void addSurveyPlan(String enterpriseId, List<EntSurveyPlan> surveyPlanList
            , EntRiskAssessResultVO assessResult, SurveyItemVO vo, HashMap<String, Integer> indexMap) {
        EntSurveyPlan surveyPlan = new EntSurveyPlan();
        surveyPlan.setId(StringUtil.getUUID());
        surveyPlan.setEntId(enterpriseId);
        surveyPlan.setSurveyItemId(vo.getId());
        if (StringUtil.isNotBlank(vo.getCategoryCode())) {
            if (!indexMap.containsKey(vo.getCategoryCode())) {
                indexMap.put(vo.getCategoryCode(), 0);
            }
            Integer index = indexMap.get(vo.getCategoryCode());
            surveyPlan.setExcelColIndex(index);
            index++;
            indexMap.put(vo.getCategoryCode(), index);
        }
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
        if (ObjectUtil.isEmpty(enterprise)) {
            throw new BusinessException("enterprise is not exist!");
        }
        return enterprise;
    }


    @Override
    public void finishSurvey(String enterpriseId) {
        EnterpriseInfo enterpriseInfo = enterpriseInfoMapper.selectById(enterpriseId);
        if (ObjectUtil.isEmpty(enterpriseInfo)) {
            throw new BusinessException("enterprise is not exist!");
        }
        List<BaseFileupload> files = listFiles(enterpriseId);
        if (RiskLevel.HIGH.getCode().equals(enterpriseInfo.getRiskLevel())) {
            if (CollectionUtils.isEmpty(files)) {
                throw new BusinessException("please upload basic and intensive survey report!");
            } else if (files.size() != 2) {
                throw new BusinessException(FileSourceType.BASIC_SURVEY_PLAN_UPLOAD.getCode().equals(files.get(0).getStype())
                        ? "please upload intensive survey report!" : "please upload basic survey report!");
            }
        } else {
            if (CollectionUtils.isEmpty(files)) {
                throw new BusinessException("please upload basic survey report!");
            }
        }
        if (RiskLevel.HIGH.getCode().equals(enterpriseInfo.getRiskLevel())) {
            Integer extendCount = entSurveyExtendInfoMapper.selectCount(new QueryWrapper<EntSurveyExtendInfo>()
                    .eq("ent_id", enterpriseId)
                    .eq("valid", BaseEnum.VALID_YES.getCode()));
            if (ObjectUtils.isNull(extendCount) || extendCount == 0) {
                throw new BusinessException("please complete extend survey info!");
            }
        }
        updateEnterpriseStatus(enterpriseId, enterpriseInfo);
        scenarioSelectionInfoService.initScenarioSelectionInfo(enterpriseId);
    }

    @Override
    public String getValue(String entId, List<String> surveyItemId) {
        List<EntSurveyPlan> surveyPlans = entSurveyPlanMapper.selectList(
                new QueryWrapper<EntSurveyPlan>()
                        .eq("ent_id", entId)
                        .eq("valid", BaseEnum.VALID_YES.getCode())
                        .in("survey_item_id", surveyItemId));
        if (CollectionUtils.isEmpty(surveyPlans)) {
            throw new BusinessException("ent_survey_plan table not exit " + surveyItemId + " survey item of this enterprise");
        }
        List<String> planIds = surveyPlans.stream().map(EntSurveyPlan::getId).collect(Collectors.toList());
        List<EntSurveyResult> surveyResults = entSurveyResultService.list(
                new QueryWrapper<EntSurveyResult>()
                        .in("survey_plan_id", planIds)
                        .eq("valid", BaseEnum.VALID_YES.getCode()));
        String value = CollectionUtils.isEmpty(surveyResults) ? null : surveyResults.get(0).getResult();
        return value;
    }

    private void updateEnterpriseStatus(String enterpriseId, EnterpriseInfo enterpriseInfo) {
        enterpriseInfo.setStatus(StatusEnum.IN_DEPTH_EVALUATION.getCode());
        enterpriseInfo.setUpdateTime(DateUtil.getNowTimestamp());
        enterpriseInfoMapper.update(enterpriseInfo, new QueryWrapper<EnterpriseInfo>().eq("id", enterpriseId));
    }

    private List<BaseFileupload> listFiles(String enterpriseId) {
        return baseFileuploadMapper.selectList(new QueryWrapper<BaseFileupload>()
                .eq("source_id", enterpriseId)
                .in("stype", Arrays.asList(new String[]{FileSourceType.BASIC_SURVEY_PLAN_UPLOAD.getCode()
                        , FileSourceType.INTENSIVE_SURVEY_PLAN_UPLOAD.getCode()}))
                .eq("valid", BaseEnum.VALID_YES.getCode()));
    }
}
