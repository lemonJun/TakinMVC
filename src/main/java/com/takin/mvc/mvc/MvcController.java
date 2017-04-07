package com.takin.mvc.mvc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.takin.mvc.mvc.context.BeatContextBean;

/**
 * 兼容
 * 所有的Controller基类
 * @author renjun
 *
 */
public abstract class MvcController extends AbstractController {

    /**
     * 日志系统
     */

    protected static final Logger logger = LoggerFactory.getLogger(MvcController.class);

    /**
     * 在一个请求过程中的上下文
     * 
     * 
     */
    protected BeatContext beat = new BeatContextBean();

}
