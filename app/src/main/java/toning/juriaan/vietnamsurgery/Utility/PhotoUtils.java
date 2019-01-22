package toning.juriaan.vietnamsurgery.Utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.io.File;
import java.util.List;

import toning.juriaan.vietnamsurgery.R;
import toning.juriaan.vietnamsurgery.activity.DetailPhotoActivity;
import toning.juriaan.vietnamsurgery.model.FormTemplate;

public final class PhotoUtils {

    private static final int BACK_TO_PREVIOUS_ACTIVITY = 100;

    /**
     * Method to go to the detailPage of the photo
     * @param index Int which photo that has to be opened
     */
    public static void goToDetailPage(int index, Activity activity, FormTemplate form) {
        Intent intent = new Intent(activity, DetailPhotoActivity.class);
        intent.putExtra("obj_form", form);
        intent.putExtra("photoIndex", index);
        activity.startActivityForResult(intent, BACK_TO_PREVIOUS_ACTIVITY);
    }

    /**
     * Method to delete the picture
     * @param photoUrl String with absolute path
     * @return boolean
     */
    public static boolean deletePhoto(String photoUrl, FormTemplate form, File storageDirPng, Context context) {
        File jpgFile = new File(photoUrl);
        if(jpgFile.exists() && jpgFile.delete()) {
            List<String> pics = form.getPictures();
            pics.remove(photoUrl);
            form.setPictures(pics);
            File pngFile = new File(storageDirPng, jpgFile.getName().replace("jpg", "png"));
            if(pngFile.exists() && pngFile.delete()){
                List<String> thumbs = form.getThumbImages();
                thumbs.remove(pngFile.getAbsolutePath());
                form.setThumbImages(thumbs);
                return true;
            } else {
                Log.e(context.getClass().getSimpleName(), context.getString(R.string.error_delete_thumb));
                new AlertDialog.Builder(context)
                        .setTitle(R.string.dialog_warning_title)
                        .setMessage(context.getString(R.string.error_delete_thumb))
                        .setPositiveButton(context.getString(R.string.dialog_ok), null).show();
                return false;
            }
        }
        else {
            Log.e(context.getClass().getSimpleName(), context.getString(R.string.error_delete_pic));
            new AlertDialog.Builder(context)
                    .setTitle(R.string.dialog_warning_title)
                    .setMessage(context.getString(R.string.error_delete_pic))
                    .setPositiveButton(context.getString(R.string.dialog_ok), null).show();
            return false;
        }
    }
}
