package info.akwei.ohmybatis.sqlprovider;

import com.google.common.base.CaseFormat;

import java.lang.reflect.Field;

class FieldInfo {

    private Field field;

    private String column;

    FieldInfo(Field field, Boolean needCamelName) {
        this.field = field;
        if (needCamelName != null) {
            if (needCamelName) {
                this.column = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, this.field.getName());
            } else {
                this.column = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, this.field.getName());
            }
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

}
