package com.shencai.eil.exception;

/**
 * @program: eil-project
 * @description: define business exception
 * @author: fujl
 * @create: 2018-09-12 19:14
 **/
public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = 5441923856899380112L;

    public BusinessException(String message) {
        super(message);
    }

}