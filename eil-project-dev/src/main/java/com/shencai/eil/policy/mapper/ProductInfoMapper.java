package com.shencai.eil.policy.mapper;

import com.shencai.eil.policy.entity.ProductInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shencai.eil.policy.model.ProductInfoQueryParam;
import com.shencai.eil.policy.model.ProductVO;

import java.util.List;

/**
 * @author zhoujx
 * @since 2018-09-13
 */
public interface ProductInfoMapper extends BaseMapper<ProductInfo> {

    List<ProductVO> listProduct(ProductInfoQueryParam queryParam);
}
