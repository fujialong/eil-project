package com.shencai.eil.assessment.model;

import lombok.Data;

import java.util.List;

/**
 * Created by fanhj on 2018/10/26.
 */
@Data
public class GridVO {
    private String gridCode;
    private Double sensitiveArea;
    private Double avgConcentration;
    private Double pop;
    private Double acreage;
    private Double ecoValue;
    private Double fisheryArea;
    private Double agriculturalArea;
    private Double forestryArea;
    private String waterHeightType;
    private List<GridPointVO> pointList;
}
