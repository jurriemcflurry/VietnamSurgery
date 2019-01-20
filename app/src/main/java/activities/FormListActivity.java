package activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;

import webinterfaces.FormWebInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import toning.juriaan.models.Form;
import toning.juriaan.models.FormContent;
import toning.juriaan.models.FormContentUploadModel;
import toning.juriaan.models.FormListAdapter;
import toning.juriaan.models.Helper;
import toning.juriaan.models.R;
import toning.juriaan.models.Storage;

public class FormListActivity extends BaseActivity implements FormListAdapter.FormContentlistener {

    private RecyclerView formListRecycler;
    private FormListAdapter formListAdapter;
    private Integer toUpload = 0;
    private Integer uploadCount = 0;
    private TextView uploadCounter;
    private ArrayList<String> formContentNames;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_form_list, contentFrameLayout);
        getSupportActionBar().setTitle(getString(R.string.formContentFormList));

        uploadCounter = findViewById(R.id.upload_counter);
        uploadCounter.setVisibility(View.INVISIBLE);

        formContentNames = Storage.getFormContentNames(this);
        formListAdapter = new FormListAdapter(formContentNames, this);
        formListRecycler = findViewById(R.id.form_list_recycler);
        formListRecycler.setAdapter(formListAdapter);
        formListRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    protected void postFormContentList() {
        toUpload = formContentNames.size();
        updateUploadProgress();
        uploadCounter.setVisibility(View.VISIBLE);


        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.baseURL))
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();
        FormWebInterface client = retrofit.create(FormWebInterface.class);

        ArrayList<FormContent> formContents = Storage.getFormContents(this);
        for (FormContent formContent : formContents) {
            FormContentUploadModel uploadModel = new FormContentUploadModel(formContent, this);
            Call<Void> call = client.postFormContent(uploadModel);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Helper.log("onResponse() " + response.code());
                    if (response.code() == 200) {
                        incrementUploadCount();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }

    private synchronized void incrementUploadCount() {
        uploadCount++;
        updateUploadProgress();
        if (uploadCount >= toUpload) {
            deleteAllFormContents();
        }
    }

    private String getUploadProgressString() {
        return String.format(getString(R.string.uploadCountText), uploadCount.toString(), toUpload.toString());
    }

    private void updateUploadProgress() {
        uploadCounter.setText(getUploadProgressString());
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.form_list_menu, menu);
        return super.onPrepareOptionsMenu(menu);
    }

    private void deleteAllFormContents() {
        if (Storage.deleteAllFormContent(this)) {
            updateView();
        }
    }

    @Override
    public void onItemClick(FormContent formContent) {
        Form form = Storage.getFormById(formContent.getFormId(), this);
        Intent formOverviewIntent = new Intent(getApplicationContext(), FormOverviewActivity.class);
        formOverviewIntent.putExtra(Helper.FORM, form.getFormattedFormName());
        formOverviewIntent.putExtra(Helper.FORM_CONTENT, formContent.getFormContentId());
        startActivityForResult(formOverviewIntent, Helper.FORM_OVERVIEW_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Helper.FORM_OVERVIEW_CODE) {
            if (resultCode == Helper.CONTENT_SAVED_CODE) {
                updateView();
            }
        }
    }

    private void updateView() {
        formListAdapter.setFormContentNames(Storage.getFormContentNames(this));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.upload_menu_item:
                postFormContentList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
