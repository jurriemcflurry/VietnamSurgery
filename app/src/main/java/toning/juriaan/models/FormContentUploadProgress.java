package toning.juriaan.models;

import java.util.ArrayList;

public class FormContentUploadProgress {
    private int responses;
    private int uploadTotal;
    private ArrayList<String> errors;
    private ProgressListener progressListener;

    public FormContentUploadProgress(ProgressListener progressListener) {
        this.responses = 0;
        this.uploadTotal = 0;
        this.errors = new ArrayList<>();
        this.progressListener = progressListener;
    }

    public int getResponses() {
        return responses;
    }

    public void setResponses(int responses) {
        this.responses = responses;
    }

    public int getUploadTotal() {
        return uploadTotal;
    }

    public void setUploadTotal(int uploadTotal) {
        this.uploadTotal = uploadTotal;
    }

    public ArrayList<String> getErrors() {
        return errors;
    }

    public void setErrors(ArrayList<String> errors) {
        this.errors = errors;
    }

    public void addResponse() {
        synchronized (this) {
            responses++;
            progressListener.updateProgressView();
        }

    }

    public void clearResponses() {
        responses = 0;
    }

    public void addError(String error) {
        synchronized (this) {
            errors.add(error);
            progressListener.updateProgressView();
        }
    }

    public void clearErrors() {
        errors.clear();
    }

    public boolean isDone() {
        synchronized (this) {
            return responses >= uploadTotal;
        }
    }

    public String getErrorMessage() {
        StringBuilder stringBuilder = new StringBuilder("errors:\n");
        for (String error : errors) {
            String entry = error + "\n";
            stringBuilder.append(entry);
        }

        return stringBuilder.toString();
    }
}
