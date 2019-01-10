package Activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.FrameLayout;

import toning.juriaan.Models.FormListAdapter;
import toning.juriaan.Models.R;
import toning.juriaan.Models.Storage;

public class FormListActivity extends BaseActivity {

    private RecyclerView formListRecycler;
    private FormListAdapter formListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.form_list_activity, contentFrameLayout);
        getSupportActionBar().setTitle(getString(R.string.formContent));


        formListAdapter = new FormListAdapter(Storage.getFormContentNames(this), this);
        formListRecycler = findViewById(R.id.form_list_recycler);
        formListRecycler.setAdapter(formListAdapter);
        formListRecycler.setLayoutManager(new LinearLayoutManager(this));

    }
}
