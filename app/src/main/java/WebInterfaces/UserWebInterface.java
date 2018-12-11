package WebInterfaces;

import ResponseModels.LoginResponse;
import ResponseModels.RegisterResponse;
import ResponseModels.UsersResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import toning.juriaan.vietnamsurgery.RegisterObject;

public interface UserWebInterface {

    @POST("/api/Account")
    Call<RegisterResponse> register(@Body RegisterObject registerObject);

    @FormUrlEncoded
    @POST("/token")
    Call<LoginResponse> login(@Field("UserName") String username, @Field("Password") String password, @Field("grant_type") String grant_type);

    @GET("/api/Account")
    Call<UsersResponse> getUsers(@Header("Authorization") String access_token);
}
