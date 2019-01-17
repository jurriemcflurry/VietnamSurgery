package Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import toning.juriaan.Models.Helper;
import toning.juriaan.Models.R;

public class PhotoDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_photo_detail, contentFrameLayout);
        getSupportActionBar().setTitle(getString(R.string.camera_title));

        Intent photo = getIntent();
        Bitmap image = (Bitmap) photo.getParcelableExtra("image");
        final String imageName = photo.getStringExtra(Helper.IMAGE_NAME);

        ImageView imageView = findViewById(R.id.photo_detail_iv);
        imageView.setImageBitmap(image);

        FloatingActionButton deleteButton = findViewById(R.id.delete_btn);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(PhotoDetailActivity.this)
                        .setTitle("Delete picture?")
                        .setMessage("Are you sure you want to delete this picture?")
                        .setNegativeButton("No", null)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getIntent().putExtra(Helper.IMAGE_NAME, imageName);
                                setResult(0, getIntent());
                                finish();
                            }
                        }).create().show();
            }
        });
    }

    @Override
    public void onBackPressed(){
        getIntent().putExtra("imageid", Helper.NO_IMAGE_DELETED);
        setResult(Helper.DELETE_IMAGE, getIntent());
        finish();
    }
}
