package toning.juriaan.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import java.io.ByteArrayOutputStream;

public class Image {
    private String imageName;

    private Bitmap imageBitmap;

    private Bitmap thumbnailBitmap;

    private Uri uri;

    public Image(String imageName) {
        this.imageName = imageName;
    }

    public Image(String imageName, Uri uri) {
        this.imageName = imageName;
        this.uri = uri;
    }

    public Image(String imageName, Bitmap bitmap, Uri uri) {
        this.imageName = imageName;
        this.imageBitmap = bitmap;
        this.uri = uri;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public Bitmap getImageBitmap(Context context) {
        if (imageBitmap == null) {
            Storage.getImageBitmap(this, context);
        }
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

    public Bitmap getThumbnailBitmap(Context context) {
        if (thumbnailBitmap == null) {
            Storage.getThumbnailBitmap(this, context);
        }
        return thumbnailBitmap;
    }

    public String getThumbnailName() {
        return imageName.replaceAll("image", "thumbnail");
    }

    public void setThumbnailBitmap(Bitmap thumbnailBitmap) {
        this.thumbnailBitmap = thumbnailBitmap;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public byte[] getByteArray(Context context) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        getImageBitmap(context).compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    public static String getNextImageName(FormContent formContent, Context context) {
        return formContent.getFormContentId() + "_image_" + Storage.getNextImageNumber(formContent, context);
    }
}
