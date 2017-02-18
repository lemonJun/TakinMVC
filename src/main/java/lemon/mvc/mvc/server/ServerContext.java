package lemon.mvc.mvc.server;

import com.google.inject.Inject;

import lemon.mvc.mvc.BeatContext;
import lemon.mvc.mvc.Dispatcher;
import lemon.mvc.mvc.client.CookieHandler;
import lemon.mvc.mvc.inject.GuiceDI;

/**
 * 用于获取和设置与后台有关的信息
 * @author renjun
 *
 */
public class ServerContext {

    @Inject
    private Dispatcher dispatcher;

    BeatContext beat;

    CookieHandler cookie;
    SessionHandler session;

    public ServerContext() {
        super();
        this.beat = dispatcher.currentBeatContext();
    }

    /**
     * session处理
     * @return
     */
    public SessionHandler getSessions() {
        if (session == null)
            session = GuiceDI.getInstance(SessionHandler.class);
        return session;
    }

    /**
     * 获得实际位置
     * @return
     */
    public String getRealPath() {
        return beat.getServletContext().getRealPath("/");
    }

    /**
     * 获得部署路径
     * @return
     */
    public String getContextPath() {
        return beat.getServletContext().getContextPath();
    }

}
