package activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;
import responsemodels.FormulierenResponse;
import toning.juriaan.models.AccessToken;
import webinterfaces.FormWebInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import toning.juriaan.models.Form;
import toning.juriaan.models.FormAdapter;
import toning.juriaan.models.Helper;
import toning.juriaan.models.R;
import toning.juriaan.models.Storage;
import webinterfaces.UserWebInterface;


public class MainActivity extends BaseActivity implements FormAdapter.FormListener {

    private RecyclerView recyclerView;
    private FormAdapter formAdapter;
    private ArrayList<Form> forms;
    private Context context = this;
    private FormWebInterface formWebInterface;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_main, contentFrameLayout);
        getSupportActionBar().setTitle(getString(R.string.homeTitle));

        forms = Storage.getForms(this);
        recyclerView = findViewById(R.id.form_recycler);
        formAdapter = new FormAdapter(forms, this);
        recyclerView.setAdapter(formAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getForms();
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
                    Storage.deleteAllForms(MainActivity.this);
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
        Helper.log("main start with " + true);
        startActivity(toFormActivityIntent);
    }

    public void onItemLongClick(final Form form){
        if(AccessToken.getUserrole() == null || !AccessToken.getUserrole().equals(getString(R.string.adminCheck))){
            Snackbar.make(recyclerView, getString(R.string.noAccessDeleteForm), Snackbar.LENGTH_LONG)
                    .show();
            return;
        }
        else{
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(getString(R.string.deleteFormTitle))
                    .setMessage(getString(R.string.deleteFormMessage))
                    .setNegativeButton(getString(R.string.cancelDeleteForm), null)
                    .setPositiveButton(getString(R.string.confirmDeleteForm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int formId = form.getId();
                            Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl(getString(R.string.baseURL))
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build();

                            formWebInterface = retrofit.create(FormWebInterface.class);

                            formWebInterface.deleteForm(formId).enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    getForms();
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    Helper.log("on Failure");
                                }
                            });
                        }
                    }).create().show();
        }
    }
    
    @Override
    protected void onResume() {
        Storage.cleanStorage(this);
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
        Toast.makeText(this, getString(R.string.exit), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
