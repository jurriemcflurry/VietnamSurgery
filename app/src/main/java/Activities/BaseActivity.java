package Activities;

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

        if(AccessToken.userrole != null && AccessToken.userrole.equals(getString(R.string.admin))){
            menu.add(getString(R.string.users)).setIcon(R.drawable.user_menu_icon_black).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Intent naarUsers = new Intent(getApplicationContext(), UsersActivity.class);
                    startActivity(naarUsers);
                    return false;
                }
            });
        }

        View headerView = navigationView.getHeaderView(0);
        LinearLayout header = (LinearLayout) headerView.findViewById(R.id.headerlayout);
        final TextView login = (TextView) header.findViewById(R.id.Logintext);
        final TextView loggedInUser = (TextView) header.findViewById(R.id.LoggedinUser);
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        if(AccessToken.access_token != null){
            login.setText(getString(R.string.logout));
            loggedInUser.setText(AccessToken.userName);
            loggedInUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent toChangePassword = new Intent(getApplicationContext(), ChangePasswordActivity.class);
                    startActivity(toChangePassword);
                }
            });
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
                    AccessToken.userrole = null;
                    login.setText(getString(R.string.login));
                    loggedInUser.setText(getString(R.string.not_logged_in));
                    drawerLayout.closeDrawers();
                    Intent refresh = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(refresh);
                }
                else{
                    // ga naar pagina om in te loggen
                    Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
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
                        drawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        switch(menuItem.getItemId()){
                            case R.id.nav_1: //Bovenste Item
                                Intent naarHome = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(naarHome);
                                break;
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
}
