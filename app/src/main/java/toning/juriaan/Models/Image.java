package toning.juriaan.Models;

import android.graphics.Bitmap;

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

    public Byte[] getByteArray() {
        return null;
    }
}
