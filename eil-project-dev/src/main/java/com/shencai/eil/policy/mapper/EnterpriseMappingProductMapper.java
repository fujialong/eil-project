package com.shencai.eil.policy.mapper;

import com.shencai.eil.policy.entity.EnterpriseMappingProduct;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shencai.eil.policy.model.EnterpriseMappingProductQueryParam;
import com.shencai.eil.policy.model.ProductVO;

import java.util.List;

/**
 * @author zhoujx
 * @since 2018-09-13
 */
public interface EnterpriseMappingProductMapper extends BaseMapper<EnterpriseMappingProduct> {

    List<ProductVO> listProduct(EnterpriseMappingProductQueryParam queryParam);
}
