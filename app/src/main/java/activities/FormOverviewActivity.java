package activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadForms();

        FrameLayout contentFrameLayout = findViewById(R.id.formbase_framelayout);
        getLayoutInflater().inflate(R.layout.activity_form_overview, contentFrameLayout);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(formContent.getFormContentName());

        sectionsView = findViewById(R.id.overview_content_view);
        updateView();
    }

    private void loadForms() {
        Intent intent = getIntent();
        String formContentName = intent.getStringExtra(Helper.FORM_CONTENT);
        String formName = intent.getStringExtra(Helper.FORM);

        form = Storage.getForm(formName, this);
        formContent = Storage.getFormContent(formContentName, this);
    }

    private void updateView() {
        for (Section section : form.getFormTemplate().getSections()) {
            LinearLayout sectionView = getSectionView(section);
            sectionsView.addView(sectionView);
        }
        sectionsView.addView(getImagesView());
    }

    private LinearLayout getImagesView() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout photoGallery = new LinearLayout(this);
        photoGallery.setOrientation(LinearLayout.HORIZONTAL);
        photoGallery.setLayoutParams(layoutParams);

        for (String imageName : formContent.getImageNames()) {
            Image image = Storage.getThumbnailForImage(imageName, this);
            if (image == null) continue;
            ImageView imageView = new ImageView(this);
            imageView.setImageBitmap(image.getBitmap());
            photoGallery.addView(imageView);
        }

        return photoGallery;
    }

    private LinearLayout getSectionView(Section section) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout sectionView = new LinearLayout(this);
        sectionView.setLayoutParams(layoutParams);
        sectionView.setOrientation(LinearLayout.VERTICAL);

        TextView sectionNameText = new TextView(this);
        sectionNameText.setText(section.getSectionName());
        sectionNameText.setTextSize(25);
        sectionNameText.setTextColor(Color.BLACK);
        sectionView.addView(sectionNameText);

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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.form_content_overview_menu, menu);
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.next_menu_item:
                setResult(Helper.CONTENT_SAVED_CODE);
                formContent.updateDate();
                Storage.cleanImgDir(this);
                finish();
                return true;
            case R.id.delete_menu_item:
                Storage.deleteFormContent(formContent, this);
                setResult(Helper.CONTENT_SAVED_CODE);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
