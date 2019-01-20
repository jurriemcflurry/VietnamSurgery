package activities;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;

import toning.juriaan.models.FormContent;
import toning.juriaan.models.Helper;
import toning.juriaan.models.Image;
import toning.juriaan.models.R;
import toning.juriaan.models.SaveImageHandler;
import toning.juriaan.models.Storage;

public class CameraActivity extends FormBaseActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private GridLayout imageGridLayout;
    private FormContent formContent;
    private String formName;
    private File nextImageFile;
    private Uri nextImageUri;
    private boolean isNew;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.formbase_framelayout);
        getLayoutInflater().inflate(R.layout.activity_camera, contentFrameLayout);

        loadIntent();
        getSupportActionBar().setTitle(formContent.getFormContentName());
        updateNextImage();
        imageGridLayout = findViewById(R.id.image_grid_layout);
        imageGridLayout.setColumnCount(2);

        //onClick opent de native camera van de telefoon
        FloatingActionButton photoButton = this.findViewById(R.id.fab_camera);
        photoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, nextImageFile.getName());
                    nextImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, nextImageUri);
//                    cameraIntent.putExtra(MediaStore.Images.ImageColumns.ORIENTATION, );
                    startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });
        updateView();
    }

    private synchronized void updateView() {
        imageGridLayout.removeAllViews();
        for (String imageName : formContent.getImageNames()) {
            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(10, 10, 10, 10);
            imageView.setLayoutParams(layoutParams);

            loadImageView(imageName, imageView);
            imageGridLayout.addView(imageView);
        }
    }

    private void loadImageView(final String imageName, final ImageView imageView) {
        Image image = Storage.getImageByName(imageName, this);
        if (image == null) return;

        imageView.setImageResource(0);

        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Bitmap bitmap = image.getThumbnailBitmap(this);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent photoDetailIntent = new Intent(getApplicationContext(), PhotoDetailActivity.class);
                    photoDetailIntent.putExtra(Helper.IMAGE_NAME, imageName);
                    startActivityForResult(photoDetailIntent, Helper.CAMERA_ACTIVITY_CODE);
                }
            });
        } else {
            Drawable refreshImage = getDrawable(R.drawable.refresh_icon_black);
            bitmap = ((BitmapDrawable) refreshImage).getBitmap();
            bitmap = Bitmap.createScaledBitmap(bitmap, Helper.THUMBNAIL_SIZE, Helper.THUMBNAIL_SIZE, true);
            imageView.setImageBitmap(bitmap);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadImageView(imageName, imageView);
                }
            });
        }
    }

    private void loadIntent() {
        Intent intent = getIntent();
        formName = intent.getStringExtra(Helper.FORM);
        formContent = Storage.getFormContent(intent.getStringExtra(Helper.FORM_CONTENT), this);
        isNew = intent.getBooleanExtra(Helper.IS_NEW, false);
        Helper.log("Camera load with " + isNew);
    }

    //het resultaat van de camera (een foto) wordt hier in een nieuwe ImageView gestopt
    //de imageview wordt toegevoegd aan een gridlayout
    //click op de imageview geeft de foto groter weer
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            handleSaveImage();
            updateView();
        } else if (requestCode == Helper.CAMERA_ACTIVITY_CODE) {
            if (resultCode == Helper.CONTENT_SAVED_CODE) {
                setResult(Helper.CONTENT_SAVED_CODE);
                finish();
            } else if (resultCode == Helper.DELETE_IMAGE) {
                handleDeleteImage(data);
                updateView();
            }
        }
    }

    private void handleSaveImage() {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), nextImageUri);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bitmap == null) return;


        formContent.addImageName(nextImageFile.getName());
        Image image = new Image(nextImageFile.getName(), bitmap, nextImageUri);

        SaveImageHandler saveImageHandler = new SaveImageHandler(image, this);
        new Thread(saveImageHandler).start();

        Storage.saveFormContent(formContent, this);
        updateNextImage();
    }

    private void updateNextImage() {
        String imageName = Image.getNextImageName(formContent, this);
        nextImageFile = Storage.getImageFileWithName(imageName + Helper.IMAGE_EXTENSION, this);
        nextImageUri = Uri.fromFile(nextImageFile);
    }

    private void handleDeleteImage(Intent data) {
        String imageNameToRemove = data.getStringExtra(Helper.IMAGE_NAME);
        Helper.log("CameraActivity.handleDeleteImage() " + imageNameToRemove);
        if (!formContent.getImageNames().contains(imageNameToRemove)) return;

        formContent.getImageNames().remove(imageNameToRemove);
        Storage.deleteImage(new Image(imageNameToRemove), this);
    }

    //ophalen van de schermafmetingen
    private DisplayMetrics getDisplayMetrics() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.next_menu_item:
                if (formContent.getImageNames().isEmpty()) {
                    new AlertDialog.Builder(this)
                            .setTitle("No images found")
                            .setMessage("You need to take at least one picture")
                            .setNegativeButton("Back", null)
                            .create().show();
                } else {
                    Intent formOverviewIntent = new Intent(getApplicationContext(), FormOverviewActivity.class);
                    formOverviewIntent.putExtra(Helper.FORM, formName);
                    formOverviewIntent.putExtra(Helper.FORM_CONTENT, formContent.getFormContentId());
                    formOverviewIntent.putExtra(Helper.IS_NEW, isNew);
                    Helper.log("Camera start with " + isNew);
                    startActivityForResult(formOverviewIntent, Helper.CAMERA_ACTIVITY_CODE);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        setResult(Helper.UPDATE_CODE);
        super.onBackPressed();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.form_activity_menu, menu);
        return super.onPrepareOptionsMenu(menu);
    }
}
