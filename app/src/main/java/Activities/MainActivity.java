package Activities;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import WebInterfaces.FormWebInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import toning.juriaan.Models.AccessToken;
import toning.juriaan.Models.Form;
import toning.juriaan.Models.FormTemplateObject;
import toning.juriaan.Models.Helper;
import toning.juriaan.Models.R;
import toning.juriaan.Models.Storage;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private Form form;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //thema moet altijd worden gezet naar AppTheme, zodat de Launcher van het splashscreen niet bij elke actie wordt getoond
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        form = Form.getDummyForm();
        form.setFormName("MainActivity form");

        setupNavigation();

        Button OpenCamera = (Button) findViewById(R.id.ToCamera);
        OpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toCamera = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(toCamera);
            }
        });

        Button toFormActivityButton = findViewById(R.id.toFormActivity);
        toFormActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Storage.saveForm(form, MainActivity.this);

                Intent toFormActivityIntent = new Intent(MainActivity.this, FormActivity.class);
                toFormActivityIntent.putExtra(FormActivity.FORM, form.getFormattedFormName());
                startActivity(toFormActivityIntent);
            }
        });

        final Button postForm = findViewById(R.id.postForm);
        postForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postForm();
            }
        });
    }

    private void postForm() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.baseURL))
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();
        FormWebInterface client = retrofit.create(FormWebInterface.class);
        Call<Void> call = client.postFormTemplate(new FormTemplateObject(form.getFormTemplate()));

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Helper.log("onResponse() " + response.code());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Helper.log("onFailure()");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupNavigation(){
        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        LinearLayout header = (LinearLayout) headerView.findViewById(R.id.headerlayout);
        final TextView login = (TextView) header.findViewById(R.id.Logintext);
        final TextView loggedInUser = (TextView) header.findViewById(R.id.LoggedinUser);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        if(AccessToken.access_token != null){
            login.setText(getString(R.string.logout));
            loggedInUser.setText(AccessToken.userName);
            loggedInUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent toChangePassword = new Intent(MainActivity.this, ChangePasswordActivity.class);
                    startActivity(toChangePassword);
                }
            });
        }
        else{
            login.setText(getString(R.string.login));
            loggedInUser.setText(getString(R.string.not_logged_in));
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(login.getText().equals(getString(R.string.logout))){
                    AccessToken.access_token = null;
                    AccessToken.userName = null;
                    login.setText(getString(R.string.login));
                    loggedInUser.setText(getString(R.string.not_logged_in));
                    mDrawerLayout.closeDrawers();
                }
                else{
                    // ga naar pagina om in te loggen
                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                }
            }
        });

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        switch(menuItem.getItemId()){
                            case R.id.nav_1: //Bovenste Item
                                Intent naarForms = new Intent(MainActivity.this, FormActivity.class);
                                startActivity(naarForms);
                                break;
                            case R.id.nav_2: //2e item
                                Intent naarUsers = new Intent(MainActivity.this, UsersActivity.class);
                                startActivity(naarUsers);
                                break;
                            case R.id.nav_3: //3e item
                                break;
                            case R.id.nav_4: //4e item
                                break;
                            default: break;
                        }

                        return true;
                    }
                });
    }
}
