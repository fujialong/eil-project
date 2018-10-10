package com.shencai.eil.system.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by fanhj on 2018/10/7.
 */
@Component
public class FilePathConfig {

    @Value("#{fileSetting['file.fastGrading.modelPath']}")
    public String modelPath;

    @Value("#{fileSetting['file.destPath']}")
    public String destPath;

    @Value("#{fileSetting['file.downloadPath']}")
    public String downloadPath;
}
