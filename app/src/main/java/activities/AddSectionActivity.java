package activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
    private FrameLayout addSectionFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = findViewById(R.id.formbase_framelayout);
        getLayoutInflater().inflate(R.layout.activity_add_section, contentFrameLayout);
        getSupportActionBar().setTitle(getString(R.string.addSectionTitle));

        Intent fromCreateForm = getIntent();

        setupLayout();

    }

    private void setupLayout(){
        addSectionFrameLayout = findViewById(R.id.addSectionFrameLayout);
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
        if(sectionNameEditText.getText().toString().isEmpty()){
            Snackbar.make(addSectionFrameLayout, getString(R.string.noSectionName), Snackbar.LENGTH_LONG)
                    .show();
            return;
        }
        else{
            String sectionName = sectionNameEditText.getText().toString();
            getIntent().putExtra(Helper.SECTION_ADDED, sectionName);
            setResult(Helper.SECTION_ADDED_RESULT_CODE, getIntent());
            Helper.hideKeyboard(this);
            finish();
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
                        Helper.hideKeyboard(AddSectionActivity.this);
                        AddSectionActivity.this.finish();
                    }
                })
                .create().show();
    }
}
