package com.shencai.eil.survey.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.shencai.eil.common.constants.*;
import com.shencai.eil.common.utils.DateUtil;
import com.shencai.eil.common.utils.StringUtil;
import com.shencai.eil.exception.BusinessException;
import com.shencai.eil.grading.mapper.ParamMapper;
import com.shencai.eil.grading.model.ParamQueryParam;
import com.shencai.eil.grading.model.ParamVO;
import com.shencai.eil.policy.entity.EnterpriseInfo;
import com.shencai.eil.policy.mapper.EnterpriseInfoMapper;
import com.shencai.eil.survey.mapper.EntSurveyPlanMapper;
import com.shencai.eil.survey.mapper.GradeTemplateParamMapper;
import com.shencai.eil.survey.mapper.SurveyItemCategoryMapper;
import com.shencai.eil.survey.mapper.SurveyItemOptionMapper;
import com.shencai.eil.survey.model.*;
import com.shencai.eil.survey.service.IEntSurveyFileService;
import com.shencai.eil.system.entity.BaseFileupload;
import com.shencai.eil.system.mapper.BaseFileuploadMapper;
import com.shencai.eil.system.model.FilePathConfig;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by fanhj on 2018/10/4.
 */
@Service
public class EntSurveyFileServiceImpl implements IEntSurveyFileService {
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

    private static final int[] BASIC_TABLE_COL_WIDTH = new int[] { 2200, 3200, 3000};
    private static final int[] INTENSIVE_TABLE_COL_WIDTH = new int[] { 2000, 3000, 1000, 1000, 800, 1100};
    private static final String BASIC_SURVEY_TABLE_TITLE = "勘查基础项：";
    private static final String BASIC_SURVEY_TABLE_WIDTH = "9000";
    private static final String INTENSIVE_SURVEY_TABLE_TITLE = "勘查强化项：";
    private static final String INTENSIVE_SURVEY_TABLE_WIDTH = "9000";
    private static final int WORD_TITLE_FONT_SIZE = 16;
    private static final String RISK_LEVEL_CODE = "risk-level";
    private static final String RISK_LEVEL_PARAM_CODE = "${risk-level}";
    private static final List<String> BASIC_TABLE_TH = Arrays.asList(new String[]{"勘查条目", "勘查内容", "默认值"});
    private static final List<String> INTENSIVE_TABLE_TH
            = Arrays.asList(new String[]{"勘查条目", "勘查内容", "重要性", "成本（元）", "分级", "分级评分"});
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String getFastGradingResult(String enterpriseId) {
        EnterpriseInfo info = getEnterprise(enterpriseId);
        String fileSourceType;
        if (RiskLevel.HIGH.getCode().equals(info.getRiskLevel()) || ObjectUtils.isNotNull(info.getNeedSurveyUpgrade())) {
            fileSourceType = FileSourceType.FAST_GRADING_FILE.getCode();
        } else {
            fileSourceType = FileSourceType.BASIC_FAST_GRADING_FILE.getCode();
        }
        List<BaseFileupload> fileList = listExistFiles(enterpriseId, fileSourceType);
        String pdfAddress = null;
        if (CollectionUtils.isEmpty(fileList) || fileList.size() != 2) {
            pdfAddress = generateFile(info, fileSourceType);
        } else {
            for(BaseFileupload file: fileList) {
                if (file.getFileAdress().indexOf(".pdf") > -1) {
                    pdfAddress = file.getId();
                }
            }
        }
        return pdfAddress;
    }

    private EnterpriseInfo getEnterprise(String enterpriseId) {
        EnterpriseInfo info = enterpriseInfoMapper.selectById(enterpriseId);
        if (ObjectUtils.isNull(info)) {
            throw new BusinessException("enterprise is not exist");
        }
        return info;
    }

    private String generateFile(EnterpriseInfo info, String fileSourceType) {
        String pdfAddress;
        String fileName = StringUtil.getUUID();
        String srcPath = filePathConfig.modelPath;
        String destPath = filePathConfig.destPath + fileName + ".docx";
        String pdfPath = filePathConfig.destPath + fileName + ".pdf";
        List<GradeTemplateParamVO> gradeTemplateParamBasicList = listReplaceValue(info.getId());
        Map<String, GradeTemplateParamVO> map = new HashMap<>();
        boolean needIntensiveTable = false;
        for (GradeTemplateParamVO vo: gradeTemplateParamBasicList) {
            if (ObjectUtil.isNull(info.getRiskLevel())
                    || (ObjectUtil.isNull(info.getNeedSurveyUpgrade()) && RiskLevel.LOW.getCode().equals(info.getRiskLevel()))) {
                if (RISK_LEVEL_CODE.equals(vo.getCode())) {
                    GradeTemplateParamVO specialVO = new GradeTemplateParamVO();
                    specialVO.setParamContent("");
                    map.put(RISK_LEVEL_PARAM_CODE, specialVO);
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
            word2pdf(destPath, pdfPath);
        } catch (Exception e) {
            throw new BusinessException("file generate error!");
        }

        deleteFiles(info.getId(), fileSourceType);
        insertFile(info.getId(), fileName + ".docx"
                , fileSourceType, buildFileName(FAST_GRADING_RESULT_TITLE));
        pdfAddress = insertFile(info.getId(), fileName + ".pdf"
                , fileSourceType, buildFileName(FAST_GRADING_RESULT_TITLE));

        return pdfAddress;
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
                    , FileSourceType.BASIC_SURVEY_PLAN_EXCEL.getCode().equals(sourceType)?
                            buildFileName(BASIC_SURVEY_PLAN_TITLE): buildFileName(INTENSIVE_SURVEY_PLAN_TITLE));
            return excelUrl;
        } else {
            return fileList.get(0).getFileAdress();
        }
    }

    /**
     * replace the placeholder in the template
     * @param srcPath
     * @param destPath
     * @param replaceMap
     */
    public void handleWordFile(String srcPath, String destPath, Map<String, GradeTemplateParamVO> replaceMap, EnterpriseInfo info, boolean needIntensiveTable) throws Exception {
        XWPFDocument doc = new XWPFDocument(POIXMLDocument.openPackage(srcPath));
        replaceWords(replaceMap, doc);
        if (RiskLevel.HIGH.getCode().equals(info.getRiskLevel()) || ObjectUtils.isNotNull(info.getNeedSurveyUpgrade())) {
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
        run.setBold(true);

        XWPFTable table = doc.createTable(intensiveSurveyPlanList.size() + 1, INTENSIVE_TABLE_TH.size());
        setTableWidthAndHAlign(table, INTENSIVE_SURVEY_TABLE_WIDTH, STJc.CENTER);
        setTableGridCol(table, INTENSIVE_TABLE_COL_WIDTH);
        setTableFirstRow(table, INTENSIVE_TABLE_TH.size(), INTENSIVE_TABLE_TH);
        for (int i = 1; i < table.getRows().size(); i++) {
            XWPFTableRow rowItem = table.getRow(i);
            EntSurveyPlanVO plan = intensiveSurveyPlanList.get(i - 1);
            rowItem.getCell(0).setText(plan.getCode() + " " + plan.getName());
            rowItem.getCell(1).setText(plan.getDescription());
            rowItem.getCell(2).setText(ObjectUtils.isNull(plan.getImportance())? "--": plan.getImportance().toString());
            rowItem.getCell(3).setText(ObjectUtils.isNull(plan.getCost())? "--": plan.getCost().toString());
            rowItem.getCell(4).setText(plan.getTargetWeightCode());
            rowItem.getCell(5).setText(ObjectUtils.isNull(plan.getAssessValue())? "--": BigDecimal.valueOf(plan.getAssessValue()).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        }
    }

    private void replaceWords(Map<String, GradeTemplateParamVO> replaceMap, XWPFDocument doc) {
        Iterator itPara = doc.getParagraphsIterator();
        while(itPara.hasNext()) {
            XWPFParagraph paragraph = (XWPFParagraph) itPara.next();
            Set<String> set = replaceMap.keySet();
            Iterator iterator = set.iterator();
            while(iterator.hasNext()) {
                String key = (String) iterator.next();
                List<XWPFRun> run = paragraph.getRuns();
                for (int i = 0; i < run.size(); i++) {
                    if (run.get(i).getText(run.get(i).getTextPosition()) != null
                            && run.get(i).getText(run.get(i).getTextPosition()).equals(key)) {
                        GradeTemplateParamVO vo = replaceMap.get(key);
                        run.get(i).setText(vo.getParamContent(), 0);
                        if (GradeLineResultCode.LOW.getCode().equals(vo.getResultCode()) || String.valueOf(BaseEnum.VALID_NO.getCode()).equals(vo.getCode())) {
                            run.get(i).setBold(true);
                            run.get(i).setColor("00BC1D");
                        } else {
                            run.get(i).setBold(true);
                            run.get(i).setColor("ff0000");
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
        run.setFontSize(16);
        run.setBold(true);

        XWPFTable table = doc.createTable(basicSurveyPlanList.size() + 1, BASIC_TABLE_TH.size());
        setTableWidthAndHAlign(table, BASIC_SURVEY_TABLE_WIDTH, STJc.CENTER);
        setTableGridCol(table, BASIC_TABLE_COL_WIDTH);
        setTableFirstRow(table, BASIC_TABLE_TH.size(), BASIC_TABLE_TH);

        for (int i = 1; i < table.getRows().size(); i++) {
            XWPFTableRow rowItem = table.getRow(i);
            EntSurveyPlanVO plan = basicSurveyPlanList.get(i - 1);
            rowItem.getCell(0).setText(plan.getCode() + " " + plan.getName());
            rowItem.getCell(1).setText(plan.getDescription());
            rowItem.getCell(2).setText(StringUtil.isBlank(plan.getDefaultResult())? "--": plan.getDefaultResult());
        }
    }

    private void setTableFirstRow(XWPFTable table, int cols, List<String> heads) {
        XWPFTableRow row = table.getRow(0);
        for (int i = 0; i < cols; i++) {
            row.getCell(i).setText(heads.get(i));
        }
    }

    /**
     * convert word to pdf
     * @param srcPath
     * @param destPath
     */
    public void word2pdf(String srcPath, String destPath) throws Exception {
        XWPFDocument document=new XWPFDocument(new FileInputStream(new File(srcPath)));
        File outFile=new File(destPath);
        outFile.getParentFile().mkdirs();
        OutputStream out = new FileOutputStream(outFile);
        PdfOptions options= PdfOptions.create();
        PdfConverter.getInstance().convert(document,out,options);
    }

    /**
     * set table width and horizontal align
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
        String[] strArray = fileName.split("\\.");
        baseFileupload.setFileName(titlePre + "." + strArray[1]);
        baseFileupload.setStype(sourceType);
        String fileAddress = filePathConfig.downloadPath + fileName;
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
        if (CollectionUtils.isNotEmpty(surveyItemList)) {
            HSSFSheet sheet = workbook.createSheet(categoryCode);
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
                || ValueType.MULTIPLE_OPTION.getCode().equals(item.getValueType())
                || ValueType.MULTIPLE_OPTION_AND_OTHER.getCode().equals(item.getValueType())) {
            List<SurveyItemOptionVO> optionList = listOptions(item);
            if (CollectionUtils.isNotEmpty(optionList)) {
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
                if (ValueType.MULTIPLE_OPTION_AND_OTHER.getCode().equals(item.getValueType())) {
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
        for (SurveyItemOptionVO option: optionList) {
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
        String customFormula = "offset($A$"+ startIndex +", 1, match($"+ selectedCol +"1, $A$"+ startIndex +":$"+ sourceEndCol +"$"+ startIndex +", 0) - 1" +
                ", COUNTA(offset($A$"+ startIndex +", 1, match($"+ selectedCol +"1, $A$"+ startIndex +":$"+ sourceEndCol +"$"+ startIndex +", 0) - 1, 2000, 1)), 1)";
        DVConstraint formulaConstraint = DVConstraint.createFormulaListConstraint(customFormula);
        CellRangeAddressList cellRangeAddress = new CellRangeAddressList(1, 1, i + 1, i + 1);
        HSSFDataValidation cellValidation = new HSSFDataValidation(cellRangeAddress, formulaConstraint);
        sheet.addValidationData(cellValidation);
    }

    private void setSourceData(HSSFSheet sheet, int offset, int sourceStartIndex, String sourceTemplateCode) {
        List<ParamVO> paramList = listParamByTemplate(sourceTemplateCode);
        for (ParamVO param : paramList) {
            HSSFRow sourceRow;
            if (ObjectUtil.isNull(sheet.getRow(sourceStartIndex))) {
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
        return gradeTemplateParamMapper.listGradeTemplateParam(queryParam);
    }
}
