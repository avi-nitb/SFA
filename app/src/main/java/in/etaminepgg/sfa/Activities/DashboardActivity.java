package in.etaminepgg.sfa.Activities;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
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

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import in.etaminepgg.sfa.Models.AuthUserDetails;
import in.etaminepgg.sfa.Models.AuthUser_Model;
import in.etaminepgg.sfa.Network.ApiUrl;
import in.etaminepgg.sfa.R;
import in.etaminepgg.sfa.Utilities.Constants;
import in.etaminepgg.sfa.Utilities.DbUtils;
import in.etaminepgg.sfa.Utilities.MyDb;
import in.etaminepgg.sfa.Utilities.MySharedPrefrencesData;
import in.etaminepgg.sfa.Utilities.Utils;
import in.etaminepgg.sfa.network_asynctask.AsyncResponse;
import in.etaminepgg.sfa.network_asynctask.AsyncWorker;

import static in.etaminepgg.sfa.Activities.LoginActivity.baseContext;
import static in.etaminepgg.sfa.Utilities.Constants.IMEI;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_EMPLOYEE;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SALES_ORDER_DETAILS;
import static in.etaminepgg.sfa.Utilities.Constants.dbFileFullPath;
import static in.etaminepgg.sfa.Utilities.ConstantsA.All_SKUs_TAB;
import static in.etaminepgg.sfa.Utilities.ConstantsA.FREQUENT_SKUs_TAB;
import static in.etaminepgg.sfa.Utilities.ConstantsA.NEW_SKUs_TAB;
import static in.etaminepgg.sfa.Utilities.ConstantsA.PROMO_SKUs_TAB;
import static in.etaminepgg.sfa.Utilities.SharedPreferenceSingleton.MY_PREF;
import static in.etaminepgg.sfa.Utilities.Utils.loggedInUserID;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AsyncResponse {
    FrameLayout promoSchemes_FrameLayout;
    ImageView promoSchemesBell_ImageView;
    ProgressBar retailerVisits_ProgressBar;
    LinearLayout retailerVisits_LinearLayout;
    TextView noOfPromoSchemes_TextView, noOfRetailerVisits_TextView, newProducts_TextView,
            promotionalProducts_TextView, regularlyOrderedProducts_TextView, allProducts_TextView;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    int noOfRetailerVisits = 0;
    boolean wasShown = false;
    MySharedPrefrencesData mySharedPrefrencesData=new MySharedPrefrencesData();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        findViewsByIDs();
        setListenersToViews();

        setSupportActionBar(toolbar);

        networkcall_for_getUserDetails();
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

    private void networkcall_for_getUserDetails() {

        AsyncWorker mWorker = new AsyncWorker(DashboardActivity.this);
        mWorker.delegate = DashboardActivity.this;
        JSONObject BroadcastObject = new JSONObject();
        try {

            BroadcastObject.put("authToken",new MySharedPrefrencesData().getEmployee_AuthKey(DashboardActivity.this) );
            Log.d("inputbody_userdetails",BroadcastObject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        mWorker.execute(ApiUrl.BASE_URL+ApiUrl.LOG_URL_GETUSERDETAILS, BroadcastObject.toString(), Constants.POST_REQUEST, Constants.REQUEST_FOR_USERDETAILS);
    }

    @Override
    protected void onPostResume()
    {
        super.onPostResume();

        if (loggedInUserID == null)
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

        if (noOfRetailerVisits >= 3 && !wasShown)
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
                Utils.launchActivityWithExtra(getBaseContext(), SkuListByGenreActivity.class, intentExtraKey_TabToShow, NEW_SKUs_TAB);
            }
        });

        promotionalProducts_TextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Utils.launchActivityWithExtra(getBaseContext(), SkuListByGenreActivity.class, intentExtraKey_TabToShow, PROMO_SKUs_TAB);
            }
        });

        regularlyOrderedProducts_TextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Utils.launchActivityWithExtra(getBaseContext(), SkuListByGenreActivity.class, intentExtraKey_TabToShow, FREQUENT_SKUs_TAB);
            }
        });

        allProducts_TextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Utils.launchActivityWithExtra(getBaseContext(), SkuListByGenreActivity.class, intentExtraKey_TabToShow, All_SKUs_TAB);
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
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            String[] permissionsArray = {"android.permission.CAMERA",
                    "android.permission.ACCESS_FINE_LOCATION",
                    "android.permission.ACCESS_COARSE_LOCATION",
                    "android.permission.READ_PHONE_STATE",
                    "android.permission.WRITE_EXTERNAL_STORAGE"};
            try
            {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(DashboardActivity.this, permissionsArray, 1);
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
    {
        String uf = "upcoming feature";

        switch (menuItem.getItemId())
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

                SharedPreferences sharedPreferences = getSharedPreferences(MY_PREF, Context.MODE_PRIVATE);;
                SharedPreferences.Editor mEditor = sharedPreferences.edit();
                mEditor.remove(LoginActivity.KEY_USERNAME);
                mEditor.remove(LoginActivity.KEY_PASSWORD);
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
        else if (noOfRetailerVisits >= 6 && noOfRetailerVisits <= 10)
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

        if (ActivityCompat.checkSelfPermission(baseContext, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)
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

    void playJingle()
    {
        MediaPlayer mediaPlayer = new MediaPlayer();

        try
        {
            String jinglePath = Constants.appSpecificDirectoryPath + File.separator + Constants.JINGLE_FILE_NAME;
            mediaPlayer.setDataSource(jinglePath);
            mediaPlayer.prepare();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        mediaPlayer.start();
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void ReceivedResponseFromServer(String output, String REQUEST_NUMBER) {
        switch (REQUEST_NUMBER){
            case Constants.REQUEST_FOR_USERDETAILS:
                try {
                    JSONObject jsonObject=new JSONObject(output);
                    if(jsonObject.optInt("api_status")==1){
                        AuthUserDetails authUserDetails=new Gson().fromJson(output,AuthUserDetails.class);

                        AuthUserDetails.Data data=authUserDetails.getData();

                        mySharedPrefrencesData.setUsername(DashboardActivity.this,data.getUsername());
                        mySharedPrefrencesData.set_User_mobile(DashboardActivity.this,data.getMobile());
                        mySharedPrefrencesData.setEmail(DashboardActivity.this,data.getEmail());

                    }else {
                        Utils.showToast(baseContext,"Unsuccessful api call");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

        }
    }
}

