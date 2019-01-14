package Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
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

public class UsersActivity extends BaseActivity implements Callback<List<User>> {
    private UserWebInterface userWebInterface;
    private List<User> mContent;
    private RecyclerView list;
    private UserAdapter mListAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    public static final String detailpage = String.valueOf(R.string.toUserDetail);
    private ProgressBar pBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_users, contentFrameLayout);
        getSupportActionBar().setTitle(getString(R.string.users));

        pBar = (ProgressBar) findViewById(R.id.pBar_users);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.baseURL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        userWebInterface = retrofit.create(UserWebInterface.class);

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
        TextView register = (TextView) findViewById(R.id.register_user);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toRegister = new Intent(UsersActivity.this, RegisterActivity.class);
                startActivity(toRegister);
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        mContent.clear();
        getUsers();
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

    @Override
    public void onResponse(Call<List<User>> call, Response<List<User>> response) {
        if(response.isSuccessful() && response.body() != null){
            //fill labels with userinformation
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
