package in.etaminepgg.sfa.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
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
import in.etaminepgg.sfa.Utilities.MyDb;
import in.etaminepgg.sfa.Utilities.MySharedPrefrencesData;
import in.etaminepgg.sfa.Utilities.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.os.Environment.getExternalStoragePublicDirectory;
import static in.etaminepgg.sfa.Utilities.Constants.REQUEST_TURN_ON_LOCATION;
import static in.etaminepgg.sfa.Utilities.Constants.SHOW_UPDATE_BUTTON;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_RETAILER;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SKU_NO_ORDERREASON;
import static in.etaminepgg.sfa.Utilities.Constants.dbFileFullPath;
import static in.etaminepgg.sfa.Utilities.ConstantsA.EMPTY;
import static in.etaminepgg.sfa.Utilities.ConstantsA.INTENT_EXTRA_RETAILER_ID;
import static in.etaminepgg.sfa.Utilities.ConstantsA.INTENT_EXTRA_RETAILER_NAME;
import static in.etaminepgg.sfa.Utilities.ConstantsA.NOT_PRESENT;
import static in.etaminepgg.sfa.Utilities.ConstantsA.NO_INTERNET_CONNECTION;
import static in.etaminepgg.sfa.Utilities.Utils.getRandomNumber;
import static in.etaminepgg.sfa.Utilities.Utils.getTodayDate;

public class PickRetailerActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
    private static final int REQUEST_IMAGE_CAPTURE = 100;
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
    MySharedPrefrencesData mySharedPrefrencesData;
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
        setContentView(R.layout.activity_pick_retailer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        buildGoogleApiClient();

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
        SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), getCustomretailerList(), android.R.layout.simple_dropdown_item_1line, from, to);


        // ArrayAdapter<String> shopNamesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getRetailerNames());
        shopName_AutoCompleteTextView.setAdapter(adapter);
    }

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

    private List<HashMap<String, String>> getCustomretailerList()
    {

        // Each row in the list stores country name, currency and flag
        List<HashMap<String, String>> shopsInfoList = new ArrayList<HashMap<String, String>>();


        //List<String> shopsInfoList = new ArrayList<>();
        String SQL_SELECT_RETAILERS = "select retailer_id, retailer_name from " + TBL_RETAILER;
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_RETAILERS, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast())
        {
            String retailerID = cursor.getString(cursor.getColumnIndexOrThrow("retailer_id"));
            String retailer_name = cursor.getString(cursor.getColumnIndexOrThrow("retailer_name"));

            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put("retailer_name", retailer_name);
            hm.put("retailer_id", retailerID);
            shopsInfoList.add(hm);
            cursor.moveToNext();
        }
        cursor.close();
        return shopsInfoList;
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


            if (Utils.isNetworkConnected(PickRetailerActivity.this))
            {

                network_call_for_PutRetailerInfo(new MySharedPrefrencesData().getEmployee_AuthKey(PickRetailerActivity.this), retailerID, latitude, longitude, getImagePath());

            }
            else
            {

                updateRetailerInDB(retailerID, latitude, longitude, getImagePath());
                Utils.showSuccessDialog(PickRetailerActivity.this, "Retailer updated successfully.");
            }


        }
        else if (requestCode == REQUEST_TURN_ON_LOCATION && resultCode == RESULT_OK)
        {

        }
        else if (requestCode == REQUEST_TURN_ON_LOCATION && resultCode == RESULT_CANCELED)
        {
            Utils.showErrorDialog(this, "Updating Retailer failed. You must turn on GPS.");
        }
    }

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

                            updateRetailerInDB(retailerID, latitude, longitude, updateRetailerpictureModel.getCustomerPicturePath());
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
        shopName_AutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {

                HashMap<String, String> hashMap = (HashMap<String, String>) parent.getAdapter().getItem(position);
                shopName_AutoCompleteTextView.setText(hashMap.get("retailer_name"));
                isItemClicked = true;

                retailerID = hashMap.get("retailer_id");
                //retailerID = parent.getItemAtPosition(position).toString().split(SEPARATOR)[0];
                showRetailerDetails(retailerID);

            }
        });

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

            }
        });

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

                /*if (isItemClicked)
                {
                    if (shopName_AutoCompleteTextView.getText().toString().trim().isEmpty())
                    {
                        Utils.showPopUp(getBaseContext(), "Please Enter Retailer Shop Name");
                    }
                    else
                    {
                         launchSelectSalesOrderTypeActivity();
                    }
                }
                else
                {
                    Utils.showPopUp(getBaseContext(), "Invalid shop name. Please select shop name from dropdown");
                }*/
            }
        });

        updateRetailer_Button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!Utils.isGpsEnabled(PickRetailerActivity.this))
                {
                    Utils.enableGPS(googleApiClient, locationRequest, PickRetailerActivity.this);
                }
                else
                {
                    if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    {
                        if (isShopNameValid())
                        {
                            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, locationListener);
                        }
                        else
                        {
                            Utils.showErrorDialog(PickRetailerActivity.this, "Invalid shop name. Please select valid name from dropdown");
                        }
                    }
                    else
                    {
                        ActivityCompat.requestPermissions(PickRetailerActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ACCESS_FINE_LOCATION);
                    }
                }
            }
        });
    }

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

    private void launchSelectSalesOrderTypeActivity()
    {
        Intent intent = new Intent(this, SelectSalesOrderTypeActivity.class);
        intent.putExtra(INTENT_EXTRA_RETAILER_NAME, retailerName);
        intent.putExtra(INTENT_EXTRA_RETAILER_ID, retailerID);
        startActivity(intent);

    }

    List<String> getRetailerNames()
    {
        List<String> shopsInfoList = new ArrayList<>();
        String SQL_SELECT_RETAILERS = "select retailer_id, retailer_name from " + TBL_RETAILER;
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_RETAILERS, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast())
        {
            String retailerID = cursor.getString(cursor.getColumnIndexOrThrow("retailer_id"));
            String retailer_name = cursor.getString(cursor.getColumnIndexOrThrow("retailer_name"));
            shopsInfoList.add(retailerID + SEPARATOR + retailer_name);
            cursor.moveToNext();
        }
        cursor.close();
        return shopsInfoList;
    }

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

            if(latitude==null){


                    latitude = EMPTY;
                    longitude = EMPTY;

            }else {

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

           /* Matrix matrix = new Matrix();
            try
            {
                ExifInterface exif = new ExifInterface(filestring);

                int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                int rotationInDegrees = exifToDegrees(rotation);
                if (rotation != 0f) {matrix.preRotate(rotationInDegrees);}

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }*/

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

    private File createImageFile() throws IOException
    {
        String imageFileName = "UPD_RET_IMG_" + getTodayDate() + getRandomNumber();

        File photoDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        Log.e("photoDir", photoDir.toString());

        File image = File.createTempFile(imageFileName, ".jpg", photoDir);

        return image;
    }

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
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            {
                photoURI = FileProvider.getUriForFile(PickRetailerActivity.this, BuildConfig.APPLICATION_ID + ".provider", imageFile);
            }
            else
            {
                photoURI = Uri.fromFile(imageFile);
            }

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void updateRetailerInDB(String retailerID, String latitude, String longitude, String imgPath)
    {
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        ContentValues retailerValues = new ContentValues();

        retailerValues.put("latitude", latitude);
        retailerValues.put("longitude", longitude);
        retailerValues.put("img_source", imgPath);
        retailerValues.put("modified_date", Utils.getDateTime());
        retailerValues.put("modified_by", new MySharedPrefrencesData().getUser_Id(PickRetailerActivity.this));

        sqLiteDatabase.update(TBL_RETAILER, retailerValues, "retailer_id = ?", new String[]{retailerID});

        sqLiteDatabase.close();


        Glide.with(PickRetailerActivity.this).load(imgPath).diskCacheStrategy(DiskCacheStrategy.NONE).into(retailerPhoto_ImageView);

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
        connectGoogleApiClient();
        super.onStart();
    }


    @Override
    protected void onStop()
    {
        disConnectGoogleApiClient();
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
