package com.shencai.eil.gis.model;

import lombok.Data;

import java.util.List;

/**
 * @program: eil-project
 * @description:
 * @author: fujl
 * @create: 2018-09-22 13:59
 **/
@Data
public class GisValueParam {

    private String entId;

    private String type;

    private List<ContainsCodeValue> paramList;

}
