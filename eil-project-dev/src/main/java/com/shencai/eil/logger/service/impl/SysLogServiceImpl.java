package com.shencai.eil.logger.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shencai.eil.logger.entity.SysLog;
import com.shencai.eil.logger.mapper.SysLogMapper;
import com.shencai.eil.logger.service.ISysLogService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author fujl
 * @since 2018-09-12
 */
@Service
public class SysLogServiceImpl extends ServiceImpl<SysLogMapper, SysLog> implements ISysLogService {
    @Override
    public Page selectPage(IPage page) {
        return this.baseMapper.selectPage(page);
    }
}
