package info.akwei.ohmybatis;

import java.lang.annotation.Annotation;

public class SqlProviderPinocchio {

    private Class<? extends Annotation> clazz;

    private Class oriType;

    private String oriMethod;

    public Class<? extends Annotation> getClazz() {
        return clazz;
    }

    public void setClazz(Class<? extends Annotation> clazz) {
        this.clazz = clazz;
    }

    public Class getOriType() {
        return oriType;
    }

    public Class type() {
        return oriType;
    }

    public void setOriType(Class oriType) {
        this.oriType = oriType;
    }

    public String getOriMethod() {
        return oriMethod;
    }

    public String method() {
        return oriMethod;
    }

    public void setOriMethod(String oriMethod) {
        this.oriMethod = oriMethod;
    }
}
