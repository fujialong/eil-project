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
        String excelUrl = entSurveyFileService.generateExcel("54b4ec41837f4fe6a62bc9fb732f341f"
                , FileSourceType.BASIC_SURVEY_PLAN_EXCEL.getCode());
        System.out.print(excelUrl);
    }

    @Test
    public void testWord() {
       String pdfFile = entSurveyFileService.getFastGradingResult("54b4ec41837f4fe6a62bc9fb732f341f");
       System.out.print(pdfFile);
    }
}
