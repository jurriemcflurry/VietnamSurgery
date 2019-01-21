package activities;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import toning.juriaan.models.Field;
import toning.juriaan.models.FieldType;
import toning.juriaan.models.Form;
import toning.juriaan.models.FormTemplate;
import toning.juriaan.models.FormTemplateObject;
import toning.juriaan.models.Helper;
import toning.juriaan.models.R;
import toning.juriaan.models.Section;
import webinterfaces.FormWebInterface;

public class CreateFormActivity extends FormBaseActivity implements AdapterView.OnItemSelectedListener {

    private LinearLayout formView;
    private FormTemplate formTemplate;
    private ArrayList<Pair> dropDownValues;
    private TextInputEditText formNameEditText;
    private FloatingActionButton addSectionFAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = findViewById(R.id.formbase_framelayout);
        getLayoutInflater().inflate(R.layout.activity_create_form, contentFrameLayout);
        getSupportActionBar().setTitle(getString(R.string.createFormTitle));

        formView = findViewById(R.id.create_form_section_view);
        formNameEditText = findViewById(R.id.form_name_edittext);
        addSectionFAB = findViewById(R.id.addSectionFAB);
        addSectionFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addSection = new Intent(getApplicationContext(), AddSectionActivity.class);
                startActivityForResult(addSection, Helper.ADD_SECTION_CODE);
            }
        });

        formTemplate = new FormTemplate();
        loadStandardItems();
        updateView();
    }

    private void updateView(){
        formView.removeAllViews();

        for(final Section section : formTemplate.getSections()){
            TextView sectionView = makeSectionView(section);
            ArrayList<Field> fields = section.getFields();
            LinearLayout fieldsView = makeFieldsView();
            int i = 0;
            for(Field field : fields){
                fieldsView.addView(createViewFromField(field, i));
                i++;
            }

            sectionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent addQuestion = new Intent(getApplicationContext(), AddQuestionActivity.class);
                    addQuestion.putExtra(Helper.QUESTION_SECTION, section.getSectionName());
                    startActivityForResult(addQuestion, Helper.ADD_QUESTION_CODE);
                }
            });
            formView.addView(sectionView);
            formView.addView(fieldsView);
        }
    }

    private void loadStandardItems(){
        ArrayList<Field> personalInfoFields = makePersonalInfoFields();
        Section personalInfoSection = new Section(getString(R.string.personalInfo), personalInfoFields);

        ArrayList<Field> contactInfoFields = makeContactInfoFields();
        Section contactInfoSection = new Section(getString(R.string.contactInfo), contactInfoFields);

        formTemplate.getSections().add(personalInfoSection);
        formTemplate.getSections().add(contactInfoSection);
    }

    private TextView makeSectionView(final Section section){
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        TextView sectionHeader = new TextView(this);
        sectionHeader.setLayoutParams(layoutParams);
        sectionHeader.setTextSize(20);
        sectionHeader.setTextColor(getResources().getColor(R.color.black));
        sectionHeader.setText(section.getSectionName());

        return sectionHeader;
    }

    private LinearLayout makeFieldsView(){
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        LinearLayout fields = new LinearLayout(this);
        fields.setLayoutParams(layoutParams);
        fields.setOrientation(LinearLayout.VERTICAL);

        return fields;
    }

    private ArrayList<Field> makePersonalInfoFields(){
        ArrayList<Field> personalInfoFields = new ArrayList<>();
        personalInfoFields.add(new Field(getString(R.string.namePersonalInfo), FieldType.String.toString(), true));
        personalInfoFields.add(new Field(getString(R.string.birthYear), FieldType.String.toString(), true));

        return personalInfoFields;
    }

    private ArrayList<Field> makeContactInfoFields(){
        ArrayList<Field> contactInfoFields = new ArrayList<>();
        contactInfoFields.add(new Field(getString(R.string.districtContactInfo), FieldType.String.toString(), true));
        return contactInfoFields;
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

    private LinearLayout createTextField(Field field, int i) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        LinearLayout.LayoutParams textViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textViewLayoutParams.setMargins(0, 20, 20, 0);

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        TextView fieldName = new TextView(this);
        fieldName.setLayoutParams(textViewLayoutParams);
        fieldName.setText(field.getFieldName());

        TextView fieldType = new TextView(this);
        fieldType.setLayoutParams(textViewLayoutParams);
        fieldType.setText(field.getType());

        String value = getFieldValue(field);
        if (value != null) {
            fieldType.setText(value);
        }
        fieldType.setId(i);

        linearLayout.addView(fieldName);
        linearLayout.addView(fieldType);

        return linearLayout;
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        linearLayout.addView(textView);
        linearLayout.addView(spinner);

        return linearLayout;
    }

    private String getFieldValue(Field field) {
//        for (Map.Entry<String, String> entry : formContent.getFormContentById().entrySet()) {
//            if (entry.getKey().equals(field.getFieldName())) {
//                return entry.getValue();
//            }
//        }
        return null;
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int fieldId = parent.getId();
        String fieldValue = (String) parent.getItemAtPosition(position);
        addDropDownPair(fieldId, fieldValue);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Helper.ADD_SECTION_CODE){
            if(resultCode == Helper.SECTION_ADDED_RESULT_CODE){
               Bundle extras = data.getExtras();
               Section newSection = new Section(extras.getString(Helper.SECTION_ADDED));
               formTemplate.addSection(newSection);
               updateView();
            }
        }

        if(requestCode == Helper.ADD_QUESTION_CODE){
            if(resultCode == Helper.ADD_QUESTION_RESULT_CODE){
                Bundle extras = data.getExtras();
                String questionName = extras.getString(Helper.QUESTION_NAME);
                Boolean required = extras.getBoolean(Helper.REQUIRED);
                String type = extras.getString(Helper.QUESTION_TYPE_STRING);
                String sectionName = extras.getString(Helper.QUESTION_SECTION);

                Helper.log(questionName);
                Helper.log(required.toString());
                Helper.log(type);
                Helper.log(sectionName);

                for(Section section : formTemplate.getSections()){
                    if(section.getSectionName().equals(sectionName)){
                        section.addFields(new Field(questionName, type, required));
                    }
                }

                updateView();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.next_menu_item:
                String formName = formNameEditText.getText().toString();
                postForm(formName, formTemplate);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void postForm(String formName, FormTemplate formTemplate) {
        if(formNameEditText.getText().toString().isEmpty()){
           return;
        }


        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.baseURL))
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();
        FormWebInterface client = retrofit.create(FormWebInterface.class);
        Call<Void> call = client.postFormTemplate(new FormTemplateObject(formName, formTemplate));

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Intent backHome =  new Intent(getApplicationContext(), MainActivity.class);
                backHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(backHome);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
            }
        });
    }
}
