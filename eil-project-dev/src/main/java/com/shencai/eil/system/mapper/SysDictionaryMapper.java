package com.shencai.eil.system.mapper;

import com.shencai.eil.system.entity.SysDictionary;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shencai.eil.system.model.DictionaryQueryParam;
import com.shencai.eil.system.model.DictionaryVO;

import java.util.List;

/**
 * @author zhoujx
 * @since 2018-09-15
 */
public interface SysDictionaryMapper extends BaseMapper<SysDictionary> {

    List<DictionaryVO> listSysDictionary(DictionaryQueryParam queryParam);
}
