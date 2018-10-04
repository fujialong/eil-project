package com.shencai.eil.system.service;

import com.shencai.eil.system.entity.BaseFileupload;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shencai.eil.system.model.FileUploadVO;

/**
 * @author zhoujx
 * @since 2018-09-21
 */
public interface IBaseFileuploadService extends IService<BaseFileupload> {
    void bindFiles(FileUploadVO fileUploadVO);
}
