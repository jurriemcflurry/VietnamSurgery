package toning.juriaan.Models;

import android.annotation.SuppressLint;
import android.content.Context;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    private ArrayList<String> imageNames;

    @Expose
    @SerializedName("FormContentName")
    private String formContentName;

    @Expose
    @SerializedName("FormContentDate")
    private Date formContentDate;

    public FormContent(int formId) {
        this.formId = formId;
        formContent = new HashMap<>();
        imageNames = new ArrayList<>();
        updateDate();
    }

    public void updateDate() {
        formContentDate = Calendar.getInstance().getTime();
    }

    public String getFormContentDate() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd/MM HH:mm");
        return format.format(formContentDate);
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

        name += Storage.getFormContentNumber(name, context) + 1;

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

    public String getAnswer(String fieldName) {
        for (Map.Entry<String, String> entry : formContent.entrySet()) {
            if (entry.getKey().toLowerCase().equals(fieldName.toLowerCase())) {
                return entry.getValue();
            }
        }
        return "";
    }

    public ArrayList<String> getImageNames() {
        return imageNames;
    }

    public void setImageNames(ArrayList<String> imageNames) {
        this.imageNames = imageNames;
    }

    public void addImageName(String imagePath) {
        imageNames.add(imagePath);
    }

    public String toJson() {
        return Helper.getGson().toJson(this);
    }

    public static FormContent fromJson(String json) {
        return Helper.getGson().fromJson(json, FormContent.class);
    }
}
