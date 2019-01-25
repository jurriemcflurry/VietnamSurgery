package toning.juriaan.models;

import com.google.gson.annotations.SerializedName;

public class ChangePasswordObject {

    @SerializedName("OldPassword")
    private String oldPassword;

    @SerializedName("NewPassword")
    private String newPassword;

    @SerializedName("ConfirmPassword")
    private String confirmNewPassword;

    public ChangePasswordObject(String oldPassword, String newPassword, String confirmNewPassword){
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.confirmNewPassword = confirmNewPassword;
    }
}
