package activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
        String formName = intent.getStringExtra(Helper.FORM);
        String formContentId = intent.getStringExtra(Helper.FORM_CONTENT_ID);
        isEditing = intent.getBooleanExtra(Helper.IS_EDITING, false);

        loadFormData(formName, formContentId);
    }

    private void loadFormData(String formName, String formContentId) {
        Helper.log("formName " + formName);
        Helper.log("formContentId " + formContentId);
        form = Storage.getForm(formName, this);
        formContent = Storage.getFormContentById(formContentId, this);
        if (formContent == null && formContentId.contains(Helper.TEMP)) {
            formContentId = formContentId.replaceAll(Helper.TEMP, "");
            formContent = Storage.getFormContentById(formContentId, this);
        }

        if (formContent == null) {
            finish();
        }
    }

    private void updateView() {
        sectionsView.removeAllViews();
        loadFormData(form.getFormattedFormName(), formContent.getFormContentId());
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
                Intent formActivityIntent = new Intent(getApplicationContext(), FormActivity.class);
                Form form = Storage.getFormById(formContent.getFormId(), getApplicationContext());
                if (form != null) {
                    formActivityIntent.putExtra(Helper.FORM, form.getFormattedFormName());
                }
                formActivityIntent.putExtra(Helper.FORM_CONTENT_ID, formContent.getFormContentId());
                formActivityIntent.putExtra(Helper.IS_EDITING, true);
                formActivityIntent.putExtra(Helper.GO_TO_CAMERA, true);

                startActivityForResult(formActivityIntent, Helper.EDIT_PHOTOS_CODE);
            }
        };
    }

    private View.OnClickListener getSectionOnClickListener(final int sectionIndex) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent formActivityIntent = new Intent(getApplicationContext(), FormActivity.class);
                formActivityIntent.putExtra(Helper.IS_EDITING, true);
                if (form != null) {
                    formActivityIntent.putExtra(Helper.FORM, form.getFormattedFormName());
                }
                formActivityIntent.putExtra(Helper.FORM_CONTENT_ID, formContent.getFormContentId());
                formActivityIntent.putExtra(Helper.SECTION_INDEX, sectionIndex);

                startActivityForResult(formActivityIntent, Helper.EDIT_SECTION_CODE);
            }
        };
    }

    private LinearLayout getPhotoGalleryView() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 10, 10, 10);

        LinearLayout photoGalleryView = new LinearLayout(this);
        photoGalleryView.setOrientation(LinearLayout.VERTICAL);
        photoGalleryView.setLayoutParams(layoutParams);


        TextView photoGalleryTitleTextView = new TextView(this);
        photoGalleryTitleTextView.setText(R.string.photoGalleryTitle);
        setTitleTextView(photoGalleryTitleTextView);
        photoGalleryView.addView(photoGalleryTitleTextView);

        GridLayout photoGallery = new GridLayout(this);
        photoGallery.setColumnCount(2);
        photoGallery.setLayoutParams(layoutParams);

        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams1.setMargins(10, 10, 10, 10);

        for (String imageName : formContent.getImageNames()) {
            Image image = Storage.getImageByName(imageName, this);
            if (image == null) continue;
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(layoutParams1);
            Bitmap bitmap = image.getThumbnailBitmap(this);

            imageView.setImageBitmap(bitmap);
            photoGallery.addView(imageView);
        }

        photoGalleryView.addView(photoGallery);
        return photoGalleryView;
    }

    private LinearLayout getSectionView(Section section) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout sectionView = new LinearLayout(this);
        sectionView.setLayoutParams(layoutParams);
        sectionView.setOrientation(LinearLayout.VERTICAL);

        TextView sectionNameTextView = new TextView(this);
        sectionNameTextView.setText(section.getSectionName());
        setTitleTextView(sectionNameTextView);
        sectionView.addView(sectionNameTextView);

        for (Field field : section.getFields()) {
            LinearLayout fieldsAndDivider = getFieldView(field);
            sectionView.addView(fieldsAndDivider);
        }
        return sectionView;
    }

    private LinearLayout getFieldView(Field field) {
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        LinearLayout fieldView = new LinearLayout(this);
        fieldView.setLayoutParams(linearLayoutParams);
        fieldView.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams textViewLayout = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);

        TextView fieldNameText = new TextView(this);
        fieldNameText.setLayoutParams(textViewLayout);
        String fieldName = field.getFieldName() + ":";
        fieldNameText.setText(fieldName);
        fieldNameText.setTextSize(17);
        fieldNameText.setTextColor(Color.BLACK);

        TextView fieldValueText = new TextView(this);
        fieldValueText.setLayoutParams(textViewLayout);
        fieldValueText.setText(formContent.getAnswer(field.getFieldName()));
        fieldValueText.setWidth(0);
        fieldValueText.setTextColor(Color.BLACK);
        fieldValueText.setTextSize(17);

        fieldView.addView(fieldNameText);
        fieldView.addView(fieldValueText);

        LinearLayout fieldsAndDivider = new LinearLayout(this);
        fieldsAndDivider.setLayoutParams(linearLayoutParams);
        fieldsAndDivider.setOrientation(LinearLayout.VERTICAL);

        ViewGroup.LayoutParams dividerLayout = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 1);

        View divider = new View(this);
        divider.setLayoutParams(dividerLayout);
        divider.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

        fieldsAndDivider.addView(fieldView);
        fieldsAndDivider.addView(divider);

        return fieldsAndDivider;
    }

    private void setTitleTextView(TextView textView) {
        textView.setTextSize(25);
        textView.setTextColor(Color.BLACK);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (isEditing) {
            getMenuInflater().inflate(R.menu.form_overview_activity_save_menu, menu);
        } else {
            getMenuInflater().inflate(R.menu.form_overview_activity_delete_menu, menu);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_menu_item:
                getSaveDialog().show();
                return true;
            case R.id.delete_menu_item:
                getDeleteDialog().show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (isEditing) {
            getBackDialog().show();
        } else {
            super.onBackPressed();
        }
    }

    private AlertDialog getSaveDialog() {
        return new AlertDialog.Builder(this)
                .setTitle(R.string.saveDialogTitle)
                .setMessage(R.string.saveDialogMessage)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setResult(Helper.CONTENT_SAVED_CODE);
                        formContent.updateDate();
                        Context context = getApplicationContext();
                        Storage.confirmFormContent(context);
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

    private AlertDialog getBackDialog() {
        return new AlertDialog.Builder(this)
                .setTitle(R.string.backDialogTitle)
                .setMessage(R.string.backDialogMessage)
                .setPositiveButton(R.string.back, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Storage.cleanStorage(getApplicationContext());
                        Storage.deleteFormContent(formContent, getApplicationContext());
                        setResult(Helper.GO_BACK);
                        finish();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Helper.EDIT_PHOTOS_CODE || requestCode == Helper.EDIT_SECTION_CODE) {
            updateView();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
