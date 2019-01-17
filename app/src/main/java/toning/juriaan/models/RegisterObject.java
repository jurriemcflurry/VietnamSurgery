package toning.juriaan.models;

import com.google.gson.annotations.SerializedName;

public class RegisterObject {

    @SerializedName("Password")
    public String password;

    @SerializedName("ConfirmPassword")
    public String confirmPassword;

    @SerializedName("UserRole")
    public String userrole;

    @SerializedName("Email")
    public String email;

    public RegisterObject(String password, String confirmPassword, String userrole, String email){
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.userrole = userrole;
        this.email = email;
    }

}
