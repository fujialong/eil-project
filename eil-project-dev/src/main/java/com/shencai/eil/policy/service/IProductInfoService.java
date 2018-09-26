package com.shencai.eil.policy.service;

import com.shencai.eil.policy.entity.ProductInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shencai.eil.policy.model.ProductInfoParam;
import com.shencai.eil.policy.model.ProductInfoQueryParam;
import com.shencai.eil.policy.model.ProductVO;

import java.util.List;

/**
 * @author zhoujx
 * @since 2018-09-13
 */
public interface IProductInfoService extends IService<ProductInfo> {

    ProductVO saveProductInfo(ProductInfoParam param);

    List<ProductVO> listProduct(ProductInfoQueryParam queryParam);
}
