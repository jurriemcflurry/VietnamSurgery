package toning.juriaan.Models;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import Activities.FormListActivity;

public class FormListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<String> formContentNames;
    private FormListActivity context;

    public interface FormContentlistener {
        void onItemClick(FormContent formContent);
    }

    public FormListAdapter(ArrayList<String> formContentNames, FormListActivity context) {
        this.formContentNames = formContentNames;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new FormListViewHolder(LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.form_content_list_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        final FormContent formContent = Storage.getFormContent(formContentNames.get(i), context);
        FormListViewHolder vh = (FormListViewHolder) viewHolder;
        String textViewText = "Name: " + formContent.getFormContentName();
        vh.formContentNameTextView.setText(textViewText);
        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.onItemClick(formContent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return formContentNames.size();
    }

    public static class FormListViewHolder extends RecyclerView.ViewHolder {

        TextView formContentNameTextView;

        public FormListViewHolder(@NonNull View itemView) {
            super(itemView);
            formContentNameTextView = itemView.findViewById(R.id.form_content_name_view);
            formContentNameTextView.setTextSize(15);
            formContentNameTextView.setTextColor(Color.BLACK);
            formContentNameTextView.setPadding(0, 10, 0, 0);
        }
    }
}
