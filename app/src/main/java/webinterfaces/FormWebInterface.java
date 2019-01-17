package webinterfaces;

import responsemodels.FormulierenResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import toning.juriaan.models.FormContentUploadModel;
import toning.juriaan.models.FormTemplateObject;

public interface FormWebInterface {

    @GET("/api/Formulieren")
    Call<FormulierenResponse[]> getFormTemplates();

    @POST("/api/Formulieren")
    Call<Void> postFormTemplate(@Body FormTemplateObject body);

    @POST("/api/FormContent")
    Call<Void> postFormContent(@Body FormContentUploadModel body);
}
