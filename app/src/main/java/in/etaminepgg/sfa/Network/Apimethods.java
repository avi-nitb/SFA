package in.etaminepgg.sfa.Network;

import org.json.JSONObject;

import in.etaminepgg.sfa.Models.AuthUser_Model;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;


public interface Apimethods {
    @POST(ApiUrl.LOG_URL_AUTHUSER)
    Call<AuthUser_Model> getUserAuthKey(@Body JSONObject jsonObject_for_userdetails);
}

