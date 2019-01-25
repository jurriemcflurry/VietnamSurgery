package activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import webinterfaces.UserWebInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import toning.juriaan.models.AccessToken;
import toning.juriaan.models.ChangePasswordObject;
import toning.juriaan.models.Helper;
import toning.juriaan.models.R;

public class ChangePasswordActivity extends BaseActivity implements Callback<Void> {

    private FrameLayout changePasswordFrameLayout;
    private TextInputEditText oldPassword;
    private TextInputEditText newPassword;
    private TextInputEditText confirmNewPassword;
    private Button changePassword;
    private ProgressBar changePasswordSpinner;
    private UserWebInterface userWebInterface;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        FrameLayout contentFrameLayout = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_change_password, contentFrameLayout);
        getSupportActionBar().setTitle(getString(R.string.changePassword));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.baseURL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        userWebInterface = retrofit.create(UserWebInterface.class);

        setupLayout();
    }

    private void setupLayout(){
        changePasswordFrameLayout = findViewById(R.id.changePassword_frame_layout);
        changePasswordSpinner = findViewById(R.id.changePasswordSpinner);
        oldPassword = findViewById(R.id.oldPasswordEditText);
        newPassword = findViewById(R.id.newPasswordEditText);
        confirmNewPassword = findViewById(R.id.confirmNewPasswordEditText);
        changePassword = findViewById(R.id.changePassword_button);
        hideSpinner();

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassWord();
            }
        });
    }

    private void changePassWord(){
        if(AccessToken.getAccess_token() == null){
            return;
        }

        String oldPasswordText = oldPassword.getText().toString();
        String newPasswordText = newPassword.getText().toString();
        String confirmNewPasswordText = confirmNewPassword.getText().toString();

        if(oldPasswordText.isEmpty() || newPasswordText.isEmpty() || confirmNewPasswordText.isEmpty()){
            Snackbar.make(changePasswordFrameLayout, getString(R.string.changePasswordEmptyFields), Snackbar.LENGTH_LONG)
                    .show();
            return;
        }

        if(!newPasswordText.equals(confirmNewPasswordText)){
            Snackbar.make(changePasswordFrameLayout, getString(R.string.noPasswordMatch), Snackbar.LENGTH_LONG)
                    .show();
            return;
        }

        showSpinner();
        ChangePasswordObject changePasswordObject = new ChangePasswordObject(oldPasswordText, newPasswordText, confirmNewPasswordText);

        userWebInterface.changePassword(AccessToken.getAccess_token(), changePasswordObject).enqueue(this);
    }

    private void showSpinner(){
        changePasswordSpinner.setVisibility(View.VISIBLE);
    }

    private void hideSpinner(){
        changePasswordSpinner.setVisibility(View.GONE);
    }

    @Override
    public void onResponse(Call<Void> call, Response<Void> response) {
        Helper.hideKeyboard(this);
        hideSpinner();

        if(response.isSuccessful() && response.body() == null){
            AccessToken.setAccess_token(null);
            Snackbar.make(findViewById(R.id.changePassword_linear_layout), getString(R.string.passwordChanged), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.loginCaps), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent toLogin = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                            toLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
    public void onFailure(Call<Void> call, Throwable t) {
        Snackbar.make(findViewById(R.id.changePassword_linear_layout), getString(R.string.passwordNotChanged), Snackbar.LENGTH_LONG)
                .show();
    }
}
