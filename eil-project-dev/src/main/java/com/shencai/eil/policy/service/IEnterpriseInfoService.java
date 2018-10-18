package com.shencai.eil.policy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shencai.eil.gis.model.EntGisInfo;
import com.shencai.eil.policy.entity.EnterpriseInfo;
import com.shencai.eil.policy.model.PolicyQueryParam;

import java.util.List;

/**
 * @author zhoujx
 * @since 2018-09-13
 */
public interface IEnterpriseInfoService extends IService<EnterpriseInfo> {

    void saveEnterpriseInfo(EnterpriseInfo param);

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

    EnterpriseInfo getEnterpriseInfo(PolicyQueryParam queryParam);
}
