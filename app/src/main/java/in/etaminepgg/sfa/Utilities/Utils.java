package in.etaminepgg.sfa.Utilities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.Dimension;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import in.etaminepgg.sfa.Activities.DashboardActivity;
import in.etaminepgg.sfa.Activities.LoginActivity;
import in.etaminepgg.sfa.Activities.PickRetailerActivity;
import in.etaminepgg.sfa.R;

import static in.etaminepgg.sfa.Activities.LoginActivity.baseContext;
import static in.etaminepgg.sfa.Utilities.Constants.REQUEST_TURN_ON_LOCATION;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_GLOBAL_ATTRIBUTES;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SALES_ORDER_SKU_ATTRIBUTES;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SKU_ATTRIBUTE_MAPPING;
import static in.etaminepgg.sfa.Utilities.Constants.dbFileFullPath;
import static in.etaminepgg.sfa.Utilities.ConstantsA.NONE;
import static in.etaminepgg.sfa.Utilities.DbUtils.getActiveOrderID;
import static in.etaminepgg.sfa.Utilities.DbUtils.getSkuQuantity;
import static in.etaminepgg.sfa.Utilities.DbUtils.isSkuPresent;

/**
 * Created by etamine on 30/5/17.
 */

public class Utils
{
    //both will be updated in LoginActivity
    public static String loggedInUserName;
    public static String loggedInUserID;

    private Map<Integer, String> selectedSkuAttributesMap = new HashMap<>();

    public static void launchActivity(Context context, Class<?> activityClassToLaunch)
    {
        Intent intent = new Intent(context, activityClassToLaunch);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void launchActivityWithExtra(Context context, Class<?> activityClassToLaunch, String extraName, String extraValue)
    {
        Intent intent = new Intent(context, activityClassToLaunch);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(extraName, extraValue);
        context.startActivity(intent);
    }

    // toast for long duration
    public static void showPopUp(Context context, String messageToShow)
    {
        Toast popUp = Toast.makeText(context, messageToShow, Toast.LENGTH_LONG);
        View popUpView = popUp.getView();
        popUpView.setBackgroundColor(Color.BLACK);
        popUpView.setPadding(20, 20, 20, 20);
        popUp.setGravity(Gravity.CENTER, 0, 0);
        popUp.show();
    }

    // toast for short duration
    public static void showToast(Context context, String messageToShow)
    {
        Toast toast = Toast.makeText(context, messageToShow, Toast.LENGTH_SHORT);
        View toastView = toast.getView();
        toastView.setBackgroundColor(Color.BLACK);
        toastView.setPadding(20, 20, 20, 20);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void showErrorDialog(Context context, String messageToShow)
    {
        final Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.custom_dialog);

        TextView icon_TextView = (TextView) dialog.findViewById(R.id.icon_TextView);
        TextView title_TextView = (TextView) dialog.findViewById(R.id.title_TextView);
        TextView msg_TextView = (TextView) dialog.findViewById(R.id.msg_TextView);
        Button dismiss_Button = (Button) dialog.findViewById(R.id.dismiss_Button);

        icon_TextView.setBackground(context.getResources().getDrawable(R.drawable.ic_error));

        title_TextView.setText("Error!");

        msg_TextView.setText(messageToShow);

        dismiss_Button.setBackgroundColor(context.getResources().getColor(R.color.errorColor));

        dismiss_Button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static void showSuccessDialog(Context context, String messageToShow)
    {
        final Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.custom_dialog);

        TextView icon_TextView = (TextView) dialog.findViewById(R.id.icon_TextView);
        TextView title_TextView = (TextView) dialog.findViewById(R.id.title_TextView);
        TextView msg_TextView = (TextView) dialog.findViewById(R.id.msg_TextView);
        Button dismiss_Button = (Button) dialog.findViewById(R.id.dismiss_Button);

        icon_TextView.setBackground(context.getResources().getDrawable(R.drawable.ic_check_circle));

        title_TextView.setText("Success!");

        msg_TextView.setText(messageToShow);

        dismiss_Button.setBackgroundColor(context.getResources().getColor(R.color.successColor));

        dismiss_Button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static void makeThreadSleepFor(int milliSeconds)
    {
        try
        {
            Thread.sleep(milliSeconds);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    private static Timestamp getTimestamp()
    {
        return new Timestamp(System.currentTimeMillis());
    }

    // IST means Indian Standard Time
    // used for visit_id, order_id rows in database
    public static String getIST()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy:HH.mm.ss");
        return sdf.format(getTimestamp());
    }

    // returns Timestamp in SQL format
    // used for visit date, order_date
    public static String getDateTime()
    {
        return new Timestamp(System.currentTimeMillis()).toString();
    }

    //this date format is used to filter database results in SQL queries
    public static String getTodayDate()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(getTimestamp());
    }

    public static String getDeviceId(Context context)
    {
        String imeiNumber = null;

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)
        {
            TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            imeiNumber = telephonyManager.getDeviceId();
        }else {
            String[] permissionsArray = {"android.permission.READ_PHONE_STATE"};

            ActivityCompat.requestPermissions((Activity)context, permissionsArray, 1);
        }

        return imeiNumber;
    }

    //returns values between 1 Lakh to 11 Lakh
    public static int getRandomNumber()
    {
        Random random = new Random();
        int min = 100000;
        int max = 1100001;
        return random.nextInt(max) + min;
    }

    ///home/etamine/AndroidStudioProjects/SFA/app/src/main/assets
    public static void copyFile(AssetManager assetManager, String copyTo, String filename) throws Exception
    {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        String newFileName = copyTo + File.separator + filename;

        inputStream = assetManager.open(filename);
        outputStream = new FileOutputStream(newFileName);

        byte[] buffer = new byte[1048576];
        int bytesRead;
        long totalBytesRead = 0;

        while ((bytesRead = inputStream.read(buffer)) != -1)
        {
            totalBytesRead += bytesRead;
            outputStream.write(buffer, 0, bytesRead);

            if (totalBytesRead > 1024 * 1024)
            {
                totalBytesRead = 0;
                outputStream.flush();
            }
        }

        inputStream.close();
        outputStream.flush();
        outputStream.close();
    }

    public static boolean isGpsEnabled(Context context)
    {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            try
            {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            }
            catch (Settings.SettingNotFoundException e)
            {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        }
        else
        {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    public static void enableGPS(GoogleApiClient googleApiClient, LocationRequest locationRequest, final Activity activity)
    {
        LocationSettingsRequest.Builder locationSettingsRequestBuilder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        locationSettingsRequestBuilder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> locationSettingsResult
                = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, locationSettingsRequestBuilder.build());

        locationSettingsResult.setResultCallback(new ResultCallback<LocationSettingsResult>()
        {
            @Override
            public void onResult(@NonNull LocationSettingsResult settingsResult)
            {
                final Status status = settingsResult.getStatus();

                switch (status.getStatusCode())
                {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try
                        {
                            status.startResolutionForResult(activity, REQUEST_TURN_ON_LOCATION);
                        }
                        catch (IntentSender.SendIntentException e)
                        {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        });
    }

    public List<Integer> getAttributeIDs(String skuID)
    {
        List<Integer> attributeIDsList = new ArrayList<>();

        String SQL_SELECT_ATTRIBUTE_LIST = "SELECT attribute_id FROM " + TBL_SKU_ATTRIBUTE_MAPPING + " WHERE sku_id = ? ;";
        String[] selectionArgs = {skuID};

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_ATTRIBUTE_LIST, selectionArgs);

        while (cursor.moveToNext())
        {
            int attributeID = cursor.getInt(cursor.getColumnIndexOrThrow("attribute_id"));
            attributeIDsList.add(attributeID);
        }

        cursor.close();
        sqLiteDatabase.close();

        return attributeIDsList;
    }

    public String getAttributeName(int attributeID)
    {
        String attributeName = NONE;

        String SQL_SELECT_ATTRIBUTE_NAME = "SELECT attribute_name FROM " + TBL_GLOBAL_ATTRIBUTES + " WHERE attribute_id = ? ;";
        String[] selectionArgs = {String.valueOf(attributeID)};

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_ATTRIBUTE_NAME, selectionArgs);

        if (cursor.moveToNext())
        {
            attributeName = cursor.getString(cursor.getColumnIndexOrThrow("attribute_name"));
        }

        cursor.close();
        sqLiteDatabase.close();

        return attributeName;
    }

    public String getAttributeValueSet(int attributeID)
    {
        String attributeValueSet = NONE;

        String SQL_SELECT_ATTRIBUTE_VALUE_SET = "SELECT attribute_value FROM " + TBL_GLOBAL_ATTRIBUTES + " WHERE attribute_id = ? ;";
        String[] selectionArgs = {String.valueOf(attributeID)};

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_ATTRIBUTE_VALUE_SET, selectionArgs);

        if (cursor.moveToNext())
        {
            attributeValueSet = cursor.getString(cursor.getColumnIndexOrThrow("attribute_value"));
        }

        cursor.close();
        sqLiteDatabase.close();

        return attributeValueSet;
    }

    //dynamically builds View For alert dialog depending on attributes of SKU
    public View constructViewForAttributesDialog(String skuID, Context context)
    {
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        final float scale = context.getResources().getDisplayMetrics().density;
        int pixels = (int) (35 * scale + 0.5f);

        ViewGroup.LayoutParams lp2 = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, pixels);

        ScrollView scrollView = new ScrollView(context);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        scrollView.setLayoutParams(lp1);
        linearLayout.setLayoutParams(lp1);
        linearLayout.setPadding(15, 15, 15, 15);

        List<Integer> attributeIDsList = getAttributeIDs(skuID);

        for (final int attributeID : attributeIDsList)
        {
            TextView textView = new TextView(context);
            textView.setLayoutParams(lp2);
            textView.setText("choose from " + getAttributeName(attributeID));
            textView.setTextSize(Dimension.SP, 25);
            textView.setTextColor(Color.RED);
            textView.setGravity(Gravity.CENTER_VERTICAL);
            linearLayout.addView(textView);

            Spinner spinner = new Spinner(context);
            spinner.setLayoutParams(lp2);
            spinner.setBackgroundColor(Color.CYAN);

            spinner.setId(attributeID);

            //attributeValuesSet contains all values separated by `
            String attributeValuesSet = getAttributeValueSet(attributeID);
            String[] attributeValues = attributeValuesSet.split("`");

            ArrayAdapter<String> attributeValuesAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, attributeValues);
            attributeValuesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(attributeValuesAdapter);

            linearLayout.addView(spinner);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                {
                    String attributeValue = parent.getItemAtPosition(position).toString();

                    selectedSkuAttributesMap.put(attributeID, attributeValue);

                    Log.e("selected Value", attributeID + " : " + getAttributeName(attributeID) + " : " + attributeValue);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent)
                {

                }
            });
        }

        scrollView.addView(linearLayout);

        return scrollView;
    }

    //don't make this method static. Will not work inside ViewHolders
    //Note: ViewHolders are inner classes of Adapters
    public void pickAttributeValuesOrSelectRetailer(String skuID, String skuName, String skuPrice, Context context)
    {
        String salesOrderID = getActiveOrderID();

        if (!salesOrderID.equals(NONE))
        {
            View alertDialogView = constructViewForAttributesDialog(skuID, context);

            showSelectAttributesDialog(alertDialogView, salesOrderID, skuID, skuName, skuPrice, context);
        }
        else
        {
            Utils.launchActivity(context, PickRetailerActivity.class);
        }
    }

    void showSelectAttributesDialog(View alertDialogView, final String salesOrderID, final String skuID, final String skuName, final String skuPrice, Context context)
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setView(alertDialogView);

        dialogBuilder.setPositiveButton("add to cart", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                insertSkuOrIncreaseQuantity(salesOrderID, skuID, skuName, skuPrice);
            }
        });

        dialogBuilder.setNegativeButton("cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });

        dialogBuilder.show();
    }

    void insertSkuOrIncreaseQuantity(String salesOrderID, String skuID, String skuName, String skuPrice)
    {
        //if Sku doesn't exists in the sales order details table, insert new row for that Sku
        if (!isSkuPresent(skuID, salesOrderID))
        {
            long rowID = DbUtils.insertIntoSalesOrderDetailsTable(salesOrderID, skuID, skuName, skuPrice, "1");

            if (rowID != -1L)
            {
                long salesOrderDetailID = rowID;

                insertIntoSalesOrderSkuAttributes(salesOrderDetailID);

                Utils.showPopUp(LoginActivity.baseContext, "SKU added to cart for 1st time");
            }
        }
        else
        {
            List<Long> orderDetailIDsList = getOrderDetailIDsList(salesOrderID, skuID);

            long orderDetailIdWithSameAttributes = getOrderDetailIdWithSameAttributes(orderDetailIDsList);

            if (orderDetailIdWithSameAttributes > 0)
            {
                int currentSkuQuantity = getSkuQuantity(orderDetailIdWithSameAttributes);
                int newSkuQuantity = currentSkuQuantity + 1;
                DbUtils.increaseSkuQuantity(orderDetailIdWithSameAttributes, newSkuQuantity);

                Utils.showPopUp(LoginActivity.baseContext, "SKU quantity increased");
            }
            else if (orderDetailIdWithSameAttributes == -1L)
            {
                long rowID = DbUtils.insertIntoSalesOrderDetailsTable(salesOrderID, skuID, skuName, skuPrice, "1");

                if (rowID != -1L)
                {
                    long salesOrderDetailID = rowID;

                    insertIntoSalesOrderSkuAttributes(salesOrderDetailID);

                    Utils.showPopUp(LoginActivity.baseContext, "SKU with different attributes added to cart");

                }
            }
        }

        //Utils.showToast(context, skuName + "(id: " + skuID + ")" + "added to sales order");
    }

    void insertIntoSalesOrderSkuAttributes(long salesOrderDetailID)
    {
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        for (Map.Entry<Integer, String> entry : selectedSkuAttributesMap.entrySet())
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put("order_detail_id", salesOrderDetailID);
            contentValues.put("attribute_id", entry.getKey());
            contentValues.put("attribute_name", getAttributeName(entry.getKey()));
            contentValues.put("attribute_value", entry.getValue());
            contentValues.put("upload_status", 0);
            sqLiteDatabase.insert(TBL_SALES_ORDER_SKU_ATTRIBUTES, null, contentValues);
        }

        sqLiteDatabase.close();
    }


    List<Long> getOrderDetailIDsList(String salesOrderID, String skuID)
    {
        List<Long> orderDetailIDsList = new ArrayList<>();

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String SQL_SELECT_ORDER_DETAIL_IDS = "SELECT DISTINCT order_detail_id FROM sales_order_details WHERE order_id = ? AND sku_id = ? ;";
        String[] selectionArgs = {salesOrderID, skuID};

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_ORDER_DETAIL_IDS, selectionArgs);

        while (cursor.moveToNext())
        {
            Long orderDetailId = cursor.getLong(cursor.getColumnIndexOrThrow("order_detail_id"));

            orderDetailIDsList.add(orderDetailId);
        }

        cursor.close();
        sqLiteDatabase.close();

        return orderDetailIDsList;
    }

    Map<Integer, String> getSalesOrderSkuAttributesMap(long orderDetailID)
    {
        Map<Integer, String> salesOrderSkuAttributesMap = new HashMap<>();

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String SQL_SELECT_SALES_ORDER_ATTRIBUTES_SET = "SELECT attribute_id, attribute_value FROM sales_order_sku_attributes WHERE order_detail_id = ? ;";
        String[] selectionArgs = {String.valueOf(orderDetailID)};

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_SALES_ORDER_ATTRIBUTES_SET, selectionArgs);

        while (cursor.moveToNext())
        {
            int attributeId = cursor.getInt(cursor.getColumnIndexOrThrow("attribute_id"));
            String attributeValue = cursor.getString(cursor.getColumnIndexOrThrow("attribute_value"));

            salesOrderSkuAttributesMap.put(attributeId, attributeValue);
        }

        cursor.close();
        sqLiteDatabase.close();

        return salesOrderSkuAttributesMap;
    }

    long getOrderDetailIdWithSameAttributes(List<Long> orderDetailIDsList)
    {
        for (Long orderDetailID : orderDetailIDsList)
        {
            Map<Integer, String> salesOrderSkuAttributesMap = getSalesOrderSkuAttributesMap(orderDetailID);

            if (selectedSkuAttributesMap.equals(salesOrderSkuAttributesMap))
            {
                return orderDetailID;
            }
        }

        return -1L;
    }


    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
}
