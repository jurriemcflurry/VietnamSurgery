package toning.juriaan.Models;


public class Field {
    private String fieldName;
    private Boolean required;
    private String type;

    private String[] options;

    public Field() {
    }

    public Field(String fieldName, String type) {
        this.fieldName = fieldName;
        this.type = type;
        this.required = false;
        Helper.log(this.type);
    }

    public String[] getOptions() throws Exception {
        if (!type.equals(FieldType.choice.toString())) {
            throw new Exception("This is not a dropdown field.");
        }
        return options;
    }

    public void setOptions(String[] options) throws Exception {
        if (!type.equals(FieldType.choice.toString())) {
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
