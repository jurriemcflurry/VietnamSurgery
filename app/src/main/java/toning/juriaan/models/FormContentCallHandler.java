package toning.juriaan.models;

import android.content.Context;
import android.content.pm.PackageManager;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import webinterfaces.FormWebInterface;

public class FormContentCallHandler implements Runnable {

    private Context context;

    private FormContent formContent;

    private FormWebInterface client;

    public FormContentCallHandler(FormContent formContent, Context context, FormWebInterface client) {
        this.context = context;
        this.formContent = formContent;
        this.client = client;
    }

    @Override
    public void run() {
        FormContentUploadModel uploadModel = new FormContentUploadModel(formContent, context);
        Call<Void> call = client.postFormContent(uploadModel);
        try {
            Response<Void> response = call.execute();
            Helper.log("FormContentCallHandler.run() " + response.code());
            if (response.code() == 200) {
                Storage.deleteFormContent(formContent, context);
            } else {
                Helper.log(response.message());
                if (response.errorBody() != null) {
                    Helper.log(response.errorBody().string());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        call.enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(Call<Void> call, Response<Void> response) {
//                if (response.code() == 200) {
//                    //incrementUploadCount();
//                } else {
//                    Helper.log("onResponse() " + response.code());
//                    Helper.log(response.message());
//                    try {
//                        Helper.log(response.errorBody().string());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    Helper.log("" + response.isSuccessful());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Void> call, Throwable t) {
//                t.printStackTrace();
//            }
//        });
    }
}
