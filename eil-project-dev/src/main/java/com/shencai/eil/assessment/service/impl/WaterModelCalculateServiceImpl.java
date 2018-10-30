package com.shencai.eil.assessment.service.impl;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shencai.eil.assessment.entity.AssessGird;
import com.shencai.eil.assessment.entity.EntDiffusionModelInfo;
import com.shencai.eil.assessment.entity.GridConcentration;
import com.shencai.eil.assessment.mapper.EntDiffusionModelInfoMapper;
import com.shencai.eil.assessment.model.EntDiffusionModelInfoQueryParam;
import com.shencai.eil.assessment.model.EntDiffusionModelInfoVO;
import com.shencai.eil.assessment.service.*;
import com.shencai.eil.common.constants.*;
import com.shencai.eil.common.utils.DateUtil;
import com.shencai.eil.common.utils.StringUtil;
import com.shencai.eil.scenario.entity.PollutantParamValue;
import com.shencai.eil.scenario.mapper.ScenarioSelectionInfoMapper;
import com.shencai.eil.scenario.service.IPollutantParamValueService;
import com.shencai.eil.survey.mapper.EntSurveyResultMapper;
import com.shencai.eil.survey.model.EntSurveyResultQueryParam;
import com.shencai.eil.survey.model.EntSurveyResultVO;
import com.shencai.eil.survey.service.IEntSurveyPlanService;
import com.shencai.eil.system.model.FilePathConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @program: eil-project
 * @description:
 * @author: fujl
 * @create: 2018-10-19 14:54
 **/
@Service
@Slf4j
public class WaterModelCalculateServiceImpl implements IWaterModelCalculateService {

    public static final String YEIGHT = "Y8";
    @Autowired
    private IEntSurveyPlanService entSurveyPlanService;
    @Autowired
    private IEntDiffusionModelInfoService entDiffusionModelInfoService;
    @Autowired
    private ScenarioSelectionInfoMapper scenarioSelectionInfoMapper;
    @Autowired
    private IAssessGirdService assessGirdService;
    @Autowired
    private IGridConcentrationService gridConcentrationService;
    @Autowired
    private IPollutantParamValueService pollutantParamValueService;
    @Autowired
    private EntSurveyResultMapper entSurveyResultMapper;
    @Autowired
    private EntDiffusionModelInfoMapper entDiffusionModelInfoMapper;
    @Autowired
    private FilePathConfig filePathConfig;

    @Autowired
    private ICalculationService calculationService;

    //Heavy metals and inorganic substances
    public static final Double HEAVY_METAL = 1.0;
    //percent
    public static final int PERCENT = 50;
    public static final double DOUBLE_DEFAULT_VALUE = 0.0;
    private final static int THREAD_POOL_SIZE = 3;
    private static final int MAX_THREAD_POOL_SIZE = 6;



    @Override
    public void saveToGridConcentration(String entId) {
        ThreadUtil.execute(() -> {
            EntSurveyResultQueryParam queryParam = new EntSurveyResultQueryParam();
            queryParam.setCategoryCode(SurveyItemCategoryCode.DIFFUSION_MODEL.getCode());
            queryParam.setEnterpriseId(entId);
            List<EntSurveyResultVO> entSurveyResultVOList = entSurveyResultMapper.listEntSurveyResult(queryParam);
            Map<String, Object> paraMap = new HashMap<>();
            BlockingQueue<Runnable> workQuene = new LinkedBlockingQueue<>();
            ThreadPoolExecutor executor = new ThreadPoolExecutor(THREAD_POOL_SIZE, MAX_THREAD_POOL_SIZE,
                    0, TimeUnit.MICROSECONDS, workQuene);
            for (EntSurveyResultVO entSurveyResultVO : entSurveyResultVOList) {
                paraMap.put(entSurveyResultVO.getSurveyItemCode(), Double.valueOf(entSurveyResultVO.getResult() + ""));
            }
            EntDiffusionModelInfoQueryParam diffusionModelInfoQueryParam = new EntDiffusionModelInfoQueryParam();
            diffusionModelInfoQueryParam.setEnterpriseId(entId);
            List<EntDiffusionModelInfoVO> entDiffusionModelInfos = entDiffusionModelInfoMapper.listEntDiffusionModelInfoVO(diffusionModelInfoQueryParam);
            List<GridConcentration> allGridConcentrationList = new ArrayList<>();
            for (EntDiffusionModelInfoVO entDiffusionModelInfo : entDiffusionModelInfos) {
                executor.execute(() -> {
                if (entDiffusionModelInfo.getModelId() != null) {
                        List<GridConcentration> gridConcentrationList = calWaterConcentration(entDiffusionModelInfo, entId, paraMap);
                        allGridConcentrationList.addAll(gridConcentrationList);
                }
                List<GridConcentration> gridSolidConcentrationList = calSoilConcentration(entDiffusionModelInfo, entId, paraMap);
                allGridConcentrationList.addAll(gridSolidConcentrationList);
                });
            }
            executor.shutdown();
            try {
                if (!executor.awaitTermination(100 * 60, TimeUnit.SECONDS)) {
                    // pool didn't terminate after the first try
                    executor.shutdownNow();
                }
            } catch (InterruptedException ex) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
            gridConcentrationService.saveBatch(allGridConcentrationList);
            calculationService.calculate(entId);
        });

    }

    public String getSoilType(Double sandRate, Double clayRate) {
        String soilType = "";
        if (sandRate >= PERCENT) {
            soilType = "sand";
        }
        if (sandRate < PERCENT && sandRate < PERCENT) {
            soilType = "loam";
        }
        if (clayRate >= PERCENT) {
            soilType = "clay";
        }
        return soilType;
    }

    private List<GridConcentration> calWaterConcentration(EntDiffusionModelInfoVO entDiffusionModelInfo, String entId, Map<String, Object> paraMap) {
        List<AssessGird> assessGirdList = assessGirdService.entAssessGirdList(entId, DataModelType.WATER_TYPE.getCode());
        if (!CollectionUtils.isEmpty(assessGirdList)) {
            assessGirdList.remove(0);
        }
        List<GridConcentration> gridConcentrationList = new ArrayList<>();
        for (AssessGird assessGird : assessGirdList) {
            double c = calWaterModel(entDiffusionModelInfo, String.valueOf(assessGird.getX()), String.valueOf(assessGird.getY()), paraMap);
            GridConcentration gridConcentration = buildGridConcentration(c, assessGird, entDiffusionModelInfo);
            gridConcentrationList.add(gridConcentration);
        }
        int lastGridCode = -1;
        int lastGridSidePointSize = 0;
        int thisGridPointSize = 0;
        double lastGridSideConcentration = 0.0;
        double thisGridConcentration = 0.0;
        for (int i = 0; i < gridConcentrationList.size(); i++) {
            GridConcentration gridConcentration = gridConcentrationList.get(i);
            AssessGird assessGird = assessGirdList.get(i);
            if (DataModelType.SINGLE.getCode().equals(assessGird.getWaterType())) {
                if (i == 0) {
                    gridConcentration.setAvgConcentration(gridConcentration.getConcentration());
                } else {
                    double avg = (gridConcentration.getConcentration() + gridConcentrationList.get(i - 1).getConcentration()) / 2;
                    gridConcentration.setAvgConcentration(avg);
                }
                lastGridCode = assessGird.getGridCode();
                lastGridSidePointSize = 1;
                lastGridSideConcentration = gridConcentration.getConcentration();
            }
            if (DataModelType.DOUBLE.getCode().equals(assessGird.getWaterType())) {
                if (i == 0) {
                    thisGridConcentration = gridConcentration.getConcentration();
                    thisGridPointSize = 1;
                } else {
                    if (lastGridCode == assessGird.getGridCode()) {
                        thisGridPointSize++;
                        thisGridConcentration += gridConcentration.getConcentration();
                        if (i == gridConcentrationList.size() - 1) {
                            double avg = (thisGridConcentration + lastGridSideConcentration) / (thisGridPointSize + lastGridSidePointSize);
                            setSameGridAvgConcentration(avg, thisGridPointSize, i + 1, gridConcentrationList);
                        }
                    } else {
                        if (DataModelType.SINGLE.getCode().equals(assessGirdList.get(i - 1).getWaterType())) {
                            thisGridPointSize = 1;
                            thisGridConcentration = gridConcentration.getConcentration();
                        } else {
                            double avg = (thisGridConcentration + lastGridSideConcentration) / (thisGridPointSize + lastGridSidePointSize);
                            setSameGridAvgConcentration(avg, thisGridPointSize, i, gridConcentrationList);
                            lastGridSidePointSize = thisGridPointSize;
                            lastGridSideConcentration = thisGridConcentration;
                            thisGridPointSize = 1;
                            thisGridConcentration = gridConcentration.getConcentration();
                        }
                    }
                }
                lastGridCode = assessGird.getGridCode();
            }
            if (DataModelType.LAKER.getCode().equals(assessGird.getWaterType())) {
                gridConcentration.setAvgConcentration(gridConcentration.getConcentration());
            }
        }
        return gridConcentrationList;
    }

    private Double calWaterModel(EntDiffusionModelInfoVO entDiffusionModelInfo, String x, String y, Map<String, Object> paramMap) {
        String modelId = entDiffusionModelInfo.getModelId();
      /*//  String address = System.getProperty("user.dir");
        int index = address.lastIndexOf("\\");
        if (index == 0) {
            index = address.lastIndexOf("/");
        }
        String newAddress = address.substring(0, index);*/
        String exe = "python";
       // String command = newAddress + "\\webapps\\ROOT\\WEB-INF\\classes\\py\\Water_Model1.py";
        //  String command = newAddress + "\\webapps\\ROOT\\WEB-INF\\classes\\py\\Soil_Diffusion_Model.py";
        String command = filePathConfig.waterModel;
        List<String> list = null;
        Double ch = 0.0;
        //The amount of runoff from a river or lake
        Double qh = Double.valueOf(paramMap.get("S308") + "");
        // The width of the river
        Double b;
        Double h;
        if (ScenarioEnum.S_ONE.getCode().equals(entDiffusionModelInfo.getScenarioCode()) && EmissionModeType.DIRECT.equals(entDiffusionModelInfo.getEmissionModeType())) {
            b = (Double) paramMap.get("S302");
            h = (Double) paramMap.get("S209");
        } else {
            b = (Double) paramMap.get("S306");
            h = (Double) paramMap.get("S307");
        }
        //x Direction of the velocity（Represents the average cross section velocity in a river）
        Double u = qh / b / h;
        // The distance from the discharge port to the shore
        Double a = ((Double) paramMap.get("S208"));
        // Channel roughness defaults 0.02
        Double n = 0.02;
        //K1=10.3*Qh^(-0.49)
        Double k1 = 10.3 * Math.pow(qh, -0.49);
        // Cross section longitudinal mixing coefficient Ml=63*n*u*H^0.833
        Double m1 = 63 * n * u * Math.pow(h, 0.833);
        // a medium variable m =(1+4K1*Ml/u^2 )^(1/2)
        Double M = Math.pow((1 + 4 * k1 * m1 / Math.pow(u, 2)), (1 / 2));
        //sedimentation coefficient
        Double k3 = 0.0;
        //lateral mixing coefficient
        Double my = (0.058 * h + 0.0065 * b) * Math.pow((9.81 * h), (1 / 2));

        Double m = entDiffusionModelInfo.getReleaseAmount();
        Double desity = entDiffusionModelInfo.getDensity();
        double v;
        if (ScenarioEnum.S_ONE.getCode().equals(entDiffusionModelInfo.getScenarioCode())) {
            v = m / 365 / desity;
        } else {
            v = m / desity;
        }
        if (AssessModelEnum.ONE.getCode().equals(modelId)) {
            list = Arrays.asList(exe, command, AssessModelEnum.ONE.getEnglishName(), ch + "", m + "", qh + "");
        }
        if (AssessModelEnum.TWO.getCode().equals(modelId)) {
            list = Arrays.asList(exe, command, AssessModelEnum.TWO.getEnglishName(), x, y, ch + "", m + "", h + "", my + "", u + "", b + "");
        }
        if (AssessModelEnum.THREE.getCode().equals(modelId)) {
            list = Arrays.asList(exe, command, AssessModelEnum.THREE.getEnglishName(), x, y, ch + "", m + "", h + "", my + "", u + "", b + "", a + "");
        }
        if (AssessModelEnum.FOUR.getCode().equals(modelId)) {
            list = Arrays.asList(exe, command, AssessModelEnum.FOUR.getEnglishName(), x, y, ch + "", m + "", h + "", my + "", u + "", b + "", k1 + "");
        }
        if (AssessModelEnum.FIVE.getCode().equals(modelId)) {
            list = Arrays.asList(exe, command, AssessModelEnum.FIVE.getEnglishName(), x, y, ch + "", m + "", h + "", my + "", u + "", b + "", a + "", k1 + "");
        }
        if (AssessModelEnum.SIX.getCode().equals(modelId)) {
            list = Arrays.asList(exe, command, AssessModelEnum.ELEVEN.getEnglishName(), x, ch + "", m + "", qh + "", u + "", k1 + "", k3 + "");
        }
        if (AssessModelEnum.SEVEN.getCode().equals(modelId)) {
            list = Arrays.asList(exe, command, AssessModelEnum.SEVEN.getEnglishName(), x, ch + "", m + "", qh + "", m1 + "", u + "", k1 + "");
        }
        if (AssessModelEnum.EIGHT.getCode().equals(modelId)) {
            list = Arrays.asList(exe, command, AssessModelEnum.EIGHT.getEnglishName(), x, y, ch + "", m + "", h + "", my + "", u + "");
        }
        if (AssessModelEnum.NINE.getCode().equals(modelId)) {
            list = Arrays.asList(exe, command, AssessModelEnum.NINE.getEnglishName(), x, y, ch + "", m + "", h + "", my + "", u + "", a + "");
        }
        if (AssessModelEnum.TEN.getCode().equals(modelId)) {
            list = Arrays.asList(exe, command, AssessModelEnum.TEN.getEnglishName(), x, y, ch + "", m + "", h + "", my + "", u + "", k1 + "");
        }
        if (AssessModelEnum.ELEVEN.getCode().equals(modelId)) {
            list = Arrays.asList(exe, command, AssessModelEnum.ELEVEN.getEnglishName(), x, y, ch + "", m + "", h + "", my + "", u + "", a + "", k1 + "");
        }
        if (AssessModelEnum.TWELVE.getCode().equals(modelId)) {
            list = Arrays.asList(exe, command, AssessModelEnum.TWELVE.getEnglishName(), v + "");
        }
        String[] parmass = (String[]) list.toArray();
        Process process;
        try {
            process = Runtime.getRuntime().exec(parmass);
            InputStream is = process.getInputStream();
            DataInputStream dis = new DataInputStream(is);
            String str = dis.readLine();
            process.waitFor();
            return Double.parseDouble(str);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }


    private List<GridConcentration> calSoilConcentration(EntDiffusionModelInfoVO entDiffusionModelInfo, String entId, Map<String, Object> paramMap) {
        List<GridConcentration> result = new ArrayList<>();
        List<AssessGird> assessGirdList = assessGirdService.entAssessGirdList(entId, DataModelType.SOIL_TYPE.getCode());
        AssessGird assessGird0 = assessGirdList.get(0);
        String soilType = getSoilType(assessGird0.getSandRate(), assessGird0.getClayRate());
        for (AssessGird assessGird : assessGirdList) {
            double c = calSoilModel(entDiffusionModelInfo, String.valueOf(assessGird.getX()), String.valueOf(assessGird.getY()), String.valueOf(assessGird.getZ()), String.valueOf(assessGird.getT()), paramMap, soilType);
            //c = c*(10e40);
            GridConcentration gridConcentration = buildGridConcentration(c, assessGird, entDiffusionModelInfo);
            gridConcentration.setAvgConcentration(c);
            result.add(gridConcentration);
        }
        return result;
    }

    private Double calSoilModel(EntDiffusionModelInfoVO entDiffusionModelInfo, String x, String y, String z, String t, Map<String, Object> paramMap, String soilType) {
        String address = System.getProperty("user.dir");
        int index = address.lastIndexOf("\\");
        if (index == 0) {
            index = address.lastIndexOf("/");
        }
        String newAddress = address.substring(0, index);
        String exe = "python";
      //  String command = newAddress + "\\webapps\\ROOT\\WEB-INF\\classes\\py\\Soil_Diffusion_Model.py";
        String command = filePathConfig.soilModel;

        //θ
        Double theta = Double.valueOf(paramMap.get("S212") + "");
        //density
        Double rho = Double.valueOf(paramMap.get("S213") + "");

        List<PollutantParamValue> pollutantParamValueList = pollutantParamValueService.list(new QueryWrapper<PollutantParamValue>()
                .eq("param_id", YEIGHT).eq("valid", BaseEnum.VALID_YES.getCode())
                .eq("pollutant_name", entDiffusionModelInfo.getMaterial()));
        //Type of water distribution coefficient
        Double compoundType = null;
        if(CollectionUtils.isEmpty(pollutantParamValueList)){
            compoundType = 2.0;
        }else{
            compoundType = pollutantParamValueList.get(0).getParamValue();
        }
        Double dij = 0.0;
        if ("sand".equals(soilType)) {
            dij = 1.26 * (1.0 / 100);
        }
        if ("clay".equals(soilType)) {
            dij = 2 * 0.00000000000001;
        }
        if ("loam".equals(soilType)) {
            dij = 3.96 * (1.0 / 100);
        }
        //　Kd Heavy metal of soil-water distribution coefficient 0.63;Organic matter is 1.2 　
        // k First order adsorption desorption rate constant (organic matter).06~0.084；(heavy metal)6.94*10^-6
        Double kd;
        Double k;
        if (HEAVY_METAL.equals(compoundType)) {
            kd = 0.63;
            k = RandomUtil.randomDouble(0.06, 0.084);
        } else {
            kd = 1.2;
            k = 6.94 * 0.000001;
        }

        Double M = entDiffusionModelInfo.getReleaseAmount();
        //μc First order microbial degradation rate constant in aqueous phase
        Double uc = 0.08;
        //us
        Double us = 0.004;

        List<String> list = Arrays.asList(exe, command, theta + "", dij + "", k + "", rho + "", kd + "", k + "", uc + "", us + "", M + "", x, y, z, t);
        String[] parmass = (String[]) list.toArray();
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(parmass);
            InputStream is = process.getInputStream();
            DataInputStream dis = new DataInputStream(is);
            String str = dis.readLine();
            process.waitFor();
            String[] results = str.split(" ");
            Double sx = 0.0;
            Double xfx = 0.0;
            if(!"nan".equals(results[0])){
                sx = Double.valueOf(results[0]);
            }
            if(!"nan".equals(results[1])){
                xfx = Double.valueOf(results[1]);
            }
            return sx * theta + xfx * (1 - theta);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }


    private void setSameGridAvgConcentration(double avg, int gridPointSize, int index, List<GridConcentration> gridConcentrationList) {
        for (int i = 1; i <= gridPointSize; i++) {
            gridConcentrationList.get(index - i).setAvgConcentration(avg);
        }
    }

    private GridConcentration buildGridConcentration(double c, AssessGird assessGird, EntDiffusionModelInfoVO entDiffusionModelInfo) {
        GridConcentration gridConcentration = new GridConcentration();
        Date date = DateUtil.getNowTimestamp();
        gridConcentration.setId(StringUtil.getUUID());
        gridConcentration.setCreateTime(date);
        gridConcentration.setGridId(assessGird.getId());
        gridConcentration.setInfoId(entDiffusionModelInfo.getId());
        gridConcentration.setUpdateTime(date);
        gridConcentration.setConcentration(c);
        gridConcentration.setValid((Integer) BaseEnum.VALID_YES.getCode());
        return gridConcentration;
    }


    public List<EntDiffusionModelInfo> entDiffusionModelInfoList(String entId) {
        List<EntDiffusionModelInfo> entDiffusionModelInfos = entDiffusionModelInfoService.list(new QueryWrapper<EntDiffusionModelInfo>()
                .eq("ent_id", entId).eq("valid", BaseEnum.VALID_YES.getCode()));
        return entDiffusionModelInfos;
    }


}
