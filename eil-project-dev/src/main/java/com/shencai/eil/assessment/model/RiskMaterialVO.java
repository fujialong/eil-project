package com.shencai.eil.assessment.model;

import lombok.Data;

import java.util.List;

/**
 * Created by fanhj on 2018/10/22.
 */
@Data
public class RiskMaterialVO {
    private String materialName;
    private List<RiskMaterialParamVO> paramList;
}
