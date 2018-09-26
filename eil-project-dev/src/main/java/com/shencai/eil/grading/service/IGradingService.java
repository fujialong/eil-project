package com.shencai.eil.grading.service;

import com.shencai.eil.grading.model.GradingParam;
import com.shencai.eil.grading.model.GradingQueryParam;
import com.shencai.eil.grading.model.GradingVO;

/**
 * Created by zhoujx on 2018/9/19.
 */
public interface IGradingService {

    void execute(GradingParam param);

    GradingVO getGradingResult(GradingQueryParam queryParam);

}