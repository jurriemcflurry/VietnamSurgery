package Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import ResponseModels.LoginResponse;
import WebInterfaces.UserWebInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import toning.juriaan.Models.AccessToken;
import toning.juriaan.Models.Helper;
import toning.juriaan.Models.R;


public class LoginActivity extends BaseActivity implements Callback<LoginResponse> {

    private DrawerLayout mDrawerLayout;
    private TextInputEditText userName;
    private TextInputEditText password;
    private Button loginButton;
    private UserWebInterface userWebInterface;
    private Helper helper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.login_activity, contentFrameLayout);
        setupLayoutControls();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.baseURL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        userWebInterface = retrofit.create(UserWebInterface.class);
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
        helper.hideKeyboard(this);

        if(response.isSuccessful() && response.body() != null){
            Snackbar.make(findViewById(R.id.login_linear_layout),getString(R.string.loggedIn), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.homeCaps), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent backToHome = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(backToHome);
                        }
                    }).show();
            AccessToken.access_token = response.body().token_type + " " + response.body().accesstoken;
            AccessToken.userName = response.body().userName;
            TextView loginText = (TextView) findViewById(R.id.Logintext);
            TextView loggedInUser = (TextView) findViewById(R.id.LoggedinUser);
            loginText.setText(getString(R.string.logout));
            loggedInUser.setText(AccessToken.userName);
        }
        else{
            Snackbar.make(findViewById(R.id.login_linear_layout), response.message(),Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onFailure(Call<LoginResponse> call, Throwable t) {
        t.printStackTrace();
    }
}

