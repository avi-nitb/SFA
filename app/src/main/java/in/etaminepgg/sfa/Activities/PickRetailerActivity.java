package in.etaminepgg.sfa.Activities;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.etaminepgg.sfa.BuildConfig;
import in.etaminepgg.sfa.InputModel_For_Network.IM_IsValidAuthKey;
import in.etaminepgg.sfa.InputModel_For_Network.IM_PutRetailerInfo;
import in.etaminepgg.sfa.InputModel_For_Network.IM_UpdateRetailerPicture;
import in.etaminepgg.sfa.Models.NoOrderReasonList;
import in.etaminepgg.sfa.Models.PutRetailerInfo_Model;
import in.etaminepgg.sfa.Models.UpdateRetailerpictureModel;
import in.etaminepgg.sfa.Network.API_Call_Retrofit;
import in.etaminepgg.sfa.Network.Apimethods;
import in.etaminepgg.sfa.R;
import in.etaminepgg.sfa.Utilities.ConstantsA;
import in.etaminepgg.sfa.Utilities.DbUtils;
import in.etaminepgg.sfa.Utilities.LocationTrack;
import in.etaminepgg.sfa.Utilities.MyDb;
import in.etaminepgg.sfa.Utilities.MySharedPrefrencesData;
import in.etaminepgg.sfa.Utilities.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.os.Environment.getExternalStoragePublicDirectory;
import static in.etaminepgg.sfa.Utilities.Constants.REQUEST_TURN_ON_LOCATION;
import static in.etaminepgg.sfa.Utilities.Constants.SHOW_UPDATE_BUTTON;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_RETAILER;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SKU_NO_ORDERREASON;
import static in.etaminepgg.sfa.Utilities.Constants.dbFileFullPath;
import static in.etaminepgg.sfa.Utilities.ConstantsA.EMPTY;
import static in.etaminepgg.sfa.Utilities.ConstantsA.INTENT_EXTRA_MOBILE_RETAILER_ID;
import static in.etaminepgg.sfa.Utilities.ConstantsA.INTENT_EXTRA_RETAILER_ID;
import static in.etaminepgg.sfa.Utilities.ConstantsA.INTENT_EXTRA_RETAILER_NAME;
import static in.etaminepgg.sfa.Utilities.ConstantsA.INTENT_EXTRA_UPLOAD_STATUS;
import static in.etaminepgg.sfa.Utilities.ConstantsA.NOT_PRESENT;
import static in.etaminepgg.sfa.Utilities.ConstantsA.NO_INTERNET_CONNECTION;
import static in.etaminepgg.sfa.Utilities.Utils.getRandomNumber;
import static in.etaminepgg.sfa.Utilities.Utils.getTodayDate;

public class PickRetailerActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
    private static final int REQUEST_IMAGE_CAPTURE = 100;
    private final static int ALL_PERMISSIONS_RESULT_PICKRETAILER = 456;
    static String SEPARATOR = ", ";
    static int THRESHOLD = 1;
    private final int REQUEST_CODE_ACCESS_FINE_LOCATION = 1000;


    File imageFile = null;
    String latitude = NOT_PRESENT;
    String longitude = NOT_PRESENT;
    Button selectOrderType_Button, updateRetailer_Button;
    AutoCompleteTextView shopName_AutoCompleteTextView;
    TextView retailerName_TextView, retailerOwnerName_TextView, retailerMobile_TextView, img_TextView, latitude_TextView, longitude_TextView;
    ImageView retailerPhoto_ImageView;
    boolean isItemClicked = false;
    String retailerName;
    String retailerID;
    String mobileRetailerID;
    String uploadStatus;


    MySharedPrefrencesData mySharedPrefrencesData;
    LocationTrack locationTrack;
    String lat = NOT_PRESENT, lng = NOT_PRESENT;
    private GoogleApiClient googleApiClient;
    LocationListener locationListener = new LocationListener()
    {
        @Override
        public void onLocationChanged(Location location)
        {
            //Log.i("Location", "changed");
            //Utils.showToast(getBaseContext(), "lat: " + location.getLatitude() + "lng: " + location.getLongitude());

            latitude = String.valueOf(location.getLatitude());
            longitude = String.valueOf(location.getLongitude());

            if (googleApiClient.isConnected())
            {

                LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, locationListener);
            }


            dispatchTakePictureIntent();

            //dismissProgressDialog();
        }
    };
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();
    private LocationRequest locationRequest;

    private static int exifToDegrees(int exifOrientation)
    {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90)
        {
            return 90;
        }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180)
        {
            return 180;
        }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270)
        {
            return 270;
        }
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_pick_retailer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //buildGoogleApiClient();

        findViewsByIDs();
        setListenersToViews();
        mySharedPrefrencesData = new MySharedPrefrencesData();

        if (Utils.isNetworkConnected(getBaseContext()))
        {

            if (DbUtils.getNoOfSalesOrdersForsize() < 1)
            {

                networkcallForReasonList();
            }

        }

        shopName_AutoCompleteTextView.setThreshold(THRESHOLD);


        // Keys used in Hashmap
        String[] from = {"retailer_name"};

        // Ids of views in listview_layout
        int[] to = {android.R.id.text1};

        // Instantiating an adapter to store each items
        // R.layout.listview_layout defines the layout of each item
        //adapter for retailer
        SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), getCustomretailerList(), android.R.layout.simple_dropdown_item_1line, from, to);
        shopName_AutoCompleteTextView.setAdapter(adapter);


        //one activity used for update and sales order


        //so checking for intent extras

        if (getIntent().getStringExtra(SHOW_UPDATE_BUTTON).equalsIgnoreCase("YES"))
        {
            permissions.add(ACCESS_FINE_LOCATION);
            permissions.add(ACCESS_COARSE_LOCATION);

            permissionsToRequest = findUnAskedPermissions(permissions);
            //get the permissions we have asked for before but are not granted..
            //we will store this in a global list to access later.


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {


                if (permissionsToRequest.size() > 0)
                {
                    requestPermissions((String[]) permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT_PICKRETAILER);
                }
            }


            locationTrack = new LocationTrack(PickRetailerActivity.this);
        }

    }

    //api call for reason api

    private void networkcallForReasonList()
    {

        final ProgressDialog progressDialog = new ProgressDialog(PickRetailerActivity.this);
        Utils.startProgressDialog(PickRetailerActivity.this, progressDialog);

        final Apimethods methods = API_Call_Retrofit.getretrofit(this).create(Apimethods.class);

        IM_IsValidAuthKey IM_isValidAuthKey = new IM_IsValidAuthKey(mySharedPrefrencesData.getEmployee_AuthKey(PickRetailerActivity.this), mySharedPrefrencesData.get_User_CompanyId(PickRetailerActivity.this));


        Call<NoOrderReasonList> call = methods.getNoOrderReasonList(IM_isValidAuthKey);


        Log.i("no_order_reason_ip", new Gson().toJson(IM_isValidAuthKey));

        Log.d("url", "url=" + call.request().url().toString());

        call.enqueue(new Callback<NoOrderReasonList>()
        {
            @Override
            public void onResponse(Call<NoOrderReasonList> call, Response<NoOrderReasonList> response)
            {


                if (response.isSuccessful())
                {
                    NoOrderReasonList noOrderReasonList = response.body();

                    int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
                    SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);


                    for (NoOrderReasonList.ReasonList reasonList : noOrderReasonList.getReasonList())
                    {


                        if (!DbUtils.isReasonIdInDb(reasonList.getId()))
                        {

                            ContentValues locationValues = new ContentValues();
                            locationValues.put("reason_id", reasonList.getId());
                            locationValues.put("reasondesc", reasonList.getReason());
                            locationValues.put("created_by", reasonList.getCreatedBy());
                            locationValues.put("modified_by", reasonList.getModifiedBy());
                            locationValues.put("created_date", reasonList.getCreatedOn());
                            locationValues.put("modified_date", reasonList.getModifiedOn());
                            locationValues.put("upload_status", 1);

                            sqLiteDatabase.insert(TBL_SKU_NO_ORDERREASON, null, locationValues);
                        }
                    }

                    Utils.dismissProgressDialog(progressDialog);

                    sqLiteDatabase.close();
                }
                else
                {
                    Utils.dismissProgressDialog(progressDialog);
                    Utils.showErrorDialog(PickRetailerActivity.this, "There is something wrong in no order reason list.");
                }


            }

            @Override
            public void onFailure(Call<NoOrderReasonList> call, Throwable t)
            {
                Utils.dismissProgressDialog(progressDialog);
                Utils.showToast(PickRetailerActivity.this, ConstantsA.NO_INTERNET_CONNECTION);
            }
        });

    }

    //get retailer list
    private List<HashMap<String, String>> getCustomretailerList()
    {

        // Each row in the list stores country name, currency and flag
        List<HashMap<String, String>> shopsInfoList = new ArrayList<HashMap<String, String>>();

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String userlocationid = mySharedPrefrencesData.getUser_LocationId(PickRetailerActivity.this);

        String selected_location_id = "";

        if (userlocationid.contains(","))
        {

            String location_id_arr[] = userlocationid.split(",");
            for (int i = 0; i < location_id_arr.length; i++)
            {

                //List<String> shopsInfoList = new ArrayList<>();
                String SQL_SELECT_RETAILERS = "select retailer_id,mobile_retailer_id,retailer_name,upload_status from " + TBL_RETAILER + "  WHERE  area_id = ?;";
                Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_RETAILERS, new String[]{location_id_arr[i]});
                cursor.moveToFirst();

                while (!cursor.isAfterLast())
                {
                    String retailerID = cursor.getString(cursor.getColumnIndexOrThrow("retailer_id"));
                    String mobileRetailerId = cursor.getString(cursor.getColumnIndexOrThrow("mobile_retailer_id"));
                    String retailer_name = cursor.getString(cursor.getColumnIndexOrThrow("retailer_name"));
                    String uploadStatus = cursor.getString(cursor.getColumnIndexOrThrow("upload_status"));

                    HashMap<String, String> hm = new HashMap<String, String>();
                    hm.put("retailer_name", retailer_name);
                    hm.put("retailer_id", retailerID + "," + mobileRetailerId + "," + uploadStatus);
                    shopsInfoList.add(hm);

                    ContentValues cv = new ContentValues();
                    cv.put("emp_id", mySharedPrefrencesData.getUser_Id(PickRetailerActivity.this));
                    sqLiteDatabase.update(TBL_RETAILER, cv, " retailer_id = ? AND mobile_retailer_id = ?", new String[]{retailerID, mobileRetailerId});
                    cursor.moveToNext();
                }
                cursor.close();
            }

        }
        else
        {

            selected_location_id = userlocationid;

            //List<String> shopsInfoList = new ArrayList<>();
            String SQL_SELECT_RETAILERS = "select retailer_id,mobile_retailer_id,retailer_name,upload_status from " + TBL_RETAILER + "  WHERE  area_id = ?;";
            Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_RETAILERS, new String[]{selected_location_id});
            cursor.moveToFirst();

            while (!cursor.isAfterLast())
            {
                String retailerID = cursor.getString(cursor.getColumnIndexOrThrow("retailer_id"));
                String mobileRetailerId = cursor.getString(cursor.getColumnIndexOrThrow("mobile_retailer_id"));
                String retailer_name = cursor.getString(cursor.getColumnIndexOrThrow("retailer_name"));
                String uploadStatus = cursor.getString(cursor.getColumnIndexOrThrow("upload_status"));

                HashMap<String, String> hm = new HashMap<String, String>();
                hm.put("retailer_name", retailer_name);
                hm.put("retailer_id", retailerID + "," + mobileRetailerId + "," + uploadStatus);
                shopsInfoList.add(hm);
                ContentValues cv = new ContentValues();
                cv.put("emp_id", mySharedPrefrencesData.getUser_Id(PickRetailerActivity.this));
                sqLiteDatabase.update(TBL_RETAILER, cv, " retailer_id = ? AND mobile_retailer_id = ?", new String[]{retailerID, mobileRetailerId});
                cursor.moveToNext();
            }
            cursor.close();

        }


        return shopsInfoList;
    }


    //check for location
    private ArrayList findUnAskedPermissions(ArrayList<String> wanted)
    {
        ArrayList result = new ArrayList();

        for (String perm : wanted)
        {
            if (!hasPermission(perm))
            {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission)
    {
        if (canMakeSmores())
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores()
    {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {

        switch (requestCode)
        {

            case ALL_PERMISSIONS_RESULT_PICKRETAILER:
                for (String perms : permissionsToRequest)
                {
                    if (!hasPermission(perms))
                    {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0)
                {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0)))
                        {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                            {
                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT_PICKRETAILER);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener)
    {
        new AlertDialog.Builder(PickRetailerActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            // Bitmap imageBitmap = BitmapFactory.decodeFile(getImagePath());

            //  retailerPhoto_ImageView.setImageBitmap(imageBitmap);

            latitude_TextView.setText("Latitude: " + latitude);

            longitude_TextView.setText("Longitude: " + longitude);


            //retailerPhoto_ImageView.setImageBitmap(photo);
            if (imageFile != null)
            {

                Glide.with(getBaseContext()).load(imageFile.getAbsolutePath()).into(retailerPhoto_ImageView);
            }

            //inserting data to retailer table
            updateRetailerInDB(retailerID, mobileRetailerID, latitude, longitude, getImagePath(), uploadStatus);
            Utils.showSuccessDialog(PickRetailerActivity.this, "Retailer updated successfully.");


        }
        else if (requestCode == REQUEST_TURN_ON_LOCATION && resultCode == RESULT_OK)
        {

        }
        else if (requestCode == REQUEST_TURN_ON_LOCATION && resultCode == RESULT_CANCELED)
        {
            Utils.showErrorDialog(this, "Updating Retailer failed. You must turn on GPS.");
        }
        else if (requestCode == LocationTrack.GPS_ENABLE_REQUEST)
        {

            locationTrack = new LocationTrack(PickRetailerActivity.this);
            if (locationTrack.canGetLocation())
            {
                //mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                lat = String.valueOf(locationTrack.getLongitude());
                lng = String.valueOf(locationTrack.getLatitude());

            }
            else
            {

                locationTrack.showSettingsAlert();
            }

        }
    }


    //update retailer api

    private void network_call_for_PutRetailerInfo(final String employee_authKey, final String retailerID, final String latitude, final String longitude, final String imagePath)
    {
        final ProgressDialog progressDialog = new ProgressDialog(PickRetailerActivity.this);
        Utils.startProgressDialog(PickRetailerActivity.this, progressDialog);

        final Apimethods apimethods = API_Call_Retrofit.getretrofit(PickRetailerActivity.this).create(Apimethods.class);

        IM_PutRetailerInfo.RetailerData retailerData = new IM_PutRetailerInfo().new RetailerData(retailerID, latitude + "," + longitude, "", "Retailers", "", "", "", "", new MySharedPrefrencesData().getUser_Id(PickRetailerActivity.this), "", "", retailerName, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", new MySharedPrefrencesData().getUser_Id(PickRetailerActivity.this));

        IM_PutRetailerInfo im_putRetailerInfo = new IM_PutRetailerInfo(employee_authKey, retailerData);

        Call<PutRetailerInfo_Model> call = apimethods.putRetailerInfo(im_putRetailerInfo);

        Log.i("putretailerinfo_ip", new Gson().toJson(im_putRetailerInfo));

        Log.d("url", "url=" + call.request().url().toString());

        call.enqueue(new Callback<PutRetailerInfo_Model>()
        {
            @Override
            public void onResponse(Call<PutRetailerInfo_Model> call, Response<PutRetailerInfo_Model> response)
            {
                int statusCode = response.code();
                Log.d("Response", "" + statusCode);
                Log.d("respones", "" + response);
                if (response.isSuccessful())
                {

                    PutRetailerInfo_Model putRetailerInfo_model = response.body();

                    Log.i("putretailerinfo_op", new Gson().toJson(putRetailerInfo_model));

                    if (putRetailerInfo_model.getApiStatus() == 1)
                    {


                        network_call_for_updatepicture(employee_authKey, retailerID, imagePath);

                    }
                }
                else
                {

                    Utils.dismissProgressDialog(progressDialog);

                    Utils.showErrorDialog(PickRetailerActivity.this, "Retailer is not updated successfully.");
                }
            }

            private void network_call_for_updatepicture(String employee_authKey, final String retailerID, String imagePath)
            {

                IM_UpdateRetailerPicture.RetailerData retailerData1 = new IM_UpdateRetailerPicture().new RetailerData(retailerID, imagePath);
                IM_UpdateRetailerPicture im_updateRetailerPicture = new IM_UpdateRetailerPicture(employee_authKey, retailerData1);
                Call<UpdateRetailerpictureModel> call = apimethods.updateRetailerPicture(im_updateRetailerPicture);

                Log.i("updt_retailpicture_ip", new Gson().toJson(im_updateRetailerPicture));

                call.enqueue(new Callback<UpdateRetailerpictureModel>()
                {
                    @Override
                    public void onResponse(Call<UpdateRetailerpictureModel> call, Response<UpdateRetailerpictureModel> response)
                    {
                        if (response.isSuccessful())
                        {

                            UpdateRetailerpictureModel updateRetailerpictureModel = response.body();

                            Log.i("updt_retailpicture_op", new Gson().toJson(updateRetailerpictureModel));

                            updateRetailerInDB(retailerID, mobileRetailerID, latitude, longitude, updateRetailerpictureModel.getCustomerPicturePath(), uploadStatus);
                            Utils.dismissProgressDialog(progressDialog);
                            Utils.showSuccessDialog(PickRetailerActivity.this, "Retailer updated successfully.");
                        }
                        else
                        {
                            Utils.dismissProgressDialog(progressDialog);
                            Utils.showErrorDialog(PickRetailerActivity.this, "Retailer is not updated successfully.");
                        }
                    }

                    @Override
                    public void onFailure(Call<UpdateRetailerpictureModel> call, Throwable t)
                    {
                        Utils.dismissProgressDialog(progressDialog);
                        Utils.showToast(PickRetailerActivity.this, NO_INTERNET_CONNECTION);
                    }
                });


            }

            @Override
            public void onFailure(Call<PutRetailerInfo_Model> call, Throwable t)
            {
                Utils.dismissProgressDialog(progressDialog);
                Utils.showToast(PickRetailerActivity.this, NO_INTERNET_CONNECTION);
            }
        });


    }

    private void findViewsByIDs()
    {
        shopName_AutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.shopName_AutoCompleteTextView);
        retailerName_TextView = (TextView) findViewById(R.id.retailerName_TextView);
        retailerOwnerName_TextView = (TextView) findViewById(R.id.retailerOwnerName_TextView);
        retailerMobile_TextView = (TextView) findViewById(R.id.retailerMobile_TextView);
        img_TextView = (TextView) findViewById(R.id.img_TextView);
        retailerPhoto_ImageView = (ImageView) findViewById(R.id.retailerPhoto_ImageView);
        latitude_TextView = (TextView) findViewById(R.id.latitude_TextView);
        longitude_TextView = (TextView) findViewById(R.id.longitude_TextView);
        selectOrderType_Button = (Button) findViewById(R.id.selectOrderType_Button);
        updateRetailer_Button = (Button) findViewById(R.id.updateRetailer_Button);

        if (getIntent().getStringExtra(SHOW_UPDATE_BUTTON).equalsIgnoreCase("YES"))
        {
            getSupportActionBar().setTitle("Update Retailer");

            updateRetailer_Button.setVisibility(View.VISIBLE);
            selectOrderType_Button.setVisibility(View.GONE);
        }
        else
        {
            getSupportActionBar().setTitle("New sales Order");
            updateRetailer_Button.setVisibility(View.GONE);
            selectOrderType_Button.setVisibility(View.VISIBLE);
        }
    }

    private void setListenersToViews()
    {

        //onclick on retaile entry
        shopName_AutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {

                HashMap<String, String> hashMap = (HashMap<String, String>) parent.getAdapter().getItem(position);
                shopName_AutoCompleteTextView.setText(hashMap.get("retailer_name"));
                isItemClicked = true;

                retailerID = hashMap.get("retailer_id").split(",")[0];
                mobileRetailerID = hashMap.get("retailer_id").split(",")[1];
                uploadStatus = hashMap.get("retailer_id").split(",")[2];
                //retailerID = parent.getItemAtPosition(position).toString().split(SEPARATOR)[0];
                showRetailerDetails(retailerID);

            }
        });

        //if retailer name edited
        shopName_AutoCompleteTextView.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                isItemClicked = false;
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                if (s.length() > 2)
                {
                    if (!shopName_AutoCompleteTextView.isPopupShowing())
                    {
                        Toast.makeText(getApplicationContext(), "No retailers found", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });

        //onclick on create order

        selectOrderType_Button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (isShopNameValid())
                {
                    launchSelectSalesOrderTypeActivity();
                }
                else
                {
                    Utils.showErrorDialog(PickRetailerActivity.this, "Invalid shop name. Please select valid name from dropdown");
                }

            }
        });

        //onclick on update retailer

        updateRetailer_Button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {


                if (locationTrack.canGetLocation())
                {


                    longitude = String.valueOf(locationTrack.getLongitude());
                    latitude = String.valueOf(locationTrack.getLatitude());

                    lat = String.valueOf(latitude);
                    lng = String.valueOf(longitude);


                    dispatchTakePictureIntent();

                }
                else
                {

                    locationTrack.showSettingsAlert();
                }

            }
        });
    }

    //retailer validation
    boolean isShopNameValid()
    {
        if (isItemClicked)
        {
            if (shopName_AutoCompleteTextView.getText().toString().trim().isEmpty())
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        else
        {
            return false;
        }
    }

    //launch select order type screen
    private void launchSelectSalesOrderTypeActivity()
    {

        final String intentExtraKey_TabToShow = getResources().getString(R.string.key_tab_to_show);

        Intent intent = new Intent(this, SelectSalesOrderTypeActivity.class);
        intent.putExtra(INTENT_EXTRA_RETAILER_NAME, retailerName);
        intent.putExtra(INTENT_EXTRA_RETAILER_ID, retailerID);
        intent.putExtra(INTENT_EXTRA_MOBILE_RETAILER_ID, mobileRetailerID);
        intent.putExtra(INTENT_EXTRA_UPLOAD_STATUS, uploadStatus);
        intent.putExtra(intentExtraKey_TabToShow, ConstantsA.All_SKUs_TAB);
        startActivity(intent);

    }

    //display retailer details
    void showRetailerDetails(String retailerID)
    {
        //in local db shopname=owner name


        String SQL_SELECT_RETAILERS = "select * from " + TBL_RETAILER + " where retailer_id=?";
        String[] selectionArgs = new String[]{retailerID};
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_RETAILERS, selectionArgs);

        if (cursor.moveToFirst())
        {
            String img_source = cursor.getString(cursor.getColumnIndexOrThrow("img_source"));
            retailerName = cursor.getString(cursor.getColumnIndexOrThrow("retailer_name"));
            String ownername = cursor.getString(cursor.getColumnIndexOrThrow("shop_name"));
            String mobileNo = cursor.getString(cursor.getColumnIndexOrThrow("mobile_no"));
            String latitude = cursor.getString(cursor.getColumnIndexOrThrow("latitude"));
            String longitude = cursor.getString(cursor.getColumnIndexOrThrow("longitude"));


            if (ownername == null)
            {

                ownername = EMPTY;
            }

            if (latitude == null)
            {


                latitude = EMPTY;
                longitude = EMPTY;

            }
            else
            {

                if (latitude.equalsIgnoreCase("null"))
                {

                    latitude = EMPTY;
                    longitude = EMPTY;
                }

                if (mobileNo.equalsIgnoreCase("0"))
                {

                    mobileNo = EMPTY;

                }

            }


            retailerName_TextView.setText("Retailer Name: " + retailerName);
            retailerOwnerName_TextView.setText("Owner Name: " + ownername);
            retailerMobile_TextView.setText("Retailer Mobile: " + mobileNo);
            latitude_TextView.setText("Latitude: " + latitude);
            longitude_TextView.setText("Longitude: " + longitude);

            if (!img_source.equals(NOT_PRESENT))
            {
                // Bitmap imageBitmap = BitmapFactory.decodeFile(img_source);
                //  retailerPhoto_ImageView.setImageBitmap(imageBitmap);

                Glide.with(PickRetailerActivity.this).load(img_source).diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true).signature(new StringSignature(String.valueOf(System.currentTimeMillis() / (24 * 60 * 60 * 1000)))).into(retailerPhoto_ImageView);
            }
            else
            {
                retailerPhoto_ImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_nia));
            }
        }
        else
        {
            Utils.showToast(this, "No Data Found");
        }

        cursor.close();
    }


    //get image base 64 of shop photo
    String getImagePath()
    {
        //if photo is not captured or capturing failed
        if (imageFile == null)
        {
            return NOT_PRESENT;
        }
        else
        {

            String filestring = imageFile.getAbsolutePath();


            if (Utils.isNetworkConnected(getBaseContext()))
            {


                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;

                Bitmap bm = BitmapFactory.decodeFile(filestring, options);
                // Bitmap adjustedBitmap = Bitmap.createBitmap(bm, 0, 0, 150, 100, matrix, true);
                Log.e("bitmap", bm + "");
                ByteArrayOutputStream bao = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 50, bao);
                byte[] ba = bao.toByteArray();
                //Converting bitmap into Base64String
                String ba1 = Base64.encodeToString(ba, Base64.DEFAULT);
                Log.e("base64image", ba1);
                return ba1;
            }
            else
            {

                return filestring;
            }


            //return imageFile.getAbsolutePath();
            //return returnimage;
        }
    }


    //create file for shop photo
    private File createImageFile() throws IOException
    {
        String imageFileName = "UPD_RET_IMG_" + getTodayDate() + getRandomNumber();

        File photoDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        Log.e("photoDir", photoDir.toString());

        File image = File.createTempFile(imageFileName, ".jpg", photoDir);

        return image;
    }


    //open camera intent for full size photo

    protected void dispatchTakePictureIntent()
    {
        Uri photoURI;

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try
        {
            imageFile = createImageFile();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }

        if (imageFile != null)
        {
            photoURI = FileProvider.getUriForFile(getBaseContext(), BuildConfig.APPLICATION_ID + ".provider", imageFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    //updating retailer table

    private void updateRetailerInDB(String retailerID, String mobileRetailerID, String latitude, String longitude, String imgPath, String uploadStatus)
    {
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        ContentValues retailerValues = new ContentValues();

        retailerValues.put("latitude", latitude);
        retailerValues.put("longitude", longitude);
        retailerValues.put("img_source", imgPath);
        retailerValues.put("modified_date", Utils.getDateTime());

        if (uploadStatus.equalsIgnoreCase("0"))
        {

            retailerValues.put("modified_by", new MySharedPrefrencesData().getUser_Id(PickRetailerActivity.this));

        }
        else
        {

            retailerValues.put("modified_by", new MySharedPrefrencesData().getUser_Id(PickRetailerActivity.this) + "_" + uploadStatus);
        }

        sqLiteDatabase.update(TBL_RETAILER, retailerValues, "retailer_id = ?  AND mobile_retailer_id = ?", new String[]{retailerID, mobileRetailerID});

        sqLiteDatabase.close();

        //   Glide.with(PickRetailerActivity.this).load(imgPath).diskCacheStrategy(DiskCacheStrategy.NONE).into(retailerPhoto_ImageView);

        //showRetailerDetails(retailerID);
    }

    private void buildGoogleApiClient()
    {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onStart()
    {
        //connectGoogleApiClient();
        super.onStart();
    }


    @Override
    protected void onStop()
    {
        // disConnectGoogleApiClient();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle)
    {
        locationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(1000);
        // LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, locationListener);
    }

    @Override
    public void onConnectionSuspended(int i)
    {
        connectGoogleApiClient();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        connectGoogleApiClient();
    }

    private void connectGoogleApiClient()
    {
        if (!googleApiClient.isConnected() || !googleApiClient.isConnecting())
        {
            googleApiClient.connect();
        }
    }

    private void disConnectGoogleApiClient()
    {
        if (googleApiClient.isConnected() || googleApiClient.isConnecting())
        {
            googleApiClient.disconnect();
        }
    }
}
