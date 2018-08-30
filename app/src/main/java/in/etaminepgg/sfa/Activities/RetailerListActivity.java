package in.etaminepgg.sfa.Activities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.etaminepgg.sfa.Adapters.RetailerListAdapter;
import in.etaminepgg.sfa.Models.Retailer;
import in.etaminepgg.sfa.R;
import in.etaminepgg.sfa.Utilities.MyDb;

import static in.etaminepgg.sfa.Utilities.Constants.dbFileFullPath;

public class RetailerListActivity extends AppCompatActivity
{
    RecyclerView retailerList_RecyclerView;
    TextView emptyAdapter_TextView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_retailer_list);

        retailerList_RecyclerView = (RecyclerView) findViewById(R.id.retailerList_RecyclerView);
        emptyAdapter_TextView = (TextView) findViewById(R.id.emptyAdapter_TextView);

        retailerList_RecyclerView.setLayoutManager(new LinearLayoutManager(this));
        retailerList_RecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    protected void onPostResume()
    {
        super.onPostResume();

        List<Retailer> retailerList = getRetailerList();

        if (retailerList.size() < 1)
        {
            emptyAdapter_TextView.setVisibility(View.VISIBLE);
            retailerList_RecyclerView.setVisibility(View.GONE);
        }
        else
        {
            emptyAdapter_TextView.setVisibility(View.GONE);
            retailerList_RecyclerView.setVisibility(View.VISIBLE);
        }

        RetailerListAdapter retailerListAdapter = new RetailerListAdapter(retailerList);

        retailerList_RecyclerView.setAdapter(retailerListAdapter);
    }

    private List<Retailer> getRetailerList()
    {
        List<Retailer> retailerList = new ArrayList<>();

        String SQL_SELECT_RETAILERS = "SELECT retailer_id, retailer_name, shop_name, mobile_no FROM retailers;";
        //String[] selectionArgs = {loggedInUserID, "0", "0"};
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_RETAILERS, null);

        while (cursor.moveToNext())
        {
            String retailerID = cursor.getString(cursor.getColumnIndexOrThrow("retailer_id"));
            String retailerName = cursor.getString(cursor.getColumnIndexOrThrow("retailer_name"));
            String shopName = cursor.getString(cursor.getColumnIndexOrThrow("shop_name"));
            String mobileNumber = cursor.getString(cursor.getColumnIndexOrThrow("mobile_no"));

            Retailer retailer = new Retailer(retailerID, retailerName, shopName, mobileNumber);
            retailerList.add(retailer);
        }

        cursor.close();
        sqLiteDatabase.close();

        return retailerList;
    }
}
