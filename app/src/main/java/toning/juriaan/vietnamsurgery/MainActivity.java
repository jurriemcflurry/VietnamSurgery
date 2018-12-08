package toning.juriaan.vietnamsurgery;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //thema moet altijd worden gezet naar AppTheme, zodat de Launcher van het splashscreen niet bij elke actie wordt getoond
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
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

                        return true;
                    }
                });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        Button OpenCamera = (Button) findViewById(R.id.ToCamera);
        OpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toCamera = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(toCamera);
            }
        });

        final Button toFormActivityButton = findViewById(R.id.toFormActivity);
        toFormActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toFormActivityIntent = new Intent(MainActivity.this, FormActivity.class);

                int index = 0;
                toFormActivityIntent.putExtra(FormActivity.INDEX, index);
                Form form = getDummyForm();
                Helper.log("\n\n" + form.toString() + "\n\n");

                startActivity(toFormActivityIntent);
            }
        });
    }

    private Form getDummyForm() {
        ArrayList<Field> fields = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            fields.add(new TextField("TextFieldName " + i));
        }

        fields.add(new NumberField("NumberFieldName 1"));
        ArrayList<String> options = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            options.add("Option " + 1);
        }
        fields.add(new DropDownField("DropDownFieldName 1", options));

        Field[] fieldArray = fields.toArray(new Field[0]);

        ArrayList<Section> sections = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            sections.add(new Section("SectionName " + i, fieldArray));
        }

        Section[] sectionArray = sections.toArray(new Section[0]);

        return new Form("DummyForm 1", sectionArray);
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
}
