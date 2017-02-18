//package learn;
//
//import java.io.File;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//import learn.testioc.Hello;
//import learn.testioc.Hello2;
//
//import org.dom4j.Document;
//import org.dom4j.Element;
//import org.dom4j.io.SAXReader;
//import org.junit.Test;
//
//import com.bj58.wf.ioc.context.ClazzInfo;
//import com.google.inject.Binder;
//import com.google.inject.Guice;
//import com.google.inject.Injector;
//import com.google.inject.Module;
//
//public class TestIocAdapter {
//
//	private final String path = "D:/opt/wf/test/";
//
//	private final Map<String, ClazzInfo> map = new ConcurrentHashMap<String, ClazzInfo>();
//
//	@Test
//	public void testParse() throws Exception {
//
//		SAXReader saxReader = new SAXReader();
//		Document document = saxReader.read(new File(path
//				+ "service-context.xml"));
//		Element rootElement = document.getRootElement();
//
//		parseXML(rootElement);
//		System.out.println(map.size());
//		
//		Module module = new Module() {
//
//			@Override
//			public void configure(Binder binder) {
//
//				for (Map.Entry<String, ClazzInfo> entry : map.entrySet()) {
//					String key = entry.getKey().toString();
//					String value = entry.getValue().toString();
//					System.out.println("key=" + key + " value=" + value);
//					ClazzInfo instance = entry.getValue();
//					binder.bind(instance.getClazz()).asEagerSingleton() ;
//				}
//
//			}
//		};
//
//		Injector injector = Guice.createInjector(module);
//		Hello hello = injector.getInstance(map.get("hello1").getClazz());
//		hello.function1();
//		
//		Hello2 hello2 = injector.getInstance(map.get("hello2").getClazz());
//		hello2.function1();
//		
////		injector.createChildInjector(module);
//	}
//
//	@SuppressWarnings("unchecked")
//	private void parseXML(Element element) throws Exception {
//
//		List<Element> beanElements = element.elements("bean");
//
//		for (Element e : beanElements) {
//
//			String key = e.attributeValue("id");
//			if (key == null || "".equals(key))
//				key = e.attributeValue("name");
//
//			if (key == null || "".equals(key))
//				throw new Exception("bean need id");
//
//			String clazzName = e.attributeValue("class");
//
//			if (clazzName == null || "".equals(clazzName))
//				throw new Exception("bean need class");
//
//			Class<?> clazz = Class.forName(clazzName);
//			ClazzInfo info = new ClazzInfo();
//			info.setClazz(clazz);
//			info.setSingleton(true);
//
//			map.put(key, info);
//
//		}
//
//		List<Element> importElements = element.elements("import");
//
//		for (Element e : importElements) {
//
//			SAXReader saxReader = new SAXReader();
//			Document document = saxReader.read(new File(path
//					+ e.attributeValue("resource")));
//			Element rootElement = document.getRootElement();
//			parseXML(rootElement);
//
//		}
//
//	}
//}
