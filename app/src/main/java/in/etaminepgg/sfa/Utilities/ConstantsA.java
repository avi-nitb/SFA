package in.etaminepgg.sfa.Utilities;

/**
 * Created by etamine on 3/6/17.
 */

public interface ConstantsA
{
    String NONE = "NONE";

    String NOT_PRESENT = "NOT_PRESENT";

    int ORDER_TYPE_NEW_ORDER = 1;
    int ORDER_TYPE_NEW_REGULAR_ORDER = 2;
    int ORDER_TYPE_NO_ORDER = 3;


    public static int sCorner = 15;
    public static int sMargin = 2;

    String NEW_ORDER = "NEW_ORDER";
    String REGULAR_ORDER = "REGULAR_ORDER";

    String NO_ACTIVE_ORDER = "NO_ACTIVE_ORDER";

    String NEW_SKUs_TAB = "NEW_SKUs_TAB";
    String PROMO_SKUs_TAB = "PROMO_SKUs_TAB";
    String FREQUENT_SKUs_TAB = "FREQUENT_SKUs_TAB";
    String All_SKUs_TAB = "All_SKUs_TAB";

    String INTENT_EXTRA_RETAILER_NAME = "RETAILER_NAME";
    String INTENT_EXTRA_RETAILER_ID = "RETAILER_ID";

    String KEY_SKU_ID = "sku_id";

    String RS = "Rs. ";
    String NO_INTERNET_CONNECTION = "Failed, Please check your internet!";

}
