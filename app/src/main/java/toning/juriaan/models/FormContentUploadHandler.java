package toning.juriaan.models;

import android.content.Context;

import java.util.ArrayList;

import webinterfaces.FormWebInterface;

public class FormContentUploadHandler implements Runnable {

    private Context context;

    private ArrayList<FormContent> formContents;

    private FormWebInterface client;

    public FormContentUploadHandler(ArrayList<FormContent> formContents, Context context, FormWebInterface client) {
        this.context = context;
        this.formContents = formContents;
        this.client = client;
    }

    @Override
    public void run() {
        int i = 0;
        while (i < formContents.size()) {
            ArrayList<Thread> threads = new ArrayList<>();
            int j = i;
            while (j < i + 5 && j < formContents.size()) {
                FormContent formContent = formContents.get(j);
                FormContentCallHandler handler = new FormContentCallHandler(formContent, context, client);
                Thread thread = new Thread(handler);
                threads.add(thread);
                thread.start();
                Helper.log("Call handler started. " + j);
                j++;
            }

            for (Thread t : threads) {
                try {
                    t.join();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            i = j;
        }
    }
}
