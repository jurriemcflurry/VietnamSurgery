package toning.juriaan.vietnamsurgery;

public class LoginObject {

    public String username;
    public String password;
    private String grant_type = "password";

    public LoginObject(String username, String password){
        this.username = username;
        this.password = password;
    }
}
