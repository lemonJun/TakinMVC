package lemon.mvc.mvc.server;

import lemon.mvc.mvc.BeatContext;

public interface SessionFactory {
    public static SessionFactory factory = null;

    SessionHandler get(BeatContext beat);

    //	public static void setFactory(SessionFactory factory) {
    ////		SessionFactory.factory = factory;
    //	}
}
