package toning.juriaan.Models;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import Activities.MainActivity;

public class FormAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Form> forms;
    private MainActivity context;

    public FormAdapter(ArrayList<Form> forms, MainActivity context) {
        this.forms = forms;
        this.context = context;
    }

    public interface FormListener {
        void onItemClick(Form form);
    }

    public void setForms(ArrayList<Form> forms) {
        this.forms = forms;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new FormViewHolder(LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.form_list_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        final Form form = forms.get(i);
        FormViewHolder vh = (FormViewHolder) viewHolder;
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.onItemClick(form);
            }
        });
        vh.formName.setText(form.getFormName());
    }

    @Override
    public int getItemCount() {
        return forms.size();
    }

    public static class FormViewHolder extends RecyclerView.ViewHolder {

        TextView formName;

        public FormViewHolder(@NonNull View itemView) {
            super(itemView);
            formName = itemView.findViewById(R.id.form_list_name);
        }
    }
}
