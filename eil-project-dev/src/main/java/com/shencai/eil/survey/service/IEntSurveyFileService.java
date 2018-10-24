package com.shencai.eil.survey.service;

/**
 * Created by fanhj on 2018/10/4.
 */
public interface IEntSurveyFileService {

    String getFastGradingResult(String enterpriseId);

    String generateExcel(String enterpriseId, String sourceType);

    void importSurveyResults(String baseFileuploadId, String enterpriseId);

    String downloadBasicSurveyResult(String enterpriseId);

    String downloadIntensiveSurveyResult(String enterpriseId);
}
