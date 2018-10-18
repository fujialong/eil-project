package com.shencai.eil.policy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shencai.eil.gis.model.EntGisInfo;
import com.shencai.eil.policy.entity.EnterpriseInfo;
import com.shencai.eil.policy.model.EnterpriseQueryParam;
import com.shencai.eil.policy.model.EnterpriseVO;
import com.shencai.eil.policy.model.PolicyQueryParam;
import com.shencai.eil.policy.model.PolicyVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhoujx
 * @since 2018-09-13
 */
public interface EnterpriseInfoMapper extends BaseMapper<EnterpriseInfo> {

    List<PolicyVO> pageEnterpriseInfo(Page<PolicyVO> page, @Param("queryParam") PolicyQueryParam queryParam);

    PolicyVO getPolicy(PolicyQueryParam queryParam);

    EnterpriseVO getEnterprise(EnterpriseQueryParam queryParam);

    EnterpriseVO getEnterpriseInfoOfSurvey(EnterpriseQueryParam queryParam);

    /**
     * Gets the coordinates of the current enterprise
     *
     * @param entId
     * @return
     */
    EntGisInfo getEntLocation(String entId);

    /**
     * Gets the coordinates of all existing enterprises in the system
     * Excluding the self enterprise
     *
     * @param entId
     * @return
     */
    List<EntGisInfo> listOtherEntLocation(String entId);

    /**
     * Gets the coordinates of all existing enterprises in the system
     * All
     *
     * @return
     */
    List<EntGisInfo> listAllEntLocation();
}
