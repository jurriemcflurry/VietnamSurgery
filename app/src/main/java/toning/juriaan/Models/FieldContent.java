package toning.juriaan.Models;

public class FieldContent {
    private String name;
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
}
