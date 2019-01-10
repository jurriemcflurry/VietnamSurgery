package Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.FrameLayout;

import java.lang.reflect.Array;
import java.util.ArrayList;

import WebInterfaces.FormWebInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import toning.juriaan.Models.Form;
import toning.juriaan.Models.FormContent;
import toning.juriaan.Models.FormContentUploadModel;
import toning.juriaan.Models.FormListAdapter;
import toning.juriaan.Models.Helper;
import toning.juriaan.Models.R;
import toning.juriaan.Models.Storage;

public class FormListActivity extends BaseActivity implements FormListAdapter.FormContentlistener {

    private RecyclerView formListRecycler;
    private FormListAdapter formListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.form_list_activity, contentFrameLayout);
        getSupportActionBar().setTitle(getString(R.string.formContent));


        formListAdapter = new FormListAdapter(Storage.getFormContentNames(this), this);
        formListRecycler = findViewById(R.id.form_list_recycler);
        formListRecycler.setAdapter(formListAdapter);
        formListRecycler.setLayoutManager(new LinearLayoutManager(this));
//        postFormContentList();
    }

    protected void postFormContentList() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.testURL))
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();
        FormWebInterface client = retrofit.create(FormWebInterface.class);

        ArrayList<FormContent> formContents = Storage.getFormContents(this);
        for (FormContent formContent : formContents) {
            FormContentUploadModel uploadModel = new FormContentUploadModel(formContent, this);
            Call<Void> call = client.postFormContent(uploadModel);
            Helper.log(Helper.getGson().toJson(uploadModel));
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    try {
                        Helper.log("onResponse() " + response.code() + " " + response.errorBody().string());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Helper.log("onFailure");
                    t.printStackTrace();
                }
            });
        }
    }

    @Override
    public void onItemClick(FormContent formContent) {
        Form form = Storage.getFormById(formContent.getFormId(), this);
        Intent formOverviewIntent = new Intent(getApplicationContext(), FormOverviewActivity.class);
        formOverviewIntent.putExtra(Helper.FORM, form.getFormattedFormName());
        formOverviewIntent.putExtra(Helper.FORM_CONTENT, formContent.getFormContentName());
        startActivity(formOverviewIntent);
    }
}
