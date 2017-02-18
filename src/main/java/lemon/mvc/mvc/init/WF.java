package lemon.mvc.mvc.init;

import java.io.File;
import java.io.InputStream;
import java.util.PropertyResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lemon.mvc.mvc.context.ServiceContext;
import lemon.mvc.mvc.context.WFApplicationContext;
import lemon.mvc.mvc.exception.ResourceNotFoundException;
import lemon.mvc.util.Converter;
import lemon.mvc.util.ExceptionUtils;
import lemon.mvc.util.FileUtil;

public class WF {

    private static final Logger logger = LoggerFactory.getLogger(WF.class);

    protected static String namespace;

    public static String CONFIG_FOLDER;//unix系统下没有测试

    private static String LOG_CONFIG_FILE;

    public static String DATASOURCE_CONFIG_FILE;

    public static String LOG_PATH;

    private static ServiceContext services;

    private static String NAMESPACE_CONFIG_FOLDER;

    public static void init() {
        try {
            boolean isMoreContext = false;
            String uspCluster = System.getProperty("WF.uspcluster");
            //            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            //            System.out.println(cl.getResource("META-INF/namespace.properties").getPath());
            //
            //            InputStream inputStream = cl.getResourceAsStream("META-INF/namespace.properties");
            //            PropertyResourceBundle pp = new PropertyResourceBundle(inputStream);

            // 名称空间
            //            namespace = pp.containsKey("namespace") ? pp.getString("namespace") : "";
            namespace = "disconfig";

            if (namespace == null || "".equals(namespace.trim()))
                throw new ResourceNotFoundException("Does not specify a value for the namespace");

            String[] namespaces = namespace.split("/");
            if (namespaces.length == 2) {
                namespace = namespaces[1];
                isMoreContext = true;

            }

            // Ioc描述基本包
            //			scanBasedPackage = "com.bj58";	
            String mode = "";
            if (uspCluster == null) {
                CONFIG_FOLDER = FileUtil.getRootPath() + "/opt/wf/";
                NAMESPACE_CONFIG_FOLDER = FileUtil.getRootPath() + "/opt/wf/${namespace}";
                LOG_PATH = FileUtil.getRootPath() + "/opt/wf/logs/";
                LOG_CONFIG_FILE = CONFIG_FOLDER + "${namespace}/bj58log.properties";
                DATASOURCE_CONFIG_FILE = CONFIG_FOLDER + "${namespace}/db.properties";
                mode = "OFFLINE";
            } else {
                String rootFolder = FileUtil.getRootPath() + "/opt/web/" + uspCluster + "/";
                CONFIG_FOLDER = rootFolder + "wf/conf/";
                if (isMoreContext) {
                    NAMESPACE_CONFIG_FOLDER = rootFolder + "wf/conf/${namespace}";
                    LOG_PATH = rootFolder + "wf/logs/${namespace}/";
                    LOG_CONFIG_FILE = CONFIG_FOLDER + "${namespace}/bj58log.properties";
                    DATASOURCE_CONFIG_FILE = CONFIG_FOLDER + "${namespace}/db.properties";
                } else {
                    NAMESPACE_CONFIG_FOLDER = rootFolder + "wf/conf/${namespace}";
                    LOG_PATH = rootFolder + "wf/logs/";
                    LOG_CONFIG_FILE = CONFIG_FOLDER + "${namespace}/bj58log.properties";
                    DATASOURCE_CONFIG_FILE = CONFIG_FOLDER + "${namespace}/db.properties";
                }
                mode = "ONLINE";
            }
            logger.info("WF START MODE: " + mode + ", CONFIG_FOLDER:" + getConfigFolder());
            // 往配置文件夹拷入各类配置
            //			initConfigFile();
            initServices();
            initConverter();

        } catch (Exception e) {
            logger.error("", e);
            throw ExceptionUtils.makeThrow("META-INF in the classpath folder to ensure that there is 'namespace.properties' configuration file, and specifies the value namespace or vm parameters contain WF.uspcluster", e);
        }
    }

    /**
     * 框架版本号
     * @return WebJ version in file of META-INF/MANIFEST.MF 
     */
    public static String getVersion() {
        Package pkg = WF.class.getPackage();
        return (pkg != null ? pkg.getImplementationVersion() : null);
    }

    private static void initServices() {
        services = new WFApplicationContext();
    }

    /**
     * 返回项目配置文件的名称
     * <br>
     * 所有的配置文件将存放在 {@link CONFIG_FOLDER}
     * @return
     */
    public static String getNamespace() {
        return namespace;
    }

    /**
     * 获取配置路径
     * @return
     */
    public static String getConfigFolder() {
        return CONFIG_FOLDER;
    }

    /**
     * 获取数据库连接池配置文件路径
     * @return 完整路径
     */
    //	public static String getDBConfigFilePath(){
    //		return DATASOURCE_CONFIG_FILE.replace("${namespace}", namespace);
    //	}

    /**
     * 获取Log4j配置文件路径
     * @return 完整路径
     */
    public static String getLog4jConfigFilePath() {
        return LOG_CONFIG_FILE.replace("${namespace}", namespace);
    }

    public static ServiceContext getServiceContext() {

        return services;
    }

    private static void initConverter() {

        String sqlInjectFilePath = WF.getConfigFolder() + WF.getNamespace() + "/sql-inject.properties";
        String htmlEncodeFilePath = WF.getConfigFolder() + WF.getNamespace() + "/html-encode.properties";
        File fSql = new File(sqlInjectFilePath);

        File fHtml = new File(htmlEncodeFilePath);

        if (fSql.exists())
            Converter.initSqlInject(sqlInjectFilePath);

        if (fHtml.exists())
            Converter.initHtmlEncode(htmlEncodeFilePath);

    }

    public static String getRootPath() {
        File file = new File(System.getProperty("user.dir"));
        String path = file.getAbsolutePath().replace('\\', '/');
        path = path.substring(0, path.indexOf('/'));
        return path;
    }

    public static String getLogPath() {
        return LOG_PATH.replace("${namespace}", namespace);
    }
}
