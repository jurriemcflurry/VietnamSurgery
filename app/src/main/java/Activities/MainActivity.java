package Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;
import ResponseModels.FormulierenResponse;
import WebInterfaces.FormWebInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import toning.juriaan.Models.Form;
import toning.juriaan.Models.FormAdapter;
import toning.juriaan.Models.FormTemplateObject;
import toning.juriaan.Models.Helper;
import toning.juriaan.Models.R;
import toning.juriaan.Models.Storage;


public class MainActivity extends BaseActivity implements FormAdapter.FormListener {

    private RecyclerView recyclerView;
    private FormAdapter formAdapter;
    private ArrayList<Form> forms;
    private Context context = this;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_main, contentFrameLayout);
        getSupportActionBar().setTitle(getString(R.string.home));

        forms = new ArrayList<>();
        recyclerView = findViewById(R.id.form_recycler);
        formAdapter = new FormAdapter(forms, this);
        recyclerView.setAdapter(formAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getForms();
    }

    private void postForm(Form form) {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.baseURL))
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();
        FormWebInterface client = retrofit.create(FormWebInterface.class);
        Call<Void> call = client.postFormTemplate(new FormTemplateObject(form.getFormTemplate()));

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
            }
        });
    }

    private void getForms() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.baseURL))
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();
        FormWebInterface client = retrofit.create(FormWebInterface.class);
        Call<FormulierenResponse[]> call = client.getFormTemplates();

        call.enqueue(new Callback<FormulierenResponse[]>() {
            @Override
            public void onResponse(Call<FormulierenResponse[]> call, Response<FormulierenResponse[]> response) {
                if (response.isSuccessful() && response.body() != null && response.body().length > 0) {
                    forms.clear();
                    for (FormulierenResponse formResponse : response.body()) {
                        Form form = new Form(formResponse);
                        Storage.saveForm(form, MainActivity.this);
                    }
                    forms = Storage.getForms(context);
                    updateView();
                }
            }

            @Override
            public void onFailure(Call<FormulierenResponse[]> call, Throwable t) {
                Helper.log("onFailure() " + t.getMessage());
                t.printStackTrace();
            }
        });
    }

    public void updateView() {
        formAdapter.setForms(forms);
        formAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(Form form) {
        Intent toFormActivityIntent = new Intent(MainActivity.this, FormActivity.class);
        toFormActivityIntent.putExtra(Helper.FORM, form.getFormattedFormName());
        startActivity(toFormActivityIntent);
    }

    @Override
    protected void onResume() {
        formAdapter.updateAmounts();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
