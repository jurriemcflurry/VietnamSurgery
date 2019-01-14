package toning.juriaan.Models;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import Activities.MainActivity;

public class FormAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Form> forms;
    private MainActivity context;
    private Map<Integer, Integer> amountById;

    public FormAdapter(ArrayList<Form> forms, MainActivity context) {
        this.forms = forms;
        this.context = context;
        updateAmounts();
    }

    public void updateAmounts() {
        amountById = Storage.getAmountById(context);
        notifyDataSetChanged();
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
                .inflate(R.layout.layout_form_list, viewGroup, false));
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
        vh.contentAmount.setText(getContentAmount(form.getId()).toString());
    }

    private Integer getContentAmount(Integer id) {
        for (Map.Entry<Integer, Integer> entry : amountById.entrySet()) {
            if (entry.getKey().equals(id)) {
                return entry.getValue();
            }
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        return forms.size();
    }

    public static class FormViewHolder extends RecyclerView.ViewHolder {

        TextView formName;
        TextView contentAmount;

        public FormViewHolder(@NonNull View itemView) {
            super(itemView);
            formName = itemView.findViewById(R.id.form_template_list_name);
            contentAmount = itemView.findViewById(R.id.form_template_content_amount);

            setupTextView(formName);
            setupTextView(contentAmount);
        }

        private void setupTextView(TextView textView) {
            textView.setTextSize(15);
            textView.setTextColor(Color.BLACK);
            textView.setPadding(0, 10, 0, 0);
        }
    }
}
