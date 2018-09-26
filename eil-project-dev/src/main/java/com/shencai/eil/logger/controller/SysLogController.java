package com.shencai.eil.logger.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shencai.eil.base.entity.UserInfo;
import com.shencai.eil.logger.entity.SysLog;
import com.shencai.eil.logger.service.ISysLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**`
 * <p>
 * 前端控制器
 * </p>
 *
 * @author fujl
 * @since 2018-09-12
 */
@Controller
@RequestMapping("/sysLog")
@Slf4j
public class SysLogController {

    @Autowired
    private ISysLogService sysLogService;

    @RequestMapping("/list")
    @ResponseBody
    private Object list(UserInfo userInfo) {
        if (true) {
            log.info("userInfo:"+userInfo);
        }

        return sysLogService.list(new QueryWrapper<SysLog>().eq("1", "1"));
    }
}

