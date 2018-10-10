import com.shencai.eil.common.constants.FileSourceType;
import com.shencai.eil.survey.service.IEntSurveyFileService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by fanhj on 2018/10/6.
 */
public class SurveyFileTest extends BaseTest {
    @Autowired
    private IEntSurveyFileService entSurveyFileService;

    @Test
    public void testExcel() {
        String excelUrl = entSurveyFileService.generateExcel("ea453cfe70884dfaa8f2347e1c503438"
                , FileSourceType.BASIC_SURVEY_PLAN_EXCEL.getCode());
        System.out.print(excelUrl);
    }

    @Test
    public void testWord() {
       String pdfFile = entSurveyFileService.getFastGradingResult("ea453cfe70884dfaa8f2347e1c503438");
       System.out.print(pdfFile);
    }
}
