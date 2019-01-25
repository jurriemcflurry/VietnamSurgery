package toning.juriaan.models;

public interface ProgressListener {
    void updateProgressView();
    void setMax(int max);
    FormContentUploadProgress getProgress();
}
