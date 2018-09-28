package com.shencai.eil.policy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shencai.eil.common.constants.BaseEnum;
import com.shencai.eil.common.utils.DateUtil;
import com.shencai.eil.common.utils.StringUtil;
import com.shencai.eil.policy.entity.ProductClass;
import com.shencai.eil.policy.entity.ProductInfo;
import com.shencai.eil.policy.mapper.ProductClassMapper;
import com.shencai.eil.policy.mapper.ProductInfoMapper;
import com.shencai.eil.policy.model.ProductClassQueryParam;
import com.shencai.eil.policy.model.ProductInfoParam;
import com.shencai.eil.policy.model.ProductInfoQueryParam;
import com.shencai.eil.policy.model.ProductVO;
import com.shencai.eil.policy.service.IProductClassService;
import com.shencai.eil.policy.service.IProductInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhoujx
 * @since 2018-09-13
 */
@Service
public class ProductInfoServiceImpl extends ServiceImpl<ProductInfoMapper, ProductInfo> implements IProductInfoService {

    @Autowired
    private IProductClassService productClassService;
    @Autowired
    private ProductClassMapper productClassMapper;
    @Autowired
    private ProductInfoMapper productInfoMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductVO saveProductInfo(ProductInfoParam param) {
        ProductClass largeProductClass = saveLargeProductClass(param);
        ProductClass smallProductClass = saveSmallProductClass(param, largeProductClass);
        ProductInfo product = saveProduct(param, smallProductClass);
        ProductVO productVO = getReturnData(largeProductClass, smallProductClass, product);
        return productVO;
    }

    @Override
    public List<ProductVO> listProduct(ProductInfoQueryParam queryParam) {
        List<ProductVO> productVOList = listProductInfo(queryParam);
        List<ProductVO> productClassList = listProductClass(queryParam);
        List<ProductVO> returnList = listReturnData(productVOList, productClassList);
        return returnList;
    }

    private List<ProductVO> listProductInfo(ProductInfoQueryParam queryParam) {
        return productInfoMapper.listProduct(queryParam);
    }

    private List<ProductVO> listReturnData(List<ProductVO> productVOList, List<ProductVO> productClassList) {
        List<ProductVO> returnList = new ArrayList<>();
        for (ProductVO productClass : productClassList) {
            if (ObjectUtils.isEmpty(productClass.getParentId())) {
                String largeProductClassId = productClass.getId();
                List<ProductVO> childList = new ArrayList<>();
                for (ProductVO smallProductClass : productClassList) {
                    if (largeProductClassId.equals(smallProductClass.getParentId())) {
                        String smallProductClassId = smallProductClass.getId();
                        List<ProductVO> products = new ArrayList<>();
                        for (ProductVO product : productVOList) {
                            if (smallProductClassId.equals(product.getParentId())) {
                                products.add(product);
                            }
                        }
                        smallProductClass.setChildren(products);
                        childList.add(smallProductClass);
                    }
                }
                productClass.setChildren(childList);
                returnList.add(productClass);
            }
        }
        return returnList;
    }

    private List<ProductVO> listProductClass(ProductInfoQueryParam queryParam) {
        ProductClassQueryParam param = new ProductClassQueryParam();
        param.setIndustryId(queryParam.getIndustryId());
        return productClassMapper.listAllProductClass(param);
    }

    private ProductVO getReturnData(ProductClass largeProductClass, ProductClass smallProductClass, ProductInfo product) {
        ProductVO productVO = new ProductVO();
        productVO.setId(product.getId());
        productVO.setLargeClassName(largeProductClass.getName());
        productVO.setSmallClassName(smallProductClass.getName());
        productVO.setName(product.getName());
        return productVO;
    }

    private ProductInfo saveProduct(ProductInfoParam param, ProductClass smallProductClass) {
        ProductInfo product = this.getOne(new QueryWrapper<ProductInfo>()
                .eq("name", param.getName())
                .eq("prod_class_id", smallProductClass.getId())
                .eq("valid", BaseEnum.VALID_YES.getCode()));
        if (ObjectUtils.isEmpty(product)) {
            product.setId(StringUtil.getUUID());
            product.setName(param.getName());
            product.setProdClassId(smallProductClass.getId());
            product.setCreateTime(DateUtil.getNowTimestamp());
            product.setUpdateTime(DateUtil.getNowTimestamp());
            product.setValid((Integer) BaseEnum.VALID_YES.getCode());
            this.save(product);
        }
        return product;
    }

    private ProductClass saveSmallProductClass(ProductInfoParam param, ProductClass largeProductClass) {
        ProductClass smallProductClass = productClassService.getOne(new QueryWrapper<ProductClass>()
                .eq("name", param.getSmallClassName())
                .eq("parent_id", largeProductClass.getId())
                .eq("valid", BaseEnum.VALID_YES.getCode()));
        if (ObjectUtils.isEmpty(smallProductClass)) {
            smallProductClass.setId(StringUtil.getUUID());
            smallProductClass.setName(param.getSmallClassName());
            smallProductClass.setCreateTime(DateUtil.getNowTimestamp());
            smallProductClass.setUpdateTime(DateUtil.getNowTimestamp());
            smallProductClass.setValid((Integer) BaseEnum.VALID_YES.getCode());
            productClassService.save(smallProductClass);
        }
        return smallProductClass;
    }

    private ProductClass saveLargeProductClass(ProductInfoParam param) {
        ProductClass largeProductClass = productClassService.getOne(new QueryWrapper<ProductClass>()
                .eq("name", param.getLargeClassName())
                .isNull("parent_id")
                .eq("valid", BaseEnum.VALID_YES.getCode()));
        if (ObjectUtils.isEmpty(largeProductClass)) {
            largeProductClass.setId(StringUtil.getUUID());
            largeProductClass.setName(param.getLargeClassName());
            largeProductClass.setCreateTime(DateUtil.getNowTimestamp());
            largeProductClass.setUpdateTime(DateUtil.getNowTimestamp());
            largeProductClass.setValid((Integer) BaseEnum.VALID_YES.getCode());
            productClassService.save(largeProductClass);
        }
        return largeProductClass;
    }
}
