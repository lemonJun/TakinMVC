package learn.guice;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.google.inject.TypeLiteral;

public class TypeLiteralDemo {

    public static void main(String[] args) throws Exception {
        //
        System.out.println(String.format("guice type:%s", TypeLiteral.get(Boolean.class)));
        System.out.println(String.format("java type:%s", Boolean.class));
        System.out.println();
        //
        System.out.println(String.format("guice type:%s", TypeLiteral.get(int.class)));
        System.out.println(String.format("java type:%s", int.class));
        System.out.println();
        //
        System.out.println(String.format("guice type:%s", new TypeLiteral<Map<Integer, String>>() {
        }));
        System.out.println(String.format("java type:%s", new HashMap<Integer, String>().getClass()));
        System.out.println();
        //
        Method m = Map.class.getMethod("keySet", new Class[0]);
        System.out.println(String.format("java type:%s", m.getReturnType()));
        System.out.println(String.format("java generic type:%s", m.getGenericReturnType()));
        System.out.println(String.format("guice type:%s", TypeLiteral.get(m.getGenericReturnType())));
        System.out.println();
        TypeLiteral<Map<Integer, String>> mapType = new TypeLiteral<Map<Integer, String>>() {
        };
        System.out.println(String.format("guice type:%s", mapType.getReturnType(m)));
    }
}
