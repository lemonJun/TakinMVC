package com.takin.mvc.mvc.internal;

import com.takin.mvc.mvc.annotation.Path;

public class PathInfo {

    private Path typeAnn;

    private Path methodAnn;

    private String typePath;

    private String methodPath;

    private int typeOrder;

    private int methodOrder;

    public Path getTypeAnn() {
        return typeAnn;
    }

    public void setTypeAnn(Path typeAnn) {
        this.typeAnn = typeAnn;
    }

    public Path getMethodAnn() {
        return methodAnn;
    }

    public void setMethodAnn(Path methodAnn) {
        this.methodAnn = methodAnn;
    }

    public String getTypePath() {
        return typePath;
    }

    public void setTypePath(String typePath) {
        this.typePath = typePath;
    }

    public String getMethodPath() {
        return methodPath;
    }

    public void setMethodPath(String methodPath) {
        this.methodPath = methodPath;
    }

    public int getTypeOrder() {
        return typeOrder;
    }

    public void setTypeOrder(int typeOrder) {
        this.typeOrder = typeOrder;
    }

    public int getMethodOrder() {
        return methodOrder;
    }

    public void setMethodOrder(int methodOrder) {
        this.methodOrder = methodOrder;
    }

}
