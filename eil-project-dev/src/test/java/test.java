import com.shencai.eil.common.utils.ObjectUtil;
import com.shencai.eil.scenario.entity.AccidentScenario;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: eil-project
 * @description:
 * @author: fujialong
 * @create: 2018-10-15 03:13
 **/
public class test {

    public static void main(String[] args) {
        TestUser testUser = new TestUser();
        AccidentScenario scenario = new AccidentScenario();
        testUser.setScenario(scenario);
        testUser.setName("");
/*        Field[] fields = testUser.getClass().getDeclaredFields();
        for (Field field : fields) {+
            try {
                field.setAccessible(true);

                System.out.println(ObjectUtil.isEmpty(field.get(testUser)));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }*/
        //String s = new String();
        System.out.println(ObjectUtil.isEmpty(testUser));
       // System.out.println(ObjectUtils.isEmpty(testUser));

    }
}
