package toning.juriaan.vietnamsurgery;

import com.google.gson.annotations.SerializedName;

public class Roles {

    @SerializedName("UserId")
    public String userId;

    @SerializedName("RoleId")
    public String roleId;

    public Roles(String userId, String roleId){
        this.userId = userId;
        this.roleId = roleId;
    }
}
