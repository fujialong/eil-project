package com.shencai.eil.grading.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhoujx on 2018/10/16.
 */
@Data
public class RiskMaterialParamValueQueryParam implements Serializable {

    private String materialName;

    private String paramCode;

    private String templateCode;

    private List<String> materialNameList;
}
