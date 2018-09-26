package com.shencai.eil.policy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shencai.eil.policy.entity.EnterpriseMappingProduct;
import com.shencai.eil.policy.model.EnterpriseMappingProductQueryParam;
import com.shencai.eil.policy.model.ProductVO;

import java.util.List;

/**
 * @author zhoujx
 * @since 2018-09-13
 */
public interface IEnterpriseMappingProductService extends IService<EnterpriseMappingProduct> {

    List<ProductVO> listProduct(EnterpriseMappingProductQueryParam queryParam);
}
