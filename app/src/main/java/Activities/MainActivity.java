package Activities;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.MessageFormat;
import java.time.format.FormatStyle;
import java.util.ArrayList;

import ResponseModels.FormulierenResponse;
import WebInterfaces.FormWebInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import toning.juriaan.Models.AccessToken;
import toning.juriaan.Models.Form;
//import toning.juriaan.Models.FormTemplateObject;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //thema moet altijd worden gezet naar AppTheme, zodat de Launcher van het splashscreen niet bij elke actie wordt getoond
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_main, contentFrameLayout);
        getSupportActionBar().setTitle(getString(R.string.home));

        forms = new ArrayList<>();
        recyclerView = findViewById(R.id.form_recycler);
        formAdapter = new FormAdapter(forms, this);
        recyclerView.setAdapter(formAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        Button OpenCamera = (Button) findViewById(R.id.ToCamera);
        OpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toCamera = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(toCamera);
            }
        });

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
                Helper.log("postForm.onResponse() " + response.code());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Helper.log("postForm.onFailure()");
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
        toFormActivityIntent.putExtra(FormActivity.FORM, form.getFormattedFormName());
        startActivity(toFormActivityIntent);
    }
}
