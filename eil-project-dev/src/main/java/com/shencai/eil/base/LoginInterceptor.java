package com.shencai.eil.base;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.api.R;
import com.shencai.eil.constants.ResultStatus;
import com.shencai.eil.exception.AccessExpiredException;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.ObjectUtils;
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
        return setInterceptorStatus(httpServletRequest, httpServletResponse, false);
    }

    private static void printJson(HttpServletResponse httpServletResponse, String code) {
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
        if (!ObjectUtils.isEmpty(ticket) && !ObjectUtils.isEmpty(session.getAttribute(ticket[0]))) {
            return true;
        }

        printJson(httpServletResponse, "");
        return false;
    }
}
