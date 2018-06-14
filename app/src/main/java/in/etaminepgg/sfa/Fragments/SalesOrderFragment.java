package in.etaminepgg.sfa.Fragments;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.Selection;
import android.text.SpanWatcher;
import android.text.Spannable;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import in.etaminepgg.sfa.Activities.SkuListByGenreActivity;
import in.etaminepgg.sfa.Adapters.SalesOrderAdapter;
import in.etaminepgg.sfa.Models.SalesOrderSku;
import in.etaminepgg.sfa.R;
import in.etaminepgg.sfa.Utilities.Constants;
import in.etaminepgg.sfa.Utilities.ConstantsA;
import in.etaminepgg.sfa.Utilities.DbUtils;
import in.etaminepgg.sfa.Utilities.MyDb;
import in.etaminepgg.sfa.Utilities.PrefixEditText;
import in.etaminepgg.sfa.Utilities.Utils;

import static in.etaminepgg.sfa.Utilities.ConstantsA.NONE;

public class SalesOrderFragment extends Fragment
{
    TextView emptyAdapter_TextView, tv_orderretailername, tv_grandtoalitems, tv_grandtotal;
    boolean isRegularOrderCopied = false;
    TextView orderSummary_TextView;
    SalesOrderAdapter salesOrderAdapter;
    LinearLayout salesOrder_LinearLayout;
    LinearLayout salesOrder_LinearLayout_outer;
    RecyclerView salesOrder_RecyclerView;
    CheckBox setOrderAsRegularOrder_CheckBox;
    Button submitSalesOrder_Button;
    PrefixEditText order_overalldiscount_value;
    final String prefix = "-Rs. ";

    TextView tv_orderretailername_header,tv_order_id,tv_order_date;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_retailer_sales_order, container, false);
        this.salesOrder_LinearLayout = (LinearLayout) view.findViewById(R.id.salesOrder_LinearLayout);
        this.salesOrder_LinearLayout_outer = (LinearLayout) view.findViewById(R.id.salesOrder_LinearLayout_outer);
        this.salesOrder_RecyclerView = (RecyclerView) view.findViewById(R.id.salesOrder_RecyclerView);
        this.orderSummary_TextView = (TextView) view.findViewById(R.id.orderSummary_TextView);
        this.tv_grandtoalitems = (TextView) view.findViewById(R.id.tv_grandtoalitems);
        this.tv_grandtotal = (TextView) view.findViewById(R.id.tv_grandtotal);
        this.order_overalldiscount_value = (PrefixEditText) view.findViewById(R.id.order_overalldiscount_value);
        this.tv_orderretailername = (TextView) view.findViewById(R.id.tv_orderretailername);
        this.tv_orderretailername_header = (TextView) view.findViewById(R.id.tv_orderretailername_header);
        this.tv_order_id = (TextView) view.findViewById(R.id.tv_order_id);
        this.tv_order_date = (TextView) view.findViewById(R.id.tv_order_date);
        this.setOrderAsRegularOrder_CheckBox = (CheckBox) view.findViewById(R.id.setOrderAsRegularOrder_CheckBox);
        this.submitSalesOrder_Button = (Button) view.findViewById(R.id.submitSalesOrder_Button);
        this.emptyAdapter_TextView = (TextView) view.findViewById(R.id.emptyAdapter_TextView);
        this.salesOrder_RecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.salesOrder_RecyclerView.setItemAnimator(new DefaultItemAnimator());
        this.salesOrder_RecyclerView.setNestedScrollingEnabled(false);
        showCorrectSalesOrder();


        tv_orderretailername_header.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {


                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

                // Setting Dialog Title
                alertDialog.setTitle("Confirm Order Cancellation...");

                // Setting Dialog Message
                alertDialog.setMessage("Are you sure you want to cancel order?");

                // Setting Icon to Dialog
                alertDialog.setIcon(R.drawable.ic_check_circle);

                // Setting Positive "Yes" Button
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {

                        // Write your code here to invoke YES event
                        String activeOrderID = DbUtils.getActiveOrderID();

                        DbUtils.deleteOrder(activeOrderID);
                        salesOrder_LinearLayout.setVisibility(View.GONE);
                        emptyAdapter_TextView.setVisibility(View.VISIBLE);

                        ((SkuListByGenreActivity)getActivity()).setFirstTab();
                    }
                });

                // Setting Negative "NO" Button
                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to invoke NO event
                        Toast.makeText(getActivity(), "You clicked on NO", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });

                // Showing Alert Message
                alertDialog.show();


            }
        });
        return view;
    }

    public void showCorrectSalesOrder()
    {
        String activeOrderID = DbUtils.getActiveOrderID();
        if (!activeOrderID.equals(NONE))
        {
            String selectedOrderType = getArguments().getString(getResources().getString(R.string.key_selected_order_type));
            if (selectedOrderType.equals(ConstantsA.NEW_ORDER))
            {
                setSalesOrderAdapter(activeOrderID);
            }
            else if (selectedOrderType.equals(ConstantsA.REGULAR_ORDER))
            {
                String retailerID = DbUtils.getRetailerID();
                if (retailerID.equals(NONE))
                {
                    Utils.showErrorDialog(getActivity(), "retailer ID is: " + retailerID);
                    return;
                }
                if (DbUtils.getRegularOrderIdFor(retailerID).equals(NONE))
                {
                    setSalesOrderAdapter(activeOrderID);
                    return;
                }
                String regularOrderID = DbUtils.getRegularOrderIdFor(retailerID);
                List<SalesOrderSku> skuList = DbUtils.getSKUsInSalesOrder("SELECT order_detail_id, sku_id, sku_name, sku_price, sku_qty,sku_free_qty,sku_discount,sku_price_before_discount,sku_final_price FROM " + Constants.TBL_SALES_ORDER_DETAILS + " WHERE order_id = ? ;", new String[]{regularOrderID});
                if (!this.isRegularOrderCopied)
                {
                    for (SalesOrderSku sku : skuList)
                    {
                        insertIntoSalesOrderSkuAttributes(DbUtils.insertIntoSalesOrderDetailsTable(activeOrderID, sku.getSkuID(), sku.getSkuName(), sku.getSkuPrice(), sku.getSkuQty(),sku.getSku_free_qty(),sku.getSku_discount()), getSalesOrderAttributesMapFor(sku.getOrderDetailId()));
                    }
                    this.isRegularOrderCopied = true;
                }
                setSalesOrderAdapter(activeOrderID);
            }
            else
            {
                setSalesOrderAdapter(activeOrderID);
            }
        }
    }

    private void setSalesOrderAdapter(String activeOrderID)
    {
        List<SalesOrderSku> skuList = DbUtils.getSKUsInSalesOrder("SELECT order_detail_id, sku_id, sku_name, sku_price, sku_qty,sku_free_qty,sku_discount,sku_price_before_discount,sku_final_price FROM " + Constants.TBL_SALES_ORDER_DETAILS + " WHERE order_id = ? ;", new String[]{activeOrderID});
        this.tv_orderretailername_header.setText(DbUtils.getActiveRetailer(activeOrderID));

        this.tv_order_id.setText("Order ID : " + activeOrderID);


        try
        {
            DateFormat inFormat = new SimpleDateFormat( "yyyy-MM-dd hh:mm:ss");

            Date date=inFormat.parse( DbUtils.getActiveOrderIDDate(activeOrderID));

            DateFormat outFormat = new SimpleDateFormat( "dd-MM-yyyy hh:mm aa");

            String myDate = outFormat.format(date);


            this.tv_order_date.setText("Order Date : " +myDate);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        if (skuList.size() < 1)
        {
            //this.emptyAdapter_TextView.setVisibility(View.VISIBLE);
           // this.salesOrder_LinearLayout.setVisibility(View.GONE);
            this.salesOrder_LinearLayout.setVisibility(View.VISIBLE);
            this.emptyAdapter_TextView.setVisibility(View.GONE);
            this.salesOrder_LinearLayout.findViewById(R.id.linlay_price).setVisibility(View.GONE);
            return;
        }
        this.emptyAdapter_TextView.setVisibility(View.GONE);
        this.salesOrder_LinearLayout.setVisibility(View.VISIBLE);
        this.salesOrder_LinearLayout.findViewById(R.id.linlay_price).setVisibility(View.VISIBLE);
        this.salesOrderAdapter = new SalesOrderAdapter(skuList, this.salesOrder_LinearLayout_outer, this.setOrderAsRegularOrder_CheckBox);
        this.salesOrder_RecyclerView.setAdapter(this.salesOrderAdapter);
        //this.orderSummary_TextView.setText(DbUtils.getItemCount(activeOrderID) + " items \nTotal:  Rs. " + DbUtils.getOrderTotal(activeOrderID)+"\nOrder for "+DbUtils.getActiveRetailer(activeOrderID));

        this.orderSummary_TextView.setText("Rs. " + DbUtils.getOrderTotal(activeOrderID));

        this.tv_orderretailername.setText("Order For " + DbUtils.getActiveRetailer(activeOrderID));



        this.tv_grandtoalitems.setText("Grand Total ( " + DbUtils.getItemCount(activeOrderID) + " items )");

      //  this.order_overalldiscount_value.setText("-" + ConstantsA.RS + "0");

        String overalldiscount = this.order_overalldiscount_value.getText().toString();

        if(!overalldiscount.isEmpty()){

            this.tv_grandtotal.setText("Rs. " + (DbUtils.getOrderTotal(activeOrderID) - Float.parseFloat(overalldiscount)));
        }

    }


    Map<Integer, String> getSalesOrderAttributesMapFor(long orderDetailID)
    {
        Map<Integer, String> soAttributesMap = new HashMap();
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(MyDb.openDatabase(Constants.dbFileFullPath));
        String SQL_SELECT_SALES_ORDER_ATTRIBUTES_SET = "SELECT attribute_id, attribute_value FROM sales_order_sku_attributes WHERE order_detail_id = ? ;";
        String[] selectionArgs = {String.valueOf(orderDetailID)};

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_SALES_ORDER_ATTRIBUTES_SET, selectionArgs);
        while (cursor.moveToNext())
        {
            int attributeId = cursor.getInt(cursor.getColumnIndexOrThrow("attribute_id"));
            soAttributesMap.put(Integer.valueOf(attributeId), cursor.getString(cursor.getColumnIndexOrThrow("attribute_value")));
        }
        cursor.close();
        sqLiteDatabase.close();
        Log.e("attributesOfSku", soAttributesMap.toString());
        return soAttributesMap;
    }

    void insertIntoSalesOrderSkuAttributes(long salesOrderDetailID, Map<Integer, String> skuAttributesMap)
    {
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(MyDb.openDatabase(Constants.dbFileFullPath));
        for (Entry<Integer, String> entry : skuAttributesMap.entrySet())
        {
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
