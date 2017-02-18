package learn.guice;

import com.google.inject.Inject;

public class DemoTwo {

    @Inject
    private DemoOne one;
    
    public void say() {
        one.say();
    }

}
