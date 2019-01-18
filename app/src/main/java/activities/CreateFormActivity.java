package activities;

import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import toning.juriaan.models.Field;
import toning.juriaan.models.FieldType;
import toning.juriaan.models.R;
import toning.juriaan.models.Section;

public class CreateFormActivity extends FormBaseActivity implements AdapterView.OnItemSelectedListener {

    private LinearLayout formView;
    private TextView sectionNameView;
    private LinearLayout fieldsView;
    private ArrayList<Pair> dropDownValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = findViewById(R.id.formbase_framelayout);
        getLayoutInflater().inflate(R.layout.activity_create_form, contentFrameLayout);
        getSupportActionBar().setTitle(getString(R.string.createFormTitle));

        formView = findViewById(R.id.create_form_section_view);
        sectionNameView = findViewById(R.id.create_form_section_name);
        fieldsView = findViewById(R.id.create_form_fields_linear_view);

        loadStandardItems();
    }

    private void loadStandardItems(){
        Field[] personalInfoFields = makePersonalInfoFields();
        Section personalInfoSection = new Section(getString(R.string.personalInfo), personalInfoFields);
        sectionNameView.setText(personalInfoSection.getSectionName());
        for(int i = 0; i < personalInfoFields.length; i++){
            fieldsView.addView(createViewFromField(personalInfoFields[i], i));
        }

        Field[] contactInfoFields = makeContactInfoFields();
        Section contactInfoSection = new Section(getString(R.string.contactInfo), contactInfoFields);
        TextView contactInfoSectionView = makeSectionView(contactInfoSection);
        LinearLayout fieldsView2 = makeFieldsView();
        for(int i = 0; i < contactInfoFields.length; i++){
            fieldsView2.addView(createViewFromField(contactInfoFields[i], i));
        }
        formView.addView(contactInfoSectionView);
        formView.addView(fieldsView2);
    }

    private TextView makeSectionView(Section section){
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

    private Field[] makePersonalInfoFields(){
        Field[] personalInfoFields = new Field[2];
        personalInfoFields[0] = new Field(getString(R.string.namePersonalInfo), FieldType.String.toString());
        personalInfoFields[1] = new Field(getString(R.string.birthYear), FieldType.String.toString());

        return personalInfoFields;
    }

    private Field[] makeContactInfoFields(){
        Field[] contactInfoFields = new Field[1];
        contactInfoFields[0] = new Field(getString(R.string.districtContactInfo), FieldType.String.toString());
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
//        for (Map.Entry<String, String> entry : formContent.getFormContent().entrySet()) {
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
}
