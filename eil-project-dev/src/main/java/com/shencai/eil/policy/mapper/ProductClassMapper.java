package com.shencai.eil.policy.mapper;

import com.shencai.eil.policy.entity.ProductClass;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shencai.eil.policy.model.ProductClassQueryParam;
import com.shencai.eil.policy.model.ProductClassVO;
import com.shencai.eil.policy.model.ProductVO;

import java.util.List;

/**
 * @author zhoujx
 * @since 2018-09-13
 */
public interface ProductClassMapper extends BaseMapper<ProductClass> {

    List<ProductClassVO> listProductClass(ProductClassQueryParam queryParam);

    List<ProductVO> listAllProductClass(ProductClassQueryParam param);
}
