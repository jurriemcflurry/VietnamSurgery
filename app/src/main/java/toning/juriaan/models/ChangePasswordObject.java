package toning.juriaan.models;

import com.google.gson.annotations.SerializedName;

public class ChangePasswordObject {

    @SerializedName("OldPassword")
    public String oldPassword;

    @SerializedName("NewPassword")
    public String newPassword;

    @SerializedName("ConfirmPassword")
    public String confirmNewPassword;

    public ChangePasswordObject(String oldPassword, String newPassword, String confirmNewPassword){
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.confirmNewPassword = confirmNewPassword;
    }
}
