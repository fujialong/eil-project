package com.shencai.eil.login.model;

import lombok.Data;

/**
 * @program: eil-project
 * @description:
 * @author: fujl
 * @create: 2018-09-21 12:49
 **/
@Data
public class UserMenuVO {

    private String menuName;

    private String menuEnglishName;

    private String menuUrl;

    private String menuOrder;
}
