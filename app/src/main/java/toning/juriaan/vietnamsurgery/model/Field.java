package toning.juriaan.vietnamsurgery.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Field implements Parcelable {
    private String fieldName;
    private String answer;
    private int column;
    private int row;


    public String getFieldName() {
        return fieldName;
    }
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getAnswer() {
        return answer;
    }
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getColumn() {
        return column;
    }
    public void setColumn(int column) {
        this.column = column;
    }

    public int getRow() {
        return row;
    }
    public void setRow(int row) {
        this.row = row;
    }

    public Field() {}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(fieldName);
        out.writeString(answer);
        out.writeInt(column);
        out.writeInt(row);
    }

    public static final Creator<Field> CREATOR = new Creator<Field>() {
        @Override
        public Field createFromParcel(Parcel source) {
            return new Field(source);
        }

        @Override
        public Field[] newArray(int size) {
            return new Field[size];
        }
    };

    private Field(Parcel in) {
        fieldName = in.readString();
        answer = in.readString();
        column = in.readInt();
        row = in.readInt();
    }

}

