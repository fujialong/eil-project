package com.shencai.eil.login.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shencai.eil.login.entity.SystemBaseUser;
import com.shencai.eil.login.model.UserMenuVO;
import com.shencai.eil.login.model.UserParam;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author fujl
 * @since 2018-09-21
 */
public interface ISystemBaseUserService extends IService<SystemBaseUser> {
    /**
     * login
     *
     * @param userParam
     * @return user info
     */
    SystemBaseUser login(UserParam userParam);

    /**
     * user menu list
     * @param userId
     * @return menu
     */
    List<UserMenuVO> menuList(String userId);
}
