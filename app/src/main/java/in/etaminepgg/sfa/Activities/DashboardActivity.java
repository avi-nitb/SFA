package in.etaminepgg.sfa.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import in.etaminepgg.sfa.BuildConfig;
import in.etaminepgg.sfa.InputModel_For_Network.IM_GetSkuListAfter;
import in.etaminepgg.sfa.InputModel_For_Network.IM_IsValidAuthKey;
import in.etaminepgg.sfa.InputModel_For_Network.IM_Login;
import in.etaminepgg.sfa.InputModel_For_Network.IM_RetailerInfo;
import in.etaminepgg.sfa.Models.AuthUser_Model;
import in.etaminepgg.sfa.Models.GetSkuCategorySubCategorylist;
import in.etaminepgg.sfa.Models.GetSkuListAfterNew;
import in.etaminepgg.sfa.Models.IsSkuListUpdate;
import in.etaminepgg.sfa.Models.Location_Model;
import in.etaminepgg.sfa.Models.RetailerInfo_Model;
import in.etaminepgg.sfa.Models.RetailerList_Model;
import in.etaminepgg.sfa.Models.ValidAuthModel;
import in.etaminepgg.sfa.Network.API_Call_Retrofit;
import in.etaminepgg.sfa.Network.ApiUrl;
import in.etaminepgg.sfa.Network.Apimethods;
import in.etaminepgg.sfa.R;
import in.etaminepgg.sfa.Utilities.Constants;
import in.etaminepgg.sfa.Utilities.ConstantsA;
import in.etaminepgg.sfa.Utilities.DbUtils;
import in.etaminepgg.sfa.Utilities.MyDb;
import in.etaminepgg.sfa.Utilities.MySharedPrefrencesData;
import in.etaminepgg.sfa.Utilities.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static in.etaminepgg.sfa.Utilities.Constants.SHOW_UPDATE_BUTTON;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_EMPLOYEE;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_LOCATION_HIERARCHY;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_RETAILER;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SKU_CATEGORY;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SKU_SUBCATEGORY;
import static in.etaminepgg.sfa.Utilities.Constants.dbFileFullPath;
import static in.etaminepgg.sfa.Utilities.ConstantsA.KEY_ISNEWORREGULAR;
import static in.etaminepgg.sfa.Utilities.ConstantsA.KEY_MOBILE_RETAILER_ID;
import static in.etaminepgg.sfa.Utilities.ConstantsA.KEY_RETAILER_ID;
import static in.etaminepgg.sfa.Utilities.ConstantsA.NOT_PRESENT;
import static in.etaminepgg.sfa.Utilities.ConstantsA.NO_INTERNET_CONNECTION;
import static in.etaminepgg.sfa.Utilities.MySharedPrefrencesData.PREFS_NAME;
import static in.etaminepgg.sfa.Utilities.Utils.dismissProgressDialog;
import static in.etaminepgg.sfa.Utilities.Utils.getIST;

public class DashboardActivity extends AppCompatActivity implements OnNavigationItemSelectedListener
{
    public static boolean wasShown = false;
    public static boolean wasShown_month = false;
    DrawerLayout drawerLayout;
    View headerLayout;
    TextView header_email;
    TextView header_mob;
    TextView header_username;
    boolean isValidAuthkey = false;
    MySharedPrefrencesData mySharedPrefrencesData = new MySharedPrefrencesData();
    NavigationView navigationView;
    TextView noOfPromoSchemes_TextView;
    int noOfRetailerVisits = 0;
    TextView noOfRetailerVisits_TextView;
    String passwordInSharedPreferences;
    ImageView promoSchemesBell_ImageView;
    FrameLayout promoSchemes_FrameLayout;

    Button btn_newSalesOrder, btn_retailerModule, btn_viewSKUS, btn_salesSummary, btn_downloadHome, btn_uploadHome;
    boolean isUpdated = false;


    LinearLayout ll_newpro, ll_allpro, ll_promopro, ll_frequentpro;

    LinearLayout retailerVisits_LinearLayout;
    ProgressBar retailerVisits_ProgressBar;
    SQLiteDatabase sqLiteDatabase;
    Toolbar toolbar;
    String type = "1";
    String usernameInSharedPreferences;
    int valueFromOpenDatabase;

    String intentExtraKey_selectedOrderType, activeorderid;
    String date_str = "";


    int a = 0;
    int b = 0;


    //SKU image URL set
    public static String substringAfterLastSeparator(String str, String separator)
    {
        if (MyDb.isEmpty(str) || MyDb.isEmpty(separator))
        {
            return str;
        }
        int pos = str.lastIndexOf(separator);
        return pos != -1 ? str.substring(pos, str.length()) : str;
    }


    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_dashboard_new);

        float density = getResources().getDisplayMetrics().density;

        Log.d("density", "" + density);

        intentExtraKey_selectedOrderType = getResources().getString(R.string.key_selected_order_type);

        //activeorderid = DbUtils.getActiveOrderID();

        findViewsByIDs();
        setListenersToViews();
        this.header_username.setText(new MySharedPrefrencesData().getUsername(this));
        this.header_email.setText(new MySharedPrefrencesData().getEmail(this));
        this.header_mob.setText(new MySharedPrefrencesData().get_User_mobile(this) + "\n" + "App Version: " + BuildConfig.VERSION_NAME);
        Utils.loggedInUserID = new MySharedPrefrencesData().getUser_Id(this);
        setSupportActionBar(this.toolbar);
        getSupportActionBar().setTitle((CharSequence) "Dashboard");
        this.valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        this.sqLiteDatabase = MyDb.getDbHandle(this.valueFromOpenDatabase);
        this.mySharedPrefrencesData = new MySharedPrefrencesData();
        Utils.loggedInUserID = new MySharedPrefrencesData().getUser_Id(this);
        if (Utils.isNetworkConnected(getBaseContext()))
        {

            networkcall_for_ISValidAuthKey();

        }
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, this.drawerLayout, this.toolbar, R.string.open, R.string.close);
        this.drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        askUserToGrantPermissions();
        updateIMEI(Utils.loggedInUserID);

    }


    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.notification_menu:
                Toast.makeText(getApplicationContext(), "Developement under process.", Toast.LENGTH_LONG).show();
                return true;
            case R.id.schemes_menu:
                Toast.makeText(getApplicationContext(), "Developement under process.", Toast.LENGTH_LONG).show();
                //  Utils.launchActivity(DashboardActivity.this, SchemesActivity.class);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void onPostResume()
    {
        super.onPostResume();

        // activeorderid = DbUtils.getActiveOrderID();

        if (Utils.loggedInUserID == null)
        {
            throw new NullPointerException("loggedInUserID is null");
        }
        this.noOfRetailerVisits = DbUtils.getRetailerVisitsFor(Utils.loggedInUserID);
        this.retailerVisits_ProgressBar.setProgress(this.noOfRetailerVisits);
        this.noOfRetailerVisits_TextView.setText(String.valueOf(this.noOfRetailerVisits));
        setColorsDependingOnRetailerVisits();


        int mandatory_visits_per_day = getMandatoryVisitfortheDay_db();
        int mandatory_retailer_creation_per_month = getMandatoryRetailerCreationforMonth_db();


        int mandatory_retailer_per_month = getRetalierCountPerMonth();
        if (this.noOfRetailerVisits >= mandatory_visits_per_day && !wasShown && this.noOfRetailerVisits != 0)
        {
            showCongratulatoryMessage(this.noOfRetailerVisits);
            playJingle();
        }

        if (mandatory_retailer_per_month >= mandatory_retailer_creation_per_month && !wasShown_month && mandatory_retailer_per_month != 0)
        {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            String skipMessage = settings.getString("skipMessage" + mySharedPrefrencesData.getUser_Id(getBaseContext()), "NOT checked");
            if (!skipMessage.equals("checked"))
            {

                showCongratulatoryMessage_Retailecreation(mandatory_retailer_per_month);
            }
        }
    }

    //get mandatory retailer creation per month from db from config table
    private int getMandatoryRetailerCreationforMonth_db()
    {

        String mandatory_retailer_creation_per_month = ConstantsA.NONE;
        String SQL_FROM_CONFIG = "select  config_value from " + Constants.TBL_CONFIG + " WHERE config_for = ? ;";
        String[] selectionArgs = new String[]{Constants.RETAILER_CREATION_PER_MONTH};
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(MyDb.openDatabase(dbFileFullPath));
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_FROM_CONFIG, selectionArgs);
        if (cursor.moveToFirst())
        {
            mandatory_retailer_creation_per_month = cursor.getString(cursor.getColumnIndexOrThrow("config_value"));
        }
        cursor.close();
        sqLiteDatabase.close();

        return Integer.parseInt(mandatory_retailer_creation_per_month);
    }


    //get  retailer creation per month from db from retailer  table
    private int getRetalierCountPerMonth()
    {
        int mandatory_retailer_per_month = 0;
        String SQL_SELECT_RETAILER_ITEM_COUNT = "SELECT created_by , created_date  FROM " + Constants.TBL_RETAILER + " WHERE (created_by = ? ) ";
        String[] selectionArgs = new String[]{new MySharedPrefrencesData().getUser_Id(this)};
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(MyDb.openDatabase(dbFileFullPath));
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_RETAILER_ITEM_COUNT, selectionArgs);
        while (cursor.moveToNext())
        {
            String created_date_from_db = cursor.getString(cursor.getColumnIndexOrThrow("created_date"));
            SimpleDateFormat formatNowYear = new SimpleDateFormat("yyyy");
            Date date = stringToDate(created_date_from_db, "yyyy-MM-dd");
            Log.e("month", "" + date.getMonth());
            Log.e("year", "" + formatNowYear.format(date));
            Log.e("year1", "" + Calendar.getInstance().get(Calendar.YEAR));
            Log.e("month1", "" + Calendar.getInstance().get(Calendar.MONTH));
            if (date != null && date.getMonth() + 1 == Calendar.getInstance().get(Calendar.MONTH) + 1 && Integer.parseInt(formatNowYear.format(date)) == Calendar.getInstance().get(Calendar.YEAR))
            {
                mandatory_retailer_per_month++;
            }
        }
        cursor.close();
        sqLiteDatabase.close();
        return mandatory_retailer_per_month;
    }

    private Date stringToDate(String aDate, String aFormat)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
        Date convertedDate = new Date();
        try
        {
            convertedDate = dateFormat.parse(aDate);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        System.out.println(convertedDate);
        return convertedDate;
    }


    //get mandatory retailer cisit per day from db from config table
    private int getMandatoryVisitfortheDay_db()
    {
        String mandatory_visits_per_day = ConstantsA.NONE;
       /* String SQL_FROM_CONFIG = "select  config_value from " + Constants.TBL_CONFIG + " WHERE config_for = ? ;";
        String[] selectionArgs = new String[]{"mandatory_visits_per_day"};
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(MyDb.openDatabase(dbFileFullPath));
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_FROM_CONFIG, selectionArgs);
        if (cursor.moveToFirst())
        {
            mandatory_visits_per_day = cursor.getString(cursor.getColumnIndexOrThrow("config_value"));
        }
        cursor.close();
        sqLiteDatabase.close();*/

        String userlocationid = new MySharedPrefrencesData().getRetailerVisit_LocationId(getBaseContext());

        if (userlocationid.contains(","))
        {

            String location_id_arr[] = userlocationid.split(",");

            int sum = 0;

            for (String i : location_id_arr)
            {
                sum += Integer.parseInt(i);
            }

            mandatory_visits_per_day = String.valueOf(sum);


        }
        else
        {

            mandatory_visits_per_day = userlocationid;

        }
        return Integer.parseInt(mandatory_visits_per_day);
    }

    private void findViewsByIDs()
    {
        this.drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        this.navigationView = (NavigationView) findViewById(R.id.navigationView);
        this.headerLayout = this.navigationView.getHeaderView(0);
        this.header_username = (TextView) this.headerLayout.findViewById(R.id.profile_name);
        this.header_email = (TextView) this.headerLayout.findViewById(R.id.profile_email);
        this.header_mob = (TextView) this.headerLayout.findViewById(R.id.profile_mob);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.promoSchemes_FrameLayout = (FrameLayout) findViewById(R.id.promoSchemes_FrameLayout);
        this.promoSchemesBell_ImageView = (ImageView) findViewById(R.id.promoSchemesBell_ImageView);
        this.noOfPromoSchemes_TextView = (TextView) findViewById(R.id.noOfPromoSchemes_TextView);
        this.retailerVisits_ProgressBar = (ProgressBar) findViewById(R.id.retailerVisits_ProgressBar);
        this.retailerVisits_LinearLayout = (LinearLayout) findViewById(R.id.retailerVisits_LinearLayout);
        this.noOfRetailerVisits_TextView = (TextView) findViewById(R.id.noOfRetailerVisits_TextView);


        this.ll_allpro = (LinearLayout) findViewById(R.id.ll_allpro);
        this.ll_frequentpro = (LinearLayout) findViewById(R.id.ll_frequentpro);
        this.ll_newpro = (LinearLayout) findViewById(R.id.ll_newpro);
        this.ll_promopro = (LinearLayout) findViewById(R.id.ll_promopro);


        this.btn_newSalesOrder = (Button) findViewById(R.id.btn_newSalesOrder);
        this.btn_retailerModule = (Button) findViewById(R.id.btn_retailerModule);
        this.btn_viewSKUS = (Button) findViewById(R.id.btn_viewSKUS);
        this.btn_salesSummary = (Button) findViewById(R.id.btn_salesSummary);
        this.btn_downloadHome = (Button) findViewById(R.id.btn_downloadhome);
        this.btn_uploadHome = (Button) findViewById(R.id.btn_uploadhome);
    }

    private void setListenersToViews()
    {
        final String intentExtraKey_TabToShow = getResources().getString(R.string.key_tab_to_show);
        this.navigationView.setNavigationItemSelectedListener(this);
        this.retailerVisits_LinearLayout.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Utils.launchActivity(DashboardActivity.this, SalesSummaryActivity.class);
            }
        });
        this.retailerVisits_ProgressBar.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Utils.launchActivity(DashboardActivity.this, SalesSummaryActivity.class);
            }
        });
        this.ll_newpro.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {

            }
        });
        this.ll_promopro.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                boolean isPromotion = DbUtils.getConfigValue(Constants.PROMOTION_REQUIRED);

                if (isPromotion)
                {

                    Utils.launchActivityWithExtra(DashboardActivity.this, SkuListByGenreActivity.class, intentExtraKey_TabToShow, ConstantsA.PROMO_SKUs_TAB);
                }
                else
                {
                    Utils.showToast(getBaseContext(), "There is no promotional schemes for retailers.");
                }

            }
        });
        this.ll_frequentpro.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {


            }
        });
        this.ll_allpro.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {

            }
        });


        btn_newSalesOrder.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Utils.launchActivityWithExtra(DashboardActivity.this, PickRetailerActivity.class, SHOW_UPDATE_BUTTON, "NO");
            }
        });

        btn_retailerModule.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Utils.launchActivity(DashboardActivity.this, RetailerModuleActivity.class);
            }
        });

        btn_viewSKUS.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {


                Intent intent = new Intent(DashboardActivity.this, SkuListByGenreActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(intentExtraKey_TabToShow, ConstantsA.All_SKUs_TAB);
                intent.putExtra(KEY_MOBILE_RETAILER_ID, "");
                intent.putExtra(KEY_RETAILER_ID, "");
                intent.putExtra(KEY_ISNEWORREGULAR, "");
                startActivity(intent);

                //Utils.launchActivityWithExtra(DashboardActivity.this, SkuListByGenreActivity.class, intentExtraKey_TabToShow, ConstantsA.All_SKUs_TAB);

            }
        });

        btn_salesSummary.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Utils.launchActivity(DashboardActivity.this, SalesSummaryActivity.class);
            }
        });

        btn_downloadHome.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                downloadData();
            }
        });

        btn_uploadHome.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Utils.launchActivity(DashboardActivity.this, UploadActivity.class);
            }
        });
    }


    //all permissions check
    private void askUserToGrantPermissions()
    {
        if (VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            String[] permissionsArray = new String[]{"android.permission.CAMERA", "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION", "android.permission.READ_PHONE_STATE", "android.permission.WRITE_EXTERNAL_STORAGE"};
            try
            {
                if (ContextCompat.checkSelfPermission(this, "android.permission.CAMERA") != 0 || ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") != 0 || ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") != 0 || ContextCompat.checkSelfPermission(this, "android.permission.READ_PHONE_STATE") != 0 || ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") != 0)
                {
                    ActivityCompat.requestPermissions(this, permissionsArray, 1);
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
    {
        String uf = "upcoming feature";
        switch (menuItem.getItemId())
        {
            case R.id.navItem_NewRetailer:
                Utils.launchActivity(this, CreateRetailerActivity.class);
                break;
            case R.id.navItem_NewSalesOrder:
                Utils.launchActivityWithExtra(this, PickRetailerActivity.class, SHOW_UPDATE_BUTTON, "NO");
                break;
            case R.id.navItem_UpdateRetailer:
                Utils.launchActivityWithExtra(this, PickRetailerActivity.class, SHOW_UPDATE_BUTTON, "YES");
                break;
            case R.id.navItem_logout:
                Editor editor = getSharedPreferences(PREFS_NAME, 0).edit();
                editor.putBoolean("hasLoggedIn", false);
                editor.commit();
                if (Utils.isNetworkConnected(DashboardActivity.this))
                {


                    callLogoutApi(mySharedPrefrencesData.getEmployee_AuthKey(DashboardActivity.this));

                }
                else
                {

                    LoginActivity.is_config_inserted_to_db = true;
                    Utils.launchActivity(this, LoginActivity.class);
                    finish();
                }

                break;
            case R.id.navItem_mySalesHistory:
                Utils.launchActivity(this, MySalesHistoryActivity.class);
                break;

            case R.id.navItem_profile:
                Utils.launchActivity(this, ProfileActivity.class);
                break;
            case R.id.navItem_userManual:
                Utils.launchActivity(this, HelpActivity.class);
                break;
            case R.id.navItem_uploadData:
                Utils.launchActivity(this, UploadActivity.class);
                break;

            case R.id.navItem_downloadData:


                downloadData();

                break;
        }
        this.drawerLayout.closeDrawer((int) GravityCompat.START);
        return true;
    }


    //download data
    private void downloadData()
    {

        if (Utils.isNetworkConnected(DashboardActivity.this))
        {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(DashboardActivity.this);

            // Setting Dialog Title
            alertDialog.setTitle("Confirm Download data...");

            // Setting Dialog Message
            alertDialog.setMessage("Are you sure you want to download data?");

            // Setting Icon to Dialog
            alertDialog.setIcon(R.drawable.ic_check_circle);

            // Setting Positive "Yes" Button
            alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {

                    // Write your code here to invoke YES event

                    //call updated api
                    //for first time passing hardcode date and next pass last updated date
                    networkcall_for_isskulistUpdated();

                }
            });

            // Setting Negative "NO" Button
            alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {
                    // Write your code here to invoke NO event
                    Toast.makeText(DashboardActivity.this, "You clicked on NO", Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                }
            });

            // Showing Alert Message
            alertDialog.show();


        }
        else
        {

            Utils.showErrorDialog(DashboardActivity.this, NO_INTERNET_CONNECTION);
        }
    }


    ///download data api update

    private void networkcall_for_isskulistUpdated()
    {


        final ProgressDialog progressDialog = new ProgressDialog(DashboardActivity.this);
        Utils.startProgressDialog(DashboardActivity.this, progressDialog);

        Apimethods apimethods = (Apimethods) API_Call_Retrofit.getretrofit(DashboardActivity.this).create(Apimethods.class);


        if (this.mySharedPrefrencesData.getfirsttimeflagforAll(getBaseContext()))
        {
            date_str = "2016-05-25";
        }
        else
        {
            date_str = this.mySharedPrefrencesData.getSkulistUpdateDate(getBaseContext());
            // date_str = "2016-05-25";
        }


        IM_GetSkuListAfter IM_getSkuListAfter = new IM_GetSkuListAfter(this.mySharedPrefrencesData.getEmployee_AuthKey(DashboardActivity.this), this.type, date_str);


        //IM_GetSkuListAfter IM_getSkuListAfter = new IM_GetSkuListAfter(this.mySharedPrefrencesData.getEmployee_AuthKey(DashboardActivity.this), this.type, "2018-08-10");
        Call<IsSkuListUpdate> call = apimethods.isSkuListUpdated(IM_getSkuListAfter);
        Log.i("isSkulistUpdate_ip", new Gson().toJson(IM_getSkuListAfter));

        call.enqueue(new Callback<IsSkuListUpdate>()
        {
            public void onResponse(Call<IsSkuListUpdate> call, Response<IsSkuListUpdate> response)
            {
                Log.d("Response", "" + response.code());
                Log.d("respones", "" + response);
                if (response.isSuccessful())
                {
                    IsSkuListUpdate isSkuListUpdate = (IsSkuListUpdate) response.body();
                    Log.i("isSkulistUpdate_op", new Gson().toJson(isSkuListUpdate));
                    if (isSkuListUpdate.getIsUpdated().intValue() == 1)
                    {
                        Utils.dismissProgressDialog(progressDialog);
                        isUpdated = true;

                        //networkcall_for_getSKUlistAfter(mySharedPrefrencesData.getEmployee_AuthKey(DashboardActivity.this), true);
                        networkcall_for_getSKUlistAfterIncrementalData(date_str, mySharedPrefrencesData.getEmployee_AuthKey(DashboardActivity.this), true);
                        return;
                    }
                    return;
                }
                else
                {

                    //mySharedPrefrencesData.setfirsttimeflagforAll(DashboardActivity.this, false);
                    mySharedPrefrencesData.setSkulistUpdateDate(DashboardActivity.this, Utils.getTodayDate());

                    Utils.dismissProgressDialog(progressDialog);

                    //networkcall_for_retailerlist(mySharedPrefrencesData.getEmployee_AuthKey(DashboardActivity.this));


                }

            }

            public void onFailure(Call<IsSkuListUpdate> call, Throwable t)
            {
                Utils.dismissProgressDialog(progressDialog);
                Utils.showToast(DashboardActivity.this, ConstantsA.NO_INTERNET_CONNECTION);
            }
        });

    }


    ///get all incremental and update data api for SKU,RETAILER,LOCATION,CATEGORY,SUBCATEGORY
    private void networkcall_for_getSKUlistAfterIncrementalData(String date_str, String employee_authKey, boolean b)
    {

        final ProgressDialog progressDialog = new ProgressDialog(DashboardActivity.this);
        Utils.startProgressDialog(DashboardActivity.this, progressDialog);
        progressDialog.setCancelable(false);


        final Apimethods methods = (Apimethods) API_Call_Retrofit.getretrofit(DashboardActivity.this)
                .create(Apimethods.class);

        IM_GetSkuListAfter IM_getSkuListAfter = new IM_GetSkuListAfter(employee_authKey, this.type,
                date_str);
        Call<JsonObject> call = methods.getSkuListAfterNew(IM_getSkuListAfter);
        Log.i("GetSkuListAfter_ip_new", new Gson().toJson(IM_getSkuListAfter));
        Log.d("url", "url=" + call.request().url().toString());

        call.enqueue(new Callback<JsonObject>()
        {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response)
            {
                if (response.isSuccessful())
                {


                    mySharedPrefrencesData.setfirsttimeflagforAll(DashboardActivity.this, false);
                    mySharedPrefrencesData.setSkulistUpdateDate(DashboardActivity.this, Utils.getTodayDate());

                    JsonObject jsonObject = response.body();

                    JsonArray jsonArray = jsonObject.getAsJsonArray("Details");

                    //for sku
                    JsonArray jsonArray1 = jsonArray.get(0).getAsJsonArray();

                    for (int i = 0; i < jsonArray1.size(); i++)
                    {

                        JsonObject jsonObject1 = jsonArray1.get(i).getAsJsonObject();

                        skuDataUpdate(jsonObject1, getBaseContext(), sqLiteDatabase);

                    }

                    //for retailers
                    JsonArray jsonArray2 = jsonArray.get(1).getAsJsonArray();

                    for (int i = 0; i < jsonArray2.size(); i++)
                    {

                        JsonObject jsonObject1 = jsonArray2.get(i).getAsJsonObject();

                        retailerDataUpdate(jsonObject1, getBaseContext(), sqLiteDatabase);

                    }

                    //for category
                    JsonArray jsonArray3 = jsonArray.get(2).getAsJsonArray();


                    for (int i = 0; i < jsonArray3.size(); i++)
                    {

                        JsonObject jsonObject1 = jsonArray3.get(i).getAsJsonObject();

                        categoryDataUpdate(jsonObject1, getBaseContext(), sqLiteDatabase);

                    }


                    //for subcategory
                    JsonArray jsonArray4 = jsonArray.get(3).getAsJsonArray();

                    for (int i = 0; i < jsonArray4.size(); i++)
                    {

                        JsonObject jsonObject1 = jsonArray4.get(i).getAsJsonObject();

                        subCategoryDataUpdate(jsonObject1, getBaseContext(), sqLiteDatabase);

                    }

                    //for location
                    JsonArray jsonArray5 = jsonArray.get(4).getAsJsonArray();

                    for (int i = 0; i < jsonArray5.size(); i++)
                    {

                        JsonObject jsonObject1 = jsonArray5.get(i).getAsJsonObject();

                        locationDataUpdate(jsonObject1, getBaseContext(), sqLiteDatabase);

                    }

                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t)
            {
                progressDialog.dismiss();
            }
        });
    }


    private void skuDataUpdate(JsonObject jsonObject1, Context baseContext, SQLiteDatabase sqLiteDatabase)
    {

        GetSkuListAfterNew.SkuId skuId_Info = new GetSkuListAfterNew().new SkuId();

        skuId_Info.setSkuId(jsonObject1.get("sku_id").getAsString());
        skuId_Info.setSkuName(jsonObject1.get("sku_name").getAsString());
        skuId_Info.setSkuPrice(jsonObject1.get("sku_price").getAsString());
        skuId_Info.setSkuDescription(jsonObject1.get("sku_description").getAsString());
        skuId_Info.setSkuCategory(jsonObject1.get("sku_category").getAsString());
        skuId_Info.setSkuSubCategory(jsonObject1.get("sku_sub_category").getAsString());
        skuId_Info.setCategoryDescription(jsonObject1.get("category_description").getAsString());
        skuId_Info.setSubCategoryDescription(jsonObject1.get("sub_category_description").getAsString());
        skuId_Info.setSkuNewFlag(jsonObject1.get("sku_new_flag").getAsString());


        JsonArray jsonArray = jsonObject1.get("sku_media").getAsJsonArray();
        ArrayList<GetSkuListAfterNew.SkuId.SkuMedium> skuMediumArrayList = new ArrayList<>();

        for (int i = 0; i < jsonArray.size(); i++)
        {

            JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();

            GetSkuListAfterNew.SkuId.SkuMedium skuMedium = new GetSkuListAfterNew().new SkuId().new SkuMedium();

            skuMedium.setSkuId(jsonObject.get("sku_id").getAsString());
            skuMedium.setMediaType(jsonObject.get("media_type").getAsString());
            skuMedium.setMediaFile(jsonObject.get("media_file").getAsString());

            skuMediumArrayList.add(skuMedium);
        }


        skuId_Info.setSkuMedia(skuMediumArrayList);

        ContentValues skuValues = new ContentValues();
        skuValues.put(ConstantsA.KEY_SKU_ID, skuId_Info.getSkuId());
        skuValues.put("sku_name", skuId_Info.getSkuName());
        skuValues.put("sku_price", skuId_Info.getSkuPrice());
        skuValues.put("description", skuId_Info.getSkuDescription());
        skuValues.put("sku_category", skuId_Info.getSkuCategory());
        skuValues.put("sku_sub_category", skuId_Info.getSkuSubCategory());
        skuValues.put("sku_category_description", skuId_Info.getCategoryDescription());
        skuValues.put("sku_sub_category_description", skuId_Info.getSubCategoryDescription());
        skuValues.put("new_sku", skuId_Info.getSkuNewFlag());
        skuValues.put("upload_status", Integer.valueOf(1));


        for (GetSkuListAfterNew.SkuId.SkuMedium skuMedia : skuId_Info.getSkuMedia())
        {
            String selection = "sku_id = ?";
            String[] selectionArgs = new String[]{skuMedia.getSkuId()};
            if (skuMedia.getMediaType().equalsIgnoreCase("photo"))
            {

                if (skuMedia.getMediaFile() != null && skuMedia.getMediaFile().length() > 2)
                {


                    String sku_photourl_from_server = skuMedia.getMediaFile();

                    skuValues.put("sku_photo_source", ApiUrl.BASEURL_SKUTHUMBIMAGE + DashboardActivity.substringAfterLastSeparator(sku_photourl_from_server, "/"));


                }
                else
                {

                    skuValues.put("sku_photo_source", NOT_PRESENT);


                }
            }
            else if (skuMedia.getMediaType().equalsIgnoreCase
                    ("catalogue"))
            {

                if (skuMedia.getMediaFile() != null && skuMedia.getMediaFile().length() > 2)
                {

                    String sku_photourl_from_server = skuMedia.getMediaFile();

                    skuValues.put("sku_catalogue_source", ApiUrl.BASEURL_SKUTHUMBIMAGE + DashboardActivity.substringAfterLastSeparator(sku_photourl_from_server, "/"));


                }
                else
                {

                    skuValues.put("sku_catalogue_source", NOT_PRESENT);


                }


            }
            else if (skuMedia.getMediaType().equalsIgnoreCase
                    ("video"))
            {

                if (skuMedia.getMediaFile() != null && skuMedia.getMediaFile().length() > 2)
                {


                    String sku_photourl_from_server = skuMedia.getMediaFile();

                    skuValues.put("sku_video_source", ApiUrl.BASEURL_SKUTHUMBIMAGE + DashboardActivity.substringAfterLastSeparator(sku_photourl_from_server, "/"));


                }
                else
                {

                    skuValues.put("sku_video_source", NOT_PRESENT);


                }

            }
            else
            {

            }
        }

        if (!DbUtils.isSKUPresentInDb(skuId_Info.getSkuId()))
        {

            sqLiteDatabase.insert(Constants.TBL_SKU, null, skuValues);

        }
        else if (DbUtils.isSKUPresentInDb(skuId_Info.getSkuId()))
        {

            delete_sku_row_from_db(skuId_Info.getSkuId());

            sqLiteDatabase.insert(Constants.TBL_SKU, null, skuValues);
        }

    }

    private void retailerDataUpdate(JsonObject jsonObject1, Context baseContext, SQLiteDatabase sqLiteDatabase)
    {


        RetailerInfo_Model.RetailerData retailerData = new RetailerInfo_Model().new RetailerData();

        retailerData.setCustomerCode(jsonObject1.get("customer_code").getAsString());
        if (!jsonObject1.get("customer_geopos").isJsonNull())
        {

            retailerData.setCustomerGeopos(jsonObject1.get("customer_geopos").getAsString());
        }
        else
        {
            retailerData.setCustomerContactDesignation("0,0");
        }


        if (!jsonObject1.get("customer_companyname").isJsonNull())
        {

            retailerData.setCustomerCompanyname(jsonObject1.get("customer_companyname").getAsString());
        }

        if (!jsonObject1.get("customer_type").isJsonNull())
        {

            retailerData.setCustomerType(jsonObject1.get("customer_type").getAsString());

        }

        if (!jsonObject1.get("assigned_distributor").isJsonNull())
        {

            retailerData.setAssignedDistributor(jsonObject1.get("assigned_distributor").getAsString());

        }
        else
        {

            retailerData.setAssignedDistributor("0");
        }

        if (!jsonObject1.get("distributor_name").isJsonNull())
        {

            retailerData.setDistributor_name(jsonObject1.get("distributor_name").getAsString());

        }
        else
        {

            retailerData.setDistributor_name("No Name");
        }


        if (!jsonObject1.get("customer_contact_name").isJsonNull())
        {

            retailerData.setCustomerContactName(jsonObject1.get("customer_contact_name").getAsString());
        }

        if (!jsonObject1.get("contact_cell").isJsonNull())
        {

            retailerData.setContactCell(jsonObject1.get("contact_cell").getAsString());
        }

        if (!jsonObject1.get("contact_email").isJsonNull())
        {

            retailerData.setContactEmail(jsonObject1.get("contact_email").getAsString());
        }

        if (!jsonObject1.get("location_id").isJsonNull())
        {

            retailerData.setLocationId(jsonObject1.get("location_id").getAsString());
        }


        if (!jsonObject1.get("customer_address_1").isJsonNull())
        {

            retailerData.setCustomerAddress1(jsonObject1.get("customer_address_1").getAsString());
        }

        if (!jsonObject1.get("customer_city").isJsonNull())
        {


            retailerData.setCustomerCity(jsonObject1.get("customer_city").getAsString());
        }

        if (!jsonObject1.get("customer_pincode").isJsonNull())
        {


            retailerData.setCustomerPincode(jsonObject1.get("customer_pincode").getAsString());
        }
        if (!jsonObject1.get("customer_region").isJsonNull())
        {


            retailerData.setCustomerRegion(jsonObject1.get("customer_region").getAsString());
        }
        if (!jsonObject1.get("customer_state").isJsonNull())
        {


            retailerData.setCustomerState(jsonObject1.get("customer_state").getAsString());
        }


      /*  retailerData.setDelAddress1(jsonObject1.get("del_address_1").getAsString());
        retailerData.setDelAddressCity(jsonObject1.get("del_address_city").getAsString());
        retailerData.setDelAddressPincode(jsonObject1.get("del_address_pincode").getAsString());
        retailerData.setDelAddressState(jsonObject1.get("del_address_state").getAsString());*/

        retailerData.setCreatedBy(jsonObject1.get("created_by").getAsString());
        retailerData.setCreatedDate(jsonObject1.get("created_date").getAsString());

        if (!jsonObject1.get("modified_by").isJsonNull())
        {

            retailerData.setModifiedBy(jsonObject1.get("modified_by").getAsString());

        }

        if (!jsonObject1.get("modified_date").isJsonNull())
        {


            retailerData.setModifiedDate(jsonObject1.get("modified_date").getAsString());
        }

        if (!jsonObject1.get("customer_picture").isJsonNull())
        {

            retailerData.setCustomerPicture(jsonObject1.get("customer_picture").getAsString());

        }
        else
        {

            retailerData.setCustomerPicture(NOT_PRESENT);
        }


       /* retailerData.setOwnerName(jsonObject1.get("owner_name").getAsString());

        retailerData.setOwnerMobileNumber(jsonObject1.get("owner_mobile_number").getAsString());*/


        retailerData.setCompanyId(jsonObject1.get("company_id").getAsString());


        Log.i("RETAILERINFO", new Gson().toJson(retailerData));


        ContentValues retailerValues = new ContentValues();
        retailerValues.put("retailer_id", retailerData.getCustomerCode());
        retailerValues.put("mobile_retailer_id", "RETAILER_" + getIST() + "_" + retailerData.getCustomerCode());
        retailerValues.put("emp_id", mySharedPrefrencesData.getUser_Id(DashboardActivity.this));
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
        if (retailerData.getAssignedDistributor() != null)
        {

            retailerValues.put("distributor_id", retailerData.getAssignedDistributor());
        }
        else
        {
            retailerValues.put("distributor_id", "0");
        }

        if (retailerData.getDistributor_name() != null)
        {

            retailerValues.put("distributor_name", retailerData.getDistributor_name());
        }
        else
        {
            retailerValues.put("distributor_name", "No Name");
        }


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

        if (retailerData.getCustomerPicture() != null && retailerData.getCustomerPicture().length() > 1)
        {


            retailerValues.put("img_source", retailerData.getCustomerPicture());
        }
        else
        {

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

        if (!DbUtils.isRetailerPresentInDb(retailerData.getCustomerCode()))
        {
            sqLiteDatabase.insert(TBL_RETAILER, null, retailerValues);
            // DbUtils.clear_table(TBL_CONFIG);

        }
        else
        {
            // delete_retailer_row_from_db(retailerData.getCustomerCode());

            retailerValues.remove("mobile_retailer_id");
            sqLiteDatabase.update(TBL_RETAILER, retailerValues, "retailer_id = ?", new String[]{retailerData.getCustomerCode()});
            // DbUtils.clear_table(TBL_CONFIG);
        }


    }

    private void categoryDataUpdate(JsonObject jsonObject1, Context baseContext, SQLiteDatabase sqLiteDatabase)
    {

        GetSkuCategorySubCategorylist.SkuCategory skuCategory = new GetSkuCategorySubCategorylist().new SkuCategory();

        skuCategory.setCategoryId(jsonObject1.get("category_id").getAsString());
        skuCategory.setCategoryName(jsonObject1.get("category_name").getAsString());
        skuCategory.setCategoryDescription(jsonObject1.get("category_description").getAsString());


        ContentValues skuCategoryValues = new ContentValues();
        skuCategoryValues.put("category_id", skuCategory.getCategoryId());
        skuCategoryValues.put("category_name", skuCategory.getCategoryName());
        skuCategoryValues.put("category_description", skuCategory.getCategoryDescription());
        skuCategoryValues.put("upload_status", 1);

        if (!DbUtils.isSKUCategoryPresentInDb(skuCategory.getCategoryId()))
        {

            sqLiteDatabase.insert(TBL_SKU_CATEGORY, null, skuCategoryValues);

        }
        else
        {


            delete_category_row_from_db(skuCategory.getCategoryId());

            sqLiteDatabase.insert(TBL_SKU_CATEGORY, null, skuCategoryValues);
        }


    }

    private void subCategoryDataUpdate(JsonObject jsonObject1, Context baseContext, SQLiteDatabase sqLiteDatabase)
    {

        GetSkuCategorySubCategorylist.SkuCategory.SubCategory skuSubCategory = new GetSkuCategorySubCategorylist().new SkuCategory().new SubCategory();

        skuSubCategory.setSubCategoryId(jsonObject1.get("sub_category_id").getAsString());
        skuSubCategory.setCategoryId(jsonObject1.get("category_id").getAsString());
        skuSubCategory.setSubCategoryName(jsonObject1.get("sub_category_name").getAsString());
        skuSubCategory.setSubCategoryDescription(jsonObject1.get("sub_category_description").getAsString());


        ContentValues skuSubCategoryValues = new ContentValues();
        skuSubCategoryValues.put("sub_category_id", skuSubCategory.getSubCategoryId());
        skuSubCategoryValues.put("category_id", skuSubCategory.getCategoryId());
        skuSubCategoryValues.put("sub_category_name", skuSubCategory.getSubCategoryName());
        skuSubCategoryValues.put("sub_category_description", skuSubCategory.getSubCategoryDescription());
        skuSubCategoryValues.put("created_date", Utils.getTodayDate());
        skuSubCategoryValues.put("upload_status", 1);
        sqLiteDatabase.insert(TBL_SKU_SUBCATEGORY, null, skuSubCategoryValues);

        if (!DbUtils.isSKUSubCategoryPresentInDb(skuSubCategory.getSubCategoryId()))
        {

            sqLiteDatabase.insert(TBL_SKU_SUBCATEGORY, null, skuSubCategoryValues);

        }
        else
        {


            delete_subCategory_row_from_db(skuSubCategory.getSubCategoryId());

            sqLiteDatabase.insert(TBL_SKU_SUBCATEGORY, null, skuSubCategoryValues);
        }

    }

    private void locationDataUpdate(JsonObject jsonObject1, Context baseContext, SQLiteDatabase sqLiteDatabase)
    {
        Location_Model.LocatoinsInfo locatoinsInfo = new Location_Model().new LocatoinsInfo();

        locatoinsInfo.setLocHierId(jsonObject1.get("loc_hier_id").getAsString());
        locatoinsInfo.setName(jsonObject1.get("name").getAsString());
        locatoinsInfo.setHierLevel(jsonObject1.get("hier_level").getAsString());
        locatoinsInfo.setParentId(jsonObject1.get("parent_id").getAsString());
        locatoinsInfo.setFullHier(jsonObject1.get("full_hier").getAsString());

        ContentValues contentValues = new ContentValues();
        contentValues.put("loc_id", locatoinsInfo.getLocHierId());
        contentValues.put("loc_name", locatoinsInfo.getName());
        contentValues.put("hier_level", locatoinsInfo.getHierLevel());
        contentValues.put("full_hier_level", locatoinsInfo.getFullHier());
        contentValues.put("parent_loc_id", locatoinsInfo.getParentId());
        contentValues.put("upload_status", 1);

        if (!DbUtils.isLocationIdDb(locatoinsInfo.getLocHierId()))
        {
            sqLiteDatabase.insert(TBL_LOCATION_HIERARCHY, null, contentValues);
        }
        else
        {

            delete_location_row_from_db(locatoinsInfo.getLocHierId());
            sqLiteDatabase.insert(TBL_LOCATION_HIERARCHY, null, contentValues);

        }

    }


    private void networkcall_for_getSKUlistAfter(final String authToken, final boolean isUpdated)
    {
        final ProgressDialog progressDialog = new ProgressDialog(DashboardActivity.this);
        Utils.startProgressDialog(DashboardActivity.this, progressDialog);
        progressDialog.setCancelable(false);
        String intentExtraKey_TabToShow = getResources().getString(R.string.key_tab_to_show);

        final Apimethods methods = (Apimethods) API_Call_Retrofit.getretrofit(DashboardActivity.this)
                .create(Apimethods.class);
        String date_str = this.mySharedPrefrencesData.getSkulistUpdateDate(DashboardActivity.this);


        IM_GetSkuListAfter IM_getSkuListAfter = new IM_GetSkuListAfter(authToken, this.type,
                date_str);
        Call<GetSkuListAfterNew> call = methods.getSkuListAfter(IM_getSkuListAfter);
        Log.i("GetSkuListAfter_ip_new", new Gson().toJson(IM_getSkuListAfter));
        Log.d("url", "url=" + call.request().url().toString());

        call.enqueue(new Callback<GetSkuListAfterNew>()
        {
            @Override
            public void onResponse(Call<GetSkuListAfterNew> call, Response<GetSkuListAfterNew> response)
            {


                Log.d("Response", "" + response.code());
                Log.d("respones", "" + response);

                if (response.isSuccessful())
                {

                    GetSkuListAfterNew getSkuListAfter = (GetSkuListAfterNew) response.body();
                    Log.i("GetSkuListAfter_op_new", new Gson().toJson(getSkuListAfter));

                    mySharedPrefrencesData.setfirsttimeflagforAll(DashboardActivity.this, false);
                    mySharedPrefrencesData.setSkulistUpdateDate(DashboardActivity.this, Utils.getTodayDate());

                    if (getSkuListAfter.getApiStatus().intValue() == 1)
                    {

                        for (GetSkuListAfterNew.SkuId skuId_Info : getSkuListAfter.getSkuIds())
                        {

                            ContentValues skuValues = new ContentValues();
                            skuValues.put(ConstantsA.KEY_SKU_ID, skuId_Info.getSkuId());
                            skuValues.put("sku_name", skuId_Info.getSkuName());
                            skuValues.put("sku_price", skuId_Info.getSkuPrice());
                            skuValues.put("description", skuId_Info.getSkuDescription());
                            skuValues.put("sku_category", skuId_Info.getSkuCategory());
                            skuValues.put("sku_sub_category", skuId_Info.getSkuSubCategory());
                            skuValues.put("sku_category_description", skuId_Info.getCategoryDescription());
                            skuValues.put("sku_sub_category_description", skuId_Info.getSubCategoryDescription());
                            skuValues.put("new_sku", skuId_Info.getSkuNewFlag());
                            skuValues.put("upload_status", Integer.valueOf(1));


                            for (GetSkuListAfterNew.SkuId.SkuMedium skuMedia : skuId_Info.getSkuMedia())
                            {
                                String selection = "sku_id = ?";
                                String[] selectionArgs = new String[]{skuMedia.getSkuId()};
                                if (skuMedia.getMediaType().equalsIgnoreCase("photo"))
                                {

                                    if (skuMedia.getMediaFile() != null && skuMedia.getMediaFile().length() > 2)
                                    {


                                        String sku_photourl_from_server = skuMedia.getMediaFile();

                                        skuValues.put("sku_photo_source", ApiUrl.BASEURL_SKUTHUMBIMAGE + DashboardActivity.substringAfterLastSeparator(sku_photourl_from_server, "/"));


                                    }
                                    else
                                    {

                                        skuValues.put("sku_photo_source", NOT_PRESENT);


                                    }
                                }
                                else if (skuMedia.getMediaType().equalsIgnoreCase
                                        ("catalogue"))
                                {

                                    if (skuMedia.getMediaFile() != null && skuMedia.getMediaFile().length() > 2)
                                    {

                                        String sku_photourl_from_server = skuMedia.getMediaFile();

                                        skuValues.put("sku_catalogue_source", ApiUrl.BASEURL_SKUTHUMBIMAGE + DashboardActivity.substringAfterLastSeparator(sku_photourl_from_server, "/"));


                                    }
                                    else
                                    {

                                        skuValues.put("sku_catalogue_source", NOT_PRESENT);


                                    }


                                }
                                else if (skuMedia.getMediaType().equalsIgnoreCase
                                        ("video"))
                                {

                                    if (skuMedia.getMediaFile() != null && skuMedia.getMediaFile().length() > 2)
                                    {


                                        String sku_photourl_from_server = skuMedia.getMediaFile();

                                        skuValues.put("sku_video_source", ApiUrl.BASEURL_SKUTHUMBIMAGE + DashboardActivity.substringAfterLastSeparator(sku_photourl_from_server, "/"));


                                    }
                                    else
                                    {

                                        skuValues.put("sku_video_source", NOT_PRESENT);


                                    }

                                }
                                else
                                {

                                }
                            }

                            if (!DbUtils.isSKUPresentInDb(skuId_Info.getSkuId()) && !isUpdated)
                            {

                                sqLiteDatabase.insert(Constants.TBL_SKU, null, skuValues);

                            }
                            else if (DbUtils.isSKUPresentInDb(skuId_Info.getSkuId()) && isUpdated)
                            {

                                delete_sku_row_from_db(skuId_Info.getSkuId());

                                sqLiteDatabase.insert(Constants.TBL_SKU, null, skuValues);
                            }


                        }

                        //networkcall_for_retailerlist(mySharedPrefrencesData.getEmployee_AuthKey(DashboardActivity.this));

                        //Utils.dismissProgressDialog(progressDialog);

                    }


                }

                Utils.dismissProgressDialog(progressDialog);

            }


            @Override
            public void onFailure(Call<GetSkuListAfterNew> call, Throwable t)
            {
                Utils.dismissProgressDialog(progressDialog);
                Utils.showToast(DashboardActivity.this, ConstantsA.NO_INTERNET_CONNECTION);
            }
        });
    }

    private void networkcall_for_retailerlist(final String authToken)
    {

        final ProgressDialog progressDialog = new ProgressDialog(DashboardActivity.this);
        Utils.startProgressDialog(DashboardActivity.this, progressDialog);

        IM_IsValidAuthKey im_isValidAuthKey = new IM_IsValidAuthKey(authToken, "1");

        final Apimethods methods = (Apimethods) API_Call_Retrofit.getretrofit(DashboardActivity.this)
                .create(Apimethods.class);

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

                        a = retailerList_model.getRetailerData().size();

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
                    Utils.dismissProgressDialog(progressDialog);
                    network_call_for_getLocationsinfo(mySharedPrefrencesData.getEmployee_AuthKey(DashboardActivity.this));
                    Utils.showToast(DashboardActivity.this, "No retailer found ");

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

                                ArrayList<RetailerInfo_Model.RetailerData> retailerData = retailerInfo_model.getRetailerData();

                                Log.i("RETAILERINFO", new Gson().toJson(retailerData));

                                if (!DbUtils.isRetailerPresentInDb(retailerData.get(0).getCustomerCode()))
                                {
                                    ContentValues retailerValues = new ContentValues();
                                    retailerValues.put("retailer_id", retailerData.get(0).getCustomerCode());
                                    retailerValues.put("mobile_retailer_id", "RETAILER_" + getIST() + "_" + retailerData.get(0).getCustomerCode());
                                    retailerValues.put("emp_id", mySharedPrefrencesData.getUser_Id(DashboardActivity.this));
                                    retailerValues.put("retailer_name", retailerData
                                            .get(0).getCustomerCompanyname());
                                    retailerValues.put("shop_name", retailerData
                                            .get(0).getCustomerContactName());
                                    retailerValues.put("shop_address", retailerData
                                            .get(0).getCustomerAddress1());
                                    retailerValues.put("pincode", retailerData
                                            .get(0).getCustomerPincode());
                                    retailerValues.put("mobile_no", retailerData
                                            .get(0).getContactCell());
                                    retailerValues.put("email", retailerData.get(0).getContactEmail());
                                    retailerValues.put("area_id", retailerData.get(0).getLocationId());
                                    if (retailerData.get(0).getAssignedDistributor() != null)
                                    {

                                        retailerValues.put("distributor_id", retailerData.get(0).getAssignedDistributor());
                                    }
                                    else
                                    {
                                        retailerValues.put("distributor_id", "0");
                                    }

                                    if (retailerData.get(0).getAssignedDistributor() != null)
                                    {

                                        retailerValues.put("distributor_name", retailerData.get(1).getDistributor_name());
                                    }
                                    else
                                    {
                                        retailerValues.put("distributor_name", "No Name");
                                    }


                                    String latlong = retailerData.get(0).getCustomerGeopos();

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

                                    if (retailerData.get(0).getCustomerPicture() != null && retailerData.get(0).getCustomerPicture().length() > 1)
                                    {


                                        retailerValues.put("img_source", retailerData.get(0).getCustomerPicture());
                                    }
                                    else
                                    {

                                        retailerValues.put("img_source", NOT_PRESENT);
                                    }

                                    retailerValues.put("created_date", retailerData
                                            .get(0).getCreatedDate());
                                    retailerValues.put("modified_date", retailerData
                                            .get(0).getModifiedDate());
                                    retailerValues.put("landmark", retailerData
                                            .get(0).getCustomerCompanyname());
                                    retailerValues.put("area", retailerData
                                            .get(0).getCustomerAddress1());
                                    retailerValues.put("taluk", retailerData
                                            .get(0).getCustomerAddress1());
                                    retailerValues.put("district", retailerData
                                            .get(0).getCustomerCity());
                                    retailerValues.put("state", retailerData.get(0).getCustomerState
                                            ());
                                    retailerValues.put("upload_status", 1);

                                    sqLiteDatabase.insert(TBL_RETAILER, null, retailerValues);
                                    // DbUtils.clear_table(TBL_CONFIG);

                                    b++;

                                    if (a == b)
                                    {

                                        Utils.dismissProgressDialog(progressDialog);
                                        network_call_for_getLocationsinfo(mySharedPrefrencesData.getEmployee_AuthKey(DashboardActivity.this));

                                    }

                                }
                                else
                                {
                                    a--;

                                    if (a == b)
                                    {

                                        Utils.dismissProgressDialog(progressDialog);
                                        network_call_for_getLocationsinfo(mySharedPrefrencesData.getEmployee_AuthKey(DashboardActivity.this));

                                    }
                                }
                            }

                        }
                        else
                        {
                            dismissProgressDialog(progressDialog);
                            Utils.showToast(DashboardActivity.this, "Unsuccessful api call " + "for retailerinfo ");
                        }
                    }

                    @Override
                    public void onFailure(Call<RetailerInfo_Model> call, Throwable t)
                    {
                        dismissProgressDialog(progressDialog);
                        Utils.showToast(DashboardActivity.this, NO_INTERNET_CONNECTION);

                    }
                });
            }

            @Override
            public void onFailure(Call<RetailerList_Model> call, Throwable t)
            {
                dismissProgressDialog(progressDialog);
                Utils.showToast(DashboardActivity.this, NO_INTERNET_CONNECTION);
            }
        });
    }

    private void network_call_for_getLocationsinfo(final String employee_authKey)
    {

        final ProgressDialog progressDialog = new ProgressDialog(DashboardActivity.this);
        Utils.startProgressDialog(DashboardActivity.this, progressDialog);

        Apimethods methods = API_Call_Retrofit.getretrofit(DashboardActivity.this).create(Apimethods.class);

        IM_IsValidAuthKey authkey = new IM_IsValidAuthKey(employee_authKey, "1");

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


                            if (!DbUtils.isLocationIdDb(locatoinsInfo.getLocHierId()))
                            {

                                ContentValues contentValues = new ContentValues();
                                contentValues.put("loc_id", locatoinsInfo.getLocHierId());
                                contentValues.put("loc_name", locatoinsInfo.getName());
                                contentValues.put("hier_level", locatoinsInfo.getHierLevel());
                                contentValues.put("full_hier_level", locatoinsInfo.getFullHier());
                                contentValues.put("parent_loc_id", locatoinsInfo.getParentId());
                                contentValues.put("upload_status", 1);
                                sqLiteDatabase.insert(TBL_LOCATION_HIERARCHY, null, contentValues);
                            }


                        }
                        dismissProgressDialog(progressDialog);

                    }
                }
                else
                {

                    dismissProgressDialog(progressDialog);
                }
            }


            @Override
            public void onFailure(Call<Location_Model> call, Throwable t)
            {
                dismissProgressDialog(progressDialog);
                Utils.showToast(DashboardActivity.this, ConstantsA.NO_INTERNET_CONNECTION);
            }
        });

    }


    //delete SKU from db
    private void delete_sku_row_from_db(String sku_id)
    {
        String selection = "sku_id = ?";
        String[] selectionArgs = new String[]{sku_id};
        this.sqLiteDatabase.delete(Constants.TBL_SKU, selection, selectionArgs);
    }


    //delete category from db

    private void delete_category_row_from_db(String categoryId)
    {

        String selection = "category_id = ?";
        String[] selectionArgs = new String[]{categoryId};
        this.sqLiteDatabase.delete(Constants.TBL_SKU_CATEGORY, selection, selectionArgs);
    }

    //delete subcategory from db
    private void delete_subCategory_row_from_db(String categoryId)
    {

        String selection = "sub_category_id = ?";
        String[] selectionArgs = new String[]{categoryId};
        this.sqLiteDatabase.delete(Constants.TBL_SKU_SUBCATEGORY, selection, selectionArgs);
    }

    //delete location from db
    private void delete_location_row_from_db(String locationId)
    {
        String selection = "loc_id = ?";
        String[] selectionArgs = new String[]{locationId};
        this.sqLiteDatabase.delete(Constants.TBL_LOCATION_HIERARCHY, selection, selectionArgs);
    }


    //display color stripes for visit on home
    void setColorsDependingOnRetailerVisits()
    {
        int mandatory_visits_per_day = getMandatoryVisitfortheDay_db();
        String color1 = null;
        String color2 = null;
        String color3 = null;
        String color4 = null;
        String colorStripQuery = "SELECT config_for,config_value from " + Constants.TBL_CONFIG;
        String[] selectionArgs = new String[]{"< 33%", ">33% - <66%", ">66% - 99%", "> 100%"};
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(MyDb.openDatabase(dbFileFullPath));
        Cursor cursor = sqLiteDatabase.rawQuery(colorStripQuery, null);
        while (cursor.moveToNext())
        {
            if (cursor.getString(cursor.getColumnIndexOrThrow("config_for")).equals(selectionArgs[0]))
            {
                color1 = cursor.getString(cursor.getColumnIndexOrThrow("config_value"));
            }
            else if (cursor.getString(cursor.getColumnIndexOrThrow("config_for")).equals(selectionArgs[1]))
            {
                color2 = cursor.getString(cursor.getColumnIndexOrThrow("config_value"));
            }
            else if (cursor.getString(cursor.getColumnIndexOrThrow("config_for")).equals(selectionArgs[2]))
            {
                color3 = cursor.getString(cursor.getColumnIndexOrThrow("config_value"));
            }
            else if (cursor.getString(cursor.getColumnIndexOrThrow("config_for")).equals(selectionArgs[3]))
            {
                color4 = cursor.getString(cursor.getColumnIndexOrThrow("config_value"));
            }
        }
        cursor.close();
        sqLiteDatabase.close();
        Log.e("colorstrip", color1 + " " + color2 + " " + color3 + " " + color4);
        if (((double) this.noOfRetailerVisits) <= 0.33d * ((double) mandatory_visits_per_day))
        {
            this.retailerVisits_ProgressBar.getProgressDrawable().setColorFilter(Color.parseColor(color1), Mode.SRC_IN);
            this.noOfRetailerVisits_TextView.setTextColor(Color.parseColor(color1));
        }
        else if (((double) this.noOfRetailerVisits) >= 0.33d * ((double) mandatory_visits_per_day) && ((double) this.noOfRetailerVisits) <= 0.66d * ((double) mandatory_visits_per_day))
        {
            this.retailerVisits_ProgressBar.getProgressDrawable().setColorFilter(Color.parseColor(color2), Mode.SRC_IN);
            this.noOfRetailerVisits_TextView.setTextColor(Color.parseColor(color2));
        }
        else if (((double) this.noOfRetailerVisits) < 0.66d * ((double) mandatory_visits_per_day) || ((double) this.noOfRetailerVisits) > 0.99d * ((double) mandatory_visits_per_day))
        {
            this.retailerVisits_ProgressBar.getProgressDrawable().setColorFilter(Color.parseColor(color4), Mode.SRC_IN);
            this.noOfRetailerVisits_TextView.setTextColor(Color.parseColor(color4));
        }
        else
        {
            this.retailerVisits_ProgressBar.getProgressDrawable().setColorFilter(Color.parseColor(color3), Mode.SRC_IN);
            this.noOfRetailerVisits_TextView.setTextColor(Color.parseColor(color3));
        }
    }

    void updateIMEI(String salesPersonID)
    {
        String imeiNumber = null;

        if (ActivityCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)
        {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            imeiNumber = telephonyManager.getDeviceId();
        }

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String selection = "emp_id = ?";
        String[] selectionArgs = {salesPersonID};

        ContentValues contentValues = new ContentValues();
        contentValues.put("imei", imeiNumber);
        sqLiteDatabase.update(TBL_EMPLOYEE, contentValues, selection, selectionArgs);
        sqLiteDatabase.close();
    }


    //display congratulatory message for retailer visit
    void showCongratulatoryMessage(int noOfRetailerVisits)
    {
        wasShown = true;
        Builder builder = new Builder(this);
        builder.setTitle((CharSequence) "Congratulations!");
        builder.setMessage("You have completed " + noOfRetailerVisits + " or more retailer visits today.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
            }
        });
        builder.show();
    }


    //display congratulatory message for retailer creation
    void showCongratulatoryMessage_Retailecreation(int no_of_retailer)
    {
        wasShown_month = true;
        String checkboxitem = " Don't show again." + "You have created " + no_of_retailer + " or more retailer in this month.";
        final CharSequence[] items = {checkboxitem};
        Builder builder = new Builder(this);

        if (no_of_retailer > 4)
        {

            builder.setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked)
                {
                    String checkBoxResult = "NOT checked";
                    if (isChecked)
                    {
                        // If the user checked the item, add it to the selected items

                        checkBoxResult = "checked";

                        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                        SharedPreferences.Editor editor = settings.edit();

                        editor.putString("skipMessage" + mySharedPrefrencesData.getUser_Id(getBaseContext()), checkBoxResult);
                        editor.commit();


                    }
                    else
                    {
                        checkBoxResult = "NOT checked";

                        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                        SharedPreferences.Editor editor = settings.edit();

                        editor.putString("skipMessage" + mySharedPrefrencesData.getUser_Id(getBaseContext()), checkBoxResult);
                        editor.commit();
                    }
                }
            });
        }
        else
        {

            builder.setMessage("You have created " + no_of_retailer + " or more retailer in this month.");

        }
        builder.setTitle((CharSequence) "Congratulations!");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
            }
        });

        builder.show();
    }

    //buzz congratulatory message for retailer visit
    void playJingle()
    {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try
        {
            mediaPlayer.setDataSource(Constants.appSpecificDirectoryPath + File.separator + Constants.JINGLE_FILE_NAME);
            mediaPlayer.prepare();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        mediaPlayer.start();
    }


    //api call for logout
    private void callLogoutApi(String employee_authKey)
    {


        final ProgressDialog progressDialog = new ProgressDialog(DashboardActivity.this);
        Utils.startProgressDialog(DashboardActivity.this, progressDialog);

        final Apimethods methods = API_Call_Retrofit.getretrofit(this).create(Apimethods.class);

        IM_IsValidAuthKey IM_isValidAuthKey = new IM_IsValidAuthKey(mySharedPrefrencesData.getEmployee_AuthKey(DashboardActivity.this), mySharedPrefrencesData.get_User_CompanyId(DashboardActivity.this));


        Call<ValidAuthModel> call = methods.setLogout(IM_isValidAuthKey);


        Log.i("isValidAuthkey_ip", new Gson().toJson(IM_isValidAuthKey));

        Log.d("url", "url=" + call.request().url().toString());

        call.enqueue(new Callback<ValidAuthModel>()
        {
            @Override
            public void onResponse(Call<ValidAuthModel> call, Response<ValidAuthModel> response)
            {
                Utils.dismissProgressDialog(progressDialog);
                if (response.isSuccessful())
                {
                    Utils.showToast(DashboardActivity.this, "Logged out successfully.");
                    LoginActivity.is_config_inserted_to_db = true;
                    Utils.launchActivity(DashboardActivity.this, LoginActivity.class);
                    finish();
                }
                else
                {

                    Utils.showErrorDialog(DashboardActivity.this, "You are currently unable to logout.");
                }


            }

            @Override
            public void onFailure(Call<ValidAuthModel> call, Throwable t)
            {
                Utils.dismissProgressDialog(progressDialog);
                Utils.showToast(DashboardActivity.this, ConstantsA.NO_INTERNET_CONNECTION);
            }
        });
    }

    //api call for ISValidAuthKey
    private void networkcall_for_ISValidAuthKey()
    {

        final ProgressDialog progressDialog = new ProgressDialog(DashboardActivity.this);
        Utils.startProgressDialog(DashboardActivity.this, progressDialog);

        final Apimethods methods = API_Call_Retrofit.getretrofit(this).create(Apimethods.class);

        IM_IsValidAuthKey IM_isValidAuthKey = new IM_IsValidAuthKey(mySharedPrefrencesData.getEmployee_AuthKey(DashboardActivity.this), mySharedPrefrencesData.get_User_CompanyId(DashboardActivity.this));


        Call<ValidAuthModel> call = methods.isValidAuthKey(IM_isValidAuthKey);


        Log.i("isValidAuthkey_ip", new Gson().toJson(IM_isValidAuthKey));

        Log.d("url", "url=" + call.request().url().toString());

        call.enqueue(new Callback<ValidAuthModel>()
        {
            @Override
            public void onResponse(Call<ValidAuthModel> call, Response<ValidAuthModel> response)
            {
                int statusCode = response.code();
                Log.d("Response", "" + statusCode);
                Log.d("respones", "" + response);
                if (response.isSuccessful())
                {
                    ValidAuthModel validAuthModel = response.body();

                    Log.i("isValidAuthkey_op", new Gson().toJson(validAuthModel));

                    if (validAuthModel.getApi_status() == 1)
                    {
                       /* mySharedPrefrencesData.setUsername(DashboardActivity.this, usernameInSharedPreferences);
                        mySharedPrefrencesData.setUser_pwd(DashboardActivity.this, passwordInSharedPreferences);*/
                        //  Utils.showToast(DashboardActivity.this, "valid auth key ");
                        Log.e("authkey", mySharedPrefrencesData.getEmployee_AuthKey(DashboardActivity.this));
                        isValidAuthkey = true;
                        Utils.dismissProgressDialog(progressDialog);
                      /*  if (mySharedPrefrencesData.getApiCallForCategoryListOnce(DashboardActivity.this))
                        {
                            DbUtils.clear_table(TBL_SKU_CATEGORY);
                            DbUtils.clear_table(TBL_SKU_SUBCATEGORY);
                            networkcall_for_categorySubcategoryList();
                        }*/

                    }
                }
                else
                {
                    Utils.dismissProgressDialog(progressDialog);

                   /* mySharedPrefrencesData.setUsername(DashboardActivity.this,"");
                    mySharedPrefrencesData.setUser_pwd(DashboardActivity.this,"");
                    LoginActivity.is_config_inserted_to_db = true;
                    Utils.launchActivity(DashboardActivity.this, LoginActivity.class);
                    finish();*/
                    networkcall_for_authtoken();
                    // Utils.showToast(DashboardActivity.this, "Unsuccessful api call for isvalid auth key ");
                }

            }

            private void networkcall_for_categorySubcategoryList()
            {
                final ProgressDialog progressDialog = new ProgressDialog(DashboardActivity.this);
                Utils.startProgressDialog(DashboardActivity.this, progressDialog);

                final Apimethods methods = API_Call_Retrofit.getretrofit(DashboardActivity.this).create(Apimethods.class);

                IM_IsValidAuthKey IM_isValidAuthKey = new IM_IsValidAuthKey(mySharedPrefrencesData.getEmployee_AuthKey(DashboardActivity.this), mySharedPrefrencesData.get_User_CompanyId(DashboardActivity.this));


                Call<GetSkuCategorySubCategorylist> call = methods.getSkuCategorySubCategorylist(IM_isValidAuthKey);


                Log.i("getCategorylist_ip", new Gson().toJson(IM_isValidAuthKey));

                Log.d("url", "url=" + call.request().url().toString());

                call.enqueue(new Callback<GetSkuCategorySubCategorylist>()
                {
                    @Override
                    public void onResponse(Call<GetSkuCategorySubCategorylist> call, Response<GetSkuCategorySubCategorylist> response)
                    {

                        if (response.isSuccessful())
                        {

                            mySharedPrefrencesData.setApiCallForCategoryListOnce(DashboardActivity.this, false);
                            GetSkuCategorySubCategorylist catSubcatlist = response.body();

                            int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
                            SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

                            for (int i = 0; i < catSubcatlist.getSkuCategories().size(); i++)
                            {


                                GetSkuCategorySubCategorylist.SkuCategory skuCategory = catSubcatlist.getSkuCategories().get(i);


                                ContentValues skuCategoryValues = new ContentValues();
                                skuCategoryValues.put("category_id", skuCategory.getCategoryId());
                                skuCategoryValues.put("category_name", skuCategory.getCategoryName());
                                skuCategoryValues.put("category_description", skuCategory.getCategoryDescription());
                                skuCategoryValues.put("created_date", Utils.getTodayDate());
                                skuCategoryValues.put("upload_status", 1);
                                sqLiteDatabase.insert(TBL_SKU_CATEGORY, null, skuCategoryValues);

                                for (int j = 0; j < skuCategory.getSubCategories().size(); j++)
                                {

                                    GetSkuCategorySubCategorylist.SkuCategory.SubCategory skuSubCategory = skuCategory.getSubCategories().get(j);


                                    ContentValues skuSubCategoryValues = new ContentValues();
                                    skuSubCategoryValues.put("sub_category_id", skuSubCategory.getSubCategoryId());
                                    skuSubCategoryValues.put("category_id", skuCategory.getCategoryId());
                                    skuSubCategoryValues.put("sub_category_name", skuSubCategory.getSubCategoryName());
                                    skuSubCategoryValues.put("sub_category_description", skuSubCategory.getSubCategoryDescription());
                                    skuSubCategoryValues.put("created_date", Utils.getTodayDate());
                                    skuSubCategoryValues.put("upload_status", 1);
                                    sqLiteDatabase.insert(TBL_SKU_SUBCATEGORY, null, skuSubCategoryValues);
                                }


                            }

                            sqLiteDatabase.close();
                            Utils.dismissProgressDialog(progressDialog);
                        }
                        else
                        {
                            Utils.dismissProgressDialog(progressDialog);
                            Utils.showErrorDialog(DashboardActivity.this, "Unsuccessful api call for getSkuCategorySubcategoryList");
                        }


                    }

                    @Override
                    public void onFailure(Call<GetSkuCategorySubCategorylist> call, Throwable t)
                    {
                        Utils.dismissProgressDialog(progressDialog);
                        Utils.showToast(DashboardActivity.this, ConstantsA.NO_INTERNET_CONNECTION);
                    }
                });
            }

            private void networkcall_for_authtoken()
            {
                IM_Login IMLogin = new IM_Login(Utils.getDeviceId(DashboardActivity.this), new MySharedPrefrencesData().getUsername(DashboardActivity.this), new MySharedPrefrencesData().getUser_pwd(DashboardActivity.this), "1");
                Call<AuthUser_Model> call = methods.getUserAuthKey(IMLogin);
                //Call<AuthUser_Model> call = methods.getUserAuthKey(Utils.getDeviceId(DashboardActivity.this),"1",new MySharedPrefrencesData().getUser_pwd(DashboardActivity.this),new MySharedPrefrencesData().getUsername(DashboardActivity.this));
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
                            if (authUser_model.getApiStatus() == 1)
                            {
                                Utils.showToast(DashboardActivity.this, "succssful api call for auth token");
                                mySharedPrefrencesData.setEmployee_AuthKey(DashboardActivity.this, authUser_model.getAuthToken());
                                mySharedPrefrencesData.setAuthTokenExpiryDate(DashboardActivity.this, authUser_model.getAuthTokenExpiryDate());
                                isValidAuthkey = true;
                                Utils.dismissProgressDialog(progressDialog);
                             /*   if (mySharedPrefrencesData.getApiCallForCategoryListOnce(DashboardActivity.this))
                                {
                                    DbUtils.clear_table(TBL_SKU_CATEGORY);
                                    DbUtils.clear_table(TBL_SKU_SUBCATEGORY);
                                    networkcall_for_categorySubcategoryList();
                                }*/
                                //   networkcall_for_getSKUlistAfter(authUser_model.getAuthToken());
                            }
                        }
                        else
                        {
                            Utils.dismissProgressDialog(progressDialog);

                        }

                    }

                    @Override
                    public void onFailure(Call<AuthUser_Model> call, Throwable t)
                    {
                        Utils.dismissProgressDialog(progressDialog);
                        Utils.showToast(DashboardActivity.this, ConstantsA.NO_INTERNET_CONNECTION);

                    }
                });

            }

            @Override
            public void onFailure(Call<ValidAuthModel> call, Throwable t)
            {
                Utils.dismissProgressDialog(progressDialog);
                Utils.showToast(DashboardActivity.this, ConstantsA.NO_INTERNET_CONNECTION);

            }
        });


    }


}
