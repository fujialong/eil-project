package com.shencai.eil.assessment.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: eil-project
 * @description:
 * @author: fujl
 * @create: 2018-10-26 13:55
 **/
@Data
public class AssessStartModelCalculateParam implements Serializable {
    private String entId;

    private String solidType;

    private String materialName;
}
