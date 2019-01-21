package toning.juriaan.models;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    public static Map<Integer, Integer> getAmountById(Context context) {
        @SuppressLint("UseSparseArrays") Map<Integer, Integer> amountById = new HashMap<>();

        ArrayList<Form> forms = getForms(context);
        ArrayList<FormContent> formContents = getFormContents(context);

        for (Form form : forms) {
            try {
                amountById.put(form.getId(), 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (FormContent formContent : formContents) {
            for (Map.Entry<Integer, Integer> entry : amountById.entrySet()) {
                try {
                    if (entry.getKey() == formContent.getFormId()) {
                        entry.setValue(entry.getValue() + 1);
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return amountById;
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
            File file = getFormContentFile(formContent.getFormContentId(), context);
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
        Boolean success = false;

        try {
            saveThumbnail(image, context);
            Bitmap fullImageBitMap = image.getImageBitmap(context);

            int newWidth = fullImageBitMap.getWidth();
            int newHeight = fullImageBitMap.getHeight();
            if (fullImageBitMap.getWidth() > fullImageBitMap.getHeight()) {
                if (fullImageBitMap.getWidth() > 1000) {
                    newWidth = 1000;
                    newHeight = (1000 * fullImageBitMap.getHeight()) / fullImageBitMap.getWidth();
                }
            } else {
                if (fullImageBitMap.getHeight() > 1000) {
                    newWidth = (1000 * fullImageBitMap.getWidth()) / fullImageBitMap.getHeight();
                    newHeight = 1000;
                }
            }

            Bitmap resizedImageBitmap = ThumbnailUtils.extractThumbnail(
                    fullImageBitMap, newWidth, newHeight);
            Helper.log("new size " + newWidth + " " + newHeight);
            Helper.log("resized " + resizedImageBitmap.getWidth() + " " + resizedImageBitmap.getHeight());
            image.setImageBitmap(resizedImageBitmap);


            FileOutputStream fos = new FileOutputStream(
                    getImageFile(image.getImageName(), context));
            Bitmap resetImage = image.getImageBitmap(context);

            Helper.log("reset " + resetImage.getWidth() + " " + resetImage.getHeight());

            resetImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }

    public static boolean saveThumbnail(Image image, Context context) {
        boolean success = false;

        try {
            Bitmap imageThumbnail = ThumbnailUtils.extractThumbnail(
                    image.getImageBitmap(context),
                    Helper.THUMBNAIL_SIZE, Helper.THUMBNAIL_SIZE);

            FileOutputStream fos = new FileOutputStream(
                    getThumbnailFile(image.getThumbnailName(), context));

            imageThumbnail.compress(Bitmap.CompressFormat.PNG, 100, fos);

            success = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return success;
    }

    public static File getImageFileWithName(String fileName, Context context) {
        try {
            return getImageFile(fileName, context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean deleteFormContent(FormContent formContent, Context context) {
        boolean success = true;
        try {
            File file = getFormContentFile(formContent.getFormContentId(), context);
            if (file == null) return false;

            for (String imageName : formContent.getImageNames()) {
                success = deleteImage(new Image(imageName), context);
            }

            success = file.delete() && success;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }

    public static boolean deleteImage(Image image, Context context) {
        boolean success = false;
        try {
            File imageFile = getImageFile(image.getImageName(), context);
            File thumbnailFile = getThumbnailFile(image.getThumbnailName(), context);
            success = imageFile.delete() && thumbnailFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }

    public static boolean deleteAllFormContent(Context context) {
        boolean success = false;
        try {
            ArrayList<FormContent> formContentFiles = getFormContents(context);

            boolean failed = false;
            for (FormContent formContent : formContentFiles) {
                failed = !deleteFormContent(formContent, context);
            }

            success = !failed;
        } catch (Exception e) {
            e.printStackTrace();
        }

        cleanStorage(context);

        return success;
    }

    public static void cleanStorage(Context context) {
        cleanFormTemplateDir(context);
        cleanFormContentDir(context);
        cleanImgDir(context);
    }

    public static void cleanFormContentDir(Context context) {
        try {
            File[] files = getFormContentDir(context).listFiles();
            for (File file : files) {
                if (file.length() == 0) {
                    file.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void cleanFormTemplateDir(Context context) {
        try {
            File[] files = getFormTemplateDir(context).listFiles();
            for (File file : files) {
                if (file.length() == 0) {
                    file.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void cleanImgDir(Context context) {
        try {
            File[] files = getImagesDir(context).listFiles();
            for (File file : files) {
                if (file.length() == 0) {
                    file.delete();
                }
            }
            cleanThumbnailDir(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void cleanThumbnailDir(Context context) {
        try {
            File[] files = getThumbnailDir(context).listFiles();
            for (File file : files) {
                if (file.length() == 0) {
                    file.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Image> getImagesForFormContent(FormContent formContent, Context context) {
        try {
            ArrayList<Image> images = new ArrayList<>();
            File imageDir = getImagesDir(context);
            File[] imageFiles = imageDir.listFiles();

            for (File image : imageFiles) {
                if (image.getName().toLowerCase().contains(formContent.getFormContentId().toLowerCase())) {
                    images.add(getImage(image, context));
                }
            }
            return images;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Image getImageByName(String imageName, Context context) {
        try {
            File[] imageFiles = getImagesDir(context).listFiles();

            for (File imageFile : imageFiles) {
                if (imageFile.getName().toLowerCase().contains(imageName.toLowerCase())) {
                    return getImage(imageFile, context);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Image getImage(File imageFile, Context context) {
        try {
            String imageName = imageFile.getName().split(".png")[0];
            String imagePath = imageFile.getAbsolutePath();
            return new Image(imageName, Uri.fromFile(imageFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void getImageBitmap(Image image, Context context) {
        try {
            File imageFile = getImageFile(image.getImageName(), context);
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.fromFile(imageFile));
            image.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getThumbnailBitmap(Image image, Context context) {
        try {
            File thumbnailFile = getThumbnailFile(image.getThumbnailName(), context);
            Bitmap bitmap = BitmapFactory.decodeFile(thumbnailFile.getAbsolutePath());
            image.setThumbnailBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public static FormContent getFormContentById(String formContentName, Context context) {
        try {
            File file = getFormContentFile(formContentName, context);
            return FormContent.fromJson(readFile(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<FormContent> getFormContents(Context context) {
        ArrayList<FormContent> formContents = new ArrayList<>();

        ArrayList<String> names = getFormContentNames(context);
        for (String name : names) {
            formContents.add(getFormContentById(name, context));
        }

        return formContents;
    }

    public static ArrayList<String> getFormContentNames(Context context) {
        ArrayList<String> names = new ArrayList<>();

        try {
            File[] files = getFormContentDir(context).listFiles();
            for (File file : files) {
                if (file.length() > 0 && file.getName().contains(".json")) {
                    names.add(file.getName().split(".json")[0]);
                } else {
                    file.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return names;
    }

    public static int getNextFormContentNumber(String formContentName, Context context) {
        try {
            cleanStorage(context);
            File[] files = getFormContentDir(context).listFiles();
            int highestNumber = 0;
            for (File file : files) {
                if (file.length() <= 0) continue;
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
                    try {
                        int fileNumber = Integer.parseInt(splitName[splitName.length - 1]);
                        if (fileNumber >= highestNumber) {
                            highestNumber = fileNumber + 1;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            return highestNumber;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int getNextImageNumber(FormContent formContent, Context context) {
        cleanStorage(context);
        ArrayList<Image> images = getImagesForFormContent(formContent, context);
        int highestNumber = 0;
        for (Image image : images) {
            String[] splitImageName = image.getImageName().split("_");
            int imageNumber = Integer.parseInt(splitImageName[splitImageName.length - 1]);
            if (imageNumber >= highestNumber) {
                highestNumber = imageNumber + 1;
            }
        }
        return highestNumber;
    }

    public static void makeLogEntry(String entry, Context context) {
        try {
            File logFile = getLogFile(context);
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(logFile, true));

            writer.append(entry);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
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
        if (!imageName.contains(Helper.IMAGE_EXTENSION))
            imageName += Helper.IMAGE_EXTENSION;

        File file = new File(getImagesDir(context), imageName);
        checkFile(file);
        return file;
    }

    private static File getThumbnailFile(String thumbnailName, Context context) throws Exception {
        if (!thumbnailName.contains(Helper.IMAGE_EXTENSION))
            thumbnailName += Helper.IMAGE_EXTENSION;

        File file = new File(getThumbnailDir(context), thumbnailName);
        checkFile(file);
        return file;
    }

    private static File getLogFile(Context context) throws Exception {
        File file = new File(getLogDir(context), "log.txt");
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

    private static File getThumbnailDir(Context context) throws Exception {
        return getDir("thumbnails", context);
    }

    private static File getLogDir(Context context) throws Exception {
        return getDir("log", context);
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