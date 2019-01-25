package toning.juriaan.models;

import android.content.Context;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormContentCallback implements Callback<Void> {

    private FormContent formContent;
    private FormContentUploadProgress progress;
    private Context context;
    private FormContentUploadModel uploadModel;


    public FormContentCallback(FormContent formContent, FormContentUploadProgress progress, Context context) {
        this.formContent = formContent;
        this.progress = progress;
        this.context = context;
    }

    @Override
    public void onResponse(Call<Void> call, Response<Void> response) {
        progress.addResponse();
        if (response.isSuccessful()) {
            Storage.deleteFormContent(formContent, context);
        } else {
            progress.addError(response.message());
        }
    }

    @Override
    public void onFailure(Call<Void> call, Throwable t) {
        progress.addResponse();
        progress.addError(t.getMessage());
    }
}
