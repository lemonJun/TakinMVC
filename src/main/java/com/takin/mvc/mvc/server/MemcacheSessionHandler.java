package com.takin.mvc.mvc.server;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import com.takin.mvc.mvc.BeatContext;

public class MemcacheSessionHandler extends SessionHandler {

    BeatContext beat;

    Map<String, Object> map = new HashMap<String, Object>();

    public MemcacheSessionHandler(BeatContext beat) {
        this.beat = beat;
    }

    @Override
    public Object get(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getCreationTime() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Enumeration<String> getNames() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getLastAccessedTime() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getMaxInactiveInterval() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void invalidate() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isNew() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void remove(String name) {
        // TODO Auto-generated method stub

    }

    @Override
    public void set(String name, Object value) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setMaxInactiveInterval(int value) {
        // TODO Auto-generated method stub

    }

    @Override
    public void flush() {

    }

    private String getCacheKey() {
        return null;
    }

    private Map<String, Object> getSession() {
        return null;
    }

}
