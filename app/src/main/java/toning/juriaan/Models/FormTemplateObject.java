package toning.juriaan.Models;

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

    public FormTemplateObject(FormTemplate formTemplate) {
        this.name = "test name";
        this.region = "test region";
        this.formTemplateString = formTemplate.toJson();//.replace("\"", "\\\"");
        Helper.log(this.formTemplateString);
    }
}
