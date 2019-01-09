package toning.juriaan.Models;

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

    public FormContent(int formId) {
        this.formId = formId;
        formContent = new HashMap<>();
    }

    public String getFormContentName(String[] fieldNames) {
        String name = "";

        for (String fieldName : fieldNames) {
            for (Map.Entry<String, String> entry : formContent.entrySet()) {
                if (entry.getKey().equals(fieldName)) {
                    name += entry.getValue() + "_";
                    break;
                }
            }
        }

        Helper.log("FormContent.getFormContentName() " + name);
        return name;
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
}
