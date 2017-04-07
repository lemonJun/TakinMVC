package learn.dynamicinvoke;

import org.junit.Test;

import com.takin.mvc.mvc.converter.ConverterFactory;

public class TestBind {

    private final ConverterFactory converter = new ConverterFactory();

    @Test
    public void testBind() throws Exception {

        String id = "80";

        Boolean boolean1 = new Boolean(false);

        boolean1.booleanValue();

        BindDemo demo = new BindDemo();

        //		m.invoke(demo, 100);
        //
        //		Field[] fields = demo.getClass().getDeclaredFields();
        //		Object[] os = new Object[fields.length];
        //		for(int i = 0; i < fields.length; i++){
        //			Method m = (Method) demo.getClass().getMethod("set", int.class);
        //			os[i] = converter.convert(fields[i].getType(), id);
        //			m.invoke(demo, os[i]);
        //			
        //			
        //		}
        //		

    }
}
