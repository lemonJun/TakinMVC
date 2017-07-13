package com.bj58.wf.mvc.utils;

import org.junit.Test;

import com.takin.mvc.mvc.annotation.Controller;
import com.takin.mvc.util2.AnnotationUtils;

public class AnnoTest {

    @Test
    public void tt() {
        try {
            boolean b = AnnotationUtils.isClassAnnotationed(Class.forName("com.bj58.wf.controllers.DemonController"), Controller.class);
            System.out.println(b);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
