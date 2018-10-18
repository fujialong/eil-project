package com.shencai.eil.scenario.mapper;

import com.shencai.eil.scenario.entity.AccidentScenario;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shencai.eil.scenario.model.AccidentScenarioQueryParam;
import com.shencai.eil.scenario.model.AccidentScenarioVO;

import java.util.List;

/**
 * @author fanhj
 * @since 2018-10-12
 */
public interface AccidentScenarioMapper extends BaseMapper<AccidentScenario> {
    List<AccidentScenarioVO> listAccidentScenario(AccidentScenarioQueryParam queryParam);

    int countTotalAccidentScenarioByEnterpriseId(String enterpriseId);

    List<AccidentScenarioVO> listAccidentScenarioOfEnterprise(String enterpriseId);
}
