package toning.juriaan.Models;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class SectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TEXT = 0;
    private static final int DROP_DOWN = 1;

    private Context context;
    private Field[] fields;

    public SectionAdapter(Context context) {
        this.context = context;
    }

    public void setFields(Field[] fields) {
        this.fields = fields;
        Helper.log("in adapter");
        for (Field f : this.fields) {
            Helper.log(f.getType());
        }

        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        Field field = fields[position];
        if (field.getType().equals(FieldType.text.toString()) || field.getType().equals(FieldType.number.toString())) {
            return TEXT;
        } else if (field.getType().equals(FieldType.choice.toString())) {
            return DROP_DOWN;
        } else {
            return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int fieldType) {
        if (fieldType == TEXT) {
            return new TextFieldViewHolder(LayoutInflater.from(
                    viewGroup.getContext()).inflate(R.layout.text_field_layout, viewGroup, false));
        } else if (fieldType == DROP_DOWN) {
            return new DropDownFieldViewHolder(LayoutInflater.from(
                    viewGroup.getContext()).inflate(R.layout.drop_down_field_layout, viewGroup, false));
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder fieldViewHolder, int i) {
        if (fieldViewHolder instanceof TextFieldViewHolder) {
            onBindViewHolder((TextFieldViewHolder) fieldViewHolder, i);
        } else if (fieldViewHolder instanceof DropDownFieldViewHolder) {
            onBindViewHolder((DropDownFieldViewHolder) fieldViewHolder, i);
        }
    }

    private void onBindViewHolder(@NonNull TextFieldViewHolder textFieldViewHolder, int i) {
        Field field = fields[i];
        textFieldViewHolder.fieldLabel.setText(field.getFieldName());
    }

    private void onBindViewHolder(@NonNull DropDownFieldViewHolder dropDownFieldViewHolder, int i) {
        Field field = fields[i];
        dropDownFieldViewHolder.fieldLabel.setText(field.getFieldName());
    }

    @Override
    public int getItemCount() {
        return fields.length;
    }

    public static class TextFieldViewHolder extends RecyclerView.ViewHolder {
        TextView fieldLabel;
        EditText fieldValue;

        public TextFieldViewHolder(@NonNull View itemView) {
            super(itemView);
            fieldLabel = itemView.findViewById(R.id.text_field_label);
            fieldValue = itemView.findViewById(R.id.text_field_value);
        }
    }

    public static class DropDownFieldViewHolder extends RecyclerView.ViewHolder {
        TextView fieldLabel;
        Spinner optionsDropDown;

        public DropDownFieldViewHolder(@NonNull View itemView) {
            super(itemView);
            fieldLabel = itemView.findViewById(R.id.drop_down_field_label);
            optionsDropDown = itemView.findViewById(R.id.drop_down_field_options);
        }
    }
}
