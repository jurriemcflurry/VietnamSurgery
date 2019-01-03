package toning.juriaan.vietnamsurgery.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Section implements Parcelable {
    private String sectionName;
    private int column;
    private List<Field> fields;
    private int number;

    public String getSectionName() {
        return sectionName;
    }
    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public int getColumn() {
        return column;
    }
    public void setColumn(int column) {
        this.column = column;
    }

    public List<Field> getFields() {
        return fields;
    }
    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public int getNumber() {
        return number;
    }
    public void setNumber(int number) {
        this.number = number;
    }

    public Section() {}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(sectionName);
        out.writeInt(column);
        out.writeList(fields);
        out.writeInt(number);
    }

    public static final Creator<Section> CREATOR = new Creator<Section>() {
        @Override
        public Section createFromParcel(Parcel source) {
            return new Section(source);
        }

        @Override
        public Section[] newArray(int size) {
            return new Section[size];
        }
    };

    private Section(Parcel in) {
        sectionName = in.readString();
        column = in.readInt();
        fields = new ArrayList<>();
        in.readList(fields, Field.class.getClassLoader());
        number = in.readInt();
    }
}
