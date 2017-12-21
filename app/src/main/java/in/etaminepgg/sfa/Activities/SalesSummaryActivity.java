package in.etaminepgg.sfa.Activities;

import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.etaminepgg.sfa.R;
import in.etaminepgg.sfa.Utilities.DbUtils;
import in.etaminepgg.sfa.Utilities.MyDb;

import static in.etaminepgg.sfa.Utilities.Constants.TBL_RETAILER_VISIT;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SALES_ORDER;
import static in.etaminepgg.sfa.Utilities.Constants.dbFileFullPath;
import static in.etaminepgg.sfa.Utilities.DbUtils.getOrderTotal;
import static in.etaminepgg.sfa.Utilities.Utils.getTodayDate;
import static in.etaminepgg.sfa.Utilities.Utils.loggedInUserID;

public class SalesSummaryActivity extends AppCompatActivity
{
    TextView retailerVisitsDone_TextView, salesFromRetailerVisits_TextView, salesConversion_TextView, salesValueForTheDay_TextView;
    RecyclerView noRetailOrders_RecyclerView;

    int valueFromOpenDatabase;
    SQLiteDatabase sqLiteDatabase;

    float noOfVisitsMadeBySalesPerson;
    float noOfSalesOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_summary);

        valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        findViewsByIDs();

        noOfVisitsMadeBySalesPerson = DbUtils.getRetailerVisitsFor(loggedInUserID);
        noOfSalesOrders = getNoOfSalesOrdersFor(loggedInUserID);

        /*noRetailOrders_RecyclerView.setLayoutManager(new LinearLayoutManager(this));
        noRetailOrders_RecyclerView.setItemAnimator(new DefaultItemAnimator());
        noRetailOrders_RecyclerView.setAdapter(new noRetailOrdersAdapter());*/
    }

    @Override
    protected void onPostResume()
    {
        super.onPostResume();
        Resources resources = getResources();
        retailerVisitsDone_TextView.setText(resources.getString(R.string.label_retailer_visits_done) + " : " + (int) noOfVisitsMadeBySalesPerson);
        salesFromRetailerVisits_TextView.setText(resources.getString(R.string.label_sales_orders_from_retailer_visits) + " : " + (int) noOfSalesOrders);
        salesConversion_TextView.setText(resources.getString(R.string.label_sales_conversion) + " : " + getSalesConversion() + " %");
        salesValueForTheDay_TextView.setText(resources.getString(R.string.label_sales_value_for_the_day) + " : " + getTodaysSalesValue(loggedInUserID));
    }

    @Override
    protected void onDestroy()
    {
        sqLiteDatabase.releaseReference();
        super.onDestroy();
    }

    private void findViewsByIDs()
    {
        retailerVisitsDone_TextView = (TextView) findViewById(R.id.retailerVisitsDone_TextView);
        salesFromRetailerVisits_TextView = (TextView) findViewById(R.id.salesFromRetailerVisits_TextView);
        salesConversion_TextView = (TextView) findViewById(R.id.salesConversion_TextView);
        salesValueForTheDay_TextView = (TextView) findViewById(R.id.salesValueForTheDay_TextView);
        noRetailOrders_RecyclerView = (RecyclerView) findViewById(R.id.noRetailOrders_RecyclerView);
    }

    private float getNoOfSalesOrdersFor(String salesPersonId)
    {
        String SQL_SELECT_SALES_ORDERS =
                "SELECT" + " visit_id " + "FROM " + TBL_RETAILER_VISIT + " WHERE " + "emp_id " + "= ? AND " + "has_order " + "= ? AND " + "visit_date " + "like ? " + "GROUP BY retailer_id";
        String[] selectionArgs = {salesPersonId, "1", getTodayDate() + "%"};
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_SALES_ORDERS, selectionArgs);
        noOfSalesOrders = cursor.getCount();
        cursor.close();
        return noOfSalesOrders;
    }

    //Caution: SalesConversion is calculated from visits table
    //we don't know whether order is placed or pending as we don't have that flag in visits table
    //So, the conversion we show may be wrong
    private float getSalesConversion()
    {
        if(noOfVisitsMadeBySalesPerson <= 0)
        {
            return 0;
        }
        else
        {
            return (noOfSalesOrders / noOfVisitsMadeBySalesPerson) * 100;
        }
    }

    private float getTodaysSalesValue(String salesPersonId)
    {
        List<String> orderIdList = getOrderIdsFor(salesPersonId);

        float salesValueForTheDay = 0;

        if(orderIdList.size() > 0)
        {
            for(String orderId : orderIdList)
            {
                salesValueForTheDay = salesValueForTheDay + getOrderTotal(orderId);
            }
        }

        return salesValueForTheDay;
    }

    //for given sales person, this method gets ids of sales orders which are PLACED today
    private List<String> getOrderIdsFor(String salesPersonId)
    {
        List<String> orderIdList = new ArrayList<>();

        String SQL_SELECT_SALES_ORDER_IDS =
                "SELECT" + " order_id " + "FROM " + TBL_SALES_ORDER + " WHERE " + "emp_id " + "= ? AND " + "is_placed " + "= ? AND " + "order_date " + "like ? ";

        String[] selectionArgs = {salesPersonId, "1", getTodayDate() + "%"};

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_SALES_ORDER_IDS, selectionArgs);

        while(cursor.moveToNext())
        {
            String orderID = cursor.getString(cursor.getColumnIndexOrThrow("order_id"));
            orderIdList.add(orderID);
        }

        cursor.close();
        return orderIdList;
    }
}
