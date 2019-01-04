package ResponseModels;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("access_token")
    public String accesstoken;

    @SerializedName("token_type")
    public String token_type;

//    @SerializedName("expires_in")
//    public int expires_in;

    @SerializedName("userName")
    public String userName;

    /*@SerializedName(".issued")
    public String issued;

    @SerializedName(".expires")
    public String expires;*/
}
