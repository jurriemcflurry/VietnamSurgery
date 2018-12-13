package toning.juriaan.Models;

public class Section {
    private String sectionName;

    private Field[] fields;

    public Section(){}

    public Section(String sectionName, Field[] fields) {
        this.sectionName = sectionName;
        this.fields = fields;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public Field[] getFields() {
        return fields;
    }

    public void setFields(Field[] fields) {
        this.fields = fields;
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