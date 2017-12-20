package in.etaminepgg.sfa.Activities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.etaminepgg.sfa.Adapters.AllSKUsAdapter;
import in.etaminepgg.sfa.Adapters.PendingOrdersAdapter;
import in.etaminepgg.sfa.Models.PendingOrder;
import in.etaminepgg.sfa.R;
import in.etaminepgg.sfa.Utilities.ConstantsA;
import in.etaminepgg.sfa.Utilities.DbUtils;
import in.etaminepgg.sfa.Utilities.MyDb;
import in.etaminepgg.sfa.Utilities.Utils;

import static in.etaminepgg.sfa.Utilities.Constants.TBL_SKU;
import static in.etaminepgg.sfa.Utilities.Constants.dbFileFullPath;
import static in.etaminepgg.sfa.Utilities.Utils.loggedInUserID;

public class PendingOrdersActivity extends AppCompatActivity
{
    RecyclerView pendingOrders_RecyclerView;
    TextView emptyAdapter_TextView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_orders);

        pendingOrders_RecyclerView = (RecyclerView)findViewById(R.id.pendingOrders_RecyclerView);
        emptyAdapter_TextView = (TextView)findViewById(R.id.emptyAdapter_TextView);

        pendingOrders_RecyclerView.setLayoutManager(new LinearLayoutManager(this));
        pendingOrders_RecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    protected void onPostResume()
    {
        super.onPostResume();

        List<PendingOrder> pendingOrderList = getPendingOrderList();

        if (pendingOrderList.size() < 1)
        {
            emptyAdapter_TextView.setVisibility(View.VISIBLE);
            pendingOrders_RecyclerView.setVisibility(View.GONE);
        }
        else
        {
            emptyAdapter_TextView.setVisibility(View.GONE);
            pendingOrders_RecyclerView.setVisibility(View.VISIBLE);
        }

        PendingOrdersAdapter pendingOrdersAdapter = new PendingOrdersAdapter(pendingOrderList);
        pendingOrders_RecyclerView.setAdapter(pendingOrdersAdapter);
    }

    //to get correct results uncomment the selectionArgs and comment selectionArgs_Dummy
    private List<PendingOrder> getPendingOrderList()
    {
        List<PendingOrder> pendingOrderList = new ArrayList<>();

        String SQL_SELECT_PENDING_SALES_ORDERS = "SELECT order_id, retailer_id, order_date FROM sales_orders WHERE emp_id=? AND is_placed=? AND is_cancelled=? ;";
        //String[] selectionArgs = {loggedInUserID, "0", "0"};

        String[] selectionArgs_Dummy = {loggedInUserID, "1", "0"};

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);

        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_PENDING_SALES_ORDERS, selectionArgs_Dummy);

        while (cursor.moveToNext())
        {
            String orderID = cursor.getString(cursor.getColumnIndexOrThrow("order_id"));
            String retailerID = cursor.getString(cursor.getColumnIndexOrThrow("retailer_id"));
            String orderDate = cursor.getString(cursor.getColumnIndexOrThrow("order_date"));

            PendingOrder pendingOrder = new PendingOrder(orderID, retailerID, orderDate);
            pendingOrderList.add(pendingOrder);
        }

        cursor.close();
        sqLiteDatabase.close();

        return pendingOrderList;
    }
}
