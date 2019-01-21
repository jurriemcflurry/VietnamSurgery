package webinterfaces;

import responsemodels.FormulierenResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import toning.juriaan.models.FormContentUploadModel;
import toning.juriaan.models.FormTemplateObject;

public interface FormWebInterface {

    @GET("/api/Formulieren")
    Call<FormulierenResponse[]> getFormTemplates();

    @POST("/api/Formulieren")
    Call<Void> postFormTemplate(@Body FormTemplateObject body);

    @POST("/api/FormContent")
    Call<Void> postFormContent(@Body FormContentUploadModel body);

    @DELETE("/api/Formulieren/{id}")
    Call<Void> deleteForm(@Path("id") int formId);
}
