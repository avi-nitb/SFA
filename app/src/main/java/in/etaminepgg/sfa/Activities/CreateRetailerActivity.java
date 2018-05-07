package in.etaminepgg.sfa.Activities;

import android.Manifest;
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
import android.location.Address;
import android.location.Geocoder;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.i18n.phonenumbers.PhoneNumberMatch;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.etaminepgg.sfa.BuildConfig;
import in.etaminepgg.sfa.InputModel_For_Network.IM_CreateRetailer;
import in.etaminepgg.sfa.InputModel_For_Network.IM_PutNewArea;
import in.etaminepgg.sfa.Models.CreateretaileModel;
import in.etaminepgg.sfa.Models.PutNewArea;
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
import static in.etaminepgg.sfa.Utilities.Constants.TBL_LOCATION_HIERARCHY;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_RETAILER;
import static in.etaminepgg.sfa.Utilities.Constants.dbFileFullPath;
import static in.etaminepgg.sfa.Utilities.Utils.getDateTime;
import static in.etaminepgg.sfa.Utilities.Utils.loggedInUserID;

public class CreateRetailerActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
    private static final String ADD_NEW_AREA = "add new area";
    private static final int REQUEST_IMAGE_CAPTURE = 111;
    private final int REQUEST_CODE_ACCESS_FINE_LOCATION = 11;
    EditText etRetailerName, etOwnerName, etShopAddress, etPincode, etMobNo, etEmail;
    Spinner spnState, spnDistrict, spnTaluk, spnArea;
    Button btnPhotoCapt, btnCreateNewRetailer,btn_bcr;
    EditText enterNewArea_EditText;
    ArrayList<String> arrState;
    ArrayList<String> arrDist;
    ArrayList<String> arrCity;
    ArrayList<String> arrArea;
    ImageView ivPhotoPreview;
    File imageFile = null;
    ProgressDialog progressDialog = null;
    String lat, lng;
    private String retailerName, ownerName, shopAddress, pincode, mobileNumber, email = "";
    private String stateName, districtName, talukName, areaName;
    private String stateID, districtID, talukID, areaID;
    private GoogleApiClient googleApiClient;



    private static final String LOG_TAG = "CREATE RETAILER";
    private static final int PHOTO_REQUEST = 10;
    private Uri imageUri;
    private TextRecognizer detector;
    private static final int REQUEST_WRITE_PERMISSION = 20;
    private static final String SAVED_INSTANCE_URI = "uri";
    private static final String SAVED_INSTANCE_RESULT = "result";

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

            if (Utils.isNetworkConnected(CreateRetailerActivity.this))
            {

                networkcall_for_createretailer();
            }
            else
            {

                Utils.showErrorDialog(CreateRetailerActivity.this, "You are unable to create retailer dueto no internet connection.");
            }

            clearFormData();
        }
    };
    private Toolbar toolbar;
    private LocationRequest locationRequest;

    private void networkcall_for_createretailer()
    {
        Apimethods apimethods = API_Call_Retrofit.getretrofit(CreateRetailerActivity.this).create(Apimethods.class);


        IM_CreateRetailer.RetailerData retailerData = new IM_CreateRetailer().new RetailerData(retailerName, ownerName, "Retailers", pincode, mobileNumber, email, lat + "," + lng, areaID, getDateTime(), new MySharedPrefrencesData().getUser_Id(CreateRetailerActivity.this), getImagePath(), shopAddress);

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

                        insertIntoRetailers(createretaileModel.getCustomerCode(), loggedInUserID, retailerName, ownerName, shopAddress, pincode,
                                mobileNumber, email, areaID, lat, lng, createretaileModel.getCustomer_picture_path());
                        dismissProgressDialog();

                        Utils.showSuccessDialog(CreateRetailerActivity.this, "Retailer creation successful.");


                    }
                }
                else
                {
                    dismissProgressDialog();
                    Utils.showErrorDialog(CreateRetailerActivity.this, "Retailer creation unsuccessful.");
                }

            }

            @Override
            public void onFailure(Call<CreateretaileModel> call, Throwable t)
            {
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
        setContentView(R.layout.activity_create_retailer);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create New Retailer");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        buildGoogleApiClient();

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
        btn_bcr = (Button) findViewById(R.id.btn_bcr);
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

                ArrayAdapter<String> talukAdapter = new ArrayAdapter<>(CreateRetailerActivity.this, android.R.layout.simple_spinner_item, getAreasList(districtID));
                talukAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnArea.setAdapter(talukAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
            }
        });

      /*  spnTaluk.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
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
        });*/

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
                dispatchTakePictureIntent();
            }
        });

        btnCreateNewRetailer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!Utils.isGpsEnabled(CreateRetailerActivity.this))
                {
                    Utils.enableGPS(googleApiClient, locationRequest, CreateRetailerActivity.this);
                }
                else
                {
                    createNewRetailer();
                }
            }
        });


        if (savedInstanceState != null) {
            imageUri = Uri.parse(savedInstanceState.getString(SAVED_INSTANCE_URI));
            etRetailerName.setText(savedInstanceState.getString(SAVED_INSTANCE_RESULT));
        }
        detector = new TextRecognizer.Builder(getApplicationContext()).build();


        // TODO: Check if the TextRecognizer is operational.
        if (!detector.isOperational()) {
            Log.w(LOG_TAG, "Detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this, R.string.low_storage_error, Toast.LENGTH_LONG).show();
                Log.w(LOG_TAG, getString(R.string.low_storage_error));
            }
        }



        btn_bcr.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ActivityCompat.requestPermissions(CreateRetailerActivity.this, new
                        String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            Bitmap imageBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            ivPhotoPreview.setImageBitmap(imageBitmap);
        }
        else if (requestCode == REQUEST_TURN_ON_LOCATION && resultCode == RESULT_OK)
        {
            createNewRetailer();
        }
        else if (requestCode == REQUEST_TURN_ON_LOCATION && resultCode == RESULT_CANCELED)
        {
            Utils.showErrorDialog(this, "Retailer creation failed. You must turn on GPS.");
        }else   if (requestCode == PHOTO_REQUEST && resultCode == RESULT_OK) {
            launchMediaScanIntent();
            try {
                Bitmap bitmap = decodeBitmapUri(this, imageUri);
                Log.d("detector",""+detector.isOperational());
                if (detector.isOperational() && bitmap != null) {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> textBlocks = detector.detect(frame);
                    String blocks = "";
                    String lines = "";
                    String words = "";
                    for (int index = 0; index < textBlocks.size(); index++) {
                        //extract scanned text blocks here
                        TextBlock tBlock = textBlocks.valueAt(index);
                        blocks = blocks + tBlock.getValue() + "\n" + "\n";
                        for (Text line : tBlock.getComponents()) {
                            //extract scanned text lines here
                            lines = lines + line.getValue() + "\n";
                            for (Text element : line.getComponents()) {
                                //extract scanned text words here
                                words = words + element.getValue() + ", ";
                            }
                        }
                    }
                    if (textBlocks.size() == 0) {
                        etRetailerName.setText("Scan Failed: Found nothing to scan");
                    } else {

                        etRetailerName.setText(etRetailerName.getText() + lines + "\n");

                    }
                } else {
                    etRetailerName.setText("Could not set up the detector!");
                }
            } catch (Exception e) {
                Toast.makeText(this, "Failed to load Image", Toast.LENGTH_SHORT)
                        .show();
                Log.e(LOG_TAG, e.toString());
            }
        }
    }



    private void launchMediaScanIntent() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(imageUri);
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

    void createNewRetailer()
    {
        captureFormData();

        if (hasEnteredValidData())
        {
            if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                startProgressDialog();
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, locationListener);
            }
            else
            {
                ActivityCompat.requestPermissions(CreateRetailerActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ACCESS_FINE_LOCATION);
                //LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, locationListener);
            }

          /*  insertIntoRetailers(loggedInUserID, retailerName, ownerName, shopAddress, pincode,
                    mobileNumber, email, areaID, lat, lng, getImagePath());*/


/*
            if(Utils.isNetworkConnected(CreateRetailerActivity.this)){

                network_call_for_PutRetailerInfo(new MySharedPrefrencesData().getEmployee_AuthKey
                                (CreateRetailerActivity.this), loggedInUserID, retailerName,
                        ownerName, shopAddress, pincode, mobileNumber, email, areaID, lat, lng, getImagePath());

                insertIntoRetailers(loggedInUserID, retailerName, ownerName, shopAddress, pincode,
                        mobileNumber, email, areaID, lat, lng, getImagePath());

            }else{
                insertIntoRetailers(loggedInUserID, retailerName, ownerName, shopAddress, pincode,
                        mobileNumber, email, areaID, lat, lng, getImagePath());
            }*/
            // clearFormData();
        }
    }


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
            bm.compress(Bitmap.CompressFormat.JPEG, 50, bao);
            byte[] ba = bao.toByteArray();
            //Converting bitmap into Base64String
            String ba1 = Base64.encodeToString(ba, Base64.DEFAULT);
            Log.e("base64image", ba1);
            return ba1;
            // return imageFile.getAbsolutePath();

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

                if (areaEntered.isEmpty())
                {
                    Utils.showToast(getBaseContext(), "Area name can't be empty. Please enter valid area");
                }
                else
                {
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

                        DbUtils.insertIntoLocationHierarchy(areaID, areaEntered, "5", full_hier_level + "," + areaID, districtID);
                        setAreaAdapter();
                        Utils.showToast(getBaseContext(), "New Area Added Successfully");
                    }

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

                    DbUtils.insertIntoLocationHierarchy(Integer.parseInt(putNewArea.getLocHierId()), im_putNewArea.getAreaData().getName(), "5", putNewArea.getLocationHier(), districtID);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePicture();
                } else {
                    Toast.makeText(CreateRetailerActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }


    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStorageDirectory(), "picture.jpg");
        imageUri = FileProvider.getUriForFile(CreateRetailerActivity.this,
                BuildConfig.APPLICATION_ID + ".provider", photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, PHOTO_REQUEST);
    }

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

    private void clearFormData()
    {
        etRetailerName.setText("");
        etOwnerName.setText("");
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

        if (retailerName.isEmpty())
        {
            etRetailerName.setError(msgInvalidEntries);
            inValidEntriesCount++;
        }
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

        if (areaName.equals(ADD_NEW_AREA))
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

    List<String> getStatesList()
    {
        List<String> statesList = new ArrayList<>();

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);


        String userlocationid = new MySharedPrefrencesData().getUser_LocationId(CreateRetailerActivity.this);

        String selected_location_id="";
        if(userlocationid.contains(",")){

            String location_id_arr[]=userlocationid.split(",");
            selected_location_id=location_id_arr[0];

        }else {

            selected_location_id=userlocationid;
        }

        String user_full_hier = "SELECT " + "full_hier_level" + " FROM " + TBL_LOCATION_HIERARCHY + " WHERE " + "loc_id = ?";
        String[] selectionArgs_for_user = {selected_location_id};

        Cursor cursor1 = sqLiteDatabase.rawQuery(user_full_hier, selectionArgs_for_user);
        String fullhier = "";
        if (cursor1.moveToFirst())
        {
            fullhier = cursor1.getString(cursor1.getColumnIndexOrThrow("full_hier_level"));

        }

        String[] fullhierarray = fullhier.split(",");

        String region = fullhierarray[1];


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

        return statesList;
    }

    List<String> getDistrictsList(String stateID)
    {
        List<String> districtsList = new ArrayList<>();

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

       /* String SQL_SELECT_DISTRICTS = "SELECT " + "loc_name" + " FROM " + TBL_LOCATION_HIERARCHY + " WHERE " + "parent_loc_id = ?";
        String[] selectionArgs = {stateID};*/


        ////////////////single district//////////////
        String userlocationid = new MySharedPrefrencesData().getUser_LocationId(CreateRetailerActivity.this);

        String selected_location_id="";
        if(userlocationid.contains(",")){

            String location_id_arr[]=userlocationid.split(",");
            selected_location_id=location_id_arr[0];

        }else {

            selected_location_id=userlocationid;
        }

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


        String SQL_SELECT_DISTRICTS = "SELECT " + "loc_name" + " FROM " + TBL_LOCATION_HIERARCHY + " WHERE " + "loc_id = ?";
        String[] selectionArgs = {district};

        //////////////////////////////////////////
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_DISTRICTS, selectionArgs);

        while (cursor.moveToNext())
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

        while (cursor.moveToNext())
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

        while (cursor.moveToNext())
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

        if (cursor.moveToFirst())
        {
            locationID = cursor.getString(cursor.getColumnIndexOrThrow("loc_id"));
        }

        cursor.close();
        sqLiteDatabase.close();

        return locationID;
    }

    private void insertIntoRetailers(String retailer_id, String empID, String retailerName, String ownerName, String shopAddress, String pincode,
                                     String mobileNumber, String email, String areaID, String latitude, String longitude, String imgPath)
    {
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        ContentValues retailerValues = new ContentValues();
        retailerValues.put("retailer_id", retailer_id);
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
        retailerValues.put("created_date", Utils.getDateTime());
        retailerValues.put("modified_date", Utils.getDateTime());
        retailerValues.put("created_by", new MySharedPrefrencesData().getUser_Id(CreateRetailerActivity.this));
        retailerValues.put("landmark", shopAddress);
        retailerValues.put("area", areaName);
        //retailerValues.put("taluk", "taluk");
        retailerValues.put("district", districtName);
        retailerValues.put("state", stateName);
        retailerValues.put("upload_status", 1);
        sqLiteDatabase.insert(TBL_RETAILER, null, retailerValues);

        sqLiteDatabase.close();
    }

    void setAreaAdapter()
    {
        ArrayAdapter<String> areaAdapter = new ArrayAdapter<>(CreateRetailerActivity.this, android.R.layout.simple_spinner_item, getAreasList(districtID));
        areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnArea.setAdapter(areaAdapter);
    }


   /* public double getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        double p1 = 0;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return 0;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

         //   p1 = new LatLng(location.getLatitude(), location.getLongitude() );

            p1=location.getLatitude();

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }*/
}
