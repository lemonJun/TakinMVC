package com.takin.mvc.mvc.context;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.takin.mvc.mvc.Env;
import com.takin.mvc.mvc.inject.MVCDI;

/**
 * 这个类是做什么的
 * 
 * 
 * @author Administrator
 * @version 1.0
 * @date  2016年8月24日 下午11:20:59
 * @see 
 * @since
 */
public class WFApplicationContext implements ServiceContext {

    private static final Logger logger = LoggerFactory.getLogger(WFApplicationContext.class);

    private final static Map<String, ClazzInfo> clazzInfos = new ConcurrentHashMap<String, ClazzInfo>();

    public static Module getModule() throws Exception {

        String filePath = Env.getConfigFolder() + Env.getNamespace() + "/service-context.xml";
        File f = new File(filePath);// new File(getServiceConfigFilePath());

        if (!f.exists())
            return new Module() {

                @Override
                public void configure(Binder binder) {

                }
            };

        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(new File(filePath));
        Element rootElement = document.getRootElement();

        parseXML(rootElement);
        logger.info("clazzInfos.size=" + clazzInfos.size());

        Module module = new Module() {

            @Override
            public void configure(Binder binder) {

                for (Map.Entry<String, ClazzInfo> entry : clazzInfos.entrySet()) {
                    String key = entry.getKey().toString();
                    String value = entry.getValue().toString();
                    logger.info("key=" + key + " value=" + value);
                    ClazzInfo instance = entry.getValue();

                    binder.bind(instance.getClazz()).asEagerSingleton();
                }

            }
        };
        return module;
    }

    @Override
    public Object getBean(String name) {
        return MVCDI.getInstance(clazzInfos.get(name).getClazz());
    }

    @Override
    public boolean containsBean(String name) {

        return clazzInfos.get(name) != null;
    }

    @Override
    public boolean isSingleton(String name) {

        return clazzInfos.get(name).isSingleton();
    }

    @SuppressWarnings("unchecked")
    private static void parseXML(Element element) throws Exception {

        List<Element> beanElements = element.elements("bean");

        for (Element e : beanElements) {

            String key = e.attributeValue("id");
            if (key == null || "".equals(key))
                key = e.attributeValue("name");

            if (key == null || "".equals(key))
                throw new Exception("bean need id");

            String clazzName = e.attributeValue("class");

            if (clazzName == null || "".equals(clazzName))
                throw new Exception("bean need class");

            Class<?> clazz = Class.forName(clazzName);
            ClazzInfo info = new ClazzInfo();
            info.setClazz(clazz);
            info.setSingleton(true);

            clazzInfos.put(key, info);

        }

        List<Element> importElements = element.elements("import");

        for (Element e : importElements) {

            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(new File(Env.getConfigFolder() + Env.getNamespace() + "/" + e.attributeValue("resource")));
            Element rootElement = document.getRootElement();
            parseXML(rootElement);

        }

    }

}
