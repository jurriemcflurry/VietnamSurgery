package toning.juriaan.vietnamsurgery.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Field implements Parcelable {
    private String fieldName;
    private String answer;
    private int column;
    private int row;
    private boolean mandatory;


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

    public boolean getMandatory() {
        return mandatory;
    }
    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
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
        out.writeByte((byte) (mandatory ? 1 : 0));
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
        mandatory = in.readByte() != 0;
    }

}

