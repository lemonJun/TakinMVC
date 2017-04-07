package com.takin.mvc.mvc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface IInit {

    public static final Logger logger = LoggerFactory.getLogger(IInit.class);

    public abstract void init();
}
