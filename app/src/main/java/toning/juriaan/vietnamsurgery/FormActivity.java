package toning.juriaan.vietnamsurgery;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

@SuppressLint("Registered")
public class FormActivity extends AppCompatActivity {

    public final static String INDEX = "FormActivity.sectionIndex";
    public final static String FORM = "FormActivity.form";

    private int sectionIndex;
    private Form form;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Helper.log("FormActivity.onCreate()");

        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        Intent intent = getIntent();

        Helper.log("4");
        form = getIntent().getParcelableExtra(FORM);
        Helper.log("3");
        sectionIndex = intent.getIntExtra(INDEX, -1);
        Helper.log("5");
        Helper.log("sectionIndex: " + sectionIndex);
//        setTitle(form.getSections()[sectionIndex].getSectionName());
    }
}
