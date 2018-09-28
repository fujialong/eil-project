package com.shencai.eil.gis.model;

import lombok.Data;

import java.util.List;

/**
 * @program: eil-project
 * @description:
 * @author: fujl
 * @create: 2018-09-26 16:27
 **/
@Data
public class GisValueVO {

    private String entId;

    private Double latitude;

    private Double longitude;

    private List<String> hasSavedParamList;
}
