package in.etaminepgg.sfa.Activities;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import in.etaminepgg.sfa.Adapters.MySalesHistoryAdapter;
import in.etaminepgg.sfa.Models.MySalesHistory;
import in.etaminepgg.sfa.Models.RetailerGroupHistory;
import in.etaminepgg.sfa.Models.SkuGroupHistory;
import in.etaminepgg.sfa.R;
import in.etaminepgg.sfa.Utilities.Constants;
import in.etaminepgg.sfa.Utilities.DbUtils;
import in.etaminepgg.sfa.Utilities.MyDb;
import in.etaminepgg.sfa.Utilities.MySharedPrefrencesData;
import in.etaminepgg.sfa.Utilities.Utils;

import static in.etaminepgg.sfa.Utilities.Constants.TBL_CONFIG;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SKU;
import static in.etaminepgg.sfa.Utilities.Constants.dbFileFullPath;
import static in.etaminepgg.sfa.Utilities.Utils.loggedInUserID;

public class MySalesHistoryActivity extends AppCompatActivity
{
    private static final String TAG = "MySalesHistoryActivity";
    private static final String ANY = "ANY";
    private static final String COLON = " : ";
    Spinner retailer_Spinner, sku_Spinner;

    TextView startDate_TextView, endDate_TextView, emptyHistory_TextView;
    Button showHistory_Button;
    RecyclerView mySalesHistory_RecyclerView;
    DatePickerDialog startDatePickerDialog, endDatePickerDialog;
    DatePickerDialog.OnDateSetListener startDatePickerDialogListener, endDatePickerDialogListener;
    Calendar cal;

    String startDateChosen, endDateChosen;
    String SQL_SELECT_MY_SALES_HISTORY;
    List<String> selectionArgsList;
    MySalesHistoryAdapter mySalesHistoryAdapter;
    int default_date_range_month;
    int maximum_date_range_month;
    private int startYear, startMonth, startDate, endYear, endMonth, endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_my_sales_history);

        loggedInUserID = new MySharedPrefrencesData().getUser_Id(MySalesHistoryActivity.this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sales History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        default_date_range_month = getDefaultMonthRangeFromConfig();
        maximum_date_range_month = getMaximumRangeFromConfig();

        retailer_Spinner = (Spinner) findViewById(R.id.retailer_Spinner);
        sku_Spinner = (Spinner) findViewById(R.id.sku_Spinner);
        startDate_TextView = (TextView) findViewById(R.id.startDate_TextView);
        endDate_TextView = (TextView) findViewById(R.id.endDate_TextView);
        emptyHistory_TextView = (TextView) findViewById(R.id.emptyHistory_TextView);
        showHistory_Button = (Button) findViewById(R.id.showHistory_Button);
        mySalesHistory_RecyclerView = (RecyclerView) findViewById(R.id.mySalesHistory_RecyclerView);


        mySalesHistory_RecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mySalesHistory_RecyclerView.setItemAnimator(new DefaultItemAnimator());


        //adapter for sku spinner

        ArrayAdapter<SkuGroupHistory> skuAdapter = new ArrayAdapter<SkuGroupHistory>(this, android.R.layout.simple_spinner_item, getCustomSkuList());
        skuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sku_Spinner.setAdapter(skuAdapter);

        //adapter for retailer

        ArrayAdapter<RetailerGroupHistory> retailerAdapter = new ArrayAdapter<RetailerGroupHistory>(this, android.R.layout.simple_spinner_item, getCustomRetailerList());
        retailerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        retailer_Spinner.setAdapter(retailerAdapter);


        //start date

        startDatePickerDialogListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
            {
                captureAndShowStartDate(dayOfMonth, month, year);
            }
        };


        //end date
        endDatePickerDialogListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
            {
                captureAndShowEndDate(dayOfMonth, month, year);
            }
        };

        cal = Calendar.getInstance();

        endYear = cal.get(Calendar.YEAR);
        endMonth = cal.get(Calendar.MONTH);
        endDate = cal.get(Calendar.DAY_OF_MONTH);

        endDatePickerDialog = new DatePickerDialog(MySalesHistoryActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                endDatePickerDialogListener, endYear, endMonth, endDate);
        endDatePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        Date end_date_min = new Date();
        Calendar c3 = Calendar.getInstance();
        c3.setTime(end_date_min);
        c3.add(Calendar.MONTH, -6); // Subtract 6 months for enddate_min
        long end_minDate = c3.getTime().getTime(); // Twice

        Date end_date_max = new Date();
        Calendar c4 = Calendar.getInstance();
        c4.setTime(end_date_max);      //Subtract 0 months for enddate_max
        long end_maxDate = c4.getTime().getTime(); // Twice!


        endDatePickerDialog.getDatePicker().setMaxDate(end_maxDate);
        endDatePickerDialog.getDatePicker().setMinDate(end_minDate);

        //cal.add(Calendar.DATE, -30);

        cal.add(Calendar.MONTH, -1);

        startYear = cal.get(Calendar.YEAR);
        startMonth = cal.get(Calendar.MONTH);
        startDate = cal.get(Calendar.DAY_OF_MONTH);

        startDatePickerDialog = new DatePickerDialog(MySalesHistoryActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                startDatePickerDialogListener, startYear, startMonth, startDate);
        startDatePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Date start_date_min = new Date();
        Calendar c1 = Calendar.getInstance();
        c1.setTime(start_date_min);
        c1.add(Calendar.MONTH, -6); // Subtract 5 months for startdate_min
        long start_minDate = c1.getTime().getTime(); // Twice

        Date start_date_max = new Date();
        Calendar c2 = Calendar.getInstance();
        c2.setTime(start_date_max);
        c2.add(Calendar.MONTH, -1); // Subtract 1 months for startdate_max
        long start_maxDate = c2.getTime().getTime(); // Twice!

        startDatePickerDialog.getDatePicker().setMaxDate(start_maxDate);

        startDatePickerDialog.getDatePicker().setMinDate(start_minDate);

        captureAndShowEndDate(endDate, endMonth, endYear);
        captureAndShowStartDate(startDate, startMonth, startYear);

        startDate_TextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startDatePickerDialog.show();
            }
        });

        endDate_TextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                endDatePickerDialog.show();
            }
        });


        //show history

        showHistory_Button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (isDateRangeValid(startDateChosen, endDateChosen))
                {

                    constructQueryAndArgs();
                    populateSalesHistory();
                }
            }
        });
    }


    //sku list without id
    private List<SkuGroupHistory> getCustomSkuList()
    {

        String SQL_SELECT_ALL_SKUs = "select sku_id, sku_name from " + TBL_SKU + " ;";
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_ALL_SKUs, null);

        List<SkuGroupHistory> skuList = new ArrayList<>();
        skuList.add(new SkuGroupHistory(ANY, ANY));

        while (cursor.moveToNext())
        {
            String skuID = cursor.getString(cursor.getColumnIndexOrThrow("sku_id"));
            String skuName = cursor.getString(cursor.getColumnIndexOrThrow("sku_name"));

            skuList.add(new SkuGroupHistory(skuID, skuName));
        }

        cursor.close();
        sqLiteDatabase.close();

        return skuList;
    }

    //retailer list without id
    private List<RetailerGroupHistory> getCustomRetailerList()
    {

        String SQL_SELECT_RETAILERS = "SELECT retailer_id, retailer_name FROM retailers " + "  WHERE  emp_id = ?;";
        //String[] selectionArgs = {loggedInUserID, "0", "0"};
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_RETAILERS, new String[]{new MySharedPrefrencesData().getUser_Id(MySalesHistoryActivity.this)});

        List<RetailerGroupHistory> retailerList = new ArrayList<>();
        retailerList.add(new RetailerGroupHistory(ANY, ANY));

        while (cursor.moveToNext())
        {
            String retailerID = cursor.getString(cursor.getColumnIndexOrThrow("retailer_id"));
            String shop_name = cursor.getString(cursor.getColumnIndexOrThrow("retailer_name"));

            retailerList.add(new RetailerGroupHistory(retailerID, shop_name));
        }

        cursor.close();
        sqLiteDatabase.close();

        return retailerList;
    }


    //max month range
    private int getMaximumRangeFromConfig()
    {

        int default_month = 0;
        String SQL_Max_Month = "SELECT config_value  FROM " + TBL_CONFIG + " WHERE config_for =?";
        String[] selectionArgs = {"maximum date range in months"};

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_Max_Month, selectionArgs);


        if (cursor.moveToFirst())
        {
            default_month = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("config_value")));

        }

        cursor.close();
        sqLiteDatabase.close();

        return default_month;
    }

    //min month range
    private int getDefaultMonthRangeFromConfig()
    {

        int month_max = 0;
        String SQL_Max_Month = "SELECT config_value  FROM " + TBL_CONFIG + " WHERE config_for = ?";
        String[] selectionArgs = {"default date range in months"};

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_Max_Month, selectionArgs);


        if (cursor.moveToFirst())
        {
            month_max = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("config_value")));

        }

        cursor.close();
        sqLiteDatabase.close();

        return month_max;
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


    void captureAndShowStartDate(int dayOfMonth, int month, int year)
    {
        //startMonth starts from zero
        month = month + 1;

        String date = String.valueOf(dayOfMonth);
        //add zero before date. Else SQL queries on dates won't work.
        if (dayOfMonth <= 9)
        {
            date = "0" + date;
        }

        //add zero before month. Else SQL queries on dates won't work.
        String monthTwoDigits = String.valueOf(month);
        if (month <= 9)
        {
            monthTwoDigits = "0" + month;
        }

        //used while filtering results in SQL queries because order & visit dates are saved in YYYY-MM-DD format.
        startDateChosen = year + "-" + monthTwoDigits + "-" + date;

        String startDateToShow = dayOfMonth + "/" + monthTwoDigits + "/" + year;
        startDate_TextView.setText(startDateToShow);
    }

    void captureAndShowEndDate(int dayOfMonth, int month, int year)
    {
        //startMonth starts from zero
        month = month + 1;

        //add zero before date. Else SQL queries on dates won't work.
        String date = String.valueOf(dayOfMonth);
        if (dayOfMonth <= 9)
        {
            date = "0" + date;
        }

        //add zero before month. Else SQL queries on dates won't work.
        String monthTwoDigits = String.valueOf(month);
        if (month <= 9)
        {
            monthTwoDigits = "0" + month;
        }

        //used while filtering results in SQL queries because order & visit dates are saved in YYYY-MM-DD format.
        endDateChosen = year + "-" + monthTwoDigits + "-" + date;

        String endDateToShow = dayOfMonth + "/" + monthTwoDigits + "/" + year;
        endDate_TextView.setText(endDateToShow);
    }


    boolean isDateRangeValid(String startDateChosen, String endDateChosen)
    {
        Date startDate = new Date(startDateChosen.replace("-", "/"));
        Date endDate = new Date(endDateChosen.replace("-", "/"));

        if (endDate.after(startDate) || endDate.compareTo(startDate) == 0)
        {
            return true;
        }
        else
        {
            Utils.showErrorDialog(MySalesHistoryActivity.this, "Please enter valid date range.");
            return false;
        }
    }


    //sql query for history accrding to sku or retailer or both
    void constructQueryAndArgs()
    {
        //COUNT(sod.sku_id) as skuCount, SUM(sod.sku_final_price) as orderTotal
        //select so.order_id, so.retailer_id, so.order_date, sod.sku_id from sales_orders so INNER JOIN sales_order_details sod ON so.order_id = sod.order_id WHERE date(so.order_date) BETWEEN date('2017-10-07') AND date('2017-11-06');

        SQL_SELECT_MY_SALES_HISTORY = "select DISTINCT so.order_id,so.upload_status,so.mobile_order_id, so.mobile_retailer_id,so.total_order_value,so.total_discount, so.order_date,sod.order_detail_id, sod.sku_id, sod.sku_name, sod.sku_qty, sod.sku_final_price" +
                " from sales_orders so INNER JOIN sales_order_details sod " +
                "ON so.mobile_order_id = sod.mobile_order_id";

        selectionArgsList = new ArrayList<>();

        RetailerGroupHistory rgh = (RetailerGroupHistory) retailer_Spinner.getSelectedItem();
        String retailerChosen = rgh.retailer_id + COLON + rgh.retailer_name;

        if (!retailerChosen.split(COLON)[0].equals(ANY))
        {
            SQL_SELECT_MY_SALES_HISTORY += " where (so.retailer_id = ?) AND (so.is_placed = 1)  AND so.emp_id = " + loggedInUserID;
            selectionArgsList.add(0, retailerChosen.split(COLON)[0]);
        }

        SkuGroupHistory sgh = (SkuGroupHistory) sku_Spinner.getSelectedItem();
        String skuChosen = sgh.sku_name + COLON + sgh.sku_id;
        Constants.selected_sku_from_history = sgh.sku_id;

        //String skuChosen = sku_Spinner.getSelectedItem().toString().trim();

        if (!skuChosen.split(COLON)[1].equals(ANY))
        {
            if (!retailerChosen.split(COLON)[0].equals(ANY))
            {
                SQL_SELECT_MY_SALES_HISTORY += " AND sod.sku_id = ? AND (so.is_placed = 1)  AND so.emp_id = " + loggedInUserID;
                selectionArgsList.add(1, skuChosen.split(COLON)[1]);
            }
            else
            {
                SQL_SELECT_MY_SALES_HISTORY += " where sod.sku_id = ? AND (so.is_placed = 1)  AND so.emp_id = " + loggedInUserID;
                selectionArgsList.add(0, skuChosen.split(COLON)[1]);
            }
        }

        if (retailerChosen.split(COLON)[0].equals(ANY) && skuChosen.split(COLON)[1].equals(ANY))
        {
           /* SQL_SELECT_MY_SALES_HISTORY = "select DISTINCT so.order_id, so.retailer_id, so.order_date, sod.sku_id" +
                    " from sales_orders so INNER JOIN sales_order_details sod " +
                    "ON so.order_id = sod.order_id";*/

            SQL_SELECT_MY_SALES_HISTORY += " WHERE date(so.order_date) BETWEEN " + "date(?) AND" + " date(?) AND (so.is_placed = 1)  AND so.emp_id = " + loggedInUserID;
            selectionArgsList.add(0, startDateChosen);
            selectionArgsList.add(1, endDateChosen);
            Log.e("list1AND", selectionArgsList.toString());
        }
        else if (retailerChosen.split(COLON)[0].equals(ANY) || skuChosen.split(COLON)[1].equals(ANY))
        {
            SQL_SELECT_MY_SALES_HISTORY += " AND date(so.order_date) BETWEEN " + "date(?) AND" + " date(?) AND (so.is_placed = 1)  AND so.emp_id = " + loggedInUserID;
            selectionArgsList.add(1, startDateChosen);
            selectionArgsList.add(2, endDateChosen);
            Log.e("list1OR", selectionArgsList.toString());
        }
        else
        {
            SQL_SELECT_MY_SALES_HISTORY += " AND date(so.order_date) BETWEEN " + "date(?) AND" + " date(?) AND (so.is_placed = 1)  AND so.emp_id = " + loggedInUserID;
            selectionArgsList.add(2, startDateChosen);
            selectionArgsList.add(3, endDateChosen);
            Log.e("list1ELSE", selectionArgsList.toString());
        }

        SQL_SELECT_MY_SALES_HISTORY += ";";

        Log.e("SQL", SQL_SELECT_MY_SALES_HISTORY);
        Log.e("SQL_args", selectionArgsList.toString());
    }


    //setting adapter for history recyclerview

    void populateSalesHistory()
    {
        ArrayList<String> orderidlist = new ArrayList<>();

        String[] selectionArgs = new String[selectionArgsList.size()];
        selectionArgs = selectionArgsList.toArray(selectionArgs);
        selectionArgsList.clear();
        List<MySalesHistory> salesHistoryList = new ArrayList<>();

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_MY_SALES_HISTORY, selectionArgs);
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
            String orderDate = cursor.getString(cursor.getColumnIndexOrThrow("order_date"));
            String total_discount = cursor.getString(cursor.getColumnIndexOrThrow("total_discount"));
            String total_order_value = cursor.getString(cursor.getColumnIndexOrThrow("total_order_value"));
            String order_detail_id = cursor.getString(cursor.getColumnIndexOrThrow("order_detail_id"));

            if (!orderidlist.contains(orderID))
            {


                orderidlist.add(orderID);

                ArrayList<MySalesHistory.SkuDetails_Ordered> myskulis = new ArrayList<>();

                String totalItems = String.valueOf(DbUtils.getItemCount(orderID, orderID));
                // String orderTotal = String.valueOf(DbUtils.getOrderTotal(orderID));
                String orderTotal = total_order_value;

                String SQL_SELECT_Sales_order_Detail = "select  mobile_order_id,order_detail_id, sku_id, sku_name,sku_qty,sku_price,sku_final_price,sku_price_before_discount,sku_free_qty,sku_discount" +
                        " from sales_order_details " +
                        " WHERE order_id = ? OR mobile_order_id = ?";

                String[] selectionargs1 = {orderID, orderID};
                Cursor cursor1 = sqLiteDatabase.rawQuery(SQL_SELECT_Sales_order_Detail, selectionargs1);

                while (cursor1.moveToNext())
                {

                    String skuid = cursor1.getString(cursor1.getColumnIndexOrThrow("sku_id"));
                    String skuname = cursor1.getString(cursor1.getColumnIndexOrThrow("sku_name"));
                    String skuqty = cursor1.getString(cursor1.getColumnIndexOrThrow("sku_qty"));
                    String sku_free_qty = cursor1.getString(cursor1.getColumnIndexOrThrow("sku_free_qty"));
                    String sku_discount = cursor1.getString(cursor1.getColumnIndexOrThrow("sku_discount"));
                    String sku_unitPrice = cursor1.getString(cursor1.getColumnIndexOrThrow("sku_price"));
                    String sku_finalprice = cursor1.getString(cursor1.getColumnIndexOrThrow("sku_final_price"));

                    myskulis.add(new MySalesHistory().new SkuDetails_Ordered(skuid, skuname, skuqty, sku_free_qty, sku_discount, sku_unitPrice, sku_finalprice));

                }


                salesHistoryList.add(new MySalesHistory(orderID, retailerID, orderDate, totalItems, orderTotal, total_discount, myskulis, ""));


            }


            Log.e("orderlist", new Gson().toJson(salesHistoryList));

            //   String totalItems = String.valueOf(skuname+" : "+skuqty);
            //   String orderTotal = String.valueOf(sku_finalprice);


        }
        cursor.close();
        sqLiteDatabase.close();

        if (salesHistoryList.size() <= 0)
        {
            emptyHistory_TextView.setVisibility(View.VISIBLE);
            mySalesHistory_RecyclerView.setVisibility(View.INVISIBLE);
        }
        else
        {
            emptyHistory_TextView.setVisibility(View.GONE);
            mySalesHistory_RecyclerView.setVisibility(View.VISIBLE);
        }

        mySalesHistoryAdapter = new MySalesHistoryAdapter(salesHistoryList);
        mySalesHistory_RecyclerView.setAdapter(mySalesHistoryAdapter);
    }
}
