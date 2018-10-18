import com.shencai.eil.scenario.entity.AccidentScenario;
import lombok.Data;

import java.util.List;

/**
 * @program: eil-project
 * @description:
 * @author: fujl
 * @create: 2018-10-17 17:02
 **/
@Data
public class TestUser {

    private String name;

    private String password;

    private AccidentScenario scenario;
}
