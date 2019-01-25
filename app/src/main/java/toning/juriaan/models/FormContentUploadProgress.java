package toning.juriaan.models;

import java.util.ArrayList;

public class FormContentUploadProgress {
    private int uploadedFormContents;
    private int totalFormContents;
    private ArrayList<String> errors;
    private ProgressListener progressListener;

    public FormContentUploadProgress(int totalFormContents, ProgressListener progressListener) {
        this.uploadedFormContents = 0;
        this.totalFormContents = totalFormContents;
        this.errors = new ArrayList<>();
        this.progressListener = progressListener;
        progressListener.setMax(totalFormContents);
    }

    public int getUploadedFormContents() {
        return uploadedFormContents;
    }

    public void setUploadedFormContents(int uploadedFormContents) {
        this.uploadedFormContents = uploadedFormContents;
    }

    public int getTotalFormContents() {
        return totalFormContents;
    }

    public void setTotalFormContents(int totalFormContents) {
        this.totalFormContents = totalFormContents;
    }

    public ArrayList<String> getErrors() {
        return errors;
    }

    public void setErrors(ArrayList<String> errors) {
        this.errors = errors;
    }

    public void addResponse() {
        synchronized (this) {
            uploadedFormContents++;
            progressListener.updateProgressView();
        }

    }

    public void addError(String error) {
        synchronized (this) {
            errors.add(error);
            progressListener.updateProgressView();
            Helper.log("addError() " + error);
        }
    }
}
