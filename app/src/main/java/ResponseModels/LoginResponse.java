package ResponseModels;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("access_token")
    public String access_token;

    @SerializedName("userName")
    public String userName;

    @SerializedName("token_type")
    public String token_type;

    @SerializedName("expires_in")
    public int expires_in;

    @SerializedName(".issued")
    public String issued;

    @SerializedName(".expires")
    public String expires;
}
