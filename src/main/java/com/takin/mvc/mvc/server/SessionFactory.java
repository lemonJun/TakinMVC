package com.takin.mvc.mvc.server;

import com.takin.mvc.mvc.BeatContext;

public interface SessionFactory {
    public static SessionFactory factory = null;

    SessionHandler get(BeatContext beat);

    //	public static void setFactory(SessionFactory factory) {
    ////		SessionFactory.factory = factory;
    //	}
}
