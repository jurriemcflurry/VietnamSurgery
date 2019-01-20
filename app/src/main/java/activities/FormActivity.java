package activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import toning.juriaan.models.Field;
import toning.juriaan.models.FieldType;
import toning.juriaan.models.FormContent;
import toning.juriaan.models.Helper;
import toning.juriaan.models.R;
import toning.juriaan.models.Form;
import toning.juriaan.models.Section;
import toning.juriaan.models.Storage;


@SuppressLint("Registered")
public class FormActivity extends FormBaseActivity implements AdapterView.OnItemSelectedListener {

    private boolean isNew = true;
    private Toolbar toolbar;
    private TextView sectionNameView;
    private LinearLayout fieldsView;
    private ArrayList<Pair> dropDownValues;
    private FormContent formContent;

    private int sectionIndex;
    private Form form;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = findViewById(R.id.formbase_framelayout);
        getLayoutInflater().inflate(R.layout.activity_form, contentFrameLayout);
        getSupportActionBar().setTitle(getString(R.string.forms));

        dropDownValues = new ArrayList<>();
        loadForm();

        sectionNameView = findViewById(R.id.section_name);
        fieldsView = findViewById(R.id.fields_linear_view);

        updateView();
    }

    protected void loadForm() {
        Intent intent = getIntent();
        String formName = intent.getStringExtra(Helper.FORM);
        String formContentName = intent.getStringExtra(Helper.FORM_CONTENT);
        sectionIndex = intent.getIntExtra(Helper.SECTION_INDEX, 0);

        form = Storage.getForm(formName, this);
        if (form == null) {
            finish();
        }

        if (formContentName != null && !formContentName.isEmpty()) {
            Helper.log("loadForm() loading formContent");
            formContent = Storage.getFormContent(formContentName, this);
        }

        if (formContent == null) {
            Helper.log("loadForm() creating formContent");
            formContent = new FormContent(form.getId());
        }

        if (sectionIndex >= form.getFormTemplate().getSections().size()) {
            sectionIndex = 0;
        }
    }

    private void updateView() {
<<<<<<< HEAD
        Section section = form.getFormTemplate().getSections().get(sectionIndex);
        ArrayList<Field> fields = section.getFields();
        formContent.setFormContentName(getFieldNames(), this);
        getSupportActionBar().setTitle(formContent.getFormContentName());
=======
        Section section = form.getFormTemplate().getSections()[sectionIndex];
        Field[] fields = section.getFields();
        updateViewTitle();
>>>>>>> master
        sectionNameView.setText(section.getSectionName());
        fieldsView.removeAllViews();
        for (int i = 0; i < fields.size(); i++) {
            View view = createViewFromField(fields.get(i), i);
            fieldsView.addView(view);
            if (i == 0 && view != null) {
                view.setFocusable(true);
            }
        }
    }

    private void updateViewTitle() {
        try {
            getSupportActionBar().setTitle(formContent.updateFormContentName(this));
        } catch (Exception e) {
            e.printStackTrace();
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
        if (field.getType().equals(FieldType.String.toString())) {
            editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        } else if (field.getType().equals(FieldType.Number.toString())) {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }

        String value = formContent.getAnswer(field.getFieldName());
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
            String answer = formContent.getAnswer(field.getFieldName());
            if (!answer.isEmpty()) {
                int index = -1;
                for (int j = 0; j < field.getOptions().length; j++) {
                    if (field.getOptions()[j].toLowerCase().equals(answer.toLowerCase())) {
                        index = j;
                        break;
                    }
                }
                if (index >= 0) {
                    spinner.setSelection(index);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        linearLayout.addView(textView);
        linearLayout.addView(spinner);

        return linearLayout;
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

    @Override
    public boolean onSupportNavigateUp() {
        previousSection();
        return true;
    }

    private void nextSection() {
        saveAnswers();
        ArrayList<String> errors = validateSection();
        if (errors.size() == 0) {
            if (sectionIndex < form.getFormTemplate().getSections().size() - 1) {
                sectionIndex++;
                updateView();
<<<<<<< HEAD
            } else if (sectionIndex >= form.getFormTemplate().getSections().size() - 1) {
=======
            } else if (sectionIndex >= form.getFormTemplate().getSections().length - 1) {
                formContent.updateFormContentName(this);
>>>>>>> master
                storeFormContent();

                Intent cameraIntent = new Intent(getApplicationContext(), CameraActivity.class);
                cameraIntent.putExtra(Helper.FORM, form.getFormattedFormName());
                cameraIntent.putExtra(Helper.FORM_CONTENT, formContent.getFormContentId());
                startActivityForResult(cameraIntent, Helper.FORM_ACTIVITY_CODE);
            }
        } else {
            String errorMessage = "";
            for (String error : errors) {
                errorMessage += error + "\n";
            }
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.formErrorTitle))
                    .setMessage(errorMessage)
                    .setNegativeButton("Ok", null)
                    .create().show();
        }
    }

    private ArrayList<String> validateSection() {
        ArrayList<String> errors = new ArrayList<>();
        Section section = form.getFormTemplate().getSections().get(sectionIndex);
        for (Field field : section.getFields()) {
            String answer = formContent.getAnswer(field.getFieldName());
            if (field.isRequired()) {
                if (answer.isEmpty()) {
                    errors.add(field.getFieldName() + " " + getString(R.string.isRequired));
                    continue;
                }
            }

            if (field.getType().equals(FieldType.Number.toString()) && !answer.isEmpty()) {
                try {
                    Double.valueOf(answer);
                } catch (NumberFormatException e) {
                    errors.add(field.getFieldName() + " " + getString(R.string.isNotANumber));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return errors;
    }

    private void storeFormContent() {
        if (isNew) {
            isNew = false;
        }

        Storage.saveFormContent(formContent, this);
    }

    private void saveAnswers() {
        ArrayList<Field> fields = form.getFormTemplate().getSections().get(sectionIndex).getFields();
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
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

            Helper.log("saveAnswers() " + formContent);
            formContent.addAnswer(fieldName, fieldValue);
        }
    }

    private void previousSection() {
        saveAnswers();
        if (sectionIndex > 0) {
            sectionIndex--;
            updateView();
        } else {
            String formContentName = formContent.getFormContentId();
            if (formContentName != null) {
                Storage.deleteFormContent(formContent, this);
            }
            super.onBackPressed();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Helper.FORM_ACTIVITY_CODE) {
            if (resultCode == Helper.CONTENT_SAVED_CODE) {
                finish();
            } else if (resultCode == Helper.UPDATE_CODE) {
                updateView();
            }
        }
    }

    @Override
    public void onBackPressed() {
        previousSection();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String formContentName = formContent.getFormContentId();
        if (formContentName != null && !isNew) {
            Helper.log("names: " + formContentName + " formContent.getname() " + formContent.getFormContentId());
            formContent = Storage.getFormContent(formContentName, this);
            Helper.log("ooooooooooh " + formContent);
        }
        updateView();
    }
}
