package com.shencai.eil.survey.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shencai.eil.common.constants.*;
import com.shencai.eil.common.utils.DateUtil;
import com.shencai.eil.common.utils.EilFileUtil;
import com.shencai.eil.common.utils.ObjectUtil;
import com.shencai.eil.common.utils.StringUtil;
import com.shencai.eil.exception.BusinessException;
import com.shencai.eil.grading.entity.EntMappingTargetType;
import com.shencai.eil.grading.entity.EntRiskParamValue;
import com.shencai.eil.grading.entity.Param;
import com.shencai.eil.grading.entity.TemplateMappingParam;
import com.shencai.eil.grading.mapper.EntMappingTargetTypeMapper;
import com.shencai.eil.grading.mapper.ParamMapper;
import com.shencai.eil.grading.model.EntRiskParamValueParam;
import com.shencai.eil.grading.model.ParamQueryParam;
import com.shencai.eil.grading.model.ParamVO;
import com.shencai.eil.grading.service.IEntRiskParamValueService;
import com.shencai.eil.grading.service.IParamService;
import com.shencai.eil.grading.service.ITemplateMappingParamService;
import com.shencai.eil.policy.entity.EnterpriseInfo;
import com.shencai.eil.policy.mapper.EnterpriseInfoMapper;
import com.shencai.eil.survey.constants.ExcelEnum;
import com.shencai.eil.survey.constants.ExcelSheetName;
import com.shencai.eil.survey.entity.EntSurveyResult;
import com.shencai.eil.survey.mapper.*;
import com.shencai.eil.survey.model.*;
import com.shencai.eil.survey.service.IEntSurveyFileService;
import com.shencai.eil.survey.service.IEntSurveyResultService;
import com.shencai.eil.system.entity.BaseFileupload;
import com.shencai.eil.system.mapper.BaseFileuploadMapper;
import com.shencai.eil.system.model.FilePathConfig;
import org.apache.log4j.Logger;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by fanhj on 2018/10/4.
 */
@Service
public class EntSurveyFileServiceImpl implements IEntSurveyFileService {
    Logger logger = Logger.getLogger(EntSurveyFileServiceImpl.class);
    @Autowired
    private EntSurveyPlanMapper entSurveyPlanMapper;
    @Autowired
    private GradeTemplateParamMapper gradeTemplateParamMapper;
    @Autowired
    private SurveyItemCategoryMapper surveyItemCategoryMapper;
    @Autowired
    private SurveyItemOptionMapper surveyItemOptionMapper;
    @Autowired
    private BaseFileuploadMapper baseFileuploadMapper;
    @Autowired
    private ParamMapper paramMapper;
    @Autowired
    private EnterpriseInfoMapper enterpriseInfoMapper;
    @Autowired
    private FilePathConfig filePathConfig;
    @Autowired
    private IEntSurveyResultService entSurveyResultService;
    @Autowired
    private EntSurveyResultMapper entSurveyResultMapper;
    @Autowired
    private IParamService paramService;
    @Autowired
    private ITemplateMappingParamService templateMappingParamService;
    @Autowired
    private IEntRiskParamValueService entRiskParamValueService;
    @Autowired
    private EntMappingTargetTypeMapper entMappingTargetTypeMapper;

    private static final int READ_EXCEL_START_ROW_INDEX = 1;

    //    private static final int[] BASIC_TABLE_COL_WIDTH = new int[]{2200, 3200, 3000};
    private static final int[] BASIC_TABLE_COL_WIDTH = new int[]{3200, 4500};
    //    private static final int[] INTENSIVE_TABLE_COL_WIDTH = new int[]{2000, 3000, 1000, 1000, 800, 1100};
    private static final int[] INTENSIVE_TABLE_COL_WIDTH = new int[]{3200, 4500};
    private static final String BASIC_SURVEY_TABLE_TITLE = "勘查基础项：";
    private static final String BASIC_SURVEY_TABLE_WIDTH = "9000";
    private static final String INTENSIVE_SURVEY_TABLE_TITLE = "勘查强化项：";
    private static final String INTENSIVE_SURVEY_TABLE_WIDTH = "9000";
    private static final int WORD_TITLE_FONT_SIZE = 16;
    private static final String RISK_LEVEL_CODE = "risk-level";
    private static final List<String> paramCodesOfLowRiskLevel
            = Arrays.asList(new String[]{"R1", "R2-desc", "R3", "R4.1-desc", "R4.2-desc"
            , "R4.3-desc", "R4.4-desc", "R4.5-desc", "risk-level"});
    private static final List<String> needFontColorCode
            = Arrays.asList(new String[]{"Ru-s-level", "Ru-g-level", "R1-s-leve", "R1-g-level", "R2-level"
            , "R3-s-level", "R3-g-level", "R4.1-level", "R4.2-level", "R4.3-level", "R4.4-level", "R4.5-level"});
    //    private static final List<String> BASIC_TABLE_TH = Arrays.asList(new String[]{"勘查条目", "勘查内容", "默认值"});
    private static final List<String> BASIC_TABLE_TH = Arrays.asList(new String[]{"勘查条目", "勘查内容"});
    private static final List<String> INTENSIVE_TABLE_TH
//            = Arrays.asList(new String[]{"勘查条目", "勘查内容", "重要性", "成本（元）", "分级", "分级评分"});
            = Arrays.asList(new String[]{"勘查条目", "勘查内容"});
    private static final String UPPER_LIMIT_OF_NUMERIC_VALIDATION = "999999999999";
    private static final String LOWER_LIMIT_OF_NUMERIC_VALIDATION = "-999999999999";
    private static final int EXCEL_WORD_SPACE = 448;
    private static final String EXCEL_FONT_FAMILY = "宋体";
    private static final short EXCEL_FONT_SIZE = 11;
    private static final List<String> TYPE_OF_MATERIAL = Arrays.asList(new String[]{"M54", "S247", "S253"});
    private static final String OPTION_CODE_A = "A";
    private static final String START_SIGN = "end";
    private static final String FAST_GRADING_RESULT_TITLE = "eil_fastGrading";
    private static final String BASIC_SURVEY_PLAN_TITLE = "basic_surveyPlan";
    private static final String INTENSIVE_SURVEY_PLAN_TITLE = "intensive_surveyPlan";
    private static final List<String> SURVEY_ITEM_CODE_OF_MATERIAL_TYPE = Arrays.asList("M54", "S247", "S253");
    private static final List<String> SURVEY_ITEM_CODE_OF_MATERIAL_NAME = Arrays.asList("M55", "S248", "S254");
    private static final List<String> SURVEY_ITEM_CODE_OF_ANNUAL_INPUT_OR_OUTPUT = Arrays.asList("M57", "S250", "S256");

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String getFastGradingResult(String enterpriseId) {
        EnterpriseInfo info = getEnterprise(enterpriseId);
        String fileSourceType;
        if (RiskLevel.HIGH.getCode().equals(info.getRiskLevel()) || !ObjectUtil.isEmpty(info.getNeedSurveyUpgrade())) {
            fileSourceType = FileSourceType.FAST_GRADING_FILE.getCode();
        } else {
            fileSourceType = FileSourceType.BASIC_FAST_GRADING_FILE.getCode();
        }
        List<BaseFileupload> fileList = listExistFiles(enterpriseId, fileSourceType);
        String pdfAddress = null;
        if (CollectionUtils.isEmpty(fileList) || fileList.size() != 2) {
            pdfAddress = generateFile(info, fileSourceType);
        } else {
            for (BaseFileupload file : fileList) {
                if (file.getFileAdress().indexOf(".pdf") > -1) {
                    pdfAddress = file.getId();
                }
            }
        }
        return pdfAddress;
    }

    private EnterpriseInfo getEnterprise(String enterpriseId) {
        EnterpriseInfo info = enterpriseInfoMapper.selectById(enterpriseId);
        if (ObjectUtil.isEmpty(info)) {
            throw new BusinessException("enterprise is not exist");
        }
        return info;
    }

    private String generateFile(EnterpriseInfo info, String fileSourceType) {
        String pdfAddress;
        String fileName = StringUtil.getUUID();
        String srcPath = getSrcPath(info);
        String destPath = filePathConfig.destPath + fileName + ".docx";
        String pdfPath = filePathConfig.destPath + fileName + ".pdf";
        List<GradeTemplateParamVO> gradeTemplateParamBasicList = listReplaceValue(info.getId());
        Map<String, GradeTemplateParamVO> map = new HashMap<>();
        boolean needIntensiveTable = false;
        for (GradeTemplateParamVO vo : gradeTemplateParamBasicList) {
            if (ObjectUtil.isEmpty(info.getRiskLevel())
                    || ((ObjectUtil.isEmpty(info.getNeedSurveyUpgrade())
                    || info.getNeedSurveyUpgrade().compareTo((Integer) BaseEnum.NO.getCode()) == 0)
                    && RiskLevel.LOW.getCode().equals(info.getRiskLevel()))) {
                if (paramCodesOfLowRiskLevel.contains(vo.getCode())) {
                    GradeTemplateParamVO specialVO = new GradeTemplateParamVO();
                    specialVO.setParamContent("");
                    map.put(vo.getParamCode(), specialVO);
                } else {
                    map.put(vo.getParamCode(), vo);
                }
                continue;
            }
            map.put(vo.getParamCode(), vo);
            if (!needIntensiveTable && StringUtil.isNotBlank(vo.getCode())
                    && RISK_LEVEL_CODE.equals(vo.getCode())
                    && String.valueOf(BaseEnum.VALID_YES.getCode()).equals(vo.getResultCode())) {
                needIntensiveTable = true;
            }
        }
        try {
            handleWordFile(srcPath, destPath, map, info, needIntensiveTable);
            EilFileUtil.word2pdf(destPath, pdfPath);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("file generate error!");
        }

        deleteFiles(info.getId(), fileSourceType);
        insertFile(info.getId(), fileName + ".docx"
                , fileSourceType, buildFileName(FAST_GRADING_RESULT_TITLE));
        pdfAddress = insertFile(info.getId(), fileName + ".pdf"
                , fileSourceType, buildFileName(FAST_GRADING_RESULT_TITLE));

        return pdfAddress;
    }

    private String getSrcPath(EnterpriseInfo info) {
        String srcPath;
        int typeCount = entMappingTargetTypeMapper.selectCount(new QueryWrapper<EntMappingTargetType>()
                .eq("ent_id", info.getId()).eq("valid", BaseEnum.VALID_YES.getCode()));
        if (typeCount == 1) {
            srcPath = filePathConfig.basicModelPath;
        } else if (typeCount == 2) {
            srcPath = filePathConfig.intensiveModelPath;
        } else {
            throw new BusinessException("can't judge the risk type of enterprise");
        }
        return srcPath;
    }

    @Override
    public String generateExcel(String enterpriseId, String sourceType) {
        List<BaseFileupload> fileList = listExistFiles(enterpriseId, sourceType);
        if (CollectionUtils.isEmpty(fileList) || fileList.size() != 1) {
            HSSFWorkbook workbook = new HSSFWorkbook();
            if (FileSourceType.BASIC_SURVEY_PLAN_EXCEL.getCode().equals(sourceType)) {
                initSurveySheet(enterpriseId, workbook, SurveyItemCategoryCode.EXCEL_VIEW_BASIC_SURVEY_TABLE.getCode());
            }
            if (FileSourceType.INTENSIVE_SURVEY_PLAN_EXCEL.getCode().equals(sourceType)) {
                initSurveySheet(enterpriseId, workbook, SurveyItemCategoryCode.EXCEL_VIEW_INTENSIVE_TOP5.getCode());
                initSurveySheet(enterpriseId, workbook, SurveyItemCategoryCode.EXCEL_VIEW_INTENSIVE_TOP10.getCode());
                initSurveySheet(enterpriseId, workbook, SurveyItemCategoryCode.EXCEL_VIEW_INTENSIVE_S1.getCode());
                initSurveySheet(enterpriseId, workbook, SurveyItemCategoryCode.EXCEL_VIEW_INTENSIVE_S2.getCode());
                initSurveySheet(enterpriseId, workbook, SurveyItemCategoryCode.EXCEL_VIEW_INTENSIVE_S3.getCode());
            }
            String fileName = StringUtil.getUUID() + ".xls";
            try {
                String filePath = filePathConfig.destPath + fileName;
                OutputStream os = new FileOutputStream(filePath);
                workbook.write(os);
                os.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            deleteFiles(enterpriseId, sourceType);
            String excelUrl = insertFile(enterpriseId, fileName, sourceType
                    , FileSourceType.BASIC_SURVEY_PLAN_EXCEL.getCode().equals(sourceType) ?
                            buildFileName(BASIC_SURVEY_PLAN_TITLE) : buildFileName(INTENSIVE_SURVEY_PLAN_TITLE));
            return excelUrl;
        } else {
            return fileList.get(0).getId();
        }
    }

    /**
     * replace the placeholder in the template
     *
     * @param srcPath
     * @param destPath
     * @param replaceMap
     */
    public void handleWordFile(String srcPath, String destPath, Map<String, GradeTemplateParamVO> replaceMap, EnterpriseInfo info, boolean needIntensiveTable) throws Exception {
        logger.info("in generate word");
        XWPFDocument doc = new XWPFDocument(POIXMLDocument.openPackage(srcPath));
        replaceWords(replaceMap, doc);
        if (RiskLevel.HIGH.getCode().equals(info.getRiskLevel()) || !ObjectUtil.isEmpty(info.getNeedSurveyUpgrade())) {
            List<EntSurveyPlanVO> basicSurveyPlanList = listBasicSurveyPlan(info.getId());
            insertBasicSurveyTable(doc, basicSurveyPlanList);
            if (needIntensiveTable) {
                List<EntSurveyPlanVO> intensiveSurveyPlanList = listIntensiveSurveyPlan(info.getId());
                insertIntensiveSurveyTable(doc, intensiveSurveyPlanList);
            }
        }

        OutputStream os = new FileOutputStream(destPath);
        doc.write(os);
        os.close();
    }

    private List<EntSurveyPlanVO> listIntensiveSurveyPlan(String enterpriseId) {
        EntSurveyPlanQueryParam queryParam = new EntSurveyPlanQueryParam();
        queryParam.setEnterpriseId(enterpriseId);
        queryParam.setCategoryCode(SurveyItemCategoryCode.WEB_VIEW.getCode());
        return entSurveyPlanMapper.listIntensiveSurveyPlan(queryParam);
    }

    private void insertIntensiveSurveyTable(XWPFDocument doc, List<EntSurveyPlanVO> intensiveSurveyPlanList) {
        XWPFParagraph breakParagraph = doc.createParagraph();
        breakParagraph.setPageBreak(true);
        XWPFParagraph paragraph = doc.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText(INTENSIVE_SURVEY_TABLE_TITLE);
        run.setFontSize(WORD_TITLE_FONT_SIZE);

        XWPFTable table = doc.createTable(intensiveSurveyPlanList.size() + 1, INTENSIVE_TABLE_TH.size());
        setTableWidthAndHAlign(table, INTENSIVE_SURVEY_TABLE_WIDTH, STJc.CENTER);
        setTableGridCol(table, INTENSIVE_TABLE_COL_WIDTH);
        setTableFirstRow(table, INTENSIVE_TABLE_TH.size(), INTENSIVE_TABLE_TH);
        for (int i = 1; i < table.getRows().size(); i++) {
            XWPFTableRow rowItem = table.getRow(i);
            EntSurveyPlanVO plan = intensiveSurveyPlanList.get(i - 1);
            rowItem.getCell(0).setText(plan.getCode() + " " + plan.getName());
            rowItem.getCell(1).setText(plan.getDescription());
//            rowItem.getCell(2).setText(ObjectUtil.isEmpty(plan.getImportance()) ? "--" : plan.getImportance().toString());
//            rowItem.getCell(3).setText(ObjectUtil.isEmpty(plan.getCost()) ? "--" : plan.getCost().toString());
//            rowItem.getCell(4).setText(plan.getTargetWeightCode());
//            rowItem.getCell(5).setText(ObjectUtil.isEmpty(plan.getAssessValue()) ? "--" : BigDecimal.valueOf(plan.getAssessValue()).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        }
    }

    private void replaceWords(Map<String, GradeTemplateParamVO> replaceMap, XWPFDocument doc) {
        Iterator itPara = doc.getParagraphsIterator();
        while (itPara.hasNext()) {
            XWPFParagraph paragraph = (XWPFParagraph) itPara.next();
            Set<String> set = replaceMap.keySet();
            Iterator iterator = set.iterator();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                List<XWPFRun> run = paragraph.getRuns();
                for (int i = 0; i < run.size(); i++) {
                    if (run.get(i).getText(run.get(i).getTextPosition()) != null
                            && run.get(i).getText(run.get(i).getTextPosition()).equals(key)) {
                        GradeTemplateParamVO vo = replaceMap.get(key);
                        run.get(i).setText(vo.getParamContent(), 0);

                        if (needFontColorCode.indexOf(vo.getCode()) > -1) {
                            if (GradeLineResultCode.LOW.getCode().equals(vo.getResultCode())
                                    || String.valueOf(BaseEnum.VALID_NO.getCode()).equals(vo.getResultCode())) {
                                run.get(i).setColor("00BC1D");
                            } else {
                                run.get(i).setColor("ff0000");
                            }
                        }
                        if (paramCodesOfLowRiskLevel.indexOf(vo.getCode()) > -1) {
                            run.get(i).setBold(true);
                        }
                    }
                }
            }
        }
    }

    private List<EntSurveyPlanVO> listBasicSurveyPlan(String enterpriseId) {
        EntSurveyPlanQueryParam queryParam = new EntSurveyPlanQueryParam();
        queryParam.setEnterpriseId(enterpriseId);
        queryParam.setCategoryCode(SurveyItemCategoryCode.WEB_VIEW.getCode());
        List<EntSurveyPlanVO> basicSurveyPlanList = entSurveyPlanMapper.listBasicSurveyPlan(queryParam);
        return basicSurveyPlanList;
    }

    private void insertBasicSurveyTable(XWPFDocument doc, List<EntSurveyPlanVO> basicSurveyPlanList) {
        XWPFParagraph breakParagraph = doc.createParagraph();
        breakParagraph.setPageBreak(true);
        XWPFParagraph paragraph = doc.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText(BASIC_SURVEY_TABLE_TITLE);
        run.setFontSize(WORD_TITLE_FONT_SIZE);

        XWPFTable table = doc.createTable(basicSurveyPlanList.size() + 1, BASIC_TABLE_TH.size());
        setTableWidthAndHAlign(table, BASIC_SURVEY_TABLE_WIDTH, STJc.CENTER);
        setTableGridCol(table, BASIC_TABLE_COL_WIDTH);
        setTableFirstRow(table, BASIC_TABLE_TH.size(), BASIC_TABLE_TH);

        for (int i = 1; i < table.getRows().size(); i++) {
            XWPFTableRow rowItem = table.getRow(i);
            EntSurveyPlanVO plan = basicSurveyPlanList.get(i - 1);
            rowItem.getCell(0).setText(plan.getCode() + " " + plan.getName());
            rowItem.getCell(1).setText(plan.getDescription());
//            rowItem.getCell(2).setText(StringUtil.isBlank(plan.getDefaultResult()) ? "--" : plan.getDefaultResult());
        }
    }

    private void setTableFirstRow(XWPFTable table, int cols, List<String> heads) {
        XWPFTableRow row = table.getRow(0);
        for (int i = 0; i < cols; i++) {
            row.getCell(i).setText(heads.get(i));
        }
    }

    /**
     * set table width and horizontal align
     *
     * @param table
     * @param width
     * @param enumValue
     */
    public void setTableWidthAndHAlign(XWPFTable table, String width,
                                       STJc.Enum enumValue) {
        CTTblPr tblPr = getTableCTTblPr(table);
        CTTblWidth tblWidth = tblPr.isSetTblW() ? tblPr.getTblW() : tblPr
                .addNewTblW();
        if (enumValue != null) {
            CTJc cTJc = tblPr.addNewJc();
            cTJc.setVal(enumValue);
        }
        tblWidth.setW(new BigInteger(width));
        tblWidth.setType(STTblWidth.DXA);
    }

    /**
     * get table CTTblPr, create it when is not exist,
     *
     * @param table
     * @return
     */
    public CTTblPr getTableCTTblPr(XWPFTable table) {
        CTTbl ttbl = table.getCTTbl();
        CTTblPr tblPr = ttbl.getTblPr() == null ? ttbl.addNewTblPr() : ttbl
                .getTblPr();
        return tblPr;
    }

    /**
     * set table column width
     *
     * @param table
     * @param colWidths
     */
    public static void setTableGridCol(XWPFTable table, int[] colWidths) {
        CTTbl ttbl = table.getCTTbl();
        CTTblGrid tblGrid = ttbl.getTblGrid() != null ? ttbl.getTblGrid()
                : ttbl.addNewTblGrid();
        for (int j = 0, len = colWidths.length; j < len; j++) {
            CTTblGridCol gridCol = tblGrid.addNewGridCol();
            gridCol.setW(new BigInteger(String.valueOf(colWidths[j])));
        }
    }

    private List<BaseFileupload> listExistFiles(String enterpriseId, String sourceType) {
        return baseFileuploadMapper.selectList(new QueryWrapper<BaseFileupload>()
                .eq("source_id", enterpriseId)
                .eq("stype", sourceType)
                .eq("valid", BaseEnum.VALID_YES.getCode()));
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

    private String buildFileName(String preStr) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmm");
        return preStr + "_" + simpleDateFormat.format(new Date());
    }

    private void initSurveySheet(String enterpriseId, HSSFWorkbook workbook, String categoryCode) {
        List<SurveyItemVO> surveyItemList = listSurveyItemByCategory(enterpriseId, categoryCode);
        if (!CollectionUtils.isEmpty(surveyItemList)) {
            HSSFSheet sheet = workbook.createSheet(ExcelSheetName.getNameByCode(categoryCode));
            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            HSSFFont font = workbook.createFont();
            font.setFontName(EXCEL_FONT_FAMILY);
            font.setFontHeightInPoints(EXCEL_FONT_SIZE);
            style.setFont(font);
            HSSFRow row = sheet.createRow(0);
            for (int i = 0; i < surveyItemList.size(); i++) {
                SurveyItemVO item = surveyItemList.get(i);
                HSSFCell cell = row.createCell(i);
                cell.setCellValue(item.getName());
                cell.setCellStyle(style);
                sheet.autoSizeColumn(i);
                setValidation(sheet, i, item);
            }
        }
    }

    private void setValidation(HSSFSheet sheet, int i, SurveyItemVO item) {
        if (ValueType.OPTION.getCode().equals(item.getValueType())
                || ValueType.OPTION_AND_OTHER.getCode().equals(item.getValueType())
                || ValueType.MULTIPLE_OPTION.getCode().equals(item.getValueType())
                || ValueType.MULTIPLE_OPTION_AND_OTHER.getCode().equals(item.getValueType())) {
            List<SurveyItemOptionVO> optionList = listOptions(item);
            if (!CollectionUtils.isEmpty(optionList)) {
                List<String> optionDataList = new ArrayList<>();
                for (int j = 0; j < optionList.size(); j++) {
                    SurveyItemOptionVO option = optionList.get(j);
                    String optionItem = option.getOptionValue();
                    int old = sheet.getColumnWidth(i);
                    int ne = optionItem.length() * EXCEL_WORD_SPACE;
                    if (old < ne) {
                        sheet.setColumnWidth(i, ne);
                    }
                    optionDataList.add(optionItem);
                }
                String[] optionArray = optionDataList.toArray(new String[optionDataList.size()]);
                CellRangeAddressList cellRangeAddressList = new CellRangeAddressList(1, 1, i, i);
                DVConstraint constraint = DVConstraint.createExplicitListConstraint(optionArray);
                HSSFDataValidation dataValidation = new HSSFDataValidation(cellRangeAddressList, constraint);
                if (ValueType.MULTIPLE_OPTION_AND_OTHER.getCode().equals(item.getValueType())
                        || ValueType.OPTION_AND_OTHER.getCode().equals(item.getValueType())) {
                    dataValidation.setShowErrorBox(false);
                }
                sheet.addValidationData(dataValidation);

                if (TYPE_OF_MATERIAL.indexOf(item.getCode()) > -1) {
                    setMaterialSource(sheet, i, optionList);
                }
            }
        } else if (ValueType.NUMBER.getCode().equals(item.getValueType())) {
            CellRangeAddressList cellRangeAddressList = new CellRangeAddressList(1, 1, i, i);
            DVConstraint constraint = DVConstraint.createNumericConstraint(DataValidationConstraint.ValidationType.DECIMAL
                    , DataValidationConstraint.OperatorType.BETWEEN, LOWER_LIMIT_OF_NUMERIC_VALIDATION, UPPER_LIMIT_OF_NUMERIC_VALIDATION);
            HSSFDataValidation dataValidation = new HSSFDataValidation(cellRangeAddressList, constraint);
            dataValidation.createErrorBox("值错误", "请输入数值！");
            sheet.addValidationData(dataValidation);
        }
    }

    private void setMaterialSource(HSSFSheet sheet, int i, List<SurveyItemOptionVO> optionList) {
        int startIndex = 99;
        int offset = 0;
        HSSFRow startRow = sheet.createRow(startIndex);
        HSSFCell startSignCell = startRow.createCell(offset);
        startSignCell.setCellValue(START_SIGN);
        startIndex++;
        HSSFRow materialTypeSourceRow = sheet.createRow(startIndex);
        startIndex++;
        for (SurveyItemOptionVO option : optionList) {
            HSSFCell cell = materialTypeSourceRow.createCell(offset);
            cell.setCellValue(option.getOptionValue());
            int sourceStartIndex = startIndex;
            if (OPTION_CODE_A.equals(option.getCode())) {
                setSourceData(sheet, offset, sourceStartIndex, TemplateEnum.RAW_OR_AUXILIARY_MATERIALS.getCode());
            } else {
                setSourceData(sheet, offset, sourceStartIndex, TemplateEnum.PRODUCTION_STATUS_OF_PRODUCTS.getCode());
            }
            offset++;
        }

        char selectedCol = (char) ('A' + i);
        char sourceEndCol = (char) ('A' + offset - 1);
        String customFormula = "offset($A$" + startIndex + ", 1, match($" + selectedCol + "1, $A$" + startIndex + ":$" + sourceEndCol + "$" + startIndex + ", 0) - 1" +
                ", COUNTA(offset($A$" + startIndex + ", 1, match($" + selectedCol + "1, $A$" + startIndex + ":$" + sourceEndCol + "$" + startIndex + ", 0) - 1, 2000, 1)), 1)";
        DVConstraint formulaConstraint = DVConstraint.createFormulaListConstraint(customFormula);
        CellRangeAddressList cellRangeAddress = new CellRangeAddressList(1, 1, i + 1, i + 1);
        HSSFDataValidation cellValidation = new HSSFDataValidation(cellRangeAddress, formulaConstraint);
        sheet.addValidationData(cellValidation);
    }

    private void setSourceData(HSSFSheet sheet, int offset, int sourceStartIndex, String sourceTemplateCode) {
        List<ParamVO> paramList = listParamByTemplate(sourceTemplateCode);
        for (ParamVO param : paramList) {
            HSSFRow sourceRow;
            if (ObjectUtil.isEmpty(sheet.getRow(sourceStartIndex))) {
                sourceRow = sheet.createRow(sourceStartIndex);
            } else {
                sourceRow = sheet.getRow(sourceStartIndex);
            }
            HSSFCell sourceCell = sourceRow.createCell(offset);
            sourceCell.setCellValue(param.getName());
            sourceStartIndex++;
        }
    }

    private List<ParamVO> listParamByTemplate(String templateCode) {
        ParamQueryParam queryParam = new ParamQueryParam();
        queryParam.setTemplateCategoryCode(templateCode);
        return paramMapper.listParamByTemplate(queryParam);
    }

    private List<SurveyItemOptionVO> listOptions(SurveyItemVO item) {
        SurveyItemOptionQueryParam optionQueryParam = new SurveyItemOptionQueryParam();
        optionQueryParam.setSurveyItemId(item.getId());
        return surveyItemOptionMapper.listSurveyItemOption(optionQueryParam);
    }

    private List<SurveyItemVO> listSurveyItemByCategory(String enterpriseId, String categoryCode) {
        SurveyItemCategoryQueryParam queryParam = new SurveyItemCategoryQueryParam();
        queryParam.setCategoryCode(categoryCode);
        queryParam.setEnterpriseId(enterpriseId);
        return surveyItemCategoryMapper.listSurveyItem(queryParam);
    }

    private List<GradeTemplateParamVO> listReplaceValue(String enterpriseId) {
        GradeTemplateParamQueryParam queryParam = new GradeTemplateParamQueryParam();
        queryParam.setEnterpriseId(enterpriseId);
        return gradeTemplateParamMapper.listParamOfFastGradeReport(queryParam);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importSurveyResults(String baseFileuploadId, String enterpriseId) {
        BaseFileupload fileupload = getFileupload(baseFileuploadId);
        updateBaseFileupload(enterpriseId, fileupload);
        logicDeleteOldFile(baseFileuploadId, enterpriseId, fileupload);
        String dataBackFlowSheetName = readAndSaveSurveyResults(fileupload.getFileAdress(), enterpriseId);
        dataBackFlow(dataBackFlowSheetName, enterpriseId);
    }

    @Override
    public String downloadBasicSurveyResult(String enterpriseId) {
        List<BaseFileupload> fileList = baseFileuploadMapper.selectList(new QueryWrapper<BaseFileupload>()
                .eq("source_id", enterpriseId)
                .eq("stype", FileSourceType.BASIC_SURVEY_PLAN_UPLOAD.getCode())
                .eq("valid", BaseEnum.VALID_YES.getCode()));
        if (CollectionUtils.isEmpty(fileList)) {
            throw new BusinessException("excel of basic survey has not upload!");
        }
        return fileList.get(0).getId();
    }

    @Override
    public String downloadIntensiveSurveyResult(String enterpriseId) {
        List<BaseFileupload> fileList = baseFileuploadMapper.selectList(new QueryWrapper<BaseFileupload>()
                .eq("source_id", enterpriseId)
                .eq("stype", FileSourceType.INTENSIVE_SURVEY_PLAN_UPLOAD.getCode())
                .eq("valid", BaseEnum.VALID_YES.getCode()));
        if (CollectionUtils.isEmpty(fileList)) {
            throw new BusinessException("excel of intensive survey has not upload!");
        }
        return fileList.get(0).getId();
    }

    private void logicDeleteOldFile(String baseFileuploadId, String enterpriseId, BaseFileupload fileupload) {
        BaseFileupload updateParam = new BaseFileupload();
        updateParam.setUpdateTime(DateUtil.getNowTimestamp());
        updateParam.setValid((Integer) BaseEnum.VALID_NO.getCode());
        baseFileuploadMapper.update(updateParam, new QueryWrapper<BaseFileupload>()
                .ne("id", baseFileuploadId)
                .eq("source_id", enterpriseId)
                .eq("stype", fileupload.getStype())
                .eq("valid", BaseEnum.VALID_YES.getCode()));
    }

    private void updateBaseFileupload(String enterpriseId, BaseFileupload fileupload) {
        fileupload.setUpdateTime(DateUtil.getNowTimestamp());
        fileupload.setSourceId(enterpriseId);
        baseFileuploadMapper.updateById(fileupload);
    }

    private BaseFileupload getFileupload(String baseFileuploadId) {
        BaseFileupload baseFileupload = baseFileuploadMapper.selectOne(new QueryWrapper<BaseFileupload>()
                .eq("id", baseFileuploadId)
                .eq("valid", BaseEnum.VALID_YES.getCode()));
        return baseFileupload;
    }

    private String readAndSaveSurveyResults(String filePath, String enterpriseId) {
        List<String> sheetNames = listAllSheetOfTheExcel(filePath);
        List<EntSurveyPlanVO> entSurveyPlanVOS = listAllSurveyPlanOfTheEnterprise(enterpriseId);
        Date nowTime = DateUtil.getNowTimestamp();
        List<EntSurveyResult> surveyResults = new ArrayList<>();
        String dataBackFlowSheetName = null;
        for (String sheetName : sheetNames) {
            ExcelReader readerOfSheet = ExcelUtil.getReader(FileUtil.file(filePath), sheetName);
            List<List<Object>> sheetContent = readerOfSheet.read(READ_EXCEL_START_ROW_INDEX);
            if (ExcelSheetName.Sheet0.getName().equals(sheetName)) {
                disposeContentOfTheSheet(sheetName, entSurveyPlanVOS, nowTime, surveyResults, sheetContent);
                dataBackFlowSheetName = sheetName;
            } else {
                if (ExcelSheetName.TableA0.getName().equals(sheetName)
                        || ExcelSheetName.TableA1.getName().equals(sheetName)) {
                    disposeContentOfTheSheet(sheetName, entSurveyPlanVOS, nowTime, surveyResults, sheetContent);
                    dataBackFlowSheetName = sheetName;
                } else if (ExcelSheetName.Sheet1.getName().equals(sheetName)) {
                    disposeContentOfTheSheet(sheetName, entSurveyPlanVOS, nowTime, surveyResults, sheetContent);
                } else if (ExcelSheetName.Sheet2.getName().equals(sheetName)) {
                    disposeContentOfTheSheet(sheetName, entSurveyPlanVOS, nowTime, surveyResults, sheetContent);
                } else if (ExcelSheetName.Sheet3.getName().equals(sheetName)) {
                    disposeContentOfTheSheet(sheetName, entSurveyPlanVOS, nowTime, surveyResults, sheetContent);
                }
            }
        }
        if (!CollectionUtils.isEmpty(surveyResults)) {
            deleteEntSurveyResults(enterpriseId, nowTime, sheetNames);
            saveEntSurveyResults(surveyResults);
        }
        return dataBackFlowSheetName;
    }

    private void saveEntSurveyResults(List<EntSurveyResult> surveyResults) {
        entSurveyResultService.saveBatch(surveyResults);
    }

    private void deleteEntSurveyResults(String enterpriseId, Date nowTime, List<String> sheetNames) {
        List<String> categoryCodeList = new ArrayList<>();
        for (String sheetName : sheetNames) {
            categoryCodeList.add(ExcelSheetName.getCodeByName(sheetName));
        }
        EntSurveyResultParam param = new EntSurveyResultParam();
        param.setEnterpriseId(enterpriseId);
        param.setCategoryCodeList(categoryCodeList);
        param.setUpdateTime(nowTime);
        entSurveyResultService.deleteEntSurveyResults(param);
    }

    private void dataBackFlow(String sheetName, String enterpriseId) {
        boolean isBackFlowProdAndRawMaterial = judgeIsOrNotBackFlowProdAndRawMaterial(sheetName, enterpriseId);
        List<EntSurveyResultVO> surveyResultVOList = listSurveyResultForDataBackFlow(sheetName, enterpriseId);
        List<EntSurveyResultVO> productionSurveyResultList = new ArrayList<>();
        List<EntSurveyResultVO> rawMaterialSurveyResultList = new ArrayList<>();
        List<EntSurveyResultVO> gasEmissionsSurveyResultList = new ArrayList<>();
        List<EntSurveyResultVO> waterEmissionsSurveyResultList = new ArrayList<>();
        List<EntSurveyResultVO> heavyMetalGasEmissionsSurveyResultList = new ArrayList<>();
        List<EntSurveyResultVO> heavyMetalWaterEmissionsSurveyResultList = new ArrayList<>();
        List<Integer> rowIndexList = listAllRowIndex(surveyResultVOList);
        disposeEntSurveyResult(isBackFlowProdAndRawMaterial, surveyResultVOList,
                productionSurveyResultList, rawMaterialSurveyResultList,
                gasEmissionsSurveyResultList, waterEmissionsSurveyResultList,
                heavyMetalGasEmissionsSurveyResultList, heavyMetalWaterEmissionsSurveyResultList,
                rowIndexList);
        List<String> templateCategoryTypeList = listTemplateCategoryTypeForDataBackFlow(isBackFlowProdAndRawMaterial, sheetName);
        List<ParamVO> paramList = listParamForDataBackFlow(templateCategoryTypeList);
        List<ParamVO> productionParamList = new ArrayList<>();
        List<ParamVO> rawMaterialParamList = new ArrayList<>();
        List<ParamVO> gasEmissionsIntensityParamList = new ArrayList<>();
        List<ParamVO> waterEmissionsIntensityParamList = new ArrayList<>();
        List<ParamVO> heavyMetalGasEmissionsIntensityParamList = new ArrayList<>();
        List<ParamVO> heavyMetalWaterEmissionsIntensityParamList = new ArrayList<>();
        disposeParam(isBackFlowProdAndRawMaterial, paramList, productionParamList, rawMaterialParamList,
                gasEmissionsIntensityParamList, waterEmissionsIntensityParamList,
                heavyMetalGasEmissionsIntensityParamList, heavyMetalWaterEmissionsIntensityParamList);
        List<EntRiskParamValue> entRiskParamValueList = new ArrayList<>();
        Date nowTime = DateUtil.getNowTimestamp();
        batchCreateEntRiskParamValue(enterpriseId, productionSurveyResultList, productionParamList, entRiskParamValueList, nowTime);
        batchCreateEntRiskParamValue(enterpriseId, rawMaterialSurveyResultList, rawMaterialParamList, entRiskParamValueList, nowTime);
        Double yield = getEnterpriseYield(enterpriseId);
        List<Param> newParamList = new ArrayList<>();
        List<TemplateMappingParam> newTemplateMappingParamList = new ArrayList<>();
        batchCreateEmissionsEntRiskParamValue(enterpriseId, gasEmissionsSurveyResultList, gasEmissionsIntensityParamList,
                entRiskParamValueList, nowTime, yield, newParamList, newTemplateMappingParamList,
                TemplateEnum.TEMPLATE_ID_OF_GAS.getCode());
        batchCreateEmissionsEntRiskParamValue(enterpriseId, waterEmissionsSurveyResultList, waterEmissionsIntensityParamList,
                entRiskParamValueList, nowTime, yield, newParamList, newTemplateMappingParamList,
                TemplateEnum.TEMPLATE_ID_OF_WATER.getCode());
        batchCreateEmissionsEntRiskParamValue(enterpriseId, heavyMetalGasEmissionsSurveyResultList, heavyMetalGasEmissionsIntensityParamList,
                entRiskParamValueList, nowTime, yield, newParamList, newTemplateMappingParamList,
                TemplateEnum.TEMPLATE_ID_OF_HEAVY_METAL_GAS.getCode());
        batchCreateEmissionsEntRiskParamValue(enterpriseId, heavyMetalWaterEmissionsSurveyResultList, heavyMetalWaterEmissionsIntensityParamList,
                entRiskParamValueList, nowTime, yield, newParamList, newTemplateMappingParamList,
                TemplateEnum.TEMPLATE_ID_OF_HEAVY_METAL_WATER.getCode());
        saveParam(newParamList);
        saveTemplateMappingParam(newParamList, newTemplateMappingParamList);
        deleteOldEntRiskParamValue(entRiskParamValueList, enterpriseId, templateCategoryTypeList, nowTime);
        saveEntRiskParamValue(entRiskParamValueList);
    }

    private boolean judgeIsOrNotBackFlowProdAndRawMaterial(String sheetName, String enterpriseId) {
        boolean isBackFlowProdAndRawMaterial = true;
        if (ExcelSheetName.Sheet0.getName().equals(sheetName)) {
            List<String> categoryCodeList = new ArrayList<>();
            categoryCodeList.add(ExcelSheetName.TableA0.getCode());
            categoryCodeList.add(ExcelSheetName.TableA1.getCode());
            EntSurveyResultQueryParam queryParam = new EntSurveyResultQueryParam();
            queryParam.setCategoryCodeList(categoryCodeList);
            queryParam.setEnterpriseId(enterpriseId);
            int count = entSurveyResultMapper.countIntensiveEntSurveyResult(queryParam);
            if (count > 0) {
                isBackFlowProdAndRawMaterial = false;
            }
        }
        return isBackFlowProdAndRawMaterial;
    }

    private void saveEntRiskParamValue(List<EntRiskParamValue> entRiskParamValueList) {
        if (!CollectionUtils.isEmpty(entRiskParamValueList)) {
            entRiskParamValueService.saveBatch(entRiskParamValueList);
        }
    }

    private void deleteOldEntRiskParamValue(List<EntRiskParamValue> entRiskParamValueList, String enterpriseId,
                                            List<String> templateCategoryTypeList, Date nowTime) {
        if (!CollectionUtils.isEmpty(entRiskParamValueList)) {
            EntRiskParamValueParam param = new EntRiskParamValueParam();
            param.setEnterpriseId(enterpriseId);
            param.setTemplateCategoryTypeList(templateCategoryTypeList);
            param.setUpdateTime(nowTime);
            entRiskParamValueService.deleteEntRiskParamValue(param);
        }
    }

    private void saveTemplateMappingParam(List<Param> newParamList, List<TemplateMappingParam> newTemplateMappingParamList) {
        if (!CollectionUtils.isEmpty(newParamList)) {
            templateMappingParamService.saveBatch(newTemplateMappingParamList);
        }
    }

    private void saveParam(List<Param> newParamList) {
        if (!CollectionUtils.isEmpty(newParamList)) {
            paramService.saveBatch(newParamList);
        }
    }

    private Double getEnterpriseYield(String enterpriseId) {
        EnterpriseInfo enterpriseInfo = enterpriseInfoMapper.selectOne(new QueryWrapper<EnterpriseInfo>()
                .eq("id", enterpriseId)
                .eq("valid", BaseEnum.VALID_YES.getCode()));
        return enterpriseInfo.getYield();
    }

    private void batchCreateEmissionsEntRiskParamValue(String enterpriseId,
                                                       List<EntSurveyResultVO> emissionsSurveyResultList,
                                                       List<ParamVO> emissionsIntensityParamList,
                                                       List<EntRiskParamValue> entRiskParamValueList, Date nowTime,
                                                       Double yield, List<Param> newParamList,
                                                       List<TemplateMappingParam> newTemplateMappingParamList,
                                                       String templateId) {
        if (!CollectionUtils.isEmpty(emissionsSurveyResultList)) {
            ParamVO maxCodeNumParam = emissionsIntensityParamList.get(emissionsIntensityParamList.size() - 1);
            int maxCodeNum = maxCodeNumParam.getCodeNum();
            for (EntSurveyResultVO surveyResult : emissionsSurveyResultList) {
                EntRiskParamValue entRiskParamValue = new EntRiskParamValue();
                entRiskParamValue.setId(StringUtil.getUUID());
                entRiskParamValue.setEnterpriseId(enterpriseId);
                entRiskParamValue.setCreateTime(nowTime);
                entRiskParamValue.setUpdateTime(nowTime);
                entRiskParamValue.setValid((Integer) BaseEnum.VALID_YES.getCode());
                double emissionQuantity;
                String mainPollutantName;
                if (templateId.equals(TemplateEnum.TEMPLATE_ID_OF_GAS.getCode())
                        || templateId.equals(TemplateEnum.TEMPLATE_ID_OF_WATER.getCode())) {
                    emissionQuantity = surveyResult.getEmission() * surveyResult.getEmissionConcentration();
                    mainPollutantName = surveyResult.getMainPollutantName();
                } else {
                    emissionQuantity = surveyResult.getEmissionOfHeavyMetal();
                    mainPollutantName = surveyResult.getMainHeavyMetalPollutant();
                }
                entRiskParamValue.setValue(String.valueOf(emissionQuantity / yield));
                for (ParamVO param : emissionsIntensityParamList) {
                    if (param.getName().equals(mainPollutantName)) {
                        entRiskParamValue.setParamId(param.getId());
                        break;
                    }
                }
                if (ObjectUtil.isEmpty(entRiskParamValue.getParamId())) {
                    maxCodeNum += 1;
                    String paramCode = templateId + maxCodeNum;
                    Param param = new Param();
                    param.setId(paramCode);
                    param.setCode(paramCode);
                    param.setName(mainPollutantName);
                    param.setValueType(ValueType.DOUBLE.getCode());
                    param.setRemark(mainPollutantName + ParamEnum.SUFFIX_OF_EMISSION_INTENSITY.getCode());
                    param.setCreateTime(nowTime);
                    param.setUpdateTime(nowTime);
                    param.setValid((Integer) BaseEnum.VALID_YES.getCode());
                    newParamList.add(param);

                    entRiskParamValue.setParamId(param.getId());

                    TemplateMappingParam templateMappingParam = new TemplateMappingParam();
                    templateMappingParam.setId(StringUtil.getUUID());
                    templateMappingParam.setTemplateId(templateId);
                    templateMappingParam.setParamId(param.getId());
                    templateMappingParam.setCreateTime(nowTime);
                    templateMappingParam.setUpdateTime(nowTime);
                    templateMappingParam.setValid((Integer) BaseEnum.VALID_YES.getCode());
                    newTemplateMappingParamList.add(templateMappingParam);
                }
                entRiskParamValueList.add(entRiskParamValue);
            }
        }
    }

    private void disposeParam(boolean isBackFlowProdAndRawMaterial, List<ParamVO> paramList,
                              List<ParamVO> productionParamList, List<ParamVO> rawMaterialParamList,
                              List<ParamVO> gasEmissionsIntensityParamList, List<ParamVO> waterEmissionsIntensityParamList,
                              List<ParamVO> heavyMetalGasEmissionsIntensityParamList,
                              List<ParamVO> heavyMetalWaterEmissionsIntensityParamList) {
        for (ParamVO param : paramList) {
            String templateCategoryType = param.getTemplateCategoryType();
            if (isBackFlowProdAndRawMaterial) {
                if (TemplateCategory.PRODUCTION.getCode().equals(templateCategoryType)) {
                    productionParamList.add(param);
                } else if (TemplateCategory.RAW_MATERIAL.getCode().equals(templateCategoryType)) {
                    rawMaterialParamList.add(param);
                }
            }
            if (TemplateCategory.EMISSIONS_INTENSITY.getCode().equals(templateCategoryType)) {
                String templateId = param.getTemplateId();
                if (TemplateEnum.TEMPLATE_ID_OF_GAS.getCode().equals(templateId)) {
                    gasEmissionsIntensityParamList.add(param);
                } else if (TemplateEnum.TEMPLATE_ID_OF_WATER.getCode().equals(templateId)) {
                    waterEmissionsIntensityParamList.add(param);
                } else if (TemplateEnum.TEMPLATE_ID_OF_HEAVY_METAL_GAS.getCode().equals(templateId)) {
                    heavyMetalGasEmissionsIntensityParamList.add(param);
                } else if (TemplateEnum.TEMPLATE_ID_OF_HEAVY_METAL_WATER.getCode().equals(templateId)) {
                    heavyMetalWaterEmissionsIntensityParamList.add(param);
                }
            }
        }
    }

    private List<ParamVO> listParamForDataBackFlow(List<String> templateCategoryTypeList) {
        ParamQueryParam qParam = new ParamQueryParam();
        qParam.setTemplateCategoryTypeList(templateCategoryTypeList);
        return paramMapper.listParam(qParam);
    }

    private List<String> listTemplateCategoryTypeForDataBackFlow(boolean isBackFlowProdAndRawMaterial, String sheetName) {
        List<String> templateCategoryTypeList = new ArrayList<>();
        if (isBackFlowProdAndRawMaterial) {
            templateCategoryTypeList.add(TemplateCategory.PRODUCTION.getCode());
            templateCategoryTypeList.add(TemplateCategory.RAW_MATERIAL.getCode());
        }
        if (ExcelSheetName.Sheet0.getName().equals(sheetName)) {
            templateCategoryTypeList.add(TemplateCategory.EMISSIONS_INTENSITY.getCode());
        }
        return templateCategoryTypeList;
    }

    private void disposeEntSurveyResult(boolean isBackFlowProdAndRawMaterial,
                                        List<EntSurveyResultVO> surveyResultVOList,
                                        List<EntSurveyResultVO> productionSurveyResultList,
                                        List<EntSurveyResultVO> rawMaterialSurveyResultList,
                                        List<EntSurveyResultVO> gasEmissionsSurveyResultList,
                                        List<EntSurveyResultVO> waterEmissionsSurveyResultList,
                                        List<EntSurveyResultVO> heavyMetalGasEmissionsSurveyResultList,
                                        List<EntSurveyResultVO> heavyMetalWaterEmissionsSurveyResultList,
                                        List<Integer> rowIndexList) {
        for (Integer rowIndex : rowIndexList) {
            EntSurveyResultVO surveyResult = new EntSurveyResultVO();
            EntSurveyResultVO gasEmissionSurveyResult = new EntSurveyResultVO();
            EntSurveyResultVO heavyMetalEmissionSurveyResult = new EntSurveyResultVO();
            for (EntSurveyResultVO entSurveyResult : surveyResultVOList) {
                if (entSurveyResult.getExcelRowIndex() == rowIndex) {
                    String surveyItemCode = entSurveyResult.getSurveyItemCode();
                    String result = entSurveyResult.getResult();
                    if (isBackFlowProdAndRawMaterial) {
                        if (SURVEY_ITEM_CODE_OF_MATERIAL_TYPE.contains(surveyItemCode)) {
                            surveyResult.setMaterialTypeOptionName(result);
                            surveyResult.setMaterialTypeOptionCode(entSurveyResult.getOptionCode());
                        } else if (SURVEY_ITEM_CODE_OF_MATERIAL_NAME.contains(surveyItemCode)) {
                            surveyResult.setMaterialName(result);
                        } else if (SURVEY_ITEM_CODE_OF_ANNUAL_INPUT_OR_OUTPUT.contains(surveyItemCode)) {
                            surveyResult.setInputOrOutput(result);
                        }
                    }
                    if (ExcelEnum.MAIN_POLLUTANT_OF_WATER_CODE.getValue().equals(surveyItemCode)) {
                        surveyResult.setMainPollutantName(result);
                    } else if (ExcelEnum.EMISSION_OF_WATER_CODE.getValue().equals(surveyItemCode)) {
                        surveyResult.setEmission(Double.valueOf(result));
                    } else if (ExcelEnum.WATER_POLLUTANT_EMISSION_CONCENTRATION_CODE.getValue().equals(surveyItemCode)) {
                        surveyResult.setEmissionConcentration(Double.valueOf(result));
                    } else if (ExcelEnum.MAIN_POLLUTANT_OF_GAS_CODE.getValue().equals(surveyItemCode)) {
                        gasEmissionSurveyResult.setMainPollutantName(result);
                    } else if (ExcelEnum.EMISSION_OF_GAS_CODE.getValue().equals(surveyItemCode)) {
                        gasEmissionSurveyResult.setEmission(Double.valueOf(result));
                    } else if (ExcelEnum.AIR_POLLUTANT_EMISSION_CONCENTRATION_CODE.getValue().equals(surveyItemCode)) {
                        gasEmissionSurveyResult.setEmissionConcentration(Double.valueOf(result));
                    } else if (ExcelEnum.EMISSIONS_FORM_CODE.getValue().equals(surveyItemCode)) {
                        heavyMetalEmissionSurveyResult.setEmissionsFormOptionCode(entSurveyResult.getOptionCode());
                    } else if (ExcelEnum.MAIN_HEAVY_METAL_POLLUTANT_OF_CODE.getValue().equals(surveyItemCode)) {
                        heavyMetalEmissionSurveyResult.setMainHeavyMetalPollutant(result);
                    } else if (ExcelEnum.ANNUAL_EMISSION_CODE.getValue().equals(surveyItemCode)) {
                        heavyMetalEmissionSurveyResult.setEmissionOfHeavyMetal(Double.valueOf(result));
                    }
                }
            }
            if (!ObjectUtil.isEmpty(surveyResult)) {
                if (isBackFlowProdAndRawMaterial) {
                    String materialTypeOptionCode = surveyResult.getMaterialTypeOptionCode();
                    if (ExcelEnum.PRODUCT_OPTION_CODE.getValue().equals(materialTypeOptionCode)) {
                        productionSurveyResultList.add(surveyResult);
                    } else if (ExcelEnum.MATERIAL_OPTION_CODE.getValue().equals(materialTypeOptionCode)) {
                        rawMaterialSurveyResultList.add(surveyResult);
                    }
                }
                if (!ObjectUtil.isEmpty(surveyResult.getMainPollutantName())) {
                    waterEmissionsSurveyResultList.add(surveyResult);
                }
            }
            if (!ObjectUtil.isEmpty(gasEmissionSurveyResult)) {
                gasEmissionsSurveyResultList.add(gasEmissionSurveyResult);
            }
            if (!ObjectUtil.isEmpty(heavyMetalEmissionSurveyResult)) {
                String emissionsFormOptionCode = heavyMetalEmissionSurveyResult.getEmissionsFormOptionCode();
                if (emissionsFormOptionCode.equals(ExcelEnum.EMISSIONS_FORM_OPTION_CODE_WATER.getValue())) {
                    heavyMetalWaterEmissionsSurveyResultList.add(heavyMetalEmissionSurveyResult);
                } else if (emissionsFormOptionCode.equals(ExcelEnum.EMISSIONS_FORM_OPTION_CODE_GAS.getValue())) {
                    heavyMetalGasEmissionsSurveyResultList.add(heavyMetalEmissionSurveyResult);
                }
            }
        }
    }

    private List<Integer> listAllRowIndex(List<EntSurveyResultVO> surveyResultVOList) {
        List<Integer> rowIndexList = new ArrayList<>();
        for (EntSurveyResultVO entSurveyResultVO : surveyResultVOList) {
            Integer excelRowIndex = entSurveyResultVO.getExcelRowIndex();
            if (!rowIndexList.contains(excelRowIndex)) {
                rowIndexList.add(entSurveyResultVO.getExcelRowIndex());
            }
        }
        return rowIndexList;
    }

    private List<EntSurveyResultVO> listSurveyResultForDataBackFlow(String sheetName, String enterpriseId) {
        EntSurveyResultQueryParam queryParam = new EntSurveyResultQueryParam();
        queryParam.setEnterpriseId(enterpriseId);
        queryParam.setCategoryCode(SurveyItemCategoryCode.GO_BACK.getCode());
        queryParam.setSheetCode(ExcelSheetName.getCodeByName(sheetName));
        return entSurveyResultMapper.listEntSurveyResult(queryParam);
    }

    private void batchCreateEntRiskParamValue(String enterpriseId, List<EntSurveyResultVO> surveyResultList,
                                              List<ParamVO> paramList,
                                              List<EntRiskParamValue> entRiskParamValueList, Date nowTime) {
        for (EntSurveyResultVO surveyResult : surveyResultList) {
            EntRiskParamValue entRiskParamValue = new EntRiskParamValue();
            entRiskParamValue.setId(StringUtil.getUUID());
            entRiskParamValue.setEnterpriseId(enterpriseId);
            entRiskParamValue.setCreateTime(nowTime);
            entRiskParamValue.setUpdateTime(nowTime);
            entRiskParamValue.setValid((Integer) BaseEnum.VALID_YES.getCode());
            entRiskParamValue.setValue(surveyResult.getInputOrOutput());
            for (ParamVO param : paramList) {
                if (param.getName().equals(surveyResult.getMaterialName())) {
                    entRiskParamValue.setParamId(param.getId());
                }
            }
            entRiskParamValueList.add(entRiskParamValue);
        }
    }

    private void disposeContentOfTheSheet(String sheetName, List<EntSurveyPlanVO> entSurveyPlanVOS,
                                          Date nowTime, List<EntSurveyResult> surveyResults,
                                          List<List<Object>> sheetContent) {
        List<EntSurveyPlanVO> entSurveyPlanOfSheet = new ArrayList<>();
        for (EntSurveyPlanVO entSurveyPlan : entSurveyPlanVOS) {
            if (sheetName.equals(ExcelSheetName.getNameByCode(entSurveyPlan.getCategoryCode()))) {
                entSurveyPlanOfSheet.add(entSurveyPlan);
            }
        }
        if (!CollectionUtils.isEmpty(sheetContent)) {
            int rowSize = sheetContent.size();
            for (int i = 0; i < rowSize; i++) {
                List<Object> row = sheetContent.get(i);
                int colSize = row.size();
                boolean isReadFinished = false;
                for (int j = 0; j < colSize; j++) {
                    Object cellValue = row.get(j);
                    if (!ObjectUtil.isEmpty(cellValue)) {
                        if (ExcelEnum.END_OF_SHEET.getValue().equals(cellValue)) {
                            isReadFinished = true;
                            break;
                        }
                        for (EntSurveyPlanVO entSurveyPlan : entSurveyPlanOfSheet) {
                            if (j == entSurveyPlan.getExcelColIndex()) {
                                EntSurveyResult surveyResult = new EntSurveyResult();
                                surveyResult.setId(StringUtil.getUUID());
                                surveyResult.setSurveyPlanId(entSurveyPlan.getId());
                                surveyResult.setResult(String.valueOf(cellValue));
                                surveyResult.setExcelRowIndex(i + 1);
                                surveyResult.setCreateTime(nowTime);
                                surveyResult.setUpdateTime(nowTime);
                                surveyResult.setValid((Integer) BaseEnum.VALID_YES.getCode());
                                surveyResults.add(surveyResult);
                                break;
                            }
                        }
                    }
                }
                if (isReadFinished) {
                    break;
                }
            }
        }
    }

    private List<String> listAllSheetOfTheExcel(String filePath) {
        ExcelReader reader = ExcelUtil.getReader(FileUtil.file(filePath));
        return reader.getSheetNames();
    }

    private List<EntSurveyPlanVO> listAllSurveyPlanOfTheEnterprise(String enterpriseId) {
        EntSurveyPlanQueryParam queryParam = new EntSurveyPlanQueryParam();
        queryParam.setEnterpriseId(enterpriseId);
        List<EntSurveyPlanVO> entSurveyPlanVOS = entSurveyPlanMapper.listEntSurveyPlan(queryParam);
        if (CollectionUtils.isEmpty(entSurveyPlanVOS)) {
            throw new BusinessException("The enterprise has no survey plan");
        }
        return entSurveyPlanVOS;
    }

}
