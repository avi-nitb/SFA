package in.etaminepgg.sfa.Fragments;

import android.content.ContentValues;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.etaminepgg.sfa.Adapters.SalesOrderAdapter;
import in.etaminepgg.sfa.Models.SalesOrderSku;
import in.etaminepgg.sfa.R;
import in.etaminepgg.sfa.Utilities.DbUtils;
import in.etaminepgg.sfa.Utilities.MyDb;
import in.etaminepgg.sfa.Utilities.Utils;

import static in.etaminepgg.sfa.Utilities.Constants.TBL_SALES_ORDER_DETAILS;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SALES_ORDER_SKU_ATTRIBUTES;
import static in.etaminepgg.sfa.Utilities.Constants.dbFileFullPath;
import static in.etaminepgg.sfa.Utilities.ConstantsA.NEW_ORDER;
import static in.etaminepgg.sfa.Utilities.ConstantsA.NONE;
import static in.etaminepgg.sfa.Utilities.ConstantsA.REGULAR_ORDER;
import static in.etaminepgg.sfa.Utilities.DbUtils.getItemCount;
import static in.etaminepgg.sfa.Utilities.DbUtils.getOrderTotal;

/**
 * A simple {@link Fragment} subclass.
 */

public class SalesOrderFragment extends Fragment
{
    //String selectedOrderType;

    RecyclerView salesOrder_RecyclerView;
    CheckBox setOrderAsRegularOrder_CheckBox;
    Button submitSalesOrder_Button;
    SalesOrderAdapter salesOrderAdapter;
    LinearLayout salesOrder_LinearLayout;
    LinearLayout salesOrder_LinearLayout_outer;
    TextView orderSummary_TextView, emptyAdapter_TextView;

    //every time sales order tab is selected, regular order was being copied to active order
    //this variable  helps us to prevent above mentioned issue
    boolean isRegularOrderCopied = false;

    public SalesOrderFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_retailer_sales_order, container, false);
        salesOrder_LinearLayout = (LinearLayout) view.findViewById(R.id.salesOrder_LinearLayout);
        salesOrder_LinearLayout_outer = (LinearLayout) view.findViewById(R.id.salesOrder_LinearLayout_outer);
        salesOrder_RecyclerView = (RecyclerView) view.findViewById(R.id.salesOrder_RecyclerView);
        orderSummary_TextView = (TextView) view.findViewById(R.id.orderSummary_TextView);
        setOrderAsRegularOrder_CheckBox = (CheckBox) view.findViewById(R.id.setOrderAsRegularOrder_CheckBox);
        submitSalesOrder_Button = (Button) view.findViewById(R.id.submitSalesOrder_Button);
        emptyAdapter_TextView = (TextView) view.findViewById(R.id.emptyAdapter_TextView);

        salesOrder_RecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        salesOrder_RecyclerView.setItemAnimator(new DefaultItemAnimator());
        salesOrder_RecyclerView.setNestedScrollingEnabled(false);

        showCorrectSalesOrder();

        return view;
    }

    public void showCorrectSalesOrder()
    {
        String activeOrderID = DbUtils.getActiveOrderID();
        List<SalesOrderSku> skuList;

        //if there is active Order
        if(!activeOrderID.equals(NONE))
        {
            Resources resources = getResources();
            String intentExtraKey = resources.getString(R.string.key_selected_order_type);
            String selectedOrderType = getArguments().getString(intentExtraKey);

            if(selectedOrderType.equals(NEW_ORDER))
            {
                //get sku list from active order and display
                setSalesOrderAdapter(activeOrderID);
            }
            else if(selectedOrderType.equals(REGULAR_ORDER))
            {
                String retailerID = DbUtils.getRetailerID();

                if(!retailerID.equals(NONE))
                {
                    String regularOrderID = DbUtils.getRegularOrderIdFor(retailerID);

                    if(!regularOrderID.equals(NONE))
                    {
                        //get sku list from regular order
                        String SQL_SELECT_SKUs_IN_REGULAR_ORDER = "SELECT order_detail_id, sku_id, sku_name, sku_price, sku_qty FROM " + TBL_SALES_ORDER_DETAILS + " WHERE order_id = ?" + " ;";
                        String[] selectionArgs = new String[]{regularOrderID};
                        skuList = DbUtils.getSKUsInSalesOrder(SQL_SELECT_SKUs_IN_REGULAR_ORDER, selectionArgs);

                        if(!isRegularOrderCopied)
                        {
                            //copy all SKUs in regular order to active order
                            for(SalesOrderSku sku : skuList)
                            {
                                long orderDetailId = DbUtils.insertIntoSalesOrderDetailsTable(activeOrderID, sku.getSkuID(), sku.getSkuName(), sku.getSkuPrice(), sku.getSkuQty());

                                insertIntoSalesOrderSkuAttributes(orderDetailId, getSalesOrderAttributesMapFor(sku.getOrderDetailId()));
                            }

                            isRegularOrderCopied = true;
                        }

                        //get sku list from active order and display
                        setSalesOrderAdapter(activeOrderID);
                    }
                    // but if there is active order
                    else
                    {
                        setSalesOrderAdapter(activeOrderID);
                    }
                }
                else
                {
                    Utils.showErrorDialog(getActivity(), "retailer ID is: " + retailerID);
                }
            }
            // if launched from Dashboard
            else
            {
                //get sku list from active order and display
                setSalesOrderAdapter(activeOrderID);
            }
        }
    }

    private void setSalesOrderAdapter(String activeOrderID)
    {
        String SQL_SELECT_SKUs_IN_SALES_ORDER = "SELECT order_detail_id, sku_id, sku_name, sku_price, sku_qty FROM " + TBL_SALES_ORDER_DETAILS + " WHERE order_id = ?" + " ;";
        String[] selectionArgs = new String[]{activeOrderID};

        List<SalesOrderSku> skuList = DbUtils.getSKUsInSalesOrder(SQL_SELECT_SKUs_IN_SALES_ORDER, selectionArgs);

        if(skuList.size() < 1)
        {
            emptyAdapter_TextView.setVisibility(View.VISIBLE);
            salesOrder_LinearLayout.setVisibility(View.GONE);
        }
        else
        {
            emptyAdapter_TextView.setVisibility(View.GONE);
            salesOrder_LinearLayout.setVisibility(View.VISIBLE);

            salesOrderAdapter = new SalesOrderAdapter(skuList, salesOrder_LinearLayout_outer, setOrderAsRegularOrder_CheckBox);
            salesOrder_RecyclerView.setAdapter(salesOrderAdapter);

            String orderSummary = getItemCount(activeOrderID) + " items \n" + "Total:  Rs. " + getOrderTotal(activeOrderID);
            orderSummary_TextView.setText(orderSummary);
        }
    }

    Map<Integer, String> getSalesOrderAttributesMapFor(long orderDetailID)
    {
        Map<Integer, String> soAttributesMap = new HashMap<>();

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String SQL_SELECT_SALES_ORDER_ATTRIBUTES_SET = "SELECT attribute_id, attribute_value FROM sales_order_sku_attributes WHERE order_detail_id = ? ;";
        String[] selectionArgs = {String.valueOf(orderDetailID)};

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_SALES_ORDER_ATTRIBUTES_SET, selectionArgs);

        while(cursor.moveToNext())
        {
            int attributeId = cursor.getInt(cursor.getColumnIndexOrThrow("attribute_id"));

            String attributeValue = cursor.getString(cursor.getColumnIndexOrThrow("attribute_value"));

            //String attributeName = new Utils().getAttributeName(attributeId);

            soAttributesMap.put(attributeId, attributeValue);
        }

        cursor.close();
        sqLiteDatabase.close();

        Log.e("attributesOfSku", soAttributesMap.toString());

        //CharSequence skuAttributesWithoutCommaAtTheEnd = attributesOfSku.subSequence(0, attributesOfSku.length()-2);

        return soAttributesMap;
    }

    void insertIntoSalesOrderSkuAttributes(long salesOrderDetailID, Map<Integer, String> skuAttributesMap)
    {
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        for(Map.Entry<Integer, String> entry : skuAttributesMap.entrySet())
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put("order_detail_id", salesOrderDetailID);
            contentValues.put("attribute_id", entry.getKey());
            contentValues.put("attribute_name", new Utils().getAttributeName(entry.getKey()));
            contentValues.put("attribute_value", entry.getValue());
            contentValues.put("upload_status", 0);
            sqLiteDatabase.insert(TBL_SALES_ORDER_SKU_ATTRIBUTES, null, contentValues);
        }

        sqLiteDatabase.close();
    }
}
