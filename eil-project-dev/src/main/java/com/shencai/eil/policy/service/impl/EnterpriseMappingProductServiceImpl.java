package com.shencai.eil.policy.service.impl;

import com.shencai.eil.policy.entity.EnterpriseMappingProduct;
import com.shencai.eil.policy.mapper.EnterpriseMappingProductMapper;
import com.shencai.eil.policy.model.EnterpriseMappingProductQueryParam;
import com.shencai.eil.policy.model.ProductVO;
import com.shencai.eil.policy.service.IEnterpriseMappingProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhoujx
 * @since 2018-09-13
 */
@Service
public class EnterpriseMappingProductServiceImpl extends ServiceImpl<EnterpriseMappingProductMapper, EnterpriseMappingProduct> implements IEnterpriseMappingProductService {

    @Autowired
    private EnterpriseMappingProductMapper enterpriseMappingProductMapper;

    @Override
    public List<ProductVO> listProduct(EnterpriseMappingProductQueryParam queryParam) {
        List<ProductVO> productVOList = enterpriseMappingProductMapper.listProduct(queryParam);
        return productVOList;
    }
}
