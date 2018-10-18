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
        String excelUrl = entSurveyFileService.generateExcel("bca423b4465246ea9e2cd2431ae61cc2"
                , FileSourceType.INTENSIVE_SURVEY_PLAN_EXCEL.getCode());
        System.out.print(excelUrl);
    }

    @Test
    public void testWord() {
       String pdfFile = entSurveyFileService.getFastGradingResult("3db638d60fdd43f9b5636345ebac6148");
       System.out.print(pdfFile);
    }

    @Test
    public void uploadExcel() {
        entSurveyFileService.importSurveyResults("74fb0525907c400b84b8040152c88a29", "cd4ed89a81ca4382b62301c8ee6fa525");
    }
}
