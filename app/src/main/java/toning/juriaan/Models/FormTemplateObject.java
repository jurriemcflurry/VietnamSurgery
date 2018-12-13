package toning.juriaan.Models;

import com.google.gson.annotations.SerializedName;

public class FormTemplateObject {

    @SerializedName("Name")
    private String name;

    @SerializedName("Region")
    private String region;

    @SerializedName("FormTemplate")
    private String formTemplateString;

    public FormTemplateObject(FormTemplate formTemplate) {
        this.name = "test name";
        this.region = "test region";
        this.formTemplateString = formTemplate.toJson().replace("\"", "\\\"");
    }
}
