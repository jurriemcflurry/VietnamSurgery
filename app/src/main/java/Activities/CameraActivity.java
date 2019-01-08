package Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import toning.juriaan.Models.R;

public class CameraActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Bitmap mImageBitmap;
    private GridLayout gridLayout1;
    private Button saveImagesButton;
    private ArrayList<Bitmap> mImages = new ArrayList<>();
    private int counter = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity);
        gridLayout1 = (GridLayout) findViewById(R.id.gridLayout1);
        saveImagesButton = (Button) findViewById(R.id.save_images);

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

        saveImagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImages();
                Toast.makeText(CameraActivity.this, "Images succesfully saved!", Toast.LENGTH_LONG ).show();
            }
        });
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
            mImages.add(counter, mImageBitmap);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent photoDetail = new Intent(CameraActivity.this, PhotoDetailActivity.class);
                    photoDetail.putExtra("image", mImageBitmap);
                    photoDetail.putExtra("id", counter);
                    startActivityForResult(photoDetail, 0);
                }
            });
            gridLayout1.addView(imageView, counter);
            imageView.getLayoutParams().height = (getDisplayMetrics().heightPixels)/2;
            imageView.getLayoutParams().width = (getDisplayMetrics().widthPixels)/2;
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        }
        else if(requestCode == 0){
            //return from deletebutton from photodetailpage
            // remove image from contentlist and imageview from gridlayout
            int removeableObject = data.getIntExtra("imageid", 0);

            if(removeableObject != 9999) {
                mImages.remove(removeableObject);
                gridLayout1.removeViewAt(removeableObject);
                gridLayout1.requestLayout();
            }
        }
    }

    //ophalen van de schermafmetingen
    private DisplayMetrics getDisplayMetrics(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics;
    }

    //save images to device
    private boolean saveImages(){
        if(mImages.size() <= 0){
            return false;
        }

        String filename = "imageNumber";
        int i = 0;

        for(Bitmap image : mImages){
            try {
                filename += String.valueOf(i);
                FileOutputStream fos = this.openFileOutput(filename, Context.MODE_PRIVATE);

                image.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
                i++;
            }
            catch(Exception e){
                e.printStackTrace();
                return false;
            }
        }
        mImages.clear();
        gridLayout1.removeAllViews();
        return true;
    }

    @Override
    public void onBackPressed(){
        new AlertDialog.Builder(this)
                .setTitle("Exit?")
                .setMessage("Going back without saving erases the pictures")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Exit Photos", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CameraActivity.super.onBackPressed();
                    }
                }).create().show();
    }
}
