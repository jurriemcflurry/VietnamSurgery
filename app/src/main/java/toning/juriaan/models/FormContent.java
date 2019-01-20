package toning.juriaan.models;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;

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
    @SerializedName("ImageNames")
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
        if (formContentName != null) return;
        StringBuilder nameBuilder = new StringBuilder();

        for (String fieldName : fieldNames) {
            String value = getAnswer(fieldName);
            if (!value.isEmpty()) {
                nameBuilder.append(value);
                nameBuilder.append("_");
            }
        }

        String name = nameBuilder.toString().toLowerCase().replaceAll(" ", "_");

        name += Storage.getNextFormContentNumber(name, context);

        formContentName = name;
    }

    public boolean isValidInfo(Context context) {
        String name = getAnswer(context.getString(R.string.name));
        String district = getAnswer(context.getString(R.string.district));
        String birthYear = getAnswer(context.getString(R.string.birthYear));
        String birthyear = getAnswer(context.getString(R.string.birthyear));

        return (!name.isEmpty() && !district.isEmpty() && (!birthYear.isEmpty() || !birthyear.isEmpty()));
    }

    public boolean isValid(Context context) {
        return imageNames.size() > 0 && isValidInfo(context);
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

    public void addAnswer(String fieldName, String value) {
        boolean add = true;

        for (Map.Entry<String, String> entry : formContent.entrySet()) {
            if (entry.getKey().equals(fieldName)) {
                entry.setValue(value);
                add = false;
                break;
            }
        }

        if (add) {
            formContent.put(fieldName, value);
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
        if (imageNames == null) imageNames = new ArrayList<>();
        return imageNames;
    }

    public void setImageNames(ArrayList<String> imageNames) {
        this.imageNames = imageNames;
    }

    public void addImageName(String imageName) {
        imageNames.add(imageName);
    }

    public String toJson() {
        return Helper.getGson().toJson(this);
    }

    public static FormContent fromJson(String json) {
        return Helper.getGson().fromJson(json, FormContent.class);
    }
}
