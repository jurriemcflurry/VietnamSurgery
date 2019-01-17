package toning.juriaan.models;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

public class Image {
    String imageName;

    Bitmap bitmap;

    public Image(String imageName, Bitmap bitmap) {
        this.imageName = imageName;
        this.bitmap = bitmap;
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

    public byte[] getByteArray() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
}
