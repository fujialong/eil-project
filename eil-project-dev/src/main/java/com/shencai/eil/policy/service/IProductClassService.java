package com.shencai.eil.policy.service;

import com.shencai.eil.policy.entity.ProductClass;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shencai.eil.policy.model.ProductClassQueryParam;
import com.shencai.eil.policy.model.ProductClassVO;

import java.util.List;

/**
 * @author zhoujx
 * @since 2018-09-13
 */
public interface IProductClassService extends IService<ProductClass> {

    List<ProductClassVO> listProductClass(ProductClassQueryParam queryParam);
}
