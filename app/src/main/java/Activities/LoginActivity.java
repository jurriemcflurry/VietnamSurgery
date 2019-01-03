package Activities;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import ResponseModels.LoginResponse;
import WebInterfaces.UserWebInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import toning.juriaan.Models.AccessToken;
import toning.juriaan.Models.R;


public class LoginActivity extends AppCompatActivity implements Callback<LoginResponse> {

    private DrawerLayout mDrawerLayout;
    private TextInputEditText userName;
    private TextInputEditText password;
    private Button loginButton;
    private UserWebInterface userWebInterface;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        setupLayoutControls();
        setupNavigation();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.baseURL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        userWebInterface = retrofit.create(UserWebInterface.class);
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
        mDrawerLayout = (DrawerLayout) findViewById(R.id.login_drawer_layout);
        NavigationView navigationView = findViewById(R.id.login_nav_view);
        View headerView = navigationView.getHeaderView(0);
        LinearLayout header = (LinearLayout) headerView.findViewById(R.id.headerlayout);
        final TextView login = (TextView) header.findViewById(R.id.Logintext);
        final TextView loggedInUser = (TextView) header.findViewById(R.id.LoggedinUser);
        Toolbar toolbar = findViewById(R.id.login_toolbar);
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
                    Intent toChangePassword = new Intent(LoginActivity.this, ChangePasswordActivity.class);
                    startActivity(toChangePassword);
                }
            });
        }

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
                            case 2131230828: //Bovenste Item
                                break;
                            case 2131230829: //2e item
                                break;
                            case 2131230830: //3e item
                                break;
                            case 2131230831: //4e item
                                break;
                            default: break;
                        }

                        return true;
                    }
                });
    }

    private void setupLayoutControls(){
        userName = (TextInputEditText) findViewById(R.id.userName);
        password = (TextInputEditText) findViewById(R.id.password);
        loginButton = (Button) findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userName.getText() == null || password.getText() == null){
                    return;
                }

                //call om in te loggen
                login();
            }
        });
    }

    private void login(){

        String username = userName.getText().toString();
        String passWord = password.getText().toString();
        String granttype = getString(R.string.password2);

        userWebInterface.login(username, passWord, granttype).enqueue(this);
    }

    @Override
    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
        //sla token op
        //set headertexts goed
        //terug naar home

        if(response.isSuccessful() && response.body() != null){
            Snackbar.make(findViewById(R.id.login_linear_layout), "Succesvol ingelogd", Snackbar.LENGTH_LONG)
                    .setAction("HOME", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent backToHome = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(backToHome);
                        }
                    }).show();
            AccessToken.access_token = "Bearer " + response.body().accesstoken;
            AccessToken.userName = response.body().userName;
            TextView loginText = (TextView) findViewById(R.id.Logintext);
            TextView loggedInUser = (TextView) findViewById(R.id.LoggedinUser);
            loginText.setText(getString(R.string.logout));
            loggedInUser.setText(AccessToken.userName);
        }
    }

    @Override
    public void onFailure(Call<LoginResponse> call, Throwable t) {
        t.printStackTrace();
    }
}

