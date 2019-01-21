package activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import toning.juriaan.models.Field;
import toning.juriaan.models.Form;
import toning.juriaan.models.FormContent;
import toning.juriaan.models.Helper;
import toning.juriaan.models.Image;
import toning.juriaan.models.R;
import toning.juriaan.models.Section;
import toning.juriaan.models.Storage;

public class FormOverviewActivity extends FormBaseActivity {

    private LinearLayout sectionsView;
    private FormContent formContent;
    private Form form;
    private boolean isEditing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadIntent();

        FrameLayout contentFrameLayout = findViewById(R.id.formbase_framelayout);
        getLayoutInflater().inflate(R.layout.activity_form_overview, contentFrameLayout);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(formContent.updateFormContentName(this));

        sectionsView = findViewById(R.id.overview_content_view);
        updateView();
    }

    private void loadIntent() {
        Intent intent = getIntent();
        String formContentName = intent.getStringExtra(Helper.FORM_CONTENT_ID);
        String formName = intent.getStringExtra(Helper.FORM);
        isEditing = intent.getBooleanExtra(Helper.IS_EDITING, false);

        form = Storage.getForm(formName, this);
        formContent = Storage.getFormContentById(formContentName, this);
    }

    private void updateView() {
        sectionsView.removeAllViews();
        for (int i = 0; i < form.getFormTemplate().getSections().size(); i++) {
            Section section = form.getFormTemplate().getSections().get(i);
            LinearLayout sectionView = getSectionView(section);
            sectionView.setOnClickListener(getSectionOnClickListener(i));
            sectionsView.addView(sectionView);
        }
        LinearLayout photoGallery = getPhotoGalleryView();
        photoGallery.setOnClickListener(getPhotoOnClickListener());
        sectionsView.addView(photoGallery);
    }

    private View.OnClickListener getPhotoOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                Form form = Storage.getFormById(formContent.getFormId(), getApplicationContext());
                if (form != null) {
                    intent.putExtra(Helper.SECTION_INDEX, form.getFormTemplate().getSections().size() - 1);
                    intent.putExtra(Helper.FORM, form.getFormattedFormName());
                }
                intent.putExtra(Helper.FORM_CONTENT_ID, formContent.getFormContentId());

                setResult(Helper.EDIT_PHOTOS_CODE, intent);
                finish();
            }
        };
    }

    private View.OnClickListener getSectionOnClickListener(final int sectionIndex) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                Form form = Storage.getFormById(formContent.getFormId(), getApplicationContext());
                if (form != null) {
                    intent.putExtra(Helper.FORM, form.getFormattedFormName());
                }
                intent.putExtra(Helper.FORM_CONTENT_ID, formContent.getFormContentId());

                setResult(Helper.EDIT_SECTION_CODE, intent);
                finish();
            }
        };
    }

    private LinearLayout getPhotoGalleryView() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout photoGalleryView = new LinearLayout(this);
        photoGalleryView.setOrientation(LinearLayout.VERTICAL);

        TextView photoGalleryTitleTextView = new TextView(this);
        photoGalleryTitleTextView.setText(R.string.photoGalleryTitle);
        setTitleTextView(photoGalleryTitleTextView);

        ScrollView scrollView = new ScrollView(this);
        photoGalleryView.addView(photoGalleryTitleTextView);
        photoGalleryView.addView(scrollView);

        LinearLayout photoGallery = new LinearLayout(this);
        photoGallery.setOrientation(LinearLayout.HORIZONTAL);
        photoGallery.setLayoutParams(layoutParams);

        for (String imageName : formContent.getImageNames()) {
            Image image = Storage.getImageByName(imageName, this);
            if (image == null) continue;
            ImageView imageView = new ImageView(this);
            Bitmap bitmap = image.getThumbnailBitmap(this);

            imageView.setImageBitmap(bitmap);
            photoGallery.addView(imageView);
        }

        scrollView.addView(photoGallery);
        return photoGalleryView;
    }

    private LinearLayout getSectionView(Section section) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout sectionView = new LinearLayout(this);
        sectionView.setLayoutParams(layoutParams);
        sectionView.setOrientation(LinearLayout.VERTICAL);

        TextView sectionNameTextView = new TextView(this);
        sectionNameTextView.setText(section.getSectionName());
        setTitleTextView(sectionNameTextView);
        sectionView.addView(sectionNameTextView);

        for (Field field : section.getFields()) {
            TextView fieldNameText = new TextView(this);
            String fieldName = field.getFieldName() + ":";
            fieldNameText.setText(fieldName);
            fieldNameText.setTextColor(Color.BLACK);
            sectionView.addView(fieldNameText);

            TextView fieldValueText = new TextView(this);
            fieldValueText.setText(formContent.getAnswer(field.getFieldName()));
            fieldValueText.setTextColor(Color.BLACK);
            fieldValueText.setTextSize(17);
            sectionView.addView(fieldValueText);
        }
        return sectionView;
    }

    private void setTitleTextView(TextView textView) {
        textView.setTextSize(25);
        textView.setTextColor(Color.BLACK);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (isEditing) {
            getMenuInflater().inflate(R.menu.form_activity_menu, menu);
        } else {
            getMenuInflater().inflate(R.menu.form_overview_activity_menu, menu);
        }
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.next_menu_item:
                return true;
            case R.id.delete_menu_item:
                getDeleteDialog().show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private AlertDialog getSaveDialog() {
        return new AlertDialog.Builder(this)
                .setTitle(R.string.saveDialogTitle)
                .setMessage(R.string.saveDialogMessage)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setResult(Helper.CONTENT_SAVED_CODE);
                        formContent.updateDate();
                        Storage.cleanImgDir(getApplicationContext());
                        finish();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
    }

    private AlertDialog getDeleteDialog() {
        return new AlertDialog.Builder(this)
                .setTitle(R.string.deleteDialogTitle)
                .setMessage(R.string.deleteDialogMessage)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Storage.deleteFormContent(formContent, getApplicationContext());
                        setResult(Helper.CONTENT_SAVED_CODE);
                        finish();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
    }
}
