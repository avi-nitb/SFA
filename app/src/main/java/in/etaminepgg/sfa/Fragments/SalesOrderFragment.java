package in.etaminepgg.sfa.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.etaminepgg.sfa.Activities.LoginActivity;
import in.etaminepgg.sfa.Activities.SkuListByGenreActivity;
import in.etaminepgg.sfa.Adapters.SalesOrderAdapter;
import in.etaminepgg.sfa.Models.QuantityDiscountModel;
import in.etaminepgg.sfa.Models.SalesOrderSku;
import in.etaminepgg.sfa.R;
import in.etaminepgg.sfa.Utilities.Constants;
import in.etaminepgg.sfa.Utilities.ConstantsA;
import in.etaminepgg.sfa.Utilities.DbUtils;
import in.etaminepgg.sfa.Utilities.MyDb;
import in.etaminepgg.sfa.Utilities.Utils;

import static in.etaminepgg.sfa.Utilities.Constants.TBL_SALES_ORDER_DETAILS;
import static in.etaminepgg.sfa.Utilities.Constants.dbFileFullPath;
import static in.etaminepgg.sfa.Utilities.ConstantsA.NONE;
import static in.etaminepgg.sfa.Utilities.DbUtils.getSkuQuantityDiscount;


public class SalesOrderFragment extends Fragment
{
    final String prefix = "-Rs. ";
    TextView emptyAdapter_TextView, tv_orderretailername, tv_grandtoalitems, tv_grandtotal;
    boolean isRegularOrderCopied = false;
    TextView orderSummary_TextView;
    SalesOrderAdapter salesOrderAdapter;
    LinearLayout salesOrder_LinearLayout;
    LinearLayout salesOrder_LinearLayout_outer;
    RecyclerView salesOrder_RecyclerView;
    CheckBox setOrderAsRegularOrder_CheckBox;
    Button submitSalesOrder_Button;
    EditText order_overalldiscount_value;
    TextView tv_orderretailername_header, tv_order_id, tv_order_date, so_upldcount;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_retailer_sales_order, container, false);
        this.salesOrder_LinearLayout = (LinearLayout) view.findViewById(R.id.salesOrder_LinearLayout);
        this.salesOrder_LinearLayout_outer = (LinearLayout) view.findViewById(R.id.salesOrder_LinearLayout_outer);
        this.salesOrder_RecyclerView = (RecyclerView) view.findViewById(R.id.salesOrder_RecyclerView);
        this.orderSummary_TextView = (TextView) view.findViewById(R.id.orderSummary_TextView);
        this.tv_grandtoalitems = (TextView) view.findViewById(R.id.tv_grandtoalitems);
        this.tv_grandtotal = (TextView) view.findViewById(R.id.tv_grandtotal);
        this.order_overalldiscount_value = (EditText) view.findViewById(R.id.order_overalldiscount_value);
        this.tv_orderretailername = (TextView) view.findViewById(R.id.tv_orderretailername);
        this.tv_orderretailername_header = (TextView) view.findViewById(R.id.tv_orderretailername_header);
        this.tv_order_id = (TextView) view.findViewById(R.id.tv_order_id);
        this.tv_order_date = (TextView) view.findViewById(R.id.tv_order_date);
        this.so_upldcount = (TextView) view.findViewById(R.id.so_upldcount);
        this.setOrderAsRegularOrder_CheckBox = (CheckBox) view.findViewById(R.id.setOrderAsRegularOrder_CheckBox);
        this.submitSalesOrder_Button = (Button) view.findViewById(R.id.submitSalesOrder_Button);
        this.emptyAdapter_TextView = (TextView) view.findViewById(R.id.emptyAdapter_TextView);
        this.salesOrder_RecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.salesOrder_RecyclerView.setItemAnimator(new DefaultItemAnimator());
        this.salesOrder_RecyclerView.setNestedScrollingEnabled(false);

        so_upldcount.setText("No. of Sales orders to Upload: " + DbUtils.getSalesOrderCountForUpload(getActivity()));


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
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {

                        // Write your code here to invoke YES event
                        String mobile_activeOrderID = DbUtils.getActiveMobileOrderID(SkuListByGenreActivity.retailer_id_from_SOT, SkuListByGenreActivity.mobile_retailer_id_from_SOT, SkuListByGenreActivity.isNewRegular_from_SOT);

                        DbUtils.deleteOrder(mobile_activeOrderID);
                        salesOrder_LinearLayout.setVisibility(View.GONE);
                        emptyAdapter_TextView.setVisibility(View.VISIBLE);

                    }
                });

                // Setting Negative "NO" Button
                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
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

    //show sales adapter if regular or new
    public void showCorrectSalesOrder()
    {
        String activeOrderID = DbUtils.getActiveOrderID(SkuListByGenreActivity.retailer_id_from_SOT, SkuListByGenreActivity.mobile_retailer_id_from_SOT, SkuListByGenreActivity.isNewRegular_from_SOT);
        String mobileActiveOrderID = DbUtils.getActiveMobileOrderID(SkuListByGenreActivity.retailer_id_from_SOT, SkuListByGenreActivity.mobile_retailer_id_from_SOT, SkuListByGenreActivity.isNewRegular_from_SOT);
        if (!activeOrderID.equals(NONE))
        {
            String selectedOrderType = getArguments().getString(getResources().getString(R.string.key_selected_order_type));
            if (selectedOrderType.equals(ConstantsA.NEW_ORDER))
            {
                setSalesOrderAdapter(activeOrderID, mobileActiveOrderID);
            }
            else if (selectedOrderType.equals(ConstantsA.REGULAR_ORDER))
            {

                if (SkuListByGenreActivity.mobile_retailer_id_from_SOT.equals(NONE))
                {
                    Utils.showErrorDialog(getActivity(), "retailer ID is: " + SkuListByGenreActivity.mobile_retailer_id_from_SOT);
                    return;
                }
                if (DbUtils.getRegularOrderIdFor(SkuListByGenreActivity.mobile_retailer_id_from_SOT).equals(NONE))
                {
                    setSalesOrderAdapter(activeOrderID, mobileActiveOrderID);
                    return;
                }
                String regularOrderID = DbUtils.getRegularOrderIdFor(SkuListByGenreActivity.mobile_retailer_id_from_SOT);
                List<SalesOrderSku> skuList = DbUtils.getSKUsInSalesOrder("SELECT order_detail_id, sku_id, sku_name, sku_price, sku_qty,sku_free_qty,sku_discount,sku_price_before_discount,sku_final_price FROM " + Constants.TBL_SALES_ORDER_DETAILS + " WHERE mobile_order_id = ? ;", new String[]{regularOrderID});
                if (!this.isRegularOrderCopied)
                {
                    for (SalesOrderSku sku : skuList)
                    {
                        Map<String, String> selectedSkuQuantityMap = new HashMap<String, String>();

                        selectedSkuQuantityMap.put(getActivity().getResources().getString(R.string.tag_sky_qty_id), sku.getSkuQty());
                        selectedSkuQuantityMap.put(getActivity().getResources().getString(R.string.tag_sky_freeQty_id), sku.getSku_free_qty());
                        selectedSkuQuantityMap.put(getActivity().getResources().getString(R.string.tag_sky_discount_id), sku.getSku_discount());

                        insertSkuOrIncreaseQuantity(getActivity(), activeOrderID, mobileActiveOrderID, sku.getSkuID(), sku.getSkuName(), sku.getSkuPrice(), selectedSkuQuantityMap);

                        //insertIntoSalesOrderSkuAttributes(DbUtils.insertIntoSalesOrderDetailsTable(activeOrderID,mobileActiveOrderID, sku.getSkuID(), sku.getSkuName(), sku.getSkuPrice(), sku.getSkuQty(),sku.getSku_free_qty(),sku.getSku_discount()), getSalesOrderAttributesMapFor(sku.getOrderDetailId()));
                    }
                    this.isRegularOrderCopied = true;
                }
                setSalesOrderAdapter(activeOrderID, mobileActiveOrderID);
            }
            else
            {
                setSalesOrderAdapter(activeOrderID, mobileActiveOrderID);
            }
        }
    }


    //checking for insert or increase sku
    void insertSkuOrIncreaseQuantity(final Context context, String salesOrderID, String mobileSalesOrderID, String skuID, String skuName, String skuPrice, Map<String, String> selectedSkuQuantityMap)
    {

        String finalqty = NONE;
        String finalFreeQty = NONE;
        String finalDiscount = NONE;

        finalqty = selectedSkuQuantityMap.get(context.getResources().getString(R.string.tag_sky_qty_id));
        finalFreeQty = selectedSkuQuantityMap.get(context.getResources().getString(R.string.tag_sky_freeQty_id));
        finalDiscount = selectedSkuQuantityMap.get(context.getResources().getString(R.string.tag_sky_discount_id));


        //if Sku doesn't exists in the sales order details table, insert new row for that Sku
        if (!isSkuPresentSF(skuID, salesOrderID, mobileSalesOrderID))
        {
            long rowID = 0;

            int newSkuQuantity = Integer.parseInt(finalqty);
            int newSkuFreeQuantity = Integer.parseInt(finalFreeQty);
            float newSkuDisc = Float.parseFloat(finalDiscount);

            float skuPriceBeforeDiscount = Float.parseFloat(skuPrice) * newSkuQuantity;

            if (newSkuDisc < skuPriceBeforeDiscount)
            {

                rowID = DbUtils.insertIntoSalesOrderDetailsTable(context, salesOrderID, mobileSalesOrderID, skuID, skuName, skuPrice, finalqty, finalFreeQty, finalDiscount);

                if (rowID != -1L)
                {
                    long salesOrderDetailID = rowID;

                    //insertIntoSalesOrderSkuAttributes(salesOrderDetailID);

                    Utils.showPopUp(LoginActivity.baseContext, "SKU added to cart for 1st time");
                }


            }
            else
            {
                Utils.showPopUp(LoginActivity.baseContext, "SKU Discount should be less than SKU Total Price.");

            }

        }
        else
        {
            List<Long> orderDetailIDsList = getOrderDetailIDsList(salesOrderID, skuID);

            //long orderDetailIdWithSameAttributes = getOrderDetailIdWithSameAttributes(orderDetailIDsList);
            long orderDetailIdWithSameAttributes = 1;

            if (orderDetailIdWithSameAttributes > 0)
            {
                ArrayList<QuantityDiscountModel> quantityDiscountModelArrayList = new ArrayList<>();

                quantityDiscountModelArrayList = getSkuQuantityDiscount(orderDetailIdWithSameAttributes);

                int currentSkuQuantity = quantityDiscountModelArrayList.get(0).getSkuQty();
                int currentSkuFreeQuantity = quantityDiscountModelArrayList.get(0).getSkuFreeQty();
                float currentSkuDiscount = quantityDiscountModelArrayList.get(0).getSkuDiscount();

                int newSkuQuantity = currentSkuQuantity + Integer.parseInt(finalqty);
                int newSkuFreeQuantity = currentSkuFreeQuantity + Integer.parseInt(finalFreeQty);
                float newSkuDisc = currentSkuDiscount + Float.parseFloat(finalDiscount);

                float skuPriceBeforeDiscount = quantityDiscountModelArrayList.get(0).getSkuUnitPrice() * newSkuQuantity;

                if (newSkuDisc < skuPriceBeforeDiscount)
                {

                    DbUtils.increaseSkuQuantityFreeQuantityDiscount(orderDetailIdWithSameAttributes, quantityDiscountModelArrayList.get(0).getSkuUnitPrice(), newSkuQuantity, newSkuFreeQuantity, newSkuDisc);

                    //Utils.showPopUp(LoginActivity.baseContext, "SKU quantity increased");
                }
                else
                {
                    Utils.showPopUp(LoginActivity.baseContext, "SKU Discount should be less than SKU Total Price.");

                }

            }
            else if (orderDetailIdWithSameAttributes == -1L)
            {
                long rowID = DbUtils.insertIntoSalesOrderDetailsTable(context, salesOrderID, mobileSalesOrderID, skuID, skuName, skuPrice, finalqty, finalFreeQty, finalDiscount);

                if (rowID != -1L)
                {
                    long salesOrderDetailID = rowID;

                    //insertIntoSalesOrderSkuAttributes(salesOrderDetailID);


                    Utils.showPopUp(LoginActivity.baseContext, "SKU with different attributes added to cart");

                }
            }
        }

        //Utils.showToast(context, skuName + "(id: " + skuID + ")" + "added to sales order");
    }

    private void setSalesOrderAdapter(String activeOrderID, String mobileActiveOrderId)
    {
        List<SalesOrderSku> skuList = DbUtils.getSKUsInSalesOrder("SELECT order_detail_id, sku_id, sku_name, sku_price, sku_qty,sku_free_qty,sku_discount,sku_price_before_discount,sku_final_price FROM " + Constants.TBL_SALES_ORDER_DETAILS + " WHERE order_id = ? AND mobile_order_id = ?;", new String[]{activeOrderID, mobileActiveOrderId});
        this.tv_orderretailername_header.setText(DbUtils.getActiveRetailer(mobileActiveOrderId));

        if (activeOrderID.length() > 0)
        {

            this.tv_order_id.setText("Order ID : " + activeOrderID);
        }
        else
        {
            this.tv_order_id.setText("Order ID : " + mobileActiveOrderId);

        }


        try
        {
            DateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

            Date date = inFormat.parse(DbUtils.getActiveOrderIDDate(activeOrderID));

            DateFormat outFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");

            String myDate = outFormat.format(date);


            this.tv_order_date.setText("Order Date : " + myDate);
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

        this.orderSummary_TextView.setText("Rs. " + DbUtils.getOrderTotal(activeOrderID, mobileActiveOrderId));

        this.tv_orderretailername.setText("Order For " + DbUtils.getActiveRetailer(mobileActiveOrderId));


        this.tv_grandtoalitems.setText("Grand Total ( " + skuList.size() + " items )");

        //  this.order_overalldiscount_value.setText("-" + ConstantsA.RS + "0");

        String overalldiscount = this.order_overalldiscount_value.getText().toString();

        if (!overalldiscount.isEmpty())
        {

            this.tv_grandtotal.setText("Rs. " + (DbUtils.getOrderTotal(activeOrderID, mobileActiveOrderId) - Float.parseFloat(overalldiscount)));
        }

    }

    //checking for sku present for second time or more adding

    boolean isSkuPresentSF(String skuID, String salesOrderID, String mobileSalesOrderID)
    {
        boolean isSkuPresent = false;

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String SQL_SELECT_SKU_FROM_SALES_ORDER_DETAILS =
                "SELECT" + " sku_id " + "FROM " + TBL_SALES_ORDER_DETAILS + " WHERE " + "order_id " + "= ? AND " + "sku_id " + "= ? " + " AND mobile_order_id = ? ";
        String[] selectionArgs = {salesOrderID, skuID, mobileSalesOrderID};
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_SKU_FROM_SALES_ORDER_DETAILS, selectionArgs);

        if (cursor.moveToFirst())
        {
            isSkuPresent = true;
        }

        cursor.close();
        sqLiteDatabase.close();

        return isSkuPresent;
    }


    //get order detail list
    List<Long> getOrderDetailIDsList(String salesOrderID, String skuID)
    {
        List<Long> orderDetailIDsList = new ArrayList<>();

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String SQL_SELECT_ORDER_DETAIL_IDS = "SELECT DISTINCT order_detail_id FROM sales_order_details WHERE order_id = ? AND sku_id = ? ;";
        String[] selectionArgs = {salesOrderID, skuID};

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_ORDER_DETAIL_IDS, selectionArgs);

        while (cursor.moveToNext())
        {
            Long orderDetailId = cursor.getLong(cursor.getColumnIndexOrThrow("order_detail_id"));

            orderDetailIDsList.add(orderDetailId);
        }

        cursor.close();
        sqLiteDatabase.close();

        return orderDetailIDsList;
    }

    Map<Integer, String> getSalesOrderSkuAttributesMap(long orderDetailID)
    {
        Map<Integer, String> salesOrderSkuAttributesMap = new HashMap<>();

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String SQL_SELECT_SALES_ORDER_ATTRIBUTES_SET = "SELECT attribute_id, attribute_value FROM sales_order_sku_attributes WHERE order_detail_id = ? ;";
        String[] selectionArgs = {String.valueOf(orderDetailID)};

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_SALES_ORDER_ATTRIBUTES_SET, selectionArgs);

        while (cursor.moveToNext())
        {
            int attributeId = cursor.getInt(cursor.getColumnIndexOrThrow("attribute_id"));
            String attributeValue = cursor.getString(cursor.getColumnIndexOrThrow("attribute_value"));

            salesOrderSkuAttributesMap.put(attributeId, attributeValue);
        }

        cursor.close();
        sqLiteDatabase.close();

        return salesOrderSkuAttributesMap;
    }
}
