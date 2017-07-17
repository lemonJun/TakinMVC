package com.takin.mvc.mvc;

import java.io.File;
import java.io.IOException;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.takin.mvc.mvc.inject.MVCDI;

/**
 * 
 * 利用Filter来实行调度
 * 
 */
@WebFilter(urlPatterns = { "/*" }, dispatcherTypes = { DispatcherType.REQUEST }, initParams = { @WebInitParam(name = "encoding", value = "UTF-8") }, asyncSupported = true)
public class MvcFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(MvcFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ServletContext servletContext = filterConfig.getServletContext();
        try {
            Env.init();
            PropertyConfigurator.configure(Env.getNamespaceConfigFolder() + File.separator + "log4j.properties");
            
            //初始化分发器    核心初始化类
            InitHelper.instance.init(servletContext);
            
            //读自已的初始化
            MVCDI.getInstance(Dispatcher.class).init();
            logger.info("mvcfilter init succ.");
        } catch (Exception e) {
            servletContext.log("failed to wf initialize, system exit!!!", e);
            System.exit(1);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpReq = (HttpServletRequest) request;

        HttpServletResponse httpResp = (HttpServletResponse) response;

        if (request.getCharacterEncoding() == null) {
            request.setCharacterEncoding("UTF-8");
        }
        MVCDI.getInstance(Dispatcher.class).service(httpReq, httpResp);
    }

    @Override
    public void destroy() {

    }
}
