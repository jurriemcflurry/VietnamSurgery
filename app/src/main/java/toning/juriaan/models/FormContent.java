package toning.juriaan.models;

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
import java.util.UUID;

public class FormContent {
    @Expose
    @SerializedName("FormId")
    private int formId;

    @Expose
    @SerializedName("FormContent")
    private Map<String, String> formContentAnswers;

    @Expose
    @SerializedName("ImageNames")
    private ArrayList<String> imageNames;

    @Expose
    @SerializedName("FormContentName")
    private String formContentName;

    @Expose
    @SerializedName("FormContentDate")
    private Date formContentDate;

    @Expose
    @SerializedName("formContentId")
    private String formContentId;


    public FormContent(int formId) {
        this.formId = formId;
        formContentAnswers = new HashMap<>();
        imageNames = new ArrayList<>();
        formContentId = UUID.randomUUID().toString();
        updateDate();
    }

    public void updateDate() {
        formContentDate = Calendar.getInstance().getTime();
    }

    public String getFormContentDate() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd/MM HH:mm");
        return format.format(formContentDate);
    }

    public String updateFormContentName(Context context) {
        StringBuilder nameBuilder = new StringBuilder();

        String[] nameComponents = new String[]{
                getAnswer(context.getString(R.string.name)),
                getAnswer(context.getString(R.string.district)),
                getAnswer(context.getString(R.string.birthYear))
        };

        for (String s : nameComponents) {
            if (!s.isEmpty()) {
                nameBuilder.append(s);
                nameBuilder.append(" ");
            }
        }

        formContentName = nameBuilder.toString().trim();
        return formContentName;
    }

    public String getFormContentName() {
        return formContentName;
    }

    public String getFormContentId() {
        return formContentId;
    }

    private void setFormContentId(String formContentId) {
        this.formContentId = formContentId;
    }

    public boolean isValidInfo(Context context) {
        String name = getAnswer(context.getString(R.string.name));
        String district = getAnswer(context.getString(R.string.district));
        String birthYear = getAnswer(context.getString(R.string.birthYear));

        return (!name.isEmpty() && !district.isEmpty() && !birthYear.isEmpty());
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

    public Map<String, String> getFormContentAnswers() {
        return formContentAnswers;
    }

    public void setFormContentAnswers(Map<String, String> formContentAnswers) {
        this.formContentAnswers = formContentAnswers;
    }

    public void addAnswer(String fieldName, String value) {
        boolean add = true;
        if (value != null)
            value = value.trim();

        for (Map.Entry<String, String> entry : formContentAnswers.entrySet()) {
            if (entry.getKey().equals(fieldName)) {
                entry.setValue(value);
                add = false;
                break;
            }
        }

        if (add) {
            formContentAnswers.put(fieldName, value);
        }
    }

    public String getAnswer(String fieldName) {
        for (Map.Entry<String, String> entry : formContentAnswers.entrySet()) {
            if (entry.getKey().toLowerCase().replaceAll(" ", "").equals(
                    fieldName.toLowerCase().replaceAll(" ", ""))) {
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

    public String getNextImageName() {
        int nextImageNumber = 0;

        for (String imageName : imageNames) {
            String[] splitImageName = imageName.replaceAll(Helper.IMAGE_EXTENSION, "").split("_");
            int imageNumber = Integer.valueOf(splitImageName[splitImageName.length - 1]);
            if (imageNumber >= nextImageNumber) {
                nextImageNumber = imageNumber + 1;
            }
        }

        return getFormContentId() + "_image_" + nextImageNumber;
    }

    public String toJson() {
        return Helper.getGson().toJson(this);
    }

    public static FormContent fromJson(String json) {
        return Helper.getGson().fromJson(json, FormContent.class);
    }

    public static FormContent createTemp(FormContent formContent) {
        if (formContent.getFormContentId().contains(Helper.TEMP)) return formContent;

        FormContent tempFormContent = new FormContent(formContent.getFormId());

        tempFormContent.setFormContentId(formContent.getFormContentId() + Helper.TEMP);
        tempFormContent.setImageNames(formContent.getImageNames());
        tempFormContent.setFormContentAnswers(formContent.getFormContentAnswers());

        return tempFormContent;
    }

    public void confirm() {
        if (formContentId.contains(Helper.TEMP)) {
            formContentId = formContentId.replaceAll(Helper.TEMP, "");
            for (int i = 0; i < imageNames.size(); i++) {
                imageNames.set(i, imageNames.get(i).replaceAll(Helper.TEMP, ""));
            }
        }
    }
}
