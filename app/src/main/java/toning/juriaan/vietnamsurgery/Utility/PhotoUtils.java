package toning.juriaan.vietnamsurgery.Utility;

import android.app.Activity;
import android.content.Intent;

import java.io.File;

import toning.juriaan.vietnamsurgery.activity.DetailPhotoActivity;
import toning.juriaan.vietnamsurgery.model.FormTemplate;

public final class PhotoUtils {
    private static final int REQUEST_DELETE_IMAGE = 2;

    /**
     * Method to go to the detailPage of the photo
     * @param photoFile File with the photo that has to be opened
     */
    public static void goToDetailPage(File photoFile, Activity activity, FormTemplate form) {
        Intent intent = new Intent(activity, DetailPhotoActivity.class);
        intent.putExtra("obj_form", form);
        intent.putExtra("photoUrl", photoFile.getAbsolutePath());
        activity.startActivityForResult(intent, REQUEST_DELETE_IMAGE);
    }
}
