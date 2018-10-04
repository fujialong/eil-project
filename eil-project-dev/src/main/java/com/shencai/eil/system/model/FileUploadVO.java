package com.shencai.eil.system.model;

import lombok.Data;

import java.util.List;

/**
 * Created by fanhj on 2018/9/30.
 */
@Data
public class FileUploadVO {
    private List<String> fileIdList;
    private String sourceId;
    private String sourceType;
}
