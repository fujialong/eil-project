import com.shencai.eil.gis.model.GisValueVO;
import com.shencai.eil.gis.service.IGisValueService;
import com.shencai.eil.policy.service.IEnterpriseInfoService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @program: eil-project
 * @description:
 * @author: fujl
 * @create: 2018-09-26 10:52
 **/
@Slf4j
public class GisTest extends BaseTest {

    @Autowired
    private IEnterpriseInfoService enterpriseInfoService;


    @Test
    public void testGetEntLocation() {
        GisValueVO map = enterpriseInfoService.getEntLocation("24ef051cc42048d0b17472c54c1ce8fb");
       log.info(map.toString());
    }

    @Test
    public void testListOtherEntLocation() {
        List<GisValueVO> list = enterpriseInfoService.listOtherEntLocation("24ef051cc42048d0b17472c54c1ce8fb");
        System.out.println(list);
    }
}
