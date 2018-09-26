package com.shencai.eil.system.service.impl;

import com.shencai.eil.system.entity.SysDictionary;
import com.shencai.eil.system.mapper.SysDictionaryMapper;
import com.shencai.eil.system.model.DictionaryQueryParam;
import com.shencai.eil.system.model.DictionaryVO;
import com.shencai.eil.system.service.ISysDictionaryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhoujx
 * @since 2018-09-15
 */
@Service
public class SysDictionaryServiceImpl extends ServiceImpl<SysDictionaryMapper, SysDictionary> implements ISysDictionaryService {

    @Autowired
    private SysDictionaryMapper sysDictionaryMapper;

    @Override
    public List<DictionaryVO> listSysDictionary(DictionaryQueryParam queryParam) {
        List<DictionaryVO> dictionaryVOList = sysDictionaryMapper.listSysDictionary(queryParam);
        return dictionaryVOList;
    }
}
