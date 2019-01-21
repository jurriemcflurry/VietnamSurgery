package toning.juriaan.models;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helper {

    public final static String FORM = "Form";
    public final static String FORM_CONTENT = "FormContent";
    public final static String SECTION_INDEX = "SectionIndex";
    public final static String IMAGE_NAME = "ImageName";
    public final static String IMAGE_EXTENSION = ".png";

    public final static int FORM_ACTIVITY_CODE = 100;
    public final static int CAMERA_ACTIVITY_CODE = 101;
    public final static int FORM_OVERVIEW_CODE = 102;
    public final static int CONTENT_SAVED_CODE = 200;
    public final static int UPDATE_CODE = 201;
    public final static int NO_IMAGE_DELETED = 9999;
    public final static int DELETE_IMAGE = 202;

    public final static int ADD_SECTION_CODE = 1;
    public final static int SECTION_ADDED_RESULT_CODE = 2;
    public final static String SECTION_ADDED = "SectionAdded";

    public final static int ADD_QUESTION_CODE = 11;
    public final static int ADD_QUESTION_RESULT_CODE = 12;
    public final static String QUESTION_NAME = "questionName";
    public final static String REQUIRED = "required";
    public final static String OPTIONS = "Options";
    public final static String QUESTION_TYPE_STRING = "Type";
    public final static String QUESTION_SECTION = "Section";

    public final static int THUMBNAIL_SIZE = 400;

    public static void log(String logEntry) {
        System.out.println("-------------- " + logEntry);
    }

    public static void log(String logEntry, Context context) {
        logEntry = "-------------- " + logEntry;
        System.out.println(logEntry);
        Storage.makeLogEntry(logEntry + "\n", context);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static Gson getGson() {
        return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    }
}
