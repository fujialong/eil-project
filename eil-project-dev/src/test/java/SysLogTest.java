import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.jsonzou.jmockdata.JMockData;
import com.shencai.eil.logger.entity.SysLog;
import com.shencai.eil.logger.service.ISysLogService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @program: environment-insurance-decision
 * @description:
 * @author: fujl
 * @create: 2018-09-11 19:42
 **/
public class SysLogTest extends BaseTest {
    @Autowired
    private ISysLogService sysLogService;

    @Test
    public void testAdd() {
        SysLog sysLog = JMockData.mock(SysLog.class);
        sysLogService.save(sysLog);
    }

    @Test
    public void testPage() {
        IPage page = new Page();
        page.setCurrent(0);
        page.setSize(0);

        page = sysLogService.page(page, new QueryWrapper<SysLog>().eq("1", "1"));
      //  sysLogService.selectPage(page);
        System.out.println(page.getRecords().size());
    }
}
