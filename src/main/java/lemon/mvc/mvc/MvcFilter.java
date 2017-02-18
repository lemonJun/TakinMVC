package lemon.mvc.mvc;

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

import lemon.mvc.mvc.init.InitHelper;
import lemon.mvc.mvc.init.WF;
import lemon.mvc.mvc.inject.GuiceDI;

/**
 * 利用Filter来实行调度
 * 
 */
@WebFilter(urlPatterns = { "/*" }, dispatcherTypes = { DispatcherType.REQUEST }, initParams = { @WebInitParam(name = "encoding", value = "UTF-8") }, asyncSupported = true)
public class MvcFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ServletContext servletContext = filterConfig.getServletContext();
        try {
            //初始化日志文件
            PropertyConfigurator.configure("E:/opt/wf/disconfig/log4j.properties");
            //初始化LOG4J配置

            //初始化环境配置 
            WF.init();

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
