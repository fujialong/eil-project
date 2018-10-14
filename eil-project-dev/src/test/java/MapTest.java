import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: eil-project
 * @description:
 * @author: fujl
 * @create: 2018-10-09 16:29
 **/
@Slf4j
public class MapTest {

    @Test
    public void testCompare() {

        Map<String, Object> map = new HashMap<>();
        map.put("1", "fu");
        map.put("2", "jia");

        Map<String, Object> map1 = new HashMap<>();
        map1.put("1", "fu");
        map1.put("3", "jia");

    }


}
