package activities;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
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
import toning.juriaan.models.Storage;

public class CameraActivity extends FormBaseActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private GridLayout imageGridLayout;
    private FormContent formContent;
    private String formName;
    private final CameraActivity cameraActivity = this;
    private File nextImageFile;
    private Uri nextImageUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.formbase_framelayout);
        getLayoutInflater().inflate(R.layout.activity_camera, contentFrameLayout);
        getSupportActionBar().setTitle(getString(R.string.camera_title));

        loadIntent();
        updateNextImage();
        imageGridLayout = findViewById(R.id.image_grid_layout);

        //onClick opent de native camera van de telefoon
        FloatingActionButton photoButton = this.findViewById(R.id.fab_camera);
        photoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "foootoooo");
                    values.put(MediaStore.Images.Media.DESCRIPTION, "fooooooootttttttoooooooo");
                    nextImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, nextImageUri);
                    startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });
        Helper.log("CameraActivity.onCreate()");
        updateView();
    }

    private void updateView() {
        imageGridLayout.removeAllViews();
        Helper.log("updateView() " + formContent.getImageNames().size());
        for (String imageName : formContent.getImageNames()) {
            ImageView imageView = getImageView(imageName);
            if (imageView == null) continue;

            imageGridLayout.addView(imageView);
        }
    }

    private ImageView getImageView(final String imageName) {
        Image image = Storage.getImageByName(imageName, this);
        if (image == null) return null;

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 10, 10, 10);

        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(layoutParams);
        imageView.setImageURI(image.getUri());
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setMaxHeight(600);
        imageView.setMaxWidth(600);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoDetailIntent = new Intent(getApplicationContext(), PhotoDetailActivity.class);
                photoDetailIntent.putExtra(Helper.IMAGE_NAME, imageName);
                startActivityForResult(photoDetailIntent, Helper.CAMERA_ACTIVITY_CODE);
            }
        });

        return imageView;
    }

    private void loadIntent() {
        Intent intent = getIntent();
        formName = intent.getStringExtra(Helper.FORM);
        formContent = Storage.getFormContent(intent.getStringExtra(Helper.FORM_CONTENT), this);
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
        Storage.saveImage(image, this);
        Helper.log("handleSaveImage() " + formContent.getImageNames().size());
        Storage.saveFormContent(formContent, this);
        updateNextImage();
    }

    private void updateNextImage() {
        String imageName = Image.getNextImageName(formContent, this);
        nextImageFile = Storage.getImageFileWithName(imageName + Helper.IMAGE_EXTENSION, this);
        nextImageUri = Uri.fromFile(nextImageFile);
    }

    private void handleDeleteImage(Intent data) {
        String imageToRemoveName = data.getStringExtra(Helper.IMAGE_NAME);
        Helper.log("CameraActivity.handleDeleteImage() " + imageToRemoveName);
        if (!formContent.getImageNames().contains(imageToRemoveName)) return;

        formContent.getImageNames().remove(imageToRemoveName);
        Storage.deleteImage(imageToRemoveName, this);
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
                    formOverviewIntent.putExtra(Helper.FORM_CONTENT, formContent.getFormContentName());
                    startActivityForResult(formOverviewIntent, Helper.CAMERA_ACTIVITY_CODE);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
//        final CameraActivity cameraActivity = this;
//        new AlertDialog.Builder(this)
//                .setTitle(getString(R.string.exitTitle))
//                .setMessage(getString(R.string.imageLoss))
//                .setNegativeButton(getString(R.string.cancel), null)
//                .setPositiveButton(getString(R.string.exitPhotos), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        cameraActivity.goBack();
//                    }
//                }).create().show();
        setResult(Helper.UPDATE_CODE);
        super.onBackPressed();
    }
}
