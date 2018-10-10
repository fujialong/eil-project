import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shencai.eil.common.constants.BaseEnum;
import com.shencai.eil.common.constants.GradeLineResultCode;
import com.shencai.eil.grading.entity.TargetWeightGradeLine;
import com.shencai.eil.grading.service.IGradingService;
import com.shencai.eil.grading.service.ITargetWeightGradeLineService;
import com.shencai.eil.policy.model.EnterpriseVO;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;

import static com.shencai.eil.grading.service.impl.GradingServiceImpl.HIGH_SCORE;

/**
 * @program: eil-project
 * @description:
 * @author: fujl
 * @create: 2018-09-28 13:43
 **/
public class GradingTest extends BaseTest {

    @Autowired
    private IGradingService gradingService;
    @Autowired
    private ITargetWeightGradeLineService targetWeightGradeLineService;


    @Test
    public void testCalculateRtowPontTwoAboutGis() {
        EnterpriseVO enterpriseVO = new EnterpriseVO();
        enterpriseVO.setId("28834d513efc4758ad6ea2defc95b7fe");
      gradingService.gisTest(enterpriseVO);
    }




}
