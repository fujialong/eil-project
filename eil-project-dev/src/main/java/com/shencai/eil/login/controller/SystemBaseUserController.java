package com.shencai.eil.login.controller;


import com.shencai.eil.common.utils.StringUtil;
import com.shencai.eil.entiy.TokenEntity;
import com.shencai.eil.exception.LoginException;
import com.shencai.eil.login.entity.SystemBaseUser;
import com.shencai.eil.login.model.UserMenuVO;
import com.shencai.eil.login.model.UserParam;
import com.shencai.eil.login.service.ISystemBaseUserService;
import com.shencai.eil.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author fujl
 * @since 2018-09-21
 */
@Controller
@RequestMapping("/user")
public class SystemBaseUserController {
    private TokenEntity tokenEntity;

    @Autowired
    private ISystemBaseUserService systemBaseUserService;


    @RequestMapping(value = "/login")
    @ResponseBody
    private Result login(UserParam userParam, HttpServletRequest httpServletRequest) {
        Map<String, Object> map = new HashMap<>(1);
        SystemBaseUser user = systemBaseUserService.login(userParam);

        if (ObjectUtils.isEmpty(user)) {
            throw new LoginException("login failed");
        }

        String roleCode = systemBaseUserService.getRoleCode(user.getUserId());

        tokenEntity = TokenEntity.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .roleCode(roleCode)
                .build();

        if (StringUtils.isEmpty(user.getTicket())) {
            tokenEntity.setTicket(StringUtil.getUUID());
            user.setTicket(tokenEntity.getTicket());
            systemBaseUserService.updateById(user);
        } else {
            tokenEntity.setTicket(user.getTicket());
        }

        map.put("tokenEntity", tokenEntity);
        return Result.ok(map);
    }

    /**
     * save token to session
     *
     * @param user
     * @param httpServletRequest
     */
    private void saveUserToSession(TokenEntity user, HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession();
        session.setAttribute(user.getTicket(), user);
    }


    @RequestMapping("/menuList")
    @ResponseBody
    private Result<UserMenuVO> menuList(String userId) {
        return Result.ok(systemBaseUserService.menuList(userId));
    }
}

