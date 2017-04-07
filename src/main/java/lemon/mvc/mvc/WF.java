package lemon.mvc.mvc;

import java.io.File;

import lemon.mvc.mvc.exception.ResourceNotFoundException;
import lemon.mvc.util.ExceptionUtils;
import lemon.mvc.util.FileUtil;

public class WF {
    protected static String namespace;
    public static String CONFIG_FOLDER;//unix系统下没有测试
    private static String LOG_CONFIG_FILE;
    public static String DATASOURCE_CONFIG_FILE;
    private static String NAMESPACE_CONFIG_FOLDER;

    public static void init() {
        try {
            namespace = "disconfig";

            if (namespace == null || "".equals(namespace.trim()))
                throw new ResourceNotFoundException("Does not specify a value for the namespace");

            String[] namespaces = namespace.split("/");
            if (namespaces.length == 2) {
                namespace = namespaces[1];
            }

            CONFIG_FOLDER = FileUtil.getRootPath() + "/opt/wf/";
            NAMESPACE_CONFIG_FOLDER = FileUtil.getRootPath() + "/opt/wf/${namespace}";
            LOG_CONFIG_FILE = CONFIG_FOLDER + "${namespace}/bj58log.properties";
            DATASOURCE_CONFIG_FILE = CONFIG_FOLDER + "${namespace}/db.properties";
            // 往配置文件夹拷入各类配置
            //			initConfigFile();
            //            initServices();
            //            initConverter();

        } catch (Exception e) {
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

    //    private static void initServices() {
    //        services = new WFApplicationContext();
    //    }

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
     * 获取Log4j配置文件路径
     * @return 完整路径
    //     */
    //    public static String getLog4jConfigFilePath() {
    //        return LOG_CONFIG_FILE.replace("${namespace}", namespace);
    //    }

    //    public static ServiceContext getServiceContext() {
    //        return services;
    //    }

    //    private static void initConverter() {
    //
    //        String sqlInjectFilePath = WF.getConfigFolder() + WF.getNamespace() + "/sql-inject.properties";
    //        String htmlEncodeFilePath = WF.getConfigFolder() + WF.getNamespace() + "/html-encode.properties";
    //        File fSql = new File(sqlInjectFilePath);
    //
    //        File fHtml = new File(htmlEncodeFilePath);
    //
    //        if (fSql.exists())
    //            Converter.initSqlInject(sqlInjectFilePath);
    //
    //        if (fHtml.exists())
    //            Converter.initHtmlEncode(htmlEncodeFilePath);
    //    }

    public static String getRootPath() {
        File file = new File(System.getProperty("user.dir"));
        String path = file.getAbsolutePath().replace('\\', '/');
        path = path.substring(0, path.indexOf('/'));
        return path;
    }

    //    public static String getLogPath() {
    //        return LOG_PATH.replace("${namespace}", namespace);
    //    }
}
