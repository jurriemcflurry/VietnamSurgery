package toning.juriaan.vietnamsurgery.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import toning.juriaan.vietnamsurgery.listener.FormListListener;
import toning.juriaan.vietnamsurgery.R;
import toning.juriaan.vietnamsurgery.model.FormTemplate;

public class FormListAdapter extends RecyclerView.Adapter<FormListAdapter.FormListViewHolder> {

    private final FormListListener mListener;
    private final Context context;
    private final ArrayList<FormTemplate> formList;

    public FormListAdapter(Context context, ArrayList<FormTemplate> formList, FormListListener listener) {
        this.context = context;
        this.formList = formList;
        this.mListener = listener;
    }

    @Override
    public FormListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FormListViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.form_list_grid_view_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final FormListViewHolder holder, int position) {
        // Get the item for current position
        final FormTemplate node = getItem(position);

        // Fill the views in the VH with the content for the current position
        holder.name.setText(node.getSections().get(0).getFields().get(1).getAnswer());
        holder.district.setText(node.getSections().get(1).getFields().get(3).getAnswer());
        holder.photoCount.setText(Integer.toString(formList.get(position).getPictures().size()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(holder.itemView, node);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return formList.size();
    }

    private FormTemplate getItem(int position) {
        return formList.get(position);
    }

    public class FormListViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView district;
        TextView photoCount;

        FormListViewHolder(View itemView){
            super(itemView);
            name = itemView.findViewById(R.id.grid_item_nameTxt);
            district = itemView.findViewById(R.id.grid_item_districtAnswerTxt);
            photoCount = itemView.findViewById(R.id.grid_item_photoAnswerTxt);
        }
    }
}
