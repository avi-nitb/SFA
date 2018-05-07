package in.etaminepgg.sfa.Fragments;

import android.content.ContentValues;
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
import java.util.Map.Entry;

import in.etaminepgg.sfa.Adapters.SalesOrderAdapter;
import in.etaminepgg.sfa.Models.SalesOrderSku;
import in.etaminepgg.sfa.R;
import in.etaminepgg.sfa.Utilities.Constants;
import in.etaminepgg.sfa.Utilities.ConstantsA;
import in.etaminepgg.sfa.Utilities.DbUtils;
import in.etaminepgg.sfa.Utilities.MyDb;
import in.etaminepgg.sfa.Utilities.Utils;

import static in.etaminepgg.sfa.Utilities.Constants.TBL_RETAILER;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SALES_ORDER;
import static in.etaminepgg.sfa.Utilities.ConstantsA.NONE;

public class SalesOrderFragment extends Fragment {
    TextView emptyAdapter_TextView;
    boolean isRegularOrderCopied = false;
    TextView orderSummary_TextView;
    SalesOrderAdapter salesOrderAdapter;
    LinearLayout salesOrder_LinearLayout;
    LinearLayout salesOrder_LinearLayout_outer;
    RecyclerView salesOrder_RecyclerView;
    CheckBox setOrderAsRegularOrder_CheckBox;
    Button submitSalesOrder_Button;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_retailer_sales_order, container, false);
        this.salesOrder_LinearLayout = (LinearLayout) view.findViewById(R.id.salesOrder_LinearLayout);
        this.salesOrder_LinearLayout_outer = (LinearLayout) view.findViewById(R.id.salesOrder_LinearLayout_outer);
        this.salesOrder_RecyclerView = (RecyclerView) view.findViewById(R.id.salesOrder_RecyclerView);
        this.orderSummary_TextView = (TextView) view.findViewById(R.id.orderSummary_TextView);
        this.setOrderAsRegularOrder_CheckBox = (CheckBox) view.findViewById(R.id.setOrderAsRegularOrder_CheckBox);
        this.submitSalesOrder_Button = (Button) view.findViewById(R.id.submitSalesOrder_Button);
        this.emptyAdapter_TextView = (TextView) view.findViewById(R.id.emptyAdapter_TextView);
        this.salesOrder_RecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.salesOrder_RecyclerView.setItemAnimator(new DefaultItemAnimator());
        this.salesOrder_RecyclerView.setNestedScrollingEnabled(false);
        showCorrectSalesOrder();
        return view;
    }

    public void showCorrectSalesOrder() {
        String activeOrderID = DbUtils.getActiveOrderID();
        if (!activeOrderID.equals(NONE)) {
            String selectedOrderType = getArguments().getString(getResources().getString(R.string.key_selected_order_type));
            if (selectedOrderType.equals(ConstantsA.NEW_ORDER)) {
                setSalesOrderAdapter(activeOrderID);
            } else if (selectedOrderType.equals(ConstantsA.REGULAR_ORDER)) {
                String retailerID = DbUtils.getRetailerID();
                if (retailerID.equals(NONE)) {
                    Utils.showErrorDialog(getActivity(), "retailer ID is: " + retailerID);
                    return;
                }
                if (DbUtils.getRegularOrderIdFor(retailerID).equals(NONE)) {
                    setSalesOrderAdapter(activeOrderID);
                    return;
                }
                String regularOrderID = DbUtils.getRegularOrderIdFor(retailerID);
                List<SalesOrderSku> skuList = DbUtils.getSKUsInSalesOrder("SELECT order_detail_id, sku_id, sku_name, sku_price, sku_qty FROM " + Constants.TBL_SALES_ORDER_DETAILS + " WHERE order_id = ? ;", new String[]{regularOrderID});
                if (!this.isRegularOrderCopied) {
                    for (SalesOrderSku sku : skuList) {
                        insertIntoSalesOrderSkuAttributes(DbUtils.insertIntoSalesOrderDetailsTable(activeOrderID, sku.getSkuID(), sku.getSkuName(), sku.getSkuPrice(), sku.getSkuQty()), getSalesOrderAttributesMapFor(sku.getOrderDetailId()));
                    }
                    this.isRegularOrderCopied = true;
                }
                setSalesOrderAdapter(activeOrderID);
            } else {
                setSalesOrderAdapter(activeOrderID);
            }
        }
    }

    private void setSalesOrderAdapter(String activeOrderID) {
        List<SalesOrderSku> skuList = DbUtils.getSKUsInSalesOrder("SELECT order_detail_id, sku_id, sku_name, sku_price, sku_qty FROM " + Constants.TBL_SALES_ORDER_DETAILS + " WHERE order_id = ? ;", new String[]{activeOrderID});
        if (skuList.size() < 1) {
            this.emptyAdapter_TextView.setVisibility(View.VISIBLE);
            this.salesOrder_LinearLayout.setVisibility(View.GONE);
            return;
        }
        this.emptyAdapter_TextView.setVisibility(View.GONE);
        this.salesOrder_LinearLayout.setVisibility(View.VISIBLE);
        this.salesOrderAdapter = new SalesOrderAdapter(skuList, this.salesOrder_LinearLayout_outer, this.setOrderAsRegularOrder_CheckBox);
        this.salesOrder_RecyclerView.setAdapter(this.salesOrderAdapter);
        this.orderSummary_TextView.setText(DbUtils.getItemCount(activeOrderID) + " items \nTotal:  Rs. " + DbUtils.getOrderTotal(activeOrderID)+"\nOrder for "+DbUtils.getActiveRetailer(activeOrderID));
    }



    Map<Integer, String> getSalesOrderAttributesMapFor(long orderDetailID) {
        Map<Integer, String> soAttributesMap = new HashMap();
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(MyDb.openDatabase(Constants.dbFileFullPath));
        String SQL_SELECT_SALES_ORDER_ATTRIBUTES_SET = "SELECT attribute_id, attribute_value FROM sales_order_sku_attributes WHERE order_detail_id = ? ;";
        String[] selectionArgs = {String.valueOf(orderDetailID)};

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_SALES_ORDER_ATTRIBUTES_SET, selectionArgs);
        while (cursor.moveToNext()) {
            int attributeId = cursor.getInt(cursor.getColumnIndexOrThrow("attribute_id"));
            soAttributesMap.put(Integer.valueOf(attributeId), cursor.getString(cursor.getColumnIndexOrThrow("attribute_value")));
        }
        cursor.close();
        sqLiteDatabase.close();
        Log.e("attributesOfSku", soAttributesMap.toString());
        return soAttributesMap;
    }

    void insertIntoSalesOrderSkuAttributes(long salesOrderDetailID, Map<Integer, String> skuAttributesMap) {
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(MyDb.openDatabase(Constants.dbFileFullPath));
        for (Entry<Integer, String> entry : skuAttributesMap.entrySet()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("order_detail_id", Long.valueOf(salesOrderDetailID));
            contentValues.put("attribute_id", (Integer) entry.getKey());
            contentValues.put("attribute_name", new Utils().getAttributeName(((Integer) entry.getKey()).intValue()));
            contentValues.put("attribute_value", (String) entry.getValue());
            contentValues.put("upload_status", Integer.valueOf(0));
            sqLiteDatabase.insert(Constants.TBL_SALES_ORDER_SKU_ATTRIBUTES, null, contentValues);
        }
        sqLiteDatabase.close();
    }
}
