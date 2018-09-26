package com.shencai.eil.logger.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shencai.eil.logger.entity.SysLog;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author fujl
 * @since 2018-09-12
 */
public interface ISysLogService extends IService<SysLog> {

    Page selectPage(IPage page);
}
