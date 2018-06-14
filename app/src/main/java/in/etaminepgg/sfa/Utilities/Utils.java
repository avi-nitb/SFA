package in.etaminepgg.sfa.Utilities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

import in.etaminepgg.sfa.Activities.LoginActivity;
import in.etaminepgg.sfa.Activities.PickRetailerActivity;
import in.etaminepgg.sfa.Models.QuantityDiscountModel;
import in.etaminepgg.sfa.R;

import static in.etaminepgg.sfa.Utilities.Constants.REQUEST_TURN_ON_LOCATION;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_GLOBAL_ATTRIBUTES;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SALES_ORDER_SKU_ATTRIBUTES;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SKU_ATTRIBUTE_MAPPING;
import static in.etaminepgg.sfa.Utilities.Constants.dbFileFullPath;
import static in.etaminepgg.sfa.Utilities.ConstantsA.NONE;
import static in.etaminepgg.sfa.Utilities.DbUtils.getActiveOrderID;
import static in.etaminepgg.sfa.Utilities.DbUtils.getSkuQuantityDiscount;
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
    private Map<String, String> selectedSkuQuantityMap;

    public static void launchActivity(Context context, Class<?> activityClassToLaunch)
    {
        Intent intent = new Intent(context, activityClassToLaunch);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void launchActivityWithExtra(Context context, Class<?> activityClassToLaunch, String extraName, String extraValue)
    {
        Intent intent = new Intent(context, activityClassToLaunch);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(extraName, extraValue);
        context.startActivity(intent);
    }

    // toast for long duration
    public static void showPopUp(Context context, String messageToShow)
    {
        Toast popUp = Toast.makeText(context, messageToShow, Toast.LENGTH_LONG);
//        View popUpView = popUp.getView();
//        popUpView.setBackgroundColor(Color.BLACK);
//        popUpView.setPadding(20, 20, 20, 20);
//        popUp.setGravity(Gravity.CENTER, 0, 0);
        popUp.show();
    }

    // toast for short duration
    public static void showToast(Context context, String messageToShow)
    {
        Toast toast = Toast.makeText(context, messageToShow, Toast.LENGTH_SHORT);
        //       View toastView = toast.getView();
//        toastView.setBackgroundColor(Color.BLACK);
//        toastView.setPadding(20, 20, 20, 20);
//        toast.setGravity(Gravity.CENTER, 0, 0);
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
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            imeiNumber = telephonyManager.getDeviceId();
        }
        else
        {
            String[] permissionsArray = {"android.permission.READ_PHONE_STATE"};

            ActivityCompat.requestPermissions((Activity) context, permissionsArray, 1);
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

    public static boolean isNetworkConnected(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    public static void startProgressDialog(Context context, ProgressDialog progressDialog)
    {

        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Please Wait....");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressNumberFormat(null);
        progressDialog.setProgressPercentFormat(null);
        progressDialog.show();
    }

    public static void dismissProgressDialog(ProgressDialog progressDialog)
    {

        if (progressDialog.isShowing())
        {

            progressDialog.dismiss();
        }
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

    @SuppressLint("ResourceType")
    public View constructViewForAttributesDialog(String skuID, final Context context)
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

        TextView textView1 = new TextView(context);
        textView1.setLayoutParams(lp2);
        textView1.setText("Quantity");
        textView1.setTextSize(Dimension.SP, 20);
        textView1.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
        textView1.setGravity(Gravity.CENTER_VERTICAL);
        linearLayout.addView(textView1);

       /* Spinner spinner_qty = new Spinner(context);
        spinner_qty.setLayoutParams(lp2);
        spinner_qty.setBackgroundColor(Color.CYAN);

        String[] intArray = new String[20];
        for(int i = 0; i < 20; i++) {

            intArray[i] = String.valueOf(i+1);
        }

        ArrayAdapter<String> qty_spinner_adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, intArray);
        qty_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_qty.setAdapter(qty_spinner_adapter);

        linearLayout.addView(spinner_qty);*/

        EditText qty_edit = new EditText(context);
        qty_edit.setLayoutParams(lp2);
        qty_edit.setBackgroundColor(context.getResources().getColor(R.color.lightgray));
        qty_edit.setInputType(InputType.TYPE_CLASS_NUMBER);
        qty_edit.setPadding(10, 0, 0, 0);
        qty_edit.setText("1");
        qty_edit.setId(R.string.tag_sky_qty_id);
        qty_edit.setTextSize(Dimension.SP, 13);
        qty_edit.setTextColor(Color.BLACK);
        qty_edit.setGravity(Gravity.CENTER_VERTICAL);
        linearLayout.addView(qty_edit);


        ///for free quantity

        TextView freeQty_tv = new TextView(context);
        freeQty_tv.setLayoutParams(lp2);
        freeQty_tv.setText("Free Quantity");
        freeQty_tv.setTextSize(Dimension.SP, 20);
        freeQty_tv.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
        freeQty_tv.setGravity(Gravity.CENTER_VERTICAL);
        linearLayout.addView(freeQty_tv);

        EditText freeQty_edit = new EditText(context);
        freeQty_edit.setLayoutParams(lp2);
        freeQty_edit.setBackgroundColor(context.getResources().getColor(R.color.lightgray));
        freeQty_edit.setInputType(InputType.TYPE_CLASS_NUMBER);
        freeQty_edit.setPadding(10, 0, 0, 0);
        freeQty_edit.setText("1");
        freeQty_edit.setId(R.string.tag_sky_freeQty_id);
        freeQty_edit.setTextSize(Dimension.SP, 13);
        freeQty_edit.setTextColor(Color.BLACK);
        freeQty_edit.setGravity(Gravity.CENTER_VERTICAL);
        linearLayout.addView(freeQty_edit);

        //for sku discount

        TextView discount_tv = new TextView(context);
        discount_tv.setLayoutParams(lp2);
        discount_tv.setText("SKU Discount(Rs.)");
        discount_tv.setTextSize(Dimension.SP, 20);
        discount_tv.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
        discount_tv.setGravity(Gravity.CENTER_VERTICAL);
        linearLayout.addView(discount_tv);

        EditText discount_edit = new EditText(context);
        discount_edit.setLayoutParams(lp2);
        discount_edit.setBackgroundColor(context.getResources().getColor(R.color.lightgray));
        discount_edit.setInputType(InputType.TYPE_CLASS_NUMBER);
        discount_edit.setPadding(10, 0, 0, 0);
        discount_edit.setText("1");
        discount_edit.setId(R.string.tag_sky_discount_id);
        discount_edit.setTextSize(Dimension.SP, 13);
        discount_edit.setTextColor(Color.BLACK);
        discount_edit.setGravity(Gravity.CENTER_VERTICAL);
        linearLayout.addView(discount_edit);

        selectedSkuQuantityMap = new HashMap<>();

        selectedSkuQuantityMap.put(context.getResources().getString(R.string.tag_sky_qty_id), qty_edit.getText().toString().trim());
        selectedSkuQuantityMap.put(context.getResources().getString(R.string.tag_sky_freeQty_id), qty_edit.getText().toString().trim());
        selectedSkuQuantityMap.put(context.getResources().getString(R.string.tag_sky_discount_id), qty_edit.getText().toString().trim());

        //linearLayout.addView(qty_edit);



       /* LinearLayout linearLayout_quantity = new LinearLayout(context);
        linearLayout_quantity.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout_quantity.setLayoutParams(lp2);
        linearLayout_quantity.setGravity(Gravity.CENTER);
        linearLayout_quantity.setBackgroundColor(Color.CYAN);

        ViewGroup.LayoutParams lp_wrapcontent = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        ImageView imageview_minus= new ImageView(context);
        // Add image path from drawable folder.
        imageview_minus.setImageResource(R.drawable.ic_remove_black_24dp);
        imageview_minus.setLayoutParams(lp_wrapcontent);

        LinearLayout.LayoutParams params= new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(30, 0, 30, 0);

        final TextView txt_qty = new TextView(context);
        txt_qty.setLayoutParams(params);
        txt_qty.setText("1");
        txt_qty.setTextSize(Dimension.SP, 20);
        txt_qty.setTextColor(Color.RED);
        txt_qty.setGravity(Gravity.CENTER_VERTICAL);



        ImageView imageview_add = new ImageView(context);
        // Add image path from drawable folder.
        imageview_add.setImageResource(R.drawable.ic_add_black_24dp);
        imageview_add.setLayoutParams(lp_wrapcontent);


        linearLayout_quantity.addView(imageview_minus);
        linearLayout_quantity.addView(txt_qty);
        linearLayout_quantity.addView(imageview_add);

        linearLayout.addView(linearLayout_quantity);*/

        boolean is_Attribute = DbUtils.getConfigValue(Constants.SKU_ATTRIBUTE_REQUIRED);

        if (is_Attribute)
        {


            for (final int attributeID : attributeIDsList)
            {
                TextView textView = new TextView(context);
                textView.setLayoutParams(lp2);
                textView.setText("choose from " + getAttributeName(attributeID));
                textView.setTextSize(Dimension.SP, 20);
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
        }




     /*   imageview_add.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
               int qty=Integer.parseInt(txt_qty.getText().toString());
               qty++;
               txt_qty.setText(String.valueOf(qty));
            }
        });

        imageview_minus.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                int qty=Integer.parseInt(txt_qty.getText().toString());

                if(qty>0){
                    qty--;
                    txt_qty.setText(String.valueOf(qty));
                }

            }
        });*/

        qty_edit.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                //selectedSkuQuantityMap= new HashMap<>();

                selectedSkuQuantityMap.put(context.getResources().getString(R.string.tag_sky_qty_id), editable.toString().trim());


                Log.e("Sku Quantity Value", editable.toString().trim() + " : " + editable.toString().trim());
            }
        });

        //for free qty listener
        freeQty_edit.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                //selectedSkuQuantityMap= new HashMap<>();

                selectedSkuQuantityMap.put(context.getResources().getString(R.string.tag_sky_freeQty_id), editable.toString().trim());

                Log.e("Sku Free Quantity Value", editable.toString().trim() + " : " + editable.toString().trim());
            }
        });

        //for sku discount listener

        discount_edit.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                //selectedSkuQuantityMap= new HashMap<>();

                selectedSkuQuantityMap.put(context.getResources().getString(R.string.tag_sky_discount_id), editable.toString().trim());


                Log.e("Sku discount Value", editable.toString().trim() + " : " + editable.toString().trim());
            }
        });


     /*spinner_qty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
     {
         @Override
         public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
         {
             String skuQtyValue = adapterView.getItemAtPosition(i).toString();
             selectedSkuQuantityMap= new HashMap<>();
             selectedSkuQuantityMap.put(i, skuQtyValue);

             Log.e("Sku Quantity Value", i + " : " +  skuQtyValue);
         }

         @Override
         public void onNothingSelected(AdapterView<?> adapterView)
         {

         }
     });*/

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
            Utils.launchActivityWithExtra(context, PickRetailerActivity.class, Constants.SHOW_UPDATE_BUTTON, "NO");
        }
    }

    void showSelectAttributesDialog(final View alertDialogView, final String salesOrderID, final String skuID, final String skuName, final String skuPrice, final Context context)
    {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setView(alertDialogView);
        dialogBuilder.setPositiveButton("Add To Cart", null);
        dialogBuilder.setNegativeButton("Cancel", null);


        final AlertDialog mAlertDialog = dialogBuilder.create();


        mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener()
        {

            @Override
            public void onShow(DialogInterface dialog)
            {

                Button addToCart = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);

                Button cancel = mAlertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                addToCart.setOnClickListener(new View.OnClickListener()
                {

                    @Override
                    public void onClick(View view)
                    {
                        // TODO Do something

                        if (!selectedSkuQuantityMap.get(context.getResources().getString(R.string.tag_sky_qty_id)).isEmpty())
                        {
                            if (!selectedSkuQuantityMap.get(context.getResources().getString(R.string.tag_sky_freeQty_id)).isEmpty())
                            {

                                if (!selectedSkuQuantityMap.get(context.getResources().getString(R.string.tag_sky_discount_id)).isEmpty())
                                {

                                    insertSkuOrIncreaseQuantity(mAlertDialog, context, salesOrderID, skuID, skuName, skuPrice);


                                }
                                else
                                {
                                    Utils.showToast(LoginActivity.baseContext, "Discount can not be empty.");
                                }

                            }
                            else
                            {

                                Utils.showToast(LoginActivity.baseContext, "Free Quantity can not be empty.");
                            }

                        }
                        else
                        {
                            Utils.showToast(LoginActivity.baseContext, "Quantity can not be empty.");

                        }


                    }
                });


                cancel.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        mAlertDialog.dismiss();
                    }
                });
            }
        });


   /*     dialogBuilder.setPositiveButton("add to cart", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

                if (!selectedSkuQuantityMap.get(context.getResources().getString(R.string.tag_sky_qty_id)).isEmpty())
                {
                    if (!selectedSkuQuantityMap.get(context.getResources().getString(R.string.tag_sky_freeQty_id)).isEmpty())
                    {

                        if (!selectedSkuQuantityMap.get(context.getResources().getString(R.string.tag_sky_discount_id)).isEmpty())
                        {

                            insertSkuOrIncreaseQuantity(context, salesOrderID, skuID, skuName, skuPrice);

                        }
                        else
                        {
                            Utils.showToast(LoginActivity.baseContext, "Discount can not be empty.");
                        }

                    }
                    else
                    {

                        Utils.showToast(LoginActivity.baseContext, "Free Quantity can not be empty.");
                    }

                }
                else
                {
                    Utils.showToast(LoginActivity.baseContext, "Quantity can not be empty.");

                }


               *//* if (selectedSkuQuantityMap.isEmpty())
                {

                    Utils.showToast(LoginActivity.baseContext, "Quantity can not be empty.");

                }
                else
                {

                    insertSkuOrIncreaseQuantity(context,salesOrderID, skuID, skuName, skuPrice);
                }*//*
            }
        });


        dialogBuilder.setNegativeButton("cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });*/

        //dialogBuilder.show();

        mAlertDialog.show();
    }

    void insertSkuOrIncreaseQuantity(AlertDialog mAlertDialog, final Context context, String salesOrderID, String skuID, String skuName, String skuPrice)
    {

        String finalqty = NONE;
        String finalFreeQty = NONE;
        String finalDiscount = NONE;

        finalqty = selectedSkuQuantityMap.get(context.getResources().getString(R.string.tag_sky_qty_id));
        finalFreeQty = selectedSkuQuantityMap.get(context.getResources().getString(R.string.tag_sky_freeQty_id));
        finalDiscount = selectedSkuQuantityMap.get(context.getResources().getString(R.string.tag_sky_discount_id));

      /*  for (Map.Entry<String, String> entry : selectedSkuQuantityMap.entrySet())
        {

            finalqty = entry.getValue();

        }*/


        //if Sku doesn't exists in the sales order details table, insert new row for that Sku
        if (!isSkuPresent(skuID, salesOrderID))
        {
            long rowID=0;

            int newSkuQuantity = Integer.parseInt(finalqty);
            int newSkuFreeQuantity = Integer.parseInt(finalFreeQty);
            float newSkuDisc = Float.parseFloat(finalDiscount);

            float skuPriceBeforeDiscount = Float.parseFloat(skuPrice) * newSkuQuantity;

            if (newSkuDisc < skuPriceBeforeDiscount)
            {

                rowID= DbUtils.insertIntoSalesOrderDetailsTable(salesOrderID, skuID, skuName, skuPrice, finalqty, finalFreeQty, finalDiscount);

                if (rowID != -1L)
                {
                    long salesOrderDetailID = rowID;

                    insertIntoSalesOrderSkuAttributes(salesOrderDetailID);

                    mAlertDialog.dismiss();

                    Utils.showPopUp(LoginActivity.baseContext, "SKU added to cart for 1st time");
                }


            }
            else
            {
                Utils.showPopUp(LoginActivity.baseContext, "SKU Discount should be less than SKU Total Price.");

            }

        }
        else
        {
            List<Long> orderDetailIDsList = getOrderDetailIDsList(salesOrderID, skuID);

            long orderDetailIdWithSameAttributes = getOrderDetailIdWithSameAttributes(orderDetailIDsList);

            if (orderDetailIdWithSameAttributes > 0)
            {
                ArrayList<QuantityDiscountModel> quantityDiscountModelArrayList = new ArrayList<>();

                quantityDiscountModelArrayList = getSkuQuantityDiscount(orderDetailIdWithSameAttributes);

                int currentSkuQuantity = quantityDiscountModelArrayList.get(0).getSkuQty();
                int currentSkuFreeQuantity = quantityDiscountModelArrayList.get(0).getSkuFreeQty();
                float currentSkuDiscount = quantityDiscountModelArrayList.get(0).getSkuDiscount();

                int newSkuQuantity = currentSkuQuantity + Integer.parseInt(finalqty);
                int newSkuFreeQuantity = currentSkuFreeQuantity + Integer.parseInt(finalFreeQty);
                float newSkuDisc = currentSkuDiscount + Float.parseFloat(finalDiscount);

                float skuPriceBeforeDiscount = quantityDiscountModelArrayList.get(0).getSkuUnitPrice() * newSkuQuantity;

                if (newSkuDisc < skuPriceBeforeDiscount)
                {

                    DbUtils.increaseSkuQuantityFreeQuantityDiscount(orderDetailIdWithSameAttributes, quantityDiscountModelArrayList.get(0).getSkuUnitPrice(), newSkuQuantity, newSkuFreeQuantity, newSkuDisc);
                    mAlertDialog.dismiss();
                    Utils.showPopUp(LoginActivity.baseContext, "SKU quantity increased");
                }
                else
                {
                    Utils.showPopUp(LoginActivity.baseContext, "SKU Discount should be less than SKU Total Price.");

                }

            }
            else if (orderDetailIdWithSameAttributes == -1L)
            {
                long rowID = DbUtils.insertIntoSalesOrderDetailsTable(salesOrderID, skuID, skuName, skuPrice, finalqty, finalFreeQty, finalDiscount);

                if (rowID != -1L)
                {
                    long salesOrderDetailID = rowID;

                    insertIntoSalesOrderSkuAttributes(salesOrderDetailID);

                    mAlertDialog.dismiss();

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

    public String getModifiedkeyValueString(String key, String Value)
    {
        String name_string = key + Value;
        String replacedWith1 = "<font color='blue'>" + key + "</font>";
        String modified_string_name = name_string.replaceAll(key, replacedWith1);
        return modified_string_name;
    }

}
