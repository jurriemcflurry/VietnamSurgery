package toning.juriaan.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import java.io.ByteArrayOutputStream;

public class Image {
    private String imageName;

    private Bitmap bitmap;

    private Uri uri;

    public Image(String imageName, Bitmap bitmap, Uri uri) {
        this.imageName = imageName;
        this.bitmap = bitmap;
        this.uri = uri;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public byte[] getByteArray() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public static String getNextImageName(FormContent formContent, Context context) {
        return formContent.getFormContentId() + "_image_" + Storage.getNextImageNumber(formContent, context);
    }
}
