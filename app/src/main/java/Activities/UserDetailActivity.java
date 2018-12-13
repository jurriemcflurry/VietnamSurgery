package Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import toning.juriaan.Models.R;
import toning.juriaan.Models.User;

public class UserDetailActivity extends AppCompatActivity {

    public TextView username;
    public TextView email;
    public TextView role;
    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //thema moet altijd worden gezet naar AppTheme, zodat de Launcher van het splashscreen niet bij elke actie wordt getoond
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        Intent fromUser = getIntent();
        user = fromUser.getParcelableExtra(UsersActivity.detailpage);

        username = (TextView) findViewById(R.id.detailpage_username);
        username.setText(user.username);

        email = (TextView) findViewById(R.id.detailpage_email);
        email.setText(user.email);

        String userId = user.roles.get(0).getUserId();
        String roleId = user.roles.get(0).getRoleId();

        role = (TextView) findViewById(R.id.detailpage_role);
        if(roleId.equals("1")){
            role.setText(getString(R.string.admin));
        }
        else{
            role.setText(roleId);
        }
    }
}
