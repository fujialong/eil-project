package com.shencai.eil.grading.service;

import com.shencai.eil.grading.model.GradingParam;
import com.shencai.eil.grading.model.GradingQueryParam;
import com.shencai.eil.grading.model.GradingVO;
import com.shencai.eil.policy.model.EnterpriseVO;

import java.util.concurrent.ExecutionException;

/**
 * Created by zhoujx on 2018/9/19.
 */
public interface IGradingService {

    void execute(GradingParam param) throws ExecutionException, InterruptedException;

    GradingVO getGradingResult(GradingQueryParam queryParam) throws ExecutionException, InterruptedException;

    void gisTest(EnterpriseVO enterpriseVO);

}