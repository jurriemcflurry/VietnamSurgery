package toning.juriaan.Models;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FormContent {
    @Expose
    @SerializedName("FormId")
    private int formId;

    @Expose
    @SerializedName("FormContent")
    private Map<String, String> formContent;

    @Expose
    @SerializedName("ImagePaths")
    private ArrayList<String> imagePaths;

    @Expose
    @SerializedName("FormContentName")
    private String formContentName;

    public FormContent(int formId) {
        this.formId = formId;
        formContent = new HashMap<>();
    }

    public String getFormContentName() {
        return formContentName;
    }

    public void setFormContentName(String[] fieldNames, Context context) {
        String name = "";

        for (String fieldName : fieldNames) {
            for (Map.Entry<String, String> entry : formContent.entrySet()) {
                if (entry.getKey().toLowerCase().equals(fieldName.toLowerCase())) {
                    name += entry.getValue() + "_";
                    break;
                }
            }
        }

        name = name.toLowerCase().replaceAll(" ", "_");

        name += Storage.getFormContentAmount(name, context) + 1;

        Helper.log("FormContent.getFormContentName() " + name);
        this.formContentName = name;
    }

    public int getFormId() {
        return formId;
    }

    public void setFormId(int formId) {
        this.formId = formId;
    }

    public Map<String, String> getFormContent() {
        return formContent;
    }

    public void setFormContent(Map<String, String> formContent) {
        this.formContent = formContent;
    }

    public void addAnswer(String key, String value) {
        boolean add = true;

        for (Map.Entry<String, String> entry : formContent.entrySet()) {
            if (entry.getKey().equals(key)) {
                entry.setValue(value);
                add = false;
                break;
            }
        }

        if (add) {
            formContent.put(key, value);
        }
    }

    public ArrayList<String> getImagePaths() {
        return imagePaths;
    }

    public void setImagePaths(ArrayList<String> imagePaths) {
        this.imagePaths = imagePaths;
    }

    public String toJson() {
        return Helper.getGson().toJson(this);
    }
}
