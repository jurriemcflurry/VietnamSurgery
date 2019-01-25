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
    private byte[][] images;

    @Expose
    @SerializedName("FormTemplateName")
    private String formTemplateName;

    public FormContentUploadModel(FormContent formContent, Context context) {
        this.formId = formContent.getFormId();
        this.formContent = new ArrayList<>();
        Form form = Storage.getFormById(formContent.getFormId(), context);
        if (form != null) {
            formTemplateName = form.getFormName();
        } else {
            formTemplateName = "null";
        }
        for (Map.Entry<String, String> entry : formContent.getFormContentAnswers().entrySet()) {
            this.formContent.add(new FormContentField(entry.getKey(), entry.getValue()));
        }
        ArrayList<Image> imgs = Storage.getImagesForFormContent(formContent, context);
        ArrayList<byte[]> images = new ArrayList<>();
        for (Image img : imgs) {
            images.add(img.getByteArray(context));
        }
        this.images = new byte[images.size()][];
        this.images = images.toArray(this.images);
    }
}
