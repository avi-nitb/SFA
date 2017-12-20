package in.etaminepgg.sfa.Adapters;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import in.etaminepgg.sfa.Activities.SkuListByGenreActivity;
import in.etaminepgg.sfa.Models.PendingOrder;
import in.etaminepgg.sfa.Models.Sku;
import in.etaminepgg.sfa.R;
import in.etaminepgg.sfa.Utilities.DbUtils;
import in.etaminepgg.sfa.Utilities.MyDb;
import in.etaminepgg.sfa.Utilities.Utils;

import static in.etaminepgg.sfa.Utilities.Constants.TBL_RETAILER;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SALES_ORDER;
import static in.etaminepgg.sfa.Utilities.Constants.dbFileFullPath;
import static in.etaminepgg.sfa.Utilities.ConstantsA.NEW_ORDER;
import static in.etaminepgg.sfa.Utilities.DbUtils.makeCurrentActiveOrderInactive;

/**
 * Created by etamine on 16/8/17.
 */

public class PendingOrdersAdapter extends RecyclerView.Adapter<PendingOrdersAdapter.PendingOrderViewHolder>
{
    private List<PendingOrder> pendingOrderList;

    public PendingOrdersAdapter(List<PendingOrder> pendingOrderList)
    {
        this.pendingOrderList = pendingOrderList;
    }

    @Override
    public PendingOrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemview_pending_order, parent, false);
        return new PendingOrderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PendingOrderViewHolder viewHolder, int position)
    {
        PendingOrder pendingOrder = pendingOrderList.get(position);
        String orderID = pendingOrder.getOrderID();
        String retailerID = pendingOrder.getRetailerID();
        String orderDate = pendingOrder.getOrderDate();

        viewHolder.itemView.setTag(R.string.tag_order_id, orderID);

        viewHolder.orderId_TextView.setText(orderID);
        viewHolder.retailerId_TextView.setText(retailerID);
        viewHolder.retailerName_TextView.setText(getRetailerName(retailerID));
        viewHolder.orderDate_TextView.setText(orderDate);
        viewHolder.orderTotal_TextView.setText("Rs." + DbUtils.getOrderTotal(orderID) + "/-");
        viewHolder.itemCount_TextView.setText(DbUtils.getItemCount(orderID) + " items");
    }

    @Override
    public int getItemCount()
    {
        return pendingOrderList.size();
    }

    class PendingOrderViewHolder extends RecyclerView.ViewHolder
    {
        TextView orderId_TextView, retailerId_TextView, retailerName_TextView, orderDate_TextView, orderTotal_TextView, itemCount_TextView;
        ImageView deleteOrder_ImageView;

        PendingOrderViewHolder(final View itemView)
        {
            super(itemView);

            orderId_TextView = (TextView) itemView.findViewById(R.id.orderId_TextView);
            retailerId_TextView = (TextView) itemView.findViewById(R.id.retailerId_TextView);
            retailerName_TextView = (TextView) itemView.findViewById(R.id.retailerName_TextView);
            orderDate_TextView = (TextView) itemView.findViewById(R.id.orderDate_TextView);
            orderTotal_TextView = (TextView) itemView.findViewById(R.id.orderTotal_TextView);
            itemCount_TextView = (TextView) itemView.findViewById(R.id.itemCount_TextView);
            deleteOrder_ImageView = (ImageView)itemView.findViewById(R.id.deleteOrder_ImageView);

            deleteOrder_ImageView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    int position = getLayoutPosition();
                    String orderID = itemView.getTag(R.string.tag_order_id).toString();
                    
                    cancelThisOrder(orderID);
                    makeThisOrderInActive(orderID);
                    
                    pendingOrderList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, pendingOrderList.size());
                }
            });

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {

                    String orderID = itemView.getTag(R.string.tag_order_id).toString();

                    DbUtils.makeCurrentActiveOrderInactive();
                    makeThisOrderActive(orderID);

                    Utils.makeThreadSleepFor(500);

                    String intentExtraKey_selectedOrderType = itemView.getContext().getResources().getString(R.string.key_selected_order_type);
                    Utils.launchActivityWithExtra(itemView.getContext(), SkuListByGenreActivity.class, intentExtraKey_selectedOrderType, NEW_ORDER);
                }
            });
        }
    }

    private String getRetailerName(String retailerID)
    {
        String retailerName = "Error";
        String SQL_SELECT_RETAILER_NAME = "SELECT retailer_name FROM " + TBL_RETAILER + " WHERE retailer_id = ? ;";
        String[] selectionArgs = {retailerID};

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_RETAILER_NAME, selectionArgs);

        if (cursor.moveToNext())
        {
            retailerName = cursor.getString(cursor.getColumnIndexOrThrow("retailer_name"));
        }

        cursor.close();
        sqLiteDatabase.close();

        return retailerName;
    }

    private void makeThisOrderActive(String orderID)
    {
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        ContentValues salesOrderValues = new ContentValues();
        salesOrderValues.put("is_active", "1");
        sqLiteDatabase.update(TBL_SALES_ORDER, salesOrderValues, "order_id = ?", new String[]{orderID});

        sqLiteDatabase.close();
    }

    private void cancelThisOrder(String orderID)
    {
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        ContentValues salesOrderValues = new ContentValues();
        salesOrderValues.put("is_cancelled", "1");
        sqLiteDatabase.update(TBL_SALES_ORDER, salesOrderValues, "order_id = ?", new String[]{orderID});

        sqLiteDatabase.close();
    }

    private void makeThisOrderInActive(String orderID)
    {
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        ContentValues salesOrderValues = new ContentValues();
        salesOrderValues.put("is_active", "0");
        sqLiteDatabase.update(TBL_SALES_ORDER, salesOrderValues, "order_id = ?", new String[]{orderID});

        sqLiteDatabase.close();
    }
}
