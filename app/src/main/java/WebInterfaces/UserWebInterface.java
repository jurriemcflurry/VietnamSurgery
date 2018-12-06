package WebInterfaces;

import ResponseModels.LoginResponse;
import ResponseModels.RegisterResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface UserWebInterface {

    @POST("api/Account") //efficientere body maken met een Register model oid
    Call<RegisterResponse> register(@Body String username, String password, String confirmpassword, String userrole, String email);

    @GET("token")
    Call<LoginResponse> login(@Body String username, String password);
}
