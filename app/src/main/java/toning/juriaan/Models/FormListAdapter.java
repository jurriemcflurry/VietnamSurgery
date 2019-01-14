package toning.juriaan.Models;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    public void setFormContentNames(ArrayList<String> formContentNames) {
        this.formContentNames = formContentNames;
        notifyDataSetChanged();
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

        String name = "Name: " + formContent.getAnswer("name");
        String birthyear = "Birthyear: " + formContent.getAnswer("birthyear");
        String district = "District: " + formContent.getAnswer("district");
        String dateTime = "Date: " + formContent.getFormContentDate();

        vh.formListNameView.setText(name);
        vh.formListBirthyearView.setText(birthyear);
        vh.formListDistrictView.setText(district);
        vh.formListDateView.setText(dateTime);

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

        TextView formListNameView;
        TextView formListBirthyearView;
        TextView formListDistrictView;
        TextView formListDateView;

        public FormListViewHolder(@NonNull View itemView) {
            super(itemView);
            formListNameView = itemView.findViewById(R.id.form_list_name);
            formListBirthyearView = itemView.findViewById(R.id.form_list_birthyear);
            formListDistrictView = itemView.findViewById(R.id.form_list_district);
            formListDateView = itemView.findViewById(R.id.form_list_date);

            setupTextView(formListNameView);
            setupTextView(formListBirthyearView);
            setupTextView(formListDistrictView);
            setupTextView(formListDateView);

        }

        private void setupTextView(TextView textView) {
            textView.setTextSize(15);
            textView.setTextColor(Color.BLACK);
            textView.setPadding(0, 10, 0, 0);
            textView.setSingleLine(true);
            textView.setEllipsize(TextUtils.TruncateAt.END);
        }
    }
}