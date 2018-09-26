package com.shencai.eil.entiy;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @program: eil-project
 * @description:
 * @author: fujl
 * @create: 2018-09-20 20:07
 **/
@Data
@Builder
public class TokenEntity {
    private String userId;

    private String ticket;

    private Date expireTime;

    private Date updateTime;

    private String userName;
}
