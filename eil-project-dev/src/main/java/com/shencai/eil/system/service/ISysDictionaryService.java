package com.shencai.eil.system.service;

import com.shencai.eil.system.entity.SysDictionary;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shencai.eil.system.model.DictionaryQueryParam;
import com.shencai.eil.system.model.DictionaryVO;

import java.util.List;

/**
 * @author zhoujx
 * @since 2018-09-15
 */
public interface ISysDictionaryService extends IService<SysDictionary> {

    List<DictionaryVO> listSysDictionary(DictionaryQueryParam queryParam);
}
