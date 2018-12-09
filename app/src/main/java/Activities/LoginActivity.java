package Activities;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
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

import ResponseModels.LoginResponse;
import WebInterfaces.UserWebInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import toning.juriaan.vietnamsurgery.LoginObject;
import toning.juriaan.vietnamsurgery.R;

public class LoginActivity extends AppCompatActivity implements Callback<LoginResponse> {

    private DrawerLayout mDrawerLayout;
    private TextView userNameTextView;
    private EditText userNameEditText;
    private TextView passwordTextView;
    private EditText passwordEditText;
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
                .baseUrl("http://localhost:52053/")
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
        Toolbar toolbar = findViewById(R.id.login_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

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
        userNameEditText = (EditText) findViewById(R.id.userNameEditText);
        userNameTextView = (TextView) findViewById(R.id.userNameTextView);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        passwordTextView = (TextView) findViewById(R.id.passwordTextView);
        loginButton = (Button) findViewById(R.id.login_button);

        userNameEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userNameTextView.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, dpToPx(20), 0,0 );
                userNameEditText.setLayoutParams(lp);
            }
        });

        passwordEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordTextView.setVisibility(View.VISIBLE);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call om in te loggen
                login();
            }
        });
    }

    private void login(){
        if(userNameEditText == null || passwordEditText == null){
            return;
        }

        String username = userNameEditText.toString();
        String password = passwordEditText.toString();
        LoginObject loginObject = new LoginObject(username, password);

        userWebInterface.login(loginObject).enqueue(this);
    }

    public static int dpToPx(int dp){
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    @Override
    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
        //set headertexts goed
        //sla token op

        TextView loginText = (TextView) findViewById(R.id.Logintext);
        TextView loggedInUser = (TextView) findViewById(R.id.LoggedinUser);
        loginText.setText("Ingelogd");
        Intent backToHome = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(backToHome);
    }

    @Override
    public void onFailure(Call<LoginResponse> call, Throwable t) {
        t.printStackTrace();
    }
}

