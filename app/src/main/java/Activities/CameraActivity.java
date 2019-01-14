package Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

import toning.juriaan.Models.FormContent;
import toning.juriaan.Models.Helper;
import toning.juriaan.Models.Image;
import toning.juriaan.Models.R;
import toning.juriaan.Models.Storage;

public class CameraActivity extends FormBaseActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Bitmap mImageBitmap;
    private GridLayout gridLayout1;
    private Button saveImagesButton;
    private FormContent formContent;
    private String formName;
    private ArrayList<Image> mImages = new ArrayList<>();
    private int counter = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.formbase_framelayout);
        getLayoutInflater().inflate(R.layout.camera_activity, contentFrameLayout);
        getSupportActionBar().setTitle(getString(R.string.camera_title));

        gridLayout1 = (GridLayout) findViewById(R.id.gridLayout1);
        loadIntent();
        mImages = Storage.getImagesForFormContent(formContent, this);

        //onClick opent de native camera van de telefoon
        FloatingActionButton photoButton = (FloatingActionButton) this.findViewById(R.id.fab_camera);
        photoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });
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
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            mImageBitmap = (Bitmap) extras.get("data");
            ImageView imageView = new ImageView(CameraActivity.this);
            imageView.setImageBitmap(mImageBitmap);

            while(!mImages.isEmpty() && mImages.size() <= counter){
                counter++;
            }

            String imageName = formContent.getFormContentName() + "_image_" + (formContent.getImageNames().size() + 1);
            formContent.addImageName(imageName);
            mImages.add(counter, new Image(imageName, mImageBitmap));

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent photoDetail = new Intent(CameraActivity.this, PhotoDetailActivity.class);
                    photoDetail.putExtra("image", mImageBitmap);
                    photoDetail.putExtra("id", counter);
                    startActivityForResult(photoDetail, 0);
                }
            });
            saveImage(mImages.get(counter));
            gridLayout1.addView(imageView, counter);
            imageView.getLayoutParams().height = (getDisplayMetrics().heightPixels)/2;
            imageView.getLayoutParams().width = (getDisplayMetrics().widthPixels)/2;
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        }
        else if (requestCode == Helper.CAMERA_ACTIVITY_CODE) {
            if (resultCode == Helper.CONTENT_SAVED_CODE){
                setResult(Helper.CONTENT_SAVED_CODE);
                finish();
            }
        }
        else if(requestCode == 0){
            //return from deletebutton from photodetailpage
            // remove image from contentlist and imageview from gridlayout
            int removeableObject = data.getIntExtra("imageid", 0);

            if(removeableObject != 9999) {
                deleteImage(removeableObject);
                mImages.remove(removeableObject);
                gridLayout1.removeViewAt(removeableObject);
                gridLayout1.requestLayout();
            }
        }
    }

    private boolean deleteImage(int removeableObject){
        String dir = getFilesDir().getAbsoluteFile().getAbsolutePath() + "/images/";
        String path = dir + mImages.get(removeableObject).getImageName() + ".png";
        new File(path).delete();

        return true;
    }

    //ophalen van de schermafmetingen
    private DisplayMetrics getDisplayMetrics(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics;
    }

    //save images to device
    private boolean saveImage(Image image){
        Storage.saveImage(image, this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.next_menu_item:
                if(mImages.isEmpty()){
                    new AlertDialog.Builder(this)
                            .setTitle("No images found")
                            .setMessage("You need to take at least one picture")
                            .setNegativeButton("Back", null)
                            .create().show();
                }
                else {
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
    public void onBackPressed(){
        final CameraActivity cameraActivity = this;
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.exitTitle))
                .setMessage(getString(R.string.imageLoss))
                .setNegativeButton(getString(R.string.cancel), null)
                .setPositiveButton(getString(R.string.exitPhotos), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cameraActivity.goBack();
                    }
                }).create().show();
    }

    public void goBack() {
        setResult(Helper.UPDATE_CODE);
        super.onBackPressed();
    }
}
