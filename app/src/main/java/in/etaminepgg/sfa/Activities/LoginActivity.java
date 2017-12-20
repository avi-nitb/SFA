package in.etaminepgg.sfa.Activities;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.etaminepgg.sfa.Models.AuthUser_Model;
import in.etaminepgg.sfa.Network.API_Call_Retrofit;
import in.etaminepgg.sfa.Network.ApiUrl;
import in.etaminepgg.sfa.Network.Apimethods;
import in.etaminepgg.sfa.R;
import in.etaminepgg.sfa.Utilities.Constants;
import in.etaminepgg.sfa.Utilities.ConstantsA;
import in.etaminepgg.sfa.Utilities.DbUtils;
import in.etaminepgg.sfa.Utilities.MyDb;
import in.etaminepgg.sfa.Utilities.MySharedPrefrencesData;
import in.etaminepgg.sfa.Utilities.MyUi;
import in.etaminepgg.sfa.Utilities.SharedPreferenceSingleton;
import in.etaminepgg.sfa.Utilities.Utils;
import in.etaminepgg.sfa.network_asynctask.AsyncResponse;
import in.etaminepgg.sfa.network_asynctask.AsyncWorker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static in.etaminepgg.sfa.Utilities.Constants.JINGLE_FILE_NAME;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_CONFIG;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_GLOBAL_ATTRIBUTES;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_LOCATION_HIERARCHY;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SALES_ORDER_SKU_ATTRIBUTES;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SKU_ATTRIBUTE_MAPPING;
import static in.etaminepgg.sfa.Utilities.Constants.dbFileFullPath;
import static in.etaminepgg.sfa.Utilities.Constants.appSpecificDirectoryPath;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_EMPLOYEE;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SKU;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_ITEM_SCHEME_RELATION;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_AREA;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_RETAILER;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_RETAILER_VISIT;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SALES_ORDER;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SALES_ORDER_DETAILS;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SCHEME;
import static in.etaminepgg.sfa.Utilities.ConstantsA.NOT_PRESENT;
import static in.etaminepgg.sfa.Utilities.SharedPreferenceSingleton.MY_PREF;
import static in.etaminepgg.sfa.Utilities.Utils.getDeviceId;
import static in.etaminepgg.sfa.Utilities.Utils.getTodayDate;
import static in.etaminepgg.sfa.Utilities.Utils.loggedInUserID;
import static in.etaminepgg.sfa.Utilities.Utils.loggedInUserName;
import static in.etaminepgg.sfa.Utilities.Utils.makeThreadSleepFor;


public class LoginActivity extends AppCompatActivity implements AsyncResponse
{
    public static String DbFileName = "sfaDb.ma7";
    public static final String uploadToURL = "http://etaminepgg.com/sfa/fromMobile_gfjsdfkhweriusfgkjsdhsakjsdfhgsfdkjsflksfd324435.php";
    public static final String downloadFromURL = "http://etaminepgg.com/sfa/toMobile_giuwer4598wekhhwiergfhj3t49gjhgfkjewr.php?file=";
    public static final String lastModifiedDbDateURL = "http://etaminepgg.in/aaaaabbjeroidgkdflkfdrhfkjgrdflkjdfgfdjg/incometax/checkDataDt.php";
    //appSpecificDirectoryPath, dbFileFullPath
    public static Context baseContext;

    Button login_Button;
    EditText username_EditText, password_EditText;
    int valueFromOpenDatabase;
    SQLiteDatabase sqLiteDatabase;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPreferencesEditor;

    String usernameEntered = "usernameEntered";
    String passwordEntered = "passwordEntered";

    public static String KEY_USERNAME = "username";
    public static String KEY_PASSWORD = "password";
    String imei;

    MySharedPrefrencesData mySharedPrefrencesData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        baseContext = getBaseContext();
        mySharedPrefrencesData=new MySharedPrefrencesData();
        //appSpecificDirectoryPath = baseContext.getExternalFilesDir(null);
        // appSpecificDirectoryPath = Environment.getExternalStorageDirectory().toString();

        appSpecificDirectoryPath = getApplicationContext().getExternalFilesDir(null).getAbsolutePath();
        dbFileFullPath = appSpecificDirectoryPath + File.separator + DbFileName;

        downloadDatabaseIfNotExists();

        makeThreadSleepFor(1000);
        imei=Utils.getDeviceId(LoginActivity.this);

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


        launchDashboardIfLoggedIn();
    }

    @Override
    protected void onDestroy()
    {
        sqLiteDatabase.releaseReference();
        super.onDestroy();
    }

    /* @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }*/

    private void findViewsByIDs()
    {
        login_Button = (Button) findViewById(R.id.login_Button);
        username_EditText = (EditText)findViewById(R.id.username_EditText);
        password_EditText = (EditText)findViewById(R.id.password_EditText);

        username_EditText.setText("surya");
        password_EditText.setText("surya");
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

                //!usernameEntered.isEmpty() && !passwordEntered.isEmpty()
                if (isValidUser(usernameEntered, passwordEntered))
                {
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

                   /* Utils.launchActivity(getBaseContext(), DashboardActivity.class);
                    finish();*/

                   //networkcall_for_authtoken(getDeviceId(LoginActivity.this),usernameEntered,passwordEntered);

                    networkAsync();

                }
                else
                {
                    Utils.showPopUp(getBaseContext(), "Please Enter Valid Username, Password");
                }
            }
        });
    }


    private void launchDashboardIfLoggedIn()
    {
        downloadDatabaseIfNotExists();
        //makeThreadSleepFor(1);

        sharedPreferences = getSharedPreferences(MY_PREF, Context.MODE_PRIVATE);
        String spDefaultValue = "spDefaultValue"; //sp = shared preferences

        String usernameInSharedPreferences = sharedPreferences.getString(KEY_USERNAME, spDefaultValue);
        String passwordInSharedPreferences = sharedPreferences.getString(KEY_PASSWORD, spDefaultValue);

        loggedInUserName = usernameInSharedPreferences;
        loggedInUserID = getEmployeeIdFor(loggedInUserName);

        Log.e("usernameInSharedPref", usernameInSharedPreferences);
        Log.e("passwordInSharedPref", passwordInSharedPreferences);

        if (isValidUser(usernameInSharedPreferences, passwordInSharedPreferences))
        {
            loggedInUserID = getEmployeeIdFor(loggedInUserName);
            SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");



            SharedPreferences settings = getSharedPreferences(MySharedPrefrencesData.PREFS_NAME, 0);
            //Get "hasLoggedIn" value. If the value doesn't exist yet false is returned
            boolean hasLoggedIn = settings.getBoolean("hasLoggedIn", false);
            if(hasLoggedIn){

                Utils.launchActivity(getBaseContext(), DashboardActivity.class);
                finish();
            }else {

                networkAsync();
            }

               /* String authkey_expirydate_frm_sp=mySharedPrefrencesData.getAuthTokenExpiryDate(LoginActivity.this);
                Date date_sp=sdf.parse(authkey_expirydate_frm_sp);

                String current_date=Utils.getDateTime();
                Date date_current=sdf.parse(current_date);

                if(date_sp.after(date_current)){
                    Utils.launchActivity(getBaseContext(), DashboardActivity.class);
                    finish();
                }else {

                    networkAsync();
                }*/


            //System.exit(0);

        }
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void ReceivedResponseFromServer(String output, String REQUEST_NUMBER) {
        switch (REQUEST_NUMBER){
            case Constants.REQUEST_FOR_AUTHKEY:
                try {
                    JSONObject jsonObject=new JSONObject(output);
                    if(jsonObject.optInt("api_status")==1){
                        AuthUser_Model authUser_model=new Gson().fromJson(output,AuthUser_Model.class);
                        Log.e("auth_model",authUser_model.getAuthToken());
                        mySharedPrefrencesData.setEmployee_AuthKey(LoginActivity.this,authUser_model.getAuthToken());
                        mySharedPrefrencesData.setAuthTokenExpiryDate(LoginActivity.this,authUser_model.getAuthTokenExpiryDate());
                        SharedPreferences settings = getSharedPreferences(MySharedPrefrencesData.PREFS_NAME, 0); // 0 - for private mode
                        SharedPreferences.Editor editor = settings.edit();

                        //Set "hasLoggedIn" to true
                        editor.putBoolean("hasLoggedIn", true);
                        // Commit the edits!
                        editor.commit();

                        Utils.launchActivity(getBaseContext(), DashboardActivity.class);
                        finish();

                    }else {
                        Utils.showToast(baseContext,"Unsuccessful api call");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

        }
    }

    private class setUpDatabaseAsyncTask extends AsyncTask<Void, Void, String>
    {
        ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);

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
            try
            {
                createTables();

                insertDummyData();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            return "not applicable";
        }

        @Override
        protected void onPostExecute(String s)
        {
            //super.onPostExecute(s);
            progressDialog.dismiss();
        }
    }

    private void downloadDatabaseIfNotExists()
    {
        File f = new File(appSpecificDirectoryPath, DbFileName);

        if (!f.exists())
        {
            Log.d("File Status", "Not Exist");
            new setUpDatabaseAsyncTask().execute();
        }
        else
        {
            valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
            sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);
        }
    }

    private boolean isValidUser(String userName, String password)
    {
        String SQL_SELECT_SALES_PERSON = "SELECT emp_id FROM " + TBL_EMPLOYEE + " WHERE " + "emp_username " + "= ? AND " + "emp_password " + "= ?";
        String[] selectionArgs = {userName, password};
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_SALES_PERSON, selectionArgs);
        boolean isUserFound = cursor.moveToFirst();
        cursor.close();
        return isUserFound;
    }



    private void networkcall_for_authtoken(String imei,String username,String password) {

        Apimethods methods = API_Call_Retrofit.getretrofit(this).create(Apimethods.class);
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("IMEI","erdddddddddddasdasdasdasdasdasdw10");
            jsonObject.put("username",username);
            jsonObject.put("password",password);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Call<AuthUser_Model> call =methods.getUserAuthKey(jsonObject);
        Log.d("url","url="+call.request().url().toString());

        call.enqueue(new Callback<AuthUser_Model>() {
            @Override
            public void onResponse(Call<AuthUser_Model> call, Response<AuthUser_Model> response) {
                int statusCode = response.code();
                Log.d("Response",""+statusCode);
                Log.d("respones",""+response);
                AuthUser_Model authUser_model=response.body();
                if(authUser_model.getApiStatus()==1)
                {
                   Utils.showToast(baseContext,authUser_model.getAuthToken());
                   Utils.launchActivity(getBaseContext(), DashboardActivity.class);
                   finish();
                }else
                {
                    Utils.showToast(baseContext,authUser_model.getMessage());
                }
                Utils.launchActivity(getBaseContext(), DashboardActivity.class);
                finish();

            }

            @Override
            public void onFailure(Call<AuthUser_Model> call, Throwable t) {

                Utils.showToast(baseContext, ConstantsA.NO_INTERNET_CONNECTION);

            }
        });

    }

    private String getEmployeeIdFor(String salesPersonUsername)
    {
        String salesPersonEmpID = "getEmployeeIdFor()";
        String SQL_SELECT_SALES_PERSON_ID = "SELECT " + "emp_id" + " FROM " + TBL_EMPLOYEE + " WHERE " + "emp_username " + "= ?";
        String[] selectionArgs = {salesPersonUsername};
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_SALES_PERSON_ID, selectionArgs);
        if(cursor.moveToFirst())
        {
            salesPersonEmpID = cursor.getString(cursor.getColumnIndexOrThrow("emp_id"));
        }
        cursor.close();
        return salesPersonEmpID;
    }

    private void createTables()
    {
        valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        sqLiteDatabase.execSQL("CREATE TABLE " + TBL_LOCATION_HIERARCHY + " ("
                + "loc_id varchar(50), "
                + "loc_name varchar(50), "
                + "hier_level varchar(50), "
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
                + "sku_id varchar(50), "
                + "sku_name varchar(50), "
                + "sku_price varchar(50), "
                + "description varchar(50), "
                + "sku_category varchar(50), "
                + "sku_sub_category varchar(50), "
                + "new_sku varchar(1), "
                + "promotional_sku varchar(1), "
                + "sku_size varchar(50), "
                + "sku_material varchar(50), "
                + "sku_usage varchar(50), "
                + "sku_color varchar(50), "
                + "sku_shape varchar(50), "
                + "sku_photo_source varchar(50), "
                + "sku_uom varchar(50), "
                + "created_date varchar(50), "
                + "modified_date varchar(50), "
                + "created_by varchar(50), "
                + "modified_by varchar(50), "
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
                + "attribute_id INTEGER PRIMARY KEY, "
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
                + "is_regular varchar(1), "
                + "created_date varchar(50), "
                + "modified_date varchar(50), "
                + "is_active varchar(1), "
                + "is_placed varchar(1), "
                + "is_cancelled varchar(1), "
                + "upload_status varchar(1) );");

        sqLiteDatabase.execSQL("CREATE TABLE " + TBL_SALES_ORDER_DETAILS + " ("
                + "order_detail_id  INTEGER PRIMARY KEY, "
                + "order_id varchar(50), "
                + "sku_id varchar(50), "
                + "sku_name varchar(50), "
                + "sku_price varchar(50), "
                + "sku_qty varchar(50), "
                + "sku_final_price varchar(50), "
                /*+ "scheme_id varchar(50), "*/
                + "upload_status varchar(1) );");

        sqLiteDatabase.execSQL("CREATE TABLE " + TBL_SALES_ORDER_SKU_ATTRIBUTES + " ("
                + "sosa_id INTEGER PRIMARY KEY, " //sosa_id - sales_order_sku_attribute_id
                + "order_detail_id INTEGER, "
                + "attribute_id INTEGER, "
                + "attribute_name varchar(50), "
                + "attribute_value varchar(100), "
                + "created_date varchar(50), "
                + "modified_date varchar(50), "
                + "created_by varchar(50), "
                + "modified_by varchar(50), "
                + "upload_status varchar(1) );");

        sqLiteDatabase.execSQL("CREATE TABLE " + TBL_CONFIG + " ("
                + "config_id INTEGER PRIMARY KEY, "
                + "config_for varchar(100), "
                + "config_value varchar(50), "
              /*  + "created_date varchar(50), "
                + "modified_date varchar(50), "
                + "created_by varchar(50), "
                + "modified_by varchar(50), "*/
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
    }

    private void insertDummyData()
    {
        insertDummyLocationData();

        ContentValues contentValues = new ContentValues();
        contentValues.put("emp_id", "id_surya");
        contentValues.put("imei",Utils.getDeviceId(LoginActivity.this));
        contentValues.put("reports_to", "abhinav");
        contentValues.put("emp_name", "Surya");
        contentValues.put("emp_username", "surya");
        contentValues.put("emp_password", "surya");
        contentValues.put("created_date",  getTodayDate());
        contentValues.put("modified_date",  getTodayDate());
        contentValues.put("upload_status", 0);
        sqLiteDatabase.insert(TBL_EMPLOYEE, null, contentValues);


        ContentValues managerValues = new ContentValues();
        managerValues.put("emp_id", "emp_id0");
        managerValues.put("imei", "IMEI-0");
        managerValues.put("reports_to", "Rama");
        managerValues.put("emp_name", "emp_name0");
        managerValues.put("emp_username", "u0");
        managerValues.put("emp_password", "p0");
        managerValues.put("created_date",  getTodayDate());
        managerValues.put("modified_date",  getTodayDate());
        managerValues.put("upload_status", 0);
        sqLiteDatabase.insert(TBL_EMPLOYEE, null, managerValues);

        for (int i = 1; i <= 9; i++)
        {
            ContentValues employeeValues = new ContentValues();
            employeeValues.put("emp_id", "emp_id" + i);
            employeeValues.put("imei", "IMEI-" + i);
            employeeValues.put("reports_to", "emp_id0");
            employeeValues.put("emp_name", "emp_name" + i);
            employeeValues.put("emp_username", "u" + i);
            employeeValues.put("emp_password", "p" + i);
            employeeValues.put("created_date", getTodayDate());
            employeeValues.put("modified_date", getTodayDate());
            employeeValues.put("upload_status", 0);
            sqLiteDatabase.insert(TBL_EMPLOYEE, null, employeeValues);

            ContentValues retailerValues = new ContentValues();
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
            sqLiteDatabase.insert(TBL_RETAILER, null, retailerValues);

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

        for (int i = 1; i <= 10; i++)
        {
            ContentValues skuValues = new ContentValues();
            skuValues.put("sku_id", "sku_id" + i);
            skuValues.put("sku_name", "sku_name" + i);
            skuValues.put("sku_price",  + i);
            skuValues.put("description", "description" + i);
            skuValues.put("sku_category", "sku_category" + i);
            skuValues.put("sku_sub_category", "sku_sub_category" + i);
            skuValues.put("new_sku", 1);
            skuValues.put("promotional_sku", 1);
            skuValues.put("sku_size", "sku_size" + i);
            skuValues.put("sku_material", "sku_material" + i);
            skuValues.put("sku_usage", "sku_usage" + i);
            skuValues.put("sku_color", "sku_color" + i);
            skuValues.put("sku_shape", "sku_shape" + i);
            skuValues.put("sku_photo_source", "sku_photo_source" + i);
            skuValues.put("sku_uom", "sku_uom" + i);
            skuValues.put("upload_status", 0);
            sqLiteDatabase.insert(TBL_SKU, null, skuValues);
        }

        //To display similar SKUs - SKUs with same cat, sub_cat as above
        for (int catAndSubCat = 1; catAndSubCat <= 10; catAndSubCat++)
        {
            int j = catAndSubCat+10;

            ContentValues skuValues = new ContentValues();
            skuValues.put("sku_id", "sku_id" + j);
            skuValues.put("sku_name", "sku_name" + j);
            skuValues.put("sku_price", + j);
            skuValues.put("description", "description" + j);
            skuValues.put("sku_category", "sku_category" + catAndSubCat);
            skuValues.put("sku_sub_category", "sku_sub_category" + catAndSubCat);
            skuValues.put("new_sku", 1);
            skuValues.put("promotional_sku", 1);
            skuValues.put("sku_size", "sku_size" + j);
            skuValues.put("sku_material", "sku_material" + j);
            skuValues.put("sku_usage", "sku_usage" + j);
            skuValues.put("sku_color", "sku_color" + j);
            skuValues.put("sku_shape", "sku_shape" + j);
            skuValues.put("sku_photo_source", "sku_photo_source" + j);
            skuValues.put("sku_uom", "sku_uom" + j);
            skuValues.put("upload_status", 0);
            sqLiteDatabase.insert(TBL_SKU, null, skuValues);
        }

        insertIntoGlobalAttrTable("color","colorSet1", "any`red`green`blue");
        insertIntoGlobalAttrTable("color","colorSet2", "any`pink`cyan`black");
        insertIntoGlobalAttrTable("size","sizeSet1", "any`tiny`xlarge`medium");
        insertIntoGlobalAttrTable("size","sizeSet2", "any`large`small`xxlarge");
        insertIntoGlobalAttrTable("shape","shapeSet1", "any`circular`oval");
        insertIntoGlobalAttrTable("shape","shapeSet2", "any`square`rectangle");

        for (int i = 1; i<=20; i++)
        {
            if ((i%2) == 0)
            {
                insetIntoSkuAttrMappingTable(i,2);
                insetIntoSkuAttrMappingTable(i,4);
                insetIntoSkuAttrMappingTable(i,6);
            }
            else
            {
                insetIntoSkuAttrMappingTable(i,1);
                insetIntoSkuAttrMappingTable(i,3);
                //insetIntoSkuAttrMappingTable(i,5);
            }
        }

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

        for (int state=1; state<=3; state++)
        {
            ContentValues stateValues = new ContentValues();
            stateValues.put("loc_id", state);
            stateValues.put("loc_name", "state"+state);
            stateValues.put("hier_level", "1");
            stateValues.put("parent_loc_id", "0");
            stateValues.put("upload_status", 0);
            sqLiteDatabase.insert(TBL_LOCATION_HIERARCHY, null, stateValues);

            for (int district=1; district<=3; district++)
            {
                districtID = String.valueOf(state) + String.valueOf(district);

                ContentValues districtValues = new ContentValues();
                districtValues.put("loc_id", districtID);
                districtValues.put("loc_name", "district"+districtID);
                districtValues.put("hier_level", "2");
                districtValues.put("parent_loc_id", state);
                districtValues.put("upload_status", 0);
                sqLiteDatabase.insert(TBL_LOCATION_HIERARCHY, null, districtValues);

                for (int taluk=1; taluk<=3; taluk++)
                {
                    talukID = String.valueOf(state) + String.valueOf(district) + String.valueOf(taluk);

                    ContentValues talukValues = new ContentValues();
                    talukValues.put("loc_id", talukID);
                    talukValues.put("loc_name", "taluk"+talukID);
                    talukValues.put("hier_level", "3");
                    talukValues.put("parent_loc_id", districtID);
                    talukValues.put("upload_status", 0);
                    sqLiteDatabase.insert(TBL_LOCATION_HIERARCHY, null, talukValues);

                    for (int area=1; area<=3; area++)
                    {
                        String areaID = String.valueOf(state) + String.valueOf(district) + String.valueOf(taluk) + String.valueOf(area);

                        ContentValues areaValues = new ContentValues();
                        areaValues.put("loc_id", areaID);
                        areaValues.put("loc_name", "area"+areaID);
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
        skuAttrMappingValues.put("sku_id", "sku_id"+skuIdNumber);
        skuAttrMappingValues.put("attribute_id", attributeID);
        skuAttrMappingValues.put("upload_status", 0);
        sqLiteDatabase.insert(TBL_SKU_ATTRIBUTE_MAPPING, null, skuAttrMappingValues);
    }

    void insertIntoGlobalAttrTable(String attributeType, String attributeName, String attributeValuesSet)
    {
        ContentValues globalAttributeValues = new ContentValues();
        globalAttributeValues.put("attribute_type", attributeType);
        globalAttributeValues.put("attribute_name", attributeName);
        globalAttributeValues.put("attribute_value", attributeValuesSet);
        globalAttributeValues.put("upload_status", 0);
        sqLiteDatabase.insert(TBL_GLOBAL_ATTRIBUTES, null, globalAttributeValues);
    }


    public void networkAsync(){
        AsyncWorker mWorker = new AsyncWorker(LoginActivity.this);
        mWorker.delegate = LoginActivity.this;
        JSONObject BroadcastObject = new JSONObject();
        try {


            BroadcastObject.put("IMEI",imei );
            BroadcastObject.put("username", usernameEntered);
            BroadcastObject.put("password", passwordEntered);
            Log.e("deviceid",imei);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        mWorker.execute(ApiUrl.BASE_URL+ApiUrl.LOG_URL_AUTHUSER, BroadcastObject.toString(), Constants.POST_REQUEST, Constants.REQUEST_FOR_AUTHKEY);
    }
}
