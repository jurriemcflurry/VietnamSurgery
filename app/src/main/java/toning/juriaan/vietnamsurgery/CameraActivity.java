package toning.juriaan.vietnamsurgery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

public class CameraActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Bitmap mImageBitmap;
    private GridLayout gridLayout1;
    private ArrayList<Bitmap> mImages = new ArrayList<>();
    private FormTemplate form;
    private int noOfSections;
    private Toolbar toolbar;

    String mCurrentPhotoPath;
    TextView sectionNameTv;
    TextView stepCounter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity);

        loadIntent();
        setupFields();
        setupToolbar();
        setupFloatingActionButton();

        /*if(form.getBitmapImages() != null) {
            mImages.addAll(form.getBitmapImages());
            for( Bitmap bitmapImage : form.getBitmapImages()) {
                try {
                    ImageView iv = new ImageView(this);
                    iv.setImageBitmap(bitmapImage);
                    gridLayout1.addView(iv);
                    iv.getLayoutParams().height = (getDisplayMetrics().heightPixels)/2;
                    iv.getLayoutParams().width = (getDisplayMetrics().widthPixels)/2;
                    iv.setScaleType(ImageView.ScaleType.FIT_XY);

                } catch (Exception ex) {
                    Log.e("TESTT", "oops " + ex.getMessage());
                }
            }
        }*/
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
        ab.setTitle("New form" + form.getFormName());
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
        String dateStamp = new SimpleDateFormat("yyyyMMdd_HH:mm:ss").format(new Date());
        String imageFileName = patientName + "_" + birthYear + "_" + dateStamp;
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
            File file = new File(mCurrentPhotoPath);
            ImageView imageView = new ImageView(CameraActivity.this);
            int thumbSize = 400;
            try {
                Bitmap thumb = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(mCurrentPhotoPath), thumbSize, thumbSize);
                /*Bitmap bitmap = MediaStore.Images.Media
                        .getBitmap(this.getContentResolver(), Uri.fromFile(file));*/
                imageView.setImageBitmap(thumb);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(10, 10, 10, 10);
                imageView.setLayoutParams(lp);
                gridLayout1.addView(imageView);
                mImages.add(thumb);

            } catch (Exception ex) {
                Log.i("TESTT", "I made a fkup");
            }
        }
    }

    private boolean saveImages(){
        if(mImages.size() <= 0){
            Toast.makeText(getApplicationContext(), "Make at least one picture", Toast.LENGTH_LONG).show();
            return false;
        }

        String filename = form.getSections().get(0).getFields().get(1).getAnswer();
        int i = 0;
        List<String> pictures = new ArrayList<>();

        for(Bitmap image : mImages){
            String newFileName = "";
            try {
                newFileName = filename + "_" + String.valueOf(i);
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

                FileOutputStream fos = new FileOutputStream(mypath);

                image.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
                pictures.add(mypath.getAbsolutePath());
                i++;
            }
            catch(Exception e){
                e.printStackTrace();
                return false;
            }
        }
        mImages.clear();
        gridLayout1.removeAllViews();
        form.setPictures(pictures);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // this takes the user 'back', as if they pressed the left-facing
                //form.setBitmapImages(mImages);
                Intent formIntent = new Intent(getApplicationContext(), FormActivity.class);
                formIntent.putExtra("obj_form", form);
                formIntent.putExtra("step", noOfSections);
                startActivity(formIntent);
                return true;
            case R.id.action_next:
                if(saveImages()){
                    Toast.makeText(CameraActivity.this, "Images saved!", Toast.LENGTH_SHORT).show();
                    Intent formOverviewIntent = new Intent(getApplicationContext(), OverviewFormActivity.class);
                    formOverviewIntent.putExtra("obj_form", form);
                    startActivity(formOverviewIntent);
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
}
