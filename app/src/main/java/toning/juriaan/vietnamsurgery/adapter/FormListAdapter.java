package toning.juriaan.vietnamsurgery.adapter;

import android.content.Context;
import android.util.Log;
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
            TextView name = gridView.findViewById(R.id.grid_item_nameTxt);
            name.setText(formList.get(position).getSections().get(0).getFields().get(1).getAnswer());

            TextView district = gridView.findViewById(R.id.grid_item_districtAnswerTxt);
            district.setText(formList.get(position).getSections().get(1).getFields().get(2).getAnswer());

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
