package com.shencai.eil.scenario.service.impl;

import com.shencai.eil.scenario.entity.AccidentScenario;
import com.shencai.eil.scenario.mapper.AccidentScenarioMapper;
import com.shencai.eil.scenario.model.AccidentScenarioQueryParam;
import com.shencai.eil.scenario.model.AccidentScenarioVO;
import com.shencai.eil.scenario.service.IAccidentScenarioService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author fanhj
 * @since 2018-10-12
 */
@Service
public class AccidentScenarioServiceImpl extends ServiceImpl<AccidentScenarioMapper, AccidentScenario> implements IAccidentScenarioService {

    @Autowired
    private AccidentScenarioMapper accidentScenarioMapper;

    @Override
    public List<AccidentScenarioVO> listAccidentScenario(AccidentScenarioQueryParam queryParam) {
        return accidentScenarioMapper.listAccidentScenario(queryParam);
    }
}
