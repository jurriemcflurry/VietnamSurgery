package toning.juriaan.vietnamsurgery;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

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
