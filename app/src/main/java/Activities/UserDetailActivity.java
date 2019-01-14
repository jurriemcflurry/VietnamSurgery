package Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import ResponseModels.DeleteResponse;
import WebInterfaces.UserWebInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import toning.juriaan.Models.AccessToken;
import toning.juriaan.Models.R;
import toning.juriaan.Models.User;

public class UserDetailActivity extends BaseActivity implements Callback<DeleteResponse> {

    public TextView username;
    public TextView email;
    public TextView role;
    private User user;
    private UserWebInterface userWebInterface;
    private Button deleteUser;
    private ProgressBar pBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_user_detail, contentFrameLayout);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.baseURL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        userWebInterface = retrofit.create(UserWebInterface.class);

        pBar = findViewById(R.id.pBar);
        pBar.setVisibility(View.INVISIBLE);

        Intent fromUser = getIntent();
        user = fromUser.getParcelableExtra(UsersActivity.detailpage);

        getSupportActionBar().setTitle(user.username);

        username = findViewById(R.id.detailpage_username);
        username.setText(user.username);

        email = findViewById(R.id.detailpage_email);
        email.setText(user.email);

        String roleUserId = user.roles.get(0).getUserId();
        String roleId = user.roles.get(0).getRoleId();

        final String userId = user.getId();
        System.out.println(userId);

        role = findViewById(R.id.detailpage_role);
        if(roleId.equals("1")){
            role.setText(getString(R.string.admin));
        }
        else{
            role.setText(roleId);
        }

        deleteUser = findViewById(R.id.detailpage_delete_button);
        deleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AccessToken.userrole.equals("Admin")){
                    new AlertDialog.Builder(UserDetailActivity.this)
                            .setTitle("Delete user?")
                            .setMessage("Are you sure you want to delete user " + user.username + "?")
                            .setNegativeButton("Cancel", null)
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    pBar.setVisibility(View.VISIBLE);
                                    deleteUserCall(userId);
                                }
                            }).create().show();
                }
                else{
                    Snackbar.make(findViewById(R.id.detailpage), getString(R.string.noAccess), Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.login), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent toLogin = new Intent(UserDetailActivity.this, LoginActivity.class);
                                    startActivity(toLogin);
                                }
                            }).show();
                    return;
                }
            }
        });
    }

    private void deleteUserCall(String userId){
        userWebInterface.deleteUser(userId).enqueue(this);
    }

    @Override
    public void onResponse(Call<DeleteResponse> call, Response<DeleteResponse> response) {
            pBar.setVisibility(View.INVISIBLE);
            Snackbar.make(findViewById(R.id.detailpage), getString(R.string.userDeleted), Snackbar.LENGTH_LONG)
                    .show();
            Intent returnIntent = new Intent(UserDetailActivity.this, UsersActivity.class);
            startActivity(returnIntent);
            finish();

    }

    @Override
    public void onFailure(Call<DeleteResponse> call, Throwable t) {
        t.printStackTrace();
        pBar.setVisibility(View.INVISIBLE);
        Snackbar.make(findViewById(R.id.detailpage), getString(R.string.userDeleted), Snackbar.LENGTH_LONG)
                .show();
        Intent returnIntent = new Intent(UserDetailActivity.this, UsersActivity.class);
        startActivity(returnIntent);
        finish();
    }
}
