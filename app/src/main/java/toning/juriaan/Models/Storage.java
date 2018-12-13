package toning.juriaan.Models;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Storage {
    public static Form getFormTemplate(String formattedFormName, Context context) {
        try {
            File file = Storage.getFormTemplateFile(formattedFormName, context);
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close();
            return Form.fromJson(stringBuilder.toString());
        } catch (Exception  e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean saveFormTemplate(Form form, Context context) {
        Boolean success = false;

        try {
            String formJson = form.toJson();
            File file = Storage.getFormTemplateFile(form.getFormattedFormName(), context);
            FileOutputStream fOut = new FileOutputStream(file, false);

            OutputStreamWriter writer = new OutputStreamWriter(fOut);

            writer.write(formJson);
            writer.flush();
            writer.close();
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return success;
    }

    private static File getFormTemplateFile(String formattedFormName, Context context) throws Exception {
        File file = new File(getFormTemplateDirPath(context), formattedFormName + ".json");
        if (!checkFile(file)) {
            throw new Exception("Something went wrong with finding/creating a file for path: " + file.getAbsolutePath());
        }

        return file;
    }

    private static boolean checkFile(File file) {
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    Helper.log("Making file failed");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return file.exists();
    }

    private static File getFormTemplateDirPath(Context context) throws Exception {
        File file = new File(context.getFilesDir().getAbsoluteFile().getAbsolutePath() + "/FormTemplates/");
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
