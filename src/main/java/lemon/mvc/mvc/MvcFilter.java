package lemon.mvc.mvc;

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

import lemon.mvc.mvc.inject.GuiceDI;

/**
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
            WF.init();
            PropertyConfigurator.configure(WF.getConfigFolder() + File.separator + WF.getNamespace() + File.separator + "log4j.properties");
            logger.info("MVC CONFIG_FOLDER:" + WF.getConfigFolder());

            //初始化分发器    核心初始化类
            InitHelper.instance.init(servletContext);

            //读自已的初始化
            GuiceDI.getInstance(Dispatcher.class).init();
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
        GuiceDI.getInstance(Dispatcher.class).service(httpReq, httpResp);
    }

    @Override
    public void destroy() {

    }
}
