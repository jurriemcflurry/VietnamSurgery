package toning.juriaan.vietnamsurgery;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class CameraActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Bitmap mImageBitmap;
    private GridLayout gridLayout1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity);
        gridLayout1 = (GridLayout) findViewById(R.id.gridLayout1);

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
        }
    }

    //ophalen van de schermafmetingen
    private DisplayMetrics getDisplayMetrics(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics;
    }
}
