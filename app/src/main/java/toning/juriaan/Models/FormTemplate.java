package toning.juriaan.Models;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class FormTemplate {

    @Expose
    @SerializedName("Sections")
    private Section[] sections;

    public FormTemplate(){}

    public FormTemplate(Section[] sections) {
        this.sections = sections;
    }

    public Section[] getSections() {
        return sections;
    }

    public void setSections(Section[] sections) {
        this.sections = sections;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public static FormTemplate fromJson(String templateJson) {
        return new Gson().fromJson(templateJson, FormTemplate.class);
    }


}
