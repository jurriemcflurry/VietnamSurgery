package toning.juriaan.vietnamsurgery;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

public abstract class Field {

    public static final String DROP_DOWN = DropDownField.class.getName();
    public static final String TEXT = TextField.class.getName();
    public static final String NUMBER = NumberField.class.getName();

    private String fieldName;

    private Boolean required;

    private String fieldValue;

    public Field(String fieldName) {
        this.fieldName = fieldName;
        this.fieldValue = "niets";
        this.required = false;
    }

    public Field(String fieldName, Boolean required) {
        this.fieldName = fieldName;
        this.required = required;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(@Nullable String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public Boolean isRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public abstract String getType();

    @Override
    public String toString() {
        String s = getType() + "\n";
        s += getFieldName() + "\n";
        s += getFieldValue() + "\n";
        s += isRequired() + "\n";

        return s;
    }
}

