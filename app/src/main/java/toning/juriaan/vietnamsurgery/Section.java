package toning.juriaan.vietnamsurgery;

import java.util.List;

public class Section {
    private String sectionName;

    private List<Field> fields;

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }
}
