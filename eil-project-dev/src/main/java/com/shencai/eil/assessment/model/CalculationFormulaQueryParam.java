package com.shencai.eil.assessment.model;

import lombok.Data;

import java.util.List;

/**
 * Created by fanhj on 2018/10/21.
 */
@Data
public class CalculationFormulaQueryParam {
    private List<String> conditions;
    private String bisCode;
}
