package com.shencai.eil.entiy;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: environment-insurance-decision
 * @description: SysLogEntity
 * @author: fujl
 * @create: 2018-09-11 18:38
 **/
@Data
public class SysLogEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;
    /**
     * user operation
     */
    private String operation;

    /**
     * request method
     */
    private String method;

    /**
     * request params
     */
    private String params;

    /**
     * request time
     */
    private Long time;

    /**
     * ip address
     */
    private String ip;

    private Date createDate;
}
