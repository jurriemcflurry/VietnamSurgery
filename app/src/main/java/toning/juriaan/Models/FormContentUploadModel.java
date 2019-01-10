package toning.juriaan.Models;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class FormContentUploadModel {

    @Expose
    @SerializedName("Id")
    private int formId;

    @Expose
    @SerializedName("FormContent")
    private String formContent;

    @Expose
    @SerializedName("Images")
    private ArrayList<byte[]> images;

    public FormContentUploadModel(FormContent formContent, Context context) {
        this.formId = formContent.getFormId();
        Gson gson = Helper.getGson();
        this.formContent = gson.toJson(formContent.getFormContent()).toLowerCase();
        ArrayList<Image> imgs = Storage.getImagesForFormContent(formContent, context);
        images = new ArrayList<>();
        for (Image img : imgs) {
            images.add(img.getByteArray());
        }

    }
}
