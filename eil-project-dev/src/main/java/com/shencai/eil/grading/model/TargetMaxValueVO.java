package com.shencai.eil.grading.model;

import lombok.Data;

/**
 * @program: eil-project
 * @description:
 * @author: fujialong
 * @create: 2018-10-14 10:04
 **/
@Data
public class TargetMaxValueVO {

    private String targetId;

    private Double maxValue;

    private Double minValue;
}
