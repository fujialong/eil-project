import com.shencai.eil.scenario.model.AccidentScenarioVO;
import com.shencai.eil.scenario.model.ScenarioSelectionInfoQueryParam;
import com.shencai.eil.scenario.service.IAccidentScenarioService;
import com.shencai.eil.scenario.service.IScenarioSelectionInfoService;
import com.shencai.eil.scenario.service.IScenarioSelectionService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by fanhj on 2018/10/12.
 */
public class ScenarioTest extends BaseTest {
    @Autowired
    private IScenarioSelectionInfoService scenarioSelectionInfoService;
    @Autowired
    private IAccidentScenarioService accidentScenarioService;
    @Autowired
    private IScenarioSelectionService testTrashDischargeService;
    @Test
    public void testPage() {
        ScenarioSelectionInfoQueryParam queryParam = new ScenarioSelectionInfoQueryParam();
        queryParam.setCurrent(1);
        queryParam.setSize(20);
        queryParam.setEnterpriseId("21bc2e7d6fd54019a1d36b36ad07ff15");
        scenarioSelectionInfoService.pageScenarioSelectionInfo(queryParam);
    }

    @Test
    public void testListAccidentScenario() {
        List<AccidentScenarioVO> sclist = accidentScenarioService.listAccidentScenario(null);
        System.out.print(sclist);
    }

    @Test
    public void testInit() {
        scenarioSelectionInfoService.initScenarioSelectionInfo("cd4ed89a81ca4382b62301c8ee6fa525");
    }

    @Test
    public void test() {
        testTrashDischargeService.liquidDrip("674855d5cc3c41238ad2902e58e54ddf");
    }

}
