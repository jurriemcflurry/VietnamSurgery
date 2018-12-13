package toning.juriaan.Models;

public class FormContent {
    private FieldContent[] fields;

    public FormContent() { }

    public FormContent(FieldContent[] fields) {
        this.fields = fields;
    }

    public FieldContent[] getFields() {
        return fields;
    }

    public void setFields(FieldContent[] fields) {
        this.fields = fields;
    }
}
