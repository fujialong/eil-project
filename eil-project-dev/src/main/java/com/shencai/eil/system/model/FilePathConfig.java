package com.shencai.eil.system.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by fanhj on 2018/10/7.
 */
@Component
public class FilePathConfig {

    @Value("#{fileSetting['file.basic.fastGrading.modelPath']}")
    public String basicModelPath;

    @Value("#{fileSetting['file.intensive.fastGrading.modelPath']}")
    public String intensiveModelPath;

    @Value("#{fileSetting['file.lowRisk.finalReport.modelPath']}")
    public String lowRiskFinalReport;

    @Value("#{fileSetting['file.highRisk.finalReport.modelPath']}")
    public String highRiskFinalReport;

    @Value("#{fileSetting['file.destPath']}")
    public String destPath;
}
