package Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import toning.juriaan.Models.Field;
import toning.juriaan.Models.FieldContent;
import toning.juriaan.Models.FieldType;
import toning.juriaan.Models.R;
import toning.juriaan.Models.Form;
import toning.juriaan.Models.Helper;
import toning.juriaan.Models.Section;
import toning.juriaan.Models.SectionAdapter;
import toning.juriaan.Models.Storage;


@SuppressLint("Registered")
public class FormActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public final static String FORM = "FormActivity.form1";

    private Toolbar toolbar;
    private TextView sectionNameView;
    private RecyclerView fieldsView;
    private SectionAdapter sectionAdapter;
    private ArrayList<Pair> dropDownValues;

    private int sectionIndex;
    private Form form;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        Intent intent = getIntent();
        form = Storage.getFormTemplate(intent.getStringExtra(FORM), this);
        dropDownValues = new ArrayList<>();

        toolbar = findViewById(R.id.form_toolbar);
        setSupportActionBar(toolbar);
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.form_activity_menu, menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.next_menu_item:
                nextSection();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void nextSection() {
        //TODO if index is higher than or equal to sections.size() goto camera
        saveAnswers();
        Storage.saveForm(form, this);
        if (sectionIndex < form.getFormTemplate().getSections().length - 1) {
            sectionIndex++;
            updateView();
        } else {

        }
    }

    private void saveAnswers() {
        Field[] fields = form.getFormTemplate().getSections()[sectionIndex].getFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (field.getType().equals(FieldType.text.toString()) || field.getType().equals(FieldType.number.toString())) {
                EditText textField = findViewById(i);
                String fieldValue = textField.getText().toString();
                saveAnswer(field.getFieldName(), fieldValue);
                textField.setText("");
            } else if (field.getType().equals(FieldType.choice.toString())) {
                Spinner spinner = findViewById(i);
                String fieldValue = getDropDownValueAt(i);
                saveAnswer(field.getFieldName(), fieldValue);
            }
        }
    }

    private void saveAnswer(String fieldName, String fieldValue) {
        for (FieldContent field : form.getFormContent().getFields()) {
            if (field.getName().equals(fieldName)) {
                field.setValue(fieldValue);
                return;
            }
        }
    }

    private void previousSection() {
        //TODO if index is 0 goto formoverview
        if (sectionIndex > 0) {
            sectionIndex--;
            updateView();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int fieldId = parent.getId();
        String fieldValue = (String) parent.getItemAtPosition(position);
        addDropDownPair(fieldId, fieldValue);
    }

    public void addDropDownPair(int first, String second) {
        for (int i = 0; i < dropDownValues.size(); i++) {
            if (dropDownValues.get(i).first.equals(first)) {
                dropDownValues.remove(i);
                break;
            }
        }
        Pair pair = Pair.create(first, second);
        dropDownValues.add(pair);
    }

    public String getDropDownValueAt(int id) {
        for (int i = 0; i < dropDownValues.size(); i++) {
            if (dropDownValues.get(i).first.equals(id)) {
                return (String) dropDownValues.get(i).second;
            }
        }
        return null;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
