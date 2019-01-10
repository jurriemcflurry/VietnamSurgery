package WebInterfaces;

import ResponseModels.FormulierenResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import toning.juriaan.Models.Form;
import toning.juriaan.Models.FormContentUploadModel;
import toning.juriaan.Models.FormTemplateObject;

public interface FormWebInterface {

    @GET("/api/Formulieren")
    Call<FormulierenResponse[]> getFormTemplates();

    @POST("/api/Formulieren")
    Call<Void> postFormTemplate(@Body FormTemplateObject body);

    @POST("/api/FormContent")
    Call<Void> postFormContent(@Body FormContentUploadModel body);
}
