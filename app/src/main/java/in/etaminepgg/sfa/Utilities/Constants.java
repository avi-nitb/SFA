package in.etaminepgg.sfa.Utilities;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

/**
 * Created by Pustak on 10-02-2016.
 */

public class Constants
{
    public static final int REQUEST_TURN_ON_LOCATION = 112;
    public static final String GET_REQUEST = "GET";
    public static final String POST_REQUEST = "POST";
    public static final String REQUEST_FOR_AUTHKEY = "1000";
    public static final String REQUEST_FOR_USERDETAILS = "1001";
    public static final String REQUEST_FOR_ISVALIDAUTHKEY = "1002";
    public static final String REQUEST_FOR_GETBASICCONFIG = "1003";
    public static final String LOGIN_AUTHTOKEN = "authtoken";
    public static String TBL_RETAILER = "retailers";
    public static String TBL_EMPLOYEE = "employees";
    public static String TBL_SCHEME = "schemes";
    public static String TBL_RETAILER_VISIT = "retailer_visits";
    public static String TBL_AREA = "areas";
    public static String TBL_SALES_ORDER = "sales_orders";
    public static String TBL_SALES_ORDER_DETAILS = "sales_order_details";
    public static String TBL_SKU = "SKUs";
    public static String TBL_ITEM_SCHEME_RELATION = "sku_scheme_relation";
    public static String TBL_GLOBAL_ATTRIBUTES = "global_attributes";
    public static String TBL_SKU_ATTRIBUTE_MAPPING = "sku_attribute_mapping";
    public static String TBL_SALES_ORDER_SKU_ATTRIBUTES = "sales_order_sku_attributes";
    public static String TBL_CONFIG = "config";
    public static String TBL_LOCATION_HIERARCHY = "loc_hierarchy";
    public static String TBL_SKU_CATEGORY = "sfa_sku_category";
    public static String TBL_SKU_SUBCATEGORY = "sfa_sku_sub_category";
    public static String TBL_SKU_NO_ORDERREASON= "sfa_sku_noOrder_Reason";
    public static double LATITUDE = 0.0;
    public static double LONGITUDE = 0.0;
    public static String dbFileFullPath;
    public static int DBI = -1;
    public static SQLiteDatabase DBH = null;
    public static String appSpecificDirectoryPath ;
    public static String IMAGES_DIR = appSpecificDirectoryPath + "/Images";
    public static Bitmap M_BITMAP;
    public static String STR_UPLD_PHOTO_NAME = "";
    public static String IMEI = "";
    public static String JINGLE_FILE_NAME = "jingle.mp3";
    public static String selected_sku_from_history = "ANY";

    public  static final String PROMOTION_REQUIRED = "promotion scheme for retailers";
    public  static final String RETAILER_CREATION_PER_MONTH = "Retailer_addition_per_month";
    public  static final String SKU_ATTRIBUTE_REQUIRED = "Sku-attributes";
    public  static final String SHOW_UPDATE_BUTTON = "show_update_button";
    public  static final String OP_TYPE_SKU_NEW = "N";
    public  static final String OP_TYPE_SKU_MODIFICATION = "M";
    public  static final String MOD_TYPE_SKU_INFO = "properties";
    public  static final String MOD_TYPE_SKU_ATTR = "attributes";
    public  static final String MOD_TYPE_SKU_MULTIMEDIA = "multimedia";
    public  static final String MOD_TYPE_SKU_ATTR_AND_INFO = "attributes,properties";
    public  static final String MOD_TYPE_SKU_INFO_AND_ATTR = "properties,attributes";
    public  static final String MOD_TYPE_SKU_MULTIMEDIA_AND_ATTR = "multimedia,attributes";
    public  static final String MOD_TYPE_SKU_ATTR_AND_MULTIMEDIA = "attributes,multimedia";
    public  static final String MOD_TYPE_SKU_MULTIMEDIA_AND_INFO = "multimedia,properties";
    public  static final String MOD_TYPE_SKU_INFO_AND_MULTIMEDIA = "properties,multimedia";

    public static String  search_skuId="";


}
