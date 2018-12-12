package toning.juriaan.vietnamsurgery;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class User implements Parcelable {

    @SerializedName("Email")
    public String email;

    @SerializedName("Roles")
    public Roles[] roles;

    @SerializedName("Id")
    public String id;

    @SerializedName("UserName")
    public String username;

    protected User(Parcel in){
        email = in.readString();
        id = in.readString();
        username = in.readString();
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
    }

    public String getId(){
        return this.id;
    }
}
