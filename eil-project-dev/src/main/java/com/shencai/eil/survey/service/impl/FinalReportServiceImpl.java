package com.shencai.eil.survey.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.shencai.eil.assessment.entity.EntAssessResult;
import com.shencai.eil.assessment.mapper.EntAssessResultMapper;
import com.shencai.eil.assessment.mapper.EntDiffusionModelInfoMapper;
import com.shencai.eil.assessment.mapper.GridCalculationValueMapper;
import com.shencai.eil.assessment.model.EntDiffusionModelInfoQueryParam;
import com.shencai.eil.assessment.model.EntDiffusionModelInfoVO;
import com.shencai.eil.assessment.model.GridCalculationValueVO;
import com.shencai.eil.common.constants.*;
import com.shencai.eil.common.utils.DateUtil;
import com.shencai.eil.common.utils.EilFileUtil;
import com.shencai.eil.common.utils.StringUtil;
import com.shencai.eil.exception.BusinessException;
import com.shencai.eil.grading.entity.EntMappingTargetType;
import com.shencai.eil.grading.mapper.EntMappingTargetTypeMapper;
import com.shencai.eil.grading.mapper.EntRiskAssessResultMapper;
import com.shencai.eil.grading.model.EntRiskAssessResultQueryParam;
import com.shencai.eil.grading.model.EntRiskAssessResultVO;
import com.shencai.eil.policy.entity.EnterpriseInfo;
import com.shencai.eil.policy.mapper.EnterpriseInfoMapper;
import com.shencai.eil.policy.mapper.IndustryCategoryMapper;
import com.shencai.eil.scenario.mapper.AccidentScenarioMapper;
import com.shencai.eil.scenario.model.AccidentScenarioVO;
import com.shencai.eil.survey.constants.FinalReportParam;
import com.shencai.eil.survey.entity.GradeTemplateParam;
import com.shencai.eil.survey.mapper.EntSurveyExtendInfoMapper;
import com.shencai.eil.survey.mapper.EntSurveyPlanMapper;
import com.shencai.eil.survey.mapper.GradeTemplateParamMapper;
import com.shencai.eil.survey.model.*;
import com.shencai.eil.survey.service.IFinalReportService;
import com.shencai.eil.system.entity.BaseFileupload;
import com.shencai.eil.system.mapper.BaseFileuploadMapper;
import com.shencai.eil.system.model.CustomBarRenderer;
import com.shencai.eil.system.model.CustomXWPFDocument;
import com.shencai.eil.system.model.FilePathConfig;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * Created by fanhj on 2018/10/16.
 */
@Service
public class FinalReportServiceImpl implements IFinalReportService {
    @Autowired
    private EnterpriseInfoMapper enterpriseInfoMapper;
    @Autowired
    private IndustryCategoryMapper industryCategoryMapper;
    @Autowired
    private GradeTemplateParamMapper gradeTemplateParamMapper;
    @Autowired
    private EntMappingTargetTypeMapper entMappingTargetTypeMapper;
    @Autowired
    private AccidentScenarioMapper accidentScenarioMapper;
    @Autowired
    private EntSurveyPlanMapper entSurveyPlanMapper;
    @Autowired
    private EntSurveyExtendInfoMapper entSurveyExtendInfoMapper;
    @Autowired
    private BaseFileuploadMapper baseFileuploadMapper;
    @Autowired
    private EntRiskAssessResultMapper entRiskAssessResultMapper;
    @Autowired
    private EntAssessResultMapper entAssessResultMapper;
    @Autowired
    private EntDiffusionModelInfoMapper entDiffusionModelInfoMapper;
    @Autowired
    private GridCalculationValueMapper gridCalculationValueMapper;
    @Autowired
    private FilePathConfig filePathConfig;

    private static final String COMPONENT_LOCATION_VALID = "地下";
    private static final List<String> COMPONENT_TYPE_OF_STORAGE_LIST
            = Arrays.asList(new String[]{"反应釜、工艺储罐、气体储罐、塔器", "常压单包容储罐", "常压储罐"});
    private static final List<String> COMPONENT_TYPE_OF_PIPE_LIST
            = Arrays.asList(new String[]{"内径≤75mm管道", "75mm＜内径≤150mm管道", "内径＞150管道"});
    private static final String OPTION_VALUE_YES = "是";
    private static final String CHAIN_EFFECTIVE = "entExisted";
    private static final String RISK_LEVEL_FLAG = "risk_level";
    private static final List<String> RISK_LEVEL_LIST
            = Arrays.asList(new String[]{"involvedRiskMaterial", "hasHighPressureDevice", "existStorage", "existPipe"});
    private static final String CONTROL_RISK_LEVEL_FLAG = "control_risk_level";
    private static final int LOW_RISK_LEVEL_LIMIT = 3;
    private static final List<String> CONTROL_RISK_LEVEL_LIST
            = Arrays.asList(new String[]{"unloadInvolved", "unloadSpecify", "hasSafeBasin", "autoInBasin", "hasCofferdam",
            "hasStorage", "hasAlarmSystem", "professionalHandled", "empSpecify", "hasFireDivice"});
    private static final int LOW_CONTROL_RISK_LEVEL_LIMIT = 6;
    private static final String RECEPTOR_RISK_LEVEL_FLAG = "receptor_risk_level";
    private static final List<String> RECEPTOR_RISK_LEVEL_LIST
            = Arrays.asList(new String[]{"hasSurfaceWater", "hasHospital", "hasSchool", "hasCommunity", "hasFarm"});
    private static final int LOW_RECEPTOR_RISK_LEVEL_LIMIT = 1;
    private static final String YES = "1";
    private static final String EXIST = "存在";
    private static final String NOT_EXIST = "不存在";
    private static final String HIGH_RISK = "高";
    private static final String LOW_RISK = "低";
    private static final String BUT = ", 但";
    private static final String FILE_NAME = "final_report";
    private static final String HIGHER = "较高";
    private static final String LOWER = "较低";
    private static final String RISK_FACTOR_MAPPING_CONTENT = "生产全过程中使用的物质、";
    private static final String PRIMARY_CONTROL_MECHANISM_MAPPING_CONTENT = "生产全过程中的管控措施、";
    private static final String SECONDARY_CONTROL_MECHANISM_MAPPING_CONTENT = "企业应急能力和设施建设、";
    private static final String R_FOUR_ONE_MAPPING_CONTENT = "企业周边敏感人群目标情况、";
    private static final String R_FOUR_TWO_MAPPING_CONTENT = "企业周边敏感财产目标情况、";
    private static final String R_FOUR_THREE_MAPPING_CONTENT = "企业周边敏感生态目标情况、";
    private static final String R_FOUR_FOUR_MAPPING_CONTENT = "企业周边水环境情况、";
    private static final String R_FOUR_FIVE_MAPPING_CONTENT = "企业周边土壤环境情况、";

    private static final String COST_DESC = "应急处置与清污费用、";
    private static final String ESV_DESC = "生态环境损害、";
    private static final String BI_DESC = "第三者财产损失、";
    private static final String PI_DESC = "第三者人身损害、";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String getFinalReport(String enterpriseId) {
        List<BaseFileupload> fileList = listExistFiles(enterpriseId, FileSourceType.FINAL_REPORT.getCode());
        String pdfAddress = null;
        if (org.springframework.util.CollectionUtils.isEmpty(fileList) || fileList.size() != 2) {
            pdfAddress = generateFinalReport(enterpriseId);
        } else {
            for (BaseFileupload file : fileList) {
                if (file.getFileAdress().indexOf(".pdf") > -1) {
                    pdfAddress = file.getId();
                }
            }
        }
        return pdfAddress;
    }

    private List<BaseFileupload> listExistFiles(String enterpriseId, String sourceType) {
        return baseFileuploadMapper.selectList(new QueryWrapper<BaseFileupload>()
                .eq("source_id", enterpriseId)
                .eq("stype", sourceType)
                .eq("valid", BaseEnum.VALID_YES.getCode()));
    }

    private String generateFinalReport(String enterpriseId) {
        HashMap<String, Object> replaceMap = new HashMap<>();
        HashMap<String, Integer> needSpecialHandleMap = new HashMap<>();
        EnterpriseInfo enterpriseInfo = getEnterpriseInfo(enterpriseId);
        EntSurveyExtendInfoVO infoVO = getEntSurveyExtendInfo(enterpriseId);
        int totalRiskType = getTotalType(enterpriseId);
        setEnterpriseInfoParams(enterpriseInfo, replaceMap);
        setAssessmentProgressInfo(enterpriseId, replaceMap, totalRiskType, infoVO);
        //深度评估结果
        setDepthAssessment(enterpriseId, replaceMap);
        List<GridCalculationValueVO> calculationValueList = gridCalculationValueMapper.listStatisticsValueByBisType(enterpriseId);
        if (CollectionUtils.isEmpty(calculationValueList)) {
            throw new BusinessException("do not finish calculate formula of simulation!");
        }
        //损害评估损害最大类型
        setGreatestLossDesc(replaceMap, calculationValueList);
        //比较土壤和水损失差设置GREATEST_MEDIUM_LOSS 和 MEDIUM
        setCostDescByComparing(replaceMap, calculationValueList);

        List<EntRiskAssessResultVO> assessResultList = listEntRiskAssessResult(enterpriseId);
        String gradualRiskInfo = "";
        if (totalRiskType > 1) {
            gradualRiskInfo = FinalReportParam.EXIST_GRADUAL_RISK.getDefaultValue();
        }
        StringBuilder importantPoints = new StringBuilder();
        for (EntRiskAssessResultVO assessResult: assessResultList) {
            if (TargetEnum.R_U.getCode().equals(assessResult.getTargetCode())) {
                if (TargetWeightType.PROGRESSIVE_RISK.getCode().equals(assessResult.getTargetType())) {
                    if (StringUtil.isNotBlank(gradualRiskInfo)) {
                        if (GradeLineResultCode.LOW.getCode().equals(assessResult.getGradeLineCode())) {
                            gradualRiskInfo = gradualRiskInfo.replace(FinalReportParam.GRADUAL_RISK_LEVEL.getCode(), LOWER);
                        } else {
                            gradualRiskInfo = gradualRiskInfo.replace(FinalReportParam.GRADUAL_RISK_LEVEL.getCode(), HIGHER);
                        }
                    }
                } else {
                    if (GradeLineResultCode.LOW.getCode().equals(assessResult.getGradeLineCode())) {
                        replaceMap.put(FinalReportParam.SUDDEN_RISK_LEVEL.getCode(), LOWER);
                    } else {
                        replaceMap.put(FinalReportParam.SUDDEN_RISK_LEVEL.getCode(), HIGHER);
                    }
                }
            } else if (TargetEnum.RISK_FACTOR.getCode().equals(assessResult.getTargetCode())
                    && !GradeLineResultCode.LOW.getCode().equals(assessResult.getGradeLineCode())) {
                importantPoints.append(RISK_FACTOR_MAPPING_CONTENT);
            } else if (TargetEnum.PRIMARY_CONTROL_MECHANISM.getCode().equals(assessResult.getTargetCode())
                    && !GradeLineResultCode.LOW.getCode().equals(assessResult.getGradeLineCode())) {
                importantPoints.append(PRIMARY_CONTROL_MECHANISM_MAPPING_CONTENT);
            } else if (TargetEnum.SECONDARY_CONTROL_MECHANISM.getCode().equals(assessResult.getTargetCode())
                    && !GradeLineResultCode.LOW.getCode().equals(assessResult.getGradeLineCode())) {
                importantPoints.append(SECONDARY_CONTROL_MECHANISM_MAPPING_CONTENT);
            } else if (TargetEnum.R_FOUR_ONE.getCode().equals(assessResult.getTargetCode())
                    && !GradeLineResultCode.LOW.getCode().equals(assessResult.getGradeLineCode())) {
                importantPoints.append(R_FOUR_ONE_MAPPING_CONTENT);
            } else if (TargetEnum.R_FOUR_TWO.getCode().equals(assessResult.getTargetCode())
                    && !GradeLineResultCode.LOW.getCode().equals(assessResult.getGradeLineCode())) {
                importantPoints.append(R_FOUR_TWO_MAPPING_CONTENT);
            } else if (TargetEnum.R_FOUR_THREE.getCode().equals(assessResult.getTargetCode())
                    && !GradeLineResultCode.LOW.getCode().equals(assessResult.getGradeLineCode())) {
                importantPoints.append(R_FOUR_THREE_MAPPING_CONTENT);
            } else if (TargetEnum.R_FOUR_FOUR.getCode().equals(assessResult.getTargetCode())
                    && !GradeLineResultCode.LOW.getCode().equals(assessResult.getGradeLineCode())) {
                importantPoints.append(R_FOUR_FOUR_MAPPING_CONTENT);
            } else if (TargetEnum.R_FOUR_FIVE.getCode().equals(assessResult.getTargetCode())
                    && !GradeLineResultCode.LOW.getCode().equals(assessResult.getGradeLineCode())) {
                importantPoints.append(R_FOUR_FIVE_MAPPING_CONTENT);
            }
        }
        if (StringUtil.isNotBlank(importantPoints.toString())) {
            replaceMap.put(FinalReportParam.IMPORTANCE_ON.getCode(), importantPoints.toString().substring(0, importantPoints.length() - 1));
        } else {
            replaceMap.put(FinalReportParam.IMPORTANCE_ON.getCode(), "");
        }
        replaceMap.put(FinalReportParam.EXIST_GRADUAL_RISK.getCode(), gradualRiskInfo);
        replaceMap.put(FinalReportParam.RANKING_OF_RISK.getCode(), "(开发中...)");
        if (RiskLevel.HIGH.getCode().equals(enterpriseInfo.getRiskLevel())) {
            setSomeParams(enterpriseId, replaceMap, needSpecialHandleMap);
            setIntensiveParam(enterpriseId, replaceMap, needSpecialHandleMap);
            setRiskJudgement(replaceMap, needSpecialHandleMap, infoVO);
            setSuggestions(replaceMap, infoVO);
            setPictures(replaceMap, infoVO);
            replaceMap.put(FinalReportParam.NEED_UPGRADE.getCode(), FinalReportParam.NEED_UPGRADE.getDefaultValue());
        } else {
            List<GradeTemplateParamVO> paramList = gradeTemplateParamMapper.listParamWithoutSceneSurvey(enterpriseId);
            for (GradeTemplateParamVO param: paramList) {
                replaceMap.put(param.getParamCode(), param.getParamContent());
            }
            setBasicParam(enterpriseId, replaceMap);
            replaceMap.put(FinalReportParam.NEED_UPGRADE.getCode(), "");
        }
        String fileName = StringUtil.getUUID();
        try {
            String srcPath;
            if (RiskLevel.HIGH.getCode().equals(enterpriseInfo.getRiskLevel())) {
                srcPath = filePathConfig.highRiskFinalReport;
            } else {
                srcPath = filePathConfig.lowRiskFinalReport;
            }
            String destDocPath = filePathConfig.destPath + fileName + ".docx";
            String destPdfPath = filePathConfig.destPath + fileName + ".pdf";
            CustomXWPFDocument doc = new CustomXWPFDocument(POIXMLDocument.openPackage(srcPath));
            replaceWords(replaceMap, doc);
            OutputStream os = new FileOutputStream(new File(destDocPath));
            doc.write(os);
            os.close();
            EilFileUtil.word2pdf(destDocPath, destPdfPath);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        deleteFiles(enterpriseId, FileSourceType.FINAL_REPORT.getCode());
        insertFile(enterpriseId, fileName + ".docx"
                , FileSourceType.FINAL_REPORT.getCode(), buildFileName(FILE_NAME));
        String pdfAddress = insertFile(enterpriseId, fileName + ".pdf"
                , FileSourceType.FINAL_REPORT.getCode(), buildFileName(FILE_NAME));
        return pdfAddress;
    }

    private void setCostDescByComparing(HashMap<String, Object> replaceMap, List<GridCalculationValueVO> calculationValueList) {
        if (calculationValueList.size() == 1){
            if (DataModelType.WATER_TYPE.getCode().equals(calculationValueList.get(0).getBisCode())) {
                searchAndSetParam(replaceMap, GradeTemplateParamType.FINAL_REPORT.getCode()
                        , FinalReportParam.GREATEST_MEDIUM_LOSS.getCode(), String.valueOf(BaseEnum.YES.getCode()));
                searchAndSetParam(replaceMap, GradeTemplateParamType.FINAL_REPORT.getCode()
                        , FinalReportParam.MEDIUM.getCode(), String.valueOf(BaseEnum.YES.getCode()));
            } else {
                searchAndSetParam(replaceMap, GradeTemplateParamType.FINAL_REPORT.getCode()
                        , FinalReportParam.GREATEST_MEDIUM_LOSS.getCode(), String.valueOf(BaseEnum.NO.getCode()));
                searchAndSetParam(replaceMap, GradeTemplateParamType.FINAL_REPORT.getCode()
                        , FinalReportParam.MEDIUM.getCode(), String.valueOf(BaseEnum.NO.getCode()));
            }
        } else {
            double waterSum = .0;
            double soilSum = .0;
            GridCalculationValueVO waterValueVO;
            GridCalculationValueVO soilValueVO;
            if (DataModelType.WATER_TYPE.getCode().equals(calculationValueList.get(0).getBisCode())) {
                waterValueVO = calculationValueList.get(0);
                soilValueVO = calculationValueList.get(1);
            } else {
                waterValueVO = calculationValueList.get(1);
                soilValueVO = calculationValueList.get(0);
            }
            waterSum = waterValueVO.getBi() + waterValueVO.getEsv()
                        + waterValueVO.getCost() + waterValueVO.getLossHi() + waterValueVO.getLossCr();
            soilSum = soilValueVO.getBi() + soilValueVO.getEsv()
                        + soilValueVO.getCost() + soilValueVO.getLossHi() + soilValueVO.getLossCr();
            if (waterSum > soilSum && 0.95 * waterSum > soilSum) {
                searchAndSetParam(replaceMap, GradeTemplateParamType.FINAL_REPORT.getCode()
                            , FinalReportParam.GREATEST_MEDIUM_LOSS.getCode(), String.valueOf(BaseEnum.YES.getCode()));
                searchAndSetParam(replaceMap, GradeTemplateParamType.FINAL_REPORT.getCode()
                            , FinalReportParam.MEDIUM.getCode(), String.valueOf(BaseEnum.YES.getCode()));
            } else if (soilSum > waterSum && 0.95 * soilSum > waterSum) {
                searchAndSetParam(replaceMap, GradeTemplateParamType.FINAL_REPORT.getCode()
                            , FinalReportParam.GREATEST_MEDIUM_LOSS.getCode(), String.valueOf(BaseEnum.NO.getCode()));
                searchAndSetParam(replaceMap, GradeTemplateParamType.FINAL_REPORT.getCode()
                            , FinalReportParam.MEDIUM.getCode(), String.valueOf(BaseEnum.NO.getCode()));
            } else {
                searchAndSetParam(replaceMap, GradeTemplateParamType.FINAL_REPORT.getCode()
                            , FinalReportParam.GREATEST_MEDIUM_LOSS.getCode(), String.valueOf(BaseEnum.OTHER.getCode()));
                searchAndSetParam(replaceMap, GradeTemplateParamType.FINAL_REPORT.getCode()
                            , FinalReportParam.MEDIUM.getCode(), String.valueOf(BaseEnum.OTHER.getCode()));
            }
        }
    }

    private void setGreatestLossDesc(HashMap<String, Object> replaceMap, List<GridCalculationValueVO> calculationValueList) {
        Double totalPi = .0;
        Double totalBi = .0;
        Double totalEsv = .0;
        Double totalCost = .0;
        for (GridCalculationValueVO vo: calculationValueList) {
            totalPi += (vo.getLossCr() + vo.getLossHi());
            totalBi += vo.getBi();
            totalEsv += vo.getEsv();
            totalCost += vo.getCost();
        }
        List<Double> valueList = new ArrayList<>();
        valueList.add(totalPi);
        valueList.add(totalBi);
        valueList.add(totalEsv);
        valueList.add(totalCost);
        Collections.sort(valueList);
        Double maxValue = valueList.get(valueList.size() - 1);
        StringBuilder builder = new StringBuilder();
        if (totalCost == maxValue) {
            builder.append(COST_DESC);
        }
        if (totalBi == maxValue) {
            builder.append(BI_DESC);
        }
        if (totalEsv == maxValue) {
            builder.append(ESV_DESC);
        }
        if (totalPi == maxValue) {
            builder.append(PI_DESC);
        }
        replaceMap.put(FinalReportParam.GREATEST_LOSS.getCode()
                , builder.length() == 0? "": builder.substring(0, builder.length() - 1));
    }

    private void setDepthAssessment(String enterpriseId, HashMap<String, Object> replaceMap) {
        List<EntAssessResult> entAssessResultList = entAssessResultMapper
                .selectList(new QueryWrapper<EntAssessResult>()
                        .eq("ent_id", enterpriseId)
                        .eq("valid", BaseEnum.VALID_YES.getCode()));
        if (CollectionUtils.isEmpty(entAssessResultList)) {
            throw new BusinessException("do not finish simulation of model!");
        }
        EntAssessResult entAssessResult = entAssessResultList.get(0);
        replaceMap.put(FinalReportParam.COST.getCode(), String.valueOf(entAssessResult.getCost()));
        replaceMap.put(FinalReportParam.ESV.getCode(), String.valueOf(entAssessResult.getEsv()));
        replaceMap.put(FinalReportParam.BI.getCode(), String.valueOf(entAssessResult.getBi()));
        replaceMap.put(FinalReportParam.PI.getCode(), String.valueOf(entAssessResult.getPi()));
        setLossChartParam(replaceMap, entAssessResult);
    }

    private void setLossChartParam(HashMap<String, Object> replaceMap, EntAssessResult entAssessResult) {
        DefaultCategoryDataset dataSet=new DefaultCategoryDataset();
        dataSet.setValue(entAssessResult.getCost(),"财产损失","应急处置和清污费用");
        dataSet.setValue(entAssessResult.getEsv(),"财产损失","生态环境损害");
        dataSet.setValue(entAssessResult.getBi(),"财产损失","第三者财产损失");
        dataSet.setValue(entAssessResult.getPi(),"财产损失","第三者人身伤害");
        JFreeChart jFreeChart = ChartFactory
                .createBarChart("财产损失（万元）", "", "", dataSet
                        , PlotOrientation.HORIZONTAL, false, false, false);
        BarRenderer barRenderer = new CustomBarRenderer();
        barRenderer.setBarPainter(new StandardBarPainter());
        barRenderer.setMaximumBarWidth(0.4);
        barRenderer.setShadowVisible(false);
        CategoryPlot plot = jFreeChart.getCategoryPlot();// 获得图表区域对象
        plot.setRangeGridlinesVisible(false);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRenderer(barRenderer);
        plot.setOutlineVisible(false);
        CategoryAxis x = plot.getDomainAxis();
        TextTitle textTitle = jFreeChart.getTitle();
        textTitle.setFont(new Font("宋体", Font.PLAIN, 16));
        x.setTickLabelFont(new Font("sans-serif", Font.PLAIN, 12));
        String tempCharPath = filePathConfig.destPath + StringUtil.getUUID() + ".png";
        try {
            ChartUtilities.saveChartAsJPEG(new File(tempCharPath), jFreeChart, 400, 300);
        } catch (IOException e) {
            e.printStackTrace();
        }
        replaceMap.put(FinalReportParam.LOSS_CHART.getCode(), Arrays.asList(new String[]{tempCharPath}));
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
        targetCodes.add(TargetEnum.R_U.getCode());
        EntRiskAssessResultQueryParam queryParam = new EntRiskAssessResultQueryParam();
        queryParam.setEnterpriseId(enterpriseId);
        queryParam.setTargetCodeList(targetCodes);
        return entRiskAssessResultMapper.listEntRiskAssessResultLevel(queryParam);
    }

    private String buildFileName(String preStr) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmm");
        return preStr + "_" + simpleDateFormat.format(new Date());
    }

    private void deleteFiles(String enterpriseId, String sourceType) {
        BaseFileupload baseFileupload = new BaseFileupload();
        baseFileupload.setUpdateTime(DateUtil.getNowTimestamp());
        baseFileupload.setValid((Integer) BaseEnum.VALID_NO.getCode());
        baseFileuploadMapper.update(baseFileupload, new QueryWrapper<BaseFileupload>()
                .eq("source_id", enterpriseId)
                .eq("stype", sourceType)
                .eq("valid", BaseEnum.VALID_YES.getCode()));
    }


    private String insertFile(String enterpriseId, String fileName, String sourceType, String titlePre) {
        BaseFileupload baseFileupload = new BaseFileupload();
        baseFileupload.setId(StringUtil.getUUID());
        baseFileupload.setSourceId(enterpriseId);
        baseFileupload.setFileDesc(fileName);
        String[] strArray = fileName.split("\\.");
        baseFileupload.setFileName(titlePre + "." + strArray[1]);
        baseFileupload.setStype(sourceType);
        String fileAddress = filePathConfig.destPath + fileName;
        baseFileupload.setFileAdress(fileAddress);
        baseFileupload.setCreateTime(DateUtil.getNowTimestamp());
        baseFileupload.setValid((Integer) BaseEnum.VALID_YES.getCode());
        baseFileuploadMapper.insert(baseFileupload);
        return baseFileupload.getId();
    }

    private void setSomeParams(String enterpriseId, HashMap<String, Object> replaceMap, HashMap<String, Integer> needSpecialHandleMap) {
        List<GradeTemplateParamVO> paramList = gradeTemplateParamMapper.listParamOfSceneSurvey(enterpriseId);
        for (GradeTemplateParamVO param: paramList) {
            if (CHAIN_EFFECTIVE.equals(param.getCode())) {
                if (YES.equals(param.getResultCode())) {
                    needSpecialHandleMap.put(CHAIN_EFFECTIVE, 1);
                }
            } else if (RISK_LEVEL_LIST.contains(param.getCode())) {
                if (!YES.equals(param.getResultCode())) {
                    addCount(needSpecialHandleMap, RISK_LEVEL_FLAG);
                }
            } else if (CONTROL_RISK_LEVEL_LIST.contains(param.getCode())) {
                if (!YES.equals(param.getResultCode())) {
                    addCount(needSpecialHandleMap, CONTROL_RISK_LEVEL_FLAG);
                }
            } else if (RECEPTOR_RISK_LEVEL_LIST.contains(param.getCode())) {
                if (YES.equals(param.getResultCode())) {
                    addCount(needSpecialHandleMap, RECEPTOR_RISK_LEVEL_FLAG);
                }
            }
            replaceMap.put(param.getParamCode(), param.getParamContent());
        }
    }

    private void setPictures(HashMap<String, Object> replaceMap, EntSurveyExtendInfoVO infoVO) {
        if (CollectionUtils.isNotEmpty(infoVO.getPhotoList())) {
            replaceMap.put(FinalReportParam.SURVEY_ATTACHMENT.getCode(), infoVO.getPhotoList());
        } else {
            replaceMap.put(FinalReportParam.SURVEY_ATTACHMENT.getCode(), "");
        }
    }

    private void setSuggestions(HashMap<String, Object> replaceMap, EntSurveyExtendInfoVO infoVO) {
        replaceMap.put(FinalReportParam.TECHNIQUE_STEP_SUGGESTION.getCode(), infoVO.getTechniqueStepSuggestion());
        replaceMap.put(FinalReportParam.PIPELINE_STEP_SUGGESTION.getCode(), infoVO.getPipelineStepSuggestion());
        replaceMap.put(FinalReportParam.DANGER_STEP_SUGGESTION.getCode(), infoVO.getDangerStepSuggestion());
        replaceMap.put(FinalReportParam.HANDLING_PROCESS_SUGGESTION.getCode(), infoVO.getHandlingProcessSuggestion());
        replaceMap.put(FinalReportParam.EMERGENCY_FAC_SUGGESTION.getCode(), infoVO.getEmergencyFacSuggestion());
    }

    private void setRiskJudgement(HashMap<String, Object> replaceMap, HashMap<String, Integer> needSpecialHandleMap, EntSurveyExtendInfoVO infoVO) {
        if (needSpecialHandleMap.containsKey(CHAIN_EFFECTIVE)
                && needSpecialHandleMap.get(CHAIN_EFFECTIVE) > 0) {
            replaceMap.put(FinalReportParam.CHAIN_EFFECTIVE.getCode(), EXIST);
            replaceMap.put(FinalReportParam.CHAIN_EFFECTIVE_REASON.getCode(), "");
        } else {
            if (YES.equals(infoVO.getChainEffective())) {
                replaceMap.put(FinalReportParam.CHAIN_EFFECTIVE.getCode(), EXIST);
                replaceMap.put(FinalReportParam.CHAIN_EFFECTIVE_REASON.getCode(), BUT + infoVO.getChainEffectiveReason());
            } else {
                replaceMap.put(FinalReportParam.CHAIN_EFFECTIVE.getCode(), NOT_EXIST);
                replaceMap.put(FinalReportParam.CHAIN_EFFECTIVE_REASON.getCode(), "");
            }
        }
        if (needSpecialHandleMap.containsKey(RISK_LEVEL_FLAG)
                && needSpecialHandleMap.get(RISK_LEVEL_FLAG) < LOW_RISK_LEVEL_LIMIT) {
            replaceMap.put(FinalReportParam.COMMON_RISK_LEVEL.getCode(), LOW_RISK);
            replaceMap.put(FinalReportParam.HIGH_RISK_LEVEL_REASON.getCode(), "");
        } else {
            if (RiskLevel.HIGH.getCode().equals(infoVO.getRiskLevel())) {
                replaceMap.put(FinalReportParam.COMMON_RISK_LEVEL.getCode(), HIGH_RISK);
                replaceMap.put(FinalReportParam.HIGH_RISK_LEVEL_REASON.getCode(), BUT + infoVO.getHighRiskLevelReason());
            } else {
                replaceMap.put(FinalReportParam.COMMON_RISK_LEVEL.getCode(), LOW_RISK);
                replaceMap.put(FinalReportParam.HIGH_RISK_LEVEL_REASON.getCode(), "");
            }
        }
        if (needSpecialHandleMap.containsKey(CONTROL_RISK_LEVEL_FLAG)
                && needSpecialHandleMap.get(CONTROL_RISK_LEVEL_FLAG) < LOW_CONTROL_RISK_LEVEL_LIMIT) {
            replaceMap.put(FinalReportParam.CONTROL_RISK_LEVEL.getCode(), LOW_RISK);
            replaceMap.put(FinalReportParam.HIGH_CONTROL_RISK_LEVEL_REASON.getCode(), "");
        } else {
            if (RiskLevel.HIGH.getCode().equals(infoVO.getControlRiskLevel())) {
                replaceMap.put(FinalReportParam.CONTROL_RISK_LEVEL.getCode(), HIGH_RISK);
                replaceMap.put(FinalReportParam.HIGH_CONTROL_RISK_LEVEL_REASON.getCode(), BUT + infoVO.getHighControlRiskLevelReason());
            } else {
                replaceMap.put(FinalReportParam.CONTROL_RISK_LEVEL.getCode(), LOW_RISK);
                replaceMap.put(FinalReportParam.HIGH_CONTROL_RISK_LEVEL_REASON.getCode(), "");
            }
        }
        if (needSpecialHandleMap.containsKey(RECEPTOR_RISK_LEVEL_FLAG)
                && needSpecialHandleMap.get(RECEPTOR_RISK_LEVEL_FLAG) > LOW_RECEPTOR_RISK_LEVEL_LIMIT) {
            replaceMap.put(FinalReportParam.RECEPTOR_RISK_LEVEL.getCode(), HIGH_RISK);
            replaceMap.put(FinalReportParam.HIGH_RECEPTOR_RISK_LEVEL_REASON.getCode(), "");
        } else {
            if (RiskLevel.HIGH.getCode().equals(infoVO.getReceptorRiskLevel())) {
                replaceMap.put(FinalReportParam.RECEPTOR_RISK_LEVEL.getCode(), HIGH_RISK);
                replaceMap.put(FinalReportParam.HIGH_RECEPTOR_RISK_LEVEL_REASON.getCode(), BUT + infoVO.getHighReceptorRiskLevel());
            } else {
                replaceMap.put(FinalReportParam.RECEPTOR_RISK_LEVEL.getCode(), LOW_RISK);
                replaceMap.put(FinalReportParam.HIGH_RECEPTOR_RISK_LEVEL_REASON.getCode(), "");
            }
        }
    }

    private EntSurveyExtendInfoVO getEntSurveyExtendInfo(String enterpriseId) {
        List<EntSurveyExtendInfoVO> extendInfoList = entSurveyExtendInfoMapper.listEntSurveyExtendInfo(enterpriseId);
        if (CollectionUtils.isEmpty(extendInfoList)) {
            throw new BusinessException("please complete the extend survey info");
        }
        return extendInfoList.get(0);
    }

    private void addCount(HashMap<String, Integer> needSpecialHandleMap, String receptorRiskLevelFlag) {
        if (!needSpecialHandleMap.containsKey(receptorRiskLevelFlag)) {
            needSpecialHandleMap.put(receptorRiskLevelFlag, 0);
        }
        int total = needSpecialHandleMap.get(receptorRiskLevelFlag);
        total++;
        needSpecialHandleMap.put(receptorRiskLevelFlag, total);
    }

    private void setIntensiveParam(String enterpriseId, HashMap<String, Object> replaceMap
            , HashMap<String, Integer> needSpecialHandleMap) {
        List<SurveyResultVO> componentResultList = entSurveyPlanMapper.listResultOfIntensiveParamsByRow(enterpriseId);
        boolean hasUndergroundStorage = false;
        boolean hasUndergroundPipe = false;
        boolean unloadSpecify = false;
        boolean existOneCondition = false;
        boolean existAllCondition = false;
        if (CollectionUtils.isNotEmpty(componentResultList)) {
            for (SurveyResultVO vo: componentResultList) {
                if (!hasUndergroundStorage && COMPONENT_LOCATION_VALID.equals(vo.getComponentLocation())
                        && COMPONENT_TYPE_OF_STORAGE_LIST.contains(vo.getComponentType())) {
                    searchAndSetParam(replaceMap, GradeTemplateParamType.FINAL_REPORT.getCode()
                            , FinalReportParam.EXIST_STORAGE.getCode(), String.valueOf(BaseEnum.YES.getCode()));
                    hasUndergroundStorage = true;
                } else if (!hasUndergroundPipe && COMPONENT_LOCATION_VALID.equals(vo.getComponentLocation())
                        && COMPONENT_TYPE_OF_PIPE_LIST.contains(vo.getComponentType())) {
                    searchAndSetParam(replaceMap, GradeTemplateParamType.FINAL_REPORT.getCode()
                            , FinalReportParam.EXIST_PIPE.getCode(), String.valueOf(BaseEnum.YES.getCode()));
                    hasUndergroundPipe = true;
                } else if (!unloadSpecify && OPTION_VALUE_YES.equals(vo.getWasteCollection())
                        && OPTION_VALUE_YES.equals(vo.getWastePrevent())) {
                    searchAndSetParam(replaceMap, GradeTemplateParamType.FINAL_REPORT.getCode()
                            , FinalReportParam.UNLOAD_SPECIFY.getCode(), String.valueOf(BaseEnum.YES.getCode()));
                    unloadSpecify = true;
                } else if (!existAllCondition && !existOneCondition
                        && (OPTION_VALUE_YES.equals(vo.getHasFireAlarmSystem())
                            || OPTION_VALUE_YES.equals(vo.getHasOutFireSystem()))) {
                    searchAndSetParam(replaceMap, GradeTemplateParamType.FINAL_REPORT.getCode()
                            , FinalReportParam.HAS_FIRE_DEVICE.getCode(), String.valueOf(BaseEnum.NO.getCode()));
                    addCount(needSpecialHandleMap, CONTROL_RISK_LEVEL_FLAG);
                    existOneCondition = true;
                } else if (!existAllCondition && OPTION_VALUE_YES.equals(vo.getHasFireAlarmSystem())
                        && OPTION_VALUE_YES.equals(vo.getHasOutFireSystem())) {
                    searchAndSetParam(replaceMap, GradeTemplateParamType.FINAL_REPORT.getCode()
                            , FinalReportParam.HAS_FIRE_DEVICE.getCode(), String.valueOf(BaseEnum.YES.getCode()));
                    existAllCondition = true;
                }
            }
        }
        if (!hasUndergroundStorage) {
            searchAndSetParam(replaceMap, GradeTemplateParamType.FINAL_REPORT.getCode()
                    , FinalReportParam.EXIST_STORAGE.getCode(), String.valueOf(BaseEnum.NO.getCode()));
            addCount(needSpecialHandleMap, RISK_LEVEL_FLAG);
        }
        if (!hasUndergroundPipe) {
            searchAndSetParam(replaceMap, GradeTemplateParamType.FINAL_REPORT.getCode()
                    , FinalReportParam.EXIST_PIPE.getCode(), String.valueOf(BaseEnum.NO.getCode()));
            addCount(needSpecialHandleMap, RISK_LEVEL_FLAG);
        }
        if (!existOneCondition && !existOneCondition) {
            searchAndSetParam(replaceMap, GradeTemplateParamType.FINAL_REPORT.getCode()
                    , FinalReportParam.HAS_FIRE_DEVICE.getCode(), String.valueOf(BaseEnum.OTHER.getCode()));
            addCount(needSpecialHandleMap, CONTROL_RISK_LEVEL_FLAG);
        }
        if (!unloadSpecify) {
            searchAndSetParam(replaceMap, GradeTemplateParamType.FINAL_REPORT.getCode()
                    , FinalReportParam.UNLOAD_SPECIFY.getCode(), String.valueOf(BaseEnum.NO.getCode()));
            addCount(needSpecialHandleMap, CONTROL_RISK_LEVEL_FLAG);
        }
    }

    private void setBasicParam(String enterpriseId, HashMap<String, Object> replaceMap) {
        List<SurveyResultVO> componentResultList = entSurveyPlanMapper.listResultOfBasicComponentByRow(enterpriseId);
        boolean hasUndergroundStorage = false;
        boolean hasUndergroundPipe = false;
        if (CollectionUtils.isNotEmpty(componentResultList)) {
            for (SurveyResultVO vo: componentResultList) {
                if (!hasUndergroundStorage && COMPONENT_LOCATION_VALID.equals(vo.getComponentLocation())
                        && COMPONENT_TYPE_OF_STORAGE_LIST.contains(vo.getComponentType())) {
                    searchAndSetParam(replaceMap, GradeTemplateParamType.FINAL_REPORT.getCode()
                            , FinalReportParam.EXIST_STORAGE.getCode(), String.valueOf(BaseEnum.YES.getCode()));
                    hasUndergroundStorage = true;
                }
                if (!hasUndergroundPipe && COMPONENT_LOCATION_VALID.equals(vo.getComponentLocation())
                        && COMPONENT_TYPE_OF_PIPE_LIST.contains(vo.getComponentType())) {
                    searchAndSetParam(replaceMap, GradeTemplateParamType.FINAL_REPORT.getCode()
                            , FinalReportParam.EXIST_PIPE.getCode(), String.valueOf(BaseEnum.YES.getCode()));
                    hasUndergroundPipe = true;
                }
            }

        }
        if (!hasUndergroundStorage) {
            searchAndSetParam(replaceMap, GradeTemplateParamType.FINAL_REPORT.getCode()
                    , FinalReportParam.EXIST_STORAGE.getCode(), String.valueOf(BaseEnum.NO.getCode()));
        }
        if (!hasUndergroundPipe) {
            searchAndSetParam(replaceMap, GradeTemplateParamType.FINAL_REPORT.getCode()
                    , FinalReportParam.EXIST_PIPE.getCode(), String.valueOf(BaseEnum.NO.getCode()));
        }
    }

    private void searchAndSetParam(HashMap<String, Object> replaceMap, String paramType, String paramCode, String resultCode) {
        List<GradeTemplateParam> params = gradeTemplateParamMapper.selectList(new QueryWrapper<GradeTemplateParam>()
                .eq("type", paramType)
                .eq("param_code", paramCode)
                .eq("result_code", resultCode)
                .eq("valid", BaseEnum.VALID_YES.getCode()));
        if (CollectionUtils.isEmpty(params)) {
            throw new BusinessException("lack of param data");
        }
        replaceMap.put(paramCode, params.get(0).getParamContent());
    }

    private void setAssessmentProgressInfo(String enterpriseId, HashMap<String, Object> replaceMap
            , int totalRiskType, EntSurveyExtendInfoVO infoVO) {
        List<GradeTemplateParamVO> paramList = gradeTemplateParamMapper.listParamOfFinalReport(enterpriseId);
        for (int i = 0; i < paramList.size(); i++) {
            GradeTemplateParamVO param = paramList.get(i);
            if (FinalReportParam.SURVEY_INFO.getCode()
                    .equals(param.getParamCode())
                    && StringUtil.isNotBlank(param.getParamContent())) {
                String content = param.getParamContent();
                content = content.replace(FinalReportParam.SURVEY_TIME.getCode(), DateUtil.formatDateToStr(infoVO.getSurveyTime(), "yyyy-MM-dd"));
                if (totalRiskType > 1) {
                    content = content.replace(FinalReportParam.AND_GRADUAL_RISK.getCode(), "和渐进环境风险");
                } else {
                    content = content.replace(FinalReportParam.AND_GRADUAL_RISK.getCode(), "");
                }
                int totalRiskCount = accidentScenarioMapper.countTotalAccidentScenarioByEnterpriseId(enterpriseId);
                content = content.replace(FinalReportParam.TOTAL_RISK.getCode(), totalRiskCount + "");
                List<AccidentScenarioVO> scenarioList
                        = accidentScenarioMapper.listAccidentScenarioOfEnterprise(enterpriseId);
                if (CollectionUtils.isNotEmpty(scenarioList)) {
                    content = content.replace(FinalReportParam.TOTAL_RISK_TYPE.getCode(), scenarioList.size() + "");
                    StringBuilder builder = new StringBuilder();
                    for (AccidentScenarioVO accidentScenarioVO: scenarioList) {
                        builder.append(accidentScenarioVO.getName());
                        builder.append("、");
                    }
                    if (builder.length() > 0) {
                        content = content.replace(FinalReportParam.SCENARIO.getCode(), builder.toString().substring(0, builder.length() - 1));
                    } else {
                        content = content.replace(FinalReportParam.SCENARIO.getCode(), "");
                    }
                } else {
                    content = content.replace(FinalReportParam.TOTAL_RISK_TYPE.getCode(), "0");
                    content = content.replace(FinalReportParam.SCENARIO.getCode(), "");
                }
                List<EntDiffusionModelInfoVO> infoList = listDiffusionModelInfo(enterpriseId);
                List<String> waterModelNameList = new ArrayList<>();
                int totalModel = 0;
                for (EntDiffusionModelInfoVO vo: infoList) {
                    totalModel++;
                    if (!waterModelNameList.contains(vo.getModelName())) {
                        waterModelNameList.add(vo.getModelName());
                    }
                }
                //todo 损害评估
                content = content.replace(FinalReportParam.DAMAGE_ASSESSMENT.getCode(), FinalReportParam.DAMAGE_ASSESSMENT.getDefaultValue());
                if (CollectionUtils.isNotEmpty(waterModelNameList)) {
                    content = content.replace(FinalReportParam.WATER_MODELS.getCode(), StringUtils.join(waterModelNameList.toArray(), "、"));
                    content = content.replace(FinalReportParam.WATER_SIMULATION.getCode(), FinalReportParam.WATER_SIMULATION.getDefaultValue());
                    content = content.replace(FinalReportParam.WATER_SIMULATION_TIMES.getCode(), String.valueOf(totalModel));
                    content = content.replace(FinalReportParam.SOIL_MODELS.getCode(), FinalReportParam.SOIL_MODELS.getDefaultValue());
                    content = content.replace(FinalReportParam.SOIL_SIMULATION.getCode(), FinalReportParam.SOIL_SIMULATION.getDefaultValue());
                    content = content.replace(FinalReportParam.SOIL_SIMULATION_TIMES.getCode(), String.valueOf(totalModel));
                    replaceMap.put(FinalReportParam.HAS_SIMULATION.getCode(), FinalReportParam.HAS_SIMULATION.getDefaultValue());
                } else {
                    content = content.replace(FinalReportParam.WATER_MODELS.getCode(), "");
                    content = content.replace(FinalReportParam.WATER_SIMULATION.getCode(), "");
                    content.replace(FinalReportParam.SOIL_MODELS.getCode(), "");
                    content = content.replace(FinalReportParam.SOIL_SIMULATION.getCode(), "");
                    replaceMap.put(FinalReportParam.HAS_SIMULATION.getCode(), "");
                }
                replaceMap.put(param.getParamCode(), content);
            } else {
                replaceMap.put(param.getParamCode(), param.getParamContent());
            }
        }
    }

    private List<EntDiffusionModelInfoVO> listDiffusionModelInfo(String enterpriseId) {
        EntDiffusionModelInfoQueryParam queryParam = new EntDiffusionModelInfoQueryParam();
        queryParam.setEnterpriseId(enterpriseId);
        return entDiffusionModelInfoMapper.listModelOfInfo(queryParam);
    }

    private int getTotalType(String enterpriseId) {
        int totalType = entMappingTargetTypeMapper.selectCount(new QueryWrapper<EntMappingTargetType>()
                .eq("ent_id", enterpriseId)
                .eq("valid", BaseEnum.VALID_YES.getCode()));
        return totalType;
    }

    private void setEnterpriseInfoParams(EnterpriseInfo enterpriseInfo, HashMap<String, Object> replaceMap) {
        replaceMap.put(FinalReportParam.ENTERPRISE_NAME.getCode(), StringUtil.isNotBlank(enterpriseInfo.getName())? enterpriseInfo.getName(): "");
        replaceMap.put(FinalReportParam.SOCIAL_CREDIT_CODE.getCode(), StringUtil.isNotBlank(enterpriseInfo.getSocialCreditCode())? enterpriseInfo.getSocialCreditCode(): "");
        replaceMap.put(FinalReportParam.ADDRESS.getCode(), StringUtil.isNotBlank(enterpriseInfo.getAddress())? enterpriseInfo.getAddress(): "");
        replaceMap.put(FinalReportParam.CONTACTS.getCode(), StringUtil.isNotBlank(enterpriseInfo.getContacts())? enterpriseInfo.getContacts(): "");
        replaceMap.put(FinalReportParam.LINK_PHONE.getCode(), StringUtil.isNotBlank(enterpriseInfo.getLinkPhone())? enterpriseInfo.getLinkPhone(): "");
        if (StringUtil.isNotBlank(enterpriseInfo.getIndustryId())) {
            IndustryCategoryClassifyVO categoryClassifyVO = industryCategoryMapper.getClassificationById(enterpriseInfo.getIndustryId());
            String categoryCode = categoryClassifyVO.getCategoryCode();
            if (categoryCode.length() > 2) {
                StringBuilder builder = new StringBuilder();
                builder.append(categoryClassifyVO.getLevelTwoName()).append("————")
                        .append(categoryClassifyVO.getLevelThreeName());
                if (categoryCode.length() == 4) {
                    builder.append("————").append(categoryClassifyVO.getLevelFourName());
                }
                builder.append("(" + categoryClassifyVO.getCategoryCode() + ")");
                replaceMap.put(FinalReportParam.INDUSTRY.getCode(), builder.toString());
            } else {
                replaceMap.put(FinalReportParam.INDUSTRY.getCode(), "");
            }
        } else {
            replaceMap.put(FinalReportParam.INDUSTRY.getCode(), "");
        }
        replaceMap.put(FinalReportParam.EMPLOYEES_NUMBER.getCode(), ObjectUtil.isNotNull(enterpriseInfo.getEmployeesNumber())? String.valueOf(enterpriseInfo.getEmployeesNumber()): "--");
        replaceMap.put(FinalReportParam.INCOME.getCode(), ObjectUtil.isNotNull(enterpriseInfo.getIncome())? String.valueOf(enterpriseInfo.getIncome()): "--");
    }

    private EnterpriseInfo getEnterpriseInfo(String enterpriseId) {
        EnterpriseInfo enterpriseInfo = enterpriseInfoMapper.selectById(enterpriseId);
        if (ObjectUtil.isNull(enterpriseInfo)) {
            throw new BusinessException("enterprise is not exist");
        }
        return enterpriseInfo;
    }

    private static void replaceWords(Map<String, Object> replaceMap, CustomXWPFDocument doc) {
        Iterator itPara = doc.getParagraphsIterator();
        while (itPara.hasNext()) {
            XWPFParagraph paragraph = (XWPFParagraph) itPara.next();
            Set<String> set = replaceMap.keySet();
            Iterator iterator = set.iterator();
            paragraph: while (iterator.hasNext()) {
                String key = (String) iterator.next();
                List<XWPFRun> run = paragraph.getRuns();
                for (int i = 0; i < run.size(); i++) {
                    if (run.get(i).getText(run.get(i).getTextPosition()) != null
                            && run.get(i).getText(run.get(i).getTextPosition()).equals(key)) {
                        if (FinalReportParam.SURVEY_ATTACHMENT.getCode().equals(key)) {
                            setAttachmentToDoc(replaceMap, doc, paragraph, key, run, i);
                            break paragraph;
                        } else if (FinalReportParam.LOSS_CHART.getCode().equals(key)) {
                            setPicturesToDoc(replaceMap, doc, paragraph, key, run, i);
                            break paragraph;
                        } else {
                            String text = (String) replaceMap.get(key);
                            run.get(i).setText(text, 0);
                        }
                    }
                }
            }
        }
    }

    private static void setAttachmentToDoc(Map<String, Object> replaceMap, CustomXWPFDocument doc, XWPFParagraph paragraph, String key, List<XWPFRun> run, int i) {
        if (ObjectUtil.isNotNull(replaceMap.get(key))) {
            List<EntSurveyPhotoVO> picList = (List<EntSurveyPhotoVO>) replaceMap.get(key);
            run.get(i).setText("", 0);
            InputStream is = null;
            try {
                for (int k = 0; k < picList.size(); k++) {
                    EntSurveyPhotoVO photo = picList.get(k);
                    is = new FileInputStream(new File(photo.getFileAddress()));
                    addPictureDataToDoc(doc, is, photo.getFileAddress());
                    doc.createPicture(paragraph,doc.getAllPictures().size()-1, 400, 400,"");
                    paragraph.createRun().addCarriageReturn();
                    XWPFRun itemRun = paragraph.createRun();
                    itemRun.setText("图"+ (k + 1) +" 现场查勘时拍摄");
                    itemRun.addCarriageReturn();
                    XWPFRun phoneTime = paragraph.createRun();
                    phoneTime.setText(DateUtil.formatDateToStr(photo.getTimeOfPhoto()
                            , "yyyy-MM-dd HH:mm:ss     "));
                    XWPFRun phoneAddress = paragraph.createRun();
                    phoneAddress.setText(photo.getLocation());
                    paragraph.createRun().addCarriageReturn();
                    XWPFRun remark = paragraph.createRun();
                    remark.setText("说明：" + photo.getRemark());
                    paragraph.createRun().addCarriageReturn();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (InvalidFormatException e) {
                e.printStackTrace();
            }
        } else {
            run.get(i).setText("", 0);
        }
    }

    private static void setPicturesToDoc(Map<String, Object> replaceMap, CustomXWPFDocument doc, XWPFParagraph paragraph, String key, List<XWPFRun> run, int i) {
        if (ObjectUtil.isNotNull(replaceMap.get(key))) {
            List<String> pathList = (List<String>) replaceMap.get(key);
            run.get(i).setText("", 0);
            InputStream is = null;
            try {
                for (int k = 0; k < pathList.size(); k++) {
                    String path = pathList.get(k);
                    is = new FileInputStream(new File(path));
                    addPictureDataToDoc(doc, is, path);
                    doc.createPicture(paragraph,doc.getAllPictures().size()-1, 300, 220,"");
                    paragraph.createRun().addCarriageReturn();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (InvalidFormatException e) {
                e.printStackTrace();
            }
        } else {
            run.get(i).setText("", 0);
        }
    }

    private static void addPictureDataToDoc(CustomXWPFDocument doc, InputStream is, String address) throws InvalidFormatException {
        String[] strArray = address.split("\\.");
        String suffixStr = strArray[strArray.length - 1];
        if ("png".equalsIgnoreCase(suffixStr)) {
            doc.addPictureData(is, XWPFDocument.PICTURE_TYPE_PNG);
        } else if ("jpg".equalsIgnoreCase(suffixStr) || "jpeg".equalsIgnoreCase(suffixStr)) {
            doc.addPictureData(is, XWPFDocument.PICTURE_TYPE_JPEG);
        } else if ("gif".equalsIgnoreCase(suffixStr)) {
            doc.addPictureData(is, XWPFDocument.PICTURE_TYPE_GIF);
        } else if ("bmp".equalsIgnoreCase(suffixStr)) {
            doc.addPictureData(is, XWPFDocument.PICTURE_TYPE_BMP);
        }
    }
}
