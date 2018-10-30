package com.shencai.eil.assessment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shencai.eil.assessment.entity.EntAssessInfo;
import com.shencai.eil.assessment.mapper.EntAssessInfoMapper;
import com.shencai.eil.assessment.model.AssessGridVO;
import com.shencai.eil.assessment.service.IEntAssessResultService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author fanhj
 * @since 2018-10-21
 */
@Service
public class EntAssessResultServiceImpl extends ServiceImpl<EntAssessInfoMapper, EntAssessInfo> implements IEntAssessResultService {

    @Override
    public AssessGridVO gisBaseInfo(String entId) {
        return this.baseMapper.gisBaseInfo(entId);
    }
}
