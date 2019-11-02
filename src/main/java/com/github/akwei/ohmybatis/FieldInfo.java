package com.github.akwei.ohmybatis;

import javax.persistence.Id;
import java.lang.reflect.Field;

class FieldInfo {

    private Field field;

    private String column;

    FieldInfo(Field field, boolean mapUnderscoreToCamelCase) {
        this.field = field;
        if (mapUnderscoreToCamelCase) {
            this.column=CaseUtils.lowerCamelToLowerUnderScore(this.field.getName());
        } else {
            this.column = this.field.getName();
        }
    }

    Field getField() {
        return field;
    }

    String getColumn() {
        return column;
    }

     boolean isId() {
        return field.getAnnotation(Id.class) != null;
    }
}
