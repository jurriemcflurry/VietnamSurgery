package Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;

import java.io.FileOutputStream;
import java.util.ArrayList;

import toning.juriaan.Models.R;

public class CameraActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Bitmap mImageBitmap;
    private GridLayout gridLayout1;
    private Button saveImagesButton;
    private ArrayList<Bitmap> mImages = new ArrayList<>();

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
            return false;
        }

        String filename = "imageNumber";
        FileOutputStream outputStream;
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

        return true;
    }
}
