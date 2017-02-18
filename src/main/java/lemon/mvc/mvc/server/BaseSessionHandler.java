package lemon.mvc.mvc.server;

import java.util.Enumeration;

import javax.servlet.http.HttpSession;

import com.google.inject.Inject;

import lemon.mvc.mvc.BeatContext;
import lemon.mvc.mvc.Dispatcher;

public class BaseSessionHandler extends SessionHandler {

    @Inject
    private Dispatcher dispatcher;

    BeatContext beat;
    HttpSession session;

    public BaseSessionHandler() {
        super();
        this.beat = dispatcher.currentBeatContext();
        session = this.beat.getRequest().getSession();
    }

    /* (non-Javadoc)
     * @see com.bj58.wf.mvc.server.Session#get(java.lang.String)
     */
    @Override
    public Object get(String name) {
        return session.getAttribute(name);

    }

    /* (non-Javadoc)
     * @see com.bj58.wf.mvc.server.Session#getCreationTime()
     */
    @Override
    public Object getCreationTime() {
        return session.getCreationTime();
    }

    /* (non-Javadoc)
     * @see com.bj58.wf.mvc.server.Session#getNames()
     */
    @Override
    public Enumeration<String> getNames() {
        return session.getAttributeNames();
    }

    /* (non-Javadoc)
     * @see com.bj58.wf.mvc.server.Session#getId()
     */
    @Override
    public String getId() {
        return session.getId();
    }

    /* (non-Javadoc)
     * @see com.bj58.wf.mvc.server.Session#getLastAccessedTime()
     */
    @Override
    public long getLastAccessedTime() {
        return session.getLastAccessedTime();
    }

    /* (non-Javadoc)
     * @see com.bj58.wf.mvc.server.Session#getMaxInactiveInterval()
     */
    @Override
    public int getMaxInactiveInterval() {
        return session.getMaxInactiveInterval();
    }

    /* (non-Javadoc)
     * @see com.bj58.wf.mvc.server.Session#invalidate()
     */
    @Override
    public void invalidate() {
        session.invalidate();
    }

    /* (non-Javadoc)
     * @see com.bj58.wf.mvc.server.Session#isNew()
     */
    @Override
    public boolean isNew() {
        return session.isNew();
    }

    /* (non-Javadoc)
     * @see com.bj58.wf.mvc.server.Session#remove(java.lang.String)
     */
    @Override
    public void remove(String name) {
        session.removeAttribute(name);
    }

    /* (non-Javadoc)
     * @see com.bj58.wf.mvc.server.Session#set(java.lang.String, java.lang.Object)
     */
    @Override
    public void set(String name, Object value) {
        session.setAttribute(name, value);
    }

    /* (non-Javadoc)
     * @see com.bj58.wf.mvc.server.Session#setMaxInactiveInterval(int)
     */
    @Override
    public void setMaxInactiveInterval(int value) {
        session.setMaxInactiveInterval(value);
    }

    @Override
    public void flush() {
    }

}
