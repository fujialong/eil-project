package com.shencai.eil.assessment.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shencai.eil.assessment.constants.DiffusionModelBisCode;
import com.shencai.eil.assessment.constants.FormulaCondition;
import com.shencai.eil.assessment.constants.GridUseType;
import com.shencai.eil.assessment.entity.EntAssessResult;
import com.shencai.eil.assessment.entity.GridCalculationValue;
import com.shencai.eil.assessment.mapper.*;
import com.shencai.eil.assessment.model.*;
import com.shencai.eil.assessment.service.ICalculationService;
import com.shencai.eil.assessment.service.IGridCalculationValueService;
import com.shencai.eil.common.constants.BaseEnum;
import com.shencai.eil.common.constants.ComputeConstantEnum;
import com.shencai.eil.common.constants.TemplateCategory;
import com.shencai.eil.common.utils.DateUtil;
import com.shencai.eil.common.utils.StringUtil;
import com.shencai.eil.exception.BusinessException;
import com.shencai.eil.grading.entity.ComputeConstant;
import com.shencai.eil.grading.mapper.ComputeConstantMapper;
import com.shencai.eil.grading.mapper.RiskMaterialParamValueMapper;
import com.shencai.eil.policy.entity.EnterpriseInfo;
import com.shencai.eil.policy.mapper.EnterpriseInfoMapper;
import com.shencai.eil.survey.mapper.EntSurveyResultMapper;
import com.shencai.eil.survey.model.EntSurveyResultQueryParam;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wltea.expression.ExpressionEvaluator;
import org.wltea.expression.datameta.Variable;

import java.util.*;

/**
 * Created by fanhj on 2018/10/21.
 */
@Service
public class CalculationServiceImpl implements ICalculationService {
    @Autowired
    private EnterpriseInfoMapper enterpriseInfoMapper;
    @Autowired
    private CalculationFormulaMapper calculationFormulaMapper;
    @Autowired
    private GridConcentrationMapper gridConcentrationMapper;
    @Autowired
    private IGridCalculationValueService gridCalculationValueService;
    @Autowired
    private RiskMaterialParamValueMapper riskMaterialParamValueMapper;
    @Autowired
    private EntDiffusionModelInfoMapper entDiffusionModelInfoMapper;
    @Autowired
    private CantonParamValueMapper cantonParamValueMapper;
    @Autowired
    private LandUseParamValueMapper landUseParamValueMapper;
    @Autowired
    private EntSurveyResultMapper entSurveyResultMapper;
    @Autowired
    private EntAssessResultMapper entAssessResultMapper;
    @Autowired
    private ComputeConstantMapper computeConstantMapper;

    private static final String CR_CWG_CODE = "CRcwg";
    private static final String CR_DGW_CODE = "CRdgw";
    private static final String HQ_CWG_CODE = "HQcwg";
    private static final String HQ_DGW_CODE = "HQdgw";
    private static final String CR_CODE = "CR";
    private static final String HI_CODE = "HI";
    private static final String LOSS_CR_CODE = "Loss_CR";
    private static final String LOSS_HI_CODE = "Loss_HI";
    private static final List<String> ESV_CODES = Arrays.asList(new String[]{"ESV_s", "ESV_t"});
    private static final List<String> BI_CODES = Arrays.asList(new String[]{"Aqul", "AL", "FL"});
    private static final List<String> COST_CODES = Arrays.asList(new String[]{"C_s", "C_t"});
    private static final String CONCENTRATION = "concentration";
    private static final String LIMIT = "limit";
    private static final String WATER_PRE = "WFT";
    private static final String SOIL_PRE = "SPH";
    private static final String WATER_ACREAGE = "S_s";
    private static final String SOIL_ACREAGE = "S_t";
    private static final String WATER_ECO_VALUE = "Value_s";
    private static final String SOIL_ECO_VALUE = "Value_t";
    private static final String DEPTH_OF_SOIL = "H_t";
    private static final String DENSITY_OF_SOIL = "W";
    private static final String PH_TYPE_OF_SOIL = "PH_type";
    private static final String POP = "pop";

    private static final String OUTLET_DEPTH_OF_WATER_CODE = "S209";
    private static final String ENT_AROUND_AVG_DEPTH_OF_WATER_CODE = "S307";
    private static final String DENSITY_OF_SOIL_CODE = "S213";
    private static final String PH_OF_SOIL_CODE = "S303";


    @Override
    public void calculate(String enterpriseId) {
        List<GridCalculationValue> gridCalculationValueList = new ArrayList<>();
        EnterpriseInfo enterpriseInfo = getEnterpriseInfo(enterpriseId);
        executeFormula(gridCalculationValueList, enterpriseInfo, DiffusionModelBisCode.WATER.getCode());
        executeFormula(gridCalculationValueList, enterpriseInfo, DiffusionModelBisCode.SOIL.getCode());
        gridCalculationValueService.saveBatch(gridCalculationValueList);
        saveEntAssessResult(enterpriseId, gridCalculationValueList);
    }

    private void saveEntAssessResult(String enterpriseId, List<GridCalculationValue> gridCalculationValueList) {
        EntAssessResult entAssessResult = new EntAssessResult();
        entAssessResult.setBi(.0);
        entAssessResult.setEsv(.0);
        entAssessResult.setPi(.0);
        entAssessResult.setCost(.0);
        for (GridCalculationValue value: gridCalculationValueList) {
            entAssessResult.setBi(entAssessResult.getBi() + value.getBi());
            entAssessResult.setEsv(entAssessResult.getEsv() + value.getEsv());
            entAssessResult.setPi(entAssessResult.getPi() + value.getLossCr() + value.getLossHi());
            entAssessResult.setCost(entAssessResult.getCost() + value.getCost());
        }
        entAssessResult.setId(StringUtil.getUUID());
        entAssessResult.setEntId(enterpriseId);
        entAssessResult.setCreateTime(DateUtil.getNowTimestamp());
        entAssessResult.setValid((Integer) BaseEnum.VALID_YES.getCode());
        entAssessResultMapper.insert(entAssessResult);
    }

    private void executeFormula(List<GridCalculationValue> gridCalculationValueList, EnterpriseInfo enterpriseInfo, String diffusionModelBisCode) {
        //todo 中文参数需要替换
        HashMap<String, HashMap<String, Double>> materialParamMap = getMaterialParamMap(enterpriseInfo.getId()
                , DiffusionModelBisCode.WATER.getCode().equals(diffusionModelBisCode)
                        ? TemplateCategory.WATER_DAMAGE_ASSESSMENT.getCode(): TemplateCategory.SOIL_DAMAGE_ASSESSMENT.getCode());
        HashMap<String, Double> cantonParamMap = getCantonParamMap(enterpriseInfo
                , DiffusionModelBisCode.WATER.getCode().equals(diffusionModelBisCode)
                        ? TemplateCategory.WATER_DAMAGE_ASSESSMENT.getCode(): TemplateCategory.SOIL_DAMAGE_ASSESSMENT.getCode());
        List<GridConcentrationVO> gridConcentrationList = listGridConcentration(diffusionModelBisCode);
        List<CalculationFormulaVO> sensitiveFormulaList = listFormulaByCondition(diffusionModelBisCode, FormulaCondition.SENSITIVE);
        List<CalculationFormulaVO> insensitiveFormulaList = listFormulaByCondition(diffusionModelBisCode, FormulaCondition.INSENSITIVE);
        List<LandUseParamValueVO> sensitiveLandUseParamList = listLandUseParam(String.valueOf(BaseEnum.VALID_YES.getCode()));
        List<LandUseParamValueVO> insensitiveLandUseParamList = listLandUseParam(String.valueOf(BaseEnum.VALID_NO.getCode()));
        List<ComputeConstant> constants = listConstant();
        HashMap<String, Object> sensitiveParamMap = new HashMap<>();
        sensitiveParamMap.putAll(cantonParamMap);
        for (LandUseParamValueVO param: sensitiveLandUseParamList) {
            sensitiveParamMap.put(param.getParamCode(), Double.valueOf(param.getParamValue()));
        }

        HashMap<String, Object> insensitiveParamMap = new HashMap<>();
        insensitiveParamMap.putAll(cantonParamMap);
        for (LandUseParamValueVO param: insensitiveLandUseParamList) {
            insensitiveParamMap.put(param.getParamCode(), Double.valueOf(param.getParamValue()));
        }

        for (ComputeConstant constant: constants) {
            sensitiveParamMap.put(constant.getCode(), constant.getValue());
            insensitiveParamMap.put(constant.getCode(), constant.getValue());
        }

        if (DiffusionModelBisCode.WATER.getCode().equals(diffusionModelBisCode)) {
            getSurveyItemResult(enterpriseInfo, OUTLET_DEPTH_OF_WATER_CODE);
            getSurveyItemResult(enterpriseInfo, ENT_AROUND_AVG_DEPTH_OF_WATER_CODE);
        } else {
            Double density = Double.valueOf(getSurveyItemResult(enterpriseInfo, DENSITY_OF_SOIL_CODE));
            Double ph = Double.valueOf(getSurveyItemResult(enterpriseInfo, PH_OF_SOIL_CODE));
            sensitiveParamMap.put(DENSITY_OF_SOIL, density);
            insensitiveParamMap.put(DENSITY_OF_SOIL, density);
            if(ph <= 5.5) {
                sensitiveParamMap.put(PH_TYPE_OF_SOIL, "1");
                insensitiveParamMap.put(PH_TYPE_OF_SOIL, "1");
            } else if(ph > 5.5 & ph <= 6.5){
                sensitiveParamMap.put(PH_TYPE_OF_SOIL, "2");
                insensitiveParamMap.put(PH_TYPE_OF_SOIL, "2");
            }else if(ph > 6.5 & ph <= 7.5){
                sensitiveParamMap.put(PH_TYPE_OF_SOIL, "3");
                insensitiveParamMap.put(PH_TYPE_OF_SOIL, "3");
            }else{
                sensitiveParamMap.put(PH_TYPE_OF_SOIL, "4");
                insensitiveParamMap.put(PH_TYPE_OF_SOIL, "4");
            }
        }

        TemplateGridConcentrationVO temp = new TemplateGridConcentrationVO();
        List<String> materialOfAreaList = new ArrayList<>();
        HashMap<String, Double> materialMappingConcentration = new HashMap<>();
        for (GridConcentrationVO gridConcentration: gridConcentrationList) {
            if (ObjectUtil.isNull(temp.getX())) {
                temp.setGridId(gridConcentration.getGridId());
                temp.setX(gridConcentration.getX());
                temp.setY(gridConcentration.getY());
                temp.setZ(gridConcentration.getZ());
                temp.setPop(gridConcentration.getPop());
                temp.setSensitiveArea(gridConcentration.getSensitiveArea());
                temp.setGridUseType(gridConcentration.getGridUseType());
                temp.setAcreage(gridConcentration.getAcreage());
                temp.setEcoValue(gridConcentration.getEcoValue());
                materialOfAreaList.add(gridConcentration.getMaterial());
                materialMappingConcentration.put(gridConcentration.getMaterial(), gridConcentration.getConcentration());
            } else {
                if (temp.getX() == gridConcentration.getX() && temp.getY() == gridConcentration.getY()) {
                    materialOfAreaList.add(gridConcentration.getMaterial());
                    materialMappingConcentration.put(gridConcentration.getMaterial(), gridConcentration.getConcentration());
                } else {
                    //todo 数据组合
                    //canton params
                    HashMap<String, Object> paramMap = new HashMap<>();
                    List<CalculationFormulaVO> formulaList = new ArrayList<>();
                    if (BaseEnum.VALID_YES.getCode() == temp.getSensitiveArea()) {
                        paramMap.putAll(sensitiveParamMap);
                        formulaList.addAll(sensitiveFormulaList);
                    } else {
                        paramMap.putAll(insensitiveParamMap);
                        formulaList.addAll(insensitiveFormulaList);
                    }
                    paramMap.put(POP, temp.getPop());
                    if (DiffusionModelBisCode.WATER.getCode().equals(diffusionModelBisCode)) {
                        paramMap.put(WATER_ACREAGE, temp.getAcreage());
                        paramMap.put(WATER_ECO_VALUE, temp.getEcoValue());
                    } else {
                        paramMap.put(SOIL_ACREAGE, temp.getAcreage());
                        paramMap.put(SOIL_ECO_VALUE, temp.getEcoValue());
                        paramMap.put(DEPTH_OF_SOIL, temp.getZ()); //土壤高度取Z
                    }
                    //out of limits
//                    for
                    //todo 计算保存
                    GridCalculationValue gridCalculationValue = getGridCalculationValue(diffusionModelBisCode
                            , materialParamMap, temp, materialMappingConcentration, paramMap, formulaList);
                    gridCalculationValueList.add(gridCalculationValue);

                    temp.setGridId(gridConcentration.getGridId());
                    temp.setX(gridConcentration.getX());
                    temp.setY(gridConcentration.getY());
                    temp.setZ(gridConcentration.getZ());
                    temp.setPop(gridConcentration.getPop());
                    temp.setSensitiveArea(gridConcentration.getSensitiveArea());
                    temp.setGridUseType(gridConcentration.getGridUseType());
                    temp.setAcreage(gridConcentration.getAcreage());
                    temp.setEcoValue(gridConcentration.getEcoValue());
                    materialOfAreaList.clear();
                    materialOfAreaList.add(gridConcentration.getMaterial());
                    materialMappingConcentration.clear();
                    materialMappingConcentration.put(gridConcentration.getMaterial(), gridConcentration.getConcentration());
                }
            }

        }
    }

    private GridCalculationValue getGridCalculationValue(String diffusionModelBisCode, HashMap<String, HashMap<String, Double>> materialParamMap, TemplateGridConcentrationVO temp, HashMap<String, Double> materialMappingConcentration, HashMap<String, Object> paramMap, List<CalculationFormulaVO> formulaList) {
        GridCalculationValue gridCalculationValue = new GridCalculationValue();
        List<Variable> variables = new ArrayList<>();
        for (CalculationFormulaVO formula: formulaList) {
            String[] arrays = formula.getFormula().split("#");
            String paramStr = arrays[0];
            String formulaStr = arrays[1];
            List<String> params = Arrays.asList(paramStr.split(","));
            if (formula.getCode().startsWith("$")) {
                Double result = 0.0;
                Boolean flag = true;
                Iterator<Map.Entry<String, HashMap<String, Double>>> iterator = materialParamMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry entry = iterator.next();
                    String materialName = (String) entry.getKey();
                    HashMap<String, Double> valueMap = (HashMap<String, Double>) entry.getValue();
                    for (String param: params) {
                        if (CONCENTRATION.equals(param)) {
                            variables.add(Variable.createVariable(CONCENTRATION, materialMappingConcentration.get(materialName)));
                        } else if (LIMIT.equals(param)) {
                            if (DiffusionModelBisCode.WATER.getCode().equals(diffusionModelBisCode)) {
                                variables.add(Variable.createVariable(LIMIT, valueMap.get(WATER_PRE + temp.getWaterZoning())));
                            } else {
                                variables.add(Variable.createVariable(LIMIT, valueMap.get(SOIL_PRE + paramMap.get(PH_TYPE_OF_SOIL))));
                            }
                        } else {
                            variables.add(Variable.createVariable(param, valueMap.get(param)));
                        }
                    }
                    Object object = ExpressionEvaluator.evaluate(formulaStr, variables);
                    if (object instanceof Boolean) {
                        flag = flag && (Boolean) object;
                        if (flag) {
                            paramMap.put(formula.getCode(), (Double) BaseEnum.VALID_NO.getCode());
                        } else {
                            paramMap.put(formula.getCode(), (Double) BaseEnum.VALID_YES.getCode());
                        }
                    } else if (object instanceof Double) {
                        result += (Double) ExpressionEvaluator.evaluate(formulaStr, variables);
                        paramMap.put(formula.getCode(), result);
                    }
                }
            } else {
                if (LOSS_HI_CODE.equals(formula.getCode()) && (Double) paramMap.get(HI_CODE) <= 1) {
                    gridCalculationValue.setLossHi(.0);
                } else if (BI_CODES.contains(formula.getCode())) {

                }
                for (String param: params) {
                    variables.add(Variable.createVariable(param, paramMap.get(param)));
                }
                Double result = (Double) ExpressionEvaluator.evaluate(formula.getFormula(), variables);
                if (CR_CWG_CODE.equals(formula.getCode())) {
                    gridCalculationValue.setCrCwg(result);
                } else if (CR_DGW_CODE.equals(formula.getCode())) {
                    gridCalculationValue.setCrDgw(result);
                } else if (CR_DGW_CODE.equals(formula.getCode())) {
                    gridCalculationValue.setCrDgw(result);
                } else if (HQ_CWG_CODE.equals(formula.getCode())) {
                    gridCalculationValue.setHqCwg(result);
                } else if (HQ_DGW_CODE.equals(formula.getCode())) {
                    gridCalculationValue.setHqDgw(result);
                } else if (CR_CODE.equals(formula.getCode())) {
                    gridCalculationValue.setCr(result);
                } else if (HI_CODE.equals(formula.getCode())) {
                    gridCalculationValue.setHi(result);
                } else if (LOSS_CR_CODE.equals(formula.getCode())) {
                    gridCalculationValue.setLossCr(result);
                } else if (LOSS_HI_CODE.equals(formula.getCode())) {
                    gridCalculationValue.setLossHi(result);
                } else if (ESV_CODES.contains(formula.getCode())) {
                    gridCalculationValue.setEsv(result);
                } else if (GridUseType.AQUL.getName().equals(temp.getGridUseType())
                        && GridUseType.AQUL.getCode().equals(formula.getCode())) {
                    gridCalculationValue.setCost(result);
                }  else if (GridUseType.AL.getName().equals(temp.getGridUseType())
                        && GridUseType.AL.getCode().equals(formula.getCode())) {
                    gridCalculationValue.setCost(result);
                }  else if (GridUseType.FL.getName().equals(temp.getGridUseType())
                        && GridUseType.FL.getCode().equals(formula.getCode())) {
                    gridCalculationValue.setCost(result);
                } else if (COST_CODES.contains(formula.getCode())) {
                    gridCalculationValue.setCost(result);
                }
                paramMap.put(formula.getCode(), result);
            }
            variables.clear();
        }
        gridCalculationValue.setGridId(temp.getGridId());
        gridCalculationValue.setBisCode(diffusionModelBisCode);
        gridCalculationValue.setId(StringUtil.getUUID());
        gridCalculationValue.setCreateTime(DateUtil.getNowTimestamp());
        gridCalculationValue.setValid((Integer) BaseEnum.VALID_YES.getCode());
        return gridCalculationValue;
    }

    private List<ComputeConstant> listConstant() {
        return computeConstantMapper
                .selectList(new QueryWrapper<ComputeConstant>()
                        .eq("bis_code", ComputeConstantEnum.DAMAGE_ASSESSMENT.getCode())
                        .eq("valid", BaseEnum.VALID_YES.getCode()));
    }

    private String getSurveyItemResult(EnterpriseInfo enterpriseInfo, String surveyItemCode) {
        EntSurveyResultQueryParam queryParam = new EntSurveyResultQueryParam();
        queryParam.setEnterpriseId(enterpriseInfo.getId());
        queryParam.setSurveyItemCode(surveyItemCode);
        return entSurveyResultMapper.getSingleValueBySurveyItemCode(queryParam);
    }

    private List<LandUseParamValueVO> listLandUseParam(String landUse) {
        LandUseParamValueQueryParam queryParam = new LandUseParamValueQueryParam();
        queryParam.setLandUse(landUse);
        List<LandUseParamValueVO> landUseParamList = landUseParamValueMapper.listLandUseParam(queryParam);
        return landUseParamList;
    }

    private List<CalculationFormulaVO> listFormulaByCondition(String diffusionModelBisCode, FormulaCondition sensitive) {
        List<String> conditions = new ArrayList<>();
        conditions.add(sensitive.getCode());
        List<CalculationFormulaVO> sensitiveFormulaList = listFormula(diffusionModelBisCode, conditions);
        return sensitiveFormulaList;
    }

    private List<GridConcentrationVO> listGridConcentration(String diffusionModelBisCode) {
        GridConcentrationQueryParam queryParam = new GridConcentrationQueryParam();
        queryParam.setBisCode(diffusionModelBisCode);
        List<GridConcentrationVO> gridConcentrationList = gridConcentrationMapper.listGridConcentration(queryParam);
        return gridConcentrationList;
    }

    private HashMap<String, Double> getCantonParamMap(EnterpriseInfo enterpriseInfo, String TemplateCategoryCode) {
        CantonParamQueryParam paramQueryParam = new CantonParamQueryParam();
        paramQueryParam.setCantonCode(enterpriseInfo.getCantonCode().substring(0, 2) + "0000");
        paramQueryParam.setTemplateCategoryCode(TemplateCategoryCode);
        List<CantonParamVO> cantonParamList = cantonParamValueMapper.listCantonParam(paramQueryParam);
        HashMap<String, Double> cantonParamMap = new HashMap<>();
        for (CantonParamVO cantonParamVO: cantonParamList) {
            cantonParamMap.put(cantonParamVO.getParamCode(), Double.valueOf(cantonParamVO.getParamValue()));
        }
        return cantonParamMap;
    }

    private HashMap<String, HashMap<String, Double>> getMaterialParamMap(String enterpriseId, String TemplateCategoryCode) {
        List<String> materialNameList = entDiffusionModelInfoMapper.listMaterialName(enterpriseId);
        RiskMaterialQueryParam queryParam= new RiskMaterialQueryParam();
        queryParam.setMaterialNameList(materialNameList);
        //todo 模板参数未全部导入
        queryParam.setTemplateCategoryCode(TemplateCategoryCode);
        List<RiskMaterialVO> materialList = riskMaterialParamValueMapper.listRiskMaterial(queryParam);
        HashMap<String, HashMap<String, Double>> materialParamMap = new HashMap<>();
        for (RiskMaterialVO materialVO: materialList) {
            if (CollectionUtils.isNotEmpty(materialVO.getParamList())) {
                HashMap<String, Double> paramMap = new HashMap<>();
                for (RiskMaterialParamVO vo: materialVO.getParamList()) {
                    paramMap.put(vo.getParamCode(), Double.valueOf(vo.getValue()));
                }
                materialParamMap.put(materialVO.getMaterialName(), paramMap);
            }
        }
        return materialParamMap;
    }

    private EnterpriseInfo getEnterpriseInfo(String enterpriseId) {
        EnterpriseInfo enterpriseInfo = enterpriseInfoMapper.selectById(enterpriseId);
        if (ObjectUtil.isNull(enterpriseInfo)) {
            throw new BusinessException("the enterprise is not exist!");
        }
        return enterpriseInfo;
    }

    private List<CalculationFormulaVO> listFormula(String bisCode, List<String> conditions) {
        CalculationFormulaQueryParam queryParam = new CalculationFormulaQueryParam();
        queryParam.setBisCode(bisCode);
        queryParam.setConditions(conditions);
        return calculationFormulaMapper.listFormula(queryParam);
    }
}
