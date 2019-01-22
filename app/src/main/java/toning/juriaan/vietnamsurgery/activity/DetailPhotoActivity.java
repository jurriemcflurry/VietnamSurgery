package toning.juriaan.vietnamsurgery.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import toning.juriaan.vietnamsurgery.R;
import toning.juriaan.vietnamsurgery.Utility.PhotoUtils;
import toning.juriaan.vietnamsurgery.Utility.Utils;
import toning.juriaan.vietnamsurgery.listener.OnSwipeTouchListener;
import toning.juriaan.vietnamsurgery.model.FormTemplate;

public class DetailPhotoActivity extends AppCompatActivity {

    final static int REQUEST_DELETE_IMAGE = 2;
    Toolbar toolbar;
    FormTemplate form;
    TextView sectionNameTv;
    int noOfSections;
    TextView stepCounter;
    ActionBar ab;
    int photoIndex;
    TextView photoCounter;
    String photoUrl;
    ImageView imageView;
    TextView textView;
    FloatingActionButton fab;
    LinearLayout layout;

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

    /**
     * Method to load the intent
     */
    private void loadIntent(){
        Intent i = getIntent();
        form = i.getParcelableExtra("obj_form");
        photoIndex = i.getIntExtra("photoIndex", 0);
    }

    /**
     * Method to set up the fields that are necessary for this activity
     */
    private void setupFields() {
        sectionNameTv = findViewById(R.id.section_name);
        sectionNameTv.setText(R.string.section_name_photo_detail);
        noOfSections = form.getSections().size();
        stepCounter = findViewById(R.id.step_counter);
        stepCounter.setText(getString(R.string.step_text, noOfSections + 1, noOfSections + 1));
        toolbar = findViewById(R.id.form_toolbar);
        imageView = findViewById(R.id.photo_detail_iv);
        textView = findViewById(R.id.photo_name);
        layout = findViewById(R.id.section_view);
        photoCounter = findViewById(R.id.photo_counter);
        fab = findViewById(R.id.delete_btn);
        fab.setOnClickListener((View v)-> {
                new AlertDialog.Builder(DetailPhotoActivity.this)
                        .setTitle(R.string.dialog_delete_picture_title)
                        .setMessage(R.string.dialog_delete_picture_text)
                        .setNegativeButton(R.string.dialog_cancel, null)
                        .setPositiveButton(R.string.dialog_ok, (DialogInterface dialog, int which) -> {
                            PhotoUtils.deletePhoto(photoUrl, form, new File(Utils.getRootDir() + File.separator + form.getFormName() + File.separator  + "thumbs"), this);
                            form.getPictures().remove(photoUrl);
                            if(form.getPictures().size() == 0){
                                imageView.setImageBitmap(null);
                                photoCounter.setText("");
                                textView.setText(R.string.picture_no_images);
                            } else {
                                photoIndex = 0;
                                photoUrl = form.getPictures().get(0);
                                setupPhoto();
                            }
                        }).create().show();
        });
        photoUrl = form.getPictures().get(photoIndex);
    }

    /**
     * Method to set up the toolbar for this activity
     */
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        // Todo: Wanneer ik Jo zover krijg -> Name and birthyear dynamically from Excel
        String name = form.getSections().get(0).getFields().get(1).getAnswer();
        String birthYear = form.getSections().get(0).getFields().get(2).getAnswer();
        ab.setTitle(getString(R.string.form_name, form.getFormName(), name, birthYear));
    }

    /**
     * Method to set up the photo and place it in the ImageView
     */
    private void setupPhoto() {
        File photo = new File(photoUrl);
        if(photo.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(photoUrl);
            textView.setText(photo.getName());
            imageView.setImageBitmap(bitmap);
            photoCounter.setText(getString(R.string.picture_counter, photoIndex + 1, form.getPictures().size()));
            layout.setOnTouchListener(new OnSwipeTouchListener(this) {
                public void onSwipeRight() {
                    photoIndex--;
                    if(photoIndex < 0) {
                        photoIndex++;
                        Toast.makeText(getApplicationContext(), R.string.last_picture, Toast.LENGTH_LONG).show();
                    } else {
                       photoUrl = form.getPictures().get(photoIndex);
                       setupPhoto();
                    }
                    // Previous pic
                }
                public void onSwipeLeft() {
                    photoIndex++;
                    if(photoIndex > form.getPictures().size()) {
                        photoIndex--;
                        Toast.makeText(getApplicationContext(), R.string.last_picture, Toast.LENGTH_LONG).show();
                    } else {
                        photoUrl = form.getPictures().get(photoIndex);
                        setupPhoto();
                    }
                }
            });
        } else {
            textView.setText(R.string.picture_not_found);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("obj_form", form);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }
}
