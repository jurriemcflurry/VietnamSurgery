package toning.juriaan.vietnamsurgery;

import android.os.Parcel;

public class TextField extends Field {
    public TextField(String fieldName) {
        super(fieldName);
    }

    public String getType() {
        return Field.TEXT;
    }
}
