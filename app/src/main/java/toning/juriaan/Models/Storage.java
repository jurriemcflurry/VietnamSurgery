package toning.juriaan.Models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class Storage {


    public static Form getForm(String formattedFormName, Context context) {
        try {
            File file = Storage.getFormTemplateFile(formattedFormName, context);
            return Form.fromJson(readFile(file));
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
                Form form = Form.fromJson(readFile(templates[i]));
                forms.add(form);
            }

            return forms;
        } catch (Exception e) {
            return null;
        }
    }

    private static String readFile(File file) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close();
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean saveForm(Form form, Context context) {
        Boolean success = false;

        try {
            File file = getFormTemplateFile(form.getFormattedFormName(), context);
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file, false));

            writer.write(form.toJson());
            writer.flush();
            writer.close();
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return success;
    }

    public static boolean saveFormContent(FormContent formContent, Context context) {
        Boolean success = false;

        try {
            File file = getFormContentFile(formContent.getFormContentName(), context);
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file, false));

            writer.write(formContent.toJson());
            writer.flush();
            writer.close();
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return success;
    }

    public static boolean saveImage(Image image, Context context) {
        Boolean succes = false;

        try {
            FileOutputStream fos = new FileOutputStream(
                    getImageFile(image.getImageName(), context));

            image.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return succes;
    }

    public static ArrayList<Image> getImagesForFormContent(FormContent formContent, Context context) {
        try {
            File imageDir = getImagesDir(context);
            File[] imageFiles = imageDir.listFiles();

            ArrayList<Image> images = new ArrayList<>();

            for (File image : imageFiles) {
                if (image.getName().toLowerCase().contains(formContent.getFormContentName().toLowerCase())) {
                    images.add(getImage(image.getName(), context));
                }
            }

            return images;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Image getImage(String imageName, Context context) {
        try {
            return new Image(imageName.split(".png")[0], BitmapFactory.decodeFile(imageName));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Form getFormById(int formId, Context context) {
        try {
            ArrayList<Form> forms = getForms(context);
            if (forms != null) {
                for (Form form : forms) {
                    if (form.getId() == formId) {
                        return form;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static FormContent getFormContent(String formContentName, Context context) {
        try {
            File file = getFormContentFile(formContentName, context);
            return FormContent.fromJson(readFile(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getFormContentAmount(String formContentName, Context context) {
        try {
            File[] files = getFormContentDir(context).listFiles();
            int counter = 0;
            for (File file : files) {
                String name = file.getName();
                name = name.split(".json")[0];
                String[] splitName = name.split("_");
                String[] splitFormContentName = formContentName.split("_");
                boolean same = true;
                for (int i = 0; i < splitFormContentName.length; i++) {
                    if (!splitName[i].equals(splitFormContentName[i])) {
                        same = false;
                        break;
                    }
                }
                if (same) {
                    counter++;
                }
            }

            return counter;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
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

    private static File getImageFile(String imageName, Context context) throws Exception {
        File file = new File(getImagesDir(context), imageName + ".png");
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
        return getDir("form_template", context);
    }

    private static File getFormContentDir(Context context) throws Exception {
        return getDir("form_content", context);
    }

    private static File getImagesDir(Context context) throws Exception {
        return getDir("images", context);
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
