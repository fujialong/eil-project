package com.shencai.eil.survey.model;

import com.shencai.eil.system.entity.BaseFileupload;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Created by fanhj on 2018/10/18.
 */
@Data
public class EntSurveyPhotoVO {
    private String fileId;
    private String fileAddress;
    private Date timeOfPhoto;
    private String location;
    private String remark;
    private List<BaseFileupload> fileList;
}
