package com.shencai.eil.assessment.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: eil-project
 * @description:
 * @author: fujl
 * @create: 2018-10-25 19:46
 **/
@Data
public class AssessGridVO implements Serializable {

    private String entName;

    private String entId;

    private Double distanceOfOutlet;

    private Double riverWidth;

    private Double lat;

    private Double lon;

    private String flowDirection;

    private String flowDirectionName;

    private Integer wading;


}
