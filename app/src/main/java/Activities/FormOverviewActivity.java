package Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import toning.juriaan.Models.R;

public class FormOverviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_overview);

    }
}
