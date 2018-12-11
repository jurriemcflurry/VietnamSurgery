package toning.juriaan.vietnamsurgery;


import android.support.annotation.Nullable;

public class Field {
    private String fieldName;
    private String fieldValue;
    private Boolean required;
    private String type;

    private String[] options;

    public Field() { }

    public Field(String fieldName, String type) {
        this.fieldName = fieldName;
        this.fieldValue = "Niets";
        this.required = false;
    }

    public String[] getOptions() throws Exception {
        if (!type.equals(FieldType.DROP_DOWN.toString())) {
            throw new Exception("This is not a dropdown field.");
        }
        return options;
    }

    public void setOptions(String[] options) throws Exception {
        if (!type.equals(FieldType.DROP_DOWN.toString())) {
            throw new Exception("This is not a dropdown field.");
        }
        this.options = options;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        String s = getFieldName() + "\n";
        s += getFieldValue() + "\n";
        s += getType() + "\n";

        try {
            for (String option : getOptions()) {
                s += option + ", ";
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        s += "\n";

        return s;
    }
}
