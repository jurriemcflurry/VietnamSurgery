package toning.juriaan.vietnamsurgery.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import toning.juriaan.vietnamsurgery.MainActivity;
import toning.juriaan.vietnamsurgery.model.Field;
import toning.juriaan.vietnamsurgery.model.FormTemplate;
import toning.juriaan.vietnamsurgery.R;
import toning.juriaan.vietnamsurgery.model.Section;

public class FormActivity extends AppCompatActivity {

    Toolbar toolbar;
    private TextView sectionNameTv;
    private TextView stepCounter;
    private int noOfSections;
    private int noOfThisSection = 1;
    private Map<String, Integer> idsMap;
    private LinearLayout layout;
    private FormTemplate form;
    private int tempNoOfSec;
    private ActionBar ab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        loadIntent();
        setupFields();
        setupToolbar();

        if(tempNoOfSec == 0) {
            generateForm(form.getSections().get(0), layout);
        } else {
            noOfThisSection = tempNoOfSec;
            generateForm(form.getSections().get(tempNoOfSec-1), layout);
        }

        View view = findViewById(R.id.form_frame_layout);
        setupUI(view);

    }

    private void loadIntent(){
        Intent i = getIntent();
        form = i.getParcelableExtra("obj_form");
        tempNoOfSec = i.getIntExtra("step", 0);
    }

    private void setupFields(){
        sectionNameTv = findViewById(R.id.section_name);
        noOfSections = form.getSections().size();
        stepCounter = findViewById(R.id.step_counter);
        layout = findViewById(R.id.formLayout);
        toolbar = findViewById(R.id.form_toolbar);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(getString(R.string.new_form_name, form.getFormName()));
    }

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideKeyboard(FormActivity.this);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    private void emptyForm(FormTemplate form, LinearLayout layout) {
        for(Field f : form.getSections().get(noOfThisSection-1).getFields()) {
            Iterator i = idsMap.entrySet().iterator();
            while(i.hasNext()) {
                Map.Entry pair = (Map.Entry)i.next();
                if(pair.getKey().toString().equals(f.getFieldName())) {
                    try {
                        String answer = ((EditText) findViewById((int)pair.getValue())).getText().toString();
                        f.setAnswer(answer);
                    } catch (Exception ex) {
                        String answer = Boolean.toString(((RadioButton) findViewById((int)pair.getValue())).isChecked());
                        f.setAnswer(answer);
                    }
                }
            }
        }
        layout.removeAllViews();
    }

    private void generateForm(Section section, LinearLayout layout) {
        sectionNameTv.setText(form.getSections().get(noOfThisSection-1).getSectionName());
        stepCounter.setText(getString(R.string.step_text, noOfThisSection, noOfSections + 1));
        if(noOfThisSection > 1) {
            String name = form.getSections().get(0).getFields().get(1).getAnswer();
            String birthYear = form.getSections().get(0).getFields().get(2).getAnswer();
            ab.setTitle(getString(R.string.form_name, form.getFormName(), name, birthYear));
        }
        idsMap = new HashMap<>();
        if(noOfThisSection == 1) {
            RadioGroup rg = new RadioGroup(this);
            rg.setOrientation(RadioGroup.HORIZONTAL);

            for(int i = 0; i < section.getFields().size(); i++) {
                Field f = section.getFields().get(i);
                if(i < section.getFields().size() - 2) {
                    TextInputLayout editField = createTextField(f.getFieldName(), f.getColumn(), f.getAnswer());
                    idsMap.put(f.getFieldName(), f.getColumn());
                    layout.addView(editField);
                } else {
                    RadioButton rb = createRadioButton(f.getFieldName(), f.getColumn(), f.getAnswer());
                    rg.addView(rb);
                }
            }
            layout.addView(rg);
        } else {
            for(Field f : section.getFields()) {
                TextInputLayout editField = createTextField(f.getFieldName(), f.getColumn(), f.getAnswer());
                idsMap.put(f.getFieldName(), f.getColumn());
                layout.addView(editField);
            }
        }
    }

    private TextInputLayout createTextField(String textFieldName, int column, String answer) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        TextInputLayout textInputLayout = new TextInputLayout(this);
        textInputLayout.setLayoutParams(layoutParams);

        EditText editText = new EditText(textInputLayout.getContext());
        editText.setLayoutParams(layoutParams);
        editText.setHint(textFieldName);
        editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES );
        if(answer != null) {
            editText.setText(answer);
        }
        editText.setId(column);

        textInputLayout.addView(editText);

        return textInputLayout;
    }

    private RadioButton createRadioButton(String text, int id, String answer) {
        RadioButton rb = new RadioButton(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,50,50,0);
        rb.setLayoutParams(params);
        id = id+10;
        rb.setId(id);
        idsMap.put(text, id);
        rb.setText(text);
        if(answer != null && Boolean.parseBoolean(answer)) {
            rb.setChecked(true);
        }
        return rb;
    }

    private void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if(inputMethodManager != null && activity.getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(), 0);
        }
        getWindow().getDecorView().clearFocus();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_next:
                hideKeyboard(this);
                emptyForm(form, layout);
                noOfThisSection++;
                if(noOfThisSection > noOfSections) {
                    Intent i = new Intent(getApplicationContext(), CameraActivity.class);
                    i.putExtra("obj_form", form);
                    startActivity(i);
                    finish();
                }
                else {
                    generateForm(form.getSections().get(noOfThisSection - 1), layout);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        // this takes the user 'back', as if they pressed the left-facing
        hideKeyboard(this);
        if(noOfThisSection-1 == 0) {
            // Todo: STRINGS!!
            new AlertDialog.Builder(this)
                    .setTitle("Confirm")
                    .setMessage("Are you sure you want to leave this page? Unsaved information will be lost.")
                    .setPositiveButton("Leave", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(form.getRowNumber() > 0) {
                                Intent formOverview = new Intent(getApplicationContext(), FormListActivity.class);
                                formOverview.putExtra("fileName", form.getFileName());
                                formOverview.putExtra("sheetName", form.getSheetName());
                                formOverview.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                startActivity(formOverview);
                                finish();
                            } else {
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }

                        }
                    })
                    .setNegativeButton(R.string.dialog_cancel, null).show();
        } else {
            emptyForm(form, layout);
            noOfThisSection--;
            generateForm(form.getSections().get(noOfThisSection - 1), layout);
        }
    }
}
