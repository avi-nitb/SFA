package in.etaminepgg.sfa.Activities;

import android.Manifest;
import android.app.AlertDialog;
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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

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
import in.etaminepgg.sfa.Utilities.DbUtils;
import in.etaminepgg.sfa.Utilities.MyDb;
import in.etaminepgg.sfa.Utilities.Utils;

import static android.os.Environment.getExternalStoragePublicDirectory;
import static in.etaminepgg.sfa.Utilities.Constants.REQUEST_TURN_ON_LOCATION;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_LOCATION_HIERARCHY;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_RETAILER;
import static in.etaminepgg.sfa.Utilities.Constants.dbFileFullPath;
import static in.etaminepgg.sfa.Utilities.Utils.getRandomNumber;
import static in.etaminepgg.sfa.Utilities.Utils.loggedInUserID;

public class CreateRetailerActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
    private static final String ADD_NEW_AREA = "add new area";
    private static final int REQUEST_IMAGE_CAPTURE = 111;
    private final int REQUEST_CODE_ACCESS_FINE_LOCATION = 11;
    EditText etRetailerName, etShopName, etShopAddress, etPincode, etMobNo, etEmail;
    Spinner spnState, spnDistrict, spnTaluk, spnArea;
    Button btnPhotoCapt, btnCreateNewRetailer;
    EditText enterNewArea_EditText;
    ArrayList<String> arrState;
    ArrayList<String> arrDist;
    ArrayList<String> arrCity;
    ArrayList<String> arrArea;
    ImageView ivPhotoPreview;
    File imageFile = null;
    ProgressDialog progressDialog = null;
    String lat, lng;
    private String retailerName, shopName, shopAddress, pincode, mobileNumber, email = "";
    private String stateName, districtName, talukName, areaName;
    private String stateID, districtID, talukID, areaID;
    private GoogleApiClient googleApiClient;
    LocationListener locationListener = new LocationListener()
    {
        @Override
        public void onLocationChanged(Location location)
        {
            //Log.i("Location", "changed");
            //Utils.showToast(getBaseContext(), "lat: " + location.getLatitude() + "lng: " + location.getLongitude());

            lat = String.valueOf(location.getLatitude());
            lng = String.valueOf(location.getLongitude());

            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, locationListener);

            dismissProgressDialog();

            Utils.showSuccessDialog(CreateRetailerActivity.this, "Retailer creation successful.");
        }
    };
    private LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_retailer);

        buildGoogleApiClient();

        retailerName = shopName = shopAddress = pincode = mobileNumber = email = "";

        etRetailerName = (EditText) findViewById(R.id.etRetailerName);
        etShopName = (EditText) findViewById(R.id.etShopName);
        etShopAddress = (EditText) findViewById(R.id.etShopAddress);
        etPincode = (EditText) findViewById(R.id.etPincode);
        etMobNo = (EditText) findViewById(R.id.etMobNo);
        etEmail = (EditText) findViewById(R.id.etEmail);

        spnState = (Spinner) findViewById(R.id.spnState);
        spnDistrict = (Spinner) findViewById(R.id.spnDistrict);
        spnTaluk = (Spinner) findViewById(R.id.spnTaluk);
        spnArea = (Spinner) findViewById(R.id.spnArea);

        btnPhotoCapt = (Button) findViewById(R.id.btnPhotoCapt);
        ivPhotoPreview = (ImageView) findViewById(R.id.ivPhotoPreview);
        btnCreateNewRetailer = (Button) findViewById(R.id.btnCreateNewRetailer);

        arrState = new ArrayList<>();
        arrDist = new ArrayList<>();
        arrCity = new ArrayList<>();
        arrArea = new ArrayList<>();

        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getStatesList());
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnState.setAdapter(stateAdapter);

        spnState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                stateName = spnState.getSelectedItem().toString();
                stateID = getLocationID(stateName);

                ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(CreateRetailerActivity.this, android.R.layout.simple_spinner_item, getDistrictsList(stateID));
                districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnDistrict.setAdapter(districtAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
            }
        });

        spnDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                districtName = spnDistrict.getSelectedItem().toString();
                districtID = getLocationID(districtName);

                ArrayAdapter<String> talukAdapter = new ArrayAdapter<>(CreateRetailerActivity.this, android.R.layout.simple_spinner_item, getTaluksList(districtID));
                talukAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnTaluk.setAdapter(talukAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
            }
        });

        spnTaluk.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                talukName = spnTaluk.getSelectedItem().toString();
                talukID = getLocationID(talukName);

                setAreaAdapter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        spnArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                areaName = spnArea.getSelectedItem().toString();
                areaID = getLocationID(areaName);

                if(areaName.equals(ADD_NEW_AREA))
                {
                    showAddNewAreaDialog();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        btnPhotoCapt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dispatchTakePictureIntent();
            }
        });

        btnCreateNewRetailer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(!Utils.isGpsEnabled(CreateRetailerActivity.this))
                {
                    Utils.enableGPS(googleApiClient, locationRequest, CreateRetailerActivity.this);
                }
                else
                {
                    createNewRetailer();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            Bitmap imageBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            ivPhotoPreview.setImageBitmap(imageBitmap);
        }
        else if(requestCode == REQUEST_TURN_ON_LOCATION && resultCode == RESULT_OK)
        {
            createNewRetailer();
        }
        else if(requestCode == REQUEST_TURN_ON_LOCATION && resultCode == RESULT_CANCELED)
        {
            Utils.showErrorDialog(this, "Retailer creation failed. You must turn on GPS.");
        }
    }

    void createNewRetailer()
    {
        captureFormData();

        if(hasEnteredValidData())
        {
            if(ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                startProgressDialog();
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, locationListener);
            }
            else
            {
                ActivityCompat.requestPermissions(CreateRetailerActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ACCESS_FINE_LOCATION);
                //LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, locationListener);
            }

            insertIntoRetailers(loggedInUserID, retailerName, shopName, shopAddress, pincode,
                    mobileNumber, email, areaID, lat, lng, getImagePath());

            // clearFormData();
        }
    }

    String getImagePath()
    {
        //if photo is not captured
        if(imageFile == null)
        {
            return "";
        }
        else
        {
            return imageFile.getAbsolutePath();
        }
    }

    void showAddNewAreaDialog()
    {
        enterNewArea_EditText = new EditText(this);
        enterNewArea_EditText.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        enterNewArea_EditText.setHeight(120);

        AlertDialog.Builder addNewArea_AlertDialogBuilder = new AlertDialog.Builder(this);
        addNewArea_AlertDialogBuilder.setTitle("Enter New Area");
        addNewArea_AlertDialogBuilder.setView(enterNewArea_EditText);

        addNewArea_AlertDialogBuilder.setPositiveButton("ADD", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String areaEntered = enterNewArea_EditText.getText().toString().trim();

                if(areaEntered.isEmpty())
                {
                    Utils.showToast(getBaseContext(), "Area name can't be empty. Please enter valid area");
                }
                else
                {
                    int areaID = Utils.getRandomNumber();
                    DbUtils.insertIntoLocationHierarchy(areaID, areaEntered, "4", talukID);
                    setAreaAdapter();
                    Utils.showToast(getBaseContext(), "New Area Added Successfully");
                }
            }
        });

        addNewArea_AlertDialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();

                spnArea.setSelection(0);
            }
        });

        AlertDialog addNewArea_AlertDialog = addNewArea_AlertDialogBuilder.create();
        addNewArea_AlertDialog.setCanceledOnTouchOutside(false);
        addNewArea_AlertDialog.setCancelable(false);

        addNewArea_AlertDialog.show();
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
                photoURI = FileProvider.getUriForFile(CreateRetailerActivity.this, BuildConfig.APPLICATION_ID + ".provider", imageFile);
            }
            else
            {
                photoURI = Uri.fromFile(imageFile);
            }

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private File createImageFile() throws IOException
    {
        String imageFileName = "RET_IMG_" + Utils.getRandomNumber();

        File photoDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        Log.e("photoDir", photoDir.toString());

        File image = File.createTempFile(imageFileName, ".jpg", photoDir);

        return image;
    }

    private void captureFormData()
    {
        retailerName = etRetailerName.getText().toString().trim();
        shopName = etShopName.getText().toString().trim();
        shopAddress = etShopAddress.getText().toString().trim();
        pincode = etPincode.getText().toString().trim();
        mobileNumber = etMobNo.getText().toString().trim();
        email = etEmail.getText().toString().trim();

        /*stateName = spnState.getSelectedItem().toString();
        districtName = spnDistrict.getSelectedItem().toString();
        talukName = spnTaluk.getSelectedItem().toString();
        areaName = spnArea.getSelectedItem().toString();*/
    }

    private void clearFormData()
    {
        etRetailerName.setText("");
        etShopName.setText("");
        etShopAddress.setText("");
        etPincode.setText("");
        etMobNo.setText("");
        etEmail.setText("");
        ivPhotoPreview.setImageDrawable(getResources().getDrawable(R.drawable.border_rect));
    }

    boolean hasEnteredValidData()
    {
        String msgInvalidEntries = "Invalid Entry Here. Please Enter Valid Data";
        int inValidEntriesCount = 0;

        if(retailerName.isEmpty())
        {
            etRetailerName.setError(msgInvalidEntries);
            inValidEntriesCount++;
        }
        if(shopName.isEmpty())
        {
            etShopName.setError(msgInvalidEntries);
            inValidEntriesCount++;
        }
        if(shopAddress.isEmpty())
        {
            etShopAddress.setError(msgInvalidEntries);
            inValidEntriesCount++;
        }
        if(pincode.isEmpty() || !pincode.matches("^[1-9][0-9]{5}$"))
        {
            etPincode.setError(msgInvalidEntries);
            inValidEntriesCount++;
        }
        if(mobileNumber.isEmpty() || !mobileNumber.matches("^[7-9][0-9]{9}$"))
        {
            etMobNo.setError(msgInvalidEntries);
            inValidEntriesCount++;
        }

        //entering email is optional.
        if(!email.isEmpty())
        {
            if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
            {
                etEmail.setError(msgInvalidEntries);
                inValidEntriesCount++;
            }
        }

        if(areaName.equals(ADD_NEW_AREA))
        {
            Utils.showPopUp(this, "Error: Select Valid Area");
            inValidEntriesCount++;
        }

        return inValidEntriesCount == 0;
    }

    private void startProgressDialog()
    {
        progressDialog = new ProgressDialog(CreateRetailerActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle("Capturing GPS Co-Ordinates");
        progressDialog.setMessage("Please Wait....");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressNumberFormat(null);
        progressDialog.setProgressPercentFormat(null);
        progressDialog.show();
    }

    private void dismissProgressDialog()
    {
        if(progressDialog != null)
        {
            progressDialog.dismiss();
        }
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

    List<String> getStatesList()
    {
        List<String> statesList = new ArrayList<>();

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String SQL_SELECT_STATES = "SELECT " + "loc_name" + " FROM " + TBL_LOCATION_HIERARCHY + " WHERE " + "parent_loc_id = ?";
        String[] selectionArgs = {"0"};

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_STATES, selectionArgs);

        while(cursor.moveToNext())
        {
            String stateName = cursor.getString(cursor.getColumnIndexOrThrow("loc_name"));
            statesList.add(stateName);
        }

        cursor.close();
        sqLiteDatabase.close();

        return statesList;
    }

    List<String> getDistrictsList(String stateID)
    {
        List<String> districtsList = new ArrayList<>();

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String SQL_SELECT_DISTRICTS = "SELECT " + "loc_name" + " FROM " + TBL_LOCATION_HIERARCHY + " WHERE " + "parent_loc_id = ?";
        String[] selectionArgs = {stateID};

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_DISTRICTS, selectionArgs);

        while(cursor.moveToNext())
        {
            String stateName = cursor.getString(cursor.getColumnIndexOrThrow("loc_name"));
            districtsList.add(stateName);
        }

        cursor.close();
        sqLiteDatabase.close();

        return districtsList;
    }

    List<String> getTaluksList(String districtID)
    {
        List<String> taluksList = new ArrayList<>();

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String SQL_SELECT_TALUKS = "SELECT " + "loc_name" + " FROM " + TBL_LOCATION_HIERARCHY + " WHERE " + "parent_loc_id = ?";
        String[] selectionArgs = {districtID};

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_TALUKS, selectionArgs);

        while(cursor.moveToNext())
        {
            String stateName = cursor.getString(cursor.getColumnIndexOrThrow("loc_name"));
            taluksList.add(stateName);
        }

        cursor.close();
        sqLiteDatabase.close();

        return taluksList;
    }

    List<String> getAreasList(String talukID)
    {
        List<String> areasList = new ArrayList<>();

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String SQL_SELECT_AREAS = "SELECT " + "loc_name" + " FROM " + TBL_LOCATION_HIERARCHY + " WHERE " + "parent_loc_id = ?";
        String[] selectionArgs = {talukID};

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_AREAS, selectionArgs);

        while(cursor.moveToNext())
        {
            String stateName = cursor.getString(cursor.getColumnIndexOrThrow("loc_name"));
            areasList.add(stateName);
        }

        areasList.add(ADD_NEW_AREA);

        cursor.close();
        sqLiteDatabase.close();

        return areasList;
    }

    String getLocationID(String locationName)
    {
        String locationID = null;

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String SQL_SELECT_LOCATION_ID = "SELECT " + "loc_id" + " FROM " + TBL_LOCATION_HIERARCHY + " WHERE " + "loc_name = ?";
        String[] selectionArgs = {locationName};

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_LOCATION_ID, selectionArgs);

        if(cursor.moveToFirst())
        {
            locationID = cursor.getString(cursor.getColumnIndexOrThrow("loc_id"));
        }

        cursor.close();
        sqLiteDatabase.close();

        return locationID;
    }

    private void insertIntoRetailers(String empID, String retailerName, String shopName, String shopAddress, String pincode,
                                     String mobileNumber, String email, String areaID, String latitude, String longitude, String imgPath)
    {
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        ContentValues retailerValues = new ContentValues();
        retailerValues.put("retailer_id", "RET_ID_" + getRandomNumber());
        retailerValues.put("emp_id", empID);
        retailerValues.put("retailer_name", retailerName);
        retailerValues.put("shop_name", shopName);
        retailerValues.put("shop_address", shopAddress);
        retailerValues.put("pincode", pincode);
        retailerValues.put("mobile_no", mobileNumber);
        retailerValues.put("email", email);
        retailerValues.put("area_id", areaID);
        retailerValues.put("latitude", latitude);
        retailerValues.put("longitude", longitude);
        retailerValues.put("img_source", imgPath);
        retailerValues.put("created_date", Utils.getDateTime());
        retailerValues.put("modified_date", Utils.getDateTime());
        retailerValues.put("landmark", "landmark");
        retailerValues.put("area", "area");
        retailerValues.put("taluk", "taluk");
        retailerValues.put("district", "district");
        retailerValues.put("state", "state");
        retailerValues.put("upload_status", 0);
        sqLiteDatabase.insert(TBL_RETAILER, null, retailerValues);

        sqLiteDatabase.close();
    }

    void setAreaAdapter()
    {
        ArrayAdapter<String> areaAdapter = new ArrayAdapter<>(CreateRetailerActivity.this, android.R.layout.simple_spinner_item, getAreasList(talukID));
        areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnArea.setAdapter(areaAdapter);
    }
}
