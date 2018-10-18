package com.shencai.eil.survey.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shencai.eil.common.constants.BaseEnum;
import com.shencai.eil.common.constants.GradeTemplateParamType;
import com.shencai.eil.common.constants.RiskLevel;
import com.shencai.eil.common.utils.EilFileUtil;
import com.shencai.eil.common.utils.StringUtil;
import com.shencai.eil.exception.BusinessException;
import com.shencai.eil.grading.entity.EntMappingTargetType;
import com.shencai.eil.grading.mapper.EntMappingTargetTypeMapper;
import com.shencai.eil.policy.entity.EnterpriseInfo;
import com.shencai.eil.policy.mapper.EnterpriseInfoMapper;
import com.shencai.eil.policy.mapper.IndustryCategoryMapper;
import com.shencai.eil.scenario.mapper.AccidentScenarioMapper;
import com.shencai.eil.scenario.model.AccidentScenarioVO;
import com.shencai.eil.survey.constants.FinalReportParam;
import com.shencai.eil.survey.entity.GradeTemplateParam;
import com.shencai.eil.survey.mapper.EntSurveyPlanMapper;
import com.shencai.eil.survey.mapper.GradeTemplateParamMapper;
import com.shencai.eil.survey.model.GradeTemplateParamVO;
import com.shencai.eil.survey.model.IndustryCategoryClassifyVO;
import com.shencai.eil.survey.model.SurveyResultVO;
import com.shencai.eil.survey.service.IFinalReportService;
import com.shencai.eil.system.model.CustomXWPFDocument;
import com.shencai.eil.system.model.FilePathConfig;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.*;

/**
 * Created by fanhj on 2018/10/16.
 */
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
    private FilePathConfig filePathConfig;

    private static final String COMPONENT_LOCATION_VALID = "地下";
    private static final List<String> COMPONENT_TYPE_OF_STORAGE_LIST
            = Arrays.asList(new String[]{"反应釜、工艺储罐、气体储罐、塔器", "常压单包容储罐", "常压储罐"});
    private static final List<String> COMPONENT_TYPE_OF_PIPE_LIST
            = Arrays.asList(new String[]{"内径≤75mm管道", "75mm＜内径≤150mm管道", "内径＞150管道"});
    private static final String OPTION_VALUE_YES = "是";

    @Override
    public String getFinalReport(String enterpriseId) {
        HashMap<String, Object> replaceMap = new HashMap<>();
        EnterpriseInfo enterpriseInfo = getEnterpriseInfo(enterpriseId);
        int totalRiskType = getTotalType(enterpriseId);
        setEnterpriseInfoParams(enterpriseInfo, replaceMap);
        setAssessmentProgressInfo(enterpriseId, replaceMap, totalRiskType);
        if (RiskLevel.HIGH.getCode().equals(enterpriseInfo.getRiskLevel())) {
            List<GradeTemplateParamVO> paramList = gradeTemplateParamMapper.listParamOfSceneSurvey(enterpriseId);
            for (GradeTemplateParamVO param: paramList) {
                replaceMap.put(param.getParamCode(), param.getParamContent());
            }
            setIntensiveParam(enterpriseId, replaceMap);
            //todo 现场图片查询后插入
        } else {
            List<GradeTemplateParamVO> paramList = gradeTemplateParamMapper.listParamWithoutSceneSurvey(enterpriseId);
            for (GradeTemplateParamVO param: paramList) {
                replaceMap.put(param.getParamCode(), param.getParamContent());
            }
            setBasicParam(enterpriseId, replaceMap);
        }

        return "";
    }

    private void setIntensiveParam(String enterpriseId, HashMap<String, Object> replaceMap) {
        List<SurveyResultVO> componentResultList = entSurveyPlanMapper.listResultOfIntensiveParamsByRow(enterpriseId);
        boolean hasUndergroundStorage = false;
        boolean hasUndergroundPipe = false;
        boolean unloadSpecify = false;
        if (CollectionUtils.isNotEmpty(componentResultList)) {
            for (SurveyResultVO vo: componentResultList) {
                if (!hasUndergroundStorage && COMPONENT_LOCATION_VALID.equals(vo.getComponentLocation())
                        && COMPONENT_TYPE_OF_STORAGE_LIST.contains(vo.getComponentType())) {
                    searchAndSetParam(replaceMap, GradeTemplateParamType.FINAL_REPORT.getCode()
                            , FinalReportParam.EXIST_STORAGE.getCode(), (String) BaseEnum.YES.getCode());
                    hasUndergroundStorage = true;
                }
                if (!hasUndergroundPipe && COMPONENT_LOCATION_VALID.equals(vo.getComponentLocation())
                        && COMPONENT_TYPE_OF_PIPE_LIST.contains(vo.getComponentType())) {
                    searchAndSetParam(replaceMap, GradeTemplateParamType.FINAL_REPORT.getCode()
                            , FinalReportParam.EXIST_PIPE.getCode(), (String) BaseEnum.YES.getCode());
                    hasUndergroundPipe = true;
                }
                if (!unloadSpecify && OPTION_VALUE_YES.equals(vo.getWasteCollection())
                        && OPTION_VALUE_YES.equals(vo.getWastePrevent())) {
                    searchAndSetParam(replaceMap, GradeTemplateParamType.FINAL_REPORT.getCode()
                            , FinalReportParam.UNLOAD_SPECIFY.getCode(), (String) BaseEnum.YES.getCode());
                    unloadSpecify = true;
                }
            }

        }
        if (!hasUndergroundStorage) {
            searchAndSetParam(replaceMap, GradeTemplateParamType.FINAL_REPORT.getCode()
                    , FinalReportParam.EXIST_STORAGE.getCode(), (String) BaseEnum.NO.getCode());
        }
        if (!hasUndergroundPipe) {
            searchAndSetParam(replaceMap, GradeTemplateParamType.FINAL_REPORT.getCode()
                    , FinalReportParam.EXIST_PIPE.getCode(), (String) BaseEnum.NO.getCode());
        }
        if (!unloadSpecify) {
            searchAndSetParam(replaceMap, GradeTemplateParamType.FINAL_REPORT.getCode()
                    , FinalReportParam.UNLOAD_SPECIFY.getCode(), (String) BaseEnum.NO.getCode());
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
                            , FinalReportParam.EXIST_STORAGE.getCode(), (String) BaseEnum.YES.getCode());
                    hasUndergroundStorage = true;
                }
                if (!hasUndergroundPipe && COMPONENT_LOCATION_VALID.equals(vo.getComponentLocation())
                        && COMPONENT_TYPE_OF_PIPE_LIST.contains(vo.getComponentType())) {
                    searchAndSetParam(replaceMap, GradeTemplateParamType.FINAL_REPORT.getCode()
                            , FinalReportParam.EXIST_PIPE.getCode(), (String) BaseEnum.YES.getCode());
                    hasUndergroundPipe = true;
                }
            }

        }
        if (!hasUndergroundStorage) {
            searchAndSetParam(replaceMap, GradeTemplateParamType.FINAL_REPORT.getCode()
                    , FinalReportParam.EXIST_STORAGE.getCode(), (String) BaseEnum.NO.getCode());
        }
        if (!hasUndergroundPipe) {
            searchAndSetParam(replaceMap, GradeTemplateParamType.FINAL_REPORT.getCode()
                    , FinalReportParam.EXIST_PIPE.getCode(), (String) BaseEnum.NO.getCode());
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

    private void setAssessmentProgressInfo(String enterpriseId, HashMap<String, Object> replaceMap, int totalRiskType) {
        List<GradeTemplateParamVO> paramList = gradeTemplateParamMapper.listParamOfFinalReport(enterpriseId);
        for (int i = 0; i < paramList.size(); i++) {
            GradeTemplateParamVO param = paramList.get(i);
            if (FinalReportParam.SURVEY_INFO.getCode()
                    .equals(param.getParamCode())
                    && StringUtil.isNotBlank(param.getParamContent())) {
                String content = param.getParamContent();
                //todo 勘查日期，新加表
                if (totalRiskType > 1) {
                    content.replace(FinalReportParam.AND_GRADUAL_RISK.getCode(), "和渐进环境风险");
                } else {
                    content.replace(FinalReportParam.AND_GRADUAL_RISK.getCode(), "");
                }
                int totalRiskCount = accidentScenarioMapper.countTotalAccidentScenarioByEnterpriseId(enterpriseId);
                content.replace(FinalReportParam.TOTAL_RISK.getCode(), totalRiskCount + "");
                List<AccidentScenarioVO> scenarioList
                        = accidentScenarioMapper.listAccidentScenarioOfEnterprise(enterpriseId);
                if (CollectionUtils.isNotEmpty(scenarioList)) {
                    content.replace(FinalReportParam.TOTAL_RISK_TYPE.getCode(), scenarioList.size() + "");
                    StringBuilder builder = new StringBuilder();
                    for (AccidentScenarioVO accidentScenarioVO: scenarioList) {
                        builder.append(accidentScenarioVO.getName());
                        builder.append("、");
                    }
                    builder.substring(0, builder.length() - 1);
                    content.replace(FinalReportParam.SCENARIO.getCode(), builder.toString());
                } else {
                    content.replace(FinalReportParam.TOTAL_RISK_TYPE.getCode(), "0");
                    content.replace(FinalReportParam.SCENARIO.getCode(), "");
                }
                //todo 损害评估
                replaceMap.put(param.getParamCode(), content);
            } else {
                replaceMap.put(param.getParamCode(), param.getParamContent());
            }
        }
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
        replaceMap.put(FinalReportParam.EMPLOYEES_NUMBER.getCode(), ObjectUtil.isNotNull(enterpriseInfo.getEmployeesNumber())? enterpriseInfo.getEmployeesNumber(): "--");
        replaceMap.put(FinalReportParam.INCOME.getCode(), ObjectUtil.isNotNull(enterpriseInfo.getIncome())? enterpriseInfo.getIncome(): "--");
    }

    private EnterpriseInfo getEnterpriseInfo(String enterpriseId) {
        EnterpriseInfo enterpriseInfo = enterpriseInfoMapper.selectById(enterpriseId);
        if (ObjectUtil.isNull(enterpriseInfo)) {
            throw new BusinessException("enterprise is not exist");
        }
        return enterpriseInfo;
    }

    public static void main(String[] args) {
        String srcPath = "D:\\fanhj\\final_report_intensive.docx";
        try {
            CustomXWPFDocument doc = new CustomXWPFDocument(POIXMLDocument.openPackage(srcPath));
//            InputStream is = new FileInputStream(new File("D:\\fanhj\\zhexian.png"));
//            XWPFParagraph paragraph = doc.createParagraph();
//            doc.addPictureData(is, XWPFDocument.PICTURE_TYPE_PNG);
//            ((CustomXWPFDocument) doc).createPicture(doc.createParagraph(),doc.getAllPictures().size()-1, 400, 400,"");
            Map<String, Object> replaceMap = new HashMap<>();
            List<String> picList = Arrays.asList(new String[]{"Jellyfish.jpg", "Lighthouse.jpg", "Penguins.jpg", "zhexian.png"});
            replaceMap.put("${surveyAttachment}", picList);
            replaceWords(replaceMap, doc);
            OutputStream os = new FileOutputStream(new File("D:\\fanhj\\final_report1.docx"));
            doc.write(os);
            os.close();
//            is.close();
            EilFileUtil.word2pdf("D:\\fanhj\\final_report1.docx", "D:\\fanhj\\final_report1.pdf");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        catch (InvalidFormatException e) {
//            e.printStackTrace();
//        }
    }

    private static void replaceWords(Map<String, Object> replaceMap, CustomXWPFDocument doc) {
        Iterator itPara = doc.getParagraphsIterator();
        while (itPara.hasNext()) {
            XWPFParagraph paragraph = (XWPFParagraph) itPara.next();
            Set<String> set = replaceMap.keySet();
            Iterator iterator = set.iterator();
            ll: while (iterator.hasNext()) {
                String key = (String) iterator.next();
                List<XWPFRun> run = paragraph.getRuns();
                for (int i = 0; i < run.size(); i++) {
                    if (run.get(i).getText(run.get(i).getTextPosition()) != null
                            && run.get(i).getText(run.get(i).getTextPosition()).equals(key)) {
                        if ("${surveyAttachment}".equals(key)) {
                            List<String> picList = (List<String>) replaceMap.get(key);
                            run.get(i).setText("", 0);
                            InputStream is = null;
                            try {
                                for (int k = 0; k < picList.size(); k++) {
                                    is = new FileInputStream(new File("D:\\fanhj\\" + picList.get(k)));
                                    if (k < picList.size() - 1) {
                                        doc.addPictureData(is, XWPFDocument.PICTURE_TYPE_JPEG);
                                    } else {
                                        doc.addPictureData(is, XWPFDocument.PICTURE_TYPE_PNG);
                                    }
                                    doc.createPicture(paragraph,doc.getAllPictures().size()-1, 400, 400,"");
                                    paragraph.createRun().addCarriageReturn();
                                    XWPFRun itemRun = paragraph.createRun();
                                    itemRun.setText("图"+ (k + 1) +" 现场查勘时拍摄");
                                    itemRun.addCarriageReturn();
                                    XWPFRun phoneTime = paragraph.createRun();
                                    phoneTime.setText("拍摄地点：2018年10月11日" + k);
                                    XWPFRun phoneAddress = paragraph.createRun();
                                    phoneAddress.setText("      拍摄地点：企业厂房内" + k);
                                    paragraph.createRun().addCarriageReturn();
                                    XWPFRun remark = paragraph.createRun();
                                    remark.setText("说明：危险品储存情况" + k);
                                    paragraph.createRun().addCarriageReturn();
                                }
                                is = new FileInputStream(new File("D:\\fanhj\\zhexian.png"));
                                doc.addPictureData(is, XWPFDocument.PICTURE_TYPE_PNG);
                                doc.createPicture(paragraph,doc.getAllPictures().size()-1, 400, 400,"");
                                break ll;
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (InvalidFormatException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }
}
