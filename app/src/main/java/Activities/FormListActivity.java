package Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

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
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.form_list_activity, contentFrameLayout);
        getSupportActionBar().setTitle(getString(R.string.formContent));


        formListAdapter = new FormListAdapter(Storage.getFormContentNames(this), this);
        formListRecycler = findViewById(R.id.form_list_recycler);
        formListRecycler.setAdapter(formListAdapter);
        formListRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    protected void postFormContentList() {
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
                    try {
                        Helper.log("onResponse() " + response.code());
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.form_list_menu, menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onItemClick(FormContent formContent) {
        Form form = Storage.getFormById(formContent.getFormId(), this);
        Intent formOverviewIntent = new Intent(getApplicationContext(), FormOverviewActivity.class);
        formOverviewIntent.putExtra(Helper.FORM, form.getFormattedFormName());
        formOverviewIntent.putExtra(Helper.FORM_CONTENT, formContent.getFormContentName());
        startActivityForResult(formOverviewIntent, Helper.FORM_OVERVIEW_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Helper.FORM_OVERVIEW_CODE) {
            if (resultCode == Helper.CONTENT_SAVED_CODE) {
                formListAdapter.setFormContentNames(Storage.getFormContentNames(this));
            }
        }
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