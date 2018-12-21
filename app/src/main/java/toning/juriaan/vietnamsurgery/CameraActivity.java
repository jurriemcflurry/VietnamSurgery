package toning.juriaan.vietnamsurgery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
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

import static android.support.constraint.Constraints.TAG;

public class CameraActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Bitmap mImageBitmap;
    private GridLayout gridLayout1;
    private Button saveImagesButton;
    private ArrayList<Bitmap> mImages = new ArrayList<>();
    private FormTemplate form;

    private TextView sectionNameTv;
    private TextView stepCounter;
    private int noOfSections;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity);
        gridLayout1 = (GridLayout) findViewById(R.id.gridLayout1);
        saveImagesButton = (Button) findViewById(R.id.save_images);
        Intent i = getIntent();
        form = i.getParcelableExtra("obj_form");

        noOfSections = form.getSections().size();
        stepCounter = findViewById(R.id.step_counter);
        stepCounter.setText("Step " + Integer.toString(noOfSections + 1) + " of " + Integer.toString(noOfSections + 1));
        sectionNameTv = findViewById(R.id.section_name);
        sectionNameTv.setText("Photos");

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
                if(saveImages()){
                    Toast.makeText(CameraActivity.this, "Images saved!", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getApplicationContext(), OverviewFormActivity.class);
                    i.putExtra("obj_form", form);
                    startActivity(i);
                }
                // mImages.clear();
            }
        });
    }

    //het resultaat van de camera (een foto) wordt hier in een nieuwe ImageView gestopt
    //de imageview wordt toegevoegd aan een gridlayout
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            mImageBitmap = (Bitmap) extras.get("data");
            ImageView imageView = new ImageView(CameraActivity.this);
            imageView.setImageBitmap(mImageBitmap);
            gridLayout1.addView(imageView);
            imageView.getLayoutParams().height = (getDisplayMetrics().heightPixels)/2;
            imageView.getLayoutParams().width = (getDisplayMetrics().widthPixels)/2;
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            mImages.add(mImageBitmap);
        }
    }

    //ophalen van de schermafmetingen
    private DisplayMetrics getDisplayMetrics(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics;
    }

    private boolean saveImages(){
        if(mImages.size() <= 0){
            Toast.makeText(getApplicationContext(), "Make at least one picture", Toast.LENGTH_LONG).show();
            return false;
        }

        String filename = form.getSections().get(0).getFields().get(1).getAnswer() + "_";
        int i = 0;
        List<String> pictures = new ArrayList<>();

        for(Bitmap image : mImages){
            try {
                filename += String.valueOf(i);
                String root = Environment.getExternalStorageDirectory().toString();
                File myDir = new File(root + File.separator + "/LenTab/lentab-susanne/VietnamSurgery");
                File mypath = null;
                if(!myDir.exists()) {
                    if(myDir.mkdirs()){
                        mypath = new File(myDir, filename + ".png");
                    }
                } else {
                    mypath = new File(myDir, filename + ".png");
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
}
