package learn.guice;

import lemon.mvc.mvc.inject.DemoModule;
import lemon.mvc.mvc.inject.GuiceDI;

public class TestGuice {

    //说明了   凡是受guice管理的类 是可以直接注入的   那就不需要构造器注入了啊
    public static void main(String[] args) {
        try {
            GuiceDI.create(new DemoModule());
            GuiceDI.getInstance(DemoTwo.class).say();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
