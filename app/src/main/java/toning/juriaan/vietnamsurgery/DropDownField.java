package toning.juriaan.vietnamsurgery;


import android.annotation.SuppressLint;
import android.os.Parcel;

import java.util.List;

public class DropDownField extends Field {
    private List<String> options;

    public DropDownField(String fieldName, List<String> options) {
        super(fieldName);
        this.options = options;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public String getType() {
        return Field.DROP_DOWN;
    }

    @Override
    public String toString() {
        String s = super.toString();

        for (String option : getOptions()) {
            s += option + ", ";
        }

        s += "\n";

        return s;
    }
}
