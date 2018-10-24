package com.shencai.eil.assessment.model;

import lombok.Data;

import java.util.List;

/**
 * Created by fanhj on 2018/10/22.
 */
@Data
public class RiskMaterialQueryParam {
    private List<String> materialNameList;
    private String templateCategoryCode;
}
