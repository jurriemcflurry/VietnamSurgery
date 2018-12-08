package toning.juriaan.vietnamsurgery;

import java.util.ArrayList;

public class Form {
    private String formName;

    private Section[] sections;

    public Form(String formName, Section[] sections) {
        this.formName = formName;
        this.sections = sections;
    }

    public Form(String jsonString) {

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
        return "{\"isJson\": true}";
    }

    public static Form getDummyForm() {
        ArrayList<Field> fields = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            fields.add(new TextField("TextFieldName " + i));
        }

        fields.add(new NumberField("NumberFieldName 1"));
        ArrayList<String> options = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            options.add("Option " + 1);
        }
        fields.add(new DropDownField("DropDownFieldName 1", options));

        Field[] fieldArray = fields.toArray(new Field[0]);

        ArrayList<Section> sections = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            sections.add(new Section("SectionName " + i, fieldArray));
        }

        Section[] sectionArray = sections.toArray(new Section[0]);

        return new Form("DummyForm 1", sectionArray);
    }
}

