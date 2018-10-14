import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shencai.eil.common.constants.BaseEnum;
import com.shencai.eil.exception.BusinessException;
import com.shencai.eil.gis.model.GisValueVO;
import com.shencai.eil.gis.service.IGisValueClassService;
import com.shencai.eil.grading.entity.TargetMaxMin;
import com.shencai.eil.grading.service.ITargetMaxMinService;
import com.shencai.eil.policy.service.IEnterpriseInfoService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.HashMap;
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

    @Autowired
    private IGisValueClassService gisValueClassService;


    @Test
    public void testGetEntLocation() {
        GisValueVO map = enterpriseInfoService.getEntLocation("28834d513efc4758ad6ea2defc95b7fe");
        log.info(map.toString());
    }

    @Test
    public void testListOtherEntLocation() {
        List<GisValueVO> list = enterpriseInfoService.listOtherEntLocation("24ef051cc42048d0b17472c54c1ce8fb");
        log.info(list.toString());
    }

    @Test
    public void testClassCodesByCodes() {
        List<String> codes = Arrays.asList("R3_3_1_03","R4_1_1_04");
        List<String> classCodes = gisValueClassService.getClassCodesByCodes(codes);
        log.info("testClassCodesByCodes:"+classCodes.toString());
    }

    @Test
    public void test() {
        System.out.println(calculate(1, "5"));;
    }

    @Autowired
    private ITargetMaxMinService targetMaxMinService;

    public double calculate(double value, String weightId) {
        double result;


        List<TargetMaxMin> list = targetMaxMinService.list(new QueryWrapper<TargetMaxMin>()
                .eq("valid", BaseEnum.VALID_YES.getCode()));

        if (CollectionUtils.isEmpty(list)) {
            throw new BusinessException("list is null");
        }

        Map<String, TargetMaxMin> map = new HashMap<>(list.size());

        for (TargetMaxMin targetMaxMin : list) {
            map.put(targetMaxMin.getTargetId(), targetMaxMin);
        }

        TargetMaxMin targetMaxMin = map.get(weightId);
        if (ObjectUtils.isEmpty(targetMaxMin.getMinParamValue())) {

            result = value * 100 / targetMaxMin.getMaxParamValue();
            return result;
        }

        result = (value - targetMaxMin.getMinParamValue()) * 100
                / (targetMaxMin.getMaxParamValue() - targetMaxMin.getMinParamValue());
        return result;
    }
}
