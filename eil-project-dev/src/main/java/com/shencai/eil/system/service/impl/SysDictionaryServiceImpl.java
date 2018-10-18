package com.shencai.eil.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shencai.eil.system.entity.SysDictionary;
import com.shencai.eil.system.mapper.SysDictionaryMapper;
import com.shencai.eil.system.model.DictionaryQueryParam;
import com.shencai.eil.system.model.DictionaryVO;
import com.shencai.eil.system.service.ISysDictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.shencai.eil.common.utils.ObjectUtil;

import java.util.ArrayList;
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

    @Override
    public List<DictionaryVO> listTreeDictionary(DictionaryQueryParam queryParam) {
        List<DictionaryVO> dictionaryVOList = sysDictionaryMapper.listTreeDictionary(queryParam);
        List<DictionaryVO> returnList = listReturnData(dictionaryVOList);
        return returnList;
    }

    private List<DictionaryVO> listReturnData(List<DictionaryVO> dictionaryVOList) {
        List<DictionaryVO> returnList = new ArrayList<>();
        for (DictionaryVO dictionaryVO : dictionaryVOList) {
            if (ObjectUtil.isEmpty(dictionaryVO.getParentCode())) {
                String code = dictionaryVO.getCode();
                List<DictionaryVO> children = new ArrayList<>();
                for (DictionaryVO childDictionary : dictionaryVOList) {
                    if (code.equals(childDictionary.getParentCode())) {
                        children.add(childDictionary);
                    }
                }
                dictionaryVO.setChildren(children);
                returnList.add(dictionaryVO);
            }
        }
        return returnList;
    }

}
