import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.converts.SqlServerTypeConvert;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 代码生成器演示
 * </p>
 */
public class MpGenerator {

    /**
     * <p>
     * MySQL 生成演示
     * </p>
     */
    public static void main(String[] args) {
        AutoGenerator mpg = new AutoGenerator();

        // 全局配置
        final GlobalConfig gc = new GlobalConfig();
        gc.setOutputDir("D://");//TODO
        gc.setFileOverride(true);
        gc.setActiveRecord(true);
        gc.setEnableCache(false);// XML 二级缓存
        gc.setBaseResultMap(true);// XML ResultMap
        gc.setBaseColumnList(false);// XML columList
        gc.setAuthor("fujl");//TODO
        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
       // dsc.setDbType(DbType.SQL_SERVER);
        dsc.setDbType(DbType.MYSQL);//TODO
        dsc.setTypeConvert(new SqlServerTypeConvert() {
            // 自定义数据库表字段类型转换【可选】
            public DbColumnType processTypeConvert(String fieldType) {
//                System.out.println("转换类型：" + fieldType);
                String t = fieldType.toLowerCase();
                return !t.contains("char") && !t.contains("text") ? (t.contains("bigint") ? DbColumnType.LONG
                        : (t.contains("int") ? DbColumnType.INTEGER
                        : (!t.contains("date") && !t.contains("time") && !t.contains("year") ? (
                        t.contains("text") ? DbColumnType.STRING
                                : (t.contains("bit") ? DbColumnType.INTEGER
                                : (t.contains("decimal") ? DbColumnType.DOUBLE
                                : (t.contains("clob") ? DbColumnType.CLOB
                                : (t.contains("blog") ? DbColumnType.BLOB
                                : (t.contains("binary")
                                ? DbColumnType.BYTE_ARRAY
                                : (t.contains("float")
                                ? DbColumnType.FLOAT
                                : (t.contains("double")
                                ?
                                DbColumnType
                                        .DOUBLE
                                : (!t.contains(
                                "json")
                                && !t
                                .contains(
                                        "enum")
                                ?
                                DbColumnType.STRING
                                : DbColumnType.STRING)))))))))
                        : DbColumnType.DATE))) : DbColumnType.STRING;
//                return super.processTypeConvert(fieldType);
            }
        });
        //dsc.setDriverName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        dsc.setDriverName("com.mysql.jdbc.Driver");
        dsc.setUsername("devUser");
        dsc.setPassword("sckj");
        dsc.setUrl("jdbc:mysql://192.168.252.104:3209/mlsc_EIL?useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull");
        mpg.setDataSource(dsc);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);// 表名生成策略
        strategy.setInclude(new String[]{"gis_value_class"}); // 需要生成的表//TODO
        mpg.setStrategy(strategy);

        // 包配置
        final String moduleName = "gis";//TODO
        final PackageConfig pc = new PackageConfig();
        pc.setParent("com.shencai.eil");//TODO
        pc.setEntity(MessageFormat.format("{0}.entity", moduleName));
        pc.setController(MessageFormat.format("{0}.controller", moduleName));
        pc.setService(MessageFormat.format("{0}.service", moduleName));
        pc.setServiceImpl(MessageFormat.format("{0}.service.impl", moduleName));
        pc.setMapper(MessageFormat.format("{0}.mapper", moduleName));
        mpg.setPackageInfo(pc);

        // 注入自定义配置，可以在 VM 中使用 cfg.abc 【可无】
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("abc", this.getConfig().getGlobalConfig().getAuthor() + "-mp");
                this.setMap(map);
            }
        };

        List<FileOutConfig> focList = new ArrayList<FileOutConfig>();
        // 调整 xml 生成目录演示
        focList.add(new FileOutConfig("/templates/mapper.xml.vm") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                return gc.getOutputDir() + (pc.getParent() + "/" + moduleName).replace(".", "/")
                        + "/mapper/" + tableInfo.getEntityName() + "Mapper.xml";
            }
        });
        cfg.setFileOutConfigList(focList);
        mpg.setCfg(cfg);

        // 关闭默认 xml 生成，调整生成 至 根目录
        TemplateConfig tc = new TemplateConfig();
        tc.setXml(null);
        mpg.setTemplate(tc);
        // 执行生成
        mpg.execute();

    }
}