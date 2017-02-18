package lemon.mvc.mvc.server;

import java.util.Enumeration;

public abstract class SessionHandler {

    public abstract Object get(String name);

    public abstract Object getCreationTime();

    public abstract Enumeration<String> getNames();

    public abstract String getId();

    public abstract long getLastAccessedTime();

    public abstract int getMaxInactiveInterval();

    public abstract void invalidate();

    public abstract boolean isNew();

    public abstract void remove(String name);

    public abstract void set(String name, Object value);

    public abstract void setMaxInactiveInterval(int value);

    public abstract void flush();

}