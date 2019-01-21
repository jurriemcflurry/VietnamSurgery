package activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import toning.juriaan.models.FieldType;
import toning.juriaan.models.Helper;
import toning.juriaan.models.R;

public class AddQuestionActivity extends FormBaseActivity implements AdapterView.OnItemSelectedListener {

    private boolean required = false;
    private RelativeLayout addQuestionRelativeLayout;
    private TextInputEditText questionName;
    private CheckBox requiredCheckbox;
    private String sectionName;
    private Spinner questionTypeSpinner;
    private LinearLayout optionsLayout;
    private boolean firstTimeChoice = true;
    private ArrayList<EditText> optionsEditTexts;
    private String[] options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent fromSection = getIntent();
        sectionName = fromSection.getStringExtra(Helper.QUESTION_SECTION);

        FrameLayout contentFrameLayout = findViewById(R.id.formbase_framelayout);
        getLayoutInflater().inflate(R.layout.activity_add_question, contentFrameLayout);
        getSupportActionBar().setTitle(getString(R.string.addQuestionTitle));

        addQuestionRelativeLayout = findViewById(R.id.addQuestionRelativeLayout);
        optionsLayout = findViewById(R.id.optionsLayout);
        questionTypeSpinner = findViewById(R.id.questionTypeSpinner);
        String[] items = new String[FieldType.values().length];
        int i = 0;
        for(FieldType type : FieldType.values()){
            items[i] = type.toString();
            i++;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        questionTypeSpinner.setAdapter(adapter);
        questionTypeSpinner.setOnItemSelectedListener(this);

        requiredCheckbox = findViewById(R.id.requiredCheckbox);
        questionName = findViewById(R.id.questionNameEditText);
        final Button addQuestion = findViewById(R.id.addQuestionButton);
        addQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addQuestion();
            }
        });
    }

    private void addQuestion(){
        if(questionName.getText().toString().isEmpty()){
            Snackbar.make(addQuestionRelativeLayout, getString(R.string.questionNameEmpty), Snackbar.LENGTH_LONG)
                    .show();
            return;
        }

        if(questionTypeSpinner.getSelectedItem().toString().equals(FieldType.Choice.toString())){
            if(optionsEditTexts.size() < 2){
                Snackbar.make(addQuestionRelativeLayout, getString(R.string.notEnoughOptions), Snackbar.LENGTH_LONG)
                        .show();
                return;
            }

            options = new String[optionsEditTexts.size()];
            int i = 0;
            for(EditText e : optionsEditTexts){
                if(e.getText().toString().isEmpty()){
                    Snackbar.make(addQuestionRelativeLayout, getString(R.string.notEnoughOptions), Snackbar.LENGTH_LONG)
                            .show();
                    return;
                }
                options[i] = e.getText().toString();
                i++;
            }
            getIntent().putExtra(Helper.OPTIONS, options);
        }

        if(requiredCheckbox.isChecked()){
            required = true;
        }

        getIntent().putExtra(Helper.QUESTION_NAME, questionName.getText().toString());
        getIntent().putExtra(Helper.REQUIRED, required);
        getIntent().putExtra(Helper.QUESTION_TYPE_STRING, questionTypeSpinner.getSelectedItem().toString());
        getIntent().putExtra(Helper.QUESTION_SECTION, sectionName);
        setResult(Helper.ADD_QUESTION_RESULT_CODE, getIntent());
        Helper.hideKeyboard(this);
        finish();
    }

    private void makeOption(){
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        LinearLayout.LayoutParams addOptionParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        TextInputLayout textInputLayout = new TextInputLayout(this);
        textInputLayout.setLayoutParams(layoutParams);

        EditText editText = new EditText(this);
        editText.setLayoutParams(layoutParams);
        editText.setHint(getString(R.string.option));
        optionsEditTexts.add(editText);

        textInputLayout.addView(editText);

        final LinearLayout addOption = new LinearLayout(this);
        addOption.setLayoutParams(layoutParams);
        addOption.setOrientation(LinearLayout.HORIZONTAL);

        final TextView addExtraOption = new TextView(this);
        addExtraOption.setLayoutParams(addOptionParams);
        addExtraOption.setText(getString(R.string.addOptions));
        addExtraOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeOption();
                addOption.removeView(addExtraOption);
            }
        });

        addOption.addView(addExtraOption);

        optionsLayout.addView(textInputLayout);
        optionsLayout.addView(addOption);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if(parent.getItemAtPosition(position).equals(FieldType.Choice.toString())){
            optionsLayout.setVisibility(View.VISIBLE);

            if(firstTimeChoice){
                firstTimeChoice = false;
                optionsEditTexts = new ArrayList<>();
                makeOption();
            }
        }
        else{
            optionsLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
                        Helper.hideKeyboard(AddQuestionActivity.this);
                        AddQuestionActivity.this.finish();
                    }
                })
                .create().show();
    }
}
