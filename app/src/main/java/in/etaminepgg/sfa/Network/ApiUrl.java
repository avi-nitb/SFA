package in.etaminepgg.sfa.Network;

import in.etaminepgg.sfa.BuildConfig;

/**
 * Created by Jaya on 15-12-2017.
 */

public class ApiUrl
{

    public static final String BASEURL_SKUTHUMBIMAGE = BuildConfig.SERVER_URL+"sites/default/files/images";


    //public static final String BASE_URL = "http://etaminepgg.in/sfa/";

    public static final String BASE_URL = BuildConfig.SERVER_URL;

    public static final String LOG_URL_AUTHUSER = "?q=api/authUser";

    public static final String LOG_URL_LOGOUT= "?q=api/logoutUser ";

    public static final String LOG_URL_GETUSERDETAILS = "?q=api/getUserDetails";

    public static final String LOG_URL_ISVALIDAUTHKEY = "?q=api/isValidAuthKey";

    public static final String LOG_URL_GETBASICCONFIG = "?q=api/getBasicConfig";

    public static final String LOG_URL_GETIS_SKUUPDATED = "?q=api/isSkulistUpdated";

    public static final String LOG_URL_GETSKULISTAFTER = "?q=api/getSkuListAfter";
    public static final String LOG_URL_GETSKUINFO = "?q=api/getSkuInfo";
    public static final String LOG_URL_GETSKUTHUMBIMAGE = "?q=api/getSkuThumbImage";
    public static final String LOG_URL_GETSKUATTR = "?q=api/getSkuAttr";

    public static final String LOG_URL_GETSIMILARSKU = "?q=api/getSimilarSKUsFor";

    public static final String LOG_URL_GETRETAILERLIST= "?q=api/getRetailerList";
    public static final String LOG_URL_GETRETAILERINFO = "?q=api/getRetailerInfo";

    public static final String LOG_URL_PUTRETAILERINFO = "?q=api/putRetailerInfo";

    public static final String LOG_URL_GETRETAILERVISITS = "getRetailerVisits";

    public static final String LOG_URL_PUTRETAILERVISIT = "?q=api/putRetailVisit";

    public static final String LOG_URL_PUTSALESORDERMASTER = "?q=api/putSalesOrderMaster";

    public static final String LOG_URL_GETSALESORDERDETAILS = "?q=api/getSalesOrderDetails";

    public static final String LOG_URL_PUTSALESORDERDETAILS = "?q=api/putSalesOrderDetails";

    public static final String LOG_URL_GETSALESHISTORY = "?q=api/getSalesHistory";

    public static final String LOG_URL_PUTNEWAREA = "?q=api/putNewArea";

    public static final String LOG_URL_GETACTIVEORDER = "?q=api/getActiveOrder";

    public static final String LOG_URL_GETLOCATIONINFO = "?q=api/getLocationsinfo";

    public static final String LOG_URL_CREATERETAILER= "?q=api/createRetailer";

    public static final String LOG_URL_UPDATERETAILERPICTURE= "?q=api/updateRetailerpicture";

    public static final String LOG_URL_GETSKUCATEGORYSUBCATEGORYLIST= "?q=api/getSkuCategoryAndSubcategorylist";

    public static final String LOG_URL_GETNOORDER_REASONLIST= "?q=api/getNoOrderReasonList";

    public static final String LOG_URL_PUTSUMMARYREPORT= "?q=api/putSummaryReport";


}
