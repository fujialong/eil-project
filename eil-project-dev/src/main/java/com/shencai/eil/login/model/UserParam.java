package com.shencai.eil.login.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: eil-project
 * @description:
 * @author: fujl
 * @create: 2018-09-20 19:09
 **/
@Data
public class UserParam  implements Serializable {

    private String loginName;

    private String password;
}
