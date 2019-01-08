package toning.juriaan.Models;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Arrays;

public class FieldContent {
    @Expose
    @SerializedName("Name")
    private String name;

    @Expose
    @SerializedName("Value")
    private String value;

    public FieldContent() { }

    public FieldContent(String name) {
        this.name = name;
        this.value = "";
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

    public ArrayList<Answer> getAnswers() {
        return new ArrayList<>(Arrays.asList(new Gson().fromJson(this.value, Answer[].class)));
    }

    public void setAnswers(ArrayList<Answer> answers) {
        this.value = new Gson().toJson(answers.toArray());
    }
}
