package com.shencai.eil.resolver;

import com.baomidou.mybatisplus.extension.api.R;
import com.shencai.eil.constants.ResultStatus;
import com.shencai.eil.exception.AccessExpiredException;
import com.shencai.eil.exception.BusinessException;
import com.shencai.eil.exception.LoginException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @program: eil-project
 * @description: GlobalExceptionHandler
 * @author: fujl
 * @create: 2018-09-12 19:05
 **/
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Catch Default Exception
     *
     * @param e Exception
     * @return R
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    R handleException(Exception e) {
        log.error("catch error info==>:", e);

        return new R(ResultStatus.ERROR);
    }

    /**
     * Catch BusinessException
     *
     * @param e BusinessException
     * @return R
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    R handBusinessException(BusinessException e) {
        log.error("catch businessException info==>:", e);

        return R.restResult(e.getMessage(), ResultStatus.FAILED);
    }

    /**
     * overTime Exception
     *
     * @param e
     * @return
     */
    @ExceptionHandler(AccessExpiredException.class)
    @ResponseBody
    R handAccessExpiredException(AccessExpiredException e) {
        log.error("catch accessExpiredException info==>:", e);

        return R.restResult(e.getMessage(), ResultStatus.LOGIN_OUT);
    }

    /**
     * login exception
     *
     * @param e
     * @return
     */
    @ExceptionHandler(LoginException.class)
    @ResponseBody
    R handLoginException(LoginException e) {
        log.error("catch loginException info==>:", e);

        return R.restResult(e.getMessage(), ResultStatus.USERNAME_PASSWORD_ERROR);
    }
}
