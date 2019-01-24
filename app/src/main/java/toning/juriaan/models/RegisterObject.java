package toning.juriaan.models;

import com.google.gson.annotations.SerializedName;

public class RegisterObject {

    @SerializedName("Password")
    private String password;

    @SerializedName("ConfirmPassword")
    private String confirmPassword;

    @SerializedName("UserRole")
    private String userrole;

    @SerializedName("Email")
    private String email;

    public RegisterObject(String password, String confirmPassword, String userrole, String email){
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.userrole = userrole;
        this.email = email;
    }

}
