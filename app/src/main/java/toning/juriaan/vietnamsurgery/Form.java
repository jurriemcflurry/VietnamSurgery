package toning.juriaan.vietnamsurgery;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Form {
    private String formName;

    private Section[] sections;

    public Form(String formName, Section[] sections) {
        this.formName = formName;
        this.sections = sections;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public Section[] getSections() {
        return sections;
    }

    public void setSections(Section[] sections) {
        this.sections = sections;
    }


}

