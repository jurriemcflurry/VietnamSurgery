package Activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import toning.juriaan.vietnamsurgery.Form;
import toning.juriaan.vietnamsurgery.Helper;
import toning.juriaan.vietnamsurgery.R;
import toning.juriaan.vietnamsurgery.Storage;

@SuppressLint("Registered")
public class FormActivity extends AppCompatActivity {

    public final static String INDEX = "FormActivity.sectionIndex";
    public final static String FORM = "FormActivity.form";

    private int sectionIndex;
    private Form form;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        if (Storage.saveFormTemplate(Form.getDummyForm(), this)) {
            Helper.log("Success");
        } else {
            Helper.log("Fail");
        }
    }
}
