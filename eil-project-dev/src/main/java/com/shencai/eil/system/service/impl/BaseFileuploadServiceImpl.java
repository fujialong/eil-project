package com.shencai.eil.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shencai.eil.common.constants.BaseEnum;
import com.shencai.eil.exception.BusinessException;
import com.shencai.eil.system.entity.BaseFileupload;
import com.shencai.eil.system.mapper.BaseFileuploadMapper;
import com.shencai.eil.system.model.FileUploadVO;
import com.shencai.eil.system.service.IBaseFileuploadService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zhoujx
 * @since 2018-09-21
 */
@Service
public class BaseFileuploadServiceImpl extends ServiceImpl<BaseFileuploadMapper, BaseFileupload> implements IBaseFileuploadService {
    @Autowired
    private BaseFileuploadMapper baseFileuploadMapper;

    @Override
    public void bindFiles(FileUploadVO fileUploadVO) {
        if (CollectionUtils.isEmpty(fileUploadVO.getFileIdList())) {
            throw new BusinessException("don't have files");
        }
        updateFiles(fileUploadVO);
    }

    private void updateFiles(FileUploadVO fileUploadVO) {
        BaseFileupload baseFileupload = new BaseFileupload();
        baseFileupload.setSourceId(fileUploadVO.getSourceId());
        baseFileupload.setStype(fileUploadVO.getSourceType());
        baseFileuploadMapper.update(baseFileupload, new QueryWrapper<BaseFileupload>()
                .in("id", fileUploadVO.getFileIdList())
                .eq("valid", BaseEnum.VALID_YES.getCode()));
    }
}
