package com.shencai.eil.scenario.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shencai.eil.scenario.entity.ScenarioSelectionInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shencai.eil.scenario.model.AccidentScenarioResultVO;
import com.shencai.eil.scenario.model.ScenarioSelectionInfoQueryParam;
import com.shencai.eil.scenario.model.ScenarioSelectionInfoVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author fanhj
 * @since 2018-10-12
 */
public interface ScenarioSelectionInfoMapper extends BaseMapper<ScenarioSelectionInfo> {
    List<ScenarioSelectionInfoVO> pageScenarioSelectionInfo(Page<ScenarioSelectionInfoVO> page
            , @Param("param") ScenarioSelectionInfoQueryParam queryParam);
}
