package activities;

import android.os.Bundle;
import android.widget.FrameLayout;

import toning.juriaan.models.R;

public class CreateFormActivity extends FormBaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = findViewById(R.id.formbase_framelayout);
        getLayoutInflater().inflate(R.layout.activity_create_form, contentFrameLayout);
        getSupportActionBar().setTitle(getString(R.string.createFormTitle));
    }
}
