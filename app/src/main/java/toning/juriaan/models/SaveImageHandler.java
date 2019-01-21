package toning.juriaan.models;

import android.content.Context;
import android.net.Uri;

import java.util.ArrayList;

public class SaveImageHandler implements Runnable {

    private Image image;

    private Context context;

    public SaveImageHandler(Image image, Context context) {
        this.image = image;
        this.context = context;
    }

    @Override
    public void run() {
        if (image.getImageBitmap(context) == null)
            return;

        try {
            Storage.saveImage(image, context);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
