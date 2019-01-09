package toning.juriaan.Models;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class Storage {
    public static Form getForm(String formattedFormName, Context context) {
        try {
            File file = Storage.getFormTemplateFile(formattedFormName, context);
            return readForm(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<Form> getForms(Context context) {
        try {
            ArrayList<Form> forms = new ArrayList<>();

            File templateDir = getFormTemplateDir(context);
            File[] templates = templateDir.listFiles();

            for (int i = 0; i < templates.length; i++) {
                Form form = readForm(templates[i]);
                forms.add(form);
            }

            return forms;
        } catch (Exception e) {
            return null;
        }
    }

    private static Form readForm(File file) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close();
            return Form.fromJson(stringBuilder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean saveForm(Form form, Context context) {
        Boolean success = false;

        try {
            File file = Storage.getFormTemplateFile(form.getFormattedFormName(), context);
            FileOutputStream fOut = new FileOutputStream(file, false);

            OutputStreamWriter writer = new OutputStreamWriter(fOut);

            writer.write(form.toJson());
            writer.flush();
            writer.close();
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return success;
    }

    public static boolean saveFormContent(Form form, Context context) {
        Boolean success = false;

        try {
//            File file = getFormContentFile(form.getFormContent()[0].getFields()[0].getValue() +
//                    "_" + form.getFormContent()[0].getFields()[1].getValue(), context);


            success = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return success;
    }

    private static File getFormContentFile(String formattedName, Context context) throws Exception {
        File file = new File(getFormContentDir(context), formattedName + ".json");
        checkFile(file);
        return file;
    }

    private static File getFormTemplateFile(String formattedFormName, Context context) throws Exception {
        File file = new File(getFormTemplateDir(context), formattedFormName + ".json");
        checkFile(file);

        return file;
    }

    private static boolean checkFile(File file) throws Exception {
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    Helper.log("Making file failed");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!file.exists()) {
            throw new Exception("Something went wrong with finding/creating a file for path: " + file.getAbsolutePath());
        }

        return file.exists();
    }

    private static File getFormTemplateDir(Context context) throws Exception {
        return getDir("FormTemplates", context);
    }

    private static File getFormContentDir(Context context) throws Exception {
        return getDir("FormContent", context);
    }

    private static File getDir(String dirName, Context context) throws Exception {
        File file = new File(context.getFilesDir().getAbsoluteFile().getAbsolutePath() + "/" + dirName + "/");
        if (!checkDir(file)) {
            throw new Exception("Something went wrong with finding/creating a directory for path: " + file.getAbsolutePath());
        }

        return file;

    }

    private static boolean checkDir(File file) {
        if (!file.exists()) {
            try {
                if (!file.mkdirs()) {
                    Helper.log("Making dir failed");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return file.exists();
    }
}
