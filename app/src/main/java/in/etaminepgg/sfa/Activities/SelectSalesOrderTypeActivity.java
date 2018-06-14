package in.etaminepgg.sfa.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

import in.etaminepgg.sfa.Adapters.NoOrderReasonAdapter;
import in.etaminepgg.sfa.InputModel_For_Network.IM_PutRetailerVisit;
import in.etaminepgg.sfa.InputModel_For_Network.IM_PutSalesOrderMaster;
import in.etaminepgg.sfa.Models.NoReasonModel;
import in.etaminepgg.sfa.Models.PutRetailerInfo_Model;
import in.etaminepgg.sfa.Models.PutSalesOrderMaster;
import in.etaminepgg.sfa.Network.API_Call_Retrofit;
import in.etaminepgg.sfa.Network.Apimethods;
import in.etaminepgg.sfa.R;
import in.etaminepgg.sfa.Utilities.DbUtils;
import in.etaminepgg.sfa.Utilities.MyDb;
import in.etaminepgg.sfa.Utilities.MySharedPrefrencesData;
import in.etaminepgg.sfa.Utilities.Utils;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static in.etaminepgg.sfa.Utilities.Constants.REQUEST_TURN_ON_LOCATION;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_RETAILER_VISIT;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SALES_ORDER;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SKU_NO_ORDERREASON;
import static in.etaminepgg.sfa.Utilities.Constants.dbFileFullPath;
import static in.etaminepgg.sfa.Utilities.ConstantsA.INTENT_EXTRA_RETAILER_ID;
import static in.etaminepgg.sfa.Utilities.ConstantsA.INTENT_EXTRA_RETAILER_NAME;
import static in.etaminepgg.sfa.Utilities.ConstantsA.NEW_ORDER;
import static in.etaminepgg.sfa.Utilities.ConstantsA.NO_INTERNET_CONNECTION;
import static in.etaminepgg.sfa.Utilities.ConstantsA.ORDER_TYPE_NEW_ORDER;
import static in.etaminepgg.sfa.Utilities.ConstantsA.ORDER_TYPE_NEW_REGULAR_ORDER;
import static in.etaminepgg.sfa.Utilities.ConstantsA.ORDER_TYPE_NO_ORDER;
import static in.etaminepgg.sfa.Utilities.ConstantsA.REGULAR_ORDER;
import static in.etaminepgg.sfa.Utilities.Utils.getDateTime;
import static in.etaminepgg.sfa.Utilities.Utils.getIST;
import static in.etaminepgg.sfa.Utilities.Utils.getRandomNumber;
import static in.etaminepgg.sfa.Utilities.Utils.getTodayDate;
import static in.etaminepgg.sfa.Utilities.Utils.loggedInUserID;

public class SelectSalesOrderTypeActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
    private final int REQUEST_CODE_ACCESS_FINE_LOCATION = 12;

    TextView salesOrderFor_TextView;
    CheckBox cb_telephone;
    RadioGroup orderTypes_RadioGroup;
    RadioButton newOrder_RadioButton, newRegularOrder_RadioButton, noOrder_RadioButton;
    RecyclerView rv_reasons;
    LinearLayout whyNoOrder_LinearLayout;
    TextInputEditText whyNoOrder_TextInputEditText;
    Button submitOrderType_Button;
    int selectedOrderType;
    ProgressDialog progressDialog = null;

    String retailerName, retailerID, lat, lng, retailerFeedback;

    int valueFromOpenDatabase;
    SQLiteDatabase sqLiteDatabase;


    ArrayList<NoReasonModel> noReasonModelArrayList=new ArrayList<>();
    NoOrderReasonAdapter noOrderReasonAdapter;

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

            doActionDependingOnOrderType();
        }
    };
    private LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_sales_order_type);


        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Select Order Type");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        buildGoogleApiClient();
        findViewsByIDs();
        setListenersToViews();

        Intent intent = getIntent();
        if(intent != null)
        {
            retailerName = intent.getStringExtra(INTENT_EXTRA_RETAILER_NAME);
            retailerID = intent.getStringExtra(INTENT_EXTRA_RETAILER_ID);

            salesOrderFor_TextView.append(retailerName + ", " + retailerID);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostResume()
    {
        super.onPostResume();
        dismissProgressDialog();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        sqLiteDatabase.releaseReference();
    }


    private void findViewsByIDs()
    {
        salesOrderFor_TextView = (TextView) findViewById(R.id.salesOrderFor_TextView);
        cb_telephone = (CheckBox) findViewById(R.id.cb_telephone);
        orderTypes_RadioGroup = (RadioGroup) findViewById(R.id.orderTypes_RadioGroup);
        newOrder_RadioButton = (RadioButton) findViewById(R.id.newOrder_RadioButton);
        newRegularOrder_RadioButton = (RadioButton) findViewById(R.id.newRegularOrder_RadioButton);
        noOrder_RadioButton = (RadioButton) findViewById(R.id.noOrder_RadioButton);
        whyNoOrder_LinearLayout = (LinearLayout) findViewById(R.id.whyNoOrder_LinearLayout);
        whyNoOrder_TextInputEditText = (TextInputEditText) findViewById(R.id.whyNoOrder_TextInputEditText);
        submitOrderType_Button = (Button) findViewById(R.id.submitOrderType_Button);
        rv_reasons = (RecyclerView) findViewById(R.id.rv_reasons);
    }

    private void setListenersToViews()
    {

        rv_reasons.setLayoutManager(new LinearLayoutManager(this));
        noOrderReasonAdapter=new NoOrderReasonAdapter(this,noReasonModelArrayList);
        rv_reasons.setAdapter(noOrderReasonAdapter);

        cb_telephone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                if(cb_telephone.isChecked()){

                    orderTypes_RadioGroup.clearCheck();
                    whyNoOrder_LinearLayout.setVisibility(View.INVISIBLE);
                    noOrder_RadioButton.setVisibility(View.GONE);

                }else {
                    orderTypes_RadioGroup.clearCheck();
                    noOrder_RadioButton.setVisibility(View.VISIBLE);
                }
            }
        });
        orderTypes_RadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedRadioButtonId)
            {
                switch(checkedRadioButtonId)
                {
                    case R.id.newOrder_RadioButton:
                        whyNoOrder_LinearLayout.setVisibility(View.INVISIBLE);
                        selectedOrderType = ORDER_TYPE_NEW_ORDER;
                        break;

                    case R.id.newRegularOrder_RadioButton:
                        whyNoOrder_LinearLayout.setVisibility(View.INVISIBLE);
                        selectedOrderType = ORDER_TYPE_NEW_REGULAR_ORDER;
                        break;

                    case R.id.noOrder_RadioButton:
                        setNoReasonList();
                        whyNoOrder_LinearLayout.setVisibility(View.VISIBLE);
                        selectedOrderType = ORDER_TYPE_NO_ORDER;
                        break;

                    default:
                        break;
                }
            }
        });

        submitOrderType_Button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                retailerFeedback = whyNoOrder_TextInputEditText.getText().toString().trim();

                //getGpsCoordinates();

                if(!Utils.isGpsEnabled(SelectSalesOrderTypeActivity.this))
                {
                    Utils.enableGPS(googleApiClient, locationRequest, SelectSalesOrderTypeActivity.this);
                }
                else
                {
                    getGpsCoordinates();
                }
            }
        });


    }

    private void setNoReasonList()
    {

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String noOrderReasonlistQuery="SELECT * from "+TBL_SKU_NO_ORDERREASON;

        Cursor cursor = sqLiteDatabase.rawQuery(noOrderReasonlistQuery, null);

        noReasonModelArrayList.clear();

        while(cursor.moveToNext())
        {
            String reason_id = cursor.getString(cursor.getColumnIndexOrThrow("reason_id"));
            String reasondesc = cursor.getString(cursor.getColumnIndexOrThrow("reasondesc"));
            noReasonModelArrayList.add(new NoReasonModel(reason_id,reasondesc,false));
        }
        noReasonModelArrayList.add(new NoReasonModel("4","Others",false));
        cursor.close();
        sqLiteDatabase.close();
        noOrderReasonAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == REQUEST_TURN_ON_LOCATION && resultCode == RESULT_OK)
        {
            getGpsCoordinates();
        }
        else if(requestCode == REQUEST_TURN_ON_LOCATION && resultCode == RESULT_CANCELED)
        {
            Utils.showErrorDialog(this, "Visit & Order creation failed. Turn on GPS.");
        }
    }

    void getGpsCoordinates()
    {
        if(Utils.isNetworkConnected(SelectSalesOrderTypeActivity.this))
        {

            if(ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                if(getOrderFlag().equals("1"))
                {
                    startProgressDialog();
                }

                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, locationListener);
                //doActionDependingOnOrderType();
            }
            else
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ACCESS_FINE_LOCATION);
                //LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, locationListener);
            }
        }
        else
        {
            Utils.showErrorDialog(SelectSalesOrderTypeActivity.this, "Please check your internet connection.");
        }
    }

    void doActionDependingOnOrderType()
    {
        Resources resources = getResources();
        String intentExtraKey_selectedOrderType = resources.getString(R.string.key_selected_order_type);

        switch(selectedOrderType)
        {
            case ORDER_TYPE_NEW_ORDER:

                makeEntriesIntoVisitAndOrderTables(null,ORDER_TYPE_NEW_ORDER);
                dismissProgressDialog();
                retailerFeedback="";
                break;

            case ORDER_TYPE_NEW_REGULAR_ORDER:
                makeEntriesIntoVisitAndOrderTables(null,ORDER_TYPE_NEW_REGULAR_ORDER);
                dismissProgressDialog();
                retailerFeedback="";
                break;

            case ORDER_TYPE_NO_ORDER:

                if(NoOrderReasonAdapter.selected_Reason_hashmap.isEmpty()){

                    Utils.showToast(getBaseContext(),"Please select reasons for no order ");
                }else {

                    retailerFeedback="";


                    if(NoOrderReasonAdapter.selected_Reason_hashmap.containsKey("4")){

                        if(NoOrderReasonAdapter.selected_Reason_hashmap.get("4").isEmpty()){

                            Utils.showPopUp(getBaseContext(), "Reason can't be empty. Please enter the reason.");

                        }else {

                            for(Map.Entry<String, String> reasonmap:NoOrderReasonAdapter.selected_Reason_hashmap.entrySet()){

                                    retailerFeedback=retailerFeedback+","+reasonmap.getValue();
                            }
                            confirmIfHeHasMadeRetailerVisit();
                        }

                    }else {

                        confirmIfHeHasMadeRetailerVisit();
                    }
                }


              /*  if(whyNoOrder_TextInputEditText.getText().toString().trim().isEmpty())
                {
                    Utils.showPopUp(getBaseContext(), "Reason can't be empty. Please enter the reason.");
                }
                else
                {
                    confirmIfHeHasMadeRetailerVisit();
                }*/
                break;

            default:
                Utils.showPopUp(getBaseContext(), "Please Select Order Type");
                break;
        }
    }

    void confirmIfHeHasMadeRetailerVisit()
    {
        new AlertDialog.Builder(this)
                .setTitle("Are you really at retailer's location?")
                //.setMessage("If you lie, you will be fired.")
                .setCancelable(false)
                .setPositiveButton("YES", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        makeEntriesIntoVisitAndOrderTables(retailerFeedback,ORDER_TYPE_NO_ORDER);
                        whyNoOrder_TextInputEditText.setText("");
                        orderTypes_RadioGroup.clearCheck();
                        selectedOrderType = -123;
                        whyNoOrder_LinearLayout.setVisibility(View.INVISIBLE);
                        Utils.showToast(getBaseContext(), "Submission Successful");
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Utils.showPopUp(getBaseContext(), "Order Cancelled. Please visit retailer location & take Sales Order from him.");
                    }
                })
                .show();
    }

    //retailerFeedback is explanation/reason by retailer, why there is no order from him
    private void makeEntriesIntoVisitAndOrderTables(String retailerFeedback,int selectedOrderType)
    {

        if(Utils.isNetworkConnected(SelectSalesOrderTypeActivity.this)){

            networkcall_put_retailer_visit(selectedOrderType);

        }else {

            String visitID = "VISIT_" + getIST() + "_" + getRandomNumber();

            insertIntoRetailerVisitTable(visitID, retailerFeedback,false);

            DbUtils.makeCurrentActiveOrderInactive();

            //insertIntoSalesOrderTable(visitID,"",false);

            launch_corresponding_tab(selectedOrderType);
        }
    }

    private void launch_corresponding_tab(int selectedOrderType)
    {

        Resources resources = getResources();
        String intentExtraKey_selectedOrderType = resources.getString(R.string.key_selected_order_type);
        switch(selectedOrderType)
        {
            case ORDER_TYPE_NEW_ORDER:
                dismissProgressDialog();
                Utils.launchActivityWithExtra(SelectSalesOrderTypeActivity.this, SkuListByGenreActivity.class, intentExtraKey_selectedOrderType, NEW_ORDER);
                break;

            case ORDER_TYPE_NEW_REGULAR_ORDER:

                dismissProgressDialog();
                Utils.launchActivityWithExtra(SelectSalesOrderTypeActivity.this, SkuListByGenreActivity.class, intentExtraKey_selectedOrderType, REGULAR_ORDER);
                break;

            case ORDER_TYPE_NO_ORDER:

                break;

            default:
                Utils.showPopUp(SelectSalesOrderTypeActivity.this, "Please Select Order Type");
                break;
        }

    }

    private void networkcall_put_retailer_visit(final int selectedOrderType)
    {
        final Apimethods methods= API_Call_Retrofit.getretrofit(SelectSalesOrderTypeActivity.this).create(Apimethods.class);

        final ProgressDialog progressDialog=new ProgressDialog(SelectSalesOrderTypeActivity.this);
        Utils.startProgressDialog(SelectSalesOrderTypeActivity.this,progressDialog);

        IM_PutRetailerVisit.RetailerData retailerData=new IM_PutRetailerVisit().new RetailerData(retailerID,new MySharedPrefrencesData().getUser_Id(SelectSalesOrderTypeActivity.this),lat,lng,getDateTime(),"",retailerFeedback,"",new MySharedPrefrencesData().get_User_CompanyId(SelectSalesOrderTypeActivity.this));
        IM_PutRetailerVisit im_putRetailerVisit = new IM_PutRetailerVisit(new MySharedPrefrencesData().getEmployee_AuthKey(SelectSalesOrderTypeActivity.this), retailerData);

        Call<PutRetailerInfo_Model> call = methods.putRetailerVisit(im_putRetailerVisit);

        Log.i("putretailervisit_ip",new Gson().toJson(im_putRetailerVisit));

        Log.d("url", "url=" + call.request().url().toString());

        Log.d("put_retail_visit_input",new Gson().toJson(im_putRetailerVisit));

        

        call.enqueue(new Callback<PutRetailerInfo_Model>()
        {
            @Override
            public void onResponse(Call<PutRetailerInfo_Model> call, Response<PutRetailerInfo_Model> response)
            {
                int statusCode = response.code();
                Log.d("Response", "" + statusCode);
                Log.d("respones", "" + response);
                Log.d("put_retail_visit_output",new Gson().toJson(response));

                if(response.isSuccessful()){

                    PutRetailerInfo_Model putRetailerInfo_model=response.body();

                    Log.i("putretailervisit_op",new Gson().toJson(putRetailerInfo_model));

                    if(putRetailerInfo_model.getApiStatus()==1){
                        Utils.dismissProgressDialog(progressDialog);
                        Log.d("put_retail_visit_output",new Gson().toJson(putRetailerInfo_model));

                        String visitID = putRetailerInfo_model.getRetailer_visit_id();

                        insertIntoRetailerVisitTable(visitID, retailerFeedback,true);
                        DbUtils.makeCurrentActiveOrderInactive();
                        if(selectedOrderType!=ORDER_TYPE_NO_ORDER){

                            networkcall_for_putSalesOrderMaster(new MySharedPrefrencesData().getEmployee_AuthKey(SelectSalesOrderTypeActivity.this),visitID);
                        }

                    }
                }else {
                    Log.d("put_retail_visit_output",new Gson().toJson(response.errorBody()));
                    Utils.dismissProgressDialog(progressDialog);
                    Utils.showToast(SelectSalesOrderTypeActivity.this,"Retailer visit is not created successfully.");
                }

            }

            private void networkcall_for_putSalesOrderMaster(String employee_authKey, final String visitiD)
            {

                int getStatusforTelephonicOrder=0;

                if(cb_telephone.isChecked()){

                    getStatusforTelephonicOrder=1;
                }

                IM_PutSalesOrderMaster.SalesData salesData=new IM_PutSalesOrderMaster().new SalesData(visitiD,"","","","","","",""," ",loggedInUserID,getDateTime(),loggedInUserID,"1",getStatusforTelephonicOrder);
                IM_PutSalesOrderMaster im_putSalesOrderMaster=new IM_PutSalesOrderMaster(employee_authKey,salesData);
                Call<PutSalesOrderMaster> call=methods.putSalesOrdermaster(im_putSalesOrderMaster);

                Log.i("putsalesordrmaster_ip", new Gson().toJson(im_putSalesOrderMaster));

                call.enqueue(new Callback<PutSalesOrderMaster>()
                {
                    @Override
                    public void onResponse(Call<PutSalesOrderMaster> call, Response<PutSalesOrderMaster> response)
                    {

                        int statusCode = response.code();
                        Log.d("Response", "" + statusCode);
                        Log.d("respones", "" + response);
                        Log.d("put_sales_master_op",new Gson().toJson(response));
                        if(response.isSuccessful()){
                            PutSalesOrderMaster putSalesOrderMaster=response.body();

                            Log.i("putsalesordermaster_op",new Gson().toJson(putSalesOrderMaster));

                            if(putSalesOrderMaster.getApiStatus()==1){
                                Utils.dismissProgressDialog(progressDialog);


                                String orderid = putSalesOrderMaster.getSalesOrderId();
                                insertIntoSalesOrderTable(visitiD,orderid,true);
                                launch_corresponding_tab(selectedOrderType);

                            }
                        }else {
                            Log.d("put_sales_ordermaster",new Gson().toJson(response.errorBody()));
                            Utils.dismissProgressDialog(progressDialog);
                            Utils.showToast(SelectSalesOrderTypeActivity.this,"salesorder is not created successfully.");
                        }


                    }

                    @Override
                    public void onFailure(Call<PutSalesOrderMaster> call, Throwable t)
                    {
                        Utils.dismissProgressDialog(progressDialog);
                        Utils.showToast(SelectSalesOrderTypeActivity.this,NO_INTERNET_CONNECTION);
                    }
                });

            }

            @Override
            public void onFailure(Call<PutRetailerInfo_Model> call, Throwable t)
            {
                Utils.dismissProgressDialog(progressDialog);
                Utils.showToast(SelectSalesOrderTypeActivity.this,NO_INTERNET_CONNECTION);
            }
        });



    }

    void insertIntoRetailerVisitTable(String visitID, String retailerFeedback,boolean isonline)
    {
        ContentValues retailerVisitValues = new ContentValues();
        //String visitID = "VISIT_"+getIST()+"_"+getRandomNumber();
        retailerVisitValues.put("visit_id", visitID);
        retailerVisitValues.put("emp_id", loggedInUserID);
        retailerVisitValues.put("retailer_id", retailerID);
        retailerVisitValues.put("visit_date", getDateTime());
        retailerVisitValues.put("has_order", getOrderFlag());
        retailerVisitValues.put("feedback", retailerFeedback);
        retailerVisitValues.put("latitude", lat);
        retailerVisitValues.put("longitude", lng);
        if(isonline){
            retailerVisitValues.put("upload_status", "1");
        }else {

            retailerVisitValues.put("upload_status", "0");
        }
        sqLiteDatabase.insert(TBL_RETAILER_VISIT, null, retailerVisitValues);
    }

    void insertIntoSalesOrderTable(String visitID,String orderid,boolean isonline)
    {
       /* if(!isonline){

            orderid = "ORDER_" + getIST() + "_" + getRandomNumber();
        }*/
        ContentValues salesOrderValues = new ContentValues();
        salesOrderValues.put("order_id", orderid);
        salesOrderValues.put("visit_id", visitID);
        salesOrderValues.put("retailer_id", retailerID);
        salesOrderValues.put("emp_id", new MySharedPrefrencesData().getUser_Id(SelectSalesOrderTypeActivity.this));
        salesOrderValues.put("order_date", getDateTime());
        salesOrderValues.put("created_date", getTodayDate());
        salesOrderValues.put("modified_date", getTodayDate());
        salesOrderValues.put("total_order_value", "0");
        salesOrderValues.put("total_discount", "0");
        salesOrderValues.put("is_regular", "0");
        salesOrderValues.put("is_placed", "0");
        salesOrderValues.put("is_cancelled", "0");
        salesOrderValues.put("upload_status", "0");

        //we are adding new SKUs against active sales order
        //so if there is no order, we shouldn't make that order as active order
        //if we do, new SKUs get added against the order_id where there is no sales order
        if(getOrderFlag().equals("1"))
        {
            salesOrderValues.put("is_active", "1");
        }
        else
        {
            salesOrderValues.put("is_active", "0");
        }

        sqLiteDatabase.insert(TBL_SALES_ORDER, null, salesOrderValues);
    }

    private String getOrderFlag()
    {
        if(selectedOrderType == ORDER_TYPE_NO_ORDER)
        {
            return "0";
        }
        else if(selectedOrderType == ORDER_TYPE_NEW_ORDER || selectedOrderType == ORDER_TYPE_NEW_REGULAR_ORDER)
        {
            return "1";
        }
        else
        {
            return String.valueOf(selectedOrderType);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
    {
        switch(requestCode)
        {
            case REQUEST_CODE_ACCESS_FINE_LOCATION:
            {
                // If request is cancelled, the result arrays are empty.
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    {
                        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, locationListener);
                    }

                    // permission was granted, yay! Do the task you need to do.
                    //LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, locationListener);
                }
                else
                {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void startProgressDialog()
    {
        progressDialog = new ProgressDialog(SelectSalesOrderTypeActivity.this);
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
}
