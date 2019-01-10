package Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

import toning.juriaan.Models.Field;
import toning.juriaan.Models.Form;
import toning.juriaan.Models.FormContent;
import toning.juriaan.Models.Helper;
import toning.juriaan.Models.Image;
import toning.juriaan.Models.R;
import toning.juriaan.Models.Section;
import toning.juriaan.Models.Storage;

public class FormOverviewActivity extends AppCompatActivity {

    private LinearLayout sectionsView;
    private FormContent formContent;
    private Form form;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_overview);

        sectionsView = findViewById(R.id.overview_content_view);
        loadForms();
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
        Helper.log("FormOverviewActivity.updateView()");
        for(Section section : form.getFormTemplate().getSections()) {
            LinearLayout sectionView = getSectionView(section);
            sectionsView.addView(sectionView);
            Helper.log(section.getSectionName());
        }
        sectionsView.addView(getImagesView());
    }

<<<<<<< HEAD
=======
    private LinearLayout getImagesView() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout photoGallery = new LinearLayout(this);
        photoGallery.setOrientation(LinearLayout.HORIZONTAL);
        photoGallery.setLayoutParams(layoutParams);

        ArrayList<Image> images = Storage.getImagesForFormContent(formContent, this);
        for (Image image : images) {
            Helper.log("parsing image");
            ImageView imageView = new ImageView(this);
            imageView.setMaxWidth(80);
            imageView.setMaxHeight(80);
            imageView.setImageBitmap(image.getBitmap());
            photoGallery.addView(imageView);
        }

        return photoGallery;
    }

>>>>>>> master
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
}
