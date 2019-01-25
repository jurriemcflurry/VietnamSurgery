package activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import toning.juriaan.models.Helper;
import toning.juriaan.models.Image;
import toning.juriaan.models.R;
import toning.juriaan.models.Storage;

public class PhotoDetailActivity extends FormBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.formbase_framelayout);
        getLayoutInflater().inflate(R.layout.activity_photo_detail, contentFrameLayout);
        getSupportActionBar().setTitle(getString(R.string.camera_title));

        Intent intent = getIntent();
        final String imageName = intent.getStringExtra(Helper.IMAGE_NAME);
        if (imageName == null) onBackPressed();

        Image image = Storage.getImageByName(imageName, this);
        if (image == null) onBackPressed();

        ImageView imageView = findViewById(R.id.photo_detail_iv);
        imageView.setImageBitmap(image.getImageBitmap(this));

        FloatingActionButton deleteButton = findViewById(R.id.delete_btn);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(PhotoDetailActivity.this)
                        .setTitle(getString(R.string.deleteTitle))
                        .setMessage(getString(R.string.deleteMessage))
                        .setNegativeButton(getString(R.string.cancelPhotoDelete), null)
                        .setPositiveButton(getString(R.string.confirmPhotoDelete), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getIntent().putExtra(Helper.IMAGE_NAME, imageName);
                                setResult(Helper.DELETE_IMAGE, getIntent());
                                finish();
                            }
                        }).create().show();
            }
        });
    }

    @Override
    public void onBackPressed(){
        getIntent().putExtra(Helper.IMAGE_NAME, Helper.NO_IMAGE_DELETED);
        setResult(Helper.NO_IMAGE_DELETED, getIntent());
        finish();
    }
}
