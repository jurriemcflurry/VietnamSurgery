package Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import ResponseModels.ChangePasswordResponse;
import WebInterfaces.UserWebInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import toning.juriaan.Models.AccessToken;
import toning.juriaan.Models.ChangePasswordObject;
import toning.juriaan.Models.R;

public class ChangePasswordActivity extends AppCompatActivity implements Callback<ChangePasswordResponse> {

    private DrawerLayout mDrawerLayout;
    private TextInputEditText oldPassword;
    private TextInputEditText newPassword;
    private TextInputEditText confirmNewPassword;
    private Button changePassword;
    private UserWebInterface userWebInterface;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.baseURL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        userWebInterface = retrofit.create(UserWebInterface.class);

        setupNavigation();
        setupLayout();
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
        mDrawerLayout = findViewById(R.id.changePassword_drawer_layout);
        NavigationView navigationView = findViewById(R.id.changePassword_nav_view);
        View headerView = navigationView.getHeaderView(0);
        LinearLayout header = (LinearLayout) headerView.findViewById(R.id.headerlayout);
        final TextView login = (TextView) header.findViewById(R.id.Logintext);
        final TextView loggedInUser = (TextView) header.findViewById(R.id.LoggedinUser);
        Toolbar toolbar = findViewById(R.id.changePassword_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        if(AccessToken.access_token != null){
            login.setText(getString(R.string.logout));
            loggedInUser.setText(AccessToken.userName);
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
                    Intent loginIntent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
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
                                Intent naarForms = new Intent(ChangePasswordActivity.this, FormActivity.class);
                                startActivity(naarForms);
                                break;
                            case R.id.nav_2: //2e item
                                Intent naarUsers = new Intent(ChangePasswordActivity.this, UsersActivity.class);
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
        if(response.isSuccessful() && response.body() == null){
            AccessToken.access_token = null;
            Intent toLogin = new Intent(ChangePasswordActivity.this, LoginActivity.class);
            startActivity(toLogin);
        }
    }

    @Override
    public void onFailure(Call<ChangePasswordResponse> call, Throwable t) {
        t.printStackTrace();
    }
}
