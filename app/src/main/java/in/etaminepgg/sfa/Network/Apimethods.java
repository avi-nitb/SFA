package in.etaminepgg.sfa.Network;

import in.etaminepgg.sfa.Models.AuthUser_Model;
import in.etaminepgg.sfa.Models.LoginInput_Model;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;


public interface Apimethods
{

    @POST(ApiUrl.LOG_URL_AUTHUSER)
    Call<AuthUser_Model> getUserAuthKey(@Body LoginInput_Model loginInput_model);
}

