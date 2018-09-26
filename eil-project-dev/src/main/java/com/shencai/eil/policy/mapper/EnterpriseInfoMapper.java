package com.shencai.eil.policy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shencai.eil.gis.model.GisValueVO;
import com.shencai.eil.policy.entity.EnterpriseInfo;
import com.shencai.eil.policy.model.EnterpriseQueryParam;
import com.shencai.eil.policy.model.EnterpriseVO;
import com.shencai.eil.policy.model.PolicyQueryParam;
import com.shencai.eil.policy.model.PolicyVO;

import java.util.List;

/**
 * @author zhoujx
 * @since 2018-09-13
 */
public interface EnterpriseInfoMapper extends BaseMapper<EnterpriseInfo> {

    List<PolicyVO> pageEnterpriseInfo(Page<PolicyVO> page, PolicyQueryParam queryParam);

    PolicyVO getPolicy(PolicyQueryParam queryParam);

    EnterpriseVO getEnterprise(EnterpriseQueryParam queryParam);

    /**
     * Gets the coordinates of the current enterprise
     *
     * @param entId
     * @return
     */
    GisValueVO getEntLocation(String entId);

    /**
     * Gets the coordinates of all existing enterprises in the system
     * Excluding the self enterprise
     *
     * @param entId
     * @return
     */
    List<GisValueVO> listOtherEntLocation(String entId);

    /**
     * Gets the coordinates of all existing enterprises in the system
     * All
     *
     * @return
     */
    List<GisValueVO> listAllEntLocation();
}
