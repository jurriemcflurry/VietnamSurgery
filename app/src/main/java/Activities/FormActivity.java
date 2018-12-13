package Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import toning.juriaan.Models.R;
import toning.juriaan.Models.Form;
import toning.juriaan.Models.Helper;
import toning.juriaan.Models.Section;
import toning.juriaan.Models.SectionAdapter;
import toning.juriaan.Models.Storage;


@SuppressLint("Registered")
public class FormActivity extends AppCompatActivity {

    public final static String FORM = "FormActivity.form1";

    private Toolbar toolbar;
    private TextView sectionNameView;
    private RecyclerView fieldsView;
    private SectionAdapter sectionAdapter;

    private int sectionIndex;
    private Form form;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        Intent intent = getIntent();
        form = Storage.getFormTemplate(intent.getStringExtra(FORM), this);
        if (form != null) {
            form.setFormName("FormTemplate 1");
            Storage.saveFormTemplate(form, this);
        } else {
            Helper.log("FormActivity.form is null.");
        }

        toolbar = findViewById(R.id.form_toolbar);
        sectionNameView = findViewById(R.id.section_name);
        fieldsView = findViewById(R.id.fields_recycler_view);
        sectionAdapter = new SectionAdapter(this);
        fieldsView.setLayoutManager(new LinearLayoutManager(this));
        fieldsView.setAdapter(sectionAdapter);

        sectionIndex = 0;
        updateView();
    }

    private void updateView() {
        Section section = form.getFormTemplate().getSections()[sectionIndex];
        toolbar.setTitle(form.getFormName());
        sectionNameView.setText(section.getSectionName());
        sectionAdapter.setFields(section.getFields());

    }

    private void nextSection() {
        //TODO if index is higher than or equal to sections.size() goto camera
        if (sectionIndex < form.getFormTemplate().getSections().length - 1) {
            sectionIndex++;
            updateView();
        }
    }

    private void previousSection() {
        //TODO if index is 0 goto formoverview
        if (sectionIndex > 0) {
            sectionIndex--;
            updateView();
        }
    }
}
