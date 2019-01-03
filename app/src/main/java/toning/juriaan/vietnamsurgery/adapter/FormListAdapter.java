package toning.juriaan.vietnamsurgery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

import toning.juriaan.vietnamsurgery.R;
import toning.juriaan.vietnamsurgery.model.FormTemplate;

public class FormListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<FormTemplate> formList = new ArrayList<>();

    public FormListAdapter(Context context, ArrayList<FormTemplate> formList) {
        this.context = context;
        this.formList = formList;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        if (convertView == null) {

            gridView = new View(context);

            // get layout from mobile.xml
            gridView = inflater.inflate(R.layout.form_list_grid_view_item, null);

            // set image based on selected text

        } else {
            gridView = convertView;
        }

        return gridView;
    }

    @Override
    public int getCount() {
        return formList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
