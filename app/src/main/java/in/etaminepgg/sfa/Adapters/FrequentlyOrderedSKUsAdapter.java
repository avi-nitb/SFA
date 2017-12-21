package in.etaminepgg.sfa.Adapters;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import in.etaminepgg.sfa.Activities.SkuDetailsActivity;
import in.etaminepgg.sfa.Models.Sku;
import in.etaminepgg.sfa.R;
import in.etaminepgg.sfa.Utilities.MyDb;
import in.etaminepgg.sfa.Utilities.Utils;

import static in.etaminepgg.sfa.Utilities.Constants.dbFileFullPath;
import static in.etaminepgg.sfa.Utilities.ConstantsA.KEY_SKU_ID;
import static in.etaminepgg.sfa.Utilities.ConstantsA.RS;

/**
 * Created by etamine on 6/6/17.
 */

public class FrequentlyOrderedSKUsAdapter extends RecyclerView.Adapter<FrequentlyOrderedSKUsAdapter.SkuInfoViewHolder>
{
    private List<Sku> skuList;

    public FrequentlyOrderedSKUsAdapter(List<Sku> skuList)
    {
        this.skuList = skuList;
    }

    @Override
    public SkuInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemview_frequently_ordered_skus, parent, false);
        return new SkuInfoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SkuInfoViewHolder skuInfoViewHolder, int position)
    {
        String skuID = skuList.get(position).getSkuId();
        String skuName = skuList.get(position).getSkuName();
        String skuPrice = skuList.get(position).getSkuPrice();
        //String skuCategory = skuList.get(position).getSkuCategory();

        skuInfoViewHolder.itemView.setTag(R.string.tag_sku_id, skuID);
        skuInfoViewHolder.itemView.setTag(R.string.tag_sku_price, skuPrice);
        skuInfoViewHolder.itemView.setTag(R.string.tag_sku_name, skuName);

        skuInfoViewHolder.skuName_TextView.setText(skuName);
        skuInfoViewHolder.skuPrice_TextView.setText(RS + skuPrice);
        skuInfoViewHolder.retailerCount_TextView.setText(getRetailerCount(skuID) + " Retailers, ");
        skuInfoViewHolder.orderCount_TextView.setText(getOrderCount(skuID) + " Orders, ");
        skuInfoViewHolder.salesValue_TextView.setText("Sales Value: " + "Rs." + getSalesValue(skuID));
        //skuInfoViewHolder.sku_SO_Attr_TextView.setText(skuCategory);
    }

    @Override
    public int getItemCount()
    {
        return skuList.size();
    }

    private String getRetailerCount(String skuID)
    {
        String retailerCount = "NA";
        String SQL_SELECT_RETAILER_COUNT = "select COUNT(DISTINCT so.retailer_id) AS retailerCount" +
                " from sales_orders so INNER JOIN sales_order_details sod " +
                "ON so.order_id = sod.order_id where sku_id = ?";
        String[] selectionArgs = {skuID};

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_RETAILER_COUNT, selectionArgs);

        if(cursor.moveToNext())
        {
            retailerCount = cursor.getString(cursor.getColumnIndexOrThrow("retailerCount"));
        }

        cursor.close();
        sqLiteDatabase.close();

        return retailerCount;
    }

    private String getOrderCount(String skuID)
    {
        String orderCount = "NA";
        String SQL_SELECT_ORDER_COUNT = "SELECT COUNT(order_id) orderCount FROM sales_order_details WHERE sku_id = ? ;";
        String[] selectionArgs = {skuID};

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_ORDER_COUNT, selectionArgs);

        if(cursor.moveToNext())
        {
            orderCount = cursor.getString(cursor.getColumnIndexOrThrow("orderCount"));
        }

        cursor.close();
        sqLiteDatabase.close();

        return orderCount;
    }

    private String getSalesValue(String skuID)
    {
        String salesValue = "NA";
        String SQL_SELECT_SALES_VALUE_FOR_SKU = "SELECT SUM(sku_final_price) salesValue FROM sales_order_details WHERE sku_id = ? ;";
        String[] selectionArgs = {skuID};

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_SALES_VALUE_FOR_SKU, selectionArgs);

        if(cursor.moveToNext())
        {
            salesValue = cursor.getString(cursor.getColumnIndexOrThrow("salesValue"));
        }

        cursor.close();
        sqLiteDatabase.close();

        return salesValue;
    }

    class SkuInfoViewHolder extends RecyclerView.ViewHolder
    {
        ImageView skuPhoto_ImageView, addSkuToCart_ImageView;
        TextView skuName_TextView, skuPrice_TextView, retailerCount_TextView, orderCount_TextView, salesValue_TextView;

        SkuInfoViewHolder(final View itemView)
        {
            super(itemView);
            skuPhoto_ImageView = (ImageView) itemView.findViewById(R.id.skuPhoto_ImageView);
            addSkuToCart_ImageView = (ImageView) itemView.findViewById(R.id.addSkuToCart_ImageView);
            skuName_TextView = (TextView) itemView.findViewById(R.id.skuName_TextView);
            skuPrice_TextView = (TextView) itemView.findViewById(R.id.skuPrice_TextView);
            orderCount_TextView = (TextView) itemView.findViewById(R.id.orderCount_TextView);
            salesValue_TextView = (TextView) itemView.findViewById(R.id.salesValue_TextView);
            retailerCount_TextView = (TextView) itemView.findViewById(R.id.retailerCount_TextView);

            addSkuToCart_ImageView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    String skuID = itemView.getTag(R.string.tag_sku_id).toString();
                    String skuPrice = itemView.getTag(R.string.tag_sku_price).toString();
                    String skuName = itemView.getTag(R.string.tag_sku_name).toString();
                    // new DbUtils().addToSalesOrderOrPickRetailer(skuID, skuName, skuPrice, itemView.getContext());
                    new Utils().pickAttributeValuesOrSelectRetailer(skuID, skuName, skuPrice, itemView.getContext());
                }
            });

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    //Utils.showToast(view.getContext(), itemView.getTag(R.string.tag_sku_id).toString());

                    String skuID = itemView.getTag(R.string.tag_sku_id).toString();
                    Utils.launchActivityWithExtra(view.getContext(), SkuDetailsActivity.class, KEY_SKU_ID, skuID);
                }
            });
        }
    }

}
