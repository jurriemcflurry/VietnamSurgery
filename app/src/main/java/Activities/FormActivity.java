package Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import toning.juriaan.vietnamsurgery.Form;
import toning.juriaan.vietnamsurgery.Helper;
import toning.juriaan.vietnamsurgery.R;
import toning.juriaan.vietnamsurgery.Storage;

@SuppressLint("Registered")
public class FormActivity extends AppCompatActivity {

    public final static String FORM = "FormActivity.form1";

    private int sectionIndex = 0;
    private Form form;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        Intent intent = getIntent();
        form = Storage.getFormTemplate(intent.getStringExtra(FORM), this);
        if (form != null) {
            form.setFormName("FormActivity form");
            Storage.saveFormTemplate(form, this);
        } else {
            Helper.log("FormActivity.form is null.");
        }
    }
}
