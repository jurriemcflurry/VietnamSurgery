package toning.juriaan.vietnamsurgery.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import toning.juriaan.vietnamsurgery.R;
import toning.juriaan.vietnamsurgery.listener.FileNameListener;

public class FileNameAdapter extends RecyclerView.Adapter<FileNameAdapter.FileNameViewHolder> {

    private final FileNameListener mListener;
    private final Context context;
    private final List<File> files;

    public FileNameAdapter(Context context, List<File> files, FileNameListener listener) {
        this.context = context;
        this.files = files;
        this.mListener = listener;
    }

    @Override
    public FileNameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FileNameAdapter.FileNameViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.main_grid_view_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final FileNameViewHolder holder, int position) {
        // Get the item for current position
        final File node = getItem(position);

        // Fill the views in the VH with the content for the current position
        holder.fileName.setText(node.getName());
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
        return files.size();
    }

    private File getItem(int position) {
        return files.get(position);
    }

    public class FileNameViewHolder extends RecyclerView.ViewHolder {
        TextView fileName;
        View seperator;

        FileNameViewHolder(View itemView){
            super(itemView);
            fileName = itemView.findViewById(R.id.file_name);
            seperator = itemView.findViewById(R.id.seperator);
        }
    }
}
