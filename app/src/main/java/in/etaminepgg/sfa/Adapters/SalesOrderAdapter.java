package in.etaminepgg.sfa.Adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.etaminepgg.sfa.Activities.LoginActivity;
import in.etaminepgg.sfa.Activities.SkuDetailsActivity;
import in.etaminepgg.sfa.Models.SalesOrderSku;
import in.etaminepgg.sfa.R;
import in.etaminepgg.sfa.Utilities.DbUtils;
import in.etaminepgg.sfa.Utilities.MyDb;
import in.etaminepgg.sfa.Utilities.Utils;

import static in.etaminepgg.sfa.Utilities.Constants.TBL_SALES_ORDER;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SALES_ORDER_DETAILS;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SALES_ORDER_SKU_ATTRIBUTES;
import static in.etaminepgg.sfa.Utilities.Constants.dbFileFullPath;
import static in.etaminepgg.sfa.Utilities.ConstantsA.NONE;
import static in.etaminepgg.sfa.Utilities.ConstantsA.RS;
import static in.etaminepgg.sfa.Utilities.DbUtils.getOrderTotal;

/**
 * Created by etamine on 12/6/17.
 */

public class SalesOrderAdapter extends RecyclerView.Adapter<SalesOrderAdapter.SkuInfoViewHolder>
{
    Context context;
    private CheckBox setOrderAsRegularOrder_CheckBox;
    private LinearLayout salesOrder_LinearLayout;
    private LinearLayout salesOrder_LinearLayout_outer;
    private Button submitSalesOrder_Button;
    private TextView orderSummary_TextView;
    private List<SalesOrderSku> skuList;

    public SalesOrderAdapter(List<SalesOrderSku> skuList, LinearLayout salesOrder_LinearLayout_outer, CheckBox setOrderAsRegularOrder_CheckBox)
    {
        this.skuList = skuList;
        this.salesOrder_LinearLayout_outer = salesOrder_LinearLayout_outer;
        this.salesOrder_LinearLayout = (LinearLayout) salesOrder_LinearLayout_outer.findViewById(R.id.salesOrder_LinearLayout);
        this.setOrderAsRegularOrder_CheckBox = setOrderAsRegularOrder_CheckBox;
        this.submitSalesOrder_Button = (Button) salesOrder_LinearLayout.findViewById(R.id.submitSalesOrder_Button);
        this.orderSummary_TextView = (TextView) salesOrder_LinearLayout.findViewById(R.id.orderSummary_TextView);
    }

    @Override
    public SkuInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        salesOrder_LinearLayout.setVisibility(View.VISIBLE);

        context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.itemview_sales_order_sku, parent, false);
        return new SkuInfoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SkuInfoViewHolder skuInfoViewHolder, int position)
    {
        long orderDetailID = skuList.get(position).getOrderDetailId();

        String listString = "NONE";

        for(SalesOrderSku s : skuList)
        {
            listString = "";
            listString += s.getOrderDetailId() + "\t" + s.getSkuID() + "\t";
        }

        Log.e("skuList", listString);

        Log.e("orderDetailID", String.valueOf(orderDetailID));

        SpannableString salesOrderSkuAttributes = getSalesOrderAttributesFor(String.valueOf(orderDetailID));

        String skuID = skuList.get(position).getSkuID();
        String skuName = skuList.get(position).getSkuName();
        String skuPrice = skuList.get(position).getSkuPrice();
        String skuQuantity = skuList.get(position).getSkuQty();

        skuInfoViewHolder.itemView.setTag(R.string.tag_order_detail_id, orderDetailID);
        skuInfoViewHolder.itemView.setTag(R.string.tag_sku_id, skuID);
        skuInfoViewHolder.itemView.setTag(R.string.tag_sku_price, skuPrice);

        skuInfoViewHolder.skuName_TextView.setText(skuName);
        skuInfoViewHolder.skuPrice_TextView.setText(RS + skuPrice);
        skuInfoViewHolder.skuQuantity_TextInputEditText.setText(skuQuantity);

        skuInfoViewHolder.sku_SO_Attr_TextView.setText(salesOrderSkuAttributes);

        Log.e("attributeSet", String.valueOf(salesOrderSkuAttributes));

    }

    @Override
    public int getItemCount()
    {
        return skuList.size();
    }

    SpannableString getSalesOrderAttributesFor(String orderDetailID)
    {
        SpannableStringBuilder attributesOfSku = new SpannableStringBuilder();

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String SQL_SELECT_SALES_ORDER_ATTRIBUTES_SET = "SELECT attribute_id, attribute_value FROM sales_order_sku_attributes WHERE order_detail_id = ? ;";
        String[] selectionArgs = {String.valueOf(orderDetailID)};

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_SALES_ORDER_ATTRIBUTES_SET, selectionArgs);

        while(cursor.moveToNext())
        {
            int attributeId = cursor.getInt(cursor.getColumnIndexOrThrow("attribute_id"));

            String attributeValue = cursor.getString(cursor.getColumnIndexOrThrow("attribute_value"));

            String attributeName = new Utils().getAttributeName(attributeId);

            SpannableString an = new SpannableString(attributeName);
            an.setSpan(new ForegroundColorSpan(Color.DKGRAY), 0, attributeName.length(), 0);
            SpannableString av = new SpannableString(attributeValue);
            av.setSpan(new ForegroundColorSpan(Color.BLUE), 0, attributeValue.length(), 0);

            attributesOfSku.append(an);
            attributesOfSku.append(" : ");
            attributesOfSku.append(av);
            attributesOfSku.append(" , ");
        }

        cursor.close();
        sqLiteDatabase.close();

        Log.e("attributesOfSku", attributesOfSku.toString());

        //CharSequence skuAttributesWithoutCommaAtTheEnd = attributesOfSku.subSequence(0, attributesOfSku.length()-2);

        return SpannableString.valueOf(attributesOfSku);
    }

    class SkuInfoViewHolder extends RecyclerView.ViewHolder
    {
        ImageView skuPhoto_ImageView;
        TextView skuName_TextView, skuPrice_TextView, sku_SO_Attr_TextView;
        TextInputEditText skuQuantity_TextInputEditText;
        ImageButton deleteSKU_ImageButton;

        SkuInfoViewHolder(final View itemView)
        {
            super(itemView);
            skuPhoto_ImageView = (ImageView) itemView.findViewById(R.id.skuPhoto_ImageView);
            skuName_TextView = (TextView) itemView.findViewById(R.id.skuName_TextView);
            skuPrice_TextView = (TextView) itemView.findViewById(R.id.skuPrice_TextView);
            sku_SO_Attr_TextView = (TextView) itemView.findViewById(R.id.sku_SO_Attr_TextView);
            skuQuantity_TextInputEditText = (TextInputEditText) itemView.findViewById(R.id.skuQuantity_TextInputEditText);
            deleteSKU_ImageButton = (ImageButton) itemView.findViewById(R.id.deleteSKU_ImageButton);

            skuQuantity_TextInputEditText.addTextChangedListener(new TextWatcher()
            {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after)
                {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count)
                {

                }

                @Override
                public void afterTextChanged(Editable editable)
                {
                    String skuID = itemView.getTag(R.string.tag_sku_id).toString();
                    String skuPrice = itemView.getTag(R.string.tag_sku_price).toString();
                    String skuQty = editable.toString().trim();

                    if(!skuQty.isEmpty())
                    {
                        updateSkuQtyInSalesOrder(skuID, skuPrice, skuQty);
                    }
                }
            });

            skuPhoto_ImageView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Utils.launchActivity(view.getContext(), SkuDetailsActivity.class);
                }
            });

            deleteSKU_ImageButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    int position = getLayoutPosition();
                    //String activeOrderID = DbUtils.getActiveOrderID();
                    String orderDetailID = itemView.getTag(R.string.tag_order_detail_id).toString();
                    String skuID = itemView.getTag(R.string.tag_sku_id).toString();

                    if(deleteSkuInSalesOrderDetailsTable(orderDetailID, skuID) == 1)
                    {
                        deleteEntriesInSalesOrderSkuAttributes(orderDetailID);

                        skuList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, skuList.size());

                        String activeOrderID = DbUtils.getActiveOrderID();
                        String orderSummary = DbUtils.getItemCount(activeOrderID) + " items \n" + "Total:  Rs. " + getOrderTotal(activeOrderID);
                        orderSummary_TextView.setText(orderSummary);

                        if(skuList.size() <= 0)
                        {
                            salesOrder_LinearLayout.setVisibility(View.GONE);
                            salesOrder_LinearLayout_outer.findViewById(R.id.emptyAdapter_TextView).setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            salesOrder_LinearLayout.setVisibility(View.VISIBLE);
                            salesOrder_LinearLayout_outer.findViewById(R.id.emptyAdapter_TextView).setVisibility(View.GONE);
                        }
                    }
                    else
                    {
                        Utils.showToast(LoginActivity.baseContext, "Error Removing SKU");
                    }
                }
            });

            submitSalesOrder_Button.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                    String retailerID = DbUtils.getRetailerID();

                    //TODO: notifyDataSetChanged()
                    if(setOrderAsRegularOrder_CheckBox.isChecked())
                    {
                        if(!retailerID.equals(NONE))
                        {
                            eraseCurrentRegularOrderFor(retailerID);
                            setRegularOrderFor(retailerID);
                            placeTheOrder();
                        }
                        else
                        {
                            Utils.showErrorDialog(LoginActivity.baseContext, "Order Failed. No Active Order");
                        }
                    }
                    else
                    {
                        placeTheOrder();
                    }
                }
            });
        }

        private void updateSkuQtyInSalesOrder(String skuID, String skuPrice, String skuQty)
        {
            int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
            SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

            String activeOrderID = DbUtils.getActiveOrderID();
            String selection = "order_id = ? AND sku_id = ?";
            String[] selectionArgs = new String[]{activeOrderID, skuID};

            ContentValues salesOrderDetailsValues = new ContentValues();
            salesOrderDetailsValues.put("sku_qty", skuQty);
            sqLiteDatabase.update(TBL_SALES_ORDER_DETAILS, salesOrderDetailsValues, selection, selectionArgs);

            updateSkuFinalPriceInSalesOrder(sqLiteDatabase, activeOrderID, skuID, skuPrice, skuQty);
        }

        //updates sku final price in sales order details table for active sales order
        private void updateSkuFinalPriceInSalesOrder(SQLiteDatabase sqLiteDatabase, String orderID, String skuID, String skuPrice, String skuQty)
        {
            int skuPriceInt = Integer.parseInt(skuPrice);
            int skuQtyInt = Integer.parseInt(skuQty);
            String skuFinalPrice = String.valueOf(skuPriceInt * skuQtyInt);

            String selection = "order_id = ? AND sku_id = ?";
            String[] selectionArgs = new String[]{orderID, skuID};

            ContentValues salesOrderDetailsValues = new ContentValues();
            salesOrderDetailsValues.put("sku_final_price", skuFinalPrice);
            sqLiteDatabase.update(TBL_SALES_ORDER_DETAILS, salesOrderDetailsValues, selection, selectionArgs);
        }

        private void deleteEntriesInSalesOrderSkuAttributes(String orderDetailID)
        {
            int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
            SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

            String selection = "order_detail_id = ?";
            String[] selectionArgs = {orderDetailID};

            sqLiteDatabase.delete(TBL_SALES_ORDER_SKU_ATTRIBUTES, selection, selectionArgs);
        }

        private int deleteSkuInSalesOrderDetailsTable(String orderDetailID, String skuID)
        {
            int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
            SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

            String selection = "order_detail_id = ? AND sku_id = ?";
            String[] selectionArgs = {orderDetailID, skuID};

            int noOfRowsDeleted = sqLiteDatabase.delete(TBL_SALES_ORDER_DETAILS, selection, selectionArgs);

            return noOfRowsDeleted;
        }

        private void eraseCurrentRegularOrderFor(String retailerID)
        {
            int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
            SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

            ContentValues salesOrderValues = new ContentValues();
            salesOrderValues.put("is_regular", "0");
            sqLiteDatabase.update(TBL_SALES_ORDER, salesOrderValues, "retailer_id = ? AND is_regular = ?", new String[]{retailerID, "1"});
        }

        //This method sets "active order" for given retailer as regular order
        //placeTheOrder() method marks the order as placed & marks the order as inactive
        //So call this method before calling placeTheOrder()
        private void setRegularOrderFor(String retailerID)
        {
            int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
            SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

            ContentValues salesOrderValues = new ContentValues();
            salesOrderValues.put("is_regular", "1");
            sqLiteDatabase.update(TBL_SALES_ORDER, salesOrderValues, "retailer_id = ? AND is_active = ?", new String[]{retailerID, "1"});
        }

        private void placeTheOrder()
        {
            int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
            SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

            //place the order which is currently active
            ContentValues salesOrderValues = new ContentValues();
            salesOrderValues.put("is_placed", "1");

            int noOfRowsUpdated = sqLiteDatabase.update(TBL_SALES_ORDER, salesOrderValues, "is_active = ?", new String[]{"1"});

            if(noOfRowsUpdated == 1)
            {
                skuList = new ArrayList<>();

                notifyDataSetChanged();
                salesOrder_LinearLayout.setVisibility(View.GONE);

                Utils.showSuccessDialog(context, "Success. Order Placed");

            }
            else if(noOfRowsUpdated > 1)
            {
                Utils.showToast(context, "ERROR! 2 or more rows updated");
            }
            else
            {
                Utils.showToast(context, "ERROR! Order Failed");
            }

            //After order is placed, it can't be active. So make it In-Active.
            makePlacedOrderInactive(sqLiteDatabase);
        }

        private void makePlacedOrderInactive(SQLiteDatabase sqLiteDatabase)
        {
            ContentValues salesOrderValues2 = new ContentValues();
            salesOrderValues2.put("is_active", "0");
            sqLiteDatabase.update(TBL_SALES_ORDER, salesOrderValues2, "is_active = ?", new String[]{"1"});
        }
    }
}
