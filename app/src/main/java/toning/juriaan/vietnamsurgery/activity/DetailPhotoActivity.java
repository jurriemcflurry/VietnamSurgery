package toning.juriaan.vietnamsurgery.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import toning.juriaan.vietnamsurgery.model.FormTemplate;
import toning.juriaan.vietnamsurgery.R;

public class DetailPhotoActivity extends AppCompatActivity {

    Toolbar toolbar;
    FormTemplate form;
    TextView sectionNameTv;
    int noOfSections;
    TextView stepCounter;
    ActionBar ab;
    String photoUrl;
    ImageView imageView;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_photo);

        loadIntent();
        setupFields();
        setupToolbar();
        setupPhoto();
    }

    private void loadIntent(){
        Intent i = getIntent();
        form = i.getParcelableExtra("obj_form");
        photoUrl = i.getStringExtra("photoUrl");
    }

    private void setupFields(){
        sectionNameTv = findViewById(R.id.section_name);
        sectionNameTv.setText(R.string.section_name_photo_detail);
        noOfSections = form.getSections().size();
        stepCounter = findViewById(R.id.step_counter);
        stepCounter.setText(getString(R.string.step_text, noOfSections+1, noOfSections+1));
        toolbar = findViewById(R.id.form_toolbar);
        imageView = findViewById(R.id.photo_detail_iv);
        fab = findViewById(R.id.delete_btn);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        String name = form.getSections().get(0).getFields().get(1).getAnswer();
        String birthYear = form.getSections().get(0).getFields().get(2).getAnswer();
        ab.setTitle(getString(R.string.form_name, form.getFormName(), name, birthYear));
    }

    private void setupPhoto() {
        File photo = new File(photoUrl);
        if(photo.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(photoUrl);

            imageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_next:
                // Go back to previous activity
                onSupportNavigateUp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
