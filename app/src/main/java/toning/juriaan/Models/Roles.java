package toning.juriaan.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Roles implements Parcelable {

    @SerializedName("UserId")
    public String userId;

    @SerializedName("RoleId")
    public String roleId;

    protected Roles(Parcel in){
        userId = in.readString();
        roleId = in.readString();
    }

    public static final Creator<Roles> CREATOR = new Creator<Roles>() {
        @Override
        public Roles createFromParcel(Parcel source) {
            return new Roles(source);
        }

        @Override
        public Roles[] newArray(int size) {
            return new Roles[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(roleId);
    }

    public String getUserId(){
        return this.userId;
    }

    public String getRoleId(){
        return this.roleId;
    }

    public String getRole(){
        if(this.roleId.equals("1")){
            return "Admin";
        }
        else{
            return "User";
        }
    }
}
