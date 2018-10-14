package com.shencai.eil.scenario.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shencai.eil.scenario.entity.ScenarioSelectionInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shencai.eil.scenario.model.ScenarioSelectionInfoQueryParam;
import com.shencai.eil.scenario.model.ScenarioSelectionInfoVO;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author fanhj
 * @since 2018-10-12
 */
public interface IScenarioSelectionInfoService extends IService<ScenarioSelectionInfo> {
    void initScenarioSelectionInfo(String enterpriseId);
    Page<ScenarioSelectionInfoVO> pageScenarioSelectionInfo(ScenarioSelectionInfoQueryParam queryParam);
}
