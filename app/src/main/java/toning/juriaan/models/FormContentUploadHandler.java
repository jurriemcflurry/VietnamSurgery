package toning.juriaan.models;

import android.content.Context;

import java.util.ArrayList;

import retrofit2.Call;
import webinterfaces.FormWebInterface;

public class FormContentUploadHandler implements Runnable {

    private Context context;

    private ArrayList<FormContent> formContents;

    private FormWebInterface client;

    private ProgressListener progressListener;

    private boolean isRunning = false;

    public FormContentUploadHandler(
            ArrayList<FormContent> formContents,
            Context context,
            FormWebInterface client,
            ProgressListener progressListener) {
        this.context = context;
        this.formContents = formContents;
        this.client = client;
        this.progressListener = progressListener;
    }

    @Override
    public void run() {
        try {
            isRunning = true;
            int i = 0;
            while (i < formContents.size()) {
                ArrayList<Thread> threads = new ArrayList<>();
                int j = i;
                while (j < i + 5 && j < formContents.size()) {
                    FormContent formContent = formContents.get(j);
                    FormContentUploadModel uploadModel = new FormContentUploadModel(formContent, context);
                    Call<Void> call = client.postFormContent(uploadModel);

                    call.enqueue(new FormContentCallback(formContent, progressListener.getProgress(), context));

                    j++;
                }

                for (Thread t : threads) {
                    try {
                        t.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                i = j;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        isRunning = false;
    }

    public boolean isRunning() {
        return isRunning;
    }
}
