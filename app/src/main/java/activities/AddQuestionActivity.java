package activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;

import toning.juriaan.models.FieldType;
import toning.juriaan.models.Helper;
import toning.juriaan.models.R;

public class AddQuestionActivity extends FormBaseActivity {

    private boolean required = false;
    private TextInputEditText questionName;
    private CheckBox requiredCheckbox;
    private String sectionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent fromSection = getIntent();
        sectionName = fromSection.getStringExtra("section");

        FrameLayout contentFrameLayout = findViewById(R.id.formbase_framelayout);
        getLayoutInflater().inflate(R.layout.activity_add_question, contentFrameLayout);
        getSupportActionBar().setTitle(getString(R.string.addQuestionTitle));

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
            return;
        }

        if(requiredCheckbox.isChecked()){
            required = true;
        }

        getIntent().putExtra(Helper.QUESTION_NAME, questionName.getText().toString());
        getIntent().putExtra(Helper.REQUIRED, required);
        getIntent().putExtra(Helper.QUESTION_TYPE_STRING, FieldType.String.toString());
        getIntent().putExtra(Helper.QUESTION_SECTION, sectionName);
        setResult(Helper.ADD_QUESTION_RESULT_CODE, getIntent());
        finish();
    }
}
