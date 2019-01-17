package toning.juriaan.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Field {
    @Expose
    @SerializedName("Name")
    private String fieldName;

    @Expose
    @SerializedName("Required")
    private Boolean required;

    @Expose
    @SerializedName("Type")
    private String type;

    @Expose
    @SerializedName("Options")
    private String[] options;

    public Field() {
    }

    public Field(String fieldName, String type) {
        this.fieldName = fieldName;
        this.type = type;
        this.required = false;
    }

    public String[] getOptions() throws Exception {
        if (!type.equals(FieldType.Choice.toString())) {
            throw new Exception("This is not a dropdown field.");
        }
        return options;
    }

    public void setOptions(String[] options) throws Exception {
        if (!type.equals(FieldType.Choice.toString())) {
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
        s += getType() + "\n";

        try {
            for (String option : getOptions()) {
                s += option + ", ";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        s += "\n";
        s += isRequired() + "\n";

        return s;
    }
}
