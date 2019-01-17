package toning.juriaan.models;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Map;

public class FormContentUploadModel {

    @Expose
    @SerializedName("Id")
    private int formId;

    @Expose
    @SerializedName("FormContent")
    private ArrayList<FormContentField> formContent;

    @Expose
    @SerializedName("Images")
    private ArrayList<byte[]> images;

    public FormContentUploadModel(FormContent formContent, Context context) {
        this.formId = formContent.getFormId();
        Gson gson = Helper.getGson();
        this.formContent = new ArrayList<>();
        for (Map.Entry<String, String> entry : formContent.getFormContent().entrySet()) {
            this.formContent.add(new FormContentField(entry.getKey(), entry.getValue()));
        }
        ArrayList<Image> imgs = Storage.getImagesForFormContent(formContent, context);
        images = new ArrayList<>();
        for (Image img : imgs) {
            images.add(img.getByteArray());
        }

    }
}
