package in.etaminepgg.sfa.Activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import in.etaminepgg.sfa.BuildConfig;
import in.etaminepgg.sfa.InputModel_For_Network.IM_CreateRetailer;
import in.etaminepgg.sfa.InputModel_For_Network.IM_PutNewArea;
import in.etaminepgg.sfa.Models.CreateretaileModel;
import in.etaminepgg.sfa.Models.PutNewArea;
import in.etaminepgg.sfa.Network.API_Call_Retrofit;
import in.etaminepgg.sfa.Network.Apimethods;
import in.etaminepgg.sfa.R;
import in.etaminepgg.sfa.Utilities.Constants;
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
import static in.etaminepgg.sfa.Utilities.Constants.TBL_LOCATION_HIERARCHY;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_RETAILER;
import static in.etaminepgg.sfa.Utilities.Constants.dbFileFullPath;
import static in.etaminepgg.sfa.Utilities.ConstantsA.NOT_PRESENT;
import static in.etaminepgg.sfa.Utilities.Utils.getDateTime;
import static in.etaminepgg.sfa.Utilities.Utils.getIST;
import static in.etaminepgg.sfa.Utilities.Utils.getRandomNumber;
import static in.etaminepgg.sfa.Utilities.Utils.loggedInUserID;

public class CreateRetailerActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
    private static final String ADD_NEW_AREA = "add new area";
    private static final int REQUEST_IMAGE_CAPTURE = 111;
    private static final int BCR_REQUEST_IMAGE_CAPTURE = 112;
    private static final String LOG_TAG = "CREATE RETAILER";
    private static final int PHOTO_REQUEST = 10;
    private static final int PHOTO_REQUEST_BACK = 15;
    private static final int REQUEST_WRITE_PERMISSION = 20;
    private static final int REQUEST_WRITE_PERMISSION_BACK = 21;
    private static final String SAVED_INSTANCE_URI = "uri";
    private static final String SAVED_INSTANCE_RESULT = "result";
    private final static int ALL_PERMISSIONS_RESULT_CreateRetailer = 789;
    private final int REQUEST_CODE_ACCESS_FINE_LOCATION = 11;
    EditText etRetailerName, etOwnerName, etShopAddress, etPincode, etMobNo, etEmail;
    Spinner spnState, spnDistrict, spnTaluk, spnArea;
    Button btnPhotoCapt, btnCreateNewRetailer, btn_bcr_txt, btn_bcr_txt_back;
    EditText enterNewArea_EditText;
    ArrayList<String> arrState;
    ArrayList<String> arrDist;
    ArrayList<String> arrCity;
    ArrayList<String> arrArea;
    ImageView ivPhotoPreview;
    File imageFile = null;
    File imageFile_BCR = null;
    ProgressDialog progressDialog = null;
    String lat, lng;
    ArrayList<File> imagecount = new ArrayList<>();
    LocationTrack locationTrack;
    private String retailerName, ownerName, shopAddress, pincode, mobileNumber, email = "";
    private String stateName, districtName, talukName, areaName;
    private String stateID, districtID, talukID, areaID;
    private GoogleApiClient googleApiClient;
    private Uri imageUri, imageuriback;
    private TextRecognizer detector;
    private TextView tv_bcr_text, tv_bcr_photo_count, tv_ocr_back, tv_area_header, retailer_upldcount;
    private ImageView iv_bcr_photo;
    private Button btn_bcr_photo;
    private Toolbar toolbar;
    private RadioGroup rg_location;
    LocationListener locationListener = new LocationListener()
    {
        @Override
        public void onLocationChanged(Location location)
        {
            //Log.i("Location", "changed");

            lat = String.valueOf(location.getLatitude());
            lng = String.valueOf(location.getLongitude());
            Utils.showToast(getBaseContext(), "lat: " + location.getLatitude() + "lng: " + location.getLongitude());

            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, locationListener);

            dismissProgressDialog();

            insertIntoRetailers("", String.valueOf("RETAILER_" + getIST() + "_" + getRandomNumber()), loggedInUserID, retailerName, ownerName, shopAddress, pincode, mobileNumber, email, areaID, lat, lng, getImagePath(), tv_bcr_text.getText().toString(), tv_ocr_back.getText().toString(), getBCRPhotoPathList(), 0);
            Utils.showSuccessDialogForUpload(CreateRetailerActivity.this, "Retailer information saved, Do you want to Upload ?");
            clearFormData();

        }
    };
    private LocationRequest locationRequest;
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();

    //api call for create retailer
    private void networkcall_for_createretailer()
    {

        Apimethods apimethods = API_Call_Retrofit.getretrofit(CreateRetailerActivity.this).create(Apimethods.class);


        IM_CreateRetailer.RetailerData retailerData = new IM_CreateRetailer().new RetailerData(retailerName, ownerName, "Retailers", pincode, mobileNumber, email, lat + "," + lng, areaID, getDateTime(), new MySharedPrefrencesData().getUser_Id(CreateRetailerActivity.this), getImagePath(), shopAddress, tv_bcr_text.getText().toString(), tv_ocr_back.getText().toString(), getBCRPhotoPathList());

        IM_CreateRetailer createRetailer = new IM_CreateRetailer(new MySharedPrefrencesData().getEmployee_AuthKey(CreateRetailerActivity.this), retailerData);

        Call<CreateretaileModel> call = apimethods.createretailer(createRetailer);

        Log.i("create_rtlr_ip", new Gson().toJson(createRetailer));


        call.enqueue(new Callback<CreateretaileModel>()
        {
            @Override
            public void onResponse(Call<CreateretaileModel> call, Response<CreateretaileModel> response)
            {

                if (response.isSuccessful())
                {

                    CreateretaileModel createretaileModel = response.body();

                    Log.i("create_rtlr_op", new Gson().toJson(createretaileModel));

                    if (createretaileModel.getApiStatus() == 1)
                    {

                        insertIntoRetailers(createretaileModel.getCustomerCode(), String.valueOf("RETAILER_" + getIST() + "_" + getRandomNumber()), loggedInUserID, retailerName, ownerName, shopAddress, pincode,
                                mobileNumber, email, areaID, lat, lng, createretaileModel.getCustomer_picture_path(), tv_bcr_text.getText().toString(), tv_ocr_back.getText().toString(), getBCRPhotoPathList(), 1);
                        dismissProgressDialog();
                        Utils.dismissProgressDialog(progressDialog);
                        Utils.showSuccessDialog(CreateRetailerActivity.this, "Retailer creation successful.");


                    }
                    clearFormData();
                }
                else
                {
                    dismissProgressDialog();
                    Utils.dismissProgressDialog(progressDialog);
                    Utils.showErrorDialog(CreateRetailerActivity.this, "Retailer creation unsuccessful.");
                }

            }

            @Override
            public void onFailure(Call<CreateretaileModel> call, Throwable t)
            {
                dismissProgressDialog();
                Utils.dismissProgressDialog(progressDialog);
                Utils.showToast(CreateRetailerActivity.this, ConstantsA.NO_INTERNET_CONNECTION);
            }
        });


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
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_create_retailer);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create New Retailer");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //buildGoogleApiClient();
        progressDialog = new ProgressDialog(CreateRetailerActivity.this);

        retailerName = ownerName = shopAddress = pincode = mobileNumber = email = "";

        etRetailerName = (EditText) findViewById(R.id.etRetailerName);
        etOwnerName = (EditText) findViewById(R.id.eOwnerName);
        etShopAddress = (EditText) findViewById(R.id.etShopAddress);
        etPincode = (EditText) findViewById(R.id.etPincode);
        etMobNo = (EditText) findViewById(R.id.etMobNo);
        etEmail = (EditText) findViewById(R.id.etEmail);

        spnState = (Spinner) findViewById(R.id.spnState);
        spnDistrict = (Spinner) findViewById(R.id.spnDistrict);
        spnTaluk = (Spinner) findViewById(R.id.spnTaluk);
        spnArea = (Spinner) findViewById(R.id.spnArea);

        btnPhotoCapt = (Button) findViewById(R.id.btnPhotoCapt);
        btn_bcr_txt = (Button) findViewById(R.id.btn_bcr_txt);
        btn_bcr_txt_back = (Button) findViewById(R.id.btn_bcr_txt_back);
        ivPhotoPreview = (ImageView) findViewById(R.id.ivPhotoPreview);
        btnCreateNewRetailer = (Button) findViewById(R.id.btnCreateNewRetailer);


        tv_bcr_text = (TextView) findViewById(R.id.tv_ocr);
        tv_ocr_back = (TextView) findViewById(R.id.tv_ocr_back);


        retailer_upldcount = (TextView) findViewById(R.id.retailer_upldcount);


        tv_bcr_photo_count = (TextView) findViewById(R.id.bcr_count);

        btn_bcr_photo = (Button) findViewById(R.id.btn_bcr_photo);

        iv_bcr_photo = (ImageView) findViewById(R.id.iv_bcr_photo);

        rg_location = (RadioGroup) findViewById(R.id.rg_location);

        rg_location.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override

            public void onCheckedChanged(RadioGroup group, int checkedId)
            {

                // find which radio button is selected

                if (checkedId == R.id.rb_yes)
                {


                }
                else if (checkedId == R.id.rb_no)
                {


                }

            }

        });

        retailer_upldcount.setText("No. of Retailers to Upload: " + DbUtils.getRetailerCountForUpload(CreateRetailerActivity.this));


        arrState = new ArrayList<>();
        arrDist = new ArrayList<>();
        arrCity = new ArrayList<>();
        arrArea = new ArrayList<>();


        //state list for state dropdown

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

                ArrayAdapter<String> talukAdapter = new ArrayAdapter<>(CreateRetailerActivity.this, android.R.layout.simple_spinner_item, getAreasList(districtID));
                talukAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnArea.setAdapter(talukAdapter);
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

                if (areaName.equals(ADD_NEW_AREA))
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

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    String[] permissionsArray = new String[]{"android.permission.CAMERA"};
                    try
                    {
                        if (ContextCompat.checkSelfPermission(CreateRetailerActivity.this, "android.permission.CAMERA") != PackageManager.PERMISSION_GRANTED)
                        {
                            ActivityCompat.requestPermissions(CreateRetailerActivity.this, permissionsArray, 1);

                        }
                        else if (ContextCompat.checkSelfPermission(CreateRetailerActivity.this, "android.permission.CAMERA") == PackageManager.PERMISSION_DENIED)
                        {
                            ActivityCompat.shouldShowRequestPermissionRationale(CreateRetailerActivity.this, "android.permission.CAMERA");

                        }
                        else
                        {

                            dispatchTakePictureIntent(Constants.FLAG_RETAILER_PHOTO);
                        }
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
                else
                {

                    dispatchTakePictureIntent(Constants.FLAG_RETAILER_PHOTO);

                }

            }
        });

        btnCreateNewRetailer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                createNewRetailer();

            }
        });

        detector = new TextRecognizer.Builder(getApplicationContext()).build();


        // TODO: Check if the TextRecognizer is operational.
        if (!detector.isOperational())
        {
            Log.w(LOG_TAG, "Detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage)
            {
                Toast.makeText(this, R.string.low_storage_error, Toast.LENGTH_LONG).show();
                Log.w(LOG_TAG, getString(R.string.low_storage_error));
            }
        }


        btn_bcr_txt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ActivityCompat.requestPermissions(CreateRetailerActivity.this, new
                        String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
            }
        });

        btn_bcr_txt_back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                ActivityCompat.requestPermissions(CreateRetailerActivity.this, new
                        String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION_BACK);
            }
        });


        btn_bcr_photo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    String[] permissionsArray = new String[]{"android.permission.CAMERA"};
                    try
                    {
                        if (ContextCompat.checkSelfPermission(CreateRetailerActivity.this, "android.permission.CAMERA") != PackageManager.PERMISSION_GRANTED)
                        {
                            ActivityCompat.requestPermissions(CreateRetailerActivity.this, permissionsArray, 1);

                        }
                        else if (ContextCompat.checkSelfPermission(CreateRetailerActivity.this, "android.permission.CAMERA") == PackageManager.PERMISSION_DENIED)
                        {
                            ActivityCompat.shouldShowRequestPermissionRationale(CreateRetailerActivity.this, "android.permission.CAMERA");

                        }
                        else
                        {

                            dispatchTakePictureIntent(Constants.FLAG_BCR_PHOTO);
                        }
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
                else
                {

                    dispatchTakePictureIntent(Constants.FLAG_BCR_PHOTO);
                }


            }
        });


        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);

        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (permissionsToRequest.size() > 0)
            {
                requestPermissions((String[]) permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT_CreateRetailer);
            }
        }

        locationTrack = new LocationTrack(CreateRetailerActivity.this);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //retailer shop photo capture
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {

            if (imageFile != null)
            {


                Glide.with(getBaseContext()).load(imageFile.getAbsolutePath()).into(ivPhotoPreview);
            }
        }
        //BCR  photo capture
        else if (requestCode == BCR_REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            removeError();


            if (imageFile_BCR != null)
            {

                Glide.with(getBaseContext()).load(imageFile_BCR.getAbsolutePath()).into(iv_bcr_photo);
            }
            Log.d("arraysize", imagecount.size() + "");
            tv_bcr_photo_count.setText(imagecount.size() + "");


        }
        //BCR front photo  scan
        else if (requestCode == PHOTO_REQUEST && resultCode == RESULT_OK)
        {

            removeError();
            launchMediaScanIntent(PHOTO_REQUEST);

            try
            {


                // for ocr text

                Bitmap bitmap = decodeBitmapUri(this, imageUri);

                Log.d("detector", "" + detector.isOperational());
                if (detector.isOperational() && bitmap != null)
                {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> textBlocks = detector.detect(frame);
                    String blocks = "";
                    String lines = "";
                    String words = "";
                    for (int index = 0; index < textBlocks.size(); index++)
                    {
                        //extract scanned text blocks here
                        TextBlock tBlock = textBlocks.valueAt(index);
                        blocks = blocks + tBlock.getValue() + "\n" + "\n";
                        for (Text line : tBlock.getComponents())
                        {
                            //extract scanned text lines here

                            lines = lines + line.getValue() + "\n";
                            for (Text element : line.getComponents())
                            {
                                //extract scanned text words here
                                words = words + element.getValue() + ", ";
                            }
                        }
                    }
                    if (textBlocks.size() == 0)
                    {
                        tv_bcr_text.setText("Scan Failed: Found nothing to scan");
                    }
                    else
                    {
                        Log.d("tv_card_front", lines);
                        tv_bcr_text.setVisibility(View.VISIBLE);
                        tv_bcr_text.setText(lines + "\n");

                    }
                }
                else
                {
                    tv_bcr_text.setText("Could not set up the detector!");
                }
            }
            catch (Exception e)
            {
                Toast.makeText(this, "Failed to load Image", Toast.LENGTH_SHORT)
                        .show();
                Log.e(LOG_TAG, e.toString());
            }
        }

        //BCR back photo scan
        else if (requestCode == PHOTO_REQUEST_BACK && resultCode == RESULT_OK)
        {
            removeError();

            launchMediaScanIntent(PHOTO_REQUEST_BACK);

            try
            {


                // for ocr text

                Bitmap bitmap = decodeBitmapUri(this, imageuriback);

                Log.d("detector", "" + detector.isOperational());
                if (detector.isOperational() && bitmap != null)
                {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> textBlocks = detector.detect(frame);
                    String blocks = "";
                    String lines = "";
                    String words = "";
                    for (int index = 0; index < textBlocks.size(); index++)
                    {
                        //extract scanned text blocks here
                        TextBlock tBlock = textBlocks.valueAt(index);
                        blocks = blocks + tBlock.getValue() + "\n" + "\n";
                        for (Text line : tBlock.getComponents())
                        {
                            //extract scanned text lines here

                            lines = lines + line.getValue() + "\n";
                            for (Text element : line.getComponents())
                            {
                                //extract scanned text words here
                                words = words + element.getValue() + ", ";
                            }
                        }
                    }
                    if (textBlocks.size() == 0)
                    {
                        tv_ocr_back.setText("Scan Failed: Found nothing to scan");
                    }
                    else
                    {
                        Log.d("tv_card_back", lines);
                        tv_ocr_back.setVisibility(View.VISIBLE);
                        tv_ocr_back.setText(lines + "\n");

                    }
                }
                else
                {
                    tv_ocr_back.setText("Could not set up the detector!");
                }
            }
            catch (Exception e)
            {
                Toast.makeText(this, "Failed to load Image", Toast.LENGTH_SHORT)
                        .show();
                Log.e(LOG_TAG, e.toString());
            }
        }
        //after gps location on from setting alert
        else if (requestCode == LocationTrack.GPS_ENABLE_REQUEST)
        {

            locationTrack = new LocationTrack(CreateRetailerActivity.this);
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


    // for OCR media intent
    private void launchMediaScanIntent(int requestcode)
    {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

        if (requestcode == PHOTO_REQUEST)
        {
            mediaScanIntent.setData(imageUri);
        }
        else
        {
            mediaScanIntent.setData(imageuriback);
        }
        this.sendBroadcast(mediaScanIntent);
    }

    private Bitmap decodeBitmapUri(Context ctx, Uri uri) throws FileNotFoundException
    {
        int targetW = 600;
        int targetH = 600;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(uri), null, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        return BitmapFactory.decodeStream(ctx.getContentResolver()
                .openInputStream(uri), null, bmOptions);
    }


    //create retailer after submit ...checking validation

    void createNewRetailer()
    {
        captureFormData();

        if (hasEnteredValidData())
        {


            //check retailer validation in same area with retailer name

            if (!DbUtils.retailerValidation(CreateRetailerActivity.this, retailerName, areaID))
            {

                RadioButton rb = (RadioButton) rg_location.findViewById(rg_location.getCheckedRadioButtonId());

                //validating users presence at retailer location

                if (rb == null)
                {

                    Utils.showToast(this, "Please confirm your presence at retailer location. ");

                }

                //if user present at retailer location
                else if (rg_location.getCheckedRadioButtonId() == R.id.rb_yes)
                {

                    if (locationTrack.canGetLocation())
                    {


                        lat = String.valueOf(locationTrack.getLongitude());
                        lng = String.valueOf(locationTrack.getLatitude());

                        insertIntoRetailers("", String.valueOf("RETAILER_" + getIST() + "_" + getRandomNumber()), loggedInUserID, retailerName, ownerName, shopAddress, pincode, mobileNumber, email, areaID, lat, lng, getImagePath(), tv_bcr_text.getText().toString(), tv_ocr_back.getText().toString(), getBCRPhotoPathList(), 0);
                        Utils.showSuccessDialogForUpload(CreateRetailerActivity.this, "Retailer information saved, Do you want to Upload ?");
                        clearFormData();


                    }
                    else
                    {
                        //location button on and gps not on
                        locationTrack.showSettingsAlert();
                    }

                }
                //if user not present at retailer location
                else if (rg_location.getCheckedRadioButtonId() == R.id.rb_no)
                {


                    insertIntoRetailers("", String.valueOf("RETAILER_" + getIST() + "_" + getRandomNumber()), loggedInUserID, retailerName, ownerName, shopAddress, pincode, mobileNumber, email, areaID, NOT_PRESENT, NOT_PRESENT, getImagePath(), tv_bcr_text.getText().toString(), tv_ocr_back.getText().toString(), getBCRPhotoPathList(), 0);
                    clearFormData();
                    Utils.showSuccessDialogForUpload(CreateRetailerActivity.this, "Retailer information saved, Do you want to Upload ?");


                }

            }
            else
            {

                Utils.showErrorDialog(CreateRetailerActivity.this, "Retailer exists in the selected area.");
                //clearFormData();

            }

        }
    }


    //get base 64 string for retailer photo
    String getImagePath()
    {
        //if photo is not captured
        if (imageFile == null)
        {
            return "";
        }
        else
        {
            String filestring = imageFile.getAbsolutePath();
            Bitmap bm = BitmapFactory.decodeFile(filestring);
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            if (bm != null)
            {
                bm.compress(Bitmap.CompressFormat.JPEG, 50, bao);
                byte[] ba = bao.toByteArray();
                //Converting bitmap into Base64String
                String ba1 = Base64.encodeToString(ba, Base64.DEFAULT);
                Log.e("base64image", ba1);
                return ba1;
            }
            else
            {
                return "";
            }
            // return imageFile.getAbsolutePath();

        }
    }


    //get base 64 string list for BCR photo

    ArrayList<String> getBCRPhotoPathList()
    {
        //if photo is not captured
        if (imagecount == null)
        {
            return null;
        }
        else
        {
            ArrayList<String> bcrphotolist = new ArrayList<>();

            for (File imagename : imagecount)
            {


                String filestring = imagename.getAbsolutePath();
                Bitmap bm = BitmapFactory.decodeFile(filestring);
                ByteArrayOutputStream bao = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 50, bao);
                byte[] ba = bao.toByteArray();
                //Converting bitmap into Base64String
                String ba1 = Base64.encodeToString(ba, Base64.DEFAULT);
                Log.e("base64image", ba1);
                bcrphotolist.add(ba1);
            }


            return bcrphotolist;
            // return imageFile.getAbsolutePath();

        }
    }


    //show popup on click of new area

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

                if (areaEntered.isEmpty())
                {
                    Utils.showToast(getBaseContext(), "Area name can't be empty. Please enter valid area");
                }
                else
                {

                    String full_hier_level = "NONE";

                    int areaID = Utils.getRandomNumber();
                    String SQL_FULL_HEIR = "SELECT full_hier_level from " + TBL_LOCATION_HIERARCHY + " WHERE loc_id=? ";
                    String[] selectionArgs = {districtID};

                    int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
                    SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

                    Cursor cursor = sqLiteDatabase.rawQuery(SQL_FULL_HEIR, selectionArgs);

                    if (cursor.moveToFirst())
                    {
                        full_hier_level = cursor.getString(cursor.getColumnIndexOrThrow("full_hier_level"));
                    }

                    cursor.close();

                    sqLiteDatabase.close();

                    DbUtils.insertIntoLocationHierarchy(areaID, areaEntered, "5", full_hier_level + "," + areaID, districtID, "0");
                    setAreaAdapter();
                    Utils.showToast(getBaseContext(), "New Area Added Successfully");





               /*
                    if (Utils.isNetworkConnected(CreateRetailerActivity.this))
                    {

                        IM_PutNewArea.AreaData areaData = new IM_PutNewArea().new AreaData(districtID, areaEntered, "5", new MySharedPrefrencesData().get_User_CompanyId(CreateRetailerActivity.this));
                        IM_PutNewArea im_putNewArea = new IM_PutNewArea(new MySharedPrefrencesData().getEmployee_AuthKey(CreateRetailerActivity.this), areaData);

                        networkcall_for_putNewArea(im_putNewArea);

                    }
                    else
                    {


                        String full_hier_level = "NONE";

                        int areaID = Utils.getRandomNumber();
                        String SQL_FULL_HEIR = "SELECT full_hier_level from " + TBL_LOCATION_HIERARCHY + " WHERE loc_id=? ";
                        String[] selectionArgs = {districtID};

                        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
                        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

                        Cursor cursor = sqLiteDatabase.rawQuery(SQL_FULL_HEIR, selectionArgs);

                        if (cursor.moveToFirst())
                        {
                            full_hier_level = cursor.getString(cursor.getColumnIndexOrThrow("full_hier_level"));
                        }

                        cursor.close();

                        sqLiteDatabase.close();

                        DbUtils.insertIntoLocationHierarchy(areaID, areaEntered, "5", full_hier_level + "," + areaID, districtID,"0");
                        setAreaAdapter();
                        Utils.showToast(getBaseContext(), "New Area Added Successfully");
                    }*/

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


    //api call for addition of new area
    private void networkcall_for_putNewArea(final IM_PutNewArea im_putNewArea)
    {
        Apimethods apimethods = API_Call_Retrofit.getretrofit(CreateRetailerActivity.this).create(Apimethods.class);


        Call<PutNewArea> call = apimethods.putNewArea(im_putNewArea);


        call.enqueue(new Callback<PutNewArea>()
        {
            @Override
            public void onResponse(Call<PutNewArea> call, Response<PutNewArea> response)
            {
                if (response.isSuccessful())
                {
                    PutNewArea putNewArea = response.body();

                    DbUtils.insertIntoLocationHierarchy(Integer.parseInt(putNewArea.getLocHierId()), im_putNewArea.getAreaData().getName(), "5", putNewArea.getLocationHier(), districtID, "1");
                    setAreaAdapter();
                    Utils.showToast(getBaseContext(), "New Area Added Successfully");

                }
                else
                {
                    Utils.showToast(getBaseContext(), "New Area Added unsuccessfully");
                }
            }

            @Override
            public void onFailure(Call<PutNewArea> call, Throwable t)
            {
                Utils.showToast(getBaseContext(), ConstantsA.NO_INTERNET_CONNECTION);
            }
        });
    }


    //dispatch intent for retailer photo and BCR
    protected void dispatchTakePictureIntent(int flag_for_bcr_photo)
    {
        Uri photoURI;

        // Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        try
        {
            if (flag_for_bcr_photo == Constants.FLAG_RETAILER_PHOTO)
            {

                imageFile = createImageFile();
            }
            else
            {

                imageFile_BCR = createImageFileBCR();
            }
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }

        if (flag_for_bcr_photo == Constants.FLAG_RETAILER_PHOTO)
        {

            if (imageFile != null)
            {

                photoURI = FileProvider.getUriForFile(getBaseContext(), BuildConfig.APPLICATION_ID + ".provider", imageFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
        else
        {

            if (imageFile_BCR != null)
            {

                //photoURI = FileProvider.getUriForFile(this, "com.example.android.fileprovider", imageFile_BCR);
                photoURI = FileProvider.getUriForFile(getBaseContext(), BuildConfig.APPLICATION_ID + ".provider", imageFile_BCR);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                //startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

                startActivityForResult(takePictureIntent, BCR_REQUEST_IMAGE_CAPTURE);
            }
        }


    }

    //image file for retailer photo

    private File createImageFile() throws IOException
    {
        String imageFileName = "RET_IMG_" + Utils.getRandomNumber();

        File photoDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        Log.e("photoDir", photoDir.toString());

        File image = File.createTempFile(imageFileName, ".jpg", photoDir);

        return image;
    }


    //image file for BCR photo
    private File createImageFileBCR() throws IOException
    {
        String imageFileName = "BCR_IMG_" + Utils.getRandomNumber();

        // imagecount.add(imageFileName);

        File photoDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        Log.e("photoDir", photoDir.toString());

        File image = File.createTempFile(imageFileName, ".jpg", photoDir);
        imagecount.add(image);
        return image;
    }

    // ocr permission and location
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case REQUEST_WRITE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {

                    takePicture();
                }
                else
                {
                    Toast.makeText(CreateRetailerActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_WRITE_PERMISSION_BACK:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {

                    takePictureBack();
                }
                else
                {
                    Toast.makeText(CreateRetailerActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
                break;


            case ALL_PERMISSIONS_RESULT_CreateRetailer:
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
                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT_CreateRetailer);
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

    // ocr take picture

    private void takePicture()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //String imagename = "BCR_IMG_NEW_" + Utils.getRandomNumber()+".jpg";

        //imagecount.add(imagename);

        File photo = new File(Environment.getExternalStorageDirectory(), "picture.jpg");
        imageUri = FileProvider.getUriForFile(CreateRetailerActivity.this,
                BuildConfig.APPLICATION_ID + ".provider", photo);
        //imagecount.add(imageUri);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, PHOTO_REQUEST);
    }

    private void takePictureBack()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


        File photo = new File(Environment.getExternalStorageDirectory(), "pictureback.jpg");
        imageuriback = FileProvider.getUriForFile(CreateRetailerActivity.this,
                BuildConfig.APPLICATION_ID + ".provider", photo);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageuriback);
        startActivityForResult(intent, PHOTO_REQUEST_BACK);
    }


    //before retailer submit capture all filled data
    private void captureFormData()
    {
        retailerName = etRetailerName.getText().toString().trim();
        ownerName = etOwnerName.getText().toString().trim();
        shopAddress = etShopAddress.getText().toString().trim();
        pincode = etPincode.getText().toString().trim();
        mobileNumber = etMobNo.getText().toString().trim();
        email = etEmail.getText().toString().trim();

        /*stateName = spnState.getSelectedItem().toString();
        districtName = spnDistrict.getSelectedItem().toString();
        talukName = spnTaluk.getSelectedItem().toString();
        areaName = spnArea.getSelectedItem().toString();*/
    }


    //after retailer submit clear filled data
    private void clearFormData()
    {
        etRetailerName.setText("");
        etOwnerName.setText("");
        etShopAddress.setText("");
        etPincode.setText("");
        etMobNo.setText("");
        etEmail.setText("");
        ivPhotoPreview.setImageDrawable(getResources().getDrawable(R.drawable.border_rect));
        iv_bcr_photo.setImageDrawable(getResources().getDrawable(R.drawable.border_rect));
        tv_bcr_photo_count.setText("0");
        tv_bcr_text.setVisibility(View.GONE);
        tv_ocr_back.setVisibility(View.GONE);
        imagecount = new ArrayList<>();
        imageFile = null;
        rg_location.clearCheck();
    }


    //check retailer all data validation
    boolean hasEnteredValidData()
    {
        String msgInvalidEntries = "Invalid Entry Here. Please Enter Valid Data";
        int inValidEntriesCount = 0;

        if (retailerName.isEmpty())
        {
            etRetailerName.setError(msgInvalidEntries);
            inValidEntriesCount++;
        }

        if ((!tv_bcr_text.getText().toString().isEmpty()) || imagecount.size() > 0)
        {
            // Utils.showToast(this, "error data");


            if (!pincode.isEmpty() && !pincode.matches("^[1-9][0-9]{5}$"))
            {
                etPincode.setError(msgInvalidEntries);
                inValidEntriesCount++;
            }
            if (!mobileNumber.isEmpty() && !mobileNumber.matches("^[7-9][0-9]{9}$"))
            {
                etMobNo.setError(msgInvalidEntries);
                inValidEntriesCount++;
            }


            //entering email is optional.
            if (!email.isEmpty())
            {
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    etEmail.setError(msgInvalidEntries);
                    inValidEntriesCount++;
                }
            }

        }
        else
        {

            if (ownerName.isEmpty())
            {
                etOwnerName.setError(msgInvalidEntries);

                inValidEntriesCount++;
            }
            if (shopAddress.isEmpty())
            {
                etShopAddress.setError(msgInvalidEntries);
                inValidEntriesCount++;
            }
            if (pincode.isEmpty() || !pincode.matches("^[1-9][0-9]{5}$"))
            {
                etPincode.setError(msgInvalidEntries);
                inValidEntriesCount++;
            }
            if (mobileNumber.isEmpty() || !mobileNumber.matches("^[7-9][0-9]{9}$"))
            {
                etMobNo.setError(msgInvalidEntries);
                inValidEntriesCount++;
            }


            //entering email is optional.
            if (!email.isEmpty())
            {
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    etEmail.setError(msgInvalidEntries);
                    inValidEntriesCount++;
                }
            }
        }


        if (areaName.equals(ADD_NEW_AREA) || areaName.equals("Select One"))
        {
            //Utils.showPopUp(this, "Error: Select Valid Area");

            TextView errorText = (TextView) spnArea.getSelectedView();
            errorText.setError(msgInvalidEntries);

            inValidEntriesCount++;
        }

        if (inValidEntriesCount > 0)
        {

            Utils.showPopUp(CreateRetailerActivity.this, "kindly fill all  mandatory details.");
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
        if (progressDialog != null)
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

    private void removeError()
    {
        etOwnerName.setError(null);
        etShopAddress.setError(null);
        etPincode.setError(null);
        etMobNo.setError(null);
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
        //disConnectGoogleApiClient();
        super.onStop();
    }


    //get location online via google api client
    @Override
    public void onConnected(Bundle bundle)
    {
        locationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(1000);
        // LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, locationListener);
    }

    @Override
    public void onConnectionSuspended(int i)
    {
        //connectGoogleApiClient();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        //connectGoogleApiClient();
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


    //state list acc to loggedin user

    List<String> getStatesList()
    {
        List<String> statesList = new ArrayList<>();

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);


        String userlocationid = new MySharedPrefrencesData().getUser_LocationId(CreateRetailerActivity.this);

        String selected_location_id = "";
        if (userlocationid.contains(","))
        {

            String location_id_arr[] = userlocationid.split(",");

            for (int i = 0; i < location_id_arr.length; i++)
            {

                selected_location_id = location_id_arr[i];


                String user_full_hier = "SELECT " + "full_hier_level" + " FROM " + TBL_LOCATION_HIERARCHY + " WHERE " + "loc_id = ?";
                String[] selectionArgs_for_user = {selected_location_id};

                Cursor cursor1 = sqLiteDatabase.rawQuery(user_full_hier, selectionArgs_for_user);
                String fullhier = "";
                if (cursor1.moveToFirst())
                {
                    fullhier = cursor1.getString(cursor1.getColumnIndexOrThrow("full_hier_level"));

                }

                String[] fullhierarray = fullhier.split(",");

                // String region = fullhierarray[1];


                // String SQL_SELECT_STATES = "SELECT " + "loc_name" + " FROM " + TBL_LOCATION_HIERARCHY + " WHERE " + "parent_loc_id = ?";
                String SQL_SELECT_STATES = "SELECT " + "loc_name" + " FROM " + TBL_LOCATION_HIERARCHY + " WHERE " + "loc_id = ?";
                String[] selectionArgs = {fullhierarray[2]};

                Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_STATES, selectionArgs);

                while (cursor.moveToNext())
                {
                    String stateName = cursor.getString(cursor.getColumnIndexOrThrow("loc_name"));
                    if (!statesList.contains(stateName))
                    {

                        statesList.add(stateName);
                    }
                }

                cursor.close();
            }

            sqLiteDatabase.close();

        }
        else
        {

            selected_location_id = userlocationid;


            String user_full_hier = "SELECT " + "full_hier_level" + " FROM " + TBL_LOCATION_HIERARCHY + " WHERE " + "loc_id = ?";
            String[] selectionArgs_for_user = {selected_location_id};

            Cursor cursor1 = sqLiteDatabase.rawQuery(user_full_hier, selectionArgs_for_user);
            String fullhier = "";
            if (cursor1.moveToFirst())
            {
                fullhier = cursor1.getString(cursor1.getColumnIndexOrThrow("full_hier_level"));

            }

            String[] fullhierarray = fullhier.split(",");

            // String region = fullhierarray[1];


            // String SQL_SELECT_STATES = "SELECT " + "loc_name" + " FROM " + TBL_LOCATION_HIERARCHY + " WHERE " + "parent_loc_id = ?";
            String SQL_SELECT_STATES = "SELECT " + "loc_name" + " FROM " + TBL_LOCATION_HIERARCHY + " WHERE " + "loc_id = ?";
            String[] selectionArgs = {fullhierarray[2]};

            Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_STATES, selectionArgs);

            while (cursor.moveToNext())
            {
                String stateName = cursor.getString(cursor.getColumnIndexOrThrow("loc_name"));
                statesList.add(stateName);
            }

            cursor.close();
            sqLiteDatabase.close();
        }


        return statesList;
    }

    //district list acc to loggedin user
    List<String> getDistrictsList(String stateID)
    {
        List<String> districtsList = new ArrayList<>();

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

       /* String SQL_SELECT_DISTRICTS = "SELECT " + "loc_name" + " FROM " + TBL_LOCATION_HIERARCHY + " WHERE " + "parent_loc_id = ?";
        String[] selectionArgs = {stateID};*/


        ////////////////single district//////////////
        String userlocationid = new MySharedPrefrencesData().getUser_LocationId(CreateRetailerActivity.this);

        String selected_location_id = "";
        if (userlocationid.contains(","))
        {

            String location_id_arr[] = userlocationid.split(",");

            for (int i = 0; i < location_id_arr.length; i++)
            {

                selected_location_id = location_id_arr[i];

                String user_full_hier = "SELECT " + "full_hier_level" + " FROM " + TBL_LOCATION_HIERARCHY + " WHERE " + "loc_id = ?";
                String[] selectionArgs_for_user = {selected_location_id};

                Cursor cursor1 = sqLiteDatabase.rawQuery(user_full_hier, selectionArgs_for_user);
                String fullhier = "";

                if (cursor1.moveToFirst())
                {
                    fullhier = cursor1.getString(cursor1.getColumnIndexOrThrow("full_hier_level"));

                }

                String[] fullhierarray = fullhier.split(",");

                String district = fullhierarray[3];


                String SQL_SELECT_DISTRICTS = "SELECT " + "loc_name" + " FROM " + TBL_LOCATION_HIERARCHY + " WHERE " + "loc_id = ? AND parent_loc_id = ?";
                String[] selectionArgs = {district, stateID};

                //////////////////////////////////////////
                Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_DISTRICTS, selectionArgs);

                while (cursor.moveToNext())
                {
                    String districtName = cursor.getString(cursor.getColumnIndexOrThrow("loc_name"));

                    if (!districtsList.contains(districtName))
                    {

                        districtsList.add(districtName);
                    }
                }

                cursor.close();
            }


        }
        else
        {

            selected_location_id = userlocationid;

            String user_full_hier = "SELECT " + "full_hier_level" + " FROM " + TBL_LOCATION_HIERARCHY + " WHERE " + "loc_id = ?";
            String[] selectionArgs_for_user = {selected_location_id};

            Cursor cursor1 = sqLiteDatabase.rawQuery(user_full_hier, selectionArgs_for_user);
            String fullhier = "";
            if (cursor1.moveToFirst())
            {
                fullhier = cursor1.getString(cursor1.getColumnIndexOrThrow("full_hier_level"));

            }

            String[] fullhierarray = fullhier.split(",");

            String district = fullhierarray[3];


            String SQL_SELECT_DISTRICTS = "SELECT " + "loc_name" + " FROM " + TBL_LOCATION_HIERARCHY + " WHERE " + "loc_id = ? AND parent_loc_id = ?";
            String[] selectionArgs = {district, stateID};

            //////////////////////////////////////////
            Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_DISTRICTS, selectionArgs);

            while (cursor.moveToNext())
            {
                String districtName = cursor.getString(cursor.getColumnIndexOrThrow("loc_name"));
                districtsList.add(districtName);
            }

            cursor.close();
        }


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

        while (cursor.moveToNext())
        {
            String stateName = cursor.getString(cursor.getColumnIndexOrThrow("loc_name"));
            taluksList.add(stateName);
        }

        cursor.close();
        sqLiteDatabase.close();

        return taluksList;
    }

    //area list acc to loggedin user

    List<String> getAreasList(String talukID)
    {
        List<String> areasList = new ArrayList<>();

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);


        String SQL_SELECT_AREAS = "SELECT " + "loc_id,loc_name" + " FROM " + TBL_LOCATION_HIERARCHY + " WHERE " + "parent_loc_id = ?";
        String[] selectionArgs = {talukID};

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_AREAS, selectionArgs);

        areasList.add("Select One");

        while (cursor.moveToNext())
        {
            String locationId = cursor.getString(cursor.getColumnIndexOrThrow("loc_id"));
            String areaName = cursor.getString(cursor.getColumnIndexOrThrow("loc_name"));

            String userlocationid = new MySharedPrefrencesData().getUser_LocationId(CreateRetailerActivity.this);

            String selected_location_id = "";


            if (userlocationid.contains(","))
            {

                String location_id_arr[] = userlocationid.split(",");
                for (int i = 0; i < location_id_arr.length; i++)
                {

                    if (locationId.equalsIgnoreCase(location_id_arr[i]))
                    {

                        areasList.add(areaName);
                    }
                }

            }
            else
            {

                selected_location_id = userlocationid;
                if (locationId.equalsIgnoreCase(selected_location_id))
                {

                    areasList.add(areaName);
                }
            }

            //areasList.add(areaName);
        }

        areasList.add(ADD_NEW_AREA);

        cursor.close();
        sqLiteDatabase.close();

        return areasList;
    }

    //location id  acc to loc name

    String getLocationID(String locationName)
    {
        String locationID = null;

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String SQL_SELECT_LOCATION_ID = "SELECT " + "loc_id" + " FROM " + TBL_LOCATION_HIERARCHY + " WHERE " + "loc_name = ?";
        String[] selectionArgs = {locationName};

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_LOCATION_ID, selectionArgs);

        if (cursor.moveToFirst())
        {
            locationID = cursor.getString(cursor.getColumnIndexOrThrow("loc_id"));
        }

        cursor.close();
        sqLiteDatabase.close();

        return locationID;
    }


    //inserting into RETILER table

    private void insertIntoRetailers(String retailer_id, String mobile_retailer_id, String empID, String retailerName, String ownerName, String shopAddress, String pincode,
                                     String mobileNumber, String email, String areaID, String latitude, String longitude, String imgPath, String ocr_frnttxt, String ocr_backtext, ArrayList<String> bcrPhotoList, int uploadstatus)
    {
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        ContentValues retailerValues = new ContentValues();
        retailerValues.put("retailer_id", retailer_id);
        retailerValues.put("mobile_retailer_id", mobile_retailer_id);
        retailerValues.put("emp_id", empID);
        retailerValues.put("shop_name", ownerName);
        retailerValues.put("retailer_name", retailerName);
        retailerValues.put("shop_address", shopAddress);
        retailerValues.put("pincode", pincode);
        retailerValues.put("mobile_no", mobileNumber);
        retailerValues.put("email", email);
        retailerValues.put("area_id", areaID);
        retailerValues.put("latitude", latitude);
        retailerValues.put("longitude", longitude);
        retailerValues.put("img_source", imgPath);
        retailerValues.put("ocr_front_text", ocr_frnttxt);
        retailerValues.put("ocr_back_text", ocr_backtext);
        retailerValues.put("distributor_id", "0");
        retailerValues.put("distributor_name", "No Name");


        String bcrphotosstr = new Gson().toJson(bcrPhotoList);

        retailerValues.put("ocr_photolist", bcrphotosstr);


        retailerValues.put("created_date", Utils.getDateTime());
        retailerValues.put("modified_date", Utils.getDateTime());
        retailerValues.put("created_by", new MySharedPrefrencesData().getUser_Id(CreateRetailerActivity.this));
        retailerValues.put("landmark", shopAddress);
        retailerValues.put("area", areaName);
        //retailerValues.put("taluk", "taluk");
        retailerValues.put("district", districtName);
        retailerValues.put("state", stateName);
        retailerValues.put("upload_status", uploadstatus);
        sqLiteDatabase.insert(TBL_RETAILER, null, retailerValues);

        sqLiteDatabase.close();
    }


    //set area list in area dropdown

    void setAreaAdapter()
    {
        ArrayAdapter<String> areaAdapter = new ArrayAdapter<>(CreateRetailerActivity.this, android.R.layout.simple_spinner_item, getAreasList(districtID));
        areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnArea.setAdapter(areaAdapter);
    }


    //for location track offline

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

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener)
    {
        new android.support.v7.app.AlertDialog.Builder(CreateRetailerActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


}
