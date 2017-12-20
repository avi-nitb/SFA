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
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import in.etaminepgg.sfa.Adapters.MySalesHistoryAdapter;
import in.etaminepgg.sfa.Models.MySalesHistory;
import in.etaminepgg.sfa.R;
import in.etaminepgg.sfa.Utilities.DbUtils;
import in.etaminepgg.sfa.Utilities.MyDb;
import in.etaminepgg.sfa.Utilities.Utils;

import static in.etaminepgg.sfa.Utilities.Constants.TBL_SALES_ORDER;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SKU;
import static in.etaminepgg.sfa.Utilities.Constants.dbFileFullPath;
import static in.etaminepgg.sfa.Utilities.Utils.getTodayDate;

public class MySalesHistoryActivity extends AppCompatActivity
{
    private static final String TAG = "MySalesHistoryActivity";
    Spinner retailer_Spinner, sku_Spinner;
    //List<String> skuList, retailerList;
    TextView startDate_TextView, endDate_TextView, emptyHistory_TextView;
    Button showHistory_Button;
    RecyclerView mySalesHistory_RecyclerView;
    DatePickerDialog startDatePickerDialog, endDatePickerDialog;
    DatePickerDialog.OnDateSetListener startDatePickerDialogListener, endDatePickerDialogListener;
    private int startYear, startMonth, startDate, endYear, endMonth, endDate;

    Calendar cal;
    private static final String ANY = "ANY";
    private static final String COLON = " : ";
    //private static final String DD_MM_YYYY = "DD/MM/YYYY";
    String startDateChosen, endDateChosen;
    String SQL_SELECT_MY_SALES_HISTORY;
    List<String> selectionArgsList;
    MySalesHistoryAdapter mySalesHistoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_sales_history);

        retailer_Spinner = (Spinner) findViewById(R.id.retailer_Spinner);
        sku_Spinner = (Spinner) findViewById(R.id.sku_Spinner);
        startDate_TextView = (TextView) findViewById(R.id.startDate_TextView);
        endDate_TextView = (TextView) findViewById(R.id.endDate_TextView);
        emptyHistory_TextView = (TextView) findViewById(R.id.emptyHistory_TextView);
        showHistory_Button = (Button)findViewById(R.id.showHistory_Button);
        mySalesHistory_RecyclerView = (RecyclerView) findViewById(R.id.mySalesHistory_RecyclerView);


        mySalesHistory_RecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mySalesHistory_RecyclerView.setItemAnimator(new DefaultItemAnimator());

        ArrayAdapter<String> skuAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getSkuList());
        skuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sku_Spinner.setAdapter(skuAdapter);

        ArrayAdapter<String> retailerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getRetailerList());
        retailerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        retailer_Spinner.setAdapter(retailerAdapter);

        startDatePickerDialogListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
            {
                captureAndShowStartDate(dayOfMonth, month, year);
            }
        };

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
        endDatePickerDialog.getDatePicker().setMaxDate(cal.getTimeInMillis());

        cal.add(Calendar.DATE, -30);

         startYear = cal.get(Calendar.YEAR);
         startMonth = cal.get(Calendar.MONTH);
         startDate = cal.get(Calendar.DAY_OF_MONTH);

        startDatePickerDialog = new DatePickerDialog(MySalesHistoryActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                startDatePickerDialogListener, startYear, startMonth, startDate);
        startDatePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        startDatePickerDialog.getDatePicker().setMaxDate(new Date().getTime());

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

    void captureAndShowStartDate(int dayOfMonth, int month, int year)
    {
        //startMonth starts from zero
        month = month + 1;

        String date = String.valueOf(dayOfMonth);
        //add zero before date. Else SQL queries on dates won't work.
        if (dayOfMonth < 9)
        {
            date = "0"+date;
        }

        //add zero before month. Else SQL queries on dates won't work.
        String monthTwoDigits = String.valueOf(month);
        if (month < 9)
        {
            monthTwoDigits = "0"+month;
        }

        //used while filtering results in SQL queries because order & visit dates are saved in YYYY-MM-DD format.
        startDateChosen = year + "-" + monthTwoDigits + "-" + date;

        String startDateToShow = dayOfMonth+"/"+monthTwoDigits+"/"+year;
        startDate_TextView.setText(startDateToShow);
    }

    void captureAndShowEndDate(int dayOfMonth, int month, int year)
    {
        //startMonth starts from zero
        month = month + 1;

        //add zero before date. Else SQL queries on dates won't work.
        String date = String.valueOf(dayOfMonth);
        if (dayOfMonth < 9)
        {
            date = "0"+date;
        }

        //add zero before month. Else SQL queries on dates won't work.
        String monthTwoDigits = String.valueOf(month);
        if (month < 9)
        {
            monthTwoDigits = "0"+month;
        }

        //used while filtering results in SQL queries because order & visit dates are saved in YYYY-MM-DD format.
        endDateChosen = year + "-" + monthTwoDigits + "-" + date;

        String endDateToShow = dayOfMonth+"/"+monthTwoDigits+"/"+year;
        endDate_TextView.setText(endDateToShow);
    }

    private List<String> getSkuList()
    {
        String SQL_SELECT_ALL_SKUs = "select sku_id, sku_name from " + TBL_SKU + " ;";
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_ALL_SKUs, null);

        List<String> skuList = new ArrayList<>();
        skuList.add(ANY);

        while (cursor.moveToNext())
        {
            String skuID = cursor.getString(cursor.getColumnIndexOrThrow("sku_id"));
            String skuName = cursor.getString(cursor.getColumnIndexOrThrow("sku_name"));

            skuList.add(skuName + COLON + skuID);
        }

        cursor.close();
        sqLiteDatabase.close();

        return skuList;
    }

    private List<String> getRetailerList()
    {
        String SQL_SELECT_RETAILERS = "SELECT retailer_id, retailer_name FROM retailers;";
        //String[] selectionArgs = {loggedInUserID, "0", "0"};
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_RETAILERS, null);

        List<String> retailerList = new ArrayList<>();
        retailerList.add(ANY);

        while (cursor.moveToNext())
        {
            String retailerID = cursor.getString(cursor.getColumnIndexOrThrow("retailer_id"));
            String retailerName = cursor.getString(cursor.getColumnIndexOrThrow("retailer_name"));

            retailerList.add(retailerName + COLON + retailerID);
        }

        cursor.close();
        sqLiteDatabase.close();

        return retailerList;
    }

    boolean isDateRangeValid(String startDateChosen, String endDateChosen)
    {
        Date startDate = new Date(startDateChosen.replace("-","/"));
        Date endDate = new Date(endDateChosen.replace("-","/"));

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

    void constructQueryAndArgs()
    {
        //COUNT(sod.sku_id) as skuCount, SUM(sod.sku_final_price) as orderTotal
        //select so.order_id, so.retailer_id, so.order_date, sod.sku_id from sales_orders so INNER JOIN sales_order_details sod ON so.order_id = sod.order_id WHERE date(so.order_date) BETWEEN date('2017-10-07') AND date('2017-11-06');

        SQL_SELECT_MY_SALES_HISTORY = "select DISTINCT so.order_id, so.retailer_id, so.order_date, sod.sku_id" +
                " from sales_orders so INNER JOIN sales_order_details sod " +
                "ON so.order_id = sod.order_id";

        selectionArgsList = new ArrayList<>();

        String retailerChosen  = retailer_Spinner.getSelectedItem().toString().trim();

        if (!retailerChosen.equals(ANY))
        {
            SQL_SELECT_MY_SALES_HISTORY += " where so.retailer_id = ?";
            selectionArgsList.add(0, retailerChosen.split(COLON)[1]);
        }

        String skuChosen  = sku_Spinner.getSelectedItem().toString().trim();

        if (!skuChosen.equals(ANY))
        {
            if (!retailerChosen.equals(ANY))
            {
                SQL_SELECT_MY_SALES_HISTORY += " AND sod.sku_id = ?";
                selectionArgsList.add(1, skuChosen.split(COLON)[1]);
            }
            else
            {
                SQL_SELECT_MY_SALES_HISTORY += " where sod.sku_id = ?";
                selectionArgsList.add(0, skuChosen.split(COLON)[1]);
            }
        }

        if (retailerChosen.equals(ANY) && skuChosen.equals(ANY))
        {
           /* SQL_SELECT_MY_SALES_HISTORY = "select DISTINCT so.order_id, so.retailer_id, so.order_date, sod.sku_id" +
                    " from sales_orders so INNER JOIN sales_order_details sod " +
                    "ON so.order_id = sod.order_id";*/

            SQL_SELECT_MY_SALES_HISTORY += " WHERE date(so.order_date) BETWEEN " + "date(?) AND" + " date(?)";
            selectionArgsList.add(0, startDateChosen);
            selectionArgsList.add(1, endDateChosen);
        }
        else if (retailerChosen.equals(ANY) || skuChosen.equals(ANY))
        {
            SQL_SELECT_MY_SALES_HISTORY += " AND date(so.order_date) BETWEEN " + "date(?) AND" + " date(?)";
            selectionArgsList.add(1, startDateChosen);
            selectionArgsList.add(2, endDateChosen);
        }
        else
        {
            SQL_SELECT_MY_SALES_HISTORY += " AND date(so.order_date) BETWEEN " + "date(?) AND" + " date(?)";
            selectionArgsList.add(2, startDateChosen);
            selectionArgsList.add(3, endDateChosen);
        }

        SQL_SELECT_MY_SALES_HISTORY += ";";

        Log.e("SQL", SQL_SELECT_MY_SALES_HISTORY);
        Log.e("SQL_args", selectionArgsList.toString());
    }

    void populateSalesHistory()
    {
        String[] selectionArgs = new String[selectionArgsList.size()];
        selectionArgs = selectionArgsList.toArray(selectionArgs);
        selectionArgsList.clear();
        List<MySalesHistory> salesHistoryList = new ArrayList<>();
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_MY_SALES_HISTORY, selectionArgs);
        while (cursor.moveToNext())
        {
            String orderID = cursor.getString(cursor.getColumnIndexOrThrow("order_id"));
            String retailerID = cursor.getString(cursor.getColumnIndexOrThrow("retailer_id"));
            String orderDate = cursor.getString(cursor.getColumnIndexOrThrow("order_date"));
            String totalItems = String.valueOf(DbUtils.getItemCount(orderID));
            String orderTotal = String.valueOf(DbUtils.getOrderTotal(orderID));

            salesHistoryList.add(new MySalesHistory(orderID, retailerID, orderDate, totalItems, orderTotal));
        }
        cursor.close();

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
