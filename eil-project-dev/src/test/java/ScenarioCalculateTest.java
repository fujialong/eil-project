import lombok.Builder;
import lombok.Data;
import org.junit.Test;
import org.omg.CORBA.PRIVATE_MEMBER;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @program: eil-project
 * @description:
 * @author: fujl
 * @create: 2018-10-18 20:12
 **/
public class ScenarioCalculateTest extends BaseTest {

    private static final String FZXB = "非装卸臂或软管";
    private static final String ZXB = "装卸臂或软管";
    private static List<Gydy> unitTypes;

    static {
        unitTypes = Arrays.asList(
/*                Gydy.builder().typeName("反应器/工艺储罐/气体储罐").modeNo("1").unit("kg/a").build(),
                Gydy.builder().typeName("反应器/工艺储罐/气体储罐").modeNo("2").unit("kg/a").build(),
                Gydy.builder().typeName("反应器/工艺储罐/气体储罐/塔器").modeNo("3").unit("kg/a").build(),
                Gydy.builder().typeName("常压单包容储罐").modeNo("1").unit("kg/a").build(),
                Gydy.builder().typeName("常压单包容储罐").modeNo("2").unit("kg/a").build(),
                Gydy.builder().typeName("常压单包容储罐").modeNo("3").unit("kg/a").build(),
                Gydy.builder().typeName("常压储罐").modeNo("1").unit("kg/a").build(),
                Gydy.builder().typeName("内径≤75mm 的管道").modeNo("4").unit("kg/a").build(),
                Gydy.builder().typeName("内径≤75mm 的管道").modeNo("4").unit("kg/a").build(),
                Gydy.builder().typeName("75mm<内径≤150mm 的管道").modeNo("4").unit("kg/a").build(),
                Gydy.builder().typeName("75mm<内径≤150mm 的管道").modeNo("5").unit("kg/a").build(),
                Gydy.builder().typeName("内径＞150mm 的管道").modeNo("5").unit("kg/a").build(),
                Gydy.builder().typeName("内径＞150mm 的管道").modeNo("5").unit("kg/a").build(),
                Gydy.builder().typeName("泵体和压缩机").modeNo("5").unit("kg/a").build(),
                Gydy.builder().typeName("泵体和压缩机").modeNo("6").unit("kg/a").build(),
                Gydy.builder().typeName("装卸臂").modeNo("5").unit("kg/a").build(),
                Gydy.builder().typeName("装卸臂").modeNo("6").unit("kg/a").build(),*/
                Gydy.builder().typeName("装卸软管").modeNo("5").unit("kg/a").build(),
                Gydy.builder().typeName("装卸软管").modeNo("6").unit("kg/a").build());

    }

    @Test
    public void test() {
        String type = "Test";
        switch (type) {
            case FZXB:
                getLeakage();
                break;
            case ZXB:
                double result = getLeakage() * getWorkTimes();
                break;
            default:
                break;
        }
    }

    /**
     * 获取工作时间
     *
     * @return
     */
    private double getWorkTimes() {
        return 0;
    }

    /**
     * 获取泄露量
     */
    private double getLeakage() {
        double leakage = 0.0;
        for (Gydy unitType : unitTypes) {
            //泄漏量＝泄漏频率＊泄漏时间＊泄漏速率
            leakage += getleakFrequency() * getLeakageTime("1", "2", "3") * getLeakageRate();
        }
        return 0;
    }

    /**
     * 泄漏速率
     *
     * @return
     */
    private double getLeakageRate() {

        return 0;
    }

    /**
     * 获取泄露时间
     * var1 探测系统等级
     * var2 隔离系统等级
     * var3 气体泄漏时间
     *
     * @return
     */
    private double getLeakageTime(String var1, String var2, String var3) {

        //TODO 通过风险勘察条目获取var1,var2
        //TODO 根据var1,var2 查询探测系统等级表获取气体泄漏时间
        return 0;
    }

    /**
     * 获取泄露频率
     *
     * @return
     */
    private double getleakFrequency() {
        //TODO 直接查表返回
        return 0.0;

    }

    /**
     * 工艺单元
     */
    @Data
    @Builder
    class Gydy {

        private String typeName;

        private String modeNo;

        private String unit;

    }

}
