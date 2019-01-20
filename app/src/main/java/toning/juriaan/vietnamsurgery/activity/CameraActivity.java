package toning.juriaan.vietnamsurgery.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import java.util.Locale;

import toning.juriaan.vietnamsurgery.R;
import toning.juriaan.vietnamsurgery.Utility.PhotoUtils;
import toning.juriaan.vietnamsurgery.Utility.Utils;
import toning.juriaan.vietnamsurgery.model.FormTemplate;

public class CameraActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_DELETE_IMAGE = 2;
    private final String TAG = this.getClass().getSimpleName();
    private GridLayout pictureGrid;
    private ArrayList<Bitmap> mImages = new ArrayList<>();
    private FormTemplate form;
    private int noOfSections;
    private Toolbar toolbar;

    String photoName;
    String rootDir;
    File storageDirJpg;
    File storageDirPng;
    String mCurrentPhotoPath;
    TextView sectionNameTv;
    TextView stepCounter;
    List<String> pictures = new ArrayList<>();
    List<String> thumbImages = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        loadIntent();
        setupFields();
        makeDirsIfNecessary();
        setupToolbar();
        setupFloatingActionButton();
        checkForPictures();
    }

    /**
     * Method to load the intent
     */
    private void loadIntent(){
        Intent i = getIntent();
        form = i.getParcelableExtra("obj_form");
    }

    /**
     * Method to set up the fields that we need in this activity
     */
    private void setupFields(){
        pictureGrid = findViewById(R.id.gridLayout1);
        noOfSections = form.getSections().size();
        stepCounter = findViewById(R.id.step_counter);
        stepCounter.setText(getString(R.string.step_text, noOfSections + 1, noOfSections + 1));
        sectionNameTv = findViewById(R.id.section_name);
        sectionNameTv.setText(R.string.section_name_photos);
        toolbar = findViewById(R.id.form_toolbar);
        Utils.setSharedPrefs(this);
        rootDir = Utils.getRootDir();
        storageDirJpg = new File( rootDir + File.separator + form.getFormName());
        storageDirPng = new File(rootDir + File.separator + form.getFormName() + File.separator  + "thumbs");
    }

    /**
     * Method to create dirs if they don't exist yet
     */
    private void makeDirsIfNecessary() {
        if (!storageDirPng.exists()) {
            if(!storageDirPng.mkdirs()){
                Log.w("Creating file error :", storageDirPng.getAbsolutePath());
            }
        }
        if (!storageDirJpg.exists()) {
            if(!storageDirJpg.mkdirs()){
                Log.w("Creating file error: ", storageDirJpg.getAbsolutePath());
            }
        }
    }

    /**
     * Method to set up the toolbar for this activity
     */
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        String name = form.getSections().get(0).getFields().get(1).getAnswer();
        String birthYear = form.getSections().get(0).getFields().get(2).getAnswer();
        ab.setTitle(getString(R.string.form_name, form.getFormName(), name, birthYear));
    }

    /**
     * Method to set up the FAB
     */
    private void setupFloatingActionButton(){
        //onClick opent de native camera van de telefoon
        FloatingActionButton photoButton = this.findViewById(R.id.fab_camera);
        photoButton.setOnClickListener((View v) ->
            dispatchTakePictureIntent()
        );
    }

    /**
     * The method that starts the intent to take a picture.
     * It also creates a file for the picture that is going to be taken
     */
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

            Uri photoURI;

            // Continue only if the File was successfully created
            if (photoFile != null) {
                if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                    photoURI = Uri.fromFile(photoFile);
                } else {
                    photoURI = FileProvider.getUriForFile(this,
                            "toning.juriaan.vietnamsurgery.fileprovider",
                            photoFile);
                }


                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    /**
     * Method to create an ImageFile
     * @return File imageFile
     * @throws IOException if creating file fails
     */
    private File createImageFile() throws IOException {
        // Create an image file name - At this moment we're using the name, birthday & dateTimeformat
        // Todo: Create a possibility to get a name out of the Excel or let the user choose.
        String patientName = form.getSections().get(0).getFields().get(1).getAnswer();
        String birthYear = form.getSections().get(0).getFields().get(2).getAnswer();
        String district = form.getSections().get(1).getFields().get(3).getAnswer();
        SimpleDateFormat format= new SimpleDateFormat("yyyyMMdd_HH:mm:ss",Locale.getDefault());
        String myDate = format.format(new Date());
        if(mImages.size() > 0) {
            photoName = patientName + "_" + birthYear + "_" + district + "_" + myDate + "_" + Integer.toString(mImages.size());
        } else {
            photoName = patientName + "_" + birthYear + "_" + district + "_" + myDate;
        }
        File image = new File(storageDirJpg, photoName + ".jpg");

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * Method to handle the result from a startActivityForResult
     * @param requestCode requestCode from the called activity
     * @param resultCode resultCode from the called activity
     * @param data the data that was sent back from the called activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // When an image is taken, try to create a thumb, add it to a list, save it and put it in a grid
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
                Log.i(TAG, getString(R.string.error, ex.getMessage()));
            }
        }
        // When an image has to be deleted, delete this image
        if(requestCode == REQUEST_DELETE_IMAGE && resultCode == RESULT_OK) {
            String photoUrl = data.getStringExtra("photoUrl");
            if(deletePhoto(photoUrl)) {
                emptyGrid();
                checkForPictures();
            } else {
                Toast.makeText(this, R.string.error_delete, Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Method to delete the photo & thumbimage
     * @param photoUrl String that contains the absolute photoUrl
     * @return boolean
     */
    private boolean deletePhoto(String photoUrl) {
        File jpgFile = new File(photoUrl);
        if(jpgFile.exists() && jpgFile.delete()) {
            pictures.remove(photoUrl);
            form.setPictures(pictures);
            File pngFile = new File(storageDirPng, jpgFile.getName().replace("jpg", "png"));
            if(pngFile.exists() && pngFile.delete()){
                thumbImages.remove(pngFile.getAbsolutePath());
                form.setThumbImages(thumbImages);
                return true;
            } else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    /**
     * Method to save the thumbImage
     * @param thumb Bitmap of the thumbImage
     */
    private void saveThumb(Bitmap thumb) {
        try {
            File photoFile = new File(storageDirPng, photoName + ".png");

            FileOutputStream fos = new FileOutputStream(photoFile);
            thumb.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            thumbImages.add(photoFile.getAbsolutePath());
            form.setThumbImages(thumbImages);

        } catch (Exception ex) {
            Toast.makeText(this, R.string.error_save_thumb, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Method to check if it's allowed to go to the next page
     * @return boolean
     */
    private boolean goNext(){
        if(mImages.size() <= 0){
            Toast.makeText(getApplicationContext(), R.string.error_not_enough_pics, Toast.LENGTH_LONG).show();
            return false;
        }

        emptyGrid();
        return true;
    }

    /**
     * Method to add the picture in the picturegrid
     * @param picture Bitmap of the picture that needs to be added
     * @param path String with absolute path of the full picture
     */
    private void putPictureInGridLayout(Bitmap picture, String path) {
        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(10, 10, 10, 10);
        imageView.setLayoutParams(lp);
        imageView.setImageBitmap(picture);
        imageView.setOnClickListener((View v) ->
            PhotoUtils.goToDetailPage(new File(path), this, form)
        );
        pictureGrid.addView(imageView);
    }

    /**
     * Method to check if there are already pictures available in the form.
     * If so, these are placed in the picturegrid
     */
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

    /**
     * Method to empty the grid
     */
    private void emptyGrid() {
        pictureGrid.removeAllViews();
        mImages.clear();
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
        emptyGrid();
        pictures = form.getPictures();
        thumbImages = form.getThumbImages();
        checkForPictures();
    }
}
