package toning.juriaan.vietnamsurgery;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class FormTemplate implements Parcelable {
    private String formName;
    private String fileName;
    private String sheetName;
    private List<Section> sections;
    private List<String> pictures;
    private List<String> thumbImages;

    public String getFormName() {
        return formName;
    }
    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSheetName() {
        return sheetName;
    }
    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public List<Section> getSections() {
        return sections;
    }
    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    public List<String> getPictures() {
        return pictures;
    }
    public void setPictures(List<String> pictures) {
        this.pictures = pictures;
    }

    public List<String> getThumbImages() {
        return thumbImages;
    }
    public void setThumbImages(List<String> thumbImages) {
        this.thumbImages = thumbImages;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(formName);
        out.writeString(fileName);
        out.writeString(sheetName);
        out.writeList(sections);
        out.writeList(pictures);
        out.writeList(thumbImages);
    }

    public static final Creator<FormTemplate> CREATOR = new Creator<FormTemplate>() {
        @Override
        public FormTemplate createFromParcel(Parcel source) {
            return new FormTemplate(source);
        }

        @Override
        public FormTemplate[] newArray(int size) {
            return new FormTemplate[size];
        }
    };

    public FormTemplate(){}

    private FormTemplate(Parcel in) {
        formName = in.readString();
        fileName = in.readString();
        sheetName = in.readString();
        sections = new ArrayList<>();
        in.readList(sections, Section.class.getClassLoader());
        pictures = new ArrayList<>();
        in.readList(pictures, String.class.getClassLoader());
        thumbImages = new ArrayList<>();
        in.readList(thumbImages, String.class.getClassLoader());
    }

    @Override
    public String toString() {
        String str = "Info\n" + fileName + "\n";
        for (Section sec : sections) {
            for (Field f : sec.getFields()) {
                str += f.getFieldName() + ": " + f.getAnswer() + "\n";
            }
        }

        return str;
    }
}