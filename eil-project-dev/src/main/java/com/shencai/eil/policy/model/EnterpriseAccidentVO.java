package com.shencai.eil.policy.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by zhoujx on 2018/9/19.
 */
@Data
public class EnterpriseAccidentVO implements Serializable {

    private String id;

    private Date happenTime;

    private String level;

    private String levelName;

    private Integer disposed;

    private Integer thirdpartyPersonalInjury;

    private Integer thirdpartyPropertyDamage;

    private Integer soilPollution;

    private Integer waterPollution;
}
