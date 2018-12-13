package toning.juriaan.Models;

import com.google.gson.Gson;

import java.util.ArrayList;

public class Form {
    private int id;
    private String formName;
    private String region;
    private FormTemplate formTemplate;
    private FormContent formContent;

    public Form() {
    }

    public Form(String formName, String region, FormTemplate formTemplate, FormContent formContent) {
        this.formName = formName;
        this.region = region;
        this.formTemplate = formTemplate;
        this.formContent = formContent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public FormTemplate getFormTemplate() {
        return formTemplate;
    }

    public void setFormTemplate(FormTemplate formTemplate) {
        this.formTemplate = formTemplate;
    }

    public FormContent getFormContent() {
        return formContent;
    }

    public void setFormContent(FormContent formContent) {
        this.formContent = formContent;
    }

    public static Form fromJson(String jsonString) {
        Gson gson = new Gson();
        Form form = gson.fromJson(jsonString, Form.class);
        return form;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public static Form getDummyForm() {
        FormTemplate formTemplate = getDummyFormTemplate();
        FormContent formContent = getDummyFormContent(formTemplate);

        return new Form("Dummy form", "Dummy region", formTemplate, formContent);
    }

    private static FormContent getDummyFormContent(FormTemplate formTemplate) {
        ArrayList<FieldContent> contents = new ArrayList<>();
        for (Section section : formTemplate.getSections())
            for (Field field : section.getFields())
                contents.add(new FieldContent(field.getFieldName()));

        return new FormContent(contents.toArray(new FieldContent[0]));
    }

    private static FormTemplate getDummyFormTemplate() {
        ArrayList<Field> fields = new ArrayList<>();
        fields.add(new Field("TextFieldName", FieldType.text.toString()));

        fields.add(new Field("NumberFieldName", FieldType.number.toString()));
        ArrayList<String> options = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            options.add("Option " + i);
        }
        fields.add(new Field("DropDownFieldName", FieldType.choice.toString()));
        try {
            fields.get(fields.size() - 1).setOptions(options.toArray(new String[0]));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Field[] fieldArray = fields.toArray(new Field[0]);

        ArrayList<Section> sections = new ArrayList<>();
        sections.add(new Section("SectionName", fieldArray));

        Section[] sectionArray = sections.toArray(new Section[0]);

        return new FormTemplate(sectionArray);
    }
}
