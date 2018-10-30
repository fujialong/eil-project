package com.shencai.eil.assessment.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shencai.eil.assessment.entity.AssessGird;
import com.shencai.eil.assessment.entity.EntDiffusionModelInfo;
import com.shencai.eil.assessment.mapper.AssessGirdMapper;
import com.shencai.eil.assessment.model.AssessGirdParam;
import com.shencai.eil.assessment.model.AssessGridChildParam;
import com.shencai.eil.assessment.model.AssessGridVO;
import com.shencai.eil.assessment.service.IAssessGirdService;
import com.shencai.eil.assessment.service.IEntAssessResultService;
import com.shencai.eil.assessment.service.IEntDiffusionModelInfoService;
import com.shencai.eil.assessment.service.IWaterModelCalculateService;
import com.shencai.eil.common.constants.BaseEnum;
import com.shencai.eil.common.constants.DataModelType;
import com.shencai.eil.common.constants.StatusEnum;
import com.shencai.eil.common.utils.DateUtil;
import com.shencai.eil.common.utils.ObjectUtil;
import com.shencai.eil.common.utils.StringUtil;
import com.shencai.eil.exception.BusinessException;
import com.shencai.eil.policy.entity.EnterpriseInfo;
import com.shencai.eil.policy.mapper.EnterpriseInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author fanhj
 * @since 2018-10-21
 */
@Service
public class AssessGirdServiceImpl extends ServiceImpl<AssessGirdMapper, AssessGird> implements IAssessGirdService {

    @Autowired
    private IEntAssessResultService entAssessResultService;
    @Autowired
    private IAssessGirdService assessGirdService;
    @Autowired
    private IWaterModelCalculateService waterModelCalculateService;
    @Autowired
    private IEntDiffusionModelInfoService entDiffusionModelInfoService;
    @Autowired
    private EnterpriseInfoMapper enterpriseInfoMapper;
    private static final int WADING = 1;
    private static final int NO_WADING = 0;


    @Override
    public void saveAssessGrid(List<AssessGirdParam> paramList) {
        for (AssessGirdParam param : paramList) {
            if (DataModelType.WATER_TYPE.getCode().equals(param.getBisCode())) {
                saveWaterAssessGrid(param);
            }
            if (DataModelType.SOIL_TYPE.getCode().equals(param.getBisCode())) {
                saveSoilAssessGrid(param);
            }
        }
        AssessGirdParam param0 = paramList.get(0);
        EnterpriseInfo enterprise = new EnterpriseInfo();
        enterprise.setStatus(StatusEnum.IN_DAMAGE_ASSESSMENT.getCode());
        enterprise.setUpdateTime(DateUtil.getNowTimestamp());
        enterpriseInfoMapper.update(enterprise, new QueryWrapper<EnterpriseInfo>()
                .eq("id", param0.getEntId())
                .eq("valid", BaseEnum.VALID_YES.getCode()));
       /* AssessStartModelCalculateParam assessStartModelCalculateParam = new AssessStartModelCalculateParam();
        assessStartModelCalculateParam.setEntId(param.getEntId());
        assessStartModelCalculateParam.setMaterialName(param.getEntId());

        waterModelCalculateService.saveToGridConcentration(assessStartModelCalculateParam);*/
    }

    private void saveWaterAssessGrid(AssessGirdParam param) {
        List<AssessGird> allAssessGridList = new ArrayList<>();
        AssessGridVO assessGridVO = entAssessResultService.gisBaseInfo(param.getEntId());
        Double riverWidth = assessGridVO.getRiverWidth() / 1000;
        Double distanceOutlet = assessGridVO.getDistanceOfOutlet();
        if (CollectionUtil.isEmpty(param.getChildParamList())) {
            throw new BusinessException("childParamList is null,please check gis data");
        }
        int n = 1;
        int gridCode = 1;
        double lastX = 0.0;
        AssessGird assessGird0 = null;
        for (AssessGridChildParam assessGridChildParam : param.getChildParamList()) {
            AssessGird assessGird = builderAssessGrid(param, assessGridChildParam);
            assessGird.setPointSortNum(n);
            assessGird.setGridCode(gridCode);
            allAssessGridList.add(assessGird);
            if (n == 1) {
                assessGird.setX(0.0);
                assessGird.setY(0.0);
                lastX = 0.0;
                assessGird0 = assessGird;
            } else {
                if (assessGridChildParam.getWaterType().equals(DataModelType.SINGLE.getCode())) {
                    double x = assessGridChildParam.getLen() + lastX;
                    double y = 0.0;
                    assessGird.setX(x);
                    assessGird.setY(y);
                    assessGird.setAcreage(assessGridChildParam.getLen()*riverWidth);
                    lastX = x;
                }
                if (assessGridChildParam.getWaterType().equals(DataModelType.DOUBLE.getCode())) {
                    double x = assessGridChildParam.getLen() + lastX;
                    double y = (riverWidth / 2) - distanceOutlet;
                    double acreage = assessGridChildParam.getLen()*riverWidth;
                    assessGird.setX(x);
                    assessGird.setY(y);
                    assessGird.setAcreage(acreage);
                    lastX = x;
                    n++;
                    AssessGird assessGird1 = builderAssessGrid(param, assessGridChildParam);
                    assessGird1.setX(assessGird.getX());
                    assessGird1.setY(-distanceOutlet);
                    assessGird1.setPointSortNum(n);
                    assessGird1.setGridCode(gridCode);
                    assessGird1.setAcreage(acreage);
                    allAssessGridList.add(assessGird1);
                    n++;
                    AssessGird assessGird2 = builderAssessGrid(param, assessGridChildParam);
                    assessGird2.setX(assessGird.getX());
                    assessGird2.setY(riverWidth - distanceOutlet);
                    assessGird2.setPointSortNum(n);
                    assessGird2.setGridCode(gridCode);
                    assessGird2.setAcreage(acreage);
                    allAssessGridList.add(assessGird2);
                }
                if (assessGridChildParam.getWaterType().equals(DataModelType.LAKER.getCode())) {
                    double x = assessGird.getLon() - assessGird0.getLon();
                    double y = assessGird.getLat() - assessGird0.getLat();
                    assessGird.setX(x);
                    assessGird.setY(y);
                }
            }
            gridCode++;
            n++;
        }
        assessGirdService.saveBatch(allAssessGridList);
    }


    private void saveSoilAssessGrid(AssessGirdParam param) {
        List<AssessGridChildParam> children = param.getChildParamList();
        if (CollectionUtil.isEmpty(children)) {
            throw new BusinessException("childParamList is null,please check gis data");
        }
        List<AssessGird> assessGirdList = assessGirdService.entAssessGirdList(param.getEntId(), DataModelType.SOIL_TYPE.getCode());
        for (int i = 0; i < assessGirdList.size(); i++) {
            AssessGird assessGird = assessGirdList.get(i);
            AssessGridChildParam childParam;
            if (i == 0 || i == 2) {
                childParam = children.get(0);
            } else {
                childParam = children.get(1);
            }
            assessGird.setGridCode(i+1);
            assessGird.setClayRate(param.getClayRate());
            assessGird.setPowderRate(param.getPowderRate());
            assessGird.setSandRate(param.getSandRate());
            assessGird.setEcoValue(childParam.getEcoValue());
            assessGird.setPop(childParam.getPop());
            assessGird.setLon(childParam.getLon());
            assessGird.setLat(childParam.getLat());
            assessGird.setForestryArea(childParam.getForestryArea());
            assessGird.setAgriculturalArea(childParam.getAgriculturalArea());
            assessGird.setSensitiveArea(childParam.getSensitiveArea());
        }
        assessGirdService.updateBatchById(assessGirdList);
    }

    @Override
    public List<AssessGird> entAssessGirdList(String entId, String bisCode) {
        return this.list(new QueryWrapper<AssessGird>()
                .eq("ent_id", entId)
                .eq("bis_code", bisCode)
                .eq("valid", BaseEnum.VALID_YES.getCode())
                .orderByAsc("point_sort_num"));
    }

    private AssessGird builderAssessGrid(AssessGirdParam param, AssessGridChildParam assessGridChildParam) {
        AssessGird assessGird = new AssessGird();
        Date date = DateUtil.getNowTimestamp();
        assessGird.setId(StringUtil.getUUID());
        assessGird.setSensitiveArea(assessGridChildParam.getSensitiveArea());
        assessGird.setBisCode(param.getBisCode());
        assessGird.setEntId(param.getEntId());
        assessGird.setPointSortNum(assessGridChildParam.getPointSortNum());
        assessGird.setEcoValue(assessGridChildParam.getEcoValue());
        assessGird.setValid((Integer) BaseEnum.VALID_YES.getCode());
        assessGird.setLat(assessGridChildParam.getLat());
        assessGird.setLon(assessGridChildParam.getLon());
        assessGird.setSandRate(param.getSandRate());
        assessGird.setClayRate(param.getClayRate());
        assessGird.setPowderRate(param.getPowderRate());
        assessGird.setPop(assessGridChildParam.getPop());
        assessGird.setWaterType(assessGridChildParam.getWaterType());
        assessGird.setFisheryArea(assessGridChildParam.getFisheryArea());
        assessGird.setAgriculturalArea(assessGridChildParam.getAgriculturalArea());
        assessGird.setWaterQuality(assessGridChildParam.getWaterQuality() + "");
        assessGird.setUpdateTime(date);
        return assessGird;
    }

    @Override
    public AssessGridVO gisBaseInfo(String entId) {
        assessGirdService.remove(new QueryWrapper<AssessGird>().eq("ent_id", entId)
                .eq("valid", BaseEnum.VALID_YES.getCode()).eq("bis_code", "water"));
        AssessGridVO assessGridVO = entAssessResultService.gisBaseInfo(entId);
        List<EntDiffusionModelInfo> entDiffusionModelInfos = entDiffusionModelInfoService.list(new QueryWrapper<EntDiffusionModelInfo>()
                .eq("considering_water_env_risk", WADING)
                .eq("valid", BaseEnum.VALID_YES.getCode()).eq("ent_id", entId));

        if (!ObjectUtil.isEmpty(assessGridVO) && !CollectionUtils.isEmpty(entDiffusionModelInfos)) {
            assessGridVO.setWading(WADING);
        }
        if (!ObjectUtil.isEmpty(assessGridVO) && CollectionUtils.isEmpty(entDiffusionModelInfos)) {
            assessGridVO.setWading(NO_WADING);
        }
        return assessGridVO;
    }

}
