package toning.juriaan.vietnamsurgery.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;

import toning.juriaan.vietnamsurgery.FormListListener;
import toning.juriaan.vietnamsurgery.R;
import toning.juriaan.vietnamsurgery.activity.DetailPhotoActivity;
import toning.juriaan.vietnamsurgery.activity.FormListActivity;
import toning.juriaan.vietnamsurgery.activity.OverviewFormActivity;
import toning.juriaan.vietnamsurgery.model.FormTemplate;

public class FormListAdapter extends BaseAdapter {

    private final FormListListener mListener;
    private Context context;
    private ArrayList<FormTemplate> formList = new ArrayList<>();

    public FormListAdapter(Context context, ArrayList<FormTemplate> formList, FormListListener listener) {
        this.context = context;
        this.formList = formList;
        this.mListener = listener;
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
            TextView name = gridView.findViewById(R.id.grid_item_nameTxt);
            name.setText(formList.get(position).getSections().get(0).getFields().get(1).getAnswer());

            TextView district = gridView.findViewById(R.id.grid_item_districtAnswerTxt);
            district.setText(formList.get(position).getSections().get(1).getFields().get(3).getAnswer());

            TextView photoCount = gridView.findViewById(R.id.grid_item_photoAnswerTxt);
            photoCount.setText(Integer.toString(formList.get(position).getPictures().size()));

            TextView formName = gridView.findViewById(R.id.grid_item_formNameAnswerTxt);
            formName.setText("");
            TextView formNameLabel = gridView.findViewById(R.id.grid_item_formNameTxt);
            formNameLabel.setText("");

            TextView created = gridView.findViewById(R.id.grid_item_createdAnswerTxt);
            created.setText("");
            TextView createdLabel = gridView.findViewById(R.id.grid_item_createdTxt);
            createdLabel.setText("");

            gridView.setOnClickListener((View v) -> {
                FormTemplate form = (FormTemplate)getItem(position);
                mListener.onItemClick(form);
            });


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
        return formList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
