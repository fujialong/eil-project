package com.shencai.eil.logger.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shencai.eil.logger.entity.SysLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author fujl
 * @since 2018-09-12
 */
public interface SysLogMapper extends BaseMapper<SysLog> {

    Page selectPage(IPage page);
}
