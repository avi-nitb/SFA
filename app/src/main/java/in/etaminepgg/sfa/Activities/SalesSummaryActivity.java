package in.etaminepgg.sfa.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import in.etaminepgg.sfa.InputModel_For_Network.IM_GenerateReportSummary;
import in.etaminepgg.sfa.InputModel_For_Network.IM_IsValidAuthKey;
import in.etaminepgg.sfa.InputModel_For_Network.IM_Login;
import in.etaminepgg.sfa.Models.AuthUser_Model;
import in.etaminepgg.sfa.Models.GetSkuCategorySubCategorylist;
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

import static in.etaminepgg.sfa.Utilities.Constants.TBL_SKU_CATEGORY;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SKU_SUBCATEGORY;
import static in.etaminepgg.sfa.Utilities.Constants.dbFileFullPath;

public class SalesSummaryActivity extends AppCompatActivity {
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
    private Toolbar toolbar;
    private LinearLayout rootlayout;
    int valueFromOpenDatabase;
    Button btn_generate_distrributor_summary;

    MySharedPrefrencesData mySharedPrefrencesData;

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_summary);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);

        mySharedPrefrencesData=new MySharedPrefrencesData();
        setSupportActionBar(this.toolbar);
        getSupportActionBar().setTitle((CharSequence) "Sales Summary");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        this.valueFromOpenDatabase = MyDb.openDatabase(Constants.dbFileFullPath);
        this.sqLiteDatabase = MyDb.getDbHandle(this.valueFromOpenDatabase);
        findViewsByIDs();
        setListenerViews();
        this.noOfVisitsMadeBySalesPerson = (float) DbUtils.getRetailerVisitsFor(new MySharedPrefrencesData().getUser_Id(this));
        this.duplicate_noOfVisitsMadeBySalesPerson = (float) DbUtils.getAllRetailerVisitsFor(new MySharedPrefrencesData().getUser_Id(this));
        this.noOfSalesOrders = getNoOfSalesOrdersFor(new MySharedPrefrencesData().getUser_Id(this));

        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        lparams.setLayoutDirection(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,0.6f);

        LinearLayout.LayoutParams lp4 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,.4f);

        float density = getResources().getDisplayMetrics().density;
        float sp=(getResources().getDimension(R.dimen.text_size_large)/density);
        float sp_double=(getResources().getDimension(R.dimen.text_size_medium_double)/density);

        if(mySharedPrefrencesData.getUser_LocationId(getBaseContext()).contains(",")){

            String location_IdArr[]=mySharedPrefrencesData.getUser_LocationId(getBaseContext()).split(",");
            String retailerVisit_location_IdArr[]=mySharedPrefrencesData.getRetailerVisit_LocationId(getBaseContext()).split(",");

           // LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


            for(int i=0;i<location_IdArr.length;i++){

                LinearLayout linearLayout = new LinearLayout(SalesSummaryActivity.this);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout.setLayoutParams(lparams);
                lp1.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen.dim_8));
                linearLayout.setWeightSum(1);


                TextView locationtext=new TextView(this);
                locationtext.setLayoutParams(lp3);
                locationtext.setTextSize(TypedValue.COMPLEX_UNIT_SP,sp);
                locationtext.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                locationtext.setText("Retailer Visits For "+DbUtils.getLocationName(location_IdArr[i].trim()));
                linearLayout.addView(locationtext);


                TextView locationtext_value=new TextView(this);
                locationtext_value.setLayoutParams(lp4);
                locationtext_value.setTextSize(TypedValue.COMPLEX_UNIT_SP,sp_double);
                locationtext_value.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                locationtext_value.setTypeface(Typeface.DEFAULT_BOLD);
                locationtext_value.setText(""+DbUtils.getRetailerVisitsForLocationId(mySharedPrefrencesData.getUser_Id(getBaseContext()),location_IdArr[i].trim())+" ( "+retailerVisit_location_IdArr[i]+" ) ");
                linearLayout.addView(locationtext_value);

                this.rootlayout.addView(linearLayout);

            }

            this.rootlayout.removeView(btn_generate_distrributor_summary);

            this.rootlayout.addView(btn_generate_distrributor_summary);
        }else {

            String locationId=mySharedPrefrencesData.getUser_LocationId(this);


            LinearLayout linearLayout = new LinearLayout(SalesSummaryActivity.this);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setLayoutParams(lparams);
            lp1.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen.dim_8));


            TextView locationtext=new TextView(this);
            locationtext.setLayoutParams(lp3);
            locationtext.setTextSize(TypedValue.COMPLEX_UNIT_SP,sp);
            locationtext.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            locationtext.setText("Retailer Visits For "+DbUtils.getLocationName(locationId.trim()));

            linearLayout.addView(locationtext);




            TextView locationtext_value=new TextView(this);
            locationtext_value.setLayoutParams(lp4);
            locationtext_value.setTextSize(TypedValue.COMPLEX_UNIT_SP,sp_double);
            locationtext_value.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            locationtext_value.setTypeface(Typeface.DEFAULT_BOLD);
            locationtext_value.setText(""+DbUtils.getRetailerVisitsForLocationId(mySharedPrefrencesData.getUser_Id(getBaseContext()),locationId.trim())+" ( "+mySharedPrefrencesData.getRetailerVisit_LocationId(this)+" ) ");

            linearLayout.addView(locationtext_value);

            this.rootlayout.addView(linearLayout);





            this.rootlayout.removeView(btn_generate_distrributor_summary);
            this.rootlayout.addView(btn_generate_distrributor_summary);

        }
    }

    private void setListenerViews()
    {

        btn_generate_distrributor_summary.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(Utils.isNetworkConnected(SalesSummaryActivity.this)){

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(SalesSummaryActivity.this);

                    // Setting Dialog Title
                    alertDialog.setTitle("Confirm summary report generation..");

                    // Setting Dialog Message
                    alertDialog.setMessage("Do you wish to proceed with report generation?");

                    // Setting Icon to Dialog
                    alertDialog.setIcon(R.drawable.ic_check_circle);

                    // Setting Positive "Yes" Button
                    alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int which) {

                            // Write your code here to invoke YES event


                            networkcall_for_generateReport();


                        }
                    });

                    // Setting Negative "NO" Button
                    alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to invoke NO event
                            Toast.makeText(SalesSummaryActivity.this, "You clicked on NO", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                        }
                    });

                    // Showing Alert Message
                    alertDialog.show();
                }else {

                    Utils.showErrorDialog(SalesSummaryActivity.this,"Please ensure internet connection.");

                }
            }
        });
    }

    protected void onPostResume() {
        super.onPostResume();
        Resources resources = getResources();
       /* this.retailerVisitsDone_TextView.setText(resources.getString(R.string.label_retailer_visits_done) + " : " + ((int) this.noOfVisitsMadeBySalesPerson));
        int duplicatevisits=((int) this.duplicate_noOfVisitsMadeBySalesPerson)-((int) this.noOfVisitsMadeBySalesPerson);
        this.duplicate_retailerVisitsDone_TextView.setText(resources.getString(R.string.label__duplicate_retailer_visits_done) + " : " + duplicatevisits);
        this.salesFromRetailerVisits_TextView.setText(resources.getString(R.string.label_sales_orders_from_retailer_visits) + " : " + ((int) this.noOfSalesOrders));
        this.salesConversion_TextView.setText(resources.getString(R.string.label_sales_conversion) + " : " + Math.round(getSalesConversion()) + " %");
        this.salesValueForTheDay_TextView.setText(resources.getString(R.string.label_sales_value_for_the_day) + " : " + roundTwoDecimals(getTodaysSalesValue(Utils.loggedInUserID)));*/

        this.retailerVisitsDone_TextView.setText(""+((int) this.noOfVisitsMadeBySalesPerson));
        int duplicatevisits=((int) this.duplicate_noOfVisitsMadeBySalesPerson)-((int) this.noOfVisitsMadeBySalesPerson);
        this.duplicate_retailerVisitsDone_TextView.setText("" + duplicatevisits);
        this.salesFromRetailerVisits_TextView.setText( "" + ((int) this.noOfSalesOrders));
        this.salesConversion_TextView.setText("" + Math.round(getSalesConversion()) + " %");
        this.salesValueForTheDay_TextView.setText( "" + roundTwoDecimals(getTodaysSalesValue(Utils.loggedInUserID)));
    }

    protected void onDestroy() {
        this.sqLiteDatabase.releaseReference();
        super.onDestroy();
    }

    private void findViewsByIDs() {
        this.retailerVisitsDone_TextView = (TextView) findViewById(R.id.retailerVisitsDone_TextView);
        this.duplicate_retailerVisitsDone_TextView = (TextView) findViewById(R.id.duplicate_retailerVisitsDone_TextView);
        this.salesFromRetailerVisits_TextView = (TextView) findViewById(R.id.salesFromRetailerVisits_TextView);
        this.salesConversion_TextView = (TextView) findViewById(R.id.salesConversion_TextView);
        this.salesValueForTheDay_TextView = (TextView) findViewById(R.id.salesValueForTheDay_TextView);
        this.noRetailOrders_RecyclerView = (RecyclerView) findViewById(R.id.noRetailOrders_RecyclerView);
        this.btn_generate_distrributor_summary = (Button) findViewById(R.id.btn_generate_distrributor_summary);
        this.rootlayout = (LinearLayout) findViewById(R.id.linlay_salessummary);
    }

    private float getNoOfSalesOrdersFor(String salesPersonId) {
        Cursor cursor = this.sqLiteDatabase.rawQuery("SELECT order_id FROM " + Constants.TBL_SALES_ORDER + " WHERE emp_id = ? AND is_placed = ? AND order_date like ? ", new String[]{salesPersonId, "1", Utils.getTodayDate() + "%"});
        this.noOfSalesOrders = (float) cursor.getCount();
        cursor.close();
        return this.noOfSalesOrders;
    }

    private float getSalesConversion() {
        if (this.duplicate_noOfVisitsMadeBySalesPerson <= 0.0f) {
            return 0.0f;
        }
        return (this.noOfSalesOrders / this.duplicate_noOfVisitsMadeBySalesPerson) * 100.0f;
    }

    private float getTodaysSalesValue(String salesPersonId) {
        List<String> orderIdList = getOrderIdsFor(salesPersonId);
        float salesValueForTheDay = 0.0f;
        if (orderIdList.size() > 0) {
            for (String orderId : orderIdList) {
                salesValueForTheDay += (float) DbUtils.getOrderTotalFromSalesOrderTBL(orderId);
            }
        }
        return salesValueForTheDay;
    }

    private List<String> getOrderIdsFor(String salesPersonId) {
        List<String> orderIdList = new ArrayList();
        Cursor cursor = this.sqLiteDatabase.rawQuery("SELECT order_id FROM " + Constants.TBL_SALES_ORDER + " WHERE emp_id = ? AND is_placed = ? AND order_date like ? ", new String[]{salesPersonId, "1", Utils.getTodayDate() + "%"});
        while (cursor.moveToNext()) {
            orderIdList.add(cursor.getString(cursor.getColumnIndexOrThrow("order_id")));
        }
        cursor.close();
        return orderIdList;
    }

    float roundTwoDecimals(float d) {
        return Float.valueOf(new DecimalFormat("#.##").format((double) d)).floatValue();
    }


    private void networkcall_for_generateReport()
    {

        final ProgressDialog progressDialog = new ProgressDialog(SalesSummaryActivity.this);
        Utils.startProgressDialog(SalesSummaryActivity.this, progressDialog);

        final Apimethods methods = API_Call_Retrofit.getretrofit(this).create(Apimethods.class);


        IM_GenerateReportSummary im_generateReportSummary=new IM_GenerateReportSummary(mySharedPrefrencesData.getEmployee_AuthKey(SalesSummaryActivity.this), mySharedPrefrencesData.getUser_Id(SalesSummaryActivity.this));

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
                        Utils.showSuccessDialog(SalesSummaryActivity.this,validAuthModel.getMessage());

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
}
