package WebInterfaces;

import ResponseModels.LoginResponse;
import ResponseModels.RegisterResponse;
import ResponseModels.UsersResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import toning.juriaan.vietnamsurgery.LoginObject;
import toning.juriaan.vietnamsurgery.RegisterObject;

public interface UserWebInterface {

    @POST("/api/Account")
    Call<RegisterResponse> register(@Body RegisterObject registerObject);

    @POST("/token")
    Call<LoginResponse> login(@Body LoginObject loginObject);

    @GET("/api/Account")
    Call<UsersResponse> getUsers(@Header("access_token") String access_token);
}
