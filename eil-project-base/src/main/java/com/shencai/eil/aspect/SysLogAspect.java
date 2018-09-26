package com.shencai.eil.aspect;


import com.google.gson.Gson;
import com.shencai.eil.annotions.SysLog;
import com.shencai.eil.entiy.SysLogEntity;
import com.shencai.eil.util.HttpContextUtils;
import com.shencai.eil.util.IPUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * @program: environment-insurance-decision
 * @description: SysLogAspect
 * @author: fujl
 * @create: 2018-09-11 18:35
 **/
@Aspect
@Component
@Slf4j
public class SysLogAspect {

    @Autowired
    @Pointcut(value = "@annotation(com.shencai.eil.annotions.SysLog)")
    public void logPointCut() {

    }

    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        Object result = point.proceed();
        long time = System.currentTimeMillis() - beginTime;
        saveSysLog(point, time);

        return result;
    }

    private void saveSysLog(ProceedingJoinPoint joinPoint, long time) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        SysLogEntity sysLog = new SysLogEntity();
        SysLog syslog = method.getAnnotation(SysLog.class);
        if (syslog != null) {
            sysLog.setOperation(syslog.value());
        }
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        sysLog.setMethod(className + "." + methodName + "()");
        Object[] args = joinPoint.getArgs();
        try {
            String params = new Gson().toJson(args[0]);
            sysLog.setParams(params);
        } catch (Exception e) {

        }

        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        sysLog.setIp(IPUtils.getIpAddr(request));
        sysLog.setUsername("xxx");
        sysLog.setTime(time);
        sysLog.setCreateDate(new Date());

       log.info(sysLog.toString());
    }
}
