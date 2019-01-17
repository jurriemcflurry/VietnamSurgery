package webinterfaces;

import java.util.List;

import responsemodels.ChangePasswordResponse;
import responsemodels.DeleteResponse;
import responsemodels.LoginResponse;
import responsemodels.RegisterResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import toning.juriaan.models.ChangePasswordObject;
import toning.juriaan.models.User;
import toning.juriaan.models.RegisterObject;


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
