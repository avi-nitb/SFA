package in.etaminepgg.sfa.Activities;

import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import in.etaminepgg.sfa.R;
import in.etaminepgg.sfa.Utilities.Constants;
import in.etaminepgg.sfa.Utilities.DbUtils;
import in.etaminepgg.sfa.Utilities.MyDb;
import in.etaminepgg.sfa.Utilities.MySharedPrefrencesData;
import in.etaminepgg.sfa.Utilities.Utils;

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
        this.noOfVisitsMadeBySalesPerson = (float) DbUtils.getRetailerVisitsFor(new MySharedPrefrencesData().getUser_Id(this));
        this.duplicate_noOfVisitsMadeBySalesPerson = (float) DbUtils.getAllRetailerVisitsFor(new MySharedPrefrencesData().getUser_Id(this));
        this.noOfSalesOrders = getNoOfSalesOrdersFor(new MySharedPrefrencesData().getUser_Id(this));

        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        if(mySharedPrefrencesData.getUser_LocationId(getBaseContext()).contains(",")){

            String location_IdArr[]=mySharedPrefrencesData.getUser_LocationId(getBaseContext()).split(",");
            String retailerVisit_location_IdArr[]=mySharedPrefrencesData.getRetailerVisit_LocationId(getBaseContext()).split(",");


            for(int i=0;i<location_IdArr.length;i++){

                TextView locationtext=new TextView(this);
                locationtext.setLayoutParams(lparams);
                locationtext.setTextSize(14);
                locationtext.setGravity(Gravity.CENTER);
                lparams.setMargins(0,20,0,0);
                locationtext.setTextColor(getResources().getColor(R.color.colorGreen));
                locationtext.setText("Retailer Visits Done For "+DbUtils.getLocationName(location_IdArr[i].trim())+" : "+DbUtils.getRetailerVisitsForLocationId(mySharedPrefrencesData.getUser_Id(getBaseContext()),location_IdArr[i].trim())+" ( "+retailerVisit_location_IdArr[i]+" ) ");
                this.rootlayout.addView(locationtext);

            }
        }else {

            String locationId=mySharedPrefrencesData.getUser_LocationId(this);

            TextView locationtext=new TextView(this);
            locationtext.setLayoutParams(lparams);
            locationtext.setTextSize(14);
            locationtext.setGravity(Gravity.CENTER);
            lparams.setMargins(0,20,0,0);
            locationtext.setTextColor(getResources().getColor(R.color.colorGreen));
            locationtext.setText("Retailer Visits Done For "+DbUtils.getLocationName(locationId.trim())+" : "+DbUtils.getRetailerVisitsForLocationId(mySharedPrefrencesData.getUser_Id(getBaseContext()),locationId.trim())+" ( "+mySharedPrefrencesData.getRetailerVisit_LocationId(this)+" ) ");
            this.rootlayout.addView(locationtext);

        }
    }

    protected void onPostResume() {
        super.onPostResume();
        Resources resources = getResources();
        this.retailerVisitsDone_TextView.setText(resources.getString(R.string.label_retailer_visits_done) + " : " + ((int) this.noOfVisitsMadeBySalesPerson));
        int duplicatevisits=((int) this.duplicate_noOfVisitsMadeBySalesPerson)-((int) this.noOfVisitsMadeBySalesPerson);
        this.duplicate_retailerVisitsDone_TextView.setText(resources.getString(R.string.label__duplicate_retailer_visits_done) + " : " + duplicatevisits);
        this.salesFromRetailerVisits_TextView.setText(resources.getString(R.string.label_sales_orders_from_retailer_visits) + " : " + ((int) this.noOfSalesOrders));
        this.salesConversion_TextView.setText(resources.getString(R.string.label_sales_conversion) + " : " + Math.round(getSalesConversion()) + " %");
        this.salesValueForTheDay_TextView.setText(resources.getString(R.string.label_sales_value_for_the_day) + " : " + roundTwoDecimals(getTodaysSalesValue(Utils.loggedInUserID)));
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
                salesValueForTheDay += (float) DbUtils.getOrderTotal(orderId);
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
}
