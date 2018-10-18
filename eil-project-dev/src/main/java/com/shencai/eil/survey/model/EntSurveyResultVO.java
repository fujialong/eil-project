package com.shencai.eil.survey.model;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by zhoujx on 2018/10/9.
 */
@Data
public class EntSurveyResultVO implements Serializable {

    private String surveyItemCode;

    private String surveyItemName;

    private String result;

    private Integer excelRowIndex;

    private String optionCode;

    private String materialTypeOptionName;

    private String materialTypeOptionCode;

    private String materialName;

    private String inputOrOutput;

    private String mainPollutantName;

    private Double emission;

    private Double emissionConcentration;

    private String techniqueCell;

    private String containerPressure;

    private Double temperature;

    private String height;

    private String internalDiameterOfPipeline;

    private String detectionSystemClassification;

    private String isolationSystemClassification;
}
