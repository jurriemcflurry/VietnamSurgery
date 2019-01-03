package toning.juriaan.vietnamsurgery.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import toning.juriaan.vietnamsurgery.R;
import toning.juriaan.vietnamsurgery.adapter.FormListAdapter;
import toning.juriaan.vietnamsurgery.model.FormTemplate;

public class FormListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_list);

        FormTemplate form = new FormTemplate();
        GridView gridView = findViewById(R.id.form_list_grid_view);
        ArrayList<FormTemplate> list = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            list.add(new FormTemplate());
        }

        gridView.setAdapter(new FormListAdapter(this, list));

    }
}
