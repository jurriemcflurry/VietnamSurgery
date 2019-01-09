package toning.juriaan.Models;

import android.util.Pair;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class FormContent {
    @Expose
    @SerializedName("FormId")
    private int formId;

    @Expose
    @SerializedName("FormContent")
    private Pair<String, String> formContent;

    @Expose
    @SerializedName("Images")
    private ArrayList<Byte[]> images;

    private ArrayList<String> imagePaths;

    public FormContent(int formId) {
        this.formId = formId;
    }

    public int getFormId() {
        return formId;
    }

    public void setFormId(int formId) {
        this.formId = formId;
    }

    public Pair<String, String> getFormContent() {
        return formContent;
    }

    public void setFormContent(Pair<String, String> formContent) {
        this.formContent = formContent;
    }

    public ArrayList<Byte[]> getImages() {
        return images;
    }

    public void setImages(ArrayList<Byte[]> images) {
        this.images = images;
    }

    public ArrayList<String> getImagePaths() {
        return imagePaths;
    }

    public void setImagePaths(ArrayList<String> imagePaths) {
        this.imagePaths = imagePaths;
    }
}
