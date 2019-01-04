package WebInterfaces;

import java.util.List;

import ResponseModels.ChangePasswordResponse;
import ResponseModels.DeleteResponse;
import ResponseModels.LoginResponse;
import ResponseModels.RegisterResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import toning.juriaan.Models.ChangePasswordObject;
import toning.juriaan.Models.User;
import toning.juriaan.Models.RegisterObject;


public interface UserWebInterface {

    @POST("/api/Account")
    Call<RegisterResponse> register(@Body RegisterObject registerObject);

    @FormUrlEncoded
    @POST("/token")
    Call<LoginResponse> login(@Field("UserName") String username, @Field("Password") String password, @Field("grant_type") String grant_type);

    @GET("/api/Account")
    Call<List<User>> getUsers(@Header("Authorization") String access_token);

    @POST("/api/Account/ChangePassword")
    Call<ChangePasswordResponse> changePassword(@Header("Authorization") String access_token, @Body ChangePasswordObject changePasswordObject);

    @DELETE("/api/Account/{id}")
    Call<DeleteResponse> deleteUser(@Path("id") String id);
}
