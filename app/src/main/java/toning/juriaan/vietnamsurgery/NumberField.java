package toning.juriaan.vietnamsurgery;

import android.os.Parcel;

public class NumberField extends Field {
    public NumberField(String fieldName) {
        super(fieldName);
    }

    public String getType() {
        return Field.NUMBER;
    }
}
