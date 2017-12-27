package in.etaminepgg.sfa.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

import in.etaminepgg.sfa.InputModel_For_Network.IM_GetSkuInfo;
import in.etaminepgg.sfa.InputModel_For_Network.IM_GetSkuListAfter;
import in.etaminepgg.sfa.InputModel_For_Network.IM_IsValidAuthKey;
import in.etaminepgg.sfa.InputModel_For_Network.IM_Login;
import in.etaminepgg.sfa.Models.AuthUser_Model;
import in.etaminepgg.sfa.Models.GetSkuAttribute;
import in.etaminepgg.sfa.Models.GetSkuInfo;
import in.etaminepgg.sfa.Models.GetSkuListAfter;
import in.etaminepgg.sfa.Models.GetSkuThumbImage;
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

import static in.etaminepgg.sfa.Activities.LoginActivity.KEY_PASSWORD;
import static in.etaminepgg.sfa.Activities.LoginActivity.KEY_USERNAME;
import static in.etaminepgg.sfa.Activities.LoginActivity.is_config_inserted_to_db;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_EMPLOYEE;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_GLOBAL_ATTRIBUTES;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SKU;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SKU_ATTRIBUTE_MAPPING;
import static in.etaminepgg.sfa.Utilities.Constants.dbFileFullPath;
import static in.etaminepgg.sfa.Utilities.ConstantsA.All_SKUs_TAB;
import static in.etaminepgg.sfa.Utilities.ConstantsA.FREQUENT_SKUs_TAB;
import static in.etaminepgg.sfa.Utilities.ConstantsA.NEW_SKUs_TAB;
import static in.etaminepgg.sfa.Utilities.ConstantsA.PROMO_SKUs_TAB;
import static in.etaminepgg.sfa.Utilities.MyDb.isEmpty;
import static in.etaminepgg.sfa.Utilities.SharedPreferenceSingleton.MY_PREF;
import static in.etaminepgg.sfa.Utilities.Utils.isNetworkConnected;
import static in.etaminepgg.sfa.Utilities.Utils.loggedInUserID;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    FrameLayout promoSchemes_FrameLayout;
    ImageView promoSchemesBell_ImageView;
    ProgressBar retailerVisits_ProgressBar;
    LinearLayout retailerVisits_LinearLayout;
    TextView noOfPromoSchemes_TextView, noOfRetailerVisits_TextView, newProducts_TextView,
            promotionalProducts_TextView, regularlyOrderedProducts_TextView, allProducts_TextView;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    int valueFromOpenDatabase;
    SQLiteDatabase sqLiteDatabase;

    int noOfRetailerVisits = 0;
    boolean wasShown = false;
    MySharedPrefrencesData mySharedPrefrencesData = new MySharedPrefrencesData();

    SharedPreferences sharedPreferences;
    String usernameInSharedPreferences;
    String passwordInSharedPreferences;

    boolean isValidAuthkey = false;
    String type;

    public static String substringAfterLastSeparator(String str, String separator)
    {
        if(isEmpty(str) || isEmpty(separator))
        {
            return str;
        }
        int pos = str.lastIndexOf(separator);
        if(pos == -1)
        {
            return str;
        }
        return str.substring(pos, str.length());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        findViewsByIDs();
        setListenersToViews();

        setSupportActionBar(toolbar);

        valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        mySharedPrefrencesData = new MySharedPrefrencesData();

        sharedPreferences = getSharedPreferences(MY_PREF, Context.MODE_PRIVATE);
        String spDefaultValue = "spDefaultValue"; //sp = shared preferences

        usernameInSharedPreferences = sharedPreferences.getString(KEY_USERNAME, spDefaultValue);
        passwordInSharedPreferences = sharedPreferences.getString(KEY_PASSWORD, spDefaultValue);


        networkcall_for_ISValidAuthKey();

        //   networkcall_for_getUserDetails();

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        Animation swingAnimation = AnimationUtils.loadAnimation(this, R.anim.swing);
        swingAnimation.reset();
        promoSchemesBell_ImageView.clearAnimation();
        promoSchemesBell_ImageView.startAnimation(swingAnimation);

        askUserToGrantPermissions();

       /* Animation rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        rotateAnimation.reset();
        retailerVisits_ProgressBar.clearAnimation();
        retailerVisits_ProgressBar.startAnimation(rotateAnimation);*/

        updateIMEI(loggedInUserID);
    }

    @Override
    protected void onPostResume()
    {
        super.onPostResume();

        if(loggedInUserID == null)
        {
            throw new NullPointerException("loggedInUserID is null");
        }
        else
        {
            noOfRetailerVisits = DbUtils.getRetailerVisitsFor(loggedInUserID);
            retailerVisits_ProgressBar.setProgress(noOfRetailerVisits);
            noOfRetailerVisits_TextView.setText(String.valueOf(noOfRetailerVisits));
            setColorsDependingOnRetailerVisits();
        }

        if(noOfRetailerVisits >= 3 && !wasShown)
        {
            showCongratulatoryMessage(noOfRetailerVisits);
            playJingle();
        }
    }

    private void findViewsByIDs()
    {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        promoSchemes_FrameLayout = (FrameLayout) findViewById(R.id.promoSchemes_FrameLayout);
        promoSchemesBell_ImageView = (ImageView) findViewById(R.id.promoSchemesBell_ImageView);
        noOfPromoSchemes_TextView = (TextView) findViewById(R.id.noOfPromoSchemes_TextView);
        retailerVisits_ProgressBar = (ProgressBar) findViewById(R.id.retailerVisits_ProgressBar);
        retailerVisits_LinearLayout = (LinearLayout) findViewById(R.id.retailerVisits_LinearLayout);
        noOfRetailerVisits_TextView = (TextView) findViewById(R.id.noOfRetailerVisits_TextView);
        newProducts_TextView = (TextView) findViewById(R.id.newProducts_TextView);
        promotionalProducts_TextView = (TextView) findViewById(R.id.promotionalProducts_TextView);
        regularlyOrderedProducts_TextView = (TextView) findViewById(R.id.regularlyOrderedProducts_TextView);
        allProducts_TextView = (TextView) findViewById(R.id.allProducts_TextView);
    }

    private void setListenersToViews()
    {

        Resources resources = getResources();
        final String intentExtraKey_TabToShow = resources.getString(R.string.key_tab_to_show);


        navigationView.setNavigationItemSelectedListener(this);

        retailerVisits_LinearLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Utils.launchActivity(getBaseContext(), SalesSummaryActivity.class);
            }
        });

        newProducts_TextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                Utils.launchActivityWithExtra(DashboardActivity.this, SkuListByGenreActivity.class, intentExtraKey_TabToShow, NEW_SKUs_TAB);


            }
        });

        promotionalProducts_TextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Utils.launchActivityWithExtra(DashboardActivity.this, SkuListByGenreActivity.class, intentExtraKey_TabToShow, PROMO_SKUs_TAB);

            }
        });

        regularlyOrderedProducts_TextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Utils.launchActivityWithExtra(DashboardActivity.this, SkuListByGenreActivity.class, intentExtraKey_TabToShow, FREQUENT_SKUs_TAB);

            }
        });

        allProducts_TextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                Utils.launchActivityWithExtra(DashboardActivity.this, SkuListByGenreActivity.class, intentExtraKey_TabToShow, All_SKUs_TAB);


            }
        });

        promoSchemes_FrameLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Utils.launchActivity(getBaseContext(), SchemesActivity.class);
            }
        });
    }

    private void askUserToGrantPermissions()
    {
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            String[] permissionsArray = {"android.permission.CAMERA",
                    "android.permission.ACCESS_FINE_LOCATION",
                    "android.permission.ACCESS_COARSE_LOCATION",
                    "android.permission.READ_PHONE_STATE",
                    "android.permission.WRITE_EXTERNAL_STORAGE"};
            try
            {
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(DashboardActivity.this, permissionsArray, 1);
                }
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
    {
        String uf = "upcoming feature";

        switch(menuItem.getItemId())
        {
            case R.id.navItem_NewRetailer:
                //Utils.showToast(this, uf);
                Utils.launchActivity(this, CreateRetailerActivity.class);
                break;
            case R.id.navItem_NewSalesOrder:
                Utils.launchActivity(this, PickRetailerActivity.class);
                break;
            case R.id.navItem_mySalesHistory:
                Utils.launchActivity(this, MySalesHistoryActivity.class);
                break;
           /* case R.id.navItem_pendingOrders:
                Utils.launchActivity(this, PendingOrdersActivity.class);
                break;*/
            case R.id.navItem_logout:
                //TODO: Logout API Call

                SharedPreferences settings = getSharedPreferences(MySharedPrefrencesData.PREFS_NAME, 0); // 0 - for private mode
                SharedPreferences.Editor editor = settings.edit();

                //Set "hasLoggedIn" to true
                editor.putBoolean("hasLoggedIn", false);
                // Commit the edits!
                editor.commit();



                SharedPreferences sharedPreferences = getSharedPreferences(MY_PREF, Context.MODE_PRIVATE);
                SharedPreferences.Editor mEditor = sharedPreferences.edit();
                mEditor.remove(KEY_USERNAME);
                mEditor.remove(KEY_PASSWORD);
                mEditor.commit();

                Utils.launchActivity(this, LoginActivity.class);
                finish();

                break;
            default:
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    void setColorsDependingOnRetailerVisits()
    {
        if(noOfRetailerVisits <= 5)
        {
            retailerVisits_ProgressBar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
            noOfRetailerVisits_TextView.setTextColor(Color.RED);
        }
        else if(noOfRetailerVisits >= 6 && noOfRetailerVisits <= 10)
        {
            retailerVisits_ProgressBar.getProgressDrawable().setColorFilter(Color.CYAN, PorterDuff.Mode.SRC_IN);
            noOfRetailerVisits_TextView.setTextColor(Color.CYAN);
        }
        else
        {
            retailerVisits_ProgressBar.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
            noOfRetailerVisits_TextView.setTextColor(Color.GREEN);
        }
    }

    void updateIMEI(String salesPersonID)
    {
        String imeiNumber = null;

        if(ActivityCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)
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

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Congratulations!");
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



   /* @Override
    public void ReceivedResponseFromServer(String output, String REQUEST_NUMBER)
    {
        switch(REQUEST_NUMBER)
        {

            case Constants.REQUEST_FOR_USERDETAILS:
                try
                {
                    JSONObject jsonObject = new JSONObject(output);
                    if(jsonObject.optInt("api_status") == 1)
                    {
                        AuthUserDetails authUserDetails = new Gson().fromJson(output, AuthUserDetails.class);

                        AuthUserDetails.Data data = authUserDetails.getData();

                        mySharedPrefrencesData.setUsername(DashboardActivity.this, data.getUsername());
                        mySharedPrefrencesData.set_User_mobile(DashboardActivity.this, data.getMobile());
                        mySharedPrefrencesData.setEmail(DashboardActivity.this, data.getEmail());

                    }
                    else
                    {
                        Utils.showToast(DashboardActivity.this, "Unsuccessful api call for user details");
                    }
                }
                catch(JSONException e)
                {
                    e.printStackTrace();
                }
                break;
        }
    }*/

    void playJingle()
    {
        MediaPlayer mediaPlayer = new MediaPlayer();

        try
        {
            String jinglePath = Constants.appSpecificDirectoryPath + File.separator + Constants.JINGLE_FILE_NAME;
            mediaPlayer.setDataSource(jinglePath);
            mediaPlayer.prepare();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        mediaPlayer.start();
    }

   /* private void networkcall_for_authtoken()
    {

        Apimethods methods = API_Call_Retrofit.getretrofit(this).create(Apimethods.class);

        IM_Login IMLogin = new IM_Login( Utils.getDeviceId(DashboardActivity.this), usernameInSharedPreferences, usernameInSharedPreferences);

        Call<AuthUser_Model> call = methods.getUserAuthKey(IMLogin);
        Log.d("url", "url=" + call.request().url().toString());

        call.enqueue(new Callback<AuthUser_Model>()
        {
            @Override
            public void onResponse(Call<AuthUser_Model> call, Response<AuthUser_Model> response)
            {
                int statusCode = response.code();
                Log.d("Response", "" + statusCode);
                Log.d("respones", "" + response);

                if(response.isSuccessful()){
                    AuthUser_Model authUser_model = response.body();
                    if(authUser_model.getApiStatus() == 1)
                    {
                        Utils.showToast(DashboardActivity.this,"succssful api call for auth token");
                        mySharedPrefrencesData.setEmployee_AuthKey(DashboardActivity.this, authUser_model.getAuthToken());
                        mySharedPrefrencesData.setAuthTokenExpiryDate(DashboardActivity.this, authUser_model.getAuthTokenExpiryDate());

                        networkcall_for_getSKUlistAfter(authUser_model.getAuthToken());
                    }
                } else
                {

                    networkcall_for_authtoken();
                    Utils.showToast(DashboardActivity.this,"unsuccssful api call for auth token");
                }
            }

            @Override
            public void onFailure(Call<AuthUser_Model> call, Throwable t)
            {

                Utils.showToast(DashboardActivity.this, ConstantsA.NO_INTERNET_CONNECTION);

            }
        });

    }*/

    private void networkcall_for_ISValidAuthKey()
    {

        final ProgressDialog progressDialog=new ProgressDialog(DashboardActivity.this);
        Utils.startProgressDialog(DashboardActivity.this,progressDialog);

        final Apimethods methods = API_Call_Retrofit.getretrofit(this).create(Apimethods.class);

        IM_IsValidAuthKey IM_isValidAuthKey = new IM_IsValidAuthKey(mySharedPrefrencesData.getEmployee_AuthKey(DashboardActivity.this));

        Call<ValidAuthModel> call = methods.isValidAuthKey(IM_isValidAuthKey);
        Log.d("url", "url=" + call.request().url().toString());

        call.enqueue(new Callback<ValidAuthModel>()
        {
            @Override
            public void onResponse(Call<ValidAuthModel> call, Response<ValidAuthModel> response)
            {
                int statusCode = response.code();
                Log.d("Response", "" + statusCode);
                Log.d("respones", "" + response);
                if(response.isSuccessful())
                {
                    ValidAuthModel validAuthModel = response.body();
                    if(validAuthModel.getApi_status() == 1)
                    {
                        mySharedPrefrencesData.setUsername(DashboardActivity.this, usernameInSharedPreferences);
                        mySharedPrefrencesData.setUser_pwd(DashboardActivity.this, passwordInSharedPreferences);
                      //  Utils.showToast(DashboardActivity.this, "valid auth key ");
                        Log.e("authkey",mySharedPrefrencesData.getEmployee_AuthKey(DashboardActivity.this));
                        isValidAuthkey = true;
                        Utils.dismissProgressDialog(progressDialog);

                    }
                }
                else
                {
                    networkcall_for_authtoken();
                   // Utils.showToast(DashboardActivity.this, "Unsuccessful api call for isvalid auth key ");
                }

            }

            private void networkcall_for_authtoken()
            {
                IM_Login IMLogin = new IM_Login(Utils.getDeviceId(DashboardActivity.this), usernameInSharedPreferences, usernameInSharedPreferences);
                Call<AuthUser_Model> call = methods.getUserAuthKey(IMLogin);
                Log.d("url", "url=" + call.request().url().toString());

                call.enqueue(new Callback<AuthUser_Model>()
                {
                    @Override
                    public void onResponse(Call<AuthUser_Model> call, Response<AuthUser_Model> response)
                    {
                        int statusCode = response.code();
                        Log.d("Response", "" + statusCode);
                        Log.d("respones", "" + response);

                        if(response.isSuccessful())
                        {
                            AuthUser_Model authUser_model = response.body();
                            if(authUser_model.getApiStatus() == 1)
                            {
                                Utils.showToast(DashboardActivity.this, "succssful api call for auth token");
                                mySharedPrefrencesData.setEmployee_AuthKey(DashboardActivity.this, authUser_model.getAuthToken());
                                mySharedPrefrencesData.setAuthTokenExpiryDate(DashboardActivity.this, authUser_model.getAuthTokenExpiryDate());
                                isValidAuthkey = true;
                                Utils.dismissProgressDialog(progressDialog);
                                //   networkcall_for_getSKUlistAfter(authUser_model.getAuthToken());
                            }
                        }
                        else
                        {

                            networkcall_for_authtoken();
                            Utils.showToast(DashboardActivity.this, "unsuccssful api call for auth token");
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthUser_Model> call, Throwable t)
                    {

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

   /* private void networkcall_for_getUserDetails()
    {

        AsyncWorker mWorker = new AsyncWorker(DashboardActivity.this);
        mWorker.delegate = DashboardActivity.this;
        JSONObject broadcastObject = new JSONObject();
        try
        {

            broadcastObject.put("authToken", mySharedPrefrencesData.getEmployee_AuthKey(DashboardActivity.this));
            Log.d("inputbody_userdetails", broadcastObject.toString());

        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
        mWorker.execute(ApiUrl.BASE_URL + ApiUrl.LOG_URL_GETUSERDETAILS, broadcastObject.toString(), Constants.POST_REQUEST, Constants.REQUEST_FOR_USERDETAILS);
    }*/

    private void networkcall_for_getSKUlistAfter(final String authToken)
    {
        final ProgressDialog progressDialog=new ProgressDialog(DashboardActivity.this);
        Utils.startProgressDialog(DashboardActivity.this,progressDialog);

        Resources resources = getResources();
        final String intentExtraKey_TabToShow = resources.getString(R.string.key_tab_to_show);

        final Apimethods methods = API_Call_Retrofit.getretrofit(this).create(Apimethods.class);

        IM_GetSkuListAfter IM_getSkuListAfter = new IM_GetSkuListAfter(authToken, type, "2016-05-25");

        Call<GetSkuListAfter> call = methods.getSkuListAfter(IM_getSkuListAfter);
        Log.d("url", "url=" + call.request().url().toString());

        call.enqueue(new Callback<GetSkuListAfter>()
        {
            @Override
            public void onResponse(Call<GetSkuListAfter> call, Response<GetSkuListAfter> response)
            {
                int statusCode = response.code();
                Log.d("Response", "" + statusCode);
                Log.d("respones", "" + response);
                if(response.isSuccessful())
                {
                    GetSkuListAfter getSkuListAfter = response.body();
                    if(getSkuListAfter.getApiStatus() == 1)
                    {


                        for(String sku_id : getSkuListAfter.getSkuIds())
                        {

                            if(!DbUtils.isSKUPresentInDb(sku_id)){

                                networkcall_for_sku_info(sku_id, authToken);
                                networkcall_for_sku_Attr(sku_id, authToken);
                            }

                        }

                        Utils.dismissProgressDialog(progressDialog);

                        if(type.equalsIgnoreCase("1"))
                        {
                            Utils.launchActivityWithExtra(DashboardActivity.this, SkuListByGenreActivity.class, intentExtraKey_TabToShow, All_SKUs_TAB);

                        }
                        else if(type.equalsIgnoreCase("2"))
                        {
                            Utils.launchActivityWithExtra(DashboardActivity.this, SkuListByGenreActivity.class, intentExtraKey_TabToShow, PROMO_SKUs_TAB);

                        }
                        else if(type.equalsIgnoreCase("3"))
                        {
                            Utils.launchActivityWithExtra(DashboardActivity.this, SkuListByGenreActivity.class, intentExtraKey_TabToShow, FREQUENT_SKUs_TAB);

                        }
                        else if(type.equalsIgnoreCase("4"))
                        {
                            Utils.launchActivityWithExtra(DashboardActivity.this, SkuListByGenreActivity.class, intentExtraKey_TabToShow, NEW_SKUs_TAB);
                        }
                    }
                }
                else
                {
                    Utils.dismissProgressDialog(progressDialog);
                    Utils.showToast(DashboardActivity.this, "Unsuccessful api call for getSkuListAfter ");
                }

            }

            private void networkcall_for_sku_ThumbImage(String sku_id, String authToken)
            {
                IM_GetSkuInfo im_getSkuInfo = new IM_GetSkuInfo(authToken, sku_id);

                Call<GetSkuThumbImage> call = methods.getSkuThumbImage(im_getSkuInfo);
                Log.d("url", "url=" + call.request().url().toString());

                call.enqueue(new Callback<GetSkuThumbImage>()
                {
                    @Override
                    public void onResponse(Call<GetSkuThumbImage> call, Response<GetSkuThumbImage> response)
                    {
                        int statusCode = response.code();
                        Log.d("Response", "" + statusCode);
                        Log.d("respones", "" + response);
                        if(response.isSuccessful())
                        {
                            GetSkuThumbImage getSkuThumbImage = response.body();
                            if(getSkuThumbImage.getApiStatus() == 1)
                            {

                                for(GetSkuThumbImage.SkuMedium skuMedia : getSkuThumbImage.getSkuMedia())
                                {
                                    String selection = "sku_id = ?";
                                    String[] selectionArgs = {skuMedia.getSkuId()};

                                    String sku_photourl_from_server = skuMedia.getMediaFile();

                                    ContentValues contentValues = new ContentValues();

                                    String log_photo_url = substringAfterLastSeparator(sku_photourl_from_server, "/");

                                    contentValues.put("sku_photo_source", ApiUrl.BASEURL_SKUTHUMBIMAGE + log_photo_url);

                                    sqLiteDatabase.update(TBL_SKU, contentValues, selection, selectionArgs);

                                }

                            }
                        }
                        else
                        {
                            Utils.dismissProgressDialog(progressDialog);
                            Utils.showToast(DashboardActivity.this, "Unsuccessful api call for getskuinfo_thumbimage ");
                        }
                    }

                    @Override
                    public void onFailure(Call<GetSkuThumbImage> call, Throwable t)
                    {

                        Utils.dismissProgressDialog(progressDialog);
                        Utils.showToast(DashboardActivity.this, ConstantsA.NO_INTERNET_CONNECTION);
                    }
                });

            }

            private void networkcall_for_sku_Attr(String sku_id, String authToken)
            {
                IM_GetSkuInfo im_getSkuInfo = new IM_GetSkuInfo(authToken, sku_id);

                Call<GetSkuAttribute> call = methods.getSkuAttr(im_getSkuInfo);
                Log.d("url", "url=" + call.request().url().toString());

                call.enqueue(new Callback<GetSkuAttribute>()
                {
                    @Override
                    public void onResponse(Call<GetSkuAttribute> call, Response<GetSkuAttribute> response)
                    {
                        int statusCode = response.code();
                        Log.d("Response", "" + statusCode);
                        Log.d("respones", "" + response);
                        if(response.isSuccessful())
                        {
                            GetSkuAttribute getSkuAttribute = response.body();
                            if(getSkuAttribute.getApiStatus() == 1)
                            {
                                for(int j = 0; j < getSkuAttribute.getSkuAttr().size(); j++)
                                {
                                    GetSkuAttribute.SkuAttr skuAttr = getSkuAttribute.getSkuAttr().get(j);

                                    ContentValues skuAttrMappingValues = new ContentValues();
                                    skuAttrMappingValues.put("sku_id", skuAttr.getSkuId());
                                    skuAttrMappingValues.put("attribute_id", skuAttr.getGlobalAttributeId());
                                    skuAttrMappingValues.put("upload_status", 0);
                                    sqLiteDatabase.insert(TBL_SKU_ATTRIBUTE_MAPPING, null, skuAttrMappingValues);


                                    String attrvalue_str = skuAttr.getAttributeValue();

                                    String attributevalue_array[] = attrvalue_str.split(",");

                                    StringBuilder builder = new StringBuilder();
                                    for(String s : attributevalue_array)
                                    {
                                        builder.append(s);
                                        builder.append("`");
                                    }
                                    String attributevalueset = builder.toString();
                                    String attr_value_set_final = attributevalueset.substring(0, attributevalueset.length() - 1);

                                    ContentValues globalAttributeValues = new ContentValues();
                                    globalAttributeValues.put("attribute_id", skuAttr.getGlobalAttributeId());
                                    globalAttributeValues.put("attribute_type", skuAttr.getAttributeType());
                                    globalAttributeValues.put("attribute_name", skuAttr.getAttributeName());
                                    globalAttributeValues.put("attribute_value", attr_value_set_final);
                                    globalAttributeValues.put("created_date", skuAttr.getCreatedOn());
                                    globalAttributeValues.put("upload_status", 0);
                                    sqLiteDatabase.insert(TBL_GLOBAL_ATTRIBUTES, null, globalAttributeValues);
                                }

                            }
                        }
                        else
                        {
                            Utils.dismissProgressDialog(progressDialog);
                            Utils.showToast(DashboardActivity.this, "Unsuccessful api call for GetSkuAttribute ");
                        }
                    }

                    @Override
                    public void onFailure(Call<GetSkuAttribute> call, Throwable t)
                    {
                        Utils.dismissProgressDialog(progressDialog);
                        Utils.showToast(DashboardActivity.this, ConstantsA.NO_INTERNET_CONNECTION);
                    }
                });
            }

            private void networkcall_for_sku_info(final String sku_id, final String authToken)
            {
                IM_GetSkuInfo im_getSkuInfo = new IM_GetSkuInfo(authToken, sku_id);

                Call<GetSkuInfo> call = methods.getSkuInfo(im_getSkuInfo);
                Log.d("url", "url=" + call.request().url().toString());

                call.enqueue(new Callback<GetSkuInfo>()
                {
                    @Override
                    public void onResponse(Call<GetSkuInfo> call, Response<GetSkuInfo> response)
                    {
                        int statusCode = response.code();
                        Log.d("Response", "" + statusCode);
                        Log.d("respones", "" + response);
                        if(response.isSuccessful())
                        {
                            GetSkuInfo getSkuInfo = response.body();
                            if(getSkuInfo.getApiStatus() == 1)
                            {
                                GetSkuInfo.SkuIds skuId_Info = getSkuInfo.getSkuIds();
                                ContentValues skuValues = new ContentValues();
                                skuValues.put("sku_id", skuId_Info.getSkuId());
                                skuValues.put("sku_name", skuId_Info.getSkuName());
                                skuValues.put("sku_price", skuId_Info.getSkuPrice());
                                skuValues.put("description", skuId_Info.getSkuDescription());
                                skuValues.put("sku_category", skuId_Info.getSkuCategory());
                                skuValues.put("sku_sub_category", skuId_Info.getSkuSubCategory());
                                if(type.equalsIgnoreCase("1"))
                                {

                                }
                                else if(type.equalsIgnoreCase("2"))
                                {
                                    skuValues.put("promotional_sku", 1);
                                }
                                else if(type.equalsIgnoreCase("3"))
                                {

                                }
                                else if(type.equalsIgnoreCase("4"))
                                {
                                    skuValues.put("new_sku", 1);
                                }


                                skuValues.put("upload_status", 0);
                                sqLiteDatabase.insert(TBL_SKU, null, skuValues);

                                networkcall_for_sku_ThumbImage(skuId_Info.getSkuId(), authToken);

                            }
                        }
                        else
                        {
                            Utils.dismissProgressDialog(progressDialog);
                            Utils.showToast(DashboardActivity.this, "Unsuccessful api call for getskuinfo ");
                        }
                    }

                    @Override
                    public void onFailure(Call<GetSkuInfo> call, Throwable t)
                    {
                        Utils.dismissProgressDialog(progressDialog);
                        Utils.showToast(DashboardActivity.this, ConstantsA.NO_INTERNET_CONNECTION);
                    }
                });

            }

            @Override
            public void onFailure(Call<GetSkuListAfter> call, Throwable t)
            {
                Utils.dismissProgressDialog(progressDialog);
                Utils.showToast(DashboardActivity.this, ConstantsA.NO_INTERNET_CONNECTION);

            }
        });


    }
}

