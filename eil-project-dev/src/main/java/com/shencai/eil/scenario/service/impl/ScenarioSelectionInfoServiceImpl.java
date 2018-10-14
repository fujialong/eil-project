package com.shencai.eil.scenario.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shencai.eil.scenario.entity.ScenarioSelectionInfo;
import com.shencai.eil.scenario.mapper.ScenarioSelectionInfoMapper;
import com.shencai.eil.scenario.model.ScenarioSelectionInfoQueryParam;
import com.shencai.eil.scenario.model.ScenarioSelectionInfoVO;
import com.shencai.eil.scenario.service.IScenarioSelectionInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author fanhj
 * @since 2018-10-12
 */
@Service
public class ScenarioSelectionInfoServiceImpl extends ServiceImpl<ScenarioSelectionInfoMapper, ScenarioSelectionInfo> implements IScenarioSelectionInfoService {
    @Autowired
    private ScenarioSelectionInfoMapper scenarioSelectionInfoMapper;

    @Override
    public void initScenarioSelectionInfo(String enterpriseId) {
        
    }

    @Override
    public Page<ScenarioSelectionInfoVO> pageScenarioSelectionInfo(ScenarioSelectionInfoQueryParam queryParam) {
        Page<ScenarioSelectionInfoVO> page = new Page<>();
        page.setCurrent(queryParam.getCurrent());
        page.setSize(queryParam.getSize());
        page.setRecords(scenarioSelectionInfoMapper.pageScenarioSelectionInfo(page, queryParam));
        return page;
    }
}
