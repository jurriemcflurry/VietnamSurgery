package toning.juriaan.Models;

import com.google.gson.annotations.SerializedName;

public class RegisterObject {

    @SerializedName("UserName")
    public String username;

    @SerializedName("Password")
    public String password;

    @SerializedName("ConfirmPassword")
    public String confirmPassword;

    @SerializedName("UserRole")
    public String userrole;

    @SerializedName("Email")
    public String email;

    public RegisterObject(String username, String password, String confirmPassword, String userrole, String email){
        this.username = username;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.userrole = userrole;
        this.email = email;
    }

}
