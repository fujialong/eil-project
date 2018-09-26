package com.shencai.eil.annotions;

import java.lang.annotation.*;

/**
 * @program: environment-insurance-decision
 * @description: System Log
 * @author: fujl
 * @create: 2018-09-11 18:33
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SysLog {
    String value() default "";
}