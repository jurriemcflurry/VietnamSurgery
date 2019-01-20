package toning.juriaan.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Section {
    @Expose
    @SerializedName("Name")
    private String sectionName;

    @Expose
    @SerializedName("Fields")
    private ArrayList<Field> fields;

    public Section(String sectionName){
        this.sectionName = sectionName;
        fields = new ArrayList<>();
    }

    public Section(String sectionName, ArrayList<Field> fields) {
        this.sectionName = sectionName;
        this.fields = fields;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public ArrayList<Field> getFields() {
        return fields;
    }

    public void setFields(ArrayList<Field> fields) {
        this.fields = fields;
    }

    public void addFields(Field field){
        fields.add(field);
    }

    @Override
    public String toString() {
        String s = getSectionName() + "\n";
        for (Field field : getFields()) {
            s += field.toString() + "\n";
        }
        s += "\n";

        return s;
    }
}
