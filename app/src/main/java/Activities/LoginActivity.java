package Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
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

    private TextInputEditText email;
    private TextInputEditText password;
    private Button loginButton;
    private UserWebInterface userWebInterface;
    private Helper helper;
    private ProgressBar pBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_login, contentFrameLayout);
        getSupportActionBar().setTitle(getString(R.string.login));

        setupLayoutControls();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.baseURL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        userWebInterface = retrofit.create(UserWebInterface.class);
    }

    private void setupLayoutControls(){
        email = (TextInputEditText) findViewById(R.id.login_email);
        password = (TextInputEditText) findViewById(R.id.password);
        loginButton = (Button) findViewById(R.id.login_button);
        pBar = (ProgressBar) findViewById(R.id.pBar);
        pBar.setVisibility(View.INVISIBLE);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText().toString().isEmpty() || password.getText().toString().isEmpty()){
                    Snackbar.make(findViewById(R.id.login_linear_layout), getString(R.string.emptyFields),Snackbar.LENGTH_LONG)
                            .show();
                    return;
                }

                //call om in te loggen
                pBar.setVisibility(View.VISIBLE);
                login();
            }
        });
    }

    private void login(){

        String username = email.getText().toString();
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
        pBar.setVisibility(View.INVISIBLE);

        if(response.isSuccessful() && response.body() != null){
            Snackbar.make(findViewById(R.id.login_linear_layout),getString(R.string.loggedIn), Snackbar.LENGTH_INDEFINITE)
                    .show();
            AccessToken.access_token = response.body().token_type + " " + response.body().accesstoken;
            AccessToken.userName = response.body().userName;
            AccessToken.userrole = response.body().role;
            TextView loginText = (TextView) findViewById(R.id.Logintext);
            TextView loggedInUser = (TextView) findViewById(R.id.LoggedinUser);
            loginText.setText(getString(R.string.logout));
            loggedInUser.setText(AccessToken.userName);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    // Actions to do after 1,5 seconds
                    Intent backToHome = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(backToHome);
                }
            }, 1500);
        }
        else{
            if(response.message().equals(getString(R.string.badRequest))){
                Snackbar.make(findViewById(R.id.login_linear_layout), getString(R.string.failedLogin),Snackbar.LENGTH_LONG)
                        .show();
            }
            else{
                Snackbar.make(findViewById(R.id.login_linear_layout), response.message(),Snackbar.LENGTH_LONG)
                        .show();
            }
        }
    }

    @Override
    public void onFailure(Call<LoginResponse> call, Throwable t) {
        t.printStackTrace();
    }
}

