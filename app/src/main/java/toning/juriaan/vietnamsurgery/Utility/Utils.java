package toning.juriaan.vietnamsurgery.Utility;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.StatFs;
import android.support.v4.app.ActivityCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class Utils {

    /**
     * Fields that we use around this application
     */
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final String chosenDirKey = "toning.juriaan.vietnamSurgery.chosenDir";
    private static SharedPreferences prefs;

    /**
     * Function to get chosenDirKey
     */
    private static String getChosenDirKey() {
        return chosenDirKey;
    }

    /**
     * Function to set sharedPrefs
     */
    public static void setSharedPrefs(Context context) {
        prefs = context.getSharedPreferences("toning.juriaan.vietnamsurgery", Context.MODE_PRIVATE);
    }

    /**
     * Method to get the rootDir for the files
     */
    public static String getRootDir() {
        return prefs.getString(getChosenDirKey(), null);
    }

    /**
     * Method to remove the rootDir for the files
     */
    public static void removeRootDirFromPrefs() {
        prefs.edit().remove(getChosenDirKey()).apply();
    }

    /**
     * Method to edit the rootDir in sharedPrefs
     */
    public static boolean editRootDirInPrefs(String dir) {
        return prefs.edit().putString(getChosenDirKey(), dir).commit();
    }

    /**
     * Function to check permissions. If we don't have these permissions, we're going to ask for them
     * @param activity activity you're using this prompt im
     * @return boolean
     */
    public static boolean verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
            return false;
        } else {
            return true;
        }
    }

    /**
     * Method to get a list of files that contains xlsx
     * @param parentDir File directory were we should be checking for files
     * @return List of files containing xlsx
     */
    public static List<File> getListOfExcelFiles(File parentDir) {
        ArrayList<File> inFiles = new ArrayList<>();
        File[] files = parentDir.listFiles();

        for( File file : files) {
            if(file.getName().substring(file.getName().lastIndexOf('.') + 1).equals("xlsx")) {
                inFiles.add(file);
            }
        }

        return inFiles;
    }

    /**
     * Method to check if there is enough space left. It check's for 100Mb
     * @param path path where to check
     * @return boolean
     */
    public static boolean isEnoughSpaceLeftOnOnDevice(String path) {
        StatFs stats = new StatFs(path);
        long bytes = stats.getAvailableBytes();
        return bytes >= 104857600;
    }
}
