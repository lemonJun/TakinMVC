package learn;

import java.io.File;

import com.takin.mvc.mvc.WF;

/**
 * 管理配置文件
 * 临时性的
 * @author renjun
 *
 */
public class ConfigManager {

    private static boolean hasCopy = false;

    public static void copyConfig() {

        if (hasCopy)
            return;

        hasCopy = true;

        File destFolder = new File(WF.getConfigFolder() + WF.getNamespace() + "/");

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        File root;
        try {
            root = new File(cl.getResource("").toURI());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        String path = root.getAbsolutePath() + "/config";
        File configFolder = new File(path);
        if (!configFolder.exists() || configFolder.isFile())
            return;

        Copy.copy(configFolder, destFolder);

    }
}
