package toning.juriaan.Models;

import com.google.gson.Gson;

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

    public String toJson() {
        return new Gson().toJson(this);
    }
}
