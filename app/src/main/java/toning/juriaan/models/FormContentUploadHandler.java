package toning.juriaan.models;

import android.content.Context;

import java.util.ArrayList;

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
            Helper.log("isRunning " + isRunning);
            int i = 0;
            while (i < formContents.size()) {
                ArrayList<Thread> threads = new ArrayList<>();
                int j = i;
                while (j < i + 5 && j < formContents.size()) {
                    FormContent formContent = formContents.get(j);
                    FormContentUploadCallHandler handler = new FormContentUploadCallHandler(
                            formContent,
                            context,
                            client,
                            progressListener.getProgress());

                    Thread thread = new Thread(handler);
                    threads.add(thread);
                    thread.start();
                    Helper.log("Call handler started. " + formContent.getFormContentId());
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
