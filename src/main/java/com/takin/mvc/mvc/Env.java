package com.takin.mvc.mvc;

import java.io.File;
import java.io.InputStream;
import java.util.PropertyResourceBundle;

import com.takin.mvc.mvc.exception.MVCException;

public class Env {

    private Env() {
    }

    protected static String namespace = "";
    private static String CONFIG_FOLDER;//unix系统下没有测试
    private static String NAMESPACE_CONFIG_FOLDER;

    public static void init() {
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            InputStream inputStream = cl.getResourceAsStream("META-INF/namespace.properties");
            PropertyResourceBundle pp = new PropertyResourceBundle(inputStream);

            namespace = pp.containsKey("namespace") ? pp.getString("namespace") : "";
            if (namespace == null || "".equals(namespace.trim()))
                throw new MVCException("Does not specify a value for the namespace");

            CONFIG_FOLDER = getRootPath() + "/opt/wf/";
            NAMESPACE_CONFIG_FOLDER = getRootPath() + "/opt/wf/" + namespace;
            System.out.println(String.format(">>config_folder:%s", CONFIG_FOLDER));
            System.out.println(String.format(">>namespace_config_folder:%s", NAMESPACE_CONFIG_FOLDER));
        } catch (Exception e) {
            throw new MVCException("META-INF in the classpath folder to ensure that there is 'namespace.properties' configuration file, and specifies the value namespace or vm parameters contain WF.uspcluster", e);
        }
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

    public static String getNamespaceConfigFolder() {
        return NAMESPACE_CONFIG_FOLDER;
    }

    /**
     * 获取配置路径
     * @return
     */
    public static String getConfigFolder() {
        return CONFIG_FOLDER;
    }

    public static String getRootPath() {
        File file = new File(System.getProperty("user.dir"));
        String path = file.getAbsolutePath().replace('\\', '/');
        path = path.substring(0, path.indexOf('/'));
        return path;
    }

}
