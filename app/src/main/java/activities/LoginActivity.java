package activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import responsemodels.LoginResponse;
import webinterfaces.UserWebInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import toning.juriaan.models.AccessToken;
import toning.juriaan.models.Helper;
import toning.juriaan.models.R;


public class LoginActivity extends BaseActivity implements Callback<LoginResponse> {

    private TextInputEditText email;
    private TextInputEditText password;
    private Button loginButton;
    private UserWebInterface userWebInterface;
    private ProgressBar pBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_login, contentFrameLayout);
        getSupportActionBar().setTitle(getString(R.string.loginTitle));

        setupLayoutControls();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.baseURL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        userWebInterface = retrofit.create(UserWebInterface.class);

    }

    private void setupLayoutControls(){
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        pBar = findViewById(R.id.pBar);
        pBar.setVisibility(View.INVISIBLE);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.hideKeyboard(LoginActivity.this);

                //check of er internetconnectie is
                if(!isNetworkAvailable()){
                    Snackbar.make(findViewById(R.id.login_linear_layout), getString(R.string.loginNoInternet),Snackbar.LENGTH_LONG)
                            .show();
                    return;
                }

                //check of velden zijn ingevuld
                if(email.getText().toString().isEmpty() || password.getText().toString().isEmpty()){
                    Snackbar.make(findViewById(R.id.login_linear_layout), getString(R.string.loginEmptyFields),Snackbar.LENGTH_LONG)
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
        String granttype = getString(R.string.granttypePassword);

        userWebInterface.login(username, passWord, granttype).enqueue(this);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
        pBar.setVisibility(View.INVISIBLE);

        if(response.isSuccessful() && response.body() != null){
            Snackbar.make(findViewById(R.id.login_linear_layout),getString(R.string.loggedIn), Snackbar.LENGTH_INDEFINITE)
                    .show();
            AccessToken.setAccess_token(response.body().token_type + " " + response.body().accesstoken);
            AccessToken.setUserName(response.body().userName);
            AccessToken.setUserrole(response.body().role);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    // Actions to do after 1,5 seconds
                    Intent backToHome = new Intent(LoginActivity.this, MainActivity.class);
                    backToHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
        Helper.hideKeyboard(this);
        pBar.setVisibility(View.INVISIBLE);
        Snackbar.make(findViewById(R.id.login_linear_layout),getString(R.string.notLoggedIn), Snackbar.LENGTH_INDEFINITE)
                .show();
    }
}

