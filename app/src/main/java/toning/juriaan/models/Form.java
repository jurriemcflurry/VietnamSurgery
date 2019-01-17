package toning.juriaan.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import responsemodels.FormulierenResponse;

public class Form {
    @Expose
    @SerializedName("Id")
    private int id;

    @Expose
    @SerializedName("Name")
    private String formName;

    @Expose
    @SerializedName("Region")
    private String region;

    @Expose
    @SerializedName("FormTemplate")
    private String formTemplateJson;

    public Form() {
    }

    public Form(FormulierenResponse response) {
        id = response.getId();
        formName = response.getName();
        region = response.getRegion();
        formTemplateJson = response.getFormTemplate();
    }

    private FormTemplate parseResponseTemplate(String templateJson) {
        return FormTemplate.fromJson(templateJson);
    }

    public Form(String formName, String region, FormTemplate formTemplate) {
        this.formName = formName;
        this.region = region;
        this.formTemplateJson = formTemplate.toJson();
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

    public String getFormTemplateJson() {
        return formTemplateJson;
    }

    public void setFormTemplateJson(String formTemplateJson) {
        this.formTemplateJson = formTemplateJson;
    }

    public FormTemplate getFormTemplate() {
        return FormTemplate.fromJson(formTemplateJson);
    }

    public void setFormTemplate(FormTemplate formTemplate) {
        setFormTemplateJson(formTemplate.toJson());
    }

    public static Form fromJson(String jsonString) {
        Form form = Helper.getGson().fromJson(jsonString, Form.class);
        return form;
    }

    public String toJson() {
        return Helper.getGson().toJson(this);
    }

    public static Form getDummyForm() {
        FormTemplate formTemplate = getDummyFormTemplate();

        return new Form("Dummy form", "Dummy region", formTemplate);
    }

    private static FormTemplate getDummyFormTemplate() {
        ArrayList<Field> fields = new ArrayList<>();
        fields.add(new Field("TextFieldName", FieldType.String.toString()));

        fields.add(new Field("NumberFieldName", FieldType.Number.toString()));
        ArrayList<String> options = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            options.add("Option " + i);
        }
        fields.add(new Field("DropDownFieldName", FieldType.Choice.toString()));
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
