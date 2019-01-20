package activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import toning.juriaan.models.Helper;
import toning.juriaan.models.R;
import toning.juriaan.models.Section;

public class AddSectionActivity extends FormBaseActivity {

    private TextInputEditText sectionNameEditText;
    private Button addSectionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = findViewById(R.id.formbase_framelayout);
        getLayoutInflater().inflate(R.layout.activity_add_section, contentFrameLayout);
        getSupportActionBar().setTitle(getString(R.string.addSectionTitle));

        Intent fromCreateForm = getIntent();

        sectionNameEditText = findViewById(R.id.sectionNameEditText);
        addSectionButton = findViewById(R.id.addSectionButton);
        addSectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSection();
            }
        });
    }

    private void addSection(){
        Helper.log(sectionNameEditText.getText().toString());
        if(sectionNameEditText.getText().toString().isEmpty()){
            return;
        }
        else{
            String sectionName = sectionNameEditText.getText().toString();
            getIntent().putExtra(Helper.SECTION_ADDED, sectionName);
            setResult(Helper.SECTION_ADDED_RESULT_CODE, getIntent());
            finish();
        }
    }
}
