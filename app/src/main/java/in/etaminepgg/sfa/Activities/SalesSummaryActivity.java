package in.etaminepgg.sfa.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import in.etaminepgg.sfa.InputModel_For_Network.IM_GenerateReportSummary;
import in.etaminepgg.sfa.Models.Distributor;
import in.etaminepgg.sfa.Models.ValidAuthModel;
import in.etaminepgg.sfa.Network.API_Call_Retrofit;
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

import static in.etaminepgg.sfa.Utilities.Constants.TBL_SALES_ORDER;
import static in.etaminepgg.sfa.Utilities.Constants.dbFileFullPath;
import static in.etaminepgg.sfa.Utilities.DbUtils.getRetailerNameAccordingToRetailerId;
import static in.etaminepgg.sfa.Utilities.Utils.getTodayDate;
import static in.etaminepgg.sfa.Utilities.Utils.loggedInUserID;

public class SalesSummaryActivity extends AppCompatActivity
{
    float noOfSalesOrders;
    float noOfVisitsMadeBySalesPerson;
    float duplicate_noOfVisitsMadeBySalesPerson;
    RecyclerView noRetailOrders_RecyclerView;
    TextView retailerVisitsDone_TextView;
    TextView duplicate_retailerVisitsDone_TextView;
    TextView salesConversion_TextView;
    TextView salesFromRetailerVisits_TextView;
    TextView salesValueForTheDay_TextView;
    SQLiteDatabase sqLiteDatabase;
    int valueFromOpenDatabase;
    Button btn_generate_distrributor_summary, btn_generate_pdf;
    MySharedPrefrencesData mySharedPrefrencesData;
    String SQL_SELECT_MY_SALES_HISTORY;
    java.util.List<String> selectionArgsList;
    int a_retailer = 0;
    int b_retailer = 0;
    private Toolbar toolbar;
    private LinearLayout rootlayout;

    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sales_summary);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);

        mySharedPrefrencesData = new MySharedPrefrencesData();
        setSupportActionBar(this.toolbar);
        getSupportActionBar().setTitle((CharSequence) "Sales Summary");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        this.valueFromOpenDatabase = MyDb.openDatabase(Constants.dbFileFullPath);
        this.sqLiteDatabase = MyDb.getDbHandle(this.valueFromOpenDatabase);


        findViewsByIDs();
        setListenerViews();


        //get no.of visit per day acc to user

        this.noOfVisitsMadeBySalesPerson = (float) DbUtils.getRetailerVisitsFor(new MySharedPrefrencesData().getUser_Id(this));

        //get no.of duplicate visits per day acc to user
        this.duplicate_noOfVisitsMadeBySalesPerson = (float) DbUtils.getAllRetailerVisitsFor(new MySharedPrefrencesData().getUser_Id(this));

        //get no.of sales orders
        this.noOfSalesOrders = getNoOfSalesOrdersFor(new MySharedPrefrencesData().getUser_Id(this));

        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        lparams.setLayoutDirection(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.6f);

        LinearLayout.LayoutParams lp4 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, .4f);

        float density = getResources().getDisplayMetrics().density;
        float sp = (getResources().getDimension(R.dimen.text_size_large) / density);
        float sp_double = (getResources().getDimension(R.dimen.text_size_medium_double) / density);


        //set no.of visit according to location assigned to user

        if (mySharedPrefrencesData.getUser_LocationId(getBaseContext()).contains(","))
        {

            String location_IdArr[] = mySharedPrefrencesData.getUser_LocationId(getBaseContext()).split(",");
            String retailerVisit_location_IdArr[] = mySharedPrefrencesData.getRetailerVisit_LocationId(getBaseContext()).split(",");

            // LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


            for (int i = 0; i < location_IdArr.length; i++)
            {

                LinearLayout linearLayout = new LinearLayout(SalesSummaryActivity.this);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout.setLayoutParams(lparams);
                lp1.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen.dim_8));
                linearLayout.setWeightSum(1);


                TextView locationtext = new TextView(this);
                locationtext.setLayoutParams(lp3);
                locationtext.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
                locationtext.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                locationtext.setText("Retailer Visits For " + DbUtils.getLocationName(location_IdArr[i].trim()));
                linearLayout.addView(locationtext);


                TextView locationtext_value = new TextView(this);
                locationtext_value.setLayoutParams(lp4);
                locationtext_value.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp_double);
                locationtext_value.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                locationtext_value.setTypeface(Typeface.DEFAULT_BOLD);
                locationtext_value.setText("" + DbUtils.getRetailerVisitsForLocationId(mySharedPrefrencesData.getUser_Id(getBaseContext()), location_IdArr[i].trim()) + " ( " + retailerVisit_location_IdArr[i] + " ) ");
                linearLayout.addView(locationtext_value);

                this.rootlayout.addView(linearLayout);

            }

            this.rootlayout.removeView(btn_generate_distrributor_summary);
            this.rootlayout.removeView(btn_generate_pdf);

            this.rootlayout.addView(btn_generate_distrributor_summary);

            this.rootlayout.addView(btn_generate_pdf);
        }
        else
        {

            String locationId = mySharedPrefrencesData.getUser_LocationId(this);


            LinearLayout linearLayout = new LinearLayout(SalesSummaryActivity.this);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setLayoutParams(lparams);
            lp1.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen.dim_8));


            TextView locationtext = new TextView(this);
            locationtext.setLayoutParams(lp3);
            locationtext.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
            locationtext.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            locationtext.setText("Retailer Visits For " + DbUtils.getLocationName(locationId.trim()));

            linearLayout.addView(locationtext);


            TextView locationtext_value = new TextView(this);
            locationtext_value.setLayoutParams(lp4);
            locationtext_value.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp_double);
            locationtext_value.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            locationtext_value.setTypeface(Typeface.DEFAULT_BOLD);
            locationtext_value.setText("" + DbUtils.getRetailerVisitsForLocationId(mySharedPrefrencesData.getUser_Id(getBaseContext()), locationId.trim()) + " ( " + mySharedPrefrencesData.getRetailerVisit_LocationId(this) + " ) ");

            linearLayout.addView(locationtext_value);

            this.rootlayout.addView(linearLayout);


            this.rootlayout.removeView(btn_generate_distrributor_summary);
            this.rootlayout.removeView(btn_generate_pdf);
            this.rootlayout.addView(btn_generate_distrributor_summary);
            this.rootlayout.addView(btn_generate_pdf);

        }
    }

    private void setListenerViews()
    {

        //generate distributor report according to backend data submitted.nothing from mobile end.just call api

        btn_generate_distrributor_summary.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (Utils.isNetworkConnected(SalesSummaryActivity.this))
                {

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(SalesSummaryActivity.this);

                    // Setting Dialog Title
                    alertDialog.setTitle("Confirm summary report generation..");

                    // Setting Dialog Message
                    alertDialog.setMessage("Do you wish to proceed with report generation?");

                    // Setting Icon to Dialog
                    alertDialog.setIcon(R.drawable.ic_check_circle);

                    // Setting Positive "Yes" Button
                    alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {

                            // Write your code here to invoke YES event


                            networkcall_for_generateReport();


                        }
                    });

                    // Setting Negative "NO" Button
                    alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            // Write your code here to invoke NO event
                            Toast.makeText(SalesSummaryActivity.this, "You clicked on NO", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                        }
                    });

                    // Showing Alert Message
                    alertDialog.show();
                }
                else
                {

                    Utils.showErrorDialog(SalesSummaryActivity.this, "Please ensure internet connection.");

                }
            }
        });


        //generate pdf according user irrespective of  upload and unupload sales order per current day

        btn_generate_pdf.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                constructQueryAndArgs();

                java.util.List<Distributor> myorderlistWithDistributorName = populateSalesHistoryDistributor();


                if (myorderlistWithDistributorName.size() > 0)
                {

                    for (int i = 0; i < myorderlistWithDistributorName.size(); i++)
                    {


                        createandDisplayPdfDistributor("Sales Order Report ", myorderlistWithDistributorName.get(i));

                    }


                    Utils.showSuccessDialog(SalesSummaryActivity.this, "Sales order reports are successfully generated.To find reports,go to your SFAREPORTs directory inside device storage.");


                }
                else
                {

                    Utils.showToast(getBaseContext(), "No order to generate pdf");


                }


            }
        });
    }


    protected void onPostResume()
    {
        super.onPostResume();
        Resources resources = getResources();
       /* this.retailerVisitsDone_TextView.setText(resources.getString(R.string.label_retailer_visits_done) + " : " + ((int) this.noOfVisitsMadeBySalesPerson));
        int duplicatevisits=((int) this.duplicate_noOfVisitsMadeBySalesPerson)-((int) this.noOfVisitsMadeBySalesPerson);
        this.duplicate_retailerVisitsDone_TextView.setText(resources.getString(R.string.label__duplicate_retailer_visits_done) + " : " + duplicatevisits);
        this.salesFromRetailerVisits_TextView.setText(resources.getString(R.string.label_sales_orders_from_retailer_visits) + " : " + ((int) this.noOfSalesOrders));
        this.salesConversion_TextView.setText(resources.getString(R.string.label_sales_conversion) + " : " + Math.round(getSalesConversion()) + " %");
        this.salesValueForTheDay_TextView.setText(resources.getString(R.string.label_sales_value_for_the_day) + " : " + roundTwoDecimals(getTodaysSalesValue(Utils.loggedInUserID)));*/

        this.retailerVisitsDone_TextView.setText("" + ((int) this.noOfVisitsMadeBySalesPerson));
        int duplicatevisits = ((int) this.duplicate_noOfVisitsMadeBySalesPerson) - ((int) this.noOfVisitsMadeBySalesPerson);
        this.duplicate_retailerVisitsDone_TextView.setText("" + duplicatevisits);
        this.salesFromRetailerVisits_TextView.setText("" + ((int) this.noOfSalesOrders));
        this.salesConversion_TextView.setText("" + Math.round(getSalesConversion()) + " %");
        this.salesValueForTheDay_TextView.setText("" + roundTwoDecimals(getTodaysSalesValue(Utils.loggedInUserID)));
    }

    protected void onDestroy()
    {
        this.sqLiteDatabase.releaseReference();
        super.onDestroy();
    }

    private void findViewsByIDs()
    {
        this.retailerVisitsDone_TextView = (TextView) findViewById(R.id.retailerVisitsDone_TextView);
        this.duplicate_retailerVisitsDone_TextView = (TextView) findViewById(R.id.duplicate_retailerVisitsDone_TextView);
        this.salesFromRetailerVisits_TextView = (TextView) findViewById(R.id.salesFromRetailerVisits_TextView);
        this.salesConversion_TextView = (TextView) findViewById(R.id.salesConversion_TextView);
        this.salesValueForTheDay_TextView = (TextView) findViewById(R.id.salesValueForTheDay_TextView);
        this.noRetailOrders_RecyclerView = (RecyclerView) findViewById(R.id.noRetailOrders_RecyclerView);
        this.btn_generate_distrributor_summary = (Button) findViewById(R.id.btn_generate_distrributor_summary);
        this.btn_generate_pdf = (Button) findViewById(R.id.btn_generate_pdf);
        this.rootlayout = (LinearLayout) findViewById(R.id.linlay_salessummary);
    }


    //get no.of orders per day accto user
    private float getNoOfSalesOrdersFor(String salesPersonId)
    {
        Cursor cursor = this.sqLiteDatabase.rawQuery("SELECT order_id FROM " + Constants.TBL_SALES_ORDER + " WHERE emp_id = ? AND is_placed = ? AND order_date like ? ", new String[]{salesPersonId, "1", Utils.getTodayDate() + "%"});
        this.noOfSalesOrders = (float) cursor.getCount();
        cursor.close();
        return this.noOfSalesOrders;
    }


    //calculate sales conversion
    private float getSalesConversion()
    {
        if (this.duplicate_noOfVisitsMadeBySalesPerson <= 0.0f)
        {
            return 0.0f;
        }
        return (this.noOfSalesOrders / this.duplicate_noOfVisitsMadeBySalesPerson) * 100.0f;
    }

    //get total order value
    private float getTodaysSalesValue(String salesPersonId)
    {
        java.util.List<String> orderIdList = getOrderIdsFor(salesPersonId);
        float salesValueForTheDay = 0.0f;
        if (orderIdList.size() > 0)
        {
            for (String orderId : orderIdList)
            {
                salesValueForTheDay += (float) DbUtils.getOrderTotalFromSalesOrderTBL(orderId);
            }
        }
        return salesValueForTheDay;
    }

    //get orderids for user
    private java.util.List<String> getOrderIdsFor(String salesPersonId)
    {
        java.util.List<String> orderIdList = new ArrayList();
        Cursor cursor = this.sqLiteDatabase.rawQuery("SELECT mobile_order_id FROM " + Constants.TBL_SALES_ORDER + " WHERE emp_id = ? AND is_placed = ? AND order_date like ? ", new String[]{salesPersonId, "1", Utils.getTodayDate() + "%"});
        while (cursor.moveToNext())
        {
            orderIdList.add(cursor.getString(cursor.getColumnIndexOrThrow("mobile_order_id")));
        }
        cursor.close();
        return orderIdList;
    }

    float roundTwoDecimals(float d)
    {
        return Float.valueOf(new DecimalFormat("#.##").format((double) d)).floatValue();
    }


    //api call for distributor report
    private void networkcall_for_generateReport()
    {

        final ProgressDialog progressDialog = new ProgressDialog(SalesSummaryActivity.this);
        Utils.startProgressDialog(SalesSummaryActivity.this, progressDialog);

        final Apimethods methods = API_Call_Retrofit.getretrofit(this).create(Apimethods.class);


        IM_GenerateReportSummary im_generateReportSummary = new IM_GenerateReportSummary(mySharedPrefrencesData.getEmployee_AuthKey(SalesSummaryActivity.this), mySharedPrefrencesData.getUser_Id(SalesSummaryActivity.this));

        Call<ValidAuthModel> call = methods.setSummaryReport(im_generateReportSummary);


        Log.i("putsummary_ip", new Gson().toJson(im_generateReportSummary));

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

                    Log.i("putsummary_op", new Gson().toJson(validAuthModel));

                    if (validAuthModel.getApi_status() == 1)
                    {
                        Utils.showSuccessDialog(SalesSummaryActivity.this, validAuthModel.getMessage());

                        Utils.dismissProgressDialog(progressDialog);
                    }
                }
                else
                {
                    Utils.dismissProgressDialog(progressDialog);


                }

            }


            @Override
            public void onFailure(Call<ValidAuthModel> call, Throwable t)
            {
                Utils.dismissProgressDialog(progressDialog);
                Utils.showToast(SalesSummaryActivity.this, ConstantsA.NO_INTERNET_CONNECTION);

            }
        });


    }


    //create one day pdf data
    void constructQueryAndArgs()
    {
        //COUNT(sod.sku_id) as skuCount, SUM(sod.sku_final_price) as orderTotal
        //select so.order_id, so.retailer_id, so.order_date, sod.sku_id from sales_orders so INNER JOIN sales_order_details sod ON so.order_id = sod.order_id WHERE date(so.order_date) BETWEEN date('2017-10-07') AND date('2017-11-06');


        SQL_SELECT_MY_SALES_HISTORY = "select DISTINCT so.order_id,so.upload_status,so.mobile_order_id, so.mobile_retailer_id,so.total_order_value,so.total_discount, so.order_date,sod.order_detail_id, sod.sku_id, sod.sku_name, sod.sku_qty,sod.sku_free_qty,sod.sku_discount,sod.sku_price, sod.sku_final_price,rtlr.distributor_id,rtlr.distributor_name,rtlr.retailer_name" +
                " from sales_orders so " +
                "INNER JOIN sales_order_details sod " + "ON so.mobile_order_id = sod.mobile_order_id" +
                " INNER JOIN retailers rtlr " + "ON so.mobile_retailer_id = rtlr.mobile_retailer_id";

        selectionArgsList = new ArrayList<>();


        SQL_SELECT_MY_SALES_HISTORY += " AND date(so.order_date) BETWEEN " + "date(?) AND" + " date(?) AND (so.is_placed = 1)  AND so.emp_id = " + loggedInUserID + " ORDER BY rtlr.distributor_id";
        selectionArgsList.add(0, getTodayDate());
        selectionArgsList.add(1, getTodayDate());
        Log.e("list1ELSE", selectionArgsList.toString());


        SQL_SELECT_MY_SALES_HISTORY += ";";

        Log.e("SQL", SQL_SELECT_MY_SALES_HISTORY);
        Log.e("SQL_args", selectionArgsList.toString());
    }

    //pass to adapter
    private List<Distributor> populateSalesHistoryDistributor()
    {
        ArrayList<String> orderidlist = new ArrayList<>();

        ArrayList<String> retailerIDList_contains = new ArrayList<>();
        ArrayList<String> retailerIDList_notContains = new ArrayList<>();

        ArrayList<String> distributorIdList = new ArrayList<>();

        String[] selectionArgs = new String[selectionArgsList.size()];
        selectionArgs = selectionArgsList.toArray(selectionArgs);
        selectionArgsList.clear();
        java.util.List<Distributor> salesHistoryList = new ArrayList<>();

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_MY_SALES_HISTORY, selectionArgs);

        JSONArray resultSet = new JSONArray();
        JSONObject returnObj = new JSONObject();

        ArrayList<Distributor.RetailersPDF> retailersPDFArrayListfinal = new ArrayList<>();

        while (cursor.moveToNext())
        {
            String uploadStatus = cursor.getString(cursor.getColumnIndexOrThrow("upload_status"));

            String orderID = null;

            if (uploadStatus.equalsIgnoreCase("1"))
            {

                orderID = cursor.getString(cursor.getColumnIndexOrThrow("order_id"));

            }
            else
            {

                orderID = cursor.getString(cursor.getColumnIndexOrThrow("mobile_order_id"));

            }
            String retailerID = cursor.getString(cursor.getColumnIndexOrThrow("mobile_retailer_id"));
            String retailerName = cursor.getString(cursor.getColumnIndexOrThrow("retailer_name"));
            String orderDate = cursor.getString(cursor.getColumnIndexOrThrow("order_date"));
            String total_discount = cursor.getString(cursor.getColumnIndexOrThrow("total_discount"));
            String total_order_value = cursor.getString(cursor.getColumnIndexOrThrow("total_order_value"));
            String order_detail_id = cursor.getString(cursor.getColumnIndexOrThrow("order_detail_id"));
            String distributorId = cursor.getString(cursor.getColumnIndexOrThrow("distributor_id"));
            String distributorName = cursor.getString(cursor.getColumnIndexOrThrow("distributor_name"));

          /*  String skuid = cursor.getString(cursor.getColumnIndexOrThrow("sku_id"));
            String skuname = cursor.getString(cursor.getColumnIndexOrThrow("sku_name"));
            String skuqty = cursor.getString(cursor.getColumnIndexOrThrow("sku_qty"));
            String sku_free_qty = cursor.getString(cursor.getColumnIndexOrThrow("sku_free_qty"));
            String sku_discount = cursor.getString(cursor.getColumnIndexOrThrow("sku_discount"));
            String sku_unitPrice = cursor.getString(cursor.getColumnIndexOrThrow("sku_price"));
            String sku_finalprice = cursor.getString(cursor.getColumnIndexOrThrow("sku_final_price"));*/

            if (distributorId != null)
            {

                Log.i("distributorId ....", orderID + " " + distributorId);
                Log.i("distributorName ....", orderID + " " + distributorName);
            }


            //for checking cursor values


            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();

            for (int i = 0; i < totalColumn; i++)
            {
                if (cursor.getColumnName(i) != null)
                {

                    try
                    {

                        if (cursor.getString(i) != null)
                        {
                            Log.d("TAG_NAME", cursor.getString(i));
                            rowObject.put(cursor.getColumnName(i), cursor.getString(i));
                        }
                        else
                        {
                            rowObject.put(cursor.getColumnName(i), "");
                        }
                    }
                    catch (Exception e)
                    {
                        Log.d("TAG_NAME", e.getMessage());
                    }
                }

            }

            resultSet.put(rowObject);


            //for generating list

            ArrayList<Distributor.RetailersPDF> retailersPDFArrayList = new ArrayList<>();
            ArrayList<Distributor.RetailersPDF.ROrders> rOrdersArrayList = new ArrayList<>();
            ArrayList<Distributor.RetailersPDF.ROrders.SkuDetails_Ordered> skuDetails_orderedArrayList = new ArrayList<>();


            if (!distributorIdList.contains(distributorId))
            {


                distributorIdList.add(distributorId);

                retailersPDFArrayListfinal = new ArrayList<>();
                retailersPDFArrayList.clear();
                rOrdersArrayList.clear();
                skuDetails_orderedArrayList.clear();
                retailerIDList_contains.clear();


                String mobileRetailerId = retailerID;
                String mRetailerName = retailerName;

                if (!retailerIDList_contains.contains(mobileRetailerId))
                {

                    retailerIDList_contains.add(mobileRetailerId);

                    String order_id_retailer = "SELECT  mobile_order_id,order_date,SUM(total_order_value) AS GRANDTOTAL,SUM(total_discount) AS TOTALDISCOUNT FROM " + TBL_SALES_ORDER + " WHERE mobile_retailer_id = ? " + " AND date(order_date) BETWEEN " + "date(?) AND" + " date(?) AND (is_placed = 1)  AND emp_id = " + loggedInUserID;

                    String[] selectionarglist = new String[]{mobileRetailerId, getTodayDate(), getTodayDate()};

                    Cursor cursor1 = sqLiteDatabase.rawQuery(order_id_retailer, selectionarglist);

                    while (cursor1.moveToNext())
                    {


                        String orderIDInn = cursor1.getString(cursor1.getColumnIndexOrThrow("mobile_order_id"));
                        String orderDateInn = cursor1.getString(cursor1.getColumnIndexOrThrow("order_date"));
                        String rGrandtotal = cursor1.getString(cursor1.getColumnIndexOrThrow("GRANDTOTAL"));
                        String rTOTALDISCOUNT = cursor1.getString(cursor1.getColumnIndexOrThrow("TOTALDISCOUNT"));


                        String SQL_SELECT_Sales_order_Detail = "select  mobile_order_id,order_detail_id, sku_id, sku_name,sku_qty,sku_price,sku_final_price,sku_price_before_discount,sku_free_qty,sku_discount" +
                                " from sales_order_details " +
                                " WHERE order_id = ? OR mobile_order_id = ?";

                        String[] selectionargs1 = {orderID, orderIDInn};
                        Cursor cursor2 = sqLiteDatabase.rawQuery(SQL_SELECT_Sales_order_Detail, selectionargs1);

                        while (cursor2.moveToNext())
                        {

                            String skuid = cursor2.getString(cursor2.getColumnIndexOrThrow("sku_id"));
                            String skuname = cursor2.getString(cursor2.getColumnIndexOrThrow("sku_name"));
                            String skuqty = cursor2.getString(cursor2.getColumnIndexOrThrow("sku_qty"));
                            String sku_free_qty = cursor2.getString(cursor2.getColumnIndexOrThrow("sku_free_qty"));
                            String sku_discount = cursor2.getString(cursor2.getColumnIndexOrThrow("sku_discount"));
                            String sku_unitPrice = cursor2.getString(cursor2.getColumnIndexOrThrow("sku_price"));
                            String sku_finalprice = cursor2.getString(cursor2.getColumnIndexOrThrow("sku_final_price"));

                            skuDetails_orderedArrayList.add(new Distributor().new RetailersPDF().new ROrders().new SkuDetails_Ordered(orderDate, skuid, skuname, skuqty, sku_free_qty, sku_discount, sku_unitPrice, sku_finalprice));

                        }

                        rOrdersArrayList.add(new Distributor().new RetailersPDF().new ROrders(mobileRetailerId, orderIDInn, orderDateInn, rGrandtotal, rTOTALDISCOUNT, skuDetails_orderedArrayList));
                        String grandTotal = String.valueOf(Float.parseFloat(rGrandtotal) + Float.parseFloat(rTOTALDISCOUNT));
                        //retailersPDFArrayList.add(new Distributor().new RetailersPDF(mobileRetailerId,mRetailerName,rGrandtotal,rTOTALDISCOUNT,grandTotal,rOrdersArrayList));
                        retailersPDFArrayList.add(new Distributor().new RetailersPDF(mobileRetailerId, mRetailerName, grandTotal, rTOTALDISCOUNT, rGrandtotal, rOrdersArrayList));

                        retailersPDFArrayListfinal.addAll(retailersPDFArrayList);

                        Log.e("retailerlist", new Gson().toJson(retailersPDFArrayListfinal));

                        cursor2.close();


                    }

                    salesHistoryList.add(new Distributor(distributorId, distributorName, retailersPDFArrayListfinal));

                    cursor1.close();


                }


            }
            else
            {

                String mobileRetailerId = retailerID;
                String mRetailerName = retailerName;

                if (!retailerIDList_contains.contains(mobileRetailerId))
                {

                    retailerIDList_contains.add(mobileRetailerId);

                    String order_id_retailer = "SELECT  mobile_order_id,order_date,SUM(total_order_value) AS GRANDTOTAL,SUM(total_discount) AS TOTALDISCOUNT FROM " + TBL_SALES_ORDER + " WHERE mobile_retailer_id = ? " + " AND date(order_date) BETWEEN " + "date(?) AND" + " date(?) AND (is_placed = 1)  AND emp_id = " + loggedInUserID;

                    String[] selectionarglist = new String[]{mobileRetailerId, getTodayDate(), getTodayDate()};

                    Cursor cursor1 = sqLiteDatabase.rawQuery(order_id_retailer, selectionarglist);

                    while (cursor1.moveToNext())
                    {


                        String orderIDInn = cursor1.getString(cursor1.getColumnIndexOrThrow("mobile_order_id"));
                        String orderDateInn = cursor1.getString(cursor1.getColumnIndexOrThrow("order_date"));
                        String rGrandtotal = cursor1.getString(cursor1.getColumnIndexOrThrow("GRANDTOTAL"));
                        String rTOTALDISCOUNT = cursor1.getString(cursor1.getColumnIndexOrThrow("TOTALDISCOUNT"));


                        String SQL_SELECT_Sales_order_Detail = "select  mobile_order_id,order_detail_id, sku_id, sku_name,sku_qty,sku_price,sku_final_price,sku_price_before_discount,sku_free_qty,sku_discount" +
                                " from sales_order_details " +
                                " WHERE order_id = ? OR mobile_order_id = ?";

                        String[] selectionargs1 = {orderID, orderIDInn};
                        Cursor cursor2 = sqLiteDatabase.rawQuery(SQL_SELECT_Sales_order_Detail, selectionargs1);

                        while (cursor2.moveToNext())
                        {

                            String skuid = cursor2.getString(cursor2.getColumnIndexOrThrow("sku_id"));
                            String skuname = cursor2.getString(cursor2.getColumnIndexOrThrow("sku_name"));
                            String skuqty = cursor2.getString(cursor2.getColumnIndexOrThrow("sku_qty"));
                            String sku_free_qty = cursor2.getString(cursor2.getColumnIndexOrThrow("sku_free_qty"));
                            String sku_discount = cursor2.getString(cursor2.getColumnIndexOrThrow("sku_discount"));
                            String sku_unitPrice = cursor2.getString(cursor2.getColumnIndexOrThrow("sku_price"));
                            String sku_finalprice = cursor2.getString(cursor2.getColumnIndexOrThrow("sku_final_price"));

                            skuDetails_orderedArrayList.add(new Distributor().new RetailersPDF().new ROrders().new SkuDetails_Ordered(orderDate, skuid, skuname, skuqty, sku_free_qty, sku_discount, sku_unitPrice, sku_finalprice));

                        }

                        rOrdersArrayList.add(new Distributor().new RetailersPDF().new ROrders(mobileRetailerId, orderIDInn, orderDateInn, rGrandtotal, rTOTALDISCOUNT, skuDetails_orderedArrayList));
                        String grandTotal = String.valueOf(Float.parseFloat(rGrandtotal) + Float.parseFloat(rTOTALDISCOUNT));
                        // retailersPDFArrayList.add(new Distributor().new RetailersPDF(mobileRetailerId,mRetailerName,rGrandtotal,rTOTALDISCOUNT,grandTotal,rOrdersArrayList));
                        retailersPDFArrayList.add(new Distributor().new RetailersPDF(mobileRetailerId, mRetailerName, grandTotal, rTOTALDISCOUNT, rGrandtotal, rOrdersArrayList));
                        retailersPDFArrayListfinal.addAll(retailersPDFArrayList);

                        Log.e("retailerlist", new Gson().toJson(retailersPDFArrayListfinal));

                        cursor2.close();


                    }

                    salesHistoryList.remove(salesHistoryList.size() - 1);

                    salesHistoryList.add(new Distributor(distributorId, distributorName, retailersPDFArrayListfinal));


                    cursor1.close();


                }


            }


        }

        Log.d("TAG_NAME", resultSet.toString());
        Log.e("orderlist", new Gson().toJson(salesHistoryList));

        cursor.close();
        sqLiteDatabase.close();


        return salesHistoryList;

    }

    // Method for creating a pdf file from text, saving it then opening it for display
    private void createandDisplayPdfDistributor(String text, Distributor distributor)
    {
        Document doc = new Document();

        String viewpdffile = null;

        try
        {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SFAREPORTS";

            File dir = new File(path);
            if (!dir.exists())
            {
                dir.mkdirs();
            }
            String pdfFileName = "";
            if (distributor.getDistributor_name() != null)
            {


                pdfFileName = "SFA_ORDER_" + distributor.getDistributor_name() + "_" + Utils.getIST() + ".pdf";
            }
            else
            {

                pdfFileName = "SFA_ORDER_" + distributor.getDistributor_id() + "_" + Utils.getIST() + ".pdf";

            }


            viewpdffile = pdfFileName;

            File file = new File(dir, pdfFileName);
            FileOutputStream fOut = new FileOutputStream(file);

            PdfWriter writer = PdfWriter.getInstance(doc, fOut);

            //open the document
            doc.open();

            Font boldFnt = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD, BaseColor.RED);
            Font boldFntBlack = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD, BaseColor.BLACK);
            Font blueFnt = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.ITALIC, BaseColor.BLUE);

            Paragraph p1 = new Paragraph(text, boldFnt);

            // Font paraFont = new Font(Font.FontFamily.HELVETICA);
            p1.setAlignment(Paragraph.ALIGN_CENTER);
            // p1.setFont(paraFont);


            //add paragraph to document
            doc.add(p1);


            // add a couple of blank lines
            doc.add(Chunk.NEWLINE);

            // add a couple of blank lines
            doc.add(Chunk.NEWLINE);


            Paragraph pDName = new Paragraph(distributor.getDistributor_name(), boldFntBlack);


            //add paragraph to document
            doc.add(pDName);


            // add a couple of blank lines
            doc.add(Chunk.NEWLINE);


           /* com.itextpdf.text.List list = new com.itextpdf.text.List(com.itextpdf.text.List.ORDERED);
            list.setAutoindent(false);
            list.setSymbolIndent(42);*/


            for (Distributor.RetailersPDF retailersPDF : distributor.getRetailersArrayList())
            {
                com.itextpdf.text.List list = new com.itextpdf.text.List(false, 10);
              /*  list.setAutoindent(false);
                list.setSymbolIndent(42);*/
                //  list.add("Retailer name : " + getRetailerNameAccordingToRetailerId(mySalesHistory.getRetailerId()));
                //  doc.add(list);

                doc.add(new Paragraph("Retailer name : " + getRetailerNameAccordingToRetailerId(retailersPDF.getRetailer_id()), blueFnt));


                // add a couple of blank lines
                doc.add(Chunk.NEWLINE);

                PdfPTable table = new PdfPTable(new float[]{2, 1, 3, 1, 1, 2, 2, 2});
                table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell("Date/Time");
                table.addCell("Sr.");
                table.addCell("SKU NAME");
                table.addCell("SKU QTY");
                table.addCell("SKU FREEQTY");
                table.addCell("SKU UNIT PRICE");
                table.addCell("SKU DISCOUNT");
                table.addCell("SKU PRICE");
                table.setHeaderRows(1);
                PdfPCell[] cells = table.getRow(0).getCells();
                for (int j = 0; j < cells.length; j++)
                {
                    cells[j].setBackgroundColor(BaseColor.GRAY);
                }

                for (Distributor.RetailersPDF.ROrders rOrders : retailersPDF.getrOrdersArrayList())
                {

                    int a = 1;

                    for (Distributor.RetailersPDF.ROrders.SkuDetails_Ordered skuDetails_ordered : rOrders.getSkuDetails_orderedArrayList())
                    {


                        table.addCell(skuDetails_ordered.getOrder_date_time());
                        table.addCell(a + "");
                        table.addCell(skuDetails_ordered.getSku_name());
                        table.addCell(skuDetails_ordered.getSku_qty());
                        table.addCell(skuDetails_ordered.getSku_free_qty());
                        table.addCell(skuDetails_ordered.getSku_unit_price());
                        table.addCell(skuDetails_ordered.getSku_discount());
                        table.addCell(skuDetails_ordered.getSku_finalprice());
                        a++;
                    }

                }

                doc.add(table);


                // add a couple of blank lines
                doc.add(Chunk.NEWLINE);

               /* PdfContentByte canvas = writer.getDirectContent();
                CMYKColor magentaColor = new CMYKColor(0.f, 1.f, 0.f, 0.f);
                canvas.setColorStroke(magentaColor);
                canvas.moveTo(36, 36);
                canvas.lineTo(36, 806);
                canvas.lineTo(559, 36);
                canvas.lineTo(559, 806);
                canvas.closePathStroke();
*/

                doc.add(new Paragraph("Sub Total : " + retailersPDF.getrSubTotal()));
                doc.add(new Paragraph("Total Discount : " + retailersPDF.getrTotalDiscount()));
                doc.add(new Paragraph("Grand Total : " + retailersPDF.getrGrandTotal(), boldFntBlack));


                // add a couple of blank lines
                doc.add(Chunk.NEWLINE);

                LineSeparator ls = new LineSeparator();
                doc.add(new Chunk(ls));


                /*DottedLineSeparator separator = new DottedLineSeparator();
                separator.setPercentage(59000f / 523f);
                Chunk linebreak = new Chunk(separator);
                doc.add(linebreak);*/


            }


        }
        catch (DocumentException de)
        {
            Log.e("PDFCreator", "DocumentException:" + de);
        }
        catch (IOException e)
        {
            Log.e("PDFCreator", "ioException:" + e);
        }
        finally
        {
            doc.close();
        }


    }


}
