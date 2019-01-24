package activities;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import toning.juriaan.models.AccessToken;
import toning.juriaan.models.Helper;
import toning.juriaan.models.R;

import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;
import com.microsoft.appcenter.AppCenter; import com.microsoft.appcenter.analytics.Analytics; import com.microsoft.appcenter.crashes.Crashes;

import java.util.List;

public class BaseActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        AppCenter.start(getApplication(), "5af67a22-a470-452f-b89c-91c5425eca0f",
                Analytics.class, Crashes.class);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();

        View headerView = navigationView.getHeaderView(0);
        LinearLayout header = headerView.findViewById(R.id.headerlayout);
        final TextView loggedInUser = header.findViewById(R.id.loggedinUser);
        ImageView editProfile = header.findViewById(R.id.editProfile);
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        if(AccessToken.userName != null){
            loggedInUser.setText(AccessToken.userName);
            editProfile.setVisibility(View.VISIBLE);
            editProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent editProfile = new Intent(getApplicationContext(), ChangePasswordActivity.class);
                    startActivity(editProfile);
                }
            });
        }
        else{
            loggedInUser.setText(getString(R.string.not_logged_in));
            editProfile.setVisibility(View.GONE);
        }

        if(AccessToken.access_token == null){
            addLoginButton(menu);
        }
        else{
            if(AccessToken.userrole.equals(getString(R.string.BaseAdminCheck))){
                addMenuItemsAdmin(menu);
            }

            addLogoutButton(menu);
        }

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        drawerLayout.closeDrawers();

                        switch(menuItem.getItemId()){
                            case R.id.nav_1: //Top item
                                Intent homeIntent = new Intent(getApplicationContext(), MainActivity.class);
                                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(homeIntent);
                                break;
                            case R.id.nav_2:
                                if(getActiveActivity().equals("activities.FormListActivity")){
                                    Intent toFormContentIntent = new Intent(
                                            getApplicationContext(), FormListActivity.class);
                                    startActivity(toFormContentIntent);
                                    finish();
                                }else{
                                    Intent toFormContentIntent = new Intent(
                                            getApplicationContext(), FormListActivity.class);
                                    startActivity(toFormContentIntent);
                                }
                            default: break;
                        }

                        return true;
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addMenuItemsAdmin(Menu menu){
        menu.add(getString(R.string.createNewForm)).setIcon(R.drawable.enter_form_menu_icon_black).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent createForm = new Intent(getApplicationContext(), CreateFormActivity.class);
                startActivity(createForm);
                return false;
            }
        });

        menu.add(getString(R.string.users)).setIcon(R.drawable.user_list_menu_icon_black).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(getActiveActivity().equals("activities.UsersActivity")){
                    Intent naarUsers = new Intent(getApplicationContext(), UsersActivity.class);
                    startActivity(naarUsers);
                    finish();
                }
                else{
                    Intent naarUsers = new Intent(getApplicationContext(), UsersActivity.class);
                    startActivity(naarUsers);
                }

                return false;
            }
        });
    }

    private void addLoginButton(Menu menu){
        menu.add(getString(R.string.login)).setIcon(R.drawable.login).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(loginIntent);
                return false;
            }
        });
    }

    private void addLogoutButton(Menu menu){
        menu.add(getString(R.string.logout)).setIcon(R.drawable.logout).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                new AlertDialog.Builder(BaseActivity.this)
                        .setTitle(getString(R.string.logoutTitle))
                        .setMessage(getString(R.string.logoutMessage))
                        .setNegativeButton(getString(R.string.cancelLogout), null)
                        .setPositiveButton(getString(R.string.confirmLogout), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                logout();
                            }
                        })
                        .create().show();

                return false;
            }
        });
    }

    private void logout(){
        AccessToken.access_token = null;
        AccessToken.userName = null;
        AccessToken.userrole = null;

        drawerLayout.closeDrawers();
        Intent refresh = new Intent(getApplicationContext(), MainActivity.class);
        refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(refresh);
    }

    private String getActiveActivity(){
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        return taskInfo.get(0).topActivity.getClassName();
    }
}
