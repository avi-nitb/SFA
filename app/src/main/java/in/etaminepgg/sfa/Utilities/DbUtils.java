package in.etaminepgg.sfa.Utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import in.etaminepgg.sfa.Activities.LoginActivity;
import in.etaminepgg.sfa.InputModel_For_Network.LocationUploadFromDb;
import in.etaminepgg.sfa.InputModel_For_Network.RetailerUpdateForUploadFromDb;
import in.etaminepgg.sfa.InputModel_For_Network.RetailerVisitUploadFromDb;
import in.etaminepgg.sfa.InputModel_For_Network.RtlrrUpldModelWithMobileRtlrId;
import in.etaminepgg.sfa.InputModel_For_Network.SalesOrderDetailsUploadFromDb;
import in.etaminepgg.sfa.InputModel_For_Network.SalesOrderFromDbForUpload;
import in.etaminepgg.sfa.Models.CreateretaileModel;
import in.etaminepgg.sfa.Models.PutNewArea;
import in.etaminepgg.sfa.Models.QuantityDiscountModel;
import in.etaminepgg.sfa.Models.RetailerDetailsForView;
import in.etaminepgg.sfa.Models.SalesOrderSku;
import in.etaminepgg.sfa.Models.Sku;
import in.etaminepgg.sfa.Models.SkuGroupHistory;

import static in.etaminepgg.sfa.Utilities.Constants.TBL_LOCATION_HIERARCHY;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_RETAILER;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_RETAILER_VISIT;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SALES_ORDER;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SALES_ORDER_DETAILS;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SKU;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SKU_CATEGORY;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SKU_NO_ORDERREASON;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SKU_SUBCATEGORY;
import static in.etaminepgg.sfa.Utilities.Constants.dbFileFullPath;
import static in.etaminepgg.sfa.Utilities.ConstantsA.NONE;
import static in.etaminepgg.sfa.Utilities.Utils.getDateTime;
import static in.etaminepgg.sfa.Utilities.Utils.getIST;
import static in.etaminepgg.sfa.Utilities.Utils.getTodayDate;

/**
 * Created by etamine on 27/7/17.
 */

 /*Log.e("dbFileFullPath", dbFileFullPath);
 Log.e("valueFromOpenDatabase", String.valueOf(valueFromOpenDatabase));*/

public class DbUtils
{
    //copy pasted from SalesSummary Activity
    public static int getRetailerVisitsFor(String salesPersonId)
    {
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String SQL_SELECT_RETAILER_VISITS_OF_SALES_PERSON =
                "SELECT" + " visit_id " + "FROM " + TBL_RETAILER_VISIT + " WHERE " + "emp_id " + "= ? AND " + "visit_date " + "like ? " + "GROUP BY mobile_retailer_id";

       /* String SQL_SELECT_RETAILER_VISITS_OF_SALES_PERSON =
                "SELECT" + " visit_id " + "FROM " + TBL_RETAILER_VISIT + " WHERE " + "emp_id " + "= ? AND " + "visit_date " + "like ? " ;*/
        String[] selectionArgs = {salesPersonId, getTodayDate() + "%"};

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_RETAILER_VISITS_OF_SALES_PERSON, selectionArgs);
        int noOfVisitsMadeBySalesPerson = cursor.getCount();
        cursor.close();
        sqLiteDatabase.close();

        return noOfVisitsMadeBySalesPerson;
    }

    //copy pasted from SalesSummary Activity for duplicate retailer visits
    public static int getAllRetailerVisitsFor(String salesPersonId)
    {
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

       /* String SQL_SELECT_RETAILER_VISITS_OF_SALES_PERSON =
                "SELECT" + " visit_id " + "FROM " + TBL_RETAILER_VISIT + " WHERE " + "emp_id " + "= ? AND " + "visit_date " + "like ? " + "GROUP BY retailer_id";*/

        String SQL_SELECT_RETAILER_VISITS_OF_SALES_PERSON =
                "SELECT" + " visit_id " + "FROM " + TBL_RETAILER_VISIT + " WHERE " + "emp_id " + "= ? AND " + "visit_date " + "like ? ";
        String[] selectionArgs = {salesPersonId, getTodayDate() + "%"};

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_RETAILER_VISITS_OF_SALES_PERSON, selectionArgs);
        int noOfVisitsMadeBySalesPerson = cursor.getCount();
        cursor.close();
        sqLiteDatabase.close();

        return noOfVisitsMadeBySalesPerson;
    }

    //copy pasted from SalesSummary Activity for  retailer visits according to location
    public static int getRetailerVisitsForLocationId(String salesPersonId, String locationId)
    {
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

       /* String SQL_SELECT_RETAILER_VISITS_OF_SALES_PERSON =
                "SELECT" + " visit_id " + "FROM " + TBL_RETAILER_VISIT + " WHERE " + "emp_id " + "= ? AND " + "visit_date " + "like ? " + "GROUP BY retailer_id";*/

        String SQL_SELECT_RETAILER_VISITS_OF_SALES_PERSON =
                "SELECT" + " retailer_visit.visit_id " + "FROM " + TBL_RETAILER + " retailer " + " INNER JOIN " + TBL_RETAILER_VISIT + " retailer_visit " + " ON retailer.mobile_retailer_id=retailer_visit.mobile_retailer_id " + " WHERE " + "retailer_visit.emp_id " + "= ? AND " + "retailer_visit.visit_date " + "like ? AND retailer.area_id = ? ";
        String[] selectionArgs = {salesPersonId, getTodayDate() + "%", locationId};

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_RETAILER_VISITS_OF_SALES_PERSON, selectionArgs);
        int noOfVisitsMadeBySalesPerson = cursor.getCount();
        cursor.close();
        sqLiteDatabase.close();

        return noOfVisitsMadeBySalesPerson;
    }


    public static void makeCurrentActiveOrderInactive(String mobileActiveOrderId)
    {
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);
        ContentValues salesOrderValues = new ContentValues();
        salesOrderValues.put("is_active", "0");
        sqLiteDatabase.update(TBL_SALES_ORDER, salesOrderValues, "is_active = ? AND emp_id = ? AND mobile_order_id = ?", new String[]{"1", new MySharedPrefrencesData().getUser_Id(LoginActivity.baseContext), mobileActiveOrderId});

        sqLiteDatabase.close();
    }


    //check active order status of retailer, if active order exists


    //for new isNewOrRegular=0
    //for regular isNewOrRegular=1

    public static String getActiveOrderStatusOfRetailer(String retailerId, String mobileRetailerID, String isNewOrRegular)
    {
        String activeOrderID = ConstantsA.NONE;
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String SQL_SELECT_ACTIVE_SALES_ORDER_ID = "select mobile_order_id from " + TBL_SALES_ORDER + " WHERE " + "is_active = ? AND emp_id = ? AND retailer_id = ? AND mobile_retailer_id = ? AND is_regular = ?";
        String[] selectionArgs = new String[]{"1", new MySharedPrefrencesData().getUser_Id(LoginActivity.baseContext), retailerId, mobileRetailerID, isNewOrRegular};
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_ACTIVE_SALES_ORDER_ID, selectionArgs);

        if (cursor.moveToFirst())
        {
            activeOrderID = cursor.getString(cursor.getColumnIndexOrThrow("mobile_order_id"));

        }

        cursor.close();
        sqLiteDatabase.close();

        return activeOrderID;
    }

    //returns order_id_count of active order, if active order exists

    public static int getActiveOrderIDCount()
    {
        int activeOrderID = -1;
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String SQL_SELECT_ACTIVE_SALES_ORDER_ID = "select COUNT(*) count from " + TBL_SALES_ORDER + " WHERE " + "is_active = ? AND emp_id = ?";
        String[] selectionArgs = new String[]{"1", new MySharedPrefrencesData().getUser_Id(LoginActivity.baseContext)};
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_ACTIVE_SALES_ORDER_ID, selectionArgs);

        if (cursor.moveToNext())
        {
            activeOrderID = cursor.getInt(cursor.getColumnIndexOrThrow("count"));
        }

        cursor.close();
        sqLiteDatabase.close();

        return activeOrderID;
    }

    //returns order_id of active order, if active order exists
    //returns getActiveOrderID() if there is no active order
    public static String getActiveOrderID(String retailerId, String mobileRetailerId, String isNewRegular)
    {
        String activeOrderID = NONE;
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String SQL_SELECT_ACTIVE_SALES_ORDER_ID = "select order_id from " + TBL_SALES_ORDER + " WHERE " + "is_active = ? AND emp_id = ?  AND retailer_id = ? AND mobile_retailer_id = ? AND is_regular = ?";
        String[] selectionArgs = new String[]{"1", new MySharedPrefrencesData().getUser_Id(LoginActivity.baseContext), retailerId, mobileRetailerId, isNewRegular};
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_ACTIVE_SALES_ORDER_ID, selectionArgs);

        if (cursor.moveToFirst())
        {
            activeOrderID = cursor.getString(cursor.getColumnIndexOrThrow("order_id"));
        }

        cursor.close();
        sqLiteDatabase.close();

        return activeOrderID;
    }


    //returns mobile_order_id of active order, if active order exists
    //returns getActiveOrderID() if there is no active order
    public static String getActiveMobileOrderID(String retailerId, String mobileRetailerId, String isNewRegular)
    {
        String mobileActiveOrderID = NONE;
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String SQL_SELECT_ACTIVE_SALES_ORDER_ID = "select mobile_order_id from " + TBL_SALES_ORDER + " WHERE " + "is_active = ? AND emp_id = ? AND retailer_id = ? AND mobile_retailer_id = ? AND is_regular = ?";
        String[] selectionArgs = new String[]{"1", new MySharedPrefrencesData().getUser_Id(LoginActivity.baseContext), retailerId, mobileRetailerId, isNewRegular};
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_ACTIVE_SALES_ORDER_ID, selectionArgs);

        if (cursor.moveToFirst())
        {
            mobileActiveOrderID = cursor.getString(cursor.getColumnIndexOrThrow("mobile_order_id"));
        }

        cursor.close();
        sqLiteDatabase.close();

        return mobileActiveOrderID;
    }


    //returns uploadStatus of active order, if active order exists

    public static String getActiveOrderUploadStatus()
    {
        String uploadStatus = NONE;
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String SQL_SELECT_ACTIVE_SALES_ORDER_ID = "select upload_status from " + TBL_SALES_ORDER + " WHERE " + "is_active = ? AND emp_id = ?";
        String[] selectionArgs = new String[]{"1", new MySharedPrefrencesData().getUser_Id(LoginActivity.baseContext)};
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_ACTIVE_SALES_ORDER_ID, selectionArgs);

        if (cursor.moveToFirst())
        {
            uploadStatus = cursor.getString(cursor.getColumnIndexOrThrow("upload_status"));
        }

        cursor.close();
        sqLiteDatabase.close();

        return uploadStatus;
    }

    //returns order_date  of active order, if active order exists
    //returns getActiveOrderID() if there is no active order
    public static String getActiveOrderIDDate(String activeOrderId)
    {
        String activeOrderDate = NONE;
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String SQL_SELECT_ACTIVE_SALES_ORDER_ID = "select order_date from " + TBL_SALES_ORDER + " WHERE " + "is_active = ? AND emp_id = ? AND order_id = ?";
        String[] selectionArgs = new String[]{"1", new MySharedPrefrencesData().getUser_Id(LoginActivity.baseContext), activeOrderId};
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_ACTIVE_SALES_ORDER_ID, selectionArgs);

        if (cursor.moveToFirst())
        {
            activeOrderDate = cursor.getString(cursor.getColumnIndexOrThrow("order_date"));
        }

        cursor.close();
        sqLiteDatabase.close();

        return activeOrderDate;
    }


    public static int getNoOfSalesOrdersForsize()
    {

        int noOfSalesOrders = 0;

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        Cursor cursor = sqLiteDatabase.rawQuery("SELECT COUNT(*) FROM " + Constants.TBL_SKU_NO_ORDERREASON, null);

        cursor.moveToFirst();

        noOfSalesOrders = cursor.getInt(0);

        cursor.close();
        sqLiteDatabase.close();
        return noOfSalesOrders;
    }


    public static int getSkuRowsSize()
    {

        int noOfSkuRows = 0;

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        Cursor cursor = sqLiteDatabase.rawQuery("SELECT COUNT(*) FROM " + Constants.TBL_SKU, null);

        cursor.moveToFirst();

        noOfSkuRows = cursor.getInt(0);

        cursor.close();
        sqLiteDatabase.close();
        return noOfSkuRows;
    }


    //delete order
    public static void deleteOrder(String activeOrderId)
    {

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String SQL_SELECT_ACTIVE_SALES_ORDER_ID = "SELECT order_date,mobile_visit_id from " + TBL_SALES_ORDER + " WHERE " + "is_active = ? AND emp_id = ? AND mobile_order_id = ?";
        String[] selectionArgs = new String[]{"1", new MySharedPrefrencesData().getUser_Id(LoginActivity.baseContext), activeOrderId};
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_ACTIVE_SALES_ORDER_ID, selectionArgs);

        Log.d("countfordelete", String.valueOf(cursor.getCount()));

        if (cursor.moveToFirst())
        {

            sqLiteDatabase.delete(TBL_RETAILER_VISIT, "mobile_visit_id = ?", new String[]{cursor.getString(cursor.getColumnIndexOrThrow("mobile_visit_id"))});

        }

        sqLiteDatabase.delete(TBL_SALES_ORDER_DETAILS, "mobile_order_id = ?", new String[]{activeOrderId});
        sqLiteDatabase.delete(TBL_SALES_ORDER, "mobile_order_id = ?", new String[]{activeOrderId});

        cursor.close();
        sqLiteDatabase.close();


    }

    //returns Retailer's Regular Order Id
    //returns NONE if there is no regular order
    public static String getRegularOrderIdFor(String retailerID)
    {
        String regularOrderID = NONE;
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);
        String SQL_SELECT_REGULAR_ORDER_ID = "SELECT " + "mobile_order_id" + " FROM " + TBL_SALES_ORDER + " WHERE " + "mobile_retailer_id = ? AND " + "is_regular = ? AND emp_id = ?";
        String[] selectionArgs = {retailerID, "1", new MySharedPrefrencesData().getUser_Id(LoginActivity.baseContext)};

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_REGULAR_ORDER_ID, selectionArgs);
        if (cursor.moveToFirst())
        {
            regularOrderID = cursor.getString(cursor.getColumnIndexOrThrow("mobile_order_id"));
        }
        cursor.close();
        sqLiteDatabase.close();

        return regularOrderID;
    }

    //returns Retailer's ID for current active order
    //returns NONE if there is no active order
    public static String getRetailerID()
    {
        String retailerID = NONE;
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);
        String SQL_SELECT_RETAILER_ID_OF_ACTIVE_ORDER = "SELECT " + "retailer_id" + " FROM " + TBL_SALES_ORDER + " WHERE " + "is_active = ? AND emp_id = ?";
        String[] selectionArgs = {"1", new MySharedPrefrencesData().getUser_Id(LoginActivity.baseContext)};

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_RETAILER_ID_OF_ACTIVE_ORDER, selectionArgs);
        if (cursor.moveToFirst())
        {
            retailerID = cursor.getString(cursor.getColumnIndexOrThrow("retailer_id"));
        }
        cursor.close();
        sqLiteDatabase.close();

        return retailerID;
    }


    //return retailer name according to retailer id

    public static String getRetailerNameAccordingToRetailerId(String retailerid)
    {
        String retailername = NONE;
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);
        String SQL_SELECT_RETAILER_ID_OF_ACTIVE_ORDER = "SELECT " + "retailer_name" + " FROM " + TBL_RETAILER + " WHERE " + "mobile_retailer_id = ?";
        String[] selectionArgs = {retailerid};

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_RETAILER_ID_OF_ACTIVE_ORDER, selectionArgs);
        if (cursor.moveToFirst())
        {
            retailername = cursor.getString(cursor.getColumnIndexOrThrow("retailer_name"));
        }
        cursor.close();
        sqLiteDatabase.close();

        return retailername;
    }

    public static long insertIntoSalesOrderDetailsTable(Context context, String orderID, String mobileOrderID, String skuID, String skuName, String skuPrice, String skuQty, String skuFreeQty, String skuDiscount)
    {
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        float skuPriceInt = Float.parseFloat(skuPrice);
        int skuQtyInt = Integer.parseInt(skuQty);
        String skuPrice_beforeDiscount = String.valueOf(skuPriceInt * skuQtyInt);
        String skuFinalPrice = String.valueOf((skuPriceInt * skuQtyInt) - Float.parseFloat(skuDiscount));

        ContentValues salesOrderDetailsValues = new ContentValues();
        salesOrderDetailsValues.put("order_id", orderID);
        salesOrderDetailsValues.put("mobile_order_id", mobileOrderID);
        salesOrderDetailsValues.put("sku_id", skuID);
        salesOrderDetailsValues.put("sku_name", skuName);
        salesOrderDetailsValues.put("sku_price", skuPrice);
        salesOrderDetailsValues.put("sku_qty", skuQty);
        salesOrderDetailsValues.put("sku_free_qty", skuFreeQty);
        salesOrderDetailsValues.put("sku_discount", skuDiscount);
        salesOrderDetailsValues.put("sku_price_before_discount", skuPrice_beforeDiscount);
        salesOrderDetailsValues.put("sku_final_price", skuFinalPrice);
        salesOrderDetailsValues.put("upload_status", 0);
        salesOrderDetailsValues.put("emp_id", new MySharedPrefrencesData().getUser_Id(context));
        salesOrderDetailsValues.put("order_date", getDateTime());
        //salesOrderDetailsValues.put("order_date", "2018-08-05 14:38:46.004");

        long rowID = sqLiteDatabase.insert(TBL_SALES_ORDER_DETAILS, null, salesOrderDetailsValues);

        sqLiteDatabase.close();

        return rowID;
    }

    // This method has no return type compared to insertIntoSalesOrderDetailsTable()
    public static void insertIntoSalesOrderDetails(String orderID, String skuID, String skuName, String skuPrice, String skuQty)
    {
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        int skuPriceInt = Integer.parseInt(skuPrice);
        int skuQtyInt = Integer.parseInt(skuQty);
        String skuFinalPrice = String.valueOf(skuPriceInt * skuQtyInt);

        ContentValues salesOrderDetailsValues = new ContentValues();
        salesOrderDetailsValues.put("order_id", orderID);
        salesOrderDetailsValues.put("sku_id", skuID);
        salesOrderDetailsValues.put("sku_name", skuName);
        salesOrderDetailsValues.put("sku_price", skuPrice);
        salesOrderDetailsValues.put("sku_qty", skuQty);
        salesOrderDetailsValues.put("sku_final_price", skuFinalPrice);
        salesOrderDetailsValues.put("upload_status", 0);
        sqLiteDatabase.insert(TBL_SALES_ORDER_DETAILS, null, salesOrderDetailsValues);

        sqLiteDatabase.close();
    }

    static boolean isSkuPresent(String skuID, String salesOrderID, String mobileSalesOrderID)
    {
        boolean isSkuPresent = false;

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String SQL_SELECT_SKU_FROM_SALES_ORDER_DETAILS =
                "SELECT" + " sku_id " + "FROM " + TBL_SALES_ORDER_DETAILS + " WHERE " + "order_id " + "= ? AND " + "sku_id " + "= ? " + " AND mobile_order_id = ? ";
        String[] selectionArgs = {salesOrderID, skuID, mobileSalesOrderID};
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_SKU_FROM_SALES_ORDER_DETAILS, selectionArgs);

        if (cursor.moveToFirst())
        {
            isSkuPresent = true;
        }

        cursor.close();
        sqLiteDatabase.close();

        return isSkuPresent;
    }

    public static ArrayList<QuantityDiscountModel> getSkuQuantityDiscount(Long orderDetailID)
    {
        ArrayList<QuantityDiscountModel> discountModelArrayList = new ArrayList<>();

        int skuQuantity = -100;
        int skuFreeQuantity = -101;
        float skuDiscount = 0;
        float skuPrice = 0;

        String sqlQuery = "SELECT sku_price,sku_qty,sku_free_qty,sku_discount FROM sales_order_details WHERE order_detail_id = ? ;";
        String[] selectionArgs = {String.valueOf(orderDetailID)};

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        Cursor cursor = sqLiteDatabase.rawQuery(sqlQuery, selectionArgs);

        if (cursor.moveToNext())
        {
            skuQuantity = cursor.getInt(cursor.getColumnIndexOrThrow("sku_qty"));
            skuFreeQuantity = cursor.getInt(cursor.getColumnIndexOrThrow("sku_free_qty"));
            skuDiscount = cursor.getFloat(cursor.getColumnIndexOrThrow("sku_discount"));
            skuPrice = cursor.getFloat(cursor.getColumnIndexOrThrow("sku_price"));
        }

        discountModelArrayList.add(new QuantityDiscountModel(skuQuantity, skuFreeQuantity, skuDiscount, skuPrice));

        cursor.close();
        sqLiteDatabase.close();

        return discountModelArrayList;
    }

    public static void increaseSkuQuantityFreeQuantityDiscount(Long orderDetailID, float skuUnitPrice, int newSkuQuantity, int newFreeQty, float newSkuDisc)
    {
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);


        String skuPrice_beforeDiscount = String.valueOf(skuUnitPrice * newSkuQuantity);
        String skuFinalPrice = String.valueOf(Float.parseFloat(skuPrice_beforeDiscount) - newSkuDisc);

        ContentValues salesOrderDetailsValues = new ContentValues();
        salesOrderDetailsValues.put("sku_qty", newSkuQuantity);
        salesOrderDetailsValues.put("sku_free_qty", newFreeQty);
        salesOrderDetailsValues.put("sku_discount", newSkuDisc);
        salesOrderDetailsValues.put("sku_price_before_discount", skuPrice_beforeDiscount);
        salesOrderDetailsValues.put("sku_final_price", skuFinalPrice);

        sqLiteDatabase.update(TBL_SALES_ORDER_DETAILS, salesOrderDetailsValues, "order_detail_id = ?", new String[]{String.valueOf(orderDetailID)});
        sqLiteDatabase.close();
    }

    public static List<Sku> getSkuList(String sqlQuery, String[] selectionArgs)
    {
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        Cursor cursor = sqLiteDatabase.rawQuery(sqlQuery, selectionArgs);

        List<Sku> skuList = new ArrayList<>();

        while (cursor.moveToNext())
        {
            String skuID = cursor.getString(cursor.getColumnIndexOrThrow("sku_id"));
            String skuName = cursor.getString(cursor.getColumnIndexOrThrow("sku_name"));
            String skuPrice = cursor.getString(cursor.getColumnIndexOrThrow("sku_price"));
            String skuCategory = cursor.getString(cursor.getColumnIndexOrThrow("sku_category"));
            String sku_category_description = cursor.getString(cursor.getColumnIndexOrThrow("sku_category_description"));
            String sku_photo_source = cursor.getString(cursor.getColumnIndexOrThrow("sku_photo_source"));

            skuList.add(new Sku(skuID, skuName, skuPrice, skuCategory, sku_category_description, sku_photo_source));
        }

        cursor.close();
        sqLiteDatabase.close();

        return skuList;
    }

    public static List<Sku> getSkuList_Category(String sqlQuery, String[] selectionArgs)
    {
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        Cursor cursor = sqLiteDatabase.rawQuery(sqlQuery, selectionArgs);

        List<Sku> skuList = new ArrayList<>();

        while (cursor.moveToNext())
        {
            String skuID = cursor.getString(cursor.getColumnIndexOrThrow("sku_id"));
            String skuName = cursor.getString(cursor.getColumnIndexOrThrow("sku_name"));
            String skuPrice = cursor.getString(cursor.getColumnIndexOrThrow("sku_price"));
            String skuCategory = cursor.getString(cursor.getColumnIndexOrThrow("sku_category"));
            String sku_category_description = cursor.getString(cursor.getColumnIndexOrThrow("sku_category_description"));
            String sku_photo_source = cursor.getString(cursor.getColumnIndexOrThrow("sku_photo_source"));

            skuList.add(new Sku(skuID, skuName, skuPrice, skuCategory, sku_category_description, sku_photo_source));
        }

        cursor.close();
        sqLiteDatabase.close();

        return skuList;
    }

    //same as getSkuList() but it doesn't fetch category
    public static List<Sku> getSkuList2(String sqlQuery, String[] selectionArgs)
    {
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        Cursor cursor = sqLiteDatabase.rawQuery(sqlQuery, selectionArgs);

        List<Sku> skuList = new ArrayList<>();

        while (cursor.moveToNext())
        {
            String skuID = cursor.getString(cursor.getColumnIndexOrThrow("sku_id"));
            String skuName = cursor.getString(cursor.getColumnIndexOrThrow("sku_name"));
            String skuPrice = cursor.getString(cursor.getColumnIndexOrThrow("sku_price"));
            //String skuCategory = cursor.getString(cursor.getColumnIndexOrThrow("sku_category"));

            skuList.add(new Sku(skuID, skuName, skuPrice));
        }

        cursor.close();
        sqLiteDatabase.close();

        return skuList;
    }

    public static List<SalesOrderSku> getSKUsInSalesOrder(String sqlQuery, String[] selectionArgs)
    {
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        Cursor cursor = sqLiteDatabase.rawQuery(sqlQuery, selectionArgs);

        List<SalesOrderSku> skuList = new ArrayList<>();

        while (cursor.moveToNext())
        {
            long orderDetailId = cursor.getLong(cursor.getColumnIndexOrThrow("order_detail_id"));
            String skuID = cursor.getString(cursor.getColumnIndexOrThrow("sku_id"));
            String skuName = cursor.getString(cursor.getColumnIndexOrThrow("sku_name"));
            String skuPrice = cursor.getString(cursor.getColumnIndexOrThrow("sku_price"));
            String skuQty = cursor.getString(cursor.getColumnIndexOrThrow("sku_qty"));
            String sku_free_qty = cursor.getString(cursor.getColumnIndexOrThrow("sku_free_qty"));
            String sku_discount = cursor.getString(cursor.getColumnIndexOrThrow("sku_discount"));
            String sku_price_before_discount = cursor.getString(cursor.getColumnIndexOrThrow("sku_price_before_discount"));
            String sku_final_price = cursor.getString(cursor.getColumnIndexOrThrow("sku_final_price"));

            skuList.add(new SalesOrderSku(orderDetailId, skuID, skuName, skuPrice, skuQty, sku_free_qty, sku_discount, sku_price_before_discount, sku_final_price));
        }

        cursor.close();
        sqLiteDatabase.close();

        return skuList;
    }

    public static float getOrderTotal(String orderID, String mobileOrderId)
    {
        float orderTotal = -1;
        String SQL_SELECT_ORDER_TOTAL = "SELECT SUM(sku_final_price) total FROM sales_order_details WHERE order_id = ? AND  mobile_order_id = ? ;";
        String[] selectionArgs = {orderID, mobileOrderId};

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_ORDER_TOTAL, selectionArgs);

        if (cursor.moveToNext())
        {
            orderTotal = cursor.getFloat(cursor.getColumnIndexOrThrow("total"));
        }

        cursor.close();
        sqLiteDatabase.close();
        return orderTotal;
    }


    public static float getOrderTotalFromSalesOrderTBL(String orderID)
    {
        float orderTotal = -1;
        String SQL_SELECT_ORDER_TOTAL = "SELECT total_order_value as total FROM " + TBL_SALES_ORDER + " WHERE mobile_order_id = ? ;";
        String[] selectionArgs = {orderID};

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_ORDER_TOTAL, selectionArgs);

        if (cursor.moveToNext())
        {
            orderTotal = cursor.getFloat(cursor.getColumnIndexOrThrow("total"));
        }

        cursor.close();
        sqLiteDatabase.close();
        //orderTotal=(float)orderTotal;
        return orderTotal;
    }

    public static float getOrderTotal_beforeDiscount(String orderID)
    {
        float orderTotal = -1;
        String SQL_SELECT_ORDER_TOTAL = "SELECT SUM(sku_price_before_discount) total FROM sales_order_details WHERE order_id = ? ;";
        String[] selectionArgs = {orderID};

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_ORDER_TOTAL, selectionArgs);

        if (cursor.moveToNext())
        {
            orderTotal = cursor.getInt(cursor.getColumnIndexOrThrow("total"));
        }

        cursor.close();
        sqLiteDatabase.close();
        orderTotal = (float) orderTotal;
        return orderTotal;
    }

    public static int getItemCount(String orderID, String mobileorderId)
    {
        int itemCount = -1;
        String SQL_SELECT_ITEM_COUNT = "SELECT COUNT(*) count FROM sales_order_details WHERE order_id = ? OR mobile_order_id = ?;";
        String[] selectionArgs = {orderID, mobileorderId};

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_ITEM_COUNT, selectionArgs);

        if (cursor.moveToNext())
        {
            itemCount = cursor.getInt(cursor.getColumnIndexOrThrow("count"));
        }

        cursor.close();
        sqLiteDatabase.close();

        return itemCount;
    }

    public static void insertIntoLocationHierarchy(int locationID, String locationName, String hierarchyLevel, String full_hier_level, String parentLocationID, String uploadstatus)
    {
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        ContentValues locationValues = new ContentValues();
        locationValues.put("loc_id", locationID);
        locationValues.put("mobile_location_id", "LOC_" + getIST() + "_" + locationID);
        locationValues.put("loc_name", locationName);
        locationValues.put("hier_level", hierarchyLevel);
        locationValues.put("full_hier_level", full_hier_level);
        locationValues.put("parent_loc_id", parentLocationID);
        locationValues.put("upload_status", uploadstatus);

        sqLiteDatabase.insert(TBL_LOCATION_HIERARCHY, null, locationValues);

        sqLiteDatabase.close();
    }


    public static boolean isAttribute_IDPresentInDb(String attr_id)
    {
        boolean isattrPresent = false;
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(MyDb.openDatabase(Constants.dbFileFullPath));
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT attribute_id FROM " + Constants.TBL_GLOBAL_ATTRIBUTES + " WHERE attribute_id = ? ", new String[]{attr_id});
        if (cursor.moveToFirst())
        {
            isattrPresent = true;
        }
        cursor.close();
        sqLiteDatabase.close();
        return isattrPresent;
    }

    //don't make this method static. Will not work inside ViewHolders
    //Note: ViewHolders are inner classes of Adapters
    /*public void addToSalesOrderOrPickRetailer(String skuID, String skuName, String skuPrice, Context context)
    {
        String salesOrderID = getActiveOrderID();

        if (!salesOrderID.equals(NONE))
        {
            String skuQuantity = getSkuQuantity(salesOrderID, skuID);

            //if Sku doesn't exists in the sales order details table, insert new row for that Sku
            if (skuQuantity.equals(NONE))
            {
                insertIntoSalesOrderDetailsTable(salesOrderID, skuID, skuName, skuPrice);
            }
            //if Sku already exists in sales order, increase quantity
            else
            {
                int currentSkuQuantity = Integer.parseInt(skuQuantity);
                String newSkuQuantity = String.valueOf(currentSkuQuantity + 1);

                increaseSkuQuantity(salesOrderID, skuID, newSkuQuantity);
            }

            Utils.showToast(context, skuName + "(id: " + skuID + ")" + "added to sales order");
        }
        else
        {
            Utils.launchActivity(context, PickRetailerActivity.class);
        }
    }*/


    ///////////////////////jayaa///////////////////


    public static String getSku_PhotoSource(String frequentlyskuID)
    {

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String sku_photo_source = null;

        String SQL_sku_Photo_url = "select  sku_photo_source from " + TBL_SKU + " WHERE sku_id = ? ;";
        String selectionargs[] = {frequentlyskuID};

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_sku_Photo_url, selectionargs);

        if (cursor.moveToFirst())
        {
            sku_photo_source = cursor.getString(cursor.getColumnIndexOrThrow("sku_photo_source"));
        }

        cursor.close();
        sqLiteDatabase.close();
        return sku_photo_source;
    }


    public static String getSkuVideoURL(String sku_id)
    {

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String sku_video_source = null;

        String SQL_sku_Photo_url = "select  sku_video_source from " + TBL_SKU + " WHERE sku_id = ? ;";
        String selectionargs[] = {sku_id};

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_sku_Photo_url, selectionargs);

        if (cursor.moveToFirst())
        {
            sku_video_source = cursor.getString(cursor.getColumnIndexOrThrow("sku_video_source"));
        }

        cursor.close();
        sqLiteDatabase.close();
        return sku_video_source;


    }

    public static String getSkuCatalogueURL(String sku_id)
    {

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String sku_catalogue_source = null;

        String sku_catalogue_source_query = "select  sku_catalogue_source from " + TBL_SKU + " WHERE sku_id = ? ;";
        String selectionargs[] = {sku_id};

        Cursor cursor = sqLiteDatabase.rawQuery(sku_catalogue_source_query, selectionargs);

        if (cursor.moveToFirst())
        {
            sku_catalogue_source = cursor.getString(cursor.getColumnIndexOrThrow("sku_catalogue_source"));
        }

        cursor.close();
        sqLiteDatabase.close();
        return sku_catalogue_source;


    }

    public static void clear_table(String tablename)
    {

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);
        sqLiteDatabase.delete(tablename, null, null);
    }


    public static boolean isRetailerPresentInDb(String retailerid)
    {
        boolean isRetailerPresent = false;

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String SQL_SELECT_RETAILER =
                "SELECT" + " retailer_id " + "FROM " + TBL_RETAILER + " WHERE " + "retailer_id " + "= ? ";
        String[] selectionArgs = {retailerid};
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_RETAILER, selectionArgs);

        if (cursor.moveToFirst())
        {
            isRetailerPresent = true;
        }

        cursor.close();
        sqLiteDatabase.close();

        return isRetailerPresent;
    }

    public static boolean isSKUPresentInDb(String skuid)
    {
        boolean isskuPresent = false;

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String SQL_SELECT_sku =
                "SELECT" + " sku_id " + "FROM " + TBL_SKU + " WHERE " + "sku_id " + "= ? ";
        String[] selectionArgs = {skuid};
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_sku, selectionArgs);

        if (cursor.moveToFirst())
        {
            isskuPresent = true;
        }

        cursor.close();
        sqLiteDatabase.close();

        return isskuPresent;
    }

    public static boolean isSKUCategoryPresentInDb(String categoryId)
    {
        boolean isskuCategory = false;

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String SQL_SELECT_sku =
                "SELECT" + " category_id " + "FROM " + TBL_SKU_CATEGORY + " WHERE " + "category_id " + "= ? ";
        String[] selectionArgs = {categoryId};
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_sku, selectionArgs);

        if (cursor.moveToFirst())
        {
            isskuCategory = true;
        }

        cursor.close();
        sqLiteDatabase.close();

        return isskuCategory;
    }

    public static boolean isSKUSubCategoryPresentInDb(String subCategoryId)
    {
        boolean isSKUSubCategoryPresentInDb = false;

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String SQL_SELECT_sku =
                "SELECT" + " sub_category_id " + "FROM " + TBL_SKU_SUBCATEGORY + " WHERE " + "sub_category_id " + "= ? ";
        String[] selectionArgs = {subCategoryId};
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_sku, selectionArgs);

        if (cursor.moveToFirst())
        {
            isSKUSubCategoryPresentInDb = true;
        }

        cursor.close();
        sqLiteDatabase.close();

        return isSKUSubCategoryPresentInDb;
    }


    public static boolean isReasonIdInDb(String reasonid)
    {
        boolean isReason = false;

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String SQL_SELECT_REASON = "SELECT" + " reason_id " + "FROM " + TBL_SKU_NO_ORDERREASON + " WHERE " + "reason_id " + "= ? ";
        String[] selectionArgs = {reasonid};
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_REASON, selectionArgs);

        if (cursor.moveToFirst())
        {
            isReason = true;
        }

        cursor.close();
        sqLiteDatabase.close();

        return isReason;
    }

    public static boolean isLocationIdDb(String locationid)
    {
        boolean isLocationId = false;

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String SQL_SELECT_REASON = "SELECT" + " loc_id " + "FROM " + TBL_LOCATION_HIERARCHY + " WHERE " + "loc_id " + "= ? ";
        String[] selectionArgs = {locationid};
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_REASON, selectionArgs);

        if (cursor.moveToFirst())
        {
            isLocationId = true;
        }

        cursor.close();
        sqLiteDatabase.close();

        return isLocationId;
    }


    /*public static boolean getPromotionFromDb()
    {

        boolean is_promotion=false;

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String isPromotionQuery = "SELECT config_value from " + Constants.TBL_CONFIG + " WHERE config_for =? ";
        String[] selectionArgs = new String[]{Constants.PROMOTION_REQUIRED};

        Cursor cursor = sqLiteDatabase.rawQuery(isPromotionQuery, selectionArgs);

        if(cursor.moveToFirst()){

            if(cursor.getString(cursor.getColumnIndexOrThrow("config_value")).equalsIgnoreCase("Yes")){

                is_promotion=true;
            }
        }

        return is_promotion;
    }*/

    public static boolean getConfigValue(String configfor)
    {

        boolean is_value = false;

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String isPromotionQuery = "SELECT config_value from " + Constants.TBL_CONFIG + " WHERE config_for =? ";
        String[] selectionArgs = new String[]{configfor};

        Cursor cursor = sqLiteDatabase.rawQuery(isPromotionQuery, selectionArgs);

        if (cursor.moveToFirst())
        {

            if (cursor.getString(cursor.getColumnIndexOrThrow("config_value")).equalsIgnoreCase("Yes"))
            {

                is_value = true;
            }
        }


        cursor.close();
        sqLiteDatabase.close();

        return is_value;
    }

    public static String getLocationName(String Location_Id)
    {

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String location_name = null;

        String SQL_SELECT_LOCATION = "SELECT " + "loc_name" + " FROM " + TBL_LOCATION_HIERARCHY + " WHERE " + "loc_id = ?";
        String[] selectionArgs = {Location_Id};

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_LOCATION, selectionArgs);

        if (cursor.moveToFirst())
        {
            location_name = cursor.getString(cursor.getColumnIndexOrThrow("loc_name"));
        }

        cursor.close();
        sqLiteDatabase.close();
        return location_name;

    }


    public static String getActiveRetailer(String activeOrderId)
    {

        String active_retailer_name = NONE;

        String SQL_QUERY_ACTIVE_RETAILER = "SELECT retailer.retailer_name from " + TBL_RETAILER + " retailer " + " INNER JOIN " + TBL_SALES_ORDER + " so " + " ON retailer.retailer_id = so.retailer_id " + " WHERE so.mobile_order_id = ?";
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(MyDb.openDatabase(Constants.dbFileFullPath));

        String[] selectionArgs = {String.valueOf(activeOrderId)};

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_QUERY_ACTIVE_RETAILER, selectionArgs);
        while (cursor.moveToNext())
        {

            active_retailer_name = cursor.getString(cursor.getColumnIndexOrThrow("retailer_name"));
        }
        cursor.close();
        sqLiteDatabase.close();
        return active_retailer_name;
    }


    public static List<SkuGroupHistory> getCustomSkuList()
    {
        String SQL_SELECT_SKUs = null;
     /*  Fragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.skuList_ViewPager + ":" + skuList_ViewPager.getCurrentItem());

        if(skuList_ViewPager.getCurrentItem()==1){
            SQL_SELECT_SKUs = "select sku_id, sku_name from " + TBL_SKU + " WHERE new_sku=1;";
        }
        else if(skuList_ViewPager.getCurrentItem()==3 )
        {*/
        SQL_SELECT_SKUs = "select sku_id, sku_name from " + TBL_SKU + " ;";
//        }
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_SKUs, null);

        List<SkuGroupHistory> skuList = new ArrayList<>();

        while (cursor.moveToNext())
        {
            String skuID = cursor.getString(cursor.getColumnIndexOrThrow("sku_id"));
            String skuName = cursor.getString(cursor.getColumnIndexOrThrow("sku_name"));

            skuList.add(new SkuGroupHistory(skuID, skuName));
        }

        cursor.close();
        sqLiteDatabase.close();

        return skuList;
    }


    //location count for upload
    public static int getLocationCountForUpload(Context context)
    {
        int itemCount = -1;
        String SQL_SELECT_ITEM_COUNT = "SELECT COUNT(*) count FROM " + TBL_LOCATION_HIERARCHY + "  WHERE upload_status = ? ;";
        /*String[] selectionArgs = {orderID};*/


        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_ITEM_COUNT, new String[]{"0"});

        if (cursor.moveToNext())
        {
            itemCount = cursor.getInt(cursor.getColumnIndexOrThrow("count"));
        }

        cursor.close();
        sqLiteDatabase.close();

        return itemCount;
    }


    //retailer count for upload
    public static int getRetailerCountForUpload(Context context)
    {
        int itemCount = -1;
        String SQL_SELECT_ITEM_COUNT = "SELECT COUNT(*) count FROM " + TBL_RETAILER + "  WHERE upload_status = ? AND emp_id = ?;";
        /*String[] selectionArgs = {orderID};*/


        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_ITEM_COUNT, new String[]{"0", new MySharedPrefrencesData().getUser_Id(context)});

        if (cursor.moveToNext())
        {
            itemCount = cursor.getInt(cursor.getColumnIndexOrThrow("count"));
        }

        cursor.close();
        sqLiteDatabase.close();

        return itemCount;
    }


    //retailer update count for upload with upload status 0
    public static ArrayList<RetailerUpdateForUploadFromDb> getRetailerUpdateList(Context context)
    {


        ArrayList<RetailerUpdateForUploadFromDb> retailerUpdateForUploadFromDbArrayList = new ArrayList<>();

        String SQL_SELECT_ITEM_COUNT = "SELECT * FROM " + TBL_RETAILER + "  WHERE upload_status = ? AND emp_id = ? AND modified_by = ?;";
        /*String[] selectionArgs = {orderID};*/


        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_ITEM_COUNT, new String[]{"1", new MySharedPrefrencesData().getUser_Id(context), new MySharedPrefrencesData().getUser_Id(context) + "_" + "1"});

        while (cursor.moveToNext())
        {
            String retailerId = cursor.getString(cursor.getColumnIndexOrThrow("retailer_id"));
            String retailerName = cursor.getString(cursor.getColumnIndexOrThrow("retailer_name"));
            String lat = cursor.getString(cursor.getColumnIndexOrThrow("latitude"));
            String lang = cursor.getString(cursor.getColumnIndexOrThrow("longitude"));
            String img_source = cursor.getString(cursor.getColumnIndexOrThrow("img_source"));

            RetailerUpdateForUploadFromDb.RetailerData retailerData = new RetailerUpdateForUploadFromDb().new RetailerData(retailerId, lat + "," + lang, "", "Retailers", "", "", "", "", new MySharedPrefrencesData().getUser_Id(context), "", "", retailerName, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", img_source, new MySharedPrefrencesData().getUser_Id(context));

            retailerUpdateForUploadFromDbArrayList.add(new RetailerUpdateForUploadFromDb(new MySharedPrefrencesData().getEmployee_AuthKey(context), retailerData));
        }

        cursor.close();
        sqLiteDatabase.close();

        return retailerUpdateForUploadFromDbArrayList;
    }

    //retailerVisit count for upload with no order
    public static int getRetailerVisitWithNoOrderUploadCount(Context context)
    {
        int itemCount = -1;
        String SQL_SELECT_ITEM_COUNT = "SELECT COUNT(*) count FROM " + TBL_RETAILER_VISIT + "  WHERE upload_status = ? AND emp_id = ? AND has_order = ?;";
        /*String[] selectionArgs = {orderID};*/


        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_ITEM_COUNT, new String[]{"0", new MySharedPrefrencesData().getUser_Id(context), "0"});

        if (cursor.moveToNext())
        {
            itemCount = cursor.getInt(cursor.getColumnIndexOrThrow("count"));
        }

        cursor.close();
        sqLiteDatabase.close();

        return itemCount;
    }

    //salesorder count for upload
    public static int getSalesOrderCountForUpload(Context context)
    {
        int itemCount = -1;
        String SQL_SELECT_ITEM_COUNT = "SELECT COUNT(*) count FROM " + TBL_SALES_ORDER + "  WHERE upload_status = ? AND emp_id = ? AND is_placed = ?;";
        /*String[] selectionArgs = {orderID};*/


        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_ITEM_COUNT, new String[]{"0", new MySharedPrefrencesData().getUser_Id(context), "1"});

        if (cursor.moveToNext())
        {
            itemCount = cursor.getInt(cursor.getColumnIndexOrThrow("count"));
        }

        cursor.close();
        sqLiteDatabase.close();

        return itemCount;
    }


    //location data for upload
    public static ArrayList<LocationUploadFromDb> getLocationDataForUpload(Context context)
    {
        ArrayList<LocationUploadFromDb> imLocationArrayList = new ArrayList<>();
        String SQL_SELECT_ITEM_COUNT = "SELECT * FROM " + TBL_LOCATION_HIERARCHY + "  WHERE upload_status = ? ;";


        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_ITEM_COUNT, new String[]{"0"});

        while (cursor.moveToNext())
        {

            String location_id = cursor.getString(cursor.getColumnIndexOrThrow("loc_id"));
            String mobileLocationId = cursor.getString(cursor.getColumnIndexOrThrow("mobile_location_id"));
            String locationName = cursor.getString(cursor.getColumnIndexOrThrow("loc_name"));
            String hierLevel = cursor.getString(cursor.getColumnIndexOrThrow("hier_level"));
            String fullHierLevel = cursor.getString(cursor.getColumnIndexOrThrow("full_hier_level"));
            String parentLocId = cursor.getString(cursor.getColumnIndexOrThrow("parent_loc_id"));

            LocationUploadFromDb.AreaData areaData = new LocationUploadFromDb().new AreaData(location_id, mobileLocationId, parentLocId, locationName, hierLevel, new MySharedPrefrencesData().get_User_CompanyId(context));
            imLocationArrayList.add(new LocationUploadFromDb(new MySharedPrefrencesData().getEmployee_AuthKey(context), areaData));


        }

        cursor.close();
        sqLiteDatabase.close();

        return imLocationArrayList;
    }


    //retailer data for upload
    public static ArrayList<RtlrrUpldModelWithMobileRtlrId> getRetailerDataForUpload(Context context)
    {
        ArrayList<RtlrrUpldModelWithMobileRtlrId> imCreateRetailerArrayList = new ArrayList<>();
        String SQL_SELECT_ITEM_COUNT = "SELECT * FROM " + TBL_RETAILER + "  WHERE upload_status = ? AND emp_id = ?;";


        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_ITEM_COUNT, new String[]{"0", new MySharedPrefrencesData().getUser_Id(context)});

        while (cursor.moveToNext())
        {

            String mob_retailer_id = cursor.getString(cursor.getColumnIndexOrThrow("mobile_retailer_id"));
            String retailerName = cursor.getString(cursor.getColumnIndexOrThrow("retailer_name"));
            String ownerName = cursor.getString(cursor.getColumnIndexOrThrow("shop_name"));
            String pincode = cursor.getString(cursor.getColumnIndexOrThrow("pincode"));
            String mobileNumber = cursor.getString(cursor.getColumnIndexOrThrow("mobile_no"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
            String lat = cursor.getString(cursor.getColumnIndexOrThrow("latitude"));
            String lng = cursor.getString(cursor.getColumnIndexOrThrow("longitude"));
            String areaID = cursor.getString(cursor.getColumnIndexOrThrow("area_id"));
            String shopphotopath = cursor.getString(cursor.getColumnIndexOrThrow("img_source"));
            String shopAddress = cursor.getString(cursor.getColumnIndexOrThrow("shop_address"));
            String bcrFrontText = cursor.getString(cursor.getColumnIndexOrThrow("ocr_front_text"));
            String bcrBackText = cursor.getString(cursor.getColumnIndexOrThrow("ocr_back_text"));
            String BCRPhotoPathList = cursor.getString(cursor.getColumnIndexOrThrow("ocr_photolist"));

            ArrayList<String> bcrpiclist = new ArrayList<>();

            if (BCRPhotoPathList != null && BCRPhotoPathList.length() > 5)
            {

                bcrpiclist = new Gson().fromJson(BCRPhotoPathList, ArrayList.class);
            }

            RtlrrUpldModelWithMobileRtlrId.RetailerDataWithMobileRtlrId retailerData = new RtlrrUpldModelWithMobileRtlrId().new RetailerDataWithMobileRtlrId(mob_retailer_id, retailerName, ownerName, "Retailers", pincode, mobileNumber, email, lat + "," + lng, areaID, getDateTime(), new MySharedPrefrencesData().getUser_Id(context), shopphotopath, shopAddress, bcrFrontText, bcrBackText, bcrpiclist);

            RtlrrUpldModelWithMobileRtlrId createRetailer = new RtlrrUpldModelWithMobileRtlrId(new MySharedPrefrencesData().getEmployee_AuthKey(context), retailerData);

            imCreateRetailerArrayList.add(createRetailer);


        }

        cursor.close();
        sqLiteDatabase.close();

        return imCreateRetailerArrayList;
    }


    //SO data for upload
    public static ArrayList<SalesOrderFromDbForUpload> getSODataFromDbForUpload(Context context)
    {
        ArrayList<SalesOrderFromDbForUpload> salesOrderFromDbForUploadArrayList = new ArrayList<>();
        String SQL_SELECT_ITEM_COUNT = "SELECT * FROM " + TBL_SALES_ORDER + "  WHERE upload_status = ? AND emp_id = ? AND is_placed = ? AND is_active = ?;";


        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_ITEM_COUNT, new String[]{"0", new MySharedPrefrencesData().getUser_Id(context), "1", "0"});

        while (cursor.moveToNext())
        {

            String orderId = cursor.getString(cursor.getColumnIndexOrThrow("order_id"));
            String mobileOrderId = cursor.getString(cursor.getColumnIndexOrThrow("mobile_order_id"));
            String visitId = cursor.getString(cursor.getColumnIndexOrThrow("visit_id"));
            String mobileVisitId = cursor.getString(cursor.getColumnIndexOrThrow("mobile_visit_id"));
            String retailerId = cursor.getString(cursor.getColumnIndexOrThrow("retailer_id"));
            String mobileRetailerId = cursor.getString(cursor.getColumnIndexOrThrow("mobile_retailer_id"));
            String totalOrderValue = cursor.getString(cursor.getColumnIndexOrThrow("total_order_value"));
            String totalDiscount = cursor.getString(cursor.getColumnIndexOrThrow("total_discount"));


            salesOrderFromDbForUploadArrayList.add(new SalesOrderFromDbForUpload(orderId, mobileOrderId, visitId, mobileVisitId, retailerId, mobileRetailerId, totalOrderValue, totalDiscount));

        }

        cursor.close();
        sqLiteDatabase.close();

        return salesOrderFromDbForUploadArrayList;
    }


   /* //retailer visits for upload
    public static ArrayList<RtlrrUpldModelWithMobileRtlrId> getRetailerDataForUpload(Context context)
    {
        ArrayList<RtlrrUpldModelWithMobileRtlrId> imCreateRetailerArrayList=new ArrayList<>();
        String SQL_SELECT_ITEM_COUNT = "SELECT * FROM "+TBL_RETAILER+"  WHERE upload_status = ? AND emp_id = ?;";



        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_ITEM_COUNT, new String[]{"0",new MySharedPrefrencesData().getUser_Id(context)});

        while (cursor.moveToNext())
        {

            String mob_retailer_id=cursor.getString(cursor.getColumnIndexOrThrow("mobile_retailer_id"));
            String retailerName=cursor.getString(cursor.getColumnIndexOrThrow("retailer_name"));
            String ownerName=cursor.getString(cursor.getColumnIndexOrThrow("shop_name"));
            String pincode=cursor.getString(cursor.getColumnIndexOrThrow("pincode"));
            String mobileNumber=cursor.getString(cursor.getColumnIndexOrThrow("mobile_no"));
            String email=cursor.getString(cursor.getColumnIndexOrThrow("email"));
            String lat=cursor.getString(cursor.getColumnIndexOrThrow("latitude"));
            String lng=cursor.getString(cursor.getColumnIndexOrThrow("longitude"));
            String areaID=cursor.getString(cursor.getColumnIndexOrThrow("area_id"));
            String shopphotopath=cursor.getString(cursor.getColumnIndexOrThrow("img_source"));
            String shopAddress=cursor.getString(cursor.getColumnIndexOrThrow("shop_address"));
            String bcrFrontText=cursor.getString(cursor.getColumnIndexOrThrow("ocr_front_text"));
            String bcrBackText=cursor.getString(cursor.getColumnIndexOrThrow("ocr_back_text"));
            String BCRPhotoPathList=cursor.getString(cursor.getColumnIndexOrThrow("ocr_photolist"));

            ArrayList<String> bcrpiclist=new ArrayList<>();

            if(BCRPhotoPathList!=null && BCRPhotoPathList.length()>5){

                bcrpiclist=new Gson().fromJson(BCRPhotoPathList,ArrayList.class);
            }

            RtlrrUpldModelWithMobileRtlrId.RetailerDataWithMobileRtlrId retailerData = new RtlrrUpldModelWithMobileRtlrId().new RetailerDataWithMobileRtlrId(mob_retailer_id,retailerName, ownerName, "Retailers", pincode, mobileNumber, email, lat + "," + lng, areaID, getDateTime(), new MySharedPrefrencesData().getUser_Id(context), shopphotopath, shopAddress, bcrFrontText, bcrBackText, bcrpiclist);

            RtlrrUpldModelWithMobileRtlrId createRetailer = new RtlrrUpldModelWithMobileRtlrId(new MySharedPrefrencesData().getEmployee_AuthKey(context), retailerData);

            imCreateRetailerArrayList.add(createRetailer);


        }

        cursor.close();
        sqLiteDatabase.close();

        return imCreateRetailerArrayList;
    }*/

    //  update retailer table after upload

    public static void updateRetailerTable(String customer_mobile_retailer_id, CreateretaileModel createretaileModel)
    {

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        ContentValues retailervalues = new ContentValues();
        retailervalues.put("upload_status", "1");
        retailervalues.put("retailer_id", createretaileModel.getCustomerCode());
        retailervalues.put("img_source", createretaileModel.getCustomer_picture_path());
        sqLiteDatabase.update(TBL_RETAILER, retailervalues, "upload_status = ? AND mobile_retailer_id = ? AND emp_id = ? ", new String[]{"0", customer_mobile_retailer_id, new MySharedPrefrencesData().getUser_Id(LoginActivity.baseContext)});

        sqLiteDatabase.close();
    }


    // retailer status

    public static String checkRetailerStatus(Context context, String mobile_retailer_id)
    {

        String retailerId = NONE;

        String SQL_SELECT_ITEM_STATUS = "SELECT * FROM " + TBL_RETAILER + "  WHERE mobile_retailer_id = ? AND emp_id = ?;";


        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_ITEM_STATUS, new String[]{mobile_retailer_id, new MySharedPrefrencesData().getUser_Id(context)});

        if (cursor.moveToFirst())
        {

            String uploadStatus = cursor.getString(cursor.getColumnIndexOrThrow("upload_status"));

            if (uploadStatus.equalsIgnoreCase("1"))
            {

                retailerId = cursor.getString(cursor.getColumnIndexOrThrow("retailer_id"));
            }

        }

        cursor.close();
        sqLiteDatabase.close();
        return retailerId;


    }

    //get retailer visit from db

    public static RetailerVisitUploadFromDb checkRetailerVisitStatus(Context context, String mobile_visit_id, String mobile_retailer_id)
    {

        RetailerVisitUploadFromDb retailerVisitUploadFromDb = null;

        String SQL_SELECT_ITEM_STATUS = "SELECT * FROM " + TBL_RETAILER_VISIT + "  WHERE mobile_visit_id = ? AND emp_id = ? AND mobile_retailer_id = ? ;";


        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_ITEM_STATUS, new String[]{mobile_visit_id, new MySharedPrefrencesData().getUser_Id(context), mobile_retailer_id});

        if (cursor.moveToNext())
        {

            String uploadStatus = cursor.getString(cursor.getColumnIndexOrThrow("upload_status"));
            String visitId = cursor.getString(cursor.getColumnIndexOrThrow("visit_id"));
            String mobileVisitId = cursor.getString(cursor.getColumnIndexOrThrow("mobile_visit_id"));
            String retailerId = cursor.getString(cursor.getColumnIndexOrThrow("retailer_id"));
            ;
            String mobileRetailerId = cursor.getString(cursor.getColumnIndexOrThrow("mobile_retailer_id"));
            String visitDate = cursor.getString(cursor.getColumnIndexOrThrow("visit_date"));
            String hasOrder = cursor.getString(cursor.getColumnIndexOrThrow("has_order"));
            String feedback = cursor.getString(cursor.getColumnIndexOrThrow("feedback"));
            String latitude = cursor.getString(cursor.getColumnIndexOrThrow("latitude"));
            String longitude = cursor.getString(cursor.getColumnIndexOrThrow("longitude"));
            if (uploadStatus.equalsIgnoreCase("1"))
            {

                retailerVisitUploadFromDb = new RetailerVisitUploadFromDb(visitId, mobileVisitId, retailerId, mobileRetailerId, visitDate, hasOrder, feedback, latitude, longitude);
            }
            else
            {

                retailerVisitUploadFromDb = new RetailerVisitUploadFromDb(NONE, mobileVisitId, retailerId, mobileRetailerId, visitDate, hasOrder, feedback, latitude, longitude);
            }

        }


        return retailerVisitUploadFromDb;

    }


    //update visit table

    public static void updateVisitTable(Context context, RetailerVisitUploadFromDb retailerVisitUploadFromDb)
    {
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        ContentValues retailervalues = new ContentValues();
        retailervalues.put("upload_status", "1");
        retailervalues.put("visit_id", retailerVisitUploadFromDb.getVisitId());
        retailervalues.put("retailer_id", retailerVisitUploadFromDb.getRetailerId());


        sqLiteDatabase.update(TBL_RETAILER_VISIT, retailervalues, "upload_status = ?  AND emp_id = ? AND mobile_visit_id = ? AND mobile_retailer_id = ?", new String[]{"0", new MySharedPrefrencesData().getUser_Id(context), retailerVisitUploadFromDb.getMobileVisitId(), retailerVisitUploadFromDb.getMobileRetailerId()});

        sqLiteDatabase.close();

    }

    //update sales order master

    public static void updateSalesOrderMaster(Context context, String visitiD, String orderId, RetailerVisitUploadFromDb retailerVisitUploadFromDb, SalesOrderFromDbForUpload salesOrderFromDbForUpload)
    {


        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        ContentValues ordervalues = new ContentValues();
        ordervalues.put("upload_status", "1");
        ordervalues.put("visit_id", visitiD);
        ordervalues.put("order_id", orderId);
        ordervalues.put("retailer_id", retailerVisitUploadFromDb.getRetailerId());

        ContentValues orderDetailValues = new ContentValues();
        orderDetailValues.put("order_id", orderId);


        sqLiteDatabase.update(TBL_SALES_ORDER, ordervalues, "upload_status = ?  AND emp_id = ? AND mobile_visit_id = ? ", new String[]{"0", new MySharedPrefrencesData().getUser_Id(context), retailerVisitUploadFromDb.getMobileVisitId()});
        sqLiteDatabase.update(TBL_SALES_ORDER_DETAILS, orderDetailValues, "upload_status = ?  AND mobile_order_id = ? ", new String[]{"0", salesOrderFromDbForUpload.getMobile_order_id()});

        sqLiteDatabase.close();
    }


    //get sales order details from db for upload

    public static ArrayList<SalesOrderDetailsUploadFromDb> checkSalesOrderDetails(Context context, String orderid)
    {

        ArrayList<SalesOrderDetailsUploadFromDb> salesOrderDetailsUploadFromDbArrayList = new ArrayList<>();


        String SQL_SELECT_ITEM_COUNT = "SELECT * FROM " + TBL_SALES_ORDER_DETAILS + "  WHERE upload_status = ?  AND order_id = ? ;";


        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_ITEM_COUNT, new String[]{"0", orderid});

        while (cursor.moveToNext())
        {


            String orderDetailId = cursor.getString(cursor.getColumnIndexOrThrow("order_detail_id"));
            String serverOrderDetailId = cursor.getString(cursor.getColumnIndexOrThrow("server_order_detail_id"));
            String orderId = cursor.getString(cursor.getColumnIndexOrThrow("order_id"));
            String mobileOrderId = cursor.getString(cursor.getColumnIndexOrThrow("mobile_order_id"));
            String skuId = cursor.getString(cursor.getColumnIndexOrThrow("sku_id"));
            String skuName = cursor.getString(cursor.getColumnIndexOrThrow("sku_name"));
            String skuPrice = cursor.getString(cursor.getColumnIndexOrThrow("sku_price"));
            String skuQty = cursor.getString(cursor.getColumnIndexOrThrow("sku_qty"));
            String skuFreeQty = cursor.getString(cursor.getColumnIndexOrThrow("sku_free_qty"));
            String skuDiscount = cursor.getString(cursor.getColumnIndexOrThrow("sku_discount"));
            String skuPriceBeforeDiscount = cursor.getString(cursor.getColumnIndexOrThrow("sku_price_before_discount"));
            String skuFinalPrice = cursor.getString(cursor.getColumnIndexOrThrow("sku_final_price"));


            salesOrderDetailsUploadFromDbArrayList.add(new SalesOrderDetailsUploadFromDb(orderDetailId, serverOrderDetailId, orderId, mobileOrderId, skuId, skuName, skuPrice, skuQty, skuFreeQty, skuDiscount, skuPriceBeforeDiscount, skuFinalPrice));

        }

        cursor.close();
        sqLiteDatabase.close();

        return salesOrderDetailsUploadFromDbArrayList;
    }


    //get retailer visit from db with no order for upload

    public static ArrayList<RetailerVisitUploadFromDb> getRetailerVisitNoOrder(Context context)
    {

        ArrayList<RetailerVisitUploadFromDb> retailerVisitUploadFromDb = new ArrayList<>();

        String SQL_SELECT_ITEM_STATUS = "SELECT * FROM " + TBL_RETAILER_VISIT + "  WHERE upload_status = ? AND emp_id = ? AND has_order = ? ;";


        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_ITEM_STATUS, new String[]{"0", new MySharedPrefrencesData().getUser_Id(context), "0"});

        while (cursor.moveToNext())
        {

            String uploadStatus = cursor.getString(cursor.getColumnIndexOrThrow("upload_status"));
            String visitId = cursor.getString(cursor.getColumnIndexOrThrow("visit_id"));
            String mobileVisitId = cursor.getString(cursor.getColumnIndexOrThrow("mobile_visit_id"));
            String mobileRetailerId = cursor.getString(cursor.getColumnIndexOrThrow("mobile_retailer_id"));

            String SQL_SELECT_RETAILERID = "SELECT retailer_id FROM " + TBL_RETAILER + "  WHERE  emp_id = ? AND mobile_retailer_id = ? ;";

            Cursor cursorInner = sqLiteDatabase.rawQuery(SQL_SELECT_RETAILERID, new String[]{new MySharedPrefrencesData().getUser_Id(context), mobileRetailerId});

            String retailerId = cursor.getString(cursor.getColumnIndexOrThrow("retailer_id"));

            if (cursorInner.moveToNext())
            {

                retailerId = cursorInner.getString(cursorInner.getColumnIndexOrThrow("retailer_id"));
            }

            String visitDate = cursor.getString(cursor.getColumnIndexOrThrow("visit_date"));
            String hasOrder = cursor.getString(cursor.getColumnIndexOrThrow("has_order"));
            String feedback = cursor.getString(cursor.getColumnIndexOrThrow("feedback"));
            String latitude = cursor.getString(cursor.getColumnIndexOrThrow("latitude"));
            String longitude = cursor.getString(cursor.getColumnIndexOrThrow("longitude"));

            retailerVisitUploadFromDb.add(new RetailerVisitUploadFromDb(visitId, mobileVisitId, retailerId, mobileRetailerId, visitDate, hasOrder, feedback, latitude, longitude));

        }


        return retailerVisitUploadFromDb;

    }


    //update locationId after upload

    public static void updateLocationTableLocId(String mobile_location_id, PutNewArea putNewArea, Context context, String locationIdMobile)
    {

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        ContentValues locationValues = new ContentValues();
        locationValues.put("upload_status", "1");
        locationValues.put("loc_id", putNewArea.getLocHierId());
        locationValues.put("full_hier_level", putNewArea.getLocationHier());


        ContentValues locationValuesForRetailer = new ContentValues();
        locationValuesForRetailer.put("area_id", putNewArea.getLocHierId());


        sqLiteDatabase.update(TBL_LOCATION_HIERARCHY, locationValues, "upload_status = ?  AND mobile_location_id = ? ", new String[]{"0", mobile_location_id});
        sqLiteDatabase.update(TBL_RETAILER, locationValuesForRetailer, "upload_status = ?  AND emp_id = ? AND area_id = ? ", new String[]{"0", new MySharedPrefrencesData().getUser_Id(context), locationIdMobile});

        sqLiteDatabase.close();
    }

    //update retailer after upload
    public static void updateRetailerInDBAfterUpload(String retailerID, String customerPicturePath, Context context)
    {
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);


        ContentValues retailerValues = new ContentValues();
        retailerValues.put("img_source", customerPicturePath);
        retailerValues.put("modified_by", new MySharedPrefrencesData().getUser_Id(context));

        sqLiteDatabase.update(TBL_RETAILER, retailerValues, " emp_id = ? AND retailer_id = ? ", new String[]{new MySharedPrefrencesData().getUser_Id(context), retailerID});

        sqLiteDatabase.close();

    }


    //retailer validation on same area with same name
    public static boolean retailerValidation(Context context, String retailerName, String areaID)
    {
        boolean retailerPresence = false;

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String SQL_SELECT_RETAILER = "SELECT" + " mobile_retailer_id " + "FROM " + TBL_RETAILER + " WHERE " + "area_id " + "= ? AND retailer_name = ? ";
        String[] selectionArgs = {areaID, retailerName};
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_RETAILER, selectionArgs);

        if (cursor.moveToFirst())
        {
            retailerPresence = true;
        }

        cursor.close();
        sqLiteDatabase.close();

        return retailerPresence;
    }

    //view list of retailers
    public static ArrayList<RetailerDetailsForView> getRetailerAllList(Context context)
    {

        ArrayList<RetailerDetailsForView> retailerDetailsForViewArrayList = new ArrayList<>();
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String userlocationid = new MySharedPrefrencesData().getUser_LocationId(context);

        String selected_location_id = "";

        if (userlocationid.contains(","))
        {

            String location_id_arr[] = userlocationid.split(",");

            for (int i = 0; i < location_id_arr.length; i++)
            {

                String SQL_SELECT_RETAILERS = "select retailer_id,mobile_retailer_id,retailer_name,shop_name,img_source,mobile_no,email,upload_status from " + TBL_RETAILER + "  WHERE  area_id = ?;";
                Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_RETAILERS, new String[]{location_id_arr[i]});
                cursor.moveToFirst();

                while (!cursor.isAfterLast())
                {
                    String retailerID = cursor.getString(cursor.getColumnIndexOrThrow("retailer_id"));
                    String mobileRetailerId = cursor.getString(cursor.getColumnIndexOrThrow("mobile_retailer_id"));
                    String retailer_name = cursor.getString(cursor.getColumnIndexOrThrow("retailer_name"));
                    String rOwnerName = cursor.getString(cursor.getColumnIndexOrThrow("shop_name"));
                    String rPic = cursor.getString(cursor.getColumnIndexOrThrow("img_source"));
                    String rMob = cursor.getString(cursor.getColumnIndexOrThrow("mobile_no"));
                    String rEmail = cursor.getString(cursor.getColumnIndexOrThrow("email"));
                    String uploadStatus = cursor.getString(cursor.getColumnIndexOrThrow("upload_status"));
                    retailerDetailsForViewArrayList.add(new RetailerDetailsForView(retailerID, mobileRetailerId, retailer_name, rOwnerName, rPic, rMob, rEmail, uploadStatus));

                    ContentValues cv = new ContentValues();
                    cv.put("emp_id", new MySharedPrefrencesData().getUser_Id(context));
                    sqLiteDatabase.update(TBL_RETAILER, cv, " retailer_id = ? AND mobile_retailer_id = ?", new String[]{retailerID, mobileRetailerId});

                    cursor.moveToNext();
                }
                cursor.close();

            }


        }
        else
        {

            selected_location_id = userlocationid;

            String SQL_SELECT_RETAILERS = "select retailer_id,mobile_retailer_id,retailer_name,shop_name,img_source,mobile_no,email,upload_status from " + TBL_RETAILER + "  WHERE  area_id = ?;";
            Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_RETAILERS, new String[]{selected_location_id});
            cursor.moveToFirst();

            while (!cursor.isAfterLast())
            {
                String retailerID = cursor.getString(cursor.getColumnIndexOrThrow("retailer_id"));
                String mobileRetailerId = cursor.getString(cursor.getColumnIndexOrThrow("mobile_retailer_id"));
                String retailer_name = cursor.getString(cursor.getColumnIndexOrThrow("retailer_name"));
                String rOwnerName = cursor.getString(cursor.getColumnIndexOrThrow("shop_name"));
                String rPic = cursor.getString(cursor.getColumnIndexOrThrow("img_source"));
                String rMob = cursor.getString(cursor.getColumnIndexOrThrow("mobile_no"));
                String rEmail = cursor.getString(cursor.getColumnIndexOrThrow("email"));
                String uploadStatus = cursor.getString(cursor.getColumnIndexOrThrow("upload_status"));
                retailerDetailsForViewArrayList.add(new RetailerDetailsForView(retailerID, mobileRetailerId, retailer_name, rOwnerName, rPic, rMob, rEmail, uploadStatus));

                ContentValues cv = new ContentValues();
                cv.put("emp_id", new MySharedPrefrencesData().getUser_Id(context));
                sqLiteDatabase.update(TBL_RETAILER, cv, " retailer_id = ? AND mobile_retailer_id = ?", new String[]{retailerID, mobileRetailerId});

                cursor.moveToNext();
            }
            cursor.close();

        }


        sqLiteDatabase.close();

        return retailerDetailsForViewArrayList;
    }


    public static int getNoOfSalesOrdersForAccToEmp(String salesPersonId)
    {
        int noOfSalesOrders = 0;
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        //Cursor cursor = sqLiteDatabase.rawQuery("SELECT order_id FROM " + Constants.TBL_SALES_ORDER + " WHERE emp_id = ? AND is_placed = ? AND order_date like ? ", new String[]{salesPersonId, "1", Utils.getTodayDate() + "%"});
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT order_id FROM " + Constants.TBL_SALES_ORDER + " WHERE emp_id = ? AND is_placed = ? AND upload_status =  ? ", new String[]{salesPersonId, "1", "1"});
        noOfSalesOrders = (int) cursor.getCount();
        cursor.close();
        sqLiteDatabase.close();
        return noOfSalesOrders;
    }

    public static int getNoOfSalesOrdersForAccToEmpUnUploaded(String salesPersonId)
    {
        int noOfSalesOrders = 0;
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        //Cursor cursor = sqLiteDatabase.rawQuery("SELECT order_id FROM " + Constants.TBL_SALES_ORDER + " WHERE emp_id = ? AND is_placed = ? AND order_date like ? ", new String[]{salesPersonId, "1", Utils.getTodayDate() + "%"});
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT order_id FROM " + Constants.TBL_SALES_ORDER + " WHERE emp_id = ? AND is_placed = ? AND upload_status =  ? ", new String[]{salesPersonId, "1", "0"});
        noOfSalesOrders = (int) cursor.getCount();
        cursor.close();
        sqLiteDatabase.close();
        return noOfSalesOrders;
    }

    //get Configvalue max retention days
    public static int getMaxRetentionDays()
    {
        int days = 0;
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);


        //Cursor cursor = sqLiteDatabase.rawQuery("SELECT order_id FROM " + Constants.TBL_SALES_ORDER + " WHERE emp_id = ? AND is_placed = ? AND order_date like ? ", new String[]{salesPersonId, "1", Utils.getTodayDate() + "%"});
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT config_value FROM " + Constants.TBL_CONFIG + " WHERE config_for = ? ", new String[]{"max_data_retention_days"});
        if (cursor.moveToFirst())
        {

            days = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("config_value")));
        }
        cursor.close();
        sqLiteDatabase.close();
        return days;
    }

    //get Configvalue min retention days
    public static int getMinRetentionDays()
    {
        int days = 0;
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);


        //Cursor cursor = sqLiteDatabase.rawQuery("SELECT order_id FROM " + Constants.TBL_SALES_ORDER + " WHERE emp_id = ? AND is_placed = ? AND order_date like ? ", new String[]{salesPersonId, "1", Utils.getTodayDate() + "%"});
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT config_value FROM " + Constants.TBL_CONFIG + " WHERE config_for = ? ", new String[]{"min_data_retention_days"});
        if (cursor.moveToFirst())
        {

            days = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("config_value")));
        }
        cursor.close();
        sqLiteDatabase.close();
        return days;
    }


    //get Configvalue max retention records
    public static int getMaxRetentionRecords()
    {
        int records = 0;
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);


        //Cursor cursor = sqLiteDatabase.rawQuery("SELECT order_id FROM " + Constants.TBL_SALES_ORDER + " WHERE emp_id = ? AND is_placed = ? AND order_date like ? ", new String[]{salesPersonId, "1", Utils.getTodayDate() + "%"});
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT config_value FROM " + Constants.TBL_CONFIG + " WHERE config_for = ? ", new String[]{"max_data_retention_rec_count"});
        if (cursor.moveToFirst())
        {

            records = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("config_value")));
        }
        cursor.close();
        sqLiteDatabase.close();
        return records;
    }

}
