package Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ResponseModels.DeleteResponse;
import WebInterfaces.DetailClickListener;
import WebInterfaces.UserWebInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import toning.juriaan.Models.Helper;
import toning.juriaan.Models.R;
import toning.juriaan.Models.User;
import toning.juriaan.Models.UserAdapter;
import toning.juriaan.Models.AccessToken;

public class UsersActivity extends BaseActivity implements Callback<List<User>> {
    private UserWebInterface userWebInterface;
    private List<User> mContent = new ArrayList<>();
    private RecyclerView list;
    private UserAdapter mListAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private FloatingActionButton addUser;
    private ProgressBar pBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_users, contentFrameLayout);
        getSupportActionBar().setTitle(getString(R.string.users));

        pBar = findViewById(R.id.pBar_users);

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
                if(AccessToken.userrole.equals("Admin")){
                    new AlertDialog.Builder(UsersActivity.this)
                            .setTitle("Delete user?")
                            .setMessage("Are you sure you want to delete user " + user.username + "?")
                            .setNegativeButton("Cancel", null)
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    pBar.setVisibility(View.VISIBLE);
                                    deleteUserCall(user.getId());
                                }
                            }).create().show();
                }
                else{
                    Snackbar.make(findViewById(R.id.users_frame_layout), getString(R.string.noAccess), Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.login), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent toLogin = new Intent(getApplicationContext(), LoginActivity.class);
                                    startActivity(toLogin);
                                }
                            }).show();
                    return;
                }
            }
        });
        list.setAdapter(mListAdapter);


        getUsers();

        addUser = findViewById(R.id.registerUser);
        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toRegister = new Intent(UsersActivity.this, RegisterActivity.class);
                startActivity(toRegister);
            }
        });
    }

    private void getUsers(){
        if(AccessToken.access_token == null){
            pBar.setVisibility(View.INVISIBLE);
            Snackbar.make(findViewById(R.id.users_linearlayout), getString(R.string.noAccess),Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.login), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent toLogin = new Intent(UsersActivity.this, LoginActivity.class);
                            startActivity(toLogin);
                        }
                    }).show();
            return;
        }

        userWebInterface.getUsers(AccessToken.access_token).enqueue(this);
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
        t.printStackTrace();
    }
}
