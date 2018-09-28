package com.shencai.eil.login.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shencai.eil.login.entity.SystemBaseUser;
import com.shencai.eil.login.mapper.SystemBaseUserMapper;
import com.shencai.eil.login.model.UserMenuVO;
import com.shencai.eil.login.model.UserParam;
import com.shencai.eil.login.service.ISystemBaseUserService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author fujl
 * @since 2018-09-21
 */
@Service
public class SystemBaseUserServiceImpl extends ServiceImpl<SystemBaseUserMapper, SystemBaseUser> implements ISystemBaseUserService {

    @Override
    public SystemBaseUser login(UserParam userParam) {
        SystemBaseUser user = this.baseMapper.selectOne(new QueryWrapper<SystemBaseUser>()
                .eq("userId", userParam.getLoginName())
                .eq("password", userParam.getPassword())
                .eq("deleteFlag", 0));

        return user;
    }

    @Override
    public List<UserMenuVO> menuList(String userId) {
        return this.baseMapper.menuList(userId);
    }

    @Override
    public String getRoleCode(String userId) {
        return this.baseMapper.getRoleCode(userId);
    }
}
