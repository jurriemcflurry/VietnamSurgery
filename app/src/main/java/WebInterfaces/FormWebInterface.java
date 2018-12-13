package WebInterfaces;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import toning.juriaan.Models.FormTemplateObject;

public interface FormWebInterface {

    @POST("/api/Formulieren")
    Call<FormTemplateObject> postFormTemplate(@Body FormTemplateObject body);
}
