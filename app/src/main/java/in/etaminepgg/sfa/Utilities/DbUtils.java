package in.etaminepgg.sfa.Utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import in.etaminepgg.sfa.Models.SalesOrderSku;
import in.etaminepgg.sfa.Models.Sku;

import static in.etaminepgg.sfa.Utilities.Constants.TBL_LOCATION_HIERARCHY;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_RETAILER;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_RETAILER_VISIT;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SALES_ORDER;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SALES_ORDER_DETAILS;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SKU;
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
        String[] selectionArgs = {salesPersonId, getTodayDate() + "%"};

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
        sqLiteDatabase.update(TBL_SALES_ORDER, salesOrderValues, "is_active = ?", new String[]{"1"});

        sqLiteDatabase.close();
    }

    //returns order_id of active order, if active order exists
    //returns getActiveOrderID() if there is no active order
    public static String getActiveOrderID()
    {
        String activeOrderID = NONE;
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String SQL_SELECT_ACTIVE_SALES_ORDER_ID = "select order_id from " + TBL_SALES_ORDER + " WHERE " + "is_active = ?";
        String[] selectionArgs = new String[]{"1"};
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_ACTIVE_SALES_ORDER_ID, selectionArgs);

        if(cursor.moveToFirst())
        {
            activeOrderID = cursor.getString(cursor.getColumnIndexOrThrow("order_id"));
        }

        cursor.close();
        sqLiteDatabase.close();

        return activeOrderID;
    }

    //returns Retailer's Regular Order Id
    //returns NONE if there is no regular order
    public static String getRegularOrderIdFor(String retailerID)
    {
        String regularOrderID = NONE;
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);
        String SQL_SELECT_REGULAR_ORDER_ID = "SELECT " + "order_id" + " FROM " + TBL_SALES_ORDER + " WHERE " + "retailer_id = ? AND " + "is_regular = ?";
        String[] selectionArgs = {retailerID, "1"};

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
        String SQL_SELECT_RETAILER_ID_OF_ACTIVE_ORDER = "SELECT " + "retailer_id" + " FROM " + TBL_SALES_ORDER + " WHERE " + "is_active = ?";
        String[] selectionArgs = {"1"};

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_RETAILER_ID_OF_ACTIVE_ORDER, selectionArgs);
        if(cursor.moveToFirst())
        {
            retailerID = cursor.getString(cursor.getColumnIndexOrThrow("retailer_id"));
        }
        cursor.close();
        sqLiteDatabase.close();

        return retailerID;
    }

    public static long insertIntoSalesOrderDetailsTable(String orderID, String skuID, String skuName, String skuPrice, String skuQty)
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

    public static int getSkuQuantity(Long orderDetailID)
    {
        int skuQuantity = -100;

        String sqlQuery = "SELECT sku_qty FROM sales_order_details WHERE order_detail_id = ? ;";
        String[] selectionArgs = {String.valueOf(orderDetailID)};

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        Cursor cursor = sqLiteDatabase.rawQuery(sqlQuery, selectionArgs);

        if(cursor.moveToNext())
        {
            skuQuantity = cursor.getInt(cursor.getColumnIndexOrThrow("sku_qty"));
        }

        cursor.close();
        sqLiteDatabase.close();

        return skuQuantity;
    }

    public static void increaseSkuQuantity(Long orderDetailID, int newSkuQuantity)
    {
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        ContentValues salesOrderDetailsValues = new ContentValues();
        salesOrderDetailsValues.put("sku_qty", newSkuQuantity);

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
            String sku_photo_source = cursor.getString(cursor.getColumnIndexOrThrow("sku_photo_source"));

            skuList.add(new Sku(skuID, skuName, skuPrice, skuCategory,sku_photo_source));
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

            skuList.add(new SalesOrderSku(orderDetailId, skuID, skuName, skuPrice, skuQty));
        }

        cursor.close();
        sqLiteDatabase.close();

        return skuList;
    }

    public static int getOrderTotal(String orderID)
    {
        int orderTotal = -1;
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

    public static void insertIntoLocationHierarchy(int locationID, String locationName, String hierarchyLevel, String parentLocationID)
    {
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        ContentValues locationValues = new ContentValues();
        locationValues.put("loc_id", locationID);
        locationValues.put("loc_name", locationName);
        locationValues.put("hier_level", hierarchyLevel);
        locationValues.put("parent_loc_id", parentLocationID);
        locationValues.put("upload_status", 0);

        sqLiteDatabase.insert(TBL_LOCATION_HIERARCHY, null, locationValues);

        sqLiteDatabase.close();
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

}
