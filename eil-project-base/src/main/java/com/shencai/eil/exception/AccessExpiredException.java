package com.shencai.eil.exception;

/**
 * @program: eil-project
 * @description:
 * @author: fujl
 * @create: 2018-09-12 20:20
 **/
public class AccessExpiredException extends BusinessException {
    public AccessExpiredException(String message) {
        super(message);
    }
}
