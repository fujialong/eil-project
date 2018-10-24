package com.shencai.eil.scenario.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shencai.eil.assessment.entity.AssessGird;
import com.shencai.eil.assessment.entity.EntDiffusionModelInfo;
import com.shencai.eil.assessment.entity.GridConcentration;
import com.shencai.eil.assessment.service.IAssessGirdService;
import com.shencai.eil.assessment.service.IEntDiffusionModelInfoService;
import com.shencai.eil.assessment.service.IGridConcentrationService;
import com.shencai.eil.common.constants.AssessModelEnum;
import com.shencai.eil.common.constants.BaseEnum;
import com.shencai.eil.common.utils.DateUtil;
import com.shencai.eil.common.utils.ObjectUtil;
import com.shencai.eil.common.utils.StringUtil;
import com.shencai.eil.exception.BusinessException;
import com.shencai.eil.scenario.mapper.ScenarioSelectionInfoMapper;
import com.shencai.eil.scenario.model.ScenarioSelectionInfoKeyVavlueVO;
import com.shencai.eil.scenario.service.IWaterModelCalculateService;
import com.shencai.eil.survey.service.IEntSurveyPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @program: eil-project
 * @description:
 * @author: fujl
 * @create: 2018-10-19 14:54
 **/
@Service
public class WaterModelCalculateServiceImpl implements IWaterModelCalculateService {

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

    private double n, cp, ch, qh, b, h, a, u, k1, M, k3, v, m1, my;

    /**
     * Model of S1
     */
    private double waterModelSone(String entId) {
        Double concentrationOfPollutant = Double.valueOf(entSurveyPlanService.getValue(entId, Arrays.asList("M47")));
        Double concentrationOfWater = Double.valueOf(entSurveyPlanService.getValue(entId, Arrays.asList("M39", "M42")));
        if (concentrationOfWater == 0) {
            return 0;
        }
        return concentrationOfPollutant / concentrationOfWater;
    }

    /**
     * Model of S2
     */
    private double waterModeStwo(String entId) {
        return 0;
    }

    /**
     * Model of S3
     */
    private double waterModeSthree(String entId) {
        return 0;
    }

    private void saveToGridConcentration(String entId, String materialName) {
        installParams(entId, materialName);
        List<EntDiffusionModelInfo> entDiffusionModelInfos = entDiffusionModelInfoList(entId);
        List<GridConcentration> gridConcentrations = new ArrayList<>();

        for (EntDiffusionModelInfo entDiffusionModelInfo : entDiffusionModelInfos) {
            for (AssessGird assessGird : entAssessGirdList(entId)) {
                double c = calculateContans(entDiffusionModelInfo.getModelId(), String.valueOf(assessGird.getX()),
                        String.valueOf(assessGird.getY()));
                GridConcentration gridConcentration = buildGridConcentration(c, assessGird, entDiffusionModelInfo);
                gridConcentrations.add(gridConcentration);
            }
        }
        gridConcentrationService.saveBatch(gridConcentrations);
    }

    private GridConcentration buildGridConcentration(double c, AssessGird assessGird, EntDiffusionModelInfo entDiffusionModelInfo) {
        GridConcentration gridConcentration = new GridConcentration();
        Date date = DateUtil.getNowTimestamp();
        gridConcentration.setId(StringUtil.getUUID());
        gridConcentration.setCreateTime(date);
        gridConcentration.setGridId(assessGird.getId());
        gridConcentration.setInfoId(entDiffusionModelInfo.getId());
        gridConcentration.setUpdateTime(date);
        gridConcentration.setConcentration(c);
        //TODO 网格平均浓度 - 根据ppt里的算法
        gridConcentration.setAvgConcentration(0.001);
        gridConcentration.setValid((Integer) BaseEnum.VALID_YES.getCode());
        return gridConcentration;
    }

    /**
     * type 情景类别
     */
    private double calculateContans(String modelId, String x, String y) {
        Double str = getModelResultDouble(modelId, x, y);
        if (str != null) {
            return str;
        }
        return 0;
    }

    private Double getModelResultDouble(String modelId, String x, String y) {
        String address = System.getProperty("user.dir");
        String exe = "python";
        String command = address + "\\eil-project-dev\\src\\main\\resources\\py\\Water_Model1.py";
        List<String> list = null;
        if (AssessModelEnum.ONE.getCode().equals(modelId)) {
            list = Arrays.asList(exe, command, AssessModelEnum.ONE.getEnglishName(), ch + "", M + "", qh + "");
        }
        if (AssessModelEnum.TWO.getCode().equals(modelId)) {
            list = Arrays.asList(exe, command, AssessModelEnum.TWO.getEnglishName(), x, y, ch + "", M + "", h + "", my + "", u + "", b + "");
        }
        if (AssessModelEnum.THREE.getCode().equals(modelId)) {
            list = Arrays.asList(exe, command, AssessModelEnum.THREE.getEnglishName(), x, y, ch + "", M + "", h + "", my + "", u + "", b + "", a + "");
        }
        if (AssessModelEnum.FOUR.getCode().equals(modelId)) {
            list = Arrays.asList(exe, command, AssessModelEnum.FOUR.getEnglishName(), x, y, ch + "", M + "", h + "", my + "", u + "", b + "", k1 + "");
        }
        if (AssessModelEnum.FIVE.getCode().equals(modelId)) {
            list = Arrays.asList(exe, command, AssessModelEnum.FIVE.getEnglishName(), x, y, ch + "", M + "", h + "", my + "", u + "", b + "", a + "", k1 + "");
        }
        if (AssessModelEnum.SEVEN.getCode().equals(modelId)) {
            list = Arrays.asList(exe, command, AssessModelEnum.SEVEN.getEnglishName(), x, ch + "", M + "", qh + "", m1 + "", u + "", k1 + "");
        }
        if (AssessModelEnum.EIGHT.getCode().equals(modelId)) {
            list = Arrays.asList(exe, command, AssessModelEnum.EIGHT.getEnglishName(), x, y, ch + "", M + "", h + "", my + "", u + "");
        }
        if (AssessModelEnum.NINE.getCode().equals(modelId)) {
            list = Arrays.asList(exe, command, AssessModelEnum.NINE.getEnglishName(), x, y, ch + "", M + "", h + "", my + "", u + "", a + "");
        }
        if (AssessModelEnum.TEN.getCode().equals(modelId)) {
            list = Arrays.asList(exe, command, AssessModelEnum.TEN.getEnglishName(), x, y, ch + "", M + "", h + "", my + "", u + "", k1 + "");
        }
        if (AssessModelEnum.ELEVEN.getCode().equals(modelId)) {
            list = Arrays.asList(exe, command, AssessModelEnum.ELEVEN.getEnglishName(), x, y, ch + "", M + "", h + "", my + "", u + "", a + "", k1 + "");
        }
        if (AssessModelEnum.TWELVE.getCode().equals(modelId)) {
            list = Arrays.asList(exe, command, AssessModelEnum.TWELVE.getEnglishName(), v + "");
        }
        if (AssessModelEnum.SIX.getCode().equals(modelId)) {
            throw new BusinessException("this model are being developed");
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

    public void installParams(String entId, String materialName) {
        //m = 污染物在计算后的值
        //double m = 0;
        //河流上游污染物浓度或湖（库）、海中污染物现状浓
        ch = 0;
        //河流流量或湖水流出量
        qh = Double.valueOf(entSurveyPlanService.getValue(entId, Arrays.asList("S308", "S301")));
        // 河流宽度
        b = Double.valueOf(entSurveyPlanService.getValue(entId, Arrays.asList("S306")));
        // 平均水深
        h = Double.valueOf(entSurveyPlanService.getValue(entId, Arrays.asList("S307")));
        //x方向流速（表示河流中断面平均流速）
        u = qh / b / h;
        // 排放口到岸边的距离
        a = Double.valueOf(entSurveyPlanService.getValue(entId, Arrays.asList("S208")));
        // 河道糙率 默认值0.02
        n = 0.02;
        //K1=10.3*Qh^(-0.49)
        k1 = 10.3 * Math.pow(qh, -0.49);
        // 断面纵向混合系数 Ml=63*n*u*H^0.833
        m1 = 63 * n * u * Math.pow(h, 0.833);
        // 中间变量 m =(1+4K1*Ml/u^2 )^(1/2)
        M = Math.pow((1 + 4 * k1 * m1 / Math.pow(u, 2)), (1 / 2));
        //沉降系数
        k3 = 0;
        // V=m/ρ溢油总体积 事故总风险-物质释放量（此处为总量），密度数据来自风险物质数据库，计算，V=m/ρ
        //对于事故情景S1，排放总量须除以365 (),密度：scenario_selection_info表中找
        ScenarioSelectionInfoKeyVavlueVO scenarioSelectionInfoKeyVavlueVO = scenarioSelectionInfoMapper.getToalMaterialRelease(entId, materialName);
        Double m = null;
        if (ObjectUtil.isEmpty(scenarioSelectionInfoKeyVavlueVO)) {
            //default Value
            v = 0.02;
        } else {
            m = scenarioSelectionInfoKeyVavlueVO.getVoDoubleValue() == null ? 0.02 : scenarioSelectionInfoKeyVavlueVO.getVoDoubleValue();
            double desity = scenarioSelectionInfoKeyVavlueVO.getDensity() == null ? 1 : Double.parseDouble(scenarioSelectionInfoKeyVavlueVO.getDensity());
            v = m / desity;
        }
        my = 0;
    }

    /**
     * Generating linear coordinates
     *
     * @param lenList
     * @return
     */
    public List<Map<String, Double>> generateLineCoordinate(List<Double> lenList) {
        double nextLen = 0;

        List<Map<String, Double>> lineCoordinateList = new ArrayList<>();
        for (Double len : lenList) {
            nextLen += len;
            buildLineMapToList(nextLen,lineCoordinateList);
        }
        return lineCoordinateList;
    }

    private void buildLineMapToList(double nextLen, List<Map<String, Double>> lineCoordinateList) {
        Map<String, Double> map = new HashMap<>();
        map.put("x", nextLen);
        map.put("y", 0.0);
        lineCoordinateList.add(map);
    }

    public List<EntDiffusionModelInfo> entDiffusionModelInfoList(String entId) {
        // 污染物排放浓度 强源计算 cp
        List<EntDiffusionModelInfo> entDiffusionModelInfos = entDiffusionModelInfoService.list(new QueryWrapper<EntDiffusionModelInfo>()
                .eq("ent_id", entId));
        return entDiffusionModelInfos;
    }

    public List<AssessGird> entAssessGirdList(String entId) {
        return assessGirdService.list(new QueryWrapper<AssessGird>()
                .eq("ent_id", entId).eq("valid", BaseEnum.VALID_YES.getCode()));
    }

}
