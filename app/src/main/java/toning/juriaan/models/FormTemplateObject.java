package toning.juriaan.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FormTemplateObject {
    @Expose
    @SerializedName("Name")
    private String name;

    @Expose
    @SerializedName("Region")
    private String region;

    @Expose
    @SerializedName("FormTemplate")
    private String formTemplateString;

    public FormTemplateObject(String name, FormTemplate formTemplate) {
        this.name = name;
        this.region = "test region";
        this.formTemplateString = formTemplate.toJson();//.replace("\"", "\\\"");
    }
}
