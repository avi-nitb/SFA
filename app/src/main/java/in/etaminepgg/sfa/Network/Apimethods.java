package in.etaminepgg.sfa.Network;

import in.etaminepgg.sfa.InputModel_For_Network.IM_CreateRetailer;
import in.etaminepgg.sfa.InputModel_For_Network.IM_GetSkuInfo;
import in.etaminepgg.sfa.InputModel_For_Network.IM_IsValidAuthKey;
import in.etaminepgg.sfa.InputModel_For_Network.IM_GetSkuListAfter;
import in.etaminepgg.sfa.InputModel_For_Network.IM_PutNewArea;
import in.etaminepgg.sfa.InputModel_For_Network.IM_PutRetailerInfo;
import in.etaminepgg.sfa.InputModel_For_Network.IM_PutRetailerVisit;
import in.etaminepgg.sfa.InputModel_For_Network.IM_PutSalesOrderMaster;
import in.etaminepgg.sfa.InputModel_For_Network.IM_PutSalesorderDetails;
import in.etaminepgg.sfa.InputModel_For_Network.IM_RetailerInfo;
import in.etaminepgg.sfa.InputModel_For_Network.IM_UpdateRetailerPicture;
import in.etaminepgg.sfa.Models.AuthUserDetails;
import in.etaminepgg.sfa.Models.AuthUser_Model;
import in.etaminepgg.sfa.Models.BasicConfigModel;
import in.etaminepgg.sfa.InputModel_For_Network.IM_Config;
import in.etaminepgg.sfa.InputModel_For_Network.IM_Login;
import in.etaminepgg.sfa.Models.CreateretaileModel;
import in.etaminepgg.sfa.Models.GetSkuAttribute;
import in.etaminepgg.sfa.Models.GetSkuCategorySubCategorylist;
import in.etaminepgg.sfa.Models.GetSkuInfo;
import in.etaminepgg.sfa.Models.GetSkuListAfter;
import in.etaminepgg.sfa.Models.GetSkuThumbImage;
import in.etaminepgg.sfa.Models.IsSkuListUpdate;
import in.etaminepgg.sfa.Models.Location_Model;
import in.etaminepgg.sfa.Models.NoOrderReasonList;
import in.etaminepgg.sfa.Models.PutNewArea;
import in.etaminepgg.sfa.Models.PutRetailerInfo_Model;
import in.etaminepgg.sfa.Models.PutSalesOrderDetails;
import in.etaminepgg.sfa.Models.PutSalesOrderMaster;
import in.etaminepgg.sfa.Models.RetailerInfo_Model;
import in.etaminepgg.sfa.Models.RetailerList_Model;
import in.etaminepgg.sfa.Models.UpdateRetailerpictureModel;
import in.etaminepgg.sfa.Models.ValidAuthModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


public interface Apimethods
{

    @POST(ApiUrl.LOG_URL_AUTHUSER)
    Call<AuthUser_Model> getUserAuthKey(@Body IM_Login IMLogin);


  /*  @FormUrlEncoded
    @POST(ApiUrl.LOG_URL_AUTHUSER)
    Call<AuthUser_Model> getUserAuthKey(@Field("IMEI") String IMEI,@Field("company_id") String company_id,@Field("password") String password,@Field("username") String username);
*/
    @POST(ApiUrl.LOG_URL_LOGOUT)
    Call<ValidAuthModel> setLogout(@Body IM_IsValidAuthKey im_isValidAuthKey);

    @POST(ApiUrl.LOG_URL_GETBASICCONFIG)
    Call<BasicConfigModel> sendBasicConfig(@Body IM_Config IM_config);

    @POST(ApiUrl.LOG_URL_GETUSERDETAILS)
    Call<AuthUserDetails> getUserDetails(@Body IM_IsValidAuthKey im_isValidAuthKey);

    @POST(ApiUrl.LOG_URL_ISVALIDAUTHKEY)
    Call<ValidAuthModel> isValidAuthKey(@Body IM_IsValidAuthKey IM_isValidAuthKey);

    @POST(ApiUrl.LOG_URL_GETIS_SKUUPDATED)
    Call<IsSkuListUpdate> isSkuListUpdated(@Body IM_GetSkuListAfter IM_getSkuListAfter);


    @POST(ApiUrl.LOG_URL_GETSKULISTAFTER)
    Call<GetSkuListAfter> getSkuListAfter(@Body IM_GetSkuListAfter IM_getSkuListAfter);

    @POST(ApiUrl.LOG_URL_GETSKUINFO)
    Call<GetSkuInfo> getSkuInfo(@Body IM_GetSkuInfo im_getSkuInfo);

    @POST(ApiUrl.LOG_URL_GETSKUATTR)
    Call<GetSkuAttribute> getSkuAttr(@Body IM_GetSkuInfo im_getSkuInfo_attr);

    @POST(ApiUrl.LOG_URL_GETSKUTHUMBIMAGE)
    Call<GetSkuThumbImage> getSkuThumbImage(@Body IM_GetSkuInfo im_getSkuInfo_thumbimage);

    @POST(ApiUrl.LOG_URL_GETRETAILERLIST)
    Call<RetailerList_Model> getRetailerList(@Body IM_IsValidAuthKey authKey);

    @POST(ApiUrl.LOG_URL_GETRETAILERINFO)
    Call<RetailerInfo_Model> getRetailerInfo(@Body IM_RetailerInfo im_retailerInfo);


    @POST(ApiUrl.LOG_URL_PUTRETAILERINFO)
    Call<PutRetailerInfo_Model> putRetailerInfo(@Body IM_PutRetailerInfo im_putRetailerInfo);

    @POST(ApiUrl.LOG_URL_GETSIMILARSKU)
    Call<GetSkuListAfter> getSimilarSkuList(@Body IM_GetSkuInfo im_getSkuInfo_attr);

    @POST(ApiUrl.LOG_URL_PUTRETAILERVISIT)
    Call<PutRetailerInfo_Model> putRetailerVisit(@Body IM_PutRetailerVisit im_putRetailerVisit);

    @POST(ApiUrl.LOG_URL_PUTSALESORDERDETAILS)
    Call<PutSalesOrderDetails> putSalesOrderDetails(@Body IM_PutSalesorderDetails im_putSalesorderDetails);

    @POST(ApiUrl.LOG_URL_PUTSALESORDERMASTER)
    Call<PutSalesOrderMaster> putSalesOrdermaster(@Body IM_PutSalesOrderMaster im_putSalesOrderMaster);

    @POST(ApiUrl.LOG_URL_GETLOCATIONINFO)
    Call<Location_Model> getLocationinfo(@Body IM_IsValidAuthKey im_isValidAuthKey);

    @POST(ApiUrl.LOG_URL_CREATERETAILER)
    Call<CreateretaileModel> createretailer(@Body IM_CreateRetailer im_createRetailer);

    @POST(ApiUrl.LOG_URL_UPDATERETAILERPICTURE)
    Call<UpdateRetailerpictureModel> updateRetailerPicture(@Body IM_UpdateRetailerPicture im_updateRetailerPicture);

    @POST(ApiUrl.LOG_URL_PUTNEWAREA)
    Call<PutNewArea> putNewArea(@Body IM_PutNewArea im_putNewArea);

    @POST(ApiUrl.LOG_URL_GETSKUCATEGORYSUBCATEGORYLIST)
    Call<GetSkuCategorySubCategorylist> getSkuCategorySubCategorylist(@Body IM_IsValidAuthKey im_isValidAuthKey);


    @POST(ApiUrl.LOG_URL_GETNOORDER_REASONLIST)
    Call<NoOrderReasonList> getNoOrderReasonList(@Body IM_IsValidAuthKey im_isValidAuthKey);
}

