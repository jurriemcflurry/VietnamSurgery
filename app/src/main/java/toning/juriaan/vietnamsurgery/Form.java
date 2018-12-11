package toning.juriaan.vietnamsurgery;

import com.google.gson.Gson;

import java.util.ArrayList;

public class Form {

    private String formName;

    private Section[] sections;

    public Form(){}

    public Form(String formName, Section[] sections) {
        this.formName = formName;
        this.sections = sections;
    }

    public static Form fromJson(String jsonString) {
        Gson gson = new Gson();
        Form form = gson.fromJson(jsonString, Form.class);
        return form;
    }

    public String getFormName() {
        return formName;
    }

    public String getFormattedFormName() {
        return formName.replace(' ', '_').toLowerCase();
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public Section[] getSections() {
        return sections;
    }

    public void setSections(Section[] sections) {
        this.sections = sections;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static Form getDummyForm() {
        ArrayList<Field> fields = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            fields.add(new Field("TextFieldName " + i, FieldType.TEXT.toString()));
        }

        fields.add(new Field("NumberFieldName 1", FieldType.NUMBER.toString()));
        ArrayList<String> options = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            options.add("Option " + i);
        }
        fields.add(new Field("DropDownFieldName 1", FieldType.DROP_DOWN.toString()));
        try {
            fields.get(fields.size() - 1).setOptions(options.toArray(new String[0]));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Field[] fieldArray = fields.toArray(new Field[0]);

        ArrayList<Section> sections = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            sections.add(new Section("SectionName " + i, fieldArray));
        }

        Section[] sectionArray = sections.toArray(new Section[0]);

        return new Form("DummyForm 1", sectionArray);
    }
}

