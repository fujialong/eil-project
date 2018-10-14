import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @program: eil-project
 * @description:
 * @author: fujialong
 * @create: 2018-10-15 03:13
 **/
public class test {

    public static void main(String[] args) {
        //两次 groupBy】
        List<RStudentExam> list = new ArrayList<>();

        RStudentExam rse1 = new RStudentExam();
        rse1.setId(1L);
        rse1.setSchoolId(1L);
        rse1.setStudentId(3L);
        list.add(rse1);

        RStudentExam rse2 = new RStudentExam();
        rse2.setId(1L);
        rse2.setSchoolId(2L);
        rse2.setStudentId(3L);
        list.add(rse2);

        RStudentExam rse3 = new RStudentExam();
        rse3.setId(2L);
        rse3.setSchoolId(1L);
        rse3.setStudentId(3L);
        list.add(rse3);

        RStudentExam rse4 = new RStudentExam();
        rse4.setId(2L);
        rse4.setSchoolId(2L);
        rse4.setStudentId(3L);
        list.add(rse4);

        RStudentExam rse5 = new RStudentExam();
        rse5.setId(2L);
        rse5.setSchoolId(2L);
        rse5.setStudentId(3L);
        list.add(rse5);

        Map<Long, Map<Long, List<RStudentExam>>> map = list
                .stream()
                .collect(Collectors.groupingBy(RStudentExam::getId, Collectors.groupingBy(RStudentExam::getSchoolId)));
        System.out.println(map);
    }



}
