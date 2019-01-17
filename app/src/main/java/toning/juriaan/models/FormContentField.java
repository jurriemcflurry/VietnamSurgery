package toning.juriaan.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FormContentField {
    @Expose
    @SerializedName("Name")
    private String name;

    @Expose
    @SerializedName("Value")
    private String value;

    public FormContentField(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
