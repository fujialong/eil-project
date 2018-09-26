package com.shencai.eil.base.entity;

import lombok.Data;

import java.util.Date;

/**
 * @program: eil-project
 * @description: user info
 * @author: fujialong
 * @create: 2018-09-12
 **/
@Data
public class UserInfo {

    private String ticket;

    private String userName;

    private Date lastActionTime;



}
