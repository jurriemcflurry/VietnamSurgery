package activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import responsemodels.DeleteResponse;
import webinterfaces.DetailClickListener;
import webinterfaces.UserWebInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import toning.juriaan.models.R;
import toning.juriaan.models.User;
import toning.juriaan.models.UserAdapter;
import toning.juriaan.models.AccessToken;

public class UsersActivity extends BaseActivity implements Callback<List<User>> {
    private UserWebInterface userWebInterface;
    private List<User> mContent = new ArrayList<>();
    private RecyclerView list;
    private UserAdapter mListAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar pBar;
    private TextView usersNoSuccess;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_users, contentFrameLayout);
        getSupportActionBar().setTitle(getString(R.string.usersTitle));

        pBar = findViewById(R.id.pBar_users);
        usersNoSuccess = findViewById(R.id.usersNoSuccess);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.baseURL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        userWebInterface = retrofit.create(UserWebInterface.class);

        list = findViewById(R.id.users_list);
        mLinearLayoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(mLinearLayoutManager);
        mListAdapter = new UserAdapter(mContent, this, new DetailClickListener() {
            @Override
            public void onItemClick(int position) {
                final User user = mListAdapter.getItem(position);
                if(AccessToken.getUserrole().equals(getString(R.string.adminCheck))){
                    new AlertDialog.Builder(UsersActivity.this)
                            .setTitle(getString(R.string.deleteUserTitle))
                            .setMessage(getString(R.string.deleteUserMessage) + user.username + "?")
                            .setNegativeButton(getString(R.string.cancelDelete), null)
                            .setPositiveButton(getString(R.string.confirmDelete), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    pBar.setVisibility(View.VISIBLE);
                                    deleteUserCall(user.getId());
                                }
                            }).create().show();
                }
                else{
                    Snackbar.make(findViewById(R.id.users_frame_layout), getString(R.string.noAccess), Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.loginSnackbar), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent toLogin = new Intent(getApplicationContext(), LoginActivity.class);
                                    startActivity(toLogin);
                                }
                            }).show();
                }
            }
        });
        list.setAdapter(mListAdapter);

        if(!isNetworkAvailable()){
            pBar.setVisibility(View.INVISIBLE);
            usersNoSuccess.setVisibility(View.VISIBLE);
        }
        else{
            usersNoSuccess.setVisibility(View.GONE);
            getUsers();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_overview_menu, menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addUser:
                Intent toRegister = new Intent(UsersActivity.this, RegisterActivity.class);
                startActivity(toRegister);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getUsers(){
        if(AccessToken.getAccess_token() == null){
            pBar.setVisibility(View.INVISIBLE);
            Snackbar.make(findViewById(R.id.users_linearlayout), getString(R.string.noAccess),Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.loginSnackbar), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent toLogin = new Intent(UsersActivity.this, LoginActivity.class);
                            startActivity(toLogin);
                        }
                    }).show();
            return;
        }

        userWebInterface.getUsers(AccessToken.getAccess_token()).enqueue(this);
    }

    private void deleteUserCall(String userId){
        userWebInterface.deleteUser(userId).enqueue(new Callback<DeleteResponse>() {
            @Override
            public void onResponse(Call<DeleteResponse> call, Response<DeleteResponse> response) {
                pBar.setVisibility(View.INVISIBLE);
                Snackbar.make(findViewById(R.id.users_frame_layout), getString(R.string.userDeleted), Snackbar.LENGTH_LONG)
                        .show();
                FrameLayout userLayout = findViewById(R.id.users_frame_layout);
                getUsers();
                userLayout.requestLayout();
            }

            @Override
            public void onFailure(Call<DeleteResponse> call, Throwable t) {
                t.printStackTrace();
                pBar.setVisibility(View.INVISIBLE);
                Snackbar.make(findViewById(R.id.users_frame_layout), getString(R.string.userDeleted), Snackbar.LENGTH_LONG)
                        .show();
                FrameLayout userLayout = findViewById(R.id.users_frame_layout);
                getUsers();
                userLayout.requestLayout();
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onResponse(Call<List<User>> call, Response<List<User>> response) {
        if(response.isSuccessful() && response.body() != null){
            mContent.clear();
            mContent.addAll(response.body());
            mListAdapter.notifyDataSetChanged();
            pBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onFailure(Call<List<User>> call, Throwable t) {
        pBar.setVisibility(View.INVISIBLE);
        usersNoSuccess.setText(getString(R.string.usersTryAgain));
        usersNoSuccess.setVisibility(View.VISIBLE);
    }
}
