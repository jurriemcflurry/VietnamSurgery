package toning.juriaan.models;

import android.content.Context;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import webinterfaces.FormWebInterface;

public class FormContentUploadCallHandler implements Runnable {

    private Context context;

    private FormContent formContent;

    private FormWebInterface client;

    private FormContentUploadProgress progress;

    public FormContentUploadCallHandler(FormContent formContent,
                                        Context context,
                                        FormWebInterface client,
                                        FormContentUploadProgress progress) {
        this.context = context;
        this.formContent = formContent;
        this.client = client;
        this.progress = progress;
    }

    @Override
    public void run() {
        FormContentUploadModel uploadModel = new FormContentUploadModel(formContent, context);
        Call<Void> call = client.postFormContent(uploadModel);
        try {
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        addSuccess();
                    } else {
                        addError(response.message());
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    addError(t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addSuccess() {
        synchronized (progress) {
            progress.addResponse();
        }
    }

    private void addError(String error) {
        synchronized (progress) {
            progress.addError(error);
        }
    }
}
