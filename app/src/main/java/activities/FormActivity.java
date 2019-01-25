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
import android.view.Menu;
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
import toning.juriaan.models.ProgressListener;
import toning.juriaan.models.R;
import toning.juriaan.models.Form;
import toning.juriaan.models.Section;
import toning.juriaan.models.Storage;


@SuppressLint("Registered")
public class FormActivity extends FormBaseActivity implements AdapterView.OnItemSelectedListener {

    private TextView sectionNameView;
    private LinearLayout fieldsView;
    private ArrayList<Pair> dropDownValues;
    private FormContent tempFormContent;

    private Form form;

    private int sectionIndex;
    private boolean isEditing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = findViewById(R.id.formbase_framelayout);
        getLayoutInflater().inflate(R.layout.activity_form, contentFrameLayout);
        getSupportActionBar().setTitle(getString(R.string.forms));

        Storage.cleanStorage(this);

        dropDownValues = new ArrayList<>();
        try {
            loadIntent();

            sectionNameView = findViewById(R.id.section_name);
            fieldsView = findViewById(R.id.fields_linear_view);

            updateView();
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    protected void loadIntent() {
        Intent intent = getIntent();
        String formName = intent.getStringExtra(Helper.FORM);
        String formContentId = intent.getStringExtra(Helper.FORM_CONTENT_ID);
        boolean goToCamera = intent.getBooleanExtra(Helper.GO_TO_CAMERA, false);
        sectionIndex = intent.getIntExtra(Helper.SECTION_INDEX, 0);
        isEditing = intent.getBooleanExtra(Helper.IS_EDITING, false);

        form = Storage.getForm(formName, this);
        if (form == null) {
            finish();
        }

        FormContent formContent = null;
        if (formContentId != null && !formContentId.isEmpty()) {
            formContent = Storage.getFormContentById(formContentId, this);
        }

        if (formContent == null) {
            formContent = new FormContent(form.getId());
        }

        tempFormContent = FormContent.createTemp(formContent);
        Storage.saveFormContent(tempFormContent, this);

        if (sectionIndex >= form.getFormTemplate().getSections().size()) {
            sectionIndex = 0;
        }

        if (goToCamera) {
            goToCameraActivity();
        }
    }

    private void updateView() {
        Section section = form.getFormTemplate().getSections().get(sectionIndex);
        ArrayList<Field> fields = section.getFields();
        getSupportActionBar().setTitle(tempFormContent.getFormContentName());
        updateViewTitle();
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
            getSupportActionBar().setTitle(tempFormContent.updateFormContentName(this));
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

        String value = tempFormContent.getAnswer(field.getFieldName());
        Helper.log("value: " + value);
        if (!value.isEmpty()) {
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
            String answer = tempFormContent.getAnswer(field.getFieldName());
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
            clearFields();
            if (!isEditing) {
                if (sectionIndex < form.getFormTemplate().getSections().size() - 1) {
                    sectionIndex++;
                    updateView();

                } else if (sectionIndex >= form.getFormTemplate().getSections().size() - 1) {
                    goToCameraActivity();
                }
            } else {
                finish();
            }
        } else {
            String errorMessage = "";
            for (String error : errors) {
                errorMessage += error + "\n";
            }
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.formErrorTitle))
                    .setMessage(errorMessage)
                    .setNegativeButton(getString(R.string.ok), null)
                    .create().show();
        }
    }

    private void goToCameraActivity() {
        tempFormContent.updateFormContentName(this);

        storeFormContent();

        Intent cameraIntent = new Intent(getApplicationContext(), CameraActivity.class);
        cameraIntent.putExtra(Helper.FORM, form.getFormattedFormName());
        cameraIntent.putExtra(Helper.FORM_CONTENT_ID, tempFormContent.getFormContentId());
        startActivityForResult(cameraIntent, Helper.FORM_ACTIVITY_CODE);
    }

    private ArrayList<String> validateSection() {
        ArrayList<String> errors = new ArrayList<>();
        Section section = form.getFormTemplate().getSections().get(sectionIndex);
        for (Field field : section.getFields()) {
            String answer = tempFormContent.getAnswer(field.getFieldName());
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
        Storage.saveFormContent(tempFormContent, this);
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
            } else if (field.getType().equals(FieldType.Choice.toString())) {
                Spinner spinner = findViewById(i);
                fieldValue = getDropDownValueAt(i);
            } else {
                continue;
            }

            tempFormContent.addAnswer(fieldName, fieldValue);
        }
    }

    private void clearFields() {
        ArrayList<Field> fields = form.getFormTemplate().getSections().get(sectionIndex).getFields();
        for (int i = 0; i < fields.size(); i++) {
            try {
                EditText textField = findViewById(i);
                textField.setText("");
            } catch (Exception e) {
            }
        }
    }

    private void previousSection() {
        saveAnswers();
        if (sectionIndex > 0 && !isEditing) {
            sectionIndex--;
            updateView();
        } else {
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
                setResult(Helper.CONTENT_SAVED_CODE);
                finish();
            } else if (resultCode == Helper.UPDATE_CODE) {
                updateView();
            } else if (resultCode == Helper.EDIT_SECTION_CODE && data != null) {
                sectionIndex = data.getIntExtra(Helper.SECTION_INDEX, 0);
                Helper.log("Form " + sectionIndex);
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
        String formContentId = tempFormContent.getFormContentId();
        FormContent formContent = Storage.getFormContentById(formContentId, this);
        if (formContent != null) {
            this.tempFormContent = formContent;
        }
        updateView();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.form_activity_menu, menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onPause() {
        saveAnswers();
        super.onPause();
    }

    @Override
    protected void onStop() {
        saveAnswers();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        saveAnswers();
        super.onDestroy();
    }
}
