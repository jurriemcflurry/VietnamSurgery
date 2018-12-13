package Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import WebInterfaces.DetailClickListener;
import WebInterfaces.UserWebInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import toning.juriaan.Models.R;
import toning.juriaan.Models.User;
import toning.juriaan.Models.UserAdapter;
import toning.juriaan.Models.AccessToken;

public class UsersActivity extends AppCompatActivity implements Callback<List<User>> {
    private DrawerLayout mDrawerLayout;
    private UserWebInterface userWebInterface;
    private List<User> mContent;
    private RecyclerView list;
    private UserAdapter mListAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    public static final String detailpage = String.valueOf(R.string.toUserDetail);

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

        userWebInterface = retrofit.create(UserWebInterface.class);

        setupNavigation();

        list = (RecyclerView) findViewById(R.id.users_list);
        mLinearLayoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(mLinearLayoutManager);
        mContent = new ArrayList<>();
        mListAdapter = new UserAdapter(mContent, this, new DetailClickListener() {
            @Override
            public void onItemClick(int position) {
                User user = mListAdapter.getItem(position);
                Intent toUserDetail = new Intent(UsersActivity.this, UserDetailActivity.class);
                toUserDetail.putExtra(detailpage, user);
                startActivity(toUserDetail);
            }
        });
        list.setAdapter(mListAdapter);


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
                    Intent loginIntent = new Intent(UsersActivity.this, LoginActivity.class);
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
    public void onResponse(Call<List<User>> call, Response<List<User>> response) {
        if(response.isSuccessful() && response.body() != null){
            //fill labels with userinformation
            mContent.addAll(response.body());
            mListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onFailure(Call<List<User>> call, Throwable t) {
        t.printStackTrace();
    }
}
