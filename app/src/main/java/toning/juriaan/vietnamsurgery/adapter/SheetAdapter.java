package toning.juriaan.vietnamsurgery.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import toning.juriaan.vietnamsurgery.R;
import toning.juriaan.vietnamsurgery.listener.SheetListener;

public class SheetAdapter extends RecyclerView.Adapter<SheetAdapter.SheetViewHolder> {

    private final SheetListener mListener;
    private final Context context;
    private final List<String> names;

    public SheetAdapter(Context context, List<String> names, SheetListener listener) {
        this.context = context;
        this.names = names;
        this.mListener = listener;
    }

    @Override
    public SheetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SheetViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.main_grid_view_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final SheetViewHolder holder, int position) {
        // Get the item for current position
        final String node = getItem(position);

        // Fill the views in the VH with the content for the current position
        holder.sheetName.setText(node);
        if(position + 1 == getItemCount()) {
            holder.seperator.setVisibility(View.GONE);
        }

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
        return names.size();
    }

    private String getItem(int position) {
        return names.get(position);
    }

    public class SheetViewHolder extends RecyclerView.ViewHolder {
        TextView sheetName;
        View seperator;

        SheetViewHolder(View itemView){
            super(itemView);
            sheetName = itemView.findViewById(R.id.file_name);
            seperator = itemView.findViewById(R.id.seperator);
        }
    }
}
