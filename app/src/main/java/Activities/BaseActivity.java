package Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import toning.juriaan.Models.AccessToken;
import toning.juriaan.Models.R;

public class BaseActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_activity);

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

        if(AccessToken.userrole != null && AccessToken.userrole.equals(getString(R.string.admin))){
            menu.add(getString(R.string.users)).setIcon(R.drawable.user_list_menu_icon_black).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Intent naarUsers = new Intent(getApplicationContext(), UsersActivity.class);
                    startActivity(naarUsers);
                    return false;
                }
            });
        }

        if(AccessToken.access_token == null){
            menu.add(getString(R.string.login)).setIcon(R.drawable.login).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(loginIntent);
                    return false;
                }
            });
        }
        else{
            menu.add(getString(R.string.logout)).setIcon(R.drawable.logout).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    logout();
                    return false;
                }
            });
        }

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        drawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        switch(menuItem.getItemId()){
                            case R.id.nav_1: //Bovenste Item
                                Intent homeIntent = new Intent(getApplicationContext(), MainActivity.class);
                                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(homeIntent);
                                break;
                            case R.id.nav_2:
                                Intent toFormContentIntent = new Intent(
                                        getApplicationContext(), FormListActivity.class);
                                startActivity(toFormContentIntent);
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

    private void logout(){
        AccessToken.access_token = null;
        AccessToken.userName = null;
        AccessToken.userrole = null;

        drawerLayout.closeDrawers();
        Intent refresh = new Intent(getApplicationContext(), MainActivity.class);
        refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(refresh);
    }
}
