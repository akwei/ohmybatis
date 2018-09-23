package com.github.akwei.ohmybatis;

public class SqlProviderPinocchio {

    private String oriMethod;

    @SuppressWarnings("unused") //通过反射调用
    public Class type() {
        return SimpleSQLProvider.class;
    }

    @SuppressWarnings("unused") //通过反射调用
    public String method() {
        return oriMethod;
    }

    void setOriMethod(String oriMethod) {
        this.oriMethod = oriMethod;
    }
}
