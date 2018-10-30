package com.shencai.eil.assessment.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @program: eil-project
 * @description:
 * @author: fujl
 * @create: 2018-10-24 11:01
 **/
@Data
public class AssessGirdParam implements Serializable {
    private String entId;
    private String bisCode;
    private Double sandRate;
    private Double clayRate;
    private Double powderRate;
    private List<AssessGridChildParam> childParamList;
}
