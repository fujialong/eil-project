package com.shencai.eil.survey.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shencai.eil.common.constants.BaseEnum;
import com.shencai.eil.common.constants.FileSourceType;
import com.shencai.eil.common.constants.GradeTemplateParamType;
import com.shencai.eil.common.utils.CommonsUtil;
import com.shencai.eil.common.utils.DateUtil;
import com.shencai.eil.common.utils.StringUtil;
import com.shencai.eil.exception.BusinessException;
import com.shencai.eil.survey.constants.FinalReportParam;
import com.shencai.eil.survey.entity.EntSurveyExtendInfo;
import com.shencai.eil.survey.entity.EntSurveyPhoto;
import com.shencai.eil.survey.mapper.EntSurveyExtendInfoMapper;
import com.shencai.eil.survey.mapper.EntSurveyPhotoMapper;
import com.shencai.eil.survey.mapper.EntSurveyPlanMapper;
import com.shencai.eil.survey.mapper.GradeTemplateParamMapper;
import com.shencai.eil.survey.model.*;
import com.shencai.eil.survey.service.IEntSurveyExtendInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shencai.eil.survey.service.IEntSurveyPhotoService;
import com.shencai.eil.system.entity.BaseFileupload;
import com.shencai.eil.system.mapper.BaseFileuploadMapper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author fanhj
 * @since 2018-10-18
 */
@Service
public class EntSurveyExtendInfoServiceImpl extends ServiceImpl<EntSurveyExtendInfoMapper, EntSurveyExtendInfo> implements IEntSurveyExtendInfoService {

    @Autowired
    private GradeTemplateParamMapper gradeTemplateParamMapper;
    @Autowired
    private EntSurveyPlanMapper entSurveyPlanMapper;
    @Autowired
    private EntSurveyExtendInfoMapper entSurveyExtendInfoMapper;
    @Autowired
    private EntSurveyPhotoMapper entSurveyPhotoMapper;
    @Autowired
    private IEntSurveyPhotoService entSurveyPhotoService;
    @Autowired
    private BaseFileuploadMapper baseFileuploadMapper;

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
    private static final int RISK_LEVEL_HIDDEN_LIMIT = 3;
    private static final List<String> CONTROL_RISK_LEVEL_LIST
            = Arrays.asList(new String[]{"unloadInvolved", "unloadSpecify", "hasSafeBasin", "autoInBasin", "hasCofferdam",
            "hasStorage", "hasAlarmSystem", "professionalHandled", "empSpecify", "hasFireDivice"});
    private static final int CONTROL_RISK_LEVEL_HIDDEN_LIMIT = 6;
    private static final String RECEPTOR_RISK_LEVEL_FLAG = "receptor_risk_level";
    private static final List<String> RECEPTOR_RISK_LEVEL_LIST
            = Arrays.asList(new String[]{"hasSurfaceWater", "hasHospital", "hasSchool", "hasCommunity", "hasFarm"});
    private static final int RECEPTOR_RISK_LEVEL_HIDDEN_LIMIT = 1;
    private static final String YES = "1";
    private static final boolean SHOW = true;
    private static final boolean HIDDEN = false;

    @Override
    public SurveyExtendItemVO getSurveyExtendItemDisplayStatus(String enterpriseId) {
        SurveyExtendItemVO surveyExtendItemVO = initSurveyExtendItem();
        checkStatus(enterpriseId, surveyExtendItemVO);
        return surveyExtendItemVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSurveyExtendItem(EntSurveyExtendInfoVO info) {
        if (StringUtil.isNotBlank(info.getId())) {
            updateSurveyExtendInfo(info);
            deletePhotos(info);
            if (CollectionUtils.isNotEmpty(info.getPhotoList())) {
                savePhotos(info, info.getId());
            }
        } else {
            EntSurveyExtendInfo entSurveyExtendInfo = saveExtendInfo(info);
            if (CollectionUtils.isNotEmpty(info.getPhotoList())) {
                savePhotos(info, entSurveyExtendInfo.getId());
            }
            if (CollectionUtils.isNotEmpty(info.getLicenceFileIdList())) {
                updateLicenceFile(info);
            }
        }
    }

    private void updateLicenceFile(EntSurveyExtendInfoVO info) {
        BaseFileupload baseFileupload = new BaseFileupload();
        baseFileupload.setSourceId(info.getEnterpriseId());
        baseFileuploadMapper.update(baseFileupload, new QueryWrapper<BaseFileupload>().in("id", info.getLicenceFileIdList()));
    }

    private void deletePhotos(EntSurveyExtendInfoVO info) {
        EntSurveyPhoto entSurveyPhoto = new EntSurveyPhoto();
        entSurveyPhoto.setUpdateTime(DateUtil.getNowTimestamp());
        entSurveyPhoto.setValid((Integer) BaseEnum.VALID_NO.getCode());
        entSurveyPhotoMapper.update(entSurveyPhoto
                , new QueryWrapper<EntSurveyPhoto>()
                .eq("survey_info_id", info.getId())
                .eq("valid", BaseEnum.VALID_YES.getCode()));
    }

    private void updateSurveyExtendInfo(EntSurveyExtendInfoVO info) {
        EntSurveyExtendInfo entSurveyExtendInfo = new EntSurveyExtendInfo();
        CommonsUtil.cloneObj(info, entSurveyExtendInfo);
        entSurveyExtendInfo.setEntId(info.getEnterpriseId());
        entSurveyExtendInfo.setUpdateTime(DateUtil.getNowTimestamp());
        entSurveyExtendInfoMapper.updateById(entSurveyExtendInfo);
    }

    private void savePhotos(EntSurveyExtendInfoVO info, String surveyItemId) {
        List<EntSurveyPhoto> photoList = new ArrayList<>();
        for (EntSurveyPhotoVO photo: info.getPhotoList()) {
            EntSurveyPhoto entSurveyPhoto = new EntSurveyPhoto();
            CommonsUtil.cloneObj(photo, entSurveyPhoto);
            entSurveyPhoto.setId(StringUtil.getUUID());
            entSurveyPhoto.setSurveyInfoId(surveyItemId);
            entSurveyPhoto.setCreateTime(DateUtil.getNowTimestamp());
            entSurveyPhoto.setValid((Integer) BaseEnum.VALID_YES.getCode());
            photoList.add(entSurveyPhoto);
        }
        if (CollectionUtils.isNotEmpty(photoList)) {
            entSurveyPhotoService.saveBatch(photoList);
        }
    }

    private EntSurveyExtendInfo saveExtendInfo(EntSurveyExtendInfoVO info) {
        EntSurveyExtendInfo entSurveyExtendInfo = new EntSurveyExtendInfo();
        CommonsUtil.cloneObj(info, entSurveyExtendInfo);
        entSurveyExtendInfo.setId(StringUtil.getUUID());
        entSurveyExtendInfo.setEntId(info.getEnterpriseId());
        entSurveyExtendInfo.setSurveyTime(DateUtil.getNowTimestamp());
        entSurveyExtendInfo.setCreateTime(DateUtil.getNowTimestamp());
        entSurveyExtendInfo.setUpdateTime(DateUtil.getNowTimestamp());
        entSurveyExtendInfo.setValid((Integer) BaseEnum.VALID_YES.getCode());
        entSurveyExtendInfoMapper.insert(entSurveyExtendInfo);
        return entSurveyExtendInfo;
    }

    @Override
    public EntSurveyExtendInfoVO getSurveyExtendItem(String enterpriseId) {
        EntSurveyExtendInfo info= getSurveyExtendInfo(enterpriseId);
        EntSurveyExtendInfoVO vo = new EntSurveyExtendInfoVO();
        CommonsUtil.cloneObj(info, vo);
        setPhotoList(info, vo);
        return vo;
    }

    private EntSurveyExtendInfo getSurveyExtendInfo(String enterpriseId) {
        List<EntSurveyExtendInfo> infoList = entSurveyExtendInfoMapper
                .selectList(new QueryWrapper<EntSurveyExtendInfo>()
                        .eq("ent_id", enterpriseId)
                        .eq("valid", BaseEnum.VALID_YES.getCode()));
        if (CollectionUtils.isEmpty(infoList)) {
            throw new BusinessException("survey extend info is not exist!");
        }
        return infoList.get(0);
    }

    private void setPhotoList(EntSurveyExtendInfo info, EntSurveyExtendInfoVO vo) {
        EntSurveyPhotoQueryParam queryParam = new EntSurveyPhotoQueryParam();
        queryParam.setSurveyInfoId(info.getId());
        List<EntSurveyPhotoVO> photoList = entSurveyPhotoMapper.listSurveyPhoto(queryParam);
        vo.setPhotoList(photoList);
    }

    private void checkStatus(String enterpriseId, SurveyExtendItemVO surveyExtendItemVO) {
        HashMap<String, Integer> needSpecialHandleMap = new HashMap<>();
        List<GradeTemplateParamVO> paramList = gradeTemplateParamMapper.listParamOfSceneSurvey(enterpriseId);
        for (GradeTemplateParamVO param: paramList) {
            if (CHAIN_EFFECTIVE.equals(param.getCode())) {
                if (YES.equals(param.getResultCode())) {
                    surveyExtendItemVO.setHasChainEffective(HIDDEN);
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
        }
        setIntensiveParam(enterpriseId, needSpecialHandleMap);
        if (needSpecialHandleMap.containsKey(RISK_LEVEL_FLAG)
                && needSpecialHandleMap.get(RISK_LEVEL_FLAG) >= RISK_LEVEL_HIDDEN_LIMIT) {
            surveyExtendItemVO.setHasRiskLevel(HIDDEN);
        }
        if (needSpecialHandleMap.containsKey(CONTROL_RISK_LEVEL_FLAG)
                && needSpecialHandleMap.get(CONTROL_RISK_LEVEL_FLAG) >= CONTROL_RISK_LEVEL_HIDDEN_LIMIT) {
            surveyExtendItemVO.setHasRiskLevel(HIDDEN);
        }
        if (needSpecialHandleMap.containsKey(RECEPTOR_RISK_LEVEL_FLAG)
                && needSpecialHandleMap.get(RECEPTOR_RISK_LEVEL_FLAG) >= RECEPTOR_RISK_LEVEL_HIDDEN_LIMIT) {
            surveyExtendItemVO.setHasRiskLevel(HIDDEN);
        }
        int fileCount = baseFileuploadMapper.selectCount(new QueryWrapper<BaseFileupload>()
                .eq("stype", FileSourceType.LICENCE.getCode())
                .eq("source_id", enterpriseId)
                .eq("valid", BaseEnum.VALID_YES.getCode()));
        if (fileCount > 0) {
            surveyExtendItemVO.setHasLicence(HIDDEN);
        }
    }

    private SurveyExtendItemVO initSurveyExtendItem() {
        SurveyExtendItemVO surveyExtendItemVO = new SurveyExtendItemVO();
        surveyExtendItemVO.setHasChainEffective(SHOW);
        surveyExtendItemVO.setHasRiskLevel(SHOW);
        surveyExtendItemVO.setHasControlRiskLevel(SHOW);
        surveyExtendItemVO.setHasReceptorRiskLevel(SHOW);
        surveyExtendItemVO.setHasLicence(SHOW);
        return surveyExtendItemVO;
    }

    private void setIntensiveParam(String enterpriseId, HashMap<String, Integer> needSpecialHandleMap) {
        List<SurveyResultVO> componentResultList = entSurveyPlanMapper.listResultOfIntensiveParamsByRow(enterpriseId);
        boolean hasUndergroundStorage = false;
        boolean hasUndergroundPipe = false;
        boolean unloadSpecify = false;
        boolean existAllCondition = false;
        if (CollectionUtils.isNotEmpty(componentResultList)) {
            for (SurveyResultVO vo: componentResultList) {
                if (!hasUndergroundStorage && COMPONENT_LOCATION_VALID.equals(vo.getComponentLocation())
                        && COMPONENT_TYPE_OF_STORAGE_LIST.contains(vo.getComponentType())) {
                    hasUndergroundStorage = true;
                } else if (!hasUndergroundPipe && COMPONENT_LOCATION_VALID.equals(vo.getComponentLocation())
                        && COMPONENT_TYPE_OF_PIPE_LIST.contains(vo.getComponentType())) {
                    hasUndergroundPipe = true;
                } else if (!unloadSpecify && OPTION_VALUE_YES.equals(vo.getWasteCollection())
                        && OPTION_VALUE_YES.equals(vo.getWastePrevent())) {
                    unloadSpecify = true;
                } else if (!existAllCondition && OPTION_VALUE_YES.equals(vo.getHasFireAlarmSystem())
                        && OPTION_VALUE_YES.equals(vo.getHasOutFireSystem())) {
                    existAllCondition = true;
                }
            }
        }

        if (!hasUndergroundStorage) {
            addCount(needSpecialHandleMap, RISK_LEVEL_FLAG);
        }
        if (!hasUndergroundPipe) {
            addCount(needSpecialHandleMap, RISK_LEVEL_FLAG);
        }
        if (!existAllCondition) {
            addCount(needSpecialHandleMap, CONTROL_RISK_LEVEL_FLAG);
        }
        if (!unloadSpecify) {
            addCount(needSpecialHandleMap, CONTROL_RISK_LEVEL_FLAG);
        }
    }

    private void addCount(HashMap<String, Integer> needSpecialHandleMap, String receptorRiskLevelFlag) {
        if (!needSpecialHandleMap.containsKey(receptorRiskLevelFlag)) {
            needSpecialHandleMap.put(receptorRiskLevelFlag, 0);
        }
        int total = needSpecialHandleMap.get(receptorRiskLevelFlag);
        total++;
        needSpecialHandleMap.put(receptorRiskLevelFlag, total);
    }
}
