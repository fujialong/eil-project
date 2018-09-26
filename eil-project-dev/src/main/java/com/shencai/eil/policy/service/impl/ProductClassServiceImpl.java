package com.shencai.eil.policy.service.impl;

import com.shencai.eil.policy.entity.ProductClass;
import com.shencai.eil.policy.mapper.ProductClassMapper;
import com.shencai.eil.policy.model.ProductClassQueryParam;
import com.shencai.eil.policy.model.ProductClassVO;
import com.shencai.eil.policy.service.IProductClassService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhoujx
 * @since 2018-09-13
 */
@Service
public class ProductClassServiceImpl extends ServiceImpl<ProductClassMapper, ProductClass> implements IProductClassService {

    @Autowired
    private ProductClassMapper productClassMapper;

    @Override
    public List<ProductClassVO> listProductClass(ProductClassQueryParam queryParam) {
        List<ProductClassVO> productClassList = productClassMapper.listProductClass(queryParam);
        return productClassList;
    }
}
