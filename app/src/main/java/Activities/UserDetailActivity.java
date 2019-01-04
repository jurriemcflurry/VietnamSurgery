package Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //thema moet altijd worden gezet naar AppTheme, zodat de Launcher van het splashscreen niet bij elke actie wordt getoond
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_user_detail, contentFrameLayout);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.baseURL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        userWebInterface = retrofit.create(UserWebInterface.class);

        Intent fromUser = getIntent();
        user = fromUser.getParcelableExtra(UsersActivity.detailpage);

        getSupportActionBar().setTitle(user.username);

        username = (TextView) findViewById(R.id.detailpage_username);
        username.setText(user.username);

        email = (TextView) findViewById(R.id.detailpage_email);
        email.setText(user.email);

        String roleUserId = user.roles.get(0).getUserId();
        String roleId = user.roles.get(0).getRoleId();

        final String userId = user.getId();

        role = (TextView) findViewById(R.id.detailpage_role);
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
                    deleteUserCall(userId);
                }
                else{
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
        if(response.isSuccessful()){
            Intent returnIntent = new Intent(UserDetailActivity.this, UsersActivity.class);
            startActivity(returnIntent);
        }
    }

    @Override
    public void onFailure(Call<DeleteResponse> call, Throwable t) {
        t.printStackTrace();
    }
}
