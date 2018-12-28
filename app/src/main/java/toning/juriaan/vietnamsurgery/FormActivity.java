package toning.juriaan.vietnamsurgery;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FormActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView sectionNameTv;
    private TextView stepCounter;
    private int noOfSections;
    private int noOfThisSection = 1;
    private Map<String, Integer> idsMap;
    private LinearLayout layout;
    private FormTemplate form;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        Intent i = getIntent();
        form = i.getParcelableExtra("obj_form");

        toolbar = findViewById(R.id.form_toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("New form" + form.getFormName());

        sectionNameTv = findViewById(R.id.section_name);
        noOfSections = form.getSections().size();
        stepCounter = findViewById(R.id.step_counter);

        sectionNameTv.setText(form.getSections().get(0).getSectionName());

        layout = findViewById(R.id.formLayout);

        Button bt = new Button(this);
        bt.setText("Next");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.END;
        bt.setLayoutParams(params);
        bt.setBackgroundColor(Color.TRANSPARENT);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emptyForm(form, layout);
                noOfThisSection++;
                if(noOfThisSection > noOfSections) {
                    Intent i = new Intent(getApplicationContext(), CameraActivity.class);
                    i.putExtra("obj_form", form);
                    startActivity(i);
                }
                else {
                    generateForm(form.getSections().get(noOfThisSection - 1), layout);
                }
            }
        });
        toolbar.addView(bt);

        int tempNoOfSec = i.getIntExtra("step", 0);
        if(tempNoOfSec == 0) {
            generateForm(form.getSections().get(0), layout);
        } else {
            noOfThisSection = tempNoOfSec;
            generateForm(form.getSections().get(tempNoOfSec-1), layout);
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
                        String answer = ((RadioButton) findViewById((int)pair.getValue())).getText().toString();
                        f.setAnswer(answer);
                    }
                }
            }
        }
        layout.removeAllViews();
    }

    private void generateForm(Section section, LinearLayout layout) {
        stepCounter.setText("Step " + noOfThisSection + " of " + Integer.toString(noOfSections + 1));
        idsMap = new HashMap<>();
        if(noOfThisSection == 1) {
            RadioGroup rg = new RadioGroup(this);
            rg.setOrientation(RadioGroup.HORIZONTAL);
            for(int i = 0; i < section.getFields().size(); i++) {
                Field f = section.getFields().get(i);
                if(i < section.getFields().size() - 2) {
                    TextInputLayout editField = createTextField(f.getFieldName(), f.getColumn());
                    idsMap.put(f.getFieldName(), f.getColumn());
                    layout.addView(editField);
                } else {
                    RadioButton rb = createRadioButton(f.getFieldName(), f.getColumn());
                    rg.addView(rb);
                }
            }
            layout.addView(rg);
        } else {
            for(Field f : section.getFields()) {
                TextInputLayout editField = createTextField(f.getFieldName(), f.getColumn());
                idsMap.put(f.getFieldName(), f.getColumn());
                layout.addView(editField);
            }
        }
    }

    private TextInputLayout createTextField(String textFieldName, int column) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        TextInputLayout textInputLayout = new TextInputLayout(this);
        textInputLayout.setLayoutParams(layoutParams);

        EditText editText = new EditText(textInputLayout.getContext());
        editText.setLayoutParams(layoutParams);
        editText.setHint(textFieldName);
        editText.setId(column);

        textInputLayout.addView(editText);

        return textInputLayout;
    }

    private RadioButton createRadioButton(String text, int id) {
        RadioButton rb = new RadioButton(this);
        rb.setId(id);
        idsMap.put(text, id);
        rb.setText(text);
        return rb;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // this takes the user 'back', as if they pressed the left-facing
                noOfThisSection--;
                if(noOfThisSection == 0) {
                    // Todo: Make sure user can go back to first screen!
                    noOfThisSection = 1;
                    return true;
                } else {
                    emptyForm(form, layout);
                    generateForm(form.getSections().get(noOfThisSection - 1), layout);
                    return true;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
