import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shencai.eil.policy.model.EnterpriseParam;
import com.shencai.eil.policy.model.EnterpriseVO;
import com.shencai.eil.survey.model.EntSurveyPlanQueryParam;
import com.shencai.eil.survey.model.EntSurveyPlanVO;
import com.shencai.eil.survey.service.IEntSurveyPlanService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by fanhj on 2018/10/6.
 */
public class SurveyPlanTest extends BaseTest{
    @Autowired
    private IEntSurveyPlanService entSurveyPlanService;

    @Test
    public void getSurveyInfo() {
        EnterpriseVO enterpriseVO = entSurveyPlanService.getEntSurveyInfo("ea453cfe70884dfaa8f2347e1c503438");
        System.out.print("finish");
    }

    @Test
    public void surveyUpgrade() {
        EnterpriseParam param = new EnterpriseParam();
        param.setId("ea453cfe70884dfaa8f2347e1c503438");
        param.setNeedSurveyUpgrade(0);
        entSurveyPlanService.surveyUpgrade(param);
        System.out.print("finish");
    }

    @Test
    public void pageBasicSurveyPlan() {
        EntSurveyPlanQueryParam queryParam = new EntSurveyPlanQueryParam();
        queryParam.setEnterpriseId("ceb5e942452b442f91a1a13f985af5bd");
        queryParam.setCurrent(1);
        queryParam.setSize(10);
        Page<EntSurveyPlanVO> entSurveyPlanList = entSurveyPlanService.pageBasicSurveyPlan(queryParam);
        System.out.print("finish");
    }

    @Test
    public void pageIntensiveSurveyPlan() {
        EntSurveyPlanQueryParam queryParam = new EntSurveyPlanQueryParam();
        queryParam.setEnterpriseId("ceb5e942452b442f91a1a13f985af5bd");
        queryParam.setCurrent(1);
        queryParam.setSize(10);
        Page<EntSurveyPlanVO> entSurveyPlanList = entSurveyPlanService.pageIntensiveSurveyPlan(queryParam);
        System.out.print("finish");
    }
}
