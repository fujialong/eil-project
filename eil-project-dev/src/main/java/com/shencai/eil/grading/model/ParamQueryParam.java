package com.shencai.eil.grading.model;

import lombok.Data;

import java.util.List;

/**
 * Created by fanhj on 2018/9/19.
 */
@Data
public class ParamQueryParam {
    private String templateCategoryType;
    private String templateCategoryCode;
    private String enterpriseId;
    private List<String> ids;

    private List<String> paramNameList;

    private List<String> templateCategoryTypeList;
}
