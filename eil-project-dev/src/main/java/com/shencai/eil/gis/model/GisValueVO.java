package com.shencai.eil.gis.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class GisValueVO implements Serializable {

    private String code;

    private Double value;

    private String classCode;

    private String entId;

}
