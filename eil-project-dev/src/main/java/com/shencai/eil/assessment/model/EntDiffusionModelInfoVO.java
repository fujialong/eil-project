package com.shencai.eil.assessment.model;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by zhoujx on 2018/10/23.
 */
@Data
public class EntDiffusionModelInfoVO implements Serializable {

    private String id;

    private String modelId;

    private String material;

    private String scenarioName;

    private String scenarioCode;

    private Double releaseAmount;

    private String wadingName;

    private String emissionModeTypeName;

    private String emissionModeType;

    private String existWaterEnvName;

    private String consideringWaterEnvRiskName;

    private String surfaceWaterConditionName;

    private String accessTypeName;

    private String pollutantTypeName;

    private String modelName;

    private String modelType;

    private Double density;
}
