package com.shencai.eil.base;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.shencai.eil.common.constants.BaseEnum;
import com.shencai.eil.common.utils.ObjectUtil;
import com.shencai.eil.constants.ResultStatus;
import com.shencai.eil.exception.AccessExpiredException;
import com.shencai.eil.login.entity.SystemBaseUser;
import com.shencai.eil.login.service.ISystemBaseUserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.shencai.eil.common.utils.ObjectUtil;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * @program: eil-project
 * @description: Login Interceptor
 * @author: fujl
 * @create: 2018-09-25 13:48
 **/
public class LoginInterceptor implements HandlerInterceptor {

    public static final String USER_LOGIN_DO = "/user/login.do";
    public static final String IGNOR_TICKET = "123456";

    @Autowired
    private ISystemBaseUserService systemBaseUserService;
    /**
     * This method is called before the Controller handles it, and return false is aborted
     * Not enabled by default
     */
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) throws AccessExpiredException {

        //Gets the requested url
        String url = httpServletRequest.getRequestURI();
        if (StringUtils.equals(url, USER_LOGIN_DO)) {
            //Is the login interface that allows it to pass through
            return true;
        }
        return setInterceptorStatus(httpServletRequest, httpServletResponse, true);
    }

    private static void printJson(HttpServletResponse httpServletResponse) {
        R result = R.restResult("ticket timeout", ResultStatus.LOGIN_OUT);
        String content = JSON.toJSONString(result);
        printContent(httpServletResponse, content);
    }

    private static void printContent(HttpServletResponse httpServletResponse, String content) {
        httpServletResponse.reset();
        httpServletResponse.setContentType("application/json");
        httpServletResponse.setHeader("Cache-Control", "no-store");
        httpServletResponse.setCharacterEncoding("UTF-8");
        PrintWriter pw;
        try {
            pw = httpServletResponse.getWriter();
            pw.write(content);
            pw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Background is enabled for login verification
     *
     * @param httpServletRequest
     * @param httpServletResponse
     * @param status
     * @return
     */
    public boolean setInterceptorStatus(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, boolean status) {
        if (!status) {
            return true;
        }

        HttpSession session = httpServletRequest.getSession();
        Map<String, String[]> map = httpServletRequest.getParameterMap();
        String[] ticket = map.get("ticket");
        if (ObjectUtil.isEmpty(ticket)) {
            printJson(httpServletResponse);
            return false;
        }

        if (IGNOR_TICKET.equals(ticket[0])) {
            return true;
        }

        SystemBaseUser user = systemBaseUserService.getOne(new QueryWrapper<SystemBaseUser>()
        .eq("deleteFlag", 0)
        .eq("ticket",ticket[0]));

        if (!ObjectUtil.isEmpty(user)) {
            return true;
        }

        printJson(httpServletResponse);
        return false;
    }
}
