package in.etaminepgg.sfa.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import in.etaminepgg.sfa.BuildConfig;
import in.etaminepgg.sfa.InputModel_For_Network.IM_Config;
import in.etaminepgg.sfa.InputModel_For_Network.IM_IsValidAuthKey;
import in.etaminepgg.sfa.InputModel_For_Network.IM_Login;
import in.etaminepgg.sfa.InputModel_For_Network.IM_RetailerInfo;
import in.etaminepgg.sfa.Models.AuthUserDetails;
import in.etaminepgg.sfa.Models.AuthUser_Model;
import in.etaminepgg.sfa.Models.BasicConfigModel;
import in.etaminepgg.sfa.Models.Location_Model;
import in.etaminepgg.sfa.Models.RetailerInfo_Model;
import in.etaminepgg.sfa.Models.RetailerList_Model;
import in.etaminepgg.sfa.Models.ValidAuthModel;
import in.etaminepgg.sfa.Network.API_Call_Retrofit;
import in.etaminepgg.sfa.Network.Apimethods;
import in.etaminepgg.sfa.R;
import in.etaminepgg.sfa.Utilities.ConstantsA;
import in.etaminepgg.sfa.Utilities.DbUtils;
import in.etaminepgg.sfa.Utilities.MyDb;
import in.etaminepgg.sfa.Utilities.MySharedPrefrencesData;
import in.etaminepgg.sfa.Utilities.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static in.etaminepgg.sfa.Utilities.Constants.JINGLE_FILE_NAME;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_CONFIG;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_EMPLOYEE;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_GLOBAL_ATTRIBUTES;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_LOCATION_HIERARCHY;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_RETAILER;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_RETAILER_VISIT;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SALES_ORDER;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SALES_ORDER_DETAILS;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SALES_ORDER_SKU_ATTRIBUTES;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SCHEME;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SKU;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SKU_ATTRIBUTE_MAPPING;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SKU_CATEGORY;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SKU_NO_ORDERREASON;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SKU_SUBCATEGORY;
import static in.etaminepgg.sfa.Utilities.Constants.appSpecificDirectoryPath;
import static in.etaminepgg.sfa.Utilities.Constants.dbFileFullPath;
import static in.etaminepgg.sfa.Utilities.ConstantsA.NOT_PRESENT;
import static in.etaminepgg.sfa.Utilities.ConstantsA.NO_INTERNET_CONNECTION;
import static in.etaminepgg.sfa.Utilities.Utils.dismissProgressDialog;
import static in.etaminepgg.sfa.Utilities.Utils.getDateTime;
import static in.etaminepgg.sfa.Utilities.Utils.getTodayDate;
import static in.etaminepgg.sfa.Utilities.Utils.loggedInUserID;
import static in.etaminepgg.sfa.Utilities.Utils.loggedInUserName;


public class LoginActivity extends AppCompatActivity
{
    public static final String uploadToURL = "http://etaminepgg" +
            ".com/sfa/fromMobile_gfjsdfkhweriusfgkjsdhsakjsdfhgsfdkjsflksfd324435.php";
    public static final String downloadFromURL = "http://etaminepgg" +
            ".com/sfa/toMobile_giuwer4598wekhhwiergfhj3t49gjhgfkjewr.php?file=";
    public static final String lastModifiedDbDateURL = "http://etaminepgg" +
            ".in/aaaaabbjeroidgkdflkfdrhfkjgrdflkjdfgfdjg/incometax/checkDataDt.php";
    public static String DbFileName = "sfaDb.ma7";
    //appSpecificDirectoryPath, dbFileFullPath
    public static Context baseContext;
    public static String KEY_USERNAME = "username";
    public static String KEY_PASSWORD = "password";
    public static boolean is_config_inserted_to_db = true;
    Button login_Button;
    EditText username_EditText, password_EditText;
    int valueFromOpenDatabase;
    SQLiteDatabase sqLiteDatabase;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPreferencesEditor;
    String usernameEntered = "";
    String passwordEntered = "";
    String imei;
    MySharedPrefrencesData mySharedPrefrencesData;
    boolean configflag = false;
    boolean authflag = false;

    int a=0;
    int b=0;


    ProgressDialog progressDialog ;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        if (requestCode == 1)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                if (ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission
                        .READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)
                {
                    TelephonyManager telephonyManager = (TelephonyManager) LoginActivity.this
                            .getSystemService(Context.TELEPHONY_SERVICE);
                    imei = telephonyManager.getDeviceId();

                    //launchDashboardIfLoggedIn();
                }
            }
            else if (grantResults.length > 0 && grantResults[0] == PackageManager
                    .PERMISSION_DENIED)
            {
                String[] permissionsArray = {"android.permission.READ_PHONE_STATE"};
                ActivityCompat.requestPermissions(LoginActivity.this, permissionsArray, 1);
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialog = new ProgressDialog(LoginActivity.this);

        baseContext = getBaseContext();
        mySharedPrefrencesData = new MySharedPrefrencesData();
        //appSpecificDirectoryPath = baseContext.getExternalFilesDir(null);
        // appSpecificDirectoryPath = Environment.getExternalStorageDirectory().toString();

        appSpecificDirectoryPath = getApplicationContext().getExternalFilesDir(null)
                .getAbsolutePath();
        dbFileFullPath = appSpecificDirectoryPath + File.separator + DbFileName;

        downloadDatabaseIfNotExists();

       // Utils.makeThreadSleepFor(5000);
        //imei = Utils.getDeviceId(LoginActivity.this);

        findViewsByIDs();
        setListenersToViews();

        try
        {
            AssetManager assetManager = getAssets();
            Utils.copyFile(assetManager, appSpecificDirectoryPath, JINGLE_FILE_NAME);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if (ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission
                .READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)
        {
            TelephonyManager telephonyManager = (TelephonyManager) LoginActivity.this
                    .getSystemService(Context.TELEPHONY_SERVICE);
            imei = telephonyManager.getDeviceId();

            sharedPreferences = getSharedPreferences(MySharedPrefrencesData.PREFS_NAME, Context.MODE_PRIVATE);

            boolean isLaunch=sharedPreferences.getBoolean("hasLoggedIn",false);

            if(isLaunch){

                launchDashboardIfLoggedIn();
            }

        }
        else
        {
            String[] permissionsArray = {"android.permission.READ_PHONE_STATE"};
            ActivityCompat.requestPermissions(LoginActivity.this, permissionsArray, 1);
        }
    }

    @Override
    protected void onDestroy()
    {
        sqLiteDatabase.releaseReference();
        super.onDestroy();
    }

    private void findViewsByIDs()
    {
        login_Button = (Button) findViewById(R.id.login_Button);
        username_EditText = (EditText) findViewById(R.id.username_EditText);
        password_EditText = (EditText) findViewById(R.id.password_EditText);

    }

    private void setListenersToViews()
    {
        login_Button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                usernameEntered = username_EditText.getText().toString().trim();
                passwordEntered = password_EditText.getText().toString().trim();

                b=0;

                callWebApi();

         /*       if (isValidUser(usernameEntered, passwordEntered)) {
                    sharedPreferences = getSharedPreferences(MY_PREF, Context.MODE_PRIVATE);
                    sharedPreferencesEditor = sharedPreferences.edit();
                    sharedPreferencesEditor.putString(KEY_USERNAME, usernameEntered);
                    sharedPreferencesEditor.putString(KEY_PASSWORD, passwordEntered);
                    sharedPreferencesEditor.commit();

                    loggedInUserName = usernameEntered;
                    loggedInUserID = getEmployeeIdFor(loggedInUserName);

                    Log.e("usernameEntered", usernameEntered);
                    Log.e("passwordEntered", passwordEntered);

                    Log.e("loggedInUserName", loggedInUserName);
                    Log.e("loggedInUserID", loggedInUserID);

                   *//* Utils.launchActivity(getBaseContext(), DashboardActivity.class);
                    finish();*//*

                    //networkcall_for_authtoken(getDeviceId(LoginActivity.this),usernameEntered,
                    // passwordEntered);

                    callWebApi();
                } else {
                    Utils.showPopUp(getBaseContext(), "Please Enter Valid Username, Password");
                }*/
            }
        });
    }

    private void callWebApi()
    {
        if (Utils.isNetworkConnected(getBaseContext()))
        {
            if (imei.equalsIgnoreCase(null) || imei.trim().length() <= 0)
            {
                Utils.showErrorDialog(LoginActivity.this, "You can not login as you denied " +
                        "permission.");
            }
            else
            {
                networkcall_for_authtoken();
            }
        }
        else
        {
            Utils.showErrorDialog(LoginActivity.this, "Please check your internet connection.");
        }

    }

    private void launchDashboardIfLoggedIn()
    {
        downloadDatabaseIfNotExists();
       // Utils.makeThreadSleepFor(5000);

       /* sharedPreferences = getSharedPreferences(MY_PREF, Context.MODE_PRIVATE);
        String spDefaultValue = "spDefaultValue"; //sp = shared preferences

        String usernameInSharedPreferences = sharedPreferences.getString(KEY_USERNAME,
                spDefaultValue);
        String passwordInSharedPreferences = sharedPreferences.getString(KEY_PASSWORD,
                spDefaultValue);*/

        String usernameInSharedPreferences = new MySharedPrefrencesData().getUsername(LoginActivity.this);
        String passwordInSharedPreferences = new MySharedPrefrencesData().getUser_pwd(LoginActivity.this);

        loggedInUserName = usernameInSharedPreferences;
        //loggedInUserID = getEmployeeIdFor(loggedInUserName);
        loggedInUserID = new MySharedPrefrencesData().getUser_Id(LoginActivity.this);

        Log.e("usernameInSharedPref", usernameInSharedPreferences);
        Log.e("passwordInSharedPref", passwordInSharedPreferences);

        if (autoLogout())
        {
            callLogoutApi(mySharedPrefrencesData.getEmployee_AuthKey(LoginActivity.this));
        }
        else
        {

            if (isValidUser(usernameInSharedPreferences, passwordInSharedPreferences))
            {
                loggedInUserID = new MySharedPrefrencesData().getUser_Id(LoginActivity.this);
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");


                SharedPreferences settings = getSharedPreferences(MySharedPrefrencesData.PREFS_NAME, 0);
                //Get "hasLoggedIn" value. If the value doesn't exist yet false is returned
                boolean hasLoggedIn = settings.getBoolean("hasLoggedIn", false);
                if (hasLoggedIn)
                {

                    Utils.launchActivity(LoginActivity.this, DashboardActivity.class);
                    finish();
                }
                //System.exit(0);

            }
        }

    }

    private void callLogoutApi(String employee_authKey)
    {


        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        Utils.startProgressDialog(LoginActivity.this, progressDialog);

        final Apimethods methods = API_Call_Retrofit.getretrofit(this).create(Apimethods.class);

        IM_IsValidAuthKey IM_isValidAuthKey = new IM_IsValidAuthKey(mySharedPrefrencesData.getEmployee_AuthKey(LoginActivity.this),"1");


        Call<ValidAuthModel> call = methods.setLogout(IM_isValidAuthKey);


        Log.i("logout_ip", new Gson().toJson(IM_isValidAuthKey));

        Log.d("url", "url=" + call.request().url().toString());

        call.enqueue(new Callback<ValidAuthModel>()
        {
            @Override
            public void onResponse(Call<ValidAuthModel> call, Response<ValidAuthModel> response)
            {
                if (response.isSuccessful())
                {
                    Utils.dismissProgressDialog(progressDialog);
                    Utils.showToast(LoginActivity.this, "Logged out successfully.");
                }

                Utils.dismissProgressDialog(progressDialog);

            }

            @Override
            public void onFailure(Call<ValidAuthModel> call, Throwable t)
            {
                Utils.dismissProgressDialog(progressDialog);
                Utils.showToast(LoginActivity.this, ConstantsA.NO_INTERNET_CONNECTION);
            }
        });
    }

    private boolean autoLogout()
    {

        valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        boolean isAutoLogout = false;
        String SQL_SELECT_LOGIN_TIME = "SELECT emp_login_time FROM " + TBL_EMPLOYEE;

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_LOGIN_TIME, null);

        while (cursor.moveToNext())
        {

            String emp_logintime = cursor.getString(cursor.getColumnIndexOrThrow("emp_login_time"));

            String currentdateandtime = getDateTime();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            try
            {
                if (sdf.parse(currentdateandtime).after(sdf.parse(emp_logintime)))
                {
                    isAutoLogout = true;
                }
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }

        }

        cursor.close();
        sqLiteDatabase.close();
        return isAutoLogout;

    }

    private boolean isValidUser(String userName, String password)
    {
        valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String SQL_SELECT_SALES_PERSON = "SELECT emp_id FROM " + TBL_EMPLOYEE + " WHERE " +
                "emp_username " + "= ? AND " + "emp_password " + "= ?";
        String[] selectionArgs = {userName, password};
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_SALES_PERSON, selectionArgs);
        boolean isUserFound = cursor.moveToFirst();
        cursor.close();
        sqLiteDatabase.close();
        return isUserFound;
    }

    private String getEmployeeIdFor(String salesPersonUsername)
    {
        String salesPersonEmpID = "getEmployeeIdFor()";
        String SQL_SELECT_SALES_PERSON_ID = "SELECT " + "emp_id" + " FROM " + TBL_EMPLOYEE + " " +
                "WHERE " + "emp_username " + "= ?";
        String[] selectionArgs = {salesPersonUsername};
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_SALES_PERSON_ID, selectionArgs);
        if (cursor.moveToFirst())
        {
            salesPersonEmpID = cursor.getString(cursor.getColumnIndexOrThrow("emp_id"));
        }
        cursor.close();
        return salesPersonEmpID;
    }

    private void networkcall_for_authtoken()
    {

        final Apimethods methods = API_Call_Retrofit.getretrofit(this).create(Apimethods.class);


        Utils.startProgressDialog(LoginActivity.this, progressDialog);

        IM_Login IMLogin = new IM_Login(imei, usernameEntered, passwordEntered,"1");

        Log.i("authuser_input", new Gson().toJson(IMLogin));

        Call<AuthUser_Model> call = methods.getUserAuthKey(IMLogin);
        //Call<AuthUser_Model> call = methods.getUserAuthKey(imei,"1",passwordEntered,usernameEntered);
        Log.d("url", "url=" + call.request().url().toString());

        call.enqueue(new Callback<AuthUser_Model>()
        {
            @Override
            public void onResponse(Call<AuthUser_Model> call, Response<AuthUser_Model> response)
            {
                int statusCode = response.code();
                Log.d("Response", "" + statusCode);
                Log.d("respones", "" + response);
                if (response.isSuccessful())
                {
                    AuthUser_Model authUser_model = response.body();


                    Log.i("authuser_output", new Gson().toJson(authUser_model));

                    if (authUser_model.getApiStatus() == 1)
                    {
//                        SharedPreferences settings1 = getSharedPreferences(MySharedPrefrencesData.PREFS_NAME, 0);
//                        //Get "hasLoggedIn" value. If the value doesn't exist yet false is returned
//                        boolean hasLoggedIn = settings1.getBoolean("hasLoggedIn", false);


                        valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
                        sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);


                        DbUtils.clear_table(TBL_EMPLOYEE);
                        DbUtils.clear_table(TBL_LOCATION_HIERARCHY);
                        DbUtils.clear_table(TBL_RETAILER);


                        loggedInUserName = usernameEntered;
                        // loggedInUserID = getEmployeeIdFor(loggedInUserName);
                        loggedInUserID = authUser_model.getUserId();

                        Log.e("usernameEntered", usernameEntered);
                        Log.e("passwordEntered", passwordEntered);

                        Log.e("loggedInUserName", loggedInUserName);
                        Log.e("loggedInUserID", loggedInUserID);


                        ContentValues suryaValues = new ContentValues();
                        suryaValues.put("emp_id", authUser_model.getUserId());
                        suryaValues.put("imei", Utils.getDeviceId(LoginActivity.this));
                        suryaValues.put("reports_to", "Manager");
                        suryaValues.put("emp_name", usernameEntered);
                        suryaValues.put("emp_username", usernameEntered);
                        suryaValues.put("emp_password", passwordEntered);
                        suryaValues.put("created_date", getTodayDate());
                        suryaValues.put("modified_date", getTodayDate());
                        suryaValues.put("emp_login_time", getDateTime());
                        suryaValues.put("upload_status", 1);
                        sqLiteDatabase.insert(TBL_EMPLOYEE, null, suryaValues);


                        mySharedPrefrencesData.setEmployee_AuthKey(LoginActivity.this,
                                authUser_model
                                        .getAuthToken());
                        mySharedPrefrencesData.setAuthTokenExpiryDate(LoginActivity.this,
                                authUser_model.getAuthTokenExpiryDate());

                        mySharedPrefrencesData.setUser_Id(LoginActivity.this,
                                authUser_model.getUserId());


                        mySharedPrefrencesData.setfirsttimeflagfornew(LoginActivity.this, true);
                        mySharedPrefrencesData.setfirsttimeflagforAll(LoginActivity.this, true);
                        mySharedPrefrencesData.setApiCallForCategoryListOnce(LoginActivity.this, true);


                        SharedPreferences settings = getSharedPreferences(MySharedPrefrencesData
                                .PREFS_NAME, 0); // 0 - for private mode
                        SharedPreferences.Editor editor = settings.edit();

                        //Set "hasLoggedIn" to true
                        editor.putBoolean("hasLoggedIn", true);
                        // Commit the edits!
                        editor.commit();

                        authflag = true;
                        networkcall_for_retailerlist(authUser_model.getAuthToken());
                    }


                }
                else
                {
                    dismissProgressDialog(progressDialog);
                    Utils.showErrorDialog(LoginActivity.this, "User not found, Please login and " + "try again with valid username");
                }
            }

            private void networkcall_for_retailerlist(final String authToken)
            {

                IM_IsValidAuthKey im_isValidAuthKey = new IM_IsValidAuthKey(authToken,"1");

                Call<RetailerList_Model> call = methods.getRetailerList(im_isValidAuthKey);

                Log.i("getRetailerlist_ip", new Gson().toJson(im_isValidAuthKey));

                Log.d("url", "url=" + call.request().url().toString());

                call.enqueue(new Callback<RetailerList_Model>()
                {
                    @Override
                    public void onResponse(Call<RetailerList_Model> call,
                                           Response<RetailerList_Model> response)
                    {

                        int statusCode = response.code();
                        Log.d("Response", "" + statusCode);
                        Log.d("respones", "" + response);


                        if (response.isSuccessful())
                        {

                            RetailerList_Model retailerList_model = response.body();

                            Log.i("getRetailerlist_op", new Gson().toJson(retailerList_model));


                            if (retailerList_model.getApiStatus() == 1)
                            {

                                a=retailerList_model.getRetailerData().size();

                                for (RetailerList_Model.RetailerDatum retailerDatum :
                                        retailerList_model.getRetailerData())
                                {

                                    String retailerid = retailerDatum.getCustomerCode();

                                    networkcall_for_retailerinfo(authToken, retailerid);

                                }
                            }

                        }
                        else
                        {

                            Utils.showToast(LoginActivity.this, "No retailer found ");
                            Utils.dismissProgressDialog(progressDialog);
                            network_call_for_basicconfig();
                        }
                    }

                    private void networkcall_for_retailerinfo(String authToken, String retailerid)
                    {

                        IM_RetailerInfo im_retailerInfo = new IM_RetailerInfo(authToken,
                                retailerid);

                        Call<RetailerInfo_Model> call = methods.getRetailerInfo(im_retailerInfo);
                        Log.d("url", "url=" + call.request().url().toString());

                        call.enqueue(new Callback<RetailerInfo_Model>()
                        {
                            @Override
                            public void onResponse(Call<RetailerInfo_Model> call,
                                                   Response<RetailerInfo_Model> response)
                            {

                                int statusCode = response.code();
                                Log.d("Response", "" + statusCode);
                                Log.d("respones", "" + response);

                                if (response.isSuccessful())
                                {

                                    RetailerInfo_Model retailerInfo_model = response.body();
                                    if (retailerInfo_model.getApiStatus() == 1)
                                    {

                                        RetailerInfo_Model.RetailerData retailerData = retailerInfo_model.getRetailerData();

                                        if (!DbUtils.isRetailerPresentInDb(retailerData.getCustomerCode()))
                                        {
                                            ContentValues retailerValues = new ContentValues();
                                            retailerValues.put("retailer_id", retailerData
                                                    .getCustomerCode());
                                            retailerValues.put("emp_id", getEmployeeIdFor
                                                    (usernameEntered));
                                            retailerValues.put("retailer_name", retailerData
                                                    .getCustomerCompanyname());
                                            retailerValues.put("shop_name", retailerData
                                                    .getCustomerContactName());
                                            retailerValues.put("shop_address", retailerData
                                                    .getCustomerAddress1());
                                            retailerValues.put("pincode", retailerData
                                                    .getCustomerPincode());
                                            retailerValues.put("mobile_no", retailerData
                                                    .getContactCell());
                                            retailerValues.put("email", retailerData.getContactEmail());
                                            retailerValues.put("area_id", retailerData.getLocationId());

                                            String latlong = retailerData.getCustomerGeopos();

                                            if (latlong != null && latlong.length() > 0)
                                            {

                                                String latlong_arra[] = latlong.split(",");
                                                retailerValues.put("latitude", latlong_arra[0]);
                                                retailerValues.put("longitude", latlong_arra[1]);
                                            }
                                            else
                                            {
                                                retailerValues.put("latitude", NOT_PRESENT);
                                                retailerValues.put("longitude", NOT_PRESENT);
                                            }

                                            if(retailerData.getCustomerPicture() != null && retailerData.getCustomerPicture().length() > 1){


                                                retailerValues.put("img_source", retailerData.getCustomerPicture());
                                            }else {

                                                retailerValues.put("img_source", NOT_PRESENT);
                                            }

                                            retailerValues.put("created_date", retailerData
                                                    .getCreatedDate());
                                            retailerValues.put("modified_date", retailerData
                                                    .getModifiedDate());
                                            retailerValues.put("landmark", retailerData
                                                    .getCustomerCompanyname());
                                            retailerValues.put("area", retailerData
                                                    .getCustomerAddress1());
                                            retailerValues.put("taluk", retailerData
                                                    .getCustomerAddress1());
                                            retailerValues.put("district", retailerData
                                                    .getCustomerCity());
                                            retailerValues.put("state", retailerData.getCustomerState
                                                    ());
                                            retailerValues.put("upload_status", 1);

                                            sqLiteDatabase.insert(TBL_RETAILER, null, retailerValues);
                                            // DbUtils.clear_table(TBL_CONFIG);

                                            b++;

                                            if(a==b){


                                                if (is_config_inserted_to_db)
                                                {
                                                    //dismissProgressDialog(progressDialog);
                                                    DbUtils.clear_table(TBL_CONFIG);
                                                    network_call_for_basicconfig();
                                                    is_config_inserted_to_db = false;
                                                }

                                            }

                                        }
                                        else
                                        {
                                            /*ContentValues contentValues=new ContentValues();
                                            contentValues.put("emp_id", getEmployeeIdFor(usernameEntered));
                                            sqLiteDatabase.update(TBL_RETAILER,contentValues,"retailer_id = ?",new String[]{retailerData.getCustomerCode()});*/
                                            if (is_config_inserted_to_db)
                                            {
                                                dismissProgressDialog(progressDialog);
                                                DbUtils.clear_table(TBL_CONFIG);
                                                network_call_for_basicconfig();
                                                is_config_inserted_to_db = false;
                                            }
                                        }
                                    }

                                }
                                else
                                {
                                    dismissProgressDialog(progressDialog);
                                    Utils.showToast(LoginActivity.this, "Unsuccessful api call " + "for retailerinfo ");
                                }
                            }

                            @Override
                            public void onFailure(Call<RetailerInfo_Model> call, Throwable t)
                            {
                                dismissProgressDialog(progressDialog);
                                Utils.showToast(LoginActivity.this, NO_INTERNET_CONNECTION);

                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<RetailerList_Model> call, Throwable t)
                    {
                        dismissProgressDialog(progressDialog);
                        Utils.showToast(LoginActivity.this, NO_INTERNET_CONNECTION);
                    }
                });
            }

            @Override
            public void onFailure(Call<AuthUser_Model> call, Throwable t)
            {
                dismissProgressDialog(progressDialog);
                Utils.showToast(LoginActivity.this, ConstantsA.NO_INTERNET_CONNECTION);

            }
        });

    }

    private void network_call_for_basicconfig()
    {
         String mobile_app_version= BuildConfig.VERSION_NAME;
        //String mobile_app_version = "0.0.1";

        Apimethods methods = API_Call_Retrofit.getretrofit(this).create(Apimethods.class);

        IM_Config IM_config = new IM_Config(imei, mobile_app_version);

        Call<BasicConfigModel> call = methods.sendBasicConfig(IM_config);

        Log.i("basicconfig_input", new Gson().toJson(IM_config));

        Log.d("url", "url=" + call.request().url().toString());


        call.enqueue(new Callback<BasicConfigModel>()
        {
            @Override
            public void onResponse(Call<BasicConfigModel> call, Response<BasicConfigModel> response)
            {
                int statusCode = response.code();
                Log.d("Response", "" + statusCode);
                Log.d("respones", "" + response);
                if (response.isSuccessful())
                {

                    BasicConfigModel basicConfigModel = response.body();

                    Log.i("basicconfig_output", new Gson().toJson(basicConfigModel));

                    if (basicConfigModel.getApi_status() == 1)
                    {


                        valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
                        sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

                        for (int i = 0; i < basicConfigModel.getApp_data().size(); i++)
                        {

                            ContentValues configValues = new ContentValues();
                            configValues.put("config_for", basicConfigModel.getApp_data().get(i)
                                    .getParameter_name());
                            configValues.put("config_value", basicConfigModel.getApp_data().get
                                    (i).getParameter_value());
                            configValues.put("upload_status", 0);
                            sqLiteDatabase.insert(TBL_CONFIG, null, configValues);

                        }

                        network_call_for_getLocationsinfo(new MySharedPrefrencesData().getEmployee_AuthKey(LoginActivity.this));

                    }


                }
                else
                {
                    Utils.showToast(baseContext, "Unsuccessful api call for config ");
                }

            }

            private void network_call_for_getLocationsinfo(final String employee_authKey)
            {
                Apimethods methods = API_Call_Retrofit.getretrofit(LoginActivity.this).create(Apimethods.class);

                IM_IsValidAuthKey authkey = new IM_IsValidAuthKey(employee_authKey,"1");

                Call<Location_Model> call = methods.getLocationinfo(authkey);

                Log.i("loc_ip", new Gson().toJson(authkey));

                Log.d("url", "url=" + call.request().url().toString());

                call.enqueue(new Callback<Location_Model>()
                {
                    @Override
                    public void onResponse(Call<Location_Model> call, Response<Location_Model> response)
                    {

                        if (response.isSuccessful())
                        {

                            Location_Model location_model = response.body();

                            Log.i("loc_op", new Gson().toJson(location_model));

                            if (location_model.getApiStatus() == 1)
                            {

                                for (int i = 0; i < location_model.getLocatoinsInfo().size(); i++)
                                {


                                    Location_Model.LocatoinsInfo locatoinsInfo = location_model.getLocatoinsInfo().get(i);

                                    ContentValues contentValues = new ContentValues();
                                    contentValues.put("loc_id", locatoinsInfo.getLocHierId());
                                    contentValues.put("loc_name", locatoinsInfo.getName());
                                    contentValues.put("hier_level", locatoinsInfo.getHierLevel());
                                    contentValues.put("full_hier_level", locatoinsInfo.getFullHier());
                                    contentValues.put("parent_loc_id", locatoinsInfo.getParentId());
                                    contentValues.put("upload_status", 1);
                                    sqLiteDatabase.insert(TBL_LOCATION_HIERARCHY, null, contentValues);


                                }

                                networkcall_for_getUserDetails(employee_authKey);


                            }
                        }
                    }

                    private void networkcall_for_getUserDetails(String employee_authKey)
                    {
                        Apimethods methods = API_Call_Retrofit.getretrofit(LoginActivity.this).create(Apimethods.class);

                        IM_IsValidAuthKey authkey = new IM_IsValidAuthKey(employee_authKey,"1");

                        Call<AuthUserDetails> call = methods.getUserDetails(authkey);

                        Log.i("getuserdetails_ip", new Gson().toJson(authkey));

                        Log.d("url", "url=" + call.request().url().toString());

                        call.enqueue(new Callback<AuthUserDetails>()
                        {
                            @Override
                            public void onResponse(Call<AuthUserDetails> call, Response<AuthUserDetails> response)
                            {
                                if (response.isSuccessful())
                                {

                                    AuthUserDetails authUserDetails = response.body();

                                    Log.i("getuserdetails_op", new Gson().toJson(authUserDetails));

                                    mySharedPrefrencesData.set_User_mobile(LoginActivity.this, authUserDetails.getData().getMobile());
                                    mySharedPrefrencesData.setUsername(LoginActivity.this, authUserDetails.getData().getUsername());
                                    mySharedPrefrencesData.setUser_pwd(LoginActivity.this, passwordEntered);
                                    mySharedPrefrencesData.setEmail(LoginActivity.this, authUserDetails.getData().getEmail());
                                    mySharedPrefrencesData.setUser_LocationId(LoginActivity.this, authUserDetails.getData().getLocationId());
                                    mySharedPrefrencesData.set_User_CompanyId(LoginActivity.this, authUserDetails.getData().getCompanyId());
                                    mySharedPrefrencesData.setRetailerVisit_LocationId(LoginActivity.this, authUserDetails.getData().getNo_mandatory_visit());

                                    configflag = true;


                                    launchDashBoardActivity(configflag, authflag);
                                }

                            }

                            @Override
                            public void onFailure(Call<AuthUserDetails> call, Throwable t)
                            {
                                dismissProgressDialog(progressDialog);
                            }
                        });

                    }

                    @Override
                    public void onFailure(Call<Location_Model> call, Throwable t)
                    {
                        dismissProgressDialog(progressDialog);
                    }
                });

            }

            @Override
            public void onFailure(Call<BasicConfigModel> call, Throwable t)
            {

                Utils.showToast(baseContext, ConstantsA.NO_INTERNET_CONNECTION);
                dismissProgressDialog(progressDialog);
            }
        });


    }

    private void launchDashBoardActivity(boolean configflag, boolean authflag)
    {
        dismissProgressDialog(progressDialog);

        if (configflag == true && authflag == true)
        {
            Utils.launchActivity(LoginActivity.this, DashboardActivity.class);
            finish();
        }

    }

    private void downloadDatabaseIfNotExists()
    {
        File f = new File(appSpecificDirectoryPath, DbFileName);

        if (!f.exists())
        {
            Log.d("File Status", "Not Exist");
            new setUpDatabaseAsyncTask(LoginActivity.this).execute();
        }
        else
        {
            valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
            sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);


        }
    }

    private void createTables()
    {
        valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        sqLiteDatabase.execSQL("CREATE TABLE " + TBL_CONFIG + " ("
                + "config_id INTEGER PRIMARY KEY, "
                + "config_for varchar(100), "
                + "config_value varchar(50), "
              /*  + "created_date varchar(50), "
                + "modified_date varchar(50), "
                + "created_by varchar(50), "
                + "modified_by varchar(50), "*/
                + "upload_status varchar(1) );");

        sqLiteDatabase.execSQL("CREATE TABLE " + TBL_EMPLOYEE + " ("
                + "emp_id varchar(50), "
                + "imei varchar(100), "
                + "reports_to varchar(50), "
                + "emp_name varchar(50), "
                + "emp_username varchar(50), "
                + "emp_password varchar(50), "
                + "created_date varchar(50), "
                + "modified_date varchar(50), "
                + "created_by varchar(50), "
                + "modified_by varchar(50), "
                + "emp_login_time varchar(50), "
                + "emp_logout_time varchar(50), "
                + "upload_status varchar(1) );");

        sqLiteDatabase.execSQL("CREATE TABLE " + TBL_LOCATION_HIERARCHY + " ("
                + "loc_id varchar(50), "
                + "loc_name varchar(50), "
                + "hier_level varchar(50), "
                + "full_hier_level varchar(50), "
                + "parent_loc_id varchar(50), "
                + "upload_status varchar(1) );");

        sqLiteDatabase.execSQL("CREATE TABLE " + TBL_RETAILER + " ("
                + "retailer_id varchar(50), "
                + "emp_id varchar(50), " //emp_id is sales person's id
                + "retailer_name varchar(50), "
                + "shop_name varchar(50), "
                + "shop_address varchar(50), "
                + "pincode varchar(50), "
                + "mobile_no varchar(50), "
                + "email varchar(50), "
                + "area_id varchar(50), "
                + "latitude varchar(50), "
                + "longitude varchar(50), "
                + "img_source varchar(50), "
                + "created_date varchar(50), "
                + "modified_date varchar(50), "
                + "created_by varchar(50), "
                + "modified_by varchar(50), "
                + "landmark varchar(50), "
                + "area varchar(50), "
                + "taluk varchar(50), "
                + "district varchar(50), "
                + "state varchar(50), "
                + "upload_status varchar(1) );");

        sqLiteDatabase.execSQL("CREATE TABLE " + TBL_SKU + " ("
                + "sku_id varchar(50) PRIMARY KEY, "
                + "sku_name varchar(50), "
                + "sku_price varchar(50), "
                + "description varchar(50), "
                + "sku_category varchar(50), "
                + "sku_sub_category varchar(50), "
                + "sku_category_description varchar(50), "
                + "sku_sub_category_description varchar(50), "
                + "new_sku varchar(1), "
                + "promotional_sku varchar(1), "
                + "sku_photo_source varchar(50), "
                + "sku_video_source varchar(100), "
                + "sku_catalogue_source varchar(100), "
                + "created_date varchar(50), "
                + "modified_date varchar(50), "
                + "created_by varchar(50), "
                + "modified_by varchar(50), "
                + "upload_status varchar(1) );");



        sqLiteDatabase.execSQL("CREATE TABLE " + TBL_RETAILER_VISIT + " ("
                + "visit_id varchar(50), "
                + "emp_id varchar(50), "
                + "retailer_id varchar(50), "
                + "visit_date varchar(50), "
                + "has_order varchar(1), "
                + "feedback varchar(50), "
                + "latitude varchar(10), "
                + "longitude varchar(10), "
                + "upload_status varchar(1) );");

        sqLiteDatabase.execSQL("CREATE TABLE " + TBL_GLOBAL_ATTRIBUTES + " ("
                + "attribute_id INTEGER, " /*PRIMARY KEY*/
                + "attribute_type varchar(50), "
                + "attribute_name varchar(50), "
                + "attribute_value varchar(100), "
                + "created_date varchar(50), "
                + "modified_date varchar(50), "
                + "created_by varchar(50), "
                + "modified_by varchar(50), "
                + "upload_status varchar(1) );");

        sqLiteDatabase.execSQL("CREATE TABLE " + TBL_SKU_ATTRIBUTE_MAPPING + " ("
                + "map_id INTEGER PRIMARY KEY, "
                + "sku_id varchar(50), "
                + "attribute_id INTEGER, "
                + "created_date varchar(50), "
                + "modified_date varchar(50), "
                + "created_by varchar(50), "
                + "modified_by varchar(50), "
                + "upload_status varchar(1) );");

        sqLiteDatabase.execSQL("CREATE TABLE " + TBL_SALES_ORDER + " ("
                + "order_id varchar(50), "
                + "visit_id varchar(50), "
                + "retailer_id varchar(50), "
                + "emp_id varchar(50), "
                + "order_date varchar(50), "
                + "total_order_value varchar(50), "
                + "total_discount varchar(50), "
                + "is_regular varchar(1), "
                + "created_date varchar(50), "
                + "modified_date varchar(50), "
                + "is_active varchar(1), "
                + "is_placed varchar(1), "
                + "is_cancelled varchar(1), "
                + "upload_status varchar(1) );");

        sqLiteDatabase.execSQL("CREATE TABLE " + TBL_SALES_ORDER_DETAILS + " ("
                + "order_detail_id  INTEGER PRIMARY KEY, "
                + "server_order_detail_id  INTEGER , "
                + "order_id varchar(50), "
                + "sku_id varchar(50), "
                + "sku_name varchar(50), "
                + "sku_price varchar(50), "
                + "sku_qty varchar(50), "
                + "sku_free_qty varchar(50), "
                + "sku_discount varchar(50), "
                + "sku_price_before_discount varchar(50), "
                + "sku_final_price varchar(50), "
                /*+ "scheme_id varchar(50), "*/
                + "upload_status varchar(1) );");

        sqLiteDatabase.execSQL("CREATE TABLE " + TBL_SALES_ORDER_SKU_ATTRIBUTES + " ("
                + "sosa_id INTEGER PRIMARY KEY, " //sosa_id - sales_order_sku_attribute_id
                + "order_detail_id INTEGER, "
                + "server_order_detail_id  INTEGER , "
                + "attribute_id INTEGER, "
                + "attribute_name varchar(50), "
                + "attribute_value varchar(100), "
                + "created_date varchar(50), "
                + "modified_date varchar(50), "
                + "created_by varchar(50), "
                + "modified_by varchar(50), "
                + "upload_status varchar(1) );");



        sqLiteDatabase.execSQL("CREATE TABLE " + TBL_SCHEME + " ("
                + "scheme_id varchar(50), "
                + "scheme_name varchar(50), "
                + "scheme_description varchar(50), "
                + "created_date varchar(50), "
                + "start_date varchar(50), "
                + "end_date varchar(50), "
               /* + "modified_date varchar(50), "
                + "created_by varchar(50), "
                + "modified_by varchar(50), "*/
                + "is_viewed varchar(1), "
                + "upload_status varchar(1) );");



/*
        sqLiteDatabase.execSQL("CREATE TABLE " + TBL_SKU_CATEGORY + " ("
                + "attribute_id INTEGER, " *//*PRIMARY KEY*//*
                + "attribute_type varchar(50), "
                + "attribute_name varchar(50), "
                + "attribute_value varchar(100), "
                + "created_date varchar(50), "
                + "modified_date varchar(50), "
                + "created_by varchar(50), "
                + "modified_by varchar(50), "
                + "upload_status varchar(1) );");
        */


        sqLiteDatabase.execSQL("CREATE TABLE " + TBL_SKU_CATEGORY + " ("
                + "category_id INTEGER PRIMARY KEY, " //sosa_id - sales_order_sku_attribute_id
                + "category_name varchar(50), "
                + "category_description TEXT, "
                + "created_date varchar(50), "
                + "modified_date varchar(50), "
                + "created_by varchar(50), "
                + "modified_by varchar(50), "
                + "is_active varchar(1), "
                + "company_id varchar(1), "
                + "upload_status varchar(1) );");


        sqLiteDatabase.execSQL("CREATE TABLE " + TBL_SKU_SUBCATEGORY + " ("
                + "sub_category_id INTEGER PRIMARY KEY, " //sosa_id - sales_order_sku_attribute_id
                + "category_id INTEGER, "
                + "sub_category_name varchar(50), "
                + "sub_category_description TEXT, "
                + "created_date varchar(50), "
                + "modified_date varchar(50), "
                + "created_by varchar(50), "
                + "modified_by varchar(50), "
                + "is_active varchar(1), "
                + "company_id varchar(1), "
                + "upload_status varchar(1) );");


        sqLiteDatabase.execSQL("CREATE TABLE " + TBL_SKU_NO_ORDERREASON + " ("
                + "reason_id INTEGER PRIMARY KEY, "
                + "reasondesc TEXT, "
                + "created_date varchar(50), "
                + "modified_date varchar(50), "
                + "created_by varchar(50), "
                + "modified_by varchar(50), "
                + "upload_status varchar(1) );");
    }

    private void insertDummyData()
    {
        //insertDummyLocationData();


       /* ContentValues suryaValues = new ContentValues();
        suryaValues.put("emp_id", "1");
        suryaValues.put("imei", Utils.getDeviceId(LoginActivity.this));
        suryaValues.put("reports_to", "abhinav");
        suryaValues.put("emp_name", "Surya");
        suryaValues.put("emp_username", "surya");
        suryaValues.put("emp_password", "surya");
        suryaValues.put("created_date", getTodayDate());
        suryaValues.put("modified_date", getTodayDate());
        suryaValues.put("upload_status", 0);
        sqLiteDatabase.insert(TBL_EMPLOYEE, null, suryaValues);


        ContentValues managerValues = new ContentValues();
        managerValues.put("emp_id", "emp_id0");
        managerValues.put("imei", "IMEI-0");
        managerValues.put("reports_to", "Rama");
        managerValues.put("emp_name", "emp_name0");
        managerValues.put("emp_username", "u0");
        managerValues.put("emp_password", "p0");
        managerValues.put("created_date", getTodayDate());
        managerValues.put("modified_date", getTodayDate());
        managerValues.put("upload_status", 0);
        sqLiteDatabase.insert(TBL_EMPLOYEE, null, managerValues);


        ContentValues etaValues = new ContentValues();
        etaValues.put("emp_id", "id_etamine");
        etaValues.put("imei", Utils.getDeviceId(LoginActivity.this));
        etaValues.put("reports_to", "Ramesh");
        etaValues.put("emp_name", "etamine");
        etaValues.put("emp_username", "jaya");
        etaValues.put("emp_password", "jaya");
        etaValues.put("created_date", getTodayDate());
        etaValues.put("modified_date", getTodayDate());
        etaValues.put("upload_status", 0);
        sqLiteDatabase.insert(TBL_EMPLOYEE, null, etaValues);*/

        for (int i = 1; i <= 9; i++)
        {
           /* ContentValues employeeValues = new ContentValues();
            employeeValues.put("emp_id", "emp_id" + i);
            employeeValues.put("imei", "IMEI-" + i);
            employeeValues.put("reports_to", "emp_id0");
            employeeValues.put("emp_name", "emp_name" + i);
            employeeValues.put("emp_username", "u" + i);
            employeeValues.put("emp_password", "p" + i);
            employeeValues.put("created_date", getTodayDate());
            employeeValues.put("modified_date", getTodayDate());
            employeeValues.put("upload_status", 0);
            sqLiteDatabase.insert(TBL_EMPLOYEE, null, employeeValues);*/

          /*  ContentValues retailerValues = new ContentValues();
            retailerValues.put("retailer_id", "retailer_id" + i);
            retailerValues.put("emp_id", "emp_id" + i);
            retailerValues.put("retailer_name", "retailer_name" + i);
            retailerValues.put("shop_name", "shop_name" + i);
            retailerValues.put("shop_address", "shop_address" + i);
            retailerValues.put("pincode", "pincode" + i);
            retailerValues.put("mobile_no", "900080000" + i);
            retailerValues.put("email", "email" + i);
            retailerValues.put("area_id", "area_id" + i);
            retailerValues.put("latitude", NOT_PRESENT);
            retailerValues.put("longitude", NOT_PRESENT);
            retailerValues.put("img_source", NOT_PRESENT);
            retailerValues.put("created_date", getTodayDate());
            retailerValues.put("modified_date", getTodayDate());
            retailerValues.put("landmark", "landmark" + i);
            retailerValues.put("area", "area" + i);
            retailerValues.put("taluk", "taluk" + i);
            retailerValues.put("district", "district" + i);
            retailerValues.put("state", "state" + i);
            retailerValues.put("upload_status", 0);
            sqLiteDatabase.insert(TBL_RETAILER, null, retailerValues);*/

            ContentValues schemeValues = new ContentValues();
            schemeValues.put("scheme_id", "scheme_id" + i);
            schemeValues.put("scheme_name", "scheme_name" + i);
            schemeValues.put("scheme_description", "scheme_description" + i);
            schemeValues.put("created_date", String.format(getTodayDate(), -2));
            schemeValues.put("start_date", String.format(getTodayDate(), -1));
            schemeValues.put("end_date", getTodayDate());
            schemeValues.put("is_viewed", 0);
            schemeValues.put("upload_status", 0);
            sqLiteDatabase.insert(TBL_SCHEME, null, schemeValues);
        }

       /* for(int i = 1; i <= 10; i++)
        {
            ContentValues skuValues = new ContentValues();
            skuValues.put("sku_id", "sku_id" + i);
            skuValues.put("sku_name", "sku_name" + i);
            skuValues.put("sku_price", +i);
            skuValues.put("description", "description" + i);
            skuValues.put("sku_category", "sku_category" + i);
            skuValues.put("sku_sub_category", "sku_sub_category" + i);
            skuValues.put("new_sku", 1);
            skuValues.put("promotional_sku", 1);
            skuValues.put("sku_photo_source", "sku_photo_source" + i);
            skuValues.put("upload_status", 0);
            sqLiteDatabase.insert(TBL_SKU, null, skuValues);
        }

        //To display similar SKUs - SKUs with same cat, sub_cat as above
        for(int catAndSubCat = 1; catAndSubCat <= 10; catAndSubCat++)
        {
            int j = catAndSubCat + 10;

            ContentValues skuValues = new ContentValues();
            skuValues.put("sku_id", "sku_id" + j);
            skuValues.put("sku_name", "sku_name" + j);
            skuValues.put("sku_price", +j);
            skuValues.put("description", "description" + j);
            skuValues.put("sku_category", "sku_category" + catAndSubCat);
            skuValues.put("sku_sub_category", "sku_sub_category" + catAndSubCat);
            skuValues.put("new_sku", 1);
            skuValues.put("promotional_sku", 1);
            skuValues.put("sku_photo_source", "sku_photo_source" + j);6+
            skuValues.put("upload_status", 0);
            sqLiteDatabase.insert(TBL_SKU, null, skuValues);
        }*/

      /*  insertIntoGlobalAttrTable("color", "colorSet1", "any`red`green`blue");
        insertIntoGlobalAttrTable("color", "colorSet2", "any`pink`cyan`black");
        insertIntoGlobalAttrTable("size", "sizeSet1", "any`tiny`xlarge`medium");
        insertIntoGlobalAttrTable("size", "sizeSet2", "any`large`small`xxlarge");
        insertIntoGlobalAttrTable("shape", "shapeSet1", "any`circular`oval");
        insertIntoGlobalAttrTable("shape", "shapeSet2", "any`square`rectangle");*/

      /*  for(int i = 1; i <= 20; i++)
        {
            if((i % 2) == 0)
            {
                insetIntoSkuAttrMappingTable(i, 2);
                insetIntoSkuAttrMappingTable(i, 4);
                insetIntoSkuAttrMappingTable(i, 6);
            }
            else
            {
                insetIntoSkuAttrMappingTable(i, 1);
                insetIntoSkuAttrMappingTable(i, 3);
                //insetIntoSkuAttrMappingTable(i,5);
            }
        }*/

        ContentValues configValues = new ContentValues();
        configValues.put("config_for", "retailer visits per day");
        configValues.put("config_value", "15");
        configValues.put("upload_status", 0);
        sqLiteDatabase.insert(TBL_CONFIG, null, configValues);

        ContentValues configValues2 = new ContentValues();
        configValues2.put("config_for", "default date range in months");
        configValues2.put("config_value", "1");
        configValues2.put("upload_status", 0);
        sqLiteDatabase.insert(TBL_CONFIG, null, configValues2);

        ContentValues configValues3 = new ContentValues();
        configValues3.put("config_for", "maximum date range in months");
        configValues3.put("config_value", "6");
        configValues3.put("upload_status", 0);
        sqLiteDatabase.insert(TBL_CONFIG, null, configValues3);
    }

    void insertDummyLocationData()
    {
        String districtID;
        String talukID;

        for (int state = 1; state <= 3; state++)
        {
            ContentValues stateValues = new ContentValues();
            stateValues.put("loc_id", state);
            stateValues.put("loc_name", "state" + state);
            stateValues.put("hier_level", "1");
            stateValues.put("parent_loc_id", "0");
            stateValues.put("upload_status", 0);
            sqLiteDatabase.insert(TBL_LOCATION_HIERARCHY, null, stateValues);

            for (int district = 1; district <= 3; district++)
            {
                districtID = String.valueOf(state) + String.valueOf(district);

                ContentValues districtValues = new ContentValues();
                districtValues.put("loc_id", districtID);
                districtValues.put("loc_name", "district" + districtID);
                districtValues.put("hier_level", "2");
                districtValues.put("parent_loc_id", state);
                districtValues.put("upload_status", 0);
                sqLiteDatabase.insert(TBL_LOCATION_HIERARCHY, null, districtValues);

                for (int taluk = 1; taluk <= 3; taluk++)
                {
                    talukID = String.valueOf(state) + String.valueOf(district) + String.valueOf
                            (taluk);

                    ContentValues talukValues = new ContentValues();
                    talukValues.put("loc_id", talukID);
                    talukValues.put("loc_name", "taluk" + talukID);
                    talukValues.put("hier_level", "3");
                    talukValues.put("parent_loc_id", districtID);
                    talukValues.put("upload_status", 0);
                    sqLiteDatabase.insert(TBL_LOCATION_HIERARCHY, null, talukValues);

                    for (int area = 1; area <= 3; area++)
                    {
                        String areaID = String.valueOf(state) + String.valueOf(district) + String
                                .valueOf(taluk) + String.valueOf(area);

                        ContentValues areaValues = new ContentValues();
                        areaValues.put("loc_id", areaID);
                        areaValues.put("loc_name", "area" + areaID);
                        areaValues.put("hier_level", "4");
                        areaValues.put("parent_loc_id", talukID);
                        areaValues.put("upload_status", 0);
                        sqLiteDatabase.insert(TBL_LOCATION_HIERARCHY, null, areaValues);
                    }
                }
            }
        }
    }

    void insetIntoSkuAttrMappingTable(int skuIdNumber, int attributeID)
    {
        ContentValues skuAttrMappingValues = new ContentValues();
        skuAttrMappingValues.put("sku_id", "sku_id" + skuIdNumber);
        skuAttrMappingValues.put("attribute_id", attributeID);
        skuAttrMappingValues.put("upload_status", 0);
        sqLiteDatabase.insert(TBL_SKU_ATTRIBUTE_MAPPING, null, skuAttrMappingValues);
    }

    void insertIntoGlobalAttrTable(String attributeType, String attributeName, String
            attributeValuesSet)
    {
        ContentValues globalAttributeValues = new ContentValues();
        globalAttributeValues.put("attribute_type", attributeType);
        globalAttributeValues.put("attribute_name", attributeName);
        globalAttributeValues.put("attribute_value", attributeValuesSet);
        globalAttributeValues.put("upload_status", 0);
        sqLiteDatabase.insert(TBL_GLOBAL_ATTRIBUTES, null, globalAttributeValues);
    }

    private class setUpDatabaseAsyncTask extends AsyncTask<Void, Void, String>
    {
        private ProgressDialog progressDialog ;

        public setUpDatabaseAsyncTask(LoginActivity loginActivity)
        {

            progressDialog = new ProgressDialog(loginActivity);
        }

        @Override
        protected void onPreExecute()
        {
            //super.onPreExecute();
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setTitle("Downloading Database. Please wait");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setProgressNumberFormat(null);
            progressDialog.setProgressPercentFormat(null);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params)
        {

            try {
                Thread.sleep(5000);
                createTables();

                insertDummyData();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            /*try
            {
                createTables();

                insertDummyData();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
*/
            return "not applicable";
        }

        @Override
        protected void onPostExecute(String s)
        {
            //super.onPostExecute(s);
            progressDialog.dismiss();
        }
    }
}
