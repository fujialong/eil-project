package com.shencai.eil.assessment.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.shencai.eil.assessment.constants.DiffusionModelBisCode;
import com.shencai.eil.assessment.entity.EntAssessInfo;
import com.shencai.eil.assessment.entity.GridCalculationValue;
import com.shencai.eil.assessment.mapper.*;
import com.shencai.eil.assessment.model.*;
import com.shencai.eil.assessment.service.ICalculationService;
import com.shencai.eil.assessment.service.IGridCalculationValueService;
import com.shencai.eil.common.constants.*;
import com.shencai.eil.common.utils.CommonsUtil;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
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
    private EntAssessInfoMapper entAssessResultMapper;
    @Autowired
    private ComputeConstantMapper computeConstantMapper;


    private static final String CR_CWG_S_CODE = "CRcwg_s";
    private static final String CR_CWG_I_CODE = "CRcwg_i";
    private static final String CR_DGW_S_CODE = "CRdgw_s";
    private static final String CR_DGW_I_CODE = "CRdgw_i";
    private static final String HQ_CWG_S_CODE = "HQcwg_s";
    private static final String HQ_CWG_I_CODE = "HQcwg_i";
    private static final String HQ_DGW_S_CODE = "HQdgw_s";
    private static final String HQ_DGW_I_CODE = "HQdgw_i";
    private static final String CR_S_CODE = "CR_s";
    private static final String CR_I_CODE = "CR_i";
    private static final String HI_S_CODE = "HI_s";
    private static final String HI_I_CODE = "HI_i";
    private static final String LOSS_CR_CODE = "Loss_CR";
    private static final String LOSS_HI_S_CODE = "Loss_HI_s";
    private static final String LOSS_HI_I_CODE = "Loss_HI_i";
    private static final String LOSS_HI_CODE = "Loss_HI";
    private static final List<String> ESV_CODES = Arrays.asList(new String[]{"ESV_s", "ESV_t"});
    private static final List<String> COST_CODES = Arrays.asList(new String[]{"C_s", "C_t"});
    private static final String BI_CODE = "BI";
    private static final String CONCENTRATION = "concentration";
    private static final String LIMIT = "limit";
    private static final String WATER_PRE = "WFT";
    private static final String SOIL_PRE = "SPH";
    private static final String WATER_ACREAGE = "S_s";
    private static final String SOIL_ACREAGE = "S_t";
    private static final String WATER_ECO_VALUE = "Value_s";
    private static final String DEPTH_OF_WATER = "H";
    private static final String DEPTH_OF_WATER_OUTLET = "depth_outlet";
    private static final String DEPTH_OF_WATER_AVG = "depth_avg";
    private static final String SOIL_ECO_VALUE = "Value_t";
    private static final String DEPTH_OF_SOIL = "H_t";
    private static final String DENSITY_OF_SOIL = "W";
    private static final String PH_TYPE_OF_SOIL = "PH_type";
    private static final String POP = "pop";
    private static final String SENSITIVE_AREA = "sensitiveArea";
    private static final String FISHERY_AREA = "AQS";
    private static final String AGRICULTURAL_AREA = "ALS";
    private static final String FORESTRY_AREA = "FLS";
    private static final String SENSITIVE = "sensitive-1";
    private static final String CAL_RF_DO = "CAL_RfDo";
    private static final String CAL_RF_DD = "CAL_RfDd";
    private static final String CAL_RF_DI = "CAL_RfDi";

    private static final String OUTLET_DEPTH_OF_WATER_CODE = "S209";
    private static final String ENT_AROUND_AVG_DEPTH_OF_WATER_CODE = "S307";
    private static final String DENSITY_OF_SOIL_CODE = "S213";
    private static final String PH_OF_SOIL_CODE = "S303";


    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW , rollbackFor = Exception.class)
    public void calculate(String enterpriseId) {
        List<GridCalculationValue> gridCalculationValueList = new ArrayList<>();
        EnterpriseInfo enterpriseInfo = getEnterpriseInfo(enterpriseId);
        executeFormula(gridCalculationValueList, enterpriseInfo, DiffusionModelBisCode.WATER.getCode());
        executeFormula(gridCalculationValueList, enterpriseInfo, DiffusionModelBisCode.SOIL.getCode());
        gridCalculationValueService.saveBatch(gridCalculationValueList);
        saveEntAssessResult(enterpriseId, gridCalculationValueList);
        EnterpriseInfo enterprise = new EnterpriseInfo();
        enterprise.setStatus(StatusEnum.FINISH_ASSESSMENT.getCode());
        enterprise.setUpdateTime(DateUtil.getNowTimestamp());
        enterpriseInfoMapper.update(enterprise, new QueryWrapper<EnterpriseInfo>()
                .eq("id", enterpriseId)
                .eq("valid", BaseEnum.VALID_YES.getCode()));

    }

    private void saveEntAssessResult(String enterpriseId, List<GridCalculationValue> gridCalculationValueList) {
        EntAssessInfo entAssessResult = new EntAssessInfo();
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
        entAssessResult.setUpdateTime(DateUtil.getNowTimestamp());
        entAssessResultMapper.update(entAssessResult, new QueryWrapper<EntAssessInfo>()
                .eq("ent_id", enterpriseId).eq("valid", BaseEnum.VALID_YES.getCode()));
    }

    private void executeFormula(List<GridCalculationValue> gridCalculationValueList, EnterpriseInfo enterpriseInfo, String diffusionModelBisCode) {
        List<GridConcentrationVO> gridConcentrationList = listGridConcentration(diffusionModelBisCode, enterpriseInfo.getId());
        if (CollectionUtils.isEmpty(gridConcentrationList)) {
            return;
        }
        List<GridVO> gridList = processingConcentrationData(gridConcentrationList);
        HashMap<String, HashMap<String, Double>> materialParamMap = getMaterialParamMap(enterpriseInfo.getId()
                , DiffusionModelBisCode.WATER.getCode().equals(diffusionModelBisCode)
                        ? TemplateCategory.WATER_DAMAGE_ASSESSMENT.getCode(): TemplateCategory.SOIL_DAMAGE_ASSESSMENT.getCode());
        HashMap<String, Double> cantonParamMap = getCantonParamMap(enterpriseInfo
                , DiffusionModelBisCode.WATER.getCode().equals(diffusionModelBisCode)
                        ? TemplateCategory.WATER_DAMAGE_ASSESSMENT.getCode(): TemplateCategory.SOIL_DAMAGE_ASSESSMENT.getCode());
        List<CalculationFormulaVO> formulaList = listFormula(diffusionModelBisCode);
        List<LandUseParamValueVO> sensitiveLandUseParamList = listLandUseParam(String.valueOf(BaseEnum.VALID_YES.getCode()));
        List<LandUseParamValueVO> insensitiveLandUseParamList = listLandUseParam(String.valueOf(BaseEnum.VALID_NO.getCode()));
        List<ComputeConstant> constants = listConstant();

        HashMap<String, Object> sensitiveParamMap = new HashMap<>();
        HashMap<String, Object> insensitiveParamMap = new HashMap<>();
        processingParamData(enterpriseInfo, diffusionModelBisCode, cantonParamMap, sensitiveLandUseParamList
                , insensitiveLandUseParamList, constants, sensitiveParamMap, insensitiveParamMap);

        for (GridVO grid: gridList) {
            addGridParamToMap(diffusionModelBisCode, sensitiveParamMap, insensitiveParamMap, grid);
            GridCalculationValue gridCalculationValue = calculateGridCalculationValue(diffusionModelBisCode
                    , materialParamMap, grid, sensitiveParamMap, insensitiveParamMap, formulaList);
            gridCalculationValue.setId(StringUtil.getUUID());
            gridCalculationValue.setEntId(enterpriseInfo.getId());
            gridCalculationValue.setGridCode(grid.getGridCode());
            gridCalculationValue.setCreateTime(DateUtil.getNowTimestamp());
            gridCalculationValue.setValid((Integer) BaseEnum.VALID_YES.getCode());
            gridCalculationValueList.add(gridCalculationValue);
        }
    }

    private void addGridParamToMap(String diffusionModelBisCode, HashMap<String, Object> sensitiveParamMap, HashMap<String, Object> insensitiveParamMap, GridVO grid) {
        sensitiveParamMap.put(POP, grid.getPop());
        insensitiveParamMap.put(POP, grid.getPop());
        sensitiveParamMap.put(SENSITIVE_AREA, grid.getSensitiveArea());
        insensitiveParamMap.put(SENSITIVE_AREA, grid.getSensitiveArea());

        if (DiffusionModelBisCode.WATER.getCode().equals(diffusionModelBisCode)) {
            sensitiveParamMap.put(WATER_ACREAGE, grid.getAcreage());
            sensitiveParamMap.put(WATER_ECO_VALUE, grid.getEcoValue());
            sensitiveParamMap.put(DEPTH_OF_WATER, sensitiveParamMap.get(grid.getWaterHeightType()));
            sensitiveParamMap.put(FISHERY_AREA, grid.getFisheryArea());
            insensitiveParamMap.put(WATER_ACREAGE, grid.getAcreage());
            insensitiveParamMap.put(WATER_ECO_VALUE, grid.getEcoValue());
            insensitiveParamMap.put(DEPTH_OF_WATER, insensitiveParamMap.get(grid.getWaterHeightType()));
            insensitiveParamMap.put(FISHERY_AREA, grid.getFisheryArea());
        } else {
            sensitiveParamMap.put(SOIL_ACREAGE, grid.getAcreage());
            sensitiveParamMap.put(SOIL_ECO_VALUE, grid.getEcoValue());
            sensitiveParamMap.put(DEPTH_OF_SOIL, grid.getPointList().get(0).getZ() * 2); //土壤高度取Z
            sensitiveParamMap.put(AGRICULTURAL_AREA, ObjectUtils.isNull(grid.getAgriculturalArea())? .0: grid.getForestryArea());
            sensitiveParamMap.put(FORESTRY_AREA, ObjectUtils.isNull(grid.getForestryArea())? .0: grid.getForestryArea());
            insensitiveParamMap.put(SOIL_ACREAGE, grid.getAcreage());
            insensitiveParamMap.put(SOIL_ECO_VALUE, grid.getEcoValue());
            insensitiveParamMap.put(DEPTH_OF_SOIL, grid.getPointList().get(0).getZ() * 2); //土壤高度取Z
            insensitiveParamMap.put(AGRICULTURAL_AREA, ObjectUtils.isNull(grid.getAgriculturalArea())? .0: grid.getForestryArea());
            insensitiveParamMap.put(FORESTRY_AREA, ObjectUtils.isNull(grid.getForestryArea())? .0: grid.getForestryArea());
        }
    }

    private GridCalculationValue calculateGridCalculationValue(String diffusionModelBisCode
            , HashMap<String, HashMap<String, Double>> materialParamMap, GridVO grid
            , HashMap<String, Object> sensitiveParamMap, HashMap<String, Object> insensitiveParamMap, List<CalculationFormulaVO> formulaList) {
        GridCalculationValue gridCalculationValue = new GridCalculationValue();
        List<Variable> variables = new ArrayList<>();
        for (CalculationFormulaVO formula: formulaList) {
            String[] arrays = formula.getFormula().split("#");
            String paramStr = arrays[0];
            String formulaStr = arrays[1];
            List<String> params = Arrays.asList(paramStr.split(","));
            if (formula.getCode().startsWith("CAL_")) {
                Double result = 0.0;
                Boolean flag = true;
                for (GridPointVO point: grid.getPointList()) {
                    Map<String,Double> materialParamValueMap = materialParamMap.get(point.getMaterial());
                    for (String param: params) {
                        if (CONCENTRATION.equals(param)) {
                            variables.add(Variable.createVariable(CONCENTRATION, grid.getAvgConcentration()));
                        } else if (LIMIT.equals(param)) {

                            if (DiffusionModelBisCode.WATER.getCode().equals(diffusionModelBisCode)) {
                                Double v;
                                if(materialParamValueMap==null||materialParamValueMap.get(param)==null){
                                    v = 1.0;
                                }else{
                                    v = materialParamValueMap.get(WATER_PRE + point.getWaterQuality());
                                }
                                variables.add(Variable.createVariable(LIMIT,v));
                            } else {
                                Double v;
                                if(materialParamValueMap==null||materialParamValueMap.get(param)==null){
                                    v = 1.0;
                                }else{
                                    v = materialParamValueMap.get(SOIL_PRE +sensitiveParamMap.get(PH_TYPE_OF_SOIL));
                                }
                                variables.add(Variable.createVariable(LIMIT, v));
                            }
                        } else {
                            Double v;
                            if(materialParamValueMap==null||materialParamValueMap.get(param)==null){
                                v = 0.0;
                            }else{
                                v = materialParamValueMap.get(param);
                            }
                            variables.add(Variable.createVariable(param, v));
                        }
                    }
                    Object object = ExpressionEvaluator.evaluate(formulaStr, variables);
                    if (object instanceof Boolean) {
                        flag = flag && (Boolean) object;
                        if (flag) {
                            sensitiveParamMap.put(formula.getCode(), Double.valueOf(String.valueOf(BaseEnum.VALID_NO.getCode())));
                            insensitiveParamMap.put(formula.getCode(), Double.valueOf(String.valueOf(BaseEnum.VALID_NO.getCode())));
                        } else {
                            sensitiveParamMap.put(formula.getCode(), Double.valueOf(String.valueOf(BaseEnum.VALID_YES.getCode())));
                            insensitiveParamMap.put(formula.getCode(), Double.valueOf(String.valueOf(BaseEnum.VALID_YES.getCode())));
                        }
                    } else if (object instanceof Double) {
                        result += (Double) ExpressionEvaluator.evaluate(formulaStr, variables);
                        sensitiveParamMap.put(formula.getCode(), result);
                        insensitiveParamMap.put(formula.getCode(), result);
                    }
                }
            } else {
                if (HI_S_CODE.equals(formula.getCode()) || HI_I_CODE.equals(formula.getCode())) {
                    for (String param: params) {
                        if( CAL_RF_DO.equals(param) || CAL_RF_DD.equals(param) || CAL_RF_DI.equals(param)){
                            if (SENSITIVE.equals(formula.getUsedCondition())) {
                                double v = Double.valueOf(String.valueOf(sensitiveParamMap.get(param)));
                                if(v==0){
                                    String rgx = "/"+param;
                                    formulaStr = formulaStr.replace(rgx,"*"+param);
                                }
                            } else {
                                double v = Double.valueOf(String.valueOf(insensitiveParamMap.get(param)));
                                if(v==0){
                                    String rgx = "/"+param;
                                    formulaStr = formulaStr.replace(rgx,"*"+param);
                                }
                            }

                        }

                    }
                }
                if (LOSS_HI_S_CODE.equals(formula.getCode())) {
                    if ((Double) sensitiveParamMap.get(HI_S_CODE) <= 1) {
                        sensitiveParamMap.put(formula.getCode(), .0);
                        insensitiveParamMap.put(formula.getCode(), .0);
                        continue;
                    }
                } else if (LOSS_HI_I_CODE.equals(formula.getCode())) {
                    if ((Double) sensitiveParamMap.get(HI_I_CODE) <= 1) {
                        sensitiveParamMap.put(formula.getCode(), .0);
                        insensitiveParamMap.put(formula.getCode(), .0);
                        continue;
                    }
                }
                for (String param: params) {
                    if (SENSITIVE.equals(formula.getUsedCondition())) {
                        variables.add(Variable.createVariable(param, sensitiveParamMap.get(param)));
                    } else {
                        variables.add(Variable.createVariable(param, insensitiveParamMap.get(param)));
                    }
                }

                System.out.println(formulaStr);
                Double result = (Double) ExpressionEvaluator.evaluate(formulaStr, variables);
                if (CR_CWG_S_CODE.equals(formula.getCode())) {
                    gridCalculationValue.setCrCwgS(result);
                } else if (CR_CWG_I_CODE.equals(formula.getCode())) {
                    gridCalculationValue.setCrCwgI(result);
                } else if (CR_DGW_S_CODE.equals(formula.getCode())) {
                    gridCalculationValue.setCrDgwS(result);
                }  else if (CR_DGW_I_CODE.equals(formula.getCode())) {
                    gridCalculationValue.setCrDgwI(result);
                } else if (CR_DGW_S_CODE.equals(formula.getCode())) {
                    gridCalculationValue.setCrDgwS(result);
                }  else if (CR_DGW_I_CODE.equals(formula.getCode())) {
                    gridCalculationValue.setCrDgwI(result);
                } else if (HQ_CWG_S_CODE.equals(formula.getCode())) {
                    gridCalculationValue.setHqCwgS(result);
                }  else if (HQ_CWG_I_CODE.equals(formula.getCode())) {
                    gridCalculationValue.setHqCwgI(result);
                } else if (HQ_DGW_S_CODE.equals(formula.getCode())) {
                    gridCalculationValue.setHqDgwS(result);
                }  else if (HQ_DGW_I_CODE.equals(formula.getCode())) {
                    gridCalculationValue.setHqDgwI(result);
                } else if (CR_S_CODE.equals(formula.getCode())) {
                    gridCalculationValue.setCrS(result);
                }  else if (CR_I_CODE.equals(formula.getCode())) {
                    gridCalculationValue.setCrI(result);
                } else if (HI_S_CODE.equals(formula.getCode())) {
                    gridCalculationValue.setHiS(result);
                }  else if (HI_I_CODE.equals(formula.getCode())) {
                    gridCalculationValue.setHiI(result);
                } else if (LOSS_CR_CODE.equals(formula.getCode())) {
                    gridCalculationValue.setLossCr(result);
                } else if (LOSS_HI_CODE.equals(formula.getCode())) {
                    gridCalculationValue.setLossHi(result);
                } else if (ESV_CODES.contains(formula.getCode())) {
                    gridCalculationValue.setEsv(result);
                } else if (BI_CODE.equals(formula.getCode())) {
                    gridCalculationValue.setBi(result);
                } else if (COST_CODES.contains(formula.getCode())) {
                    gridCalculationValue.setCost(result);
                }
                sensitiveParamMap.put(formula.getCode(), result);
                insensitiveParamMap.put(formula.getCode(), result);
            }
            variables.clear();
        }
        return gridCalculationValue;
    }

    private void processingParamData(EnterpriseInfo enterpriseInfo, String diffusionModelBisCode, HashMap<String, Double> cantonParamMap, List<LandUseParamValueVO> sensitiveLandUseParamList, List<LandUseParamValueVO> insensitiveLandUseParamList, List<ComputeConstant> constants, HashMap<String, Object> sensitiveParamMap, HashMap<String, Object> insensitiveParamMap) {
        sensitiveParamMap.putAll(cantonParamMap);
        for (LandUseParamValueVO param: sensitiveLandUseParamList) {
            sensitiveParamMap.put(param.getParamCode(), Double.valueOf(param.getParamValue()));
        }

        insensitiveParamMap.putAll(cantonParamMap);
        for (LandUseParamValueVO param: insensitiveLandUseParamList) {
            insensitiveParamMap.put(param.getParamCode(), Double.valueOf(param.getParamValue()));
        }

        for (ComputeConstant constant: constants) {
            sensitiveParamMap.put(constant.getCode(), Double.valueOf(constant.getValue()));
            insensitiveParamMap.put(constant.getCode(), Double.valueOf(constant.getValue()));
        }

        if (DiffusionModelBisCode.WATER.getCode().equals(diffusionModelBisCode)) {
            Double outlet = Double.valueOf(getSurveyItemResult(enterpriseInfo, OUTLET_DEPTH_OF_WATER_CODE));
            Double avg = Double.valueOf(getSurveyItemResult(enterpriseInfo, ENT_AROUND_AVG_DEPTH_OF_WATER_CODE));
            sensitiveParamMap.put(DEPTH_OF_WATER_OUTLET, outlet);
            sensitiveParamMap.put(DEPTH_OF_WATER_AVG, outlet);
            insensitiveParamMap.put(DEPTH_OF_WATER_OUTLET, avg);
            insensitiveParamMap.put(DEPTH_OF_WATER_AVG, avg);
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
    }

    private List<GridVO> processingConcentrationData(List<GridConcentrationVO> gridConcentrationList) {
        List<GridVO> gridList = new ArrayList<>();
        HashMap<String, List<GridPointVO>> gridMapPoints = new HashMap<>();
        for (GridConcentrationVO vo: gridConcentrationList) {
            if (!gridMapPoints.containsKey(vo)) {
                gridMapPoints.put(vo.getGridCode(), new ArrayList<>());
            }
            GridPointVO point = new GridPointVO();
            CommonsUtil.cloneObj(vo, point);
            if (ScenarioEnum.S_ONE.getCode().equals(vo.getScenarioCode())
                    && EmissionModeType.DIRECT.getCode().equals(vo.getEmissionModeType())) {
                point.setWaterHeightType(DEPTH_OF_WATER_OUTLET);
            } else {
                point.setWaterHeightType(DEPTH_OF_WATER_AVG);
            }
            gridMapPoints.get(vo.getGridCode()).add(point);
        }
        Iterator<Map.Entry<String, List<GridPointVO>>> iterator = gridMapPoints.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = iterator.next();
            String gridCode = (String) entry.getKey();
            List<GridPointVO> pointList = (List<GridPointVO>) entry.getValue();
            GridVO grid = new GridVO();
            grid.setGridCode(gridCode);
            grid.setAvgConcentration(pointList.get(0).getAvgConcentration());
            grid.setPop(pointList.get(0).getPop());
            grid.setSensitiveArea(pointList.get(0).getSensitiveArea());
            grid.setAcreage(pointList.get(0).getAcreage());
            grid.setEcoValue(pointList.get(0).getEcoValue());
            grid.setFisheryArea(pointList.get(0).getFisheryArea());
            grid.setAgriculturalArea(pointList.get(0).getAgriculturalArea());
            grid.setForestryArea(pointList.get(0).getForestryArea());
            grid.setPointList(pointList);
            grid.setWaterHeightType(pointList.get(0).getWaterHeightType());
            for (GridPointVO point: pointList) {
                if (!point.getWaterHeightType().equals(pointList.get(0).getWaterHeightType())) {
                    grid.setWaterHeightType(DEPTH_OF_WATER_OUTLET
                            .equals(pointList.get(0).getWaterHeightType())? DEPTH_OF_WATER_AVG: DEPTH_OF_WATER_OUTLET);
                }
            }
            gridList.add(grid);
        }
        return gridList;
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

    private List<GridConcentrationVO> listGridConcentration(String diffusionModelBisCode, String enterpriseId) {
        GridConcentrationQueryParam queryParam = new GridConcentrationQueryParam();
        queryParam.setBisCode(diffusionModelBisCode);
        queryParam.setEntId(enterpriseId);
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

    private List<CalculationFormulaVO> listFormula(String bisCode) {
        CalculationFormulaQueryParam queryParam = new CalculationFormulaQueryParam();
        queryParam.setBisCode(bisCode);
        return calculationFormulaMapper.listFormula(queryParam);
    }
}
