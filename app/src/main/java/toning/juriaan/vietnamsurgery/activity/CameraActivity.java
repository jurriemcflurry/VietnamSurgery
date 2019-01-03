package toning.juriaan.vietnamsurgery.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import toning.juriaan.vietnamsurgery.model.FormTemplate;
import toning.juriaan.vietnamsurgery.R;

public class CameraActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private GridLayout gridLayout1;
    private ArrayList<Bitmap> mImages = new ArrayList<>();
    private FormTemplate form;
    private int noOfSections;
    private Toolbar toolbar;

    String mCurrentPhotoPath;
    TextView sectionNameTv;
    TextView stepCounter;
    List<String> pictures = new ArrayList<>();
    List<String> thumbImages = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity);

        loadIntent();
        setupFields();
        setupToolbar();
        setupFloatingActionButton();
        checkForPictures();
    }

    private void loadIntent(){
        Intent i = getIntent();
        form = i.getParcelableExtra("obj_form");
    }

    private void setupFields(){
        gridLayout1 = findViewById(R.id.gridLayout1);
        noOfSections = form.getSections().size();
        stepCounter = findViewById(R.id.step_counter);
        stepCounter.setText(getString(R.string.step_text, noOfSections + 1, noOfSections + 1));
        sectionNameTv = findViewById(R.id.section_name);
        sectionNameTv.setText(R.string.section_name_photos);
        toolbar = findViewById(R.id.form_toolbar);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        String name = form.getSections().get(0).getFields().get(1).getAnswer();
        String birthYear = form.getSections().get(0).getFields().get(2).getAnswer();
        ab.setTitle(getString(R.string.form_name, form.getFormName(), name, birthYear));
    }

    private void setupFloatingActionButton(){
        //onClick opent de native camera van de telefoon
        FloatingActionButton photoButton = this.findViewById(R.id.fab_camera);
        photoButton.setOnClickListener((View v) -> {
            dispatchTakePictureIntent();
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "toning.juriaan.vietnamsurgery.fileprovider",
                        photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name - At this moment we're using the name, birthday & dateTimeformat
        // Todo: Create a possibility to get a name out of the Excel or let the user choose.
        String patientName = form.getSections().get(0).getFields().get(1).getAnswer();
        String birthYear = form.getSections().get(0).getFields().get(2).getAnswer();
        String dateStamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String imageFileName;
        if(mImages.size() > 0) {
            imageFileName = patientName + "_" + birthYear + "_" + "_" + dateStamp + "_" + Integer.toString(mImages.size());
        } else {
            imageFileName = patientName + "_" + birthYear + "_" + "_" + dateStamp;
        }
        File storageDir = new File(Environment.getExternalStorageDirectory().toString() + "/LenTab/lentab-susanne/VietnamSurgery");
        File image = new File(storageDir, imageFileName + ".jpg");

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    //het resultaat van de camera (een foto) wordt hier in een nieuwe ImageView gestopt
    //de imageview wordt toegevoegd aan een gridlayout
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            int thumbSize = 400;
            try {
                Bitmap thumb = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(mCurrentPhotoPath), thumbSize, thumbSize);
                mImages.add(thumb);
                saveThumb(thumb);
                putPictureInGridLayout(thumb, mCurrentPhotoPath);
                pictures.add(mCurrentPhotoPath);
                form.setPictures(pictures);
            } catch (Exception ex) {
                Log.i("TESTT", "I made a fkup");
            }
        }
    }

    private void saveThumb(Bitmap thumb) {
        // Todo: Filename...?
        String filename = form.getSections().get(0).getFields().get(1).getAnswer();
        try {
            String newFileName = filename + "_" + String.valueOf(mImages.size());
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + File.separator + "/LenTab/lentab-susanne/VietnamSurgery/thumbs");
            File mypath = null;

            if(!myDir.exists()) {
                if(myDir.mkdirs()){
                    mypath = new File(myDir, newFileName + ".png");
                }
            } else {
                mypath = new File(myDir, newFileName + ".png");
            }

            if(mypath != null) {
                FileOutputStream fos = new FileOutputStream(mypath);
                thumb.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
                thumbImages.add(mypath.getAbsolutePath());
                form.setThumbImages(thumbImages);
            } else {
                Toast.makeText(this, "There was an error while saving the thumb-image. Please try again.", Toast.LENGTH_LONG).show();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private boolean goNext(){
        if(mImages.size() <= 0){
            Toast.makeText(getApplicationContext(), "Make at least one picture", Toast.LENGTH_LONG).show();
            return false;
        }

        mImages.clear();
        gridLayout1.removeAllViews();
        return true;
    }

    private void goToDetailPage(File photoFile) {
        Intent intent = new Intent(this, DetailPhotoActivity.class);
        intent.putExtra("obj_form", form);
        intent.putExtra("photoUrl", photoFile.getAbsolutePath());
        startActivity(intent);
    }

    private void putPictureInGridLayout(Bitmap picture, String path) {
        ImageView imageView = new ImageView(this);
        imageView.setImageBitmap(picture);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(10, 10, 10, 10);
        imageView.setLayoutParams(lp);
        imageView.setOnClickListener((View v) ->
            goToDetailPage(new File(path))
        );
        gridLayout1.addView(imageView);
    }

    private void checkForPictures() {
        if(form.getThumbImages().size() > 0) {
            int index = 0;
            for( String filePath : thumbImages) {
                Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                putPictureInGridLayout(bitmap, pictures.get(index));
                mImages.add(bitmap);
                index++;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_next:
                if(goNext()){
                    Intent formOverviewIntent = new Intent(getApplicationContext(), OverviewFormActivity.class);
                    formOverviewIntent.putExtra("obj_form", form);
                    startActivity(formOverviewIntent);
                    finish();
                }
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
    public void onBackPressed() {
        // this takes the user 'back', as if they pressed the left-facing
        Intent formIntent = new Intent(getApplicationContext(), FormActivity.class);
        formIntent.putExtra("obj_form", form);
        formIntent.putExtra("step", noOfSections);
        startActivity(formIntent);
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        gridLayout1.removeAllViews();
        mImages.clear();
        pictures = form.getPictures();
        thumbImages = form.getThumbImages();
        checkForPictures();
    }
}
