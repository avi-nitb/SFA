package in.etaminepgg.sfa.Activities;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import in.etaminepgg.sfa.BuildConfig;
import in.etaminepgg.sfa.R;
import in.etaminepgg.sfa.Utilities.MyDb;
import in.etaminepgg.sfa.Utilities.Utils;

import static android.os.Environment.getExternalStoragePublicDirectory;
import static in.etaminepgg.sfa.Utilities.Constants.REQUEST_TURN_ON_LOCATION;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_RETAILER;
import static in.etaminepgg.sfa.Utilities.Constants.dbFileFullPath;
import static in.etaminepgg.sfa.Utilities.ConstantsA.INTENT_EXTRA_RETAILER_ID;
import static in.etaminepgg.sfa.Utilities.ConstantsA.INTENT_EXTRA_RETAILER_NAME;
import static in.etaminepgg.sfa.Utilities.ConstantsA.NOT_PRESENT;
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
    TextView retailerName_TextView, retailerMobile_TextView, img_TextView, latitude_TextView, longitude_TextView;
    ImageView retailerPhoto_ImageView;
    boolean isItemClicked = false;
    String retailerName;
    String retailerID;
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

            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, locationListener);

            dispatchTakePictureIntent();

            //dismissProgressDialog();
        }
    };
    private LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_retailer);

        buildGoogleApiClient();

        findViewsByIDs();
        setListenersToViews();

        shopName_AutoCompleteTextView.setThreshold(THRESHOLD);
        ArrayAdapter<String> shopNamesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getShopNames());
        shopName_AutoCompleteTextView.setAdapter(shopNamesAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            Bitmap imageBitmap = BitmapFactory.decodeFile(getImagePath());
            retailerPhoto_ImageView.setImageBitmap(imageBitmap);

            latitude_TextView.setText("Latitude: " + latitude);

            longitude_TextView.setText("Longitude: " + longitude);

            updateRetailerInDB(retailerID, latitude, longitude, getImagePath());

            Utils.showSuccessDialog(PickRetailerActivity.this, "Retailer updated successfully.");
        }
        else if(requestCode == REQUEST_TURN_ON_LOCATION && resultCode == RESULT_OK)
        {
            //  createNewRetailer();
        }
        else if(requestCode == REQUEST_TURN_ON_LOCATION && resultCode == RESULT_CANCELED)
        {
            Utils.showErrorDialog(this, "Updating Retailer failed. You must turn on GPS.");
        }
    }

    private void findViewsByIDs()
    {
        shopName_AutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.shopName_AutoCompleteTextView);
        retailerName_TextView = (TextView) findViewById(R.id.retailerName_TextView);
        retailerMobile_TextView = (TextView) findViewById(R.id.retailerMobile_TextView);
        img_TextView = (TextView) findViewById(R.id.img_TextView);
        retailerPhoto_ImageView = (ImageView) findViewById(R.id.retailerPhoto_ImageView);
        latitude_TextView = (TextView) findViewById(R.id.latitude_TextView);
        longitude_TextView = (TextView) findViewById(R.id.longitude_TextView);
        selectOrderType_Button = (Button) findViewById(R.id.selectOrderType_Button);
        updateRetailer_Button = (Button) findViewById(R.id.updateRetailer_Button);
    }

    private void setListenersToViews()
    {
        shopName_AutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {

                isItemClicked = true;

                retailerID = parent.getItemAtPosition(position).toString().split(SEPARATOR)[0];
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
                if(isShopNameValid())
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
                if(!Utils.isGpsEnabled(PickRetailerActivity.this))
                {
                    Utils.enableGPS(googleApiClient, locationRequest, PickRetailerActivity.this);
                }
                else
                {
                    if(ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    {
                        if(isShopNameValid())
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
        if(isItemClicked)
        {
            if(shopName_AutoCompleteTextView.getText().toString().trim().isEmpty())
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

    List<String> getShopNames()
    {
        List<String> shopsInfoList = new ArrayList<>();
        String SQL_SELECT_RETAILERS = "select retailer_id, shop_name from " + TBL_RETAILER;
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_RETAILERS, null);
        cursor.moveToFirst();

        while(!cursor.isAfterLast())
        {
            String retailerID = cursor.getString(cursor.getColumnIndexOrThrow("retailer_id"));
            String shopName = cursor.getString(cursor.getColumnIndexOrThrow("shop_name"));
            shopsInfoList.add(retailerID + SEPARATOR + shopName);
            cursor.moveToNext();
        }
        cursor.close();
        return shopsInfoList;
    }

    void showRetailerDetails(String retailerID)
    {
        String SQL_SELECT_RETAILERS = "select * from " + TBL_RETAILER + " where retailer_id=?";
        String[] selectionArgs = new String[]{retailerID};
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_RETAILERS, selectionArgs);

        if(cursor.moveToFirst())
        {
            String img_source = cursor.getString(cursor.getColumnIndexOrThrow("img_source"));
            retailerName = cursor.getString(cursor.getColumnIndexOrThrow("retailer_name"));
            String mobileNo = cursor.getString(cursor.getColumnIndexOrThrow("mobile_no"));
            String latitude = cursor.getString(cursor.getColumnIndexOrThrow("latitude"));
            String longitude = cursor.getString(cursor.getColumnIndexOrThrow("longitude"));

            retailerName_TextView.setText("Retailer Name: " + retailerName);
            retailerMobile_TextView.setText("Retailer Mobile: " + mobileNo);
            latitude_TextView.setText("Latitude: " + latitude);
            longitude_TextView.setText("Longitude: " + longitude);

            if(!img_source.equals(NOT_PRESENT))
            {
                Bitmap imageBitmap = BitmapFactory.decodeFile(img_source);
                retailerPhoto_ImageView.setImageBitmap(imageBitmap);
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
        if(imageFile == null)
        {
            return NOT_PRESENT;
        }
        else
        {
            return imageFile.getAbsolutePath();
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
        catch(IOException ex)
        {
            ex.printStackTrace();
        }

        if(imageFile != null)
        {
            if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
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

        sqLiteDatabase.update(TBL_RETAILER, retailerValues, "retailer_id = ?", new String[]{retailerID});

        sqLiteDatabase.close();
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
        if(!googleApiClient.isConnected() || !googleApiClient.isConnecting())
        {
            googleApiClient.connect();
        }
    }

    private void disConnectGoogleApiClient()
    {
        if(googleApiClient.isConnected() || googleApiClient.isConnecting())
        {
            googleApiClient.disconnect();
        }
    }
}
