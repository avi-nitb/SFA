package in.etaminepgg.sfa.Utilities;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import in.etaminepgg.sfa.Activities.LoginActivity;
import in.etaminepgg.sfa.Models.QuantityDiscountModel;
import in.etaminepgg.sfa.Models.SalesOrderSku;
import in.etaminepgg.sfa.Models.Sku;
import in.etaminepgg.sfa.Models.SkuGroupHistory;

import static in.etaminepgg.sfa.Utilities.Constants.TBL_LOCATION_HIERARCHY;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_RETAILER;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_RETAILER_VISIT;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SALES_ORDER;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SALES_ORDER_DETAILS;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SKU;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SKU_NO_ORDERREASON;
import static in.etaminepgg.sfa.Utilities.Constants.dbFileFullPath;
import static in.etaminepgg.sfa.Utilities.ConstantsA.NONE;
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
                "SELECT" + " visit_id " + "FROM " + TBL_RETAILER_VISIT + " WHERE " + "emp_id " + "= ? AND " + "visit_date " + "like ? " + "GROUP BY retailer_id";

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
                "SELECT" + " visit_id " + "FROM " + TBL_RETAILER_VISIT + " WHERE " + "emp_id " + "= ? AND " + "visit_date " + "like ? " ;
        String[] selectionArgs = {salesPersonId, getTodayDate() + "%"};

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_RETAILER_VISITS_OF_SALES_PERSON, selectionArgs);
        int noOfVisitsMadeBySalesPerson = cursor.getCount();
        cursor.close();
        sqLiteDatabase.close();

        return noOfVisitsMadeBySalesPerson;
    }

    //copy pasted from SalesSummary Activity for  retailer visits according to location
    public static int getRetailerVisitsForLocationId(String salesPersonId,String locationId)
    {
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

       /* String SQL_SELECT_RETAILER_VISITS_OF_SALES_PERSON =
                "SELECT" + " visit_id " + "FROM " + TBL_RETAILER_VISIT + " WHERE " + "emp_id " + "= ? AND " + "visit_date " + "like ? " + "GROUP BY retailer_id";*/

        String SQL_SELECT_RETAILER_VISITS_OF_SALES_PERSON =
                "SELECT" + " retailer_visit.visit_id " + "FROM " + TBL_RETAILER +" retailer "+" INNER JOIN "+TBL_RETAILER_VISIT+" retailer_visit "+ " ON retailer.retailer_id=retailer_visit.retailer_id "+" WHERE " + "retailer_visit.emp_id " + "= ? AND " + "retailer_visit.visit_date " + "like ? AND retailer.area_id = ? " ;
        String[] selectionArgs = {salesPersonId, getTodayDate() + "%",locationId};

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_RETAILER_VISITS_OF_SALES_PERSON, selectionArgs);
        int noOfVisitsMadeBySalesPerson = cursor.getCount();
        cursor.close();
        sqLiteDatabase.close();

        return noOfVisitsMadeBySalesPerson;
    }



    public static void makeCurrentActiveOrderInactive()
    {
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);
        ContentValues salesOrderValues = new ContentValues();
        salesOrderValues.put("is_active", "0");
        sqLiteDatabase.update(TBL_SALES_ORDER, salesOrderValues, "is_active = ? AND emp_id = ? ", new String[]{"1",new MySharedPrefrencesData().getUser_Id(LoginActivity.baseContext)});

        sqLiteDatabase.close();
    }

    //returns order_id of active order, if active order exists
    //returns getActiveOrderID() if there is no active order
    public static String getActiveOrderID()
    {
        String activeOrderID = NONE;
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String SQL_SELECT_ACTIVE_SALES_ORDER_ID = "select order_id from " + TBL_SALES_ORDER + " WHERE " + "is_active = ? AND emp_id = ?";
        String[] selectionArgs = new String[]{"1",new MySharedPrefrencesData().getUser_Id(LoginActivity.baseContext)};
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_ACTIVE_SALES_ORDER_ID, selectionArgs);

        if(cursor.moveToFirst())
        {
            activeOrderID = cursor.getString(cursor.getColumnIndexOrThrow("order_id"));
        }

        cursor.close();
        sqLiteDatabase.close();

        return activeOrderID;
    }

    //returns order_date  of active order, if active order exists
    //returns getActiveOrderID() if there is no active order
    public static String getActiveOrderIDDate(String activeOrderId)
    {
        String activeOrderDate = NONE;
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String SQL_SELECT_ACTIVE_SALES_ORDER_ID = "select order_date from " + TBL_SALES_ORDER + " WHERE " + "is_active = ? AND emp_id = ? AND order_id = ?";
        String[] selectionArgs = new String[]{"1",new MySharedPrefrencesData().getUser_Id(LoginActivity.baseContext),activeOrderId};
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_ACTIVE_SALES_ORDER_ID, selectionArgs);

        if(cursor.moveToFirst())
        {
            activeOrderDate = cursor.getString(cursor.getColumnIndexOrThrow("order_date"));
        }

        cursor.close();
        sqLiteDatabase.close();

        return activeOrderDate;
    }


    public static int getNoOfSalesOrdersForsize() {

        int noOfSalesOrders=0;

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        Cursor cursor = sqLiteDatabase.rawQuery("SELECT COUNT(*) FROM " + Constants.TBL_SKU_NO_ORDERREASON ,null);

        cursor.moveToFirst();

        noOfSalesOrders = cursor.getInt(0);

        cursor.close();
        return noOfSalesOrders;
    }


    public static int getSkuRowsSize() {

        int noOfSkuRows=0;

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        Cursor cursor = sqLiteDatabase.rawQuery("SELECT COUNT(*) FROM " + Constants.TBL_SKU ,null);

        cursor.moveToFirst();

        noOfSkuRows = cursor.getInt(0);

        cursor.close();
        return noOfSkuRows;
    }



    //delete order
    public static void deleteOrder(String activeOrderId)
    {

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String SQL_SELECT_ACTIVE_SALES_ORDER_ID = "select order_date from " + TBL_SALES_ORDER + " WHERE " + "is_active = ? AND emp_id = ? AND order_id = ?";
        String[] selectionArgs = new String[]{"1",new MySharedPrefrencesData().getUser_Id(LoginActivity.baseContext),activeOrderId};
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_ACTIVE_SALES_ORDER_ID, selectionArgs);

        sqLiteDatabase.delete(TBL_SALES_ORDER_DETAILS, "order_id = ?",new String[]{activeOrderId});
        sqLiteDatabase.delete(TBL_SALES_ORDER, "order_id = ?",new String[]{activeOrderId});

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
        String SQL_SELECT_REGULAR_ORDER_ID = "SELECT " + "order_id" + " FROM " + TBL_SALES_ORDER + " WHERE " + "retailer_id = ? AND " + "is_regular = ? AND emp_id = ?";
        String[] selectionArgs = {retailerID, "1",new MySharedPrefrencesData().getUser_Id(LoginActivity.baseContext)};

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_REGULAR_ORDER_ID, selectionArgs);
        if(cursor.moveToFirst())
        {
            regularOrderID = cursor.getString(cursor.getColumnIndexOrThrow("order_id"));
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
        String[] selectionArgs = {"1",new MySharedPrefrencesData().getUser_Id(LoginActivity.baseContext)};

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_RETAILER_ID_OF_ACTIVE_ORDER, selectionArgs);
        if(cursor.moveToFirst())
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
       String retailername=NONE;
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);
        String SQL_SELECT_RETAILER_ID_OF_ACTIVE_ORDER = "SELECT " + "retailer_name" + " FROM " + TBL_RETAILER + " WHERE " + "retailer_id = ?";
        String[] selectionArgs = {retailerid};

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_RETAILER_ID_OF_ACTIVE_ORDER, selectionArgs);
        if(cursor.moveToFirst())
        {
            retailername = cursor.getString(cursor.getColumnIndexOrThrow("retailer_name"));
        }
        cursor.close();
        sqLiteDatabase.close();

        return retailername;
    }

    public static long insertIntoSalesOrderDetailsTable(String orderID, String skuID, String skuName, String skuPrice, String skuQty,String skuFreeQty,String skuDiscount)
    {
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        float skuPriceInt = Float.parseFloat(skuPrice);
        int skuQtyInt = Integer.parseInt(skuQty);
        String skuPrice_beforeDiscount = String.valueOf(skuPriceInt * skuQtyInt);
        String skuFinalPrice = String.valueOf((skuPriceInt * skuQtyInt)-Float.parseFloat(skuDiscount));

        ContentValues salesOrderDetailsValues = new ContentValues();
        salesOrderDetailsValues.put("order_id", orderID);
        salesOrderDetailsValues.put("sku_id", skuID);
        salesOrderDetailsValues.put("sku_name", skuName);
        salesOrderDetailsValues.put("sku_price", skuPrice);
        salesOrderDetailsValues.put("sku_qty", skuQty);
        salesOrderDetailsValues.put("sku_free_qty", skuFreeQty);
        salesOrderDetailsValues.put("sku_discount", skuDiscount);
        salesOrderDetailsValues.put("sku_price_before_discount", skuPrice_beforeDiscount);
        salesOrderDetailsValues.put("sku_final_price", skuFinalPrice);
        salesOrderDetailsValues.put("upload_status", 0);

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

    static boolean isSkuPresent(String skuID, String salesOrderID)
    {
        boolean isSkuPresent = false;

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String SQL_SELECT_SKU_FROM_SALES_ORDER_DETAILS =
                "SELECT" + " sku_id " + "FROM " + TBL_SALES_ORDER_DETAILS + " WHERE " + "order_id " + "= ? AND " + "sku_id " + "= ?";
        String[] selectionArgs = {salesOrderID, skuID};
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_SKU_FROM_SALES_ORDER_DETAILS, selectionArgs);

        if(cursor.moveToFirst())
        {
            isSkuPresent = true;
        }

        cursor.close();
        sqLiteDatabase.close();

        return isSkuPresent;
    }

    public static ArrayList<QuantityDiscountModel> getSkuQuantityDiscount(Long orderDetailID)
    {
        ArrayList<QuantityDiscountModel> discountModelArrayList=new ArrayList<>();

        int skuQuantity = -100;
        int skuFreeQuantity = -101;
        float skuDiscount = 0;
        float skuPrice = 0;

        String sqlQuery = "SELECT sku_price,sku_qty,sku_free_qty,sku_discount FROM sales_order_details WHERE order_detail_id = ? ;";
        String[] selectionArgs = {String.valueOf(orderDetailID)};

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        Cursor cursor = sqLiteDatabase.rawQuery(sqlQuery, selectionArgs);

        if(cursor.moveToNext())
        {
            skuQuantity = cursor.getInt(cursor.getColumnIndexOrThrow("sku_qty"));
            skuFreeQuantity = cursor.getInt(cursor.getColumnIndexOrThrow("sku_free_qty"));
            skuDiscount = cursor.getFloat(cursor.getColumnIndexOrThrow("sku_discount"));
            skuPrice = cursor.getFloat(cursor.getColumnIndexOrThrow("sku_price"));
        }

        discountModelArrayList.add(new QuantityDiscountModel(skuQuantity,skuFreeQuantity,skuDiscount,skuPrice));

        cursor.close();
        sqLiteDatabase.close();

        return discountModelArrayList;
    }

    public static void increaseSkuQuantityFreeQuantityDiscount(Long orderDetailID,float skuUnitPrice, int newSkuQuantity,int newFreeQty,float newSkuDisc)
    {
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);



        String skuPrice_beforeDiscount = String.valueOf(skuUnitPrice * newSkuQuantity);
        String skuFinalPrice = String.valueOf(Float.parseFloat(skuPrice_beforeDiscount)-newSkuDisc);

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

        while(cursor.moveToNext())
        {
            String skuID = cursor.getString(cursor.getColumnIndexOrThrow("sku_id"));
            String skuName = cursor.getString(cursor.getColumnIndexOrThrow("sku_name"));
            String skuPrice = cursor.getString(cursor.getColumnIndexOrThrow("sku_price"));
            String skuCategory = cursor.getString(cursor.getColumnIndexOrThrow("sku_category"));
            String sku_category_description = cursor.getString(cursor.getColumnIndexOrThrow("sku_category_description"));
            String sku_photo_source = cursor.getString(cursor.getColumnIndexOrThrow("sku_photo_source"));

            skuList.add(new Sku(skuID, skuName, skuPrice, skuCategory,sku_category_description,sku_photo_source));
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

        while(cursor.moveToNext())
        {
            String skuID = cursor.getString(cursor.getColumnIndexOrThrow("sku_id"));
            String skuName = cursor.getString(cursor.getColumnIndexOrThrow("sku_name"));
            String skuPrice = cursor.getString(cursor.getColumnIndexOrThrow("sku_price"));
            String skuCategory = cursor.getString(cursor.getColumnIndexOrThrow("sku_category"));
            String sku_category_description = cursor.getString(cursor.getColumnIndexOrThrow("sku_category_description"));
            String sku_photo_source = cursor.getString(cursor.getColumnIndexOrThrow("sku_photo_source"));

            skuList.add(new Sku(skuID, skuName, skuPrice, skuCategory,sku_category_description,sku_photo_source));
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

        while(cursor.moveToNext())
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

        while(cursor.moveToNext())
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

            skuList.add(new SalesOrderSku(orderDetailId, skuID, skuName, skuPrice, skuQty,sku_free_qty,sku_discount,sku_price_before_discount,sku_final_price));
        }

        cursor.close();
        sqLiteDatabase.close();

        return skuList;
    }

    public static float getOrderTotal(String orderID)
    {
        float orderTotal = -1;
        String SQL_SELECT_ORDER_TOTAL = "SELECT SUM(sku_final_price) total FROM sales_order_details WHERE order_id = ? ;";
        String[] selectionArgs = {orderID};

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_ORDER_TOTAL, selectionArgs);

        if(cursor.moveToNext())
        {
            orderTotal = cursor.getInt(cursor.getColumnIndexOrThrow("total"));
        }

        cursor.close();
        sqLiteDatabase.close();
        orderTotal=(float)orderTotal;
        return orderTotal;
    }

  public static float getOrderTotalFromSalesOrderTBL(String orderID)
    {
        float orderTotal = -1;
        String SQL_SELECT_ORDER_TOTAL = "SELECT total_order_value as total FROM "+TBL_SALES_ORDER+ " WHERE order_id = ? ;";
        String[] selectionArgs = {orderID};

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_ORDER_TOTAL, selectionArgs);

        if(cursor.moveToNext())
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

        if(cursor.moveToNext())
        {
            orderTotal = cursor.getInt(cursor.getColumnIndexOrThrow("total"));
        }

        cursor.close();
        sqLiteDatabase.close();
        orderTotal=(float)orderTotal;
        return orderTotal;
    }

    public static int getItemCount(String orderID)
    {
        int itemCount = -1;
        String SQL_SELECT_ITEM_COUNT = "SELECT COUNT(*) count FROM sales_order_details WHERE order_id = ? ;";
        String[] selectionArgs = {orderID};

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_ITEM_COUNT, selectionArgs);

        if(cursor.moveToNext())
        {
            itemCount = cursor.getInt(cursor.getColumnIndexOrThrow("count"));
        }

        cursor.close();
        sqLiteDatabase.close();

        return itemCount;
    }

    public static void insertIntoLocationHierarchy(int locationID, String locationName, String hierarchyLevel,String full_hier_level, String parentLocationID)
    {
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        ContentValues locationValues = new ContentValues();
        locationValues.put("loc_id", locationID);
        locationValues.put("loc_name", locationName);
        locationValues.put("hier_level", hierarchyLevel);
        locationValues.put("full_hier_level", full_hier_level);
        locationValues.put("parent_loc_id", parentLocationID);
        locationValues.put("upload_status", 0);

        sqLiteDatabase.insert(TBL_LOCATION_HIERARCHY, null, locationValues);

        sqLiteDatabase.close();
    }


    public static boolean isAttribute_IDPresentInDb(String attr_id) {
        boolean isattrPresent = false;
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(MyDb.openDatabase(Constants.dbFileFullPath));
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT attribute_id FROM " + Constants.TBL_GLOBAL_ATTRIBUTES + " WHERE attribute_id = ? ", new String[]{attr_id});
        if (cursor.moveToFirst()) {
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

        String sku_photo_source=null;

        String SQL_sku_Photo_url = "select  sku_photo_source from " + TBL_SKU + " WHERE sku_id = ? ;";
        String selectionargs[]={frequentlyskuID};

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_sku_Photo_url,selectionargs );

        if(cursor.moveToFirst())
        {
            sku_photo_source  = cursor.getString(cursor.getColumnIndexOrThrow("sku_photo_source"));
        }

        cursor.close();
        sqLiteDatabase.close();
        return sku_photo_source;
    }


    public static String getSkuVideoURL(String sku_id)
    {

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String sku_video_source=null;

        String SQL_sku_Photo_url = "select  sku_video_source from " + TBL_SKU + " WHERE sku_id = ? ;";
        String selectionargs[]={sku_id};

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_sku_Photo_url,selectionargs );

        if(cursor.moveToFirst())
        {
            sku_video_source  = cursor.getString(cursor.getColumnIndexOrThrow("sku_video_source"));
        }

        cursor.close();
        sqLiteDatabase.close();
        return sku_video_source;


    }

  public static String getSkuCatalogueURL(String sku_id)
    {

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String sku_catalogue_source=null;

        String sku_catalogue_source_query = "select  sku_catalogue_source from " + TBL_SKU + " WHERE sku_id = ? ;";
        String selectionargs[]={sku_id};

        Cursor cursor = sqLiteDatabase.rawQuery(sku_catalogue_source_query,selectionargs );

        if(cursor.moveToFirst())
        {
            sku_catalogue_source  = cursor.getString(cursor.getColumnIndexOrThrow("sku_catalogue_source"));
        }

        cursor.close();
        sqLiteDatabase.close();
        return sku_catalogue_source;


    }

    public static void clear_table(String tablename){

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);
        sqLiteDatabase.delete(tablename,null,null);
    }


    public static boolean isRetailerPresentInDb(String retailerid)
    {
        boolean isRetailerPresent = false;

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String SQL_SELECT_RETAILER =
                "SELECT" + " retailer_id " + "FROM " + TBL_RETAILER + " WHERE " + "retailer_id " + "= ? " ;
        String[] selectionArgs = {retailerid};
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_RETAILER, selectionArgs);

        if(cursor.moveToFirst())
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
                "SELECT" + " sku_id " + "FROM " + TBL_SKU + " WHERE " + "sku_id " + "= ? " ;
        String[] selectionArgs = {skuid};
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_sku, selectionArgs);

        if(cursor.moveToFirst())
        {
            isskuPresent = true;
        }

        cursor.close();
        sqLiteDatabase.close();

        return isskuPresent;
    }



 public static boolean isReasonIdInDb(String reasonid)
    {
        boolean isReason = false;

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String SQL_SELECT_REASON = "SELECT" + " reason_id " + "FROM " + TBL_SKU_NO_ORDERREASON + " WHERE " + "reason_id " + "= ? " ;
        String[] selectionArgs = {reasonid};
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_REASON, selectionArgs);

        if(cursor.moveToFirst())
        {
            isReason = true;
        }

        cursor.close();
        sqLiteDatabase.close();

        return isReason;
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

        boolean is_value=false;

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String isPromotionQuery = "SELECT config_value from " + Constants.TBL_CONFIG + " WHERE config_for =? ";
        String[] selectionArgs = new String[]{configfor};

        Cursor cursor = sqLiteDatabase.rawQuery(isPromotionQuery, selectionArgs);

        if(cursor.moveToFirst()){

            if(cursor.getString(cursor.getColumnIndexOrThrow("config_value")).equalsIgnoreCase("Yes")){

                is_value=true;
            }
        }

        return is_value;
    }

    public static String getLocationName(String Location_Id){

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String location_name=null;

        String SQL_SELECT_LOCATION = "SELECT " + "loc_name" + " FROM " + TBL_LOCATION_HIERARCHY + " WHERE " + "loc_id = ?";
        String[] selectionArgs = {Location_Id};

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_LOCATION,selectionArgs );

        if(cursor.moveToFirst())
        {
            location_name  = cursor.getString(cursor.getColumnIndexOrThrow("loc_name"));
        }

        cursor.close();
        sqLiteDatabase.close();
        return location_name;

    }


    public static String getActiveRetailer(String activeOrderId)
    {

        String active_retailer_name=NONE;

        String SQL_QUERY_ACTIVE_RETAILER="SELECT retailer.retailer_name from "+TBL_RETAILER+" retailer "+" INNER JOIN "+TBL_SALES_ORDER+" so "+" ON retailer.retailer_id = so.retailer_id "+" WHERE so.order_id = ?";
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(MyDb.openDatabase(Constants.dbFileFullPath));

        String[] selectionArgs = {String.valueOf(activeOrderId)};

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_QUERY_ACTIVE_RETAILER, selectionArgs);
        while (cursor.moveToNext()) {

            active_retailer_name= cursor.getString(cursor.getColumnIndexOrThrow("retailer_name"));
        }
        cursor.close();
        sqLiteDatabase.close();
        return active_retailer_name;
    }


    public static List<SkuGroupHistory> getCustomSkuList()
    {
        String SQL_SELECT_SKUs=null;
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

            skuList.add(new SkuGroupHistory(skuID,skuName));
        }

        cursor.close();
        sqLiteDatabase.close();

        return skuList;
    }

}
