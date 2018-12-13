package toning.juriaan.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class User implements Parcelable {

    @SerializedName("Email")
    public String email;

    @SerializedName("Roles")
    public List<toning.juriaan.Models.Roles> roles;

    @SerializedName("Id")
    public String id;

    @SerializedName("UserName")
    public String username;

    protected User(Parcel in){
        email = in.readString();
        id = in.readString();
        username = in.readString();

        if(in.readByte() == 0x01){
            roles = new ArrayList<>();
            in.readList(roles, toning.juriaan.Models.Roles.class.getClassLoader());
        }
        else{
            roles = null;
        }
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(id);
        dest.writeString(username);

        if(roles == null){
            dest.writeByte((byte) (0x00));
        }else{
            dest.writeByte((byte) (0x01));
            dest.writeList(roles);
        }
    }

    public String getId(){
        return this.id;
    }
}
