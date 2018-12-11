package ResponseModels;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import toning.juriaan.vietnamsurgery.User;


//moet een array zijn ipv object, even googlen op
// java.lang.IllegalStateException: Expected BEGIN_OBJECT but was BEGIN_ARRAY
public class UsersResponse {
    @SerializedName("")
    public List<User> userlist;

}
