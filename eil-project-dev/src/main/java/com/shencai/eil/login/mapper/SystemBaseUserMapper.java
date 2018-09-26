package com.shencai.eil.login.mapper;

import com.shencai.eil.login.entity.SystemBaseUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shencai.eil.login.model.UserMenuVO;
import com.shencai.eil.model.Result;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author fujl
 * @since 2018-09-21
 */
public interface SystemBaseUserMapper extends BaseMapper<SystemBaseUser> {

    /**
     *  menuList
     *
     * @param userId
     * @return
     */
    List<UserMenuVO> menuList(String userId);
}
