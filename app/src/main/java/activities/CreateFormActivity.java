package activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.util.Pair;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

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
    private FrameLayout createFormFrameLayout;
    private ArrayList<Pair> dropDownValues;
    private TextInputEditText formNameEditText;
    private FloatingActionButton addSectionFAB;
    private String[] options;
    private ProgressBar createFormSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = findViewById(R.id.formbase_framelayout);
        getLayoutInflater().inflate(R.layout.activity_create_form, contentFrameLayout);
        getSupportActionBar().setTitle(getString(R.string.createFormTitle));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        createFormFrameLayout = findViewById(R.id.create_form_frame_layout);
        createFormSpinner = findViewById(R.id.createFormSpinner);
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

        dropDownValues = new ArrayList<>();

        formTemplate = new FormTemplate();
        loadStandardItems();
        updateView();
        hideSpinner();
    }

    private void updateView(){
        showSpinner();
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

            sectionView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(section.getSectionName().equals(getString(R.string.personalInfo)) || section.getSectionName().equals(getString(R.string.contactInfo))){
                        return false;
                    }
                    else{
                        deleteSection(section);
                    }
                    return false;
                }
            });
            formView.addView(sectionView);
            formView.addView(fieldsView);
        }

        hideSpinner();
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
        layoutParams.topMargin = 50;

        TextView sectionHeader = new TextView(this);
        sectionHeader.setLayoutParams(layoutParams);
        sectionHeader.setTextSize(25);
        sectionHeader.setTextColor(getResources().getColor(R.color.black));
        sectionHeader.setText(section.getSectionName());

        return sectionHeader;
    }

    private LinearLayout makeFieldsView(){
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = 20;

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

    private RelativeLayout createTextField(final Field field, int i) {
        RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        RelativeLayout relativeLayout = new RelativeLayout(this);
        relativeLayout.setLayoutParams(relativeLayoutParams);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        LinearLayout.LayoutParams textViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.RIGHT);

        TextView fieldName = new TextView(this);
        fieldName.setLayoutParams(textViewLayoutParams);
        String labelText = field.getFieldName();
        fieldName.setText(labelText);
        fieldName.setTextSize(20);

        TextView fieldType = new TextView(this);
        fieldType.setLayoutParams(textViewLayoutParams);
        if(field.isRequired()){
            String required = getString(R.string.requiredOverview);
            fieldType.setText(required + setLabelTextFieldType(field.getType()));
        }
        else{
            fieldType.setText(setLabelTextFieldType(field.getType()));
        }

        fieldType.setTextSize(20);

        String value = getFieldValue(field);
        if (value != null) {
            fieldType.setText(value);
        }
        fieldType.setId(i);


        linearLayout.addView(fieldType);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteField(field);
            }
        });
        relativeLayout.addView(fieldName);
        relativeLayout.addView(linearLayout);

        return relativeLayout;
    }

    private RelativeLayout createDropDownField(final Field field, int i) {
        RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        RelativeLayout relativeLayout = new RelativeLayout(this);
        relativeLayout.setLayoutParams(relativeLayoutParams);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.RIGHT);

        TextView fieldName = new TextView(this);
        String labelText = field.getFieldName();
        fieldName.setText(labelText);
        fieldName.setTextSize(20);

        TextView fieldType = new TextView(this);
        if(field.isRequired()){
            String required = getString(R.string.requiredOverview);
            fieldType.setText(required + setLabelTextFieldType(field.getType()));
        }
        else{
            fieldType.setText(setLabelTextFieldType(field.getType()));
        }
        fieldType.setTextSize(20);

        linearLayout.addView(fieldType);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteField(field);
            }
        });
        relativeLayout.addView(fieldName);
        relativeLayout.addView(linearLayout);

        return relativeLayout;
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

    private String setLabelTextFieldType(String inputText){
        if(inputText.equals(FieldType.Choice.toString())){
            return getString(R.string.choiceToDropDown);
        }
        else if(inputText.equals(FieldType.String.toString())){
            return getString(R.string.stringToText);
        }

        return FieldType.Number.toString();
    }

    private void deleteField(Field field){
        String fieldname = field.getFieldName();

        if(fieldname.equals(getString(R.string.namePersonalInfo)) || fieldname.equals(getString(R.string.birthYear)) || fieldname.equals(getString(R.string.districtContactInfo))){
            return;
        }

        for(final Section section : formTemplate.getSections()){
            ArrayList<Field> fields = section.getFields();
            int i = 0;
            for(final Field f : fields){
                if(f.getFieldName().equals(fieldname)){
                    new AlertDialog.Builder(this)
                            .setTitle(getString(R.string.deleteQuestionTitle))
                            .setMessage(getString(R.string.deleteQuestionMessage) + fieldname)
                            .setPositiveButton(getString(R.string.deleteQuestion), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    section.removeFields(f);
                                    updateView();
                                }
                            })
                            .setNegativeButton(getString(R.string.cancelDeleteQuestion), null)
                            .create().show();

                    return;
                }
                i++;
            }
        }
    }

    private void deleteSection(final Section section){

        new AlertDialog.Builder(CreateFormActivity.this)
                .setTitle(getString(R.string.deleteSubjectTitle))
                .setMessage(getString(R.string.deleteSubjectMessage) + section.getSectionName())
                .setNegativeButton(getString(R.string.cancelDeleteQuestion), null)
                .setPositiveButton(getString(R.string.deleteQuestion), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        formTemplate.removeSection(section);
                        updateView();
                    }
                })
                .create().show();
    }

    private void showSpinner(){
        createFormSpinner.setVisibility(View.VISIBLE);
    }

    private void hideSpinner(){
        createFormSpinner.setVisibility(View.GONE);
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
        Helper.hideKeyboard(this);
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
                if(type.equals(FieldType.Choice.toString())){
                    options = extras.getStringArray(Helper.OPTIONS);
                }
                String sectionName = extras.getString(Helper.QUESTION_SECTION);

                for(Section section : formTemplate.getSections()){
                    if(section.getSectionName().equals(sectionName)){
                        Field newQuestion = new Field(questionName, type, required);
                        if(newQuestion.getType().equals(FieldType.Choice.toString())){
                            try{
                                newQuestion.setOptions(options);
                            }catch(Exception e){
                                Snackbar.make(createFormFrameLayout, e.getMessage(), Snackbar.LENGTH_LONG)
                                        .show();
                            }
                        }
                        section.addFields(newQuestion);
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

    private void postForm(final String formName, final FormTemplate formTemplate) {
        if(formNameEditText.getText().toString().isEmpty()){
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.formNameEmpty))
                    .setMessage(getString(R.string.formNameEmptyMessage))
                    .setNegativeButton(getString(R.string.back), null)
                    .create().show();
        }else{
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.saveForm))
                    .setMessage(getString(R.string.saveFormMessage))
                    .setNegativeButton(getString(R.string.back), null)
                    .setPositiveButton(getString(R.string.saveForm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
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
                    })
                    .create().show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.exitCreateForm))
                .setMessage(getString(R.string.exitCreateFormMessage))
                .setNegativeButton(getString(R.string.back), null)
                .setPositiveButton(getString(R.string.exitCreateFormConfirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CreateFormActivity.this.finish();
                    }
                })
                .create().show();
    }
}
