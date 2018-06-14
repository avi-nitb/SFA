package in.etaminepgg.sfa.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.media.MediaPlayer;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import in.etaminepgg.sfa.BuildConfig;
import in.etaminepgg.sfa.Fragments.AllSKUsFragment;
import in.etaminepgg.sfa.InputModel_For_Network.IM_GetSkuListAfter;
import in.etaminepgg.sfa.InputModel_For_Network.IM_IsValidAuthKey;
import in.etaminepgg.sfa.InputModel_For_Network.IM_Login;
import in.etaminepgg.sfa.Models.AuthUser_Model;
import in.etaminepgg.sfa.Models.GetSkuCategorySubCategorylist;
import in.etaminepgg.sfa.Models.GetSkuListAfterNew;
import in.etaminepgg.sfa.Models.IsSkuListUpdate;
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
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SKU_CATEGORY;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SKU_SUBCATEGORY;
import static in.etaminepgg.sfa.Utilities.Constants.dbFileFullPath;
import static in.etaminepgg.sfa.Utilities.ConstantsA.NOT_PRESENT;
import static in.etaminepgg.sfa.Utilities.ConstantsA.REGULAR_ORDER;

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

    boolean isUpdated = false;


    /*ImageView allProducts_TextView;
    ImageView newProducts_TextView;
    ImageView promotionalProducts_TextView;
    ImageView regularlyOrderedProducts_TextView;*/


    LinearLayout ll_newpro, ll_allpro, ll_promopro, ll_frequentpro;

    LinearLayout retailerVisits_LinearLayout;
    ProgressBar retailerVisits_ProgressBar;
    SQLiteDatabase sqLiteDatabase;
    Toolbar toolbar;
    String type;
    String usernameInSharedPreferences;
    int valueFromOpenDatabase;

    String intentExtraKey_selectedOrderType, activeorderid;


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
        setContentView(R.layout.activity_dashboard_new);

        float density = getResources().getDisplayMetrics().density;

        Log.d("density", "" + density);

        intentExtraKey_selectedOrderType = getResources().getString(R.string.key_selected_order_type);

        activeorderid = DbUtils.getActiveOrderID();

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
        networkcall_for_ISValidAuthKey();
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

        activeorderid = DbUtils.getActiveOrderID();

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
            showCongratulatoryMessage_Retailecreation(mandatory_retailer_per_month);
        }
    }

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
       /* this.newProducts_TextView = (ImageView) findViewById(R.id.newProducts_TextView);
        this.promotionalProducts_TextView = (ImageView) findViewById(R.id.promotionalProducts_TextView);
        this.regularlyOrderedProducts_TextView = (ImageView) findViewById(R.id.regularlyOrderedProducts_TextView);
        this.allProducts_TextView = (ImageView) findViewById(R.id.allProducts_TextView);*/


        this.ll_allpro = (LinearLayout) findViewById(R.id.ll_allpro);
        this.ll_frequentpro = (LinearLayout) findViewById(R.id.ll_frequentpro);
        this.ll_newpro = (LinearLayout) findViewById(R.id.ll_newpro);
        this.ll_promopro = (LinearLayout) findViewById(R.id.ll_promopro);
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


                if (activeorderid.equalsIgnoreCase(ConstantsA.NONE))
                {

                    Utils.launchActivityWithExtra(DashboardActivity.this, PickRetailerActivity.class,Constants.SHOW_UPDATE_BUTTON,"NO");
                }
                else
                {


                    Utils.launchActivityWithExtra(DashboardActivity.this, SkuListByGenreActivity.class, intentExtraKey_selectedOrderType, REGULAR_ORDER);
                }


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

                if (activeorderid.equalsIgnoreCase(ConstantsA.NONE))
                {

                    Utils.launchActivityWithExtra(DashboardActivity.this, PickRetailerActivity.class,Constants.SHOW_UPDATE_BUTTON,"NO");
                    //Utils.launchActivityWithExtra(DashboardActivity.this, SkuListByGenreActivity.class, intentExtraKey_TabToShow, ConstantsA.FREQUENT_SKUs_TAB);

                }
                else
                {


                    Utils.launchActivityWithExtra(DashboardActivity.this, SkuListByGenreActivity.class, intentExtraKey_selectedOrderType, REGULAR_ORDER);
                }


            }
        });
        this.ll_allpro.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {

                if (activeorderid.equalsIgnoreCase(ConstantsA.NONE))
                {


                    Utils.launchActivityWithExtra(DashboardActivity.this, PickRetailerActivity.class,Constants.SHOW_UPDATE_BUTTON,"NO");

                }
                else
                {


                    Utils.launchActivityWithExtra(DashboardActivity.this, SkuListByGenreActivity.class, intentExtraKey_selectedOrderType, REGULAR_ORDER);
                }

            }
        });
    }


    private void askUserToGrantPermissions()
    {
        if (VERSION.SDK_INT >= 23)
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
                Editor editor = getSharedPreferences(MySharedPrefrencesData.PREFS_NAME, 0).edit();
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

            case R.id.navItem_downloadData:

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
                    alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int which) {

                            // Write your code here to invoke YES event
                            networkcall_for_isskulistUpdated();

                        }
                    });

                    // Setting Negative "NO" Button
                    alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to invoke NO event
                            Toast.makeText(DashboardActivity.this, "You clicked on NO", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                        }
                    });

                    // Showing Alert Message
                    alertDialog.show();




                }

                break;
        }
        this.drawerLayout.closeDrawer((int) GravityCompat.START);
        return true;
    }


    ///sku update

    private void networkcall_for_isskulistUpdated()
    {


        final ProgressDialog progressDialog = new ProgressDialog(DashboardActivity.this);
        Utils.startProgressDialog(DashboardActivity.this, progressDialog);

        Apimethods apimethods = (Apimethods) API_Call_Retrofit.getretrofit(DashboardActivity.this).create(Apimethods.class);
        IM_GetSkuListAfter IM_getSkuListAfter = new IM_GetSkuListAfter(this.mySharedPrefrencesData.getEmployee_AuthKey(DashboardActivity.this), this.type, this.mySharedPrefrencesData.getSkulistUpdateDate(DashboardActivity.this));
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
                        networkcall_for_getSKUlistAfter(mySharedPrefrencesData.getEmployee_AuthKey(DashboardActivity.this),true);
                        return;
                    }
                    return;
                }
                Utils.dismissProgressDialog(progressDialog);

            }

            public void onFailure(Call<IsSkuListUpdate> call, Throwable t)
            {
                Utils.dismissProgressDialog(progressDialog);
                Utils.showToast(DashboardActivity.this, ConstantsA.NO_INTERNET_CONNECTION);
            }
        });

    }


    private void networkcall_for_getSKUlistAfter(final String authToken, final boolean isUpdated)
    {
        final ProgressDialog progressDialog = new ProgressDialog(DashboardActivity.this);
        Utils.startProgressDialog(DashboardActivity.this, progressDialog);
        progressDialog.setCancelable(false);
        String intentExtraKey_TabToShow = getResources().getString(R.string.key_tab_to_show);
        final Apimethods methods = (Apimethods) API_Call_Retrofit.getretrofit(DashboardActivity.this)
                .create(Apimethods.class);
        String date_str = "";
        if (this.mySharedPrefrencesData.getfirsttimeflagforAll(DashboardActivity.this))
        {
            date_str = "2016-05-25";
        }
        else
        {
            date_str = this.mySharedPrefrencesData.getSkulistUpdateDate(DashboardActivity.this);
        }
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

                    mySharedPrefrencesData.setfirsttimeflagforAll(DashboardActivity.this,false);

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

                            if(!DbUtils.isSKUPresentInDb(skuId_Info.getSkuId()) && !isUpdated){

                                sqLiteDatabase.insert(Constants.TBL_SKU, null, skuValues);

                            }else if(DbUtils.isSKUPresentInDb(skuId_Info.getSkuId()) && isUpdated){

                                delete_sku_row_from_db(skuId_Info.getSkuId());

                                sqLiteDatabase.insert(Constants.TBL_SKU, null, skuValues);
                            }


                        }

                        Utils.dismissProgressDialog(progressDialog);

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

    private void delete_sku_row_from_db(String sku_id) {
        String selection = "sku_id = ?";
        String[] selectionArgs = new String[]{sku_id};
        this.sqLiteDatabase.delete(Constants.TBL_SKU, selection, selectionArgs);
    }




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

    void showCongratulatoryMessage_Retailecreation(int no_of_retailer)
    {
        wasShown_month = true;
        Builder builder = new Builder(this);
        builder.setTitle((CharSequence) "Congratulations!");
        builder.setMessage("You have created " + no_of_retailer + " or more retailer in this month.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
            }
        });
        builder.show();
    }

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
                        if (mySharedPrefrencesData.getApiCallForCategoryListOnce(DashboardActivity.this))
                        {
                            DbUtils.clear_table(TBL_SKU_CATEGORY);
                            DbUtils.clear_table(TBL_SKU_SUBCATEGORY);
                            networkcall_for_categorySubcategoryList();
                        }

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

                            for (int i = 0; i < catSubcatlist.getSkuCategories().size(); i++)
                            {


                                GetSkuCategorySubCategorylist.SkuCategory skuCategory = catSubcatlist.getSkuCategories().get(i);


                                int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
                                SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);


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
                                if (mySharedPrefrencesData.getApiCallForCategoryListOnce(DashboardActivity.this))
                                {
                                    DbUtils.clear_table(TBL_SKU_CATEGORY);
                                    DbUtils.clear_table(TBL_SKU_SUBCATEGORY);
                                    networkcall_for_categorySubcategoryList();
                                }
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
