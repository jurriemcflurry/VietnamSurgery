package toning.juriaan.Models;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import Activities.CameraActivity;

public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<String> imageNames;
    private CameraActivity context;

    public ImageAdapter(CameraActivity context) {
        this.context = context;
        imageNames = new ArrayList<>();
    }

    public void setImageNames(ArrayList<String> imageNames) {
        this.imageNames = imageNames;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Helper.log("onCreateViewHolder() ImageAdapter");
        return new ImageViewHolder(LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.layout_photo_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        final Image image = Storage.getImageByName(imageNames.get(i), context);
        ImageViewHolder imageViewHolder = (ImageViewHolder) viewHolder;

        Helper.log("ImageAdapter formContent.getImageNames().size() " + imageNames.size(), context);
        try {
            imageViewHolder.imageView.setImageBitmap(image.getBitmap());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.photo_view);
        }
    }
}
