package demo.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.takin.mvc.mvc.IInit;

public class InitMain implements IInit {

    private static final Logger logger = LoggerFactory.getLogger(InitMain.class);

    @Override
    public void init() {
        logger.info("test test init ");
    }

}
