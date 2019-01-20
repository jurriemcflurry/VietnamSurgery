package toning.juriaan.models;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class FormTemplate {

    @Expose
    @SerializedName("Sections")
    private ArrayList<Section> sections;

    public FormTemplate(){}

    public FormTemplate(ArrayList<Section> sections) {
        this.sections = sections;
    }

    public ArrayList<Section> getSections() {
        return sections;
    }

    public void setSections(ArrayList<Section> sections) {
        this.sections = sections;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public static FormTemplate fromJson(String templateJson) {
        return new Gson().fromJson(templateJson, FormTemplate.class);
    }


}

