package com.shencai.eil.survey.service;

import com.shencai.eil.survey.entity.EntSurveyExtendInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shencai.eil.survey.model.EntSurveyExtendInfoVO;
import com.shencai.eil.survey.model.SurveyExtendItemVO;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author fanhj
 * @since 2018-10-18
 */
public interface IEntSurveyExtendInfoService extends IService<EntSurveyExtendInfo> {
    SurveyExtendItemVO getSurveyExtendItemDisplayStatus(String enterpriseId);

    void saveSurveyExtendItem(EntSurveyExtendInfoVO info);

    EntSurveyExtendInfoVO getSurveyExtendItem(String enterpriseId);
}
