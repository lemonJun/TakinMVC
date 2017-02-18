package demo.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lemon.mvc.mvc.init.IInit;

public class InitMain implements IInit {

    private static final Logger logger = LoggerFactory.getLogger(InitMain.class);
    
    @Override
    public void init() {
        logger.info("my init ");
    }

}
