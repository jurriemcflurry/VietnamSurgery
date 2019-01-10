package Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import toning.juriaan.Models.Field;
import toning.juriaan.Models.FieldType;
import toning.juriaan.Models.FormContent;
import toning.juriaan.Models.Helper;
import toning.juriaan.Models.R;
import toning.juriaan.Models.Form;
import toning.juriaan.Models.Section;
import toning.juriaan.Models.Storage;


@SuppressLint("Registered")
public class FormActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Toolbar toolbar;
    private TextView sectionNameView;
    private LinearLayout fieldsView;
    private ArrayList<Pair> dropDownValues;
    private FormContent formContent;

    private int sectionIndex;
    private Form form;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        dropDownValues = new ArrayList<>();
        loadForms();

        toolbar = findViewById(R.id.form_toolbar);
        setSupportActionBar(toolbar);
        sectionNameView = findViewById(R.id.section_name);
        fieldsView = findViewById(R.id.fields_linear_view);

        sectionIndex = 0;
        updateView();
    }

    protected void loadForms() {
        Intent intent = getIntent();
        String formName = intent.getStringExtra(Helper.FORM);
        String formContentName = intent.getStringExtra(Helper.FORM_CONTENT);
        form = Storage.getForm(formName, this);
        if (form == null) {
            finish();
        }

        if (formContentName != null && !formContentName.equals("")) {
            formContent = Storage.getFormContent(formContentName, this);
        } else {
            formContent = new FormContent(form.getId());
        }
    }

    private void updateView() {
        Section section = form.getFormTemplate().getSections()[sectionIndex];
        Field[] fields = section.getFields();
        toolbar.setTitle(form.getFormName());
        sectionNameView.setText(section.getSectionName());
        fieldsView.removeAllViews();
        for (int i = 0; i < fields.length; i++) {
            fieldsView.addView(createViewFromField(fields[i], i));
        }
    }

    private View createViewFromField(Field field, int i) {
        if (field.getType().equals(FieldType.String.toString()) || field.getType().equals(FieldType.Number.toString())) {
            return createTextField(field, i);
        } else if (field.getType().equals(FieldType.Choice.toString())) {
            return createDropDownField(field, i);
        } else {
            return null;
        }
    }

    private TextInputLayout createTextField(Field field, int i) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        TextInputLayout textInputLayout = new TextInputLayout(this);
        textInputLayout.setLayoutParams(layoutParams);

        EditText editText = new EditText(this);
        editText.setLayoutParams(layoutParams);
        editText.setHint(field.getFieldName());
        editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        String value = getFieldValue(field);
        if (value != null) {
            editText.setText(value);
        }
        editText.setId(i);

        textInputLayout.addView(editText);

        return textInputLayout;
    }

    private LinearLayout createDropDownField(Field field, int i) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(10, 0, 0, 0);

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        TextView textView = new TextView(this);
        String labelText = field.getFieldName() + ": ";
        textView.setText(labelText);
        textView.setTextSize(17);

        Spinner spinner = new Spinner(this);
        spinner.setLayoutParams(layoutParams);
        spinner.setId(i);

        try {
            ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
                    this, android.R.layout.simple_spinner_dropdown_item, field.getOptions());
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        linearLayout.addView(textView);
        linearLayout.addView(spinner);

        return linearLayout;
    }

    private String getFieldValue(Field field) {
        for (Map.Entry<String, String> entry : formContent.getFormContent().entrySet()) {
            if (entry.getKey().equals(field.getFieldName())) {
                return entry.getValue();
            }
        }
        return null;
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
        if (sectionIndex < form.getFormTemplate().getSections().length - 1) {
            sectionIndex++;
            updateView();
        } else if (sectionIndex >= form.getFormTemplate().getSections().length - 1) {
            storeFormContent();

            Intent cameraIntent = new Intent(getApplicationContext(), CameraActivity.class);
            cameraIntent.putExtra(Helper.FORM, form.getFormattedFormName());
            cameraIntent.putExtra(Helper.FORM_CONTENT, formContent.getFormContentName());
            startActivity(cameraIntent);

//            Intent overviewIntent = new Intent(getApplicationContext(), FormOverviewActivity.class);
//            overviewIntent.putExtra(Helper.FORM, form.getFormattedFormName());
//            overviewIntent.putExtra(Helper.FORM_CONTENT, formContent.getFormContentName());
//            startActivity(overviewIntent);
        }
    }

    private void storeFormContent() {
        formContent.setFormContentName(getFieldNames(), this);
        Storage.saveFormContent(formContent, this);
    }

    private void saveAnswers() {
        Field[] fields = form.getFormTemplate().getSections()[sectionIndex].getFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            String fieldName = field.getFieldName();
            String fieldValue = "";
            if (field.getType().equals(FieldType.String.toString()) || field.getType().equals(FieldType.Number.toString())) {
                EditText textField = findViewById(i);
                fieldValue = textField.getText().toString();
                textField.setText("");
            } else if (field.getType().equals(FieldType.Choice.toString())) {
                Spinner spinner = findViewById(i);
                fieldValue = getDropDownValueAt(i);
            } else {
                continue;
            }

            formContent.addAnswer(fieldName, fieldValue);
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

    public String[] getFieldNames() {
        String[] fieldNames = {getString(R.string.name),
                getString(R.string.district),
                getString(R.string.birthYear)};
        return fieldNames;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}