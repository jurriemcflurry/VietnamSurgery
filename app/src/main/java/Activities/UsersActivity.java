package Activities;

import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import ResponseModels.UsersResponse;
import WebInterfaces.UserWebInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import toning.juriaan.vietnamsurgery.AccessToken;
import toning.juriaan.vietnamsurgery.R;
import toning.juriaan.vietnamsurgery.RegisterObject;

public class UsersActivity extends AppCompatActivity implements Callback<UsersResponse> {
    private DrawerLayout mDrawerLayout;
    private UserWebInterface userWebInterface;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //thema moet altijd worden gezet naar AppTheme, zodat de Launcher van het splashscreen niet bij elke actie wordt getoond
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.baseURL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

       // userWebInterface = retrofit.create(UserWebInterface.class);

        setupNavigation();
        getUsers();
        Button register = (Button) findViewById(R.id.register_user);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toRegister = new Intent(UsersActivity.this, RegisterActivity.class);
                startActivity(toRegister);
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

    private void getUsers(){
        if(AccessToken.access_token == null){
            return;
        }

        userWebInterface.getUsers(AccessToken.access_token).enqueue(this);
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

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ga naar pagina om in te loggen
                Intent loginIntent = new Intent(UsersActivity.this, LoginActivity.class);
                startActivity(loginIntent);

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
                                Intent naarForms = new Intent(UsersActivity.this, FormActivity.class);
                                startActivity(naarForms);
                                break;
                            case R.id.nav_2: //2e item
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

    @Override
    public void onResponse(Call<UsersResponse> call, Response<UsersResponse> response) {
        if(response.isSuccessful() && response.body() != null){
            //fill labels with userinformation
        }
    }

    @Override
    public void onFailure(Call<UsersResponse> call, Throwable t) {
        t.printStackTrace();
    }
}
