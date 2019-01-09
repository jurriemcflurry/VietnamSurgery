package Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import ResponseModels.ChangePasswordResponse;
import WebInterfaces.UserWebInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import toning.juriaan.Models.AccessToken;
import toning.juriaan.Models.ChangePasswordObject;
import toning.juriaan.Models.Helper;
import toning.juriaan.Models.R;

public class ChangePasswordActivity extends BaseActivity implements Callback<ChangePasswordResponse> {

    private DrawerLayout mDrawerLayout;
    private TextInputEditText oldPassword;
    private TextInputEditText newPassword;
    private TextInputEditText confirmNewPassword;
    private Button changePassword;
    private UserWebInterface userWebInterface;
    private Helper helper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);


        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.change_password, contentFrameLayout);
        getSupportActionBar().setTitle(getString(R.string.changePassword));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.baseURL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        userWebInterface = retrofit.create(UserWebInterface.class);

        setupLayout();
    }

    private void setupLayout(){
        oldPassword = findViewById(R.id.oldPasswordEditText);
        newPassword = findViewById(R.id.newPasswordEditText);
        confirmNewPassword = findViewById(R.id.confirmNewPasswordEditText);
        changePassword = findViewById(R.id.changePassword_button);

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassWord();
            }
        });
    }

    private void changePassWord(){
        if(AccessToken.access_token == null){
            return;
        }

        String oldPasswordText = oldPassword.getText().toString();
        String newPasswordText = newPassword.getText().toString();
        String confirmNewPasswordText = confirmNewPassword.getText().toString();

        ChangePasswordObject changePasswordObject = new ChangePasswordObject(oldPasswordText, newPasswordText, confirmNewPasswordText);

        userWebInterface.changePassword(AccessToken.access_token, changePasswordObject).enqueue(this);
    }

    @Override
    public void onResponse(Call<ChangePasswordResponse> call, Response<ChangePasswordResponse> response) {
        helper.hideKeyboard(this);

        if(response.isSuccessful() && response.body() == null){
            AccessToken.access_token = null;
            Snackbar.make(findViewById(R.id.changePassword_linear_layout), getString(R.string.passwordChanged), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.loginCaps), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent toLogin = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                            startActivity(toLogin);
                        }
                    });
        }
        else{
            Snackbar.make(findViewById(R.id.changePassword_linear_layout), response.message(),Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onFailure(Call<ChangePasswordResponse> call, Throwable t) {
        t.printStackTrace();
    }
}
