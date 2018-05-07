package in.etaminepgg.sfa.Adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import in.etaminepgg.sfa.Activities.LoginActivity;
import in.etaminepgg.sfa.InputModel_For_Network.IM_PutSalesorderDetails;
import in.etaminepgg.sfa.Models.PutSalesOrderDetails;
import in.etaminepgg.sfa.Models.SalesOrderSku;
import in.etaminepgg.sfa.Network.API_Call_Retrofit;
import in.etaminepgg.sfa.Network.Apimethods;
import in.etaminepgg.sfa.R;
import in.etaminepgg.sfa.Utilities.Constants;
import in.etaminepgg.sfa.Utilities.ConstantsA;
import in.etaminepgg.sfa.Utilities.DbUtils;
import in.etaminepgg.sfa.Utilities.MyDb;
import in.etaminepgg.sfa.Utilities.MySharedPrefrencesData;
import in.etaminepgg.sfa.Utilities.RoundedCornersTransformation;
import in.etaminepgg.sfa.Utilities.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static in.etaminepgg.sfa.Utilities.Constants.TBL_SALES_ORDER;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SALES_ORDER_DETAILS;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SALES_ORDER_SKU_ATTRIBUTES;
import static in.etaminepgg.sfa.Utilities.Constants.dbFileFullPath;
import static in.etaminepgg.sfa.Utilities.ConstantsA.NONE;
import static in.etaminepgg.sfa.Utilities.DbUtils.makeCurrentActiveOrderInactive;

public class SalesOrderAdapter extends Adapter<SalesOrderAdapter.SkuInfoViewHolder> {
    Context context;
    private TextView orderSummary_TextView;
    private LinearLayout salesOrder_LinearLayout;
    private LinearLayout salesOrder_LinearLayout_outer;
    private CheckBox setOrderAsRegularOrder_CheckBox;
    private List<SalesOrderSku> skuList;
    private Button submitSalesOrder_Button ;

    class SkuInfoViewHolder extends ViewHolder {
        ImageView deleteSKU_ImageButton;
        TextView skuName_TextView;
        ImageView skuPhoto_ImageView;
        TextView skuPrice_TextView;
        EditText skuQuantity_TextInputEditText;
        TextView sku_SO_Attr_TextView;

        SkuInfoViewHolder(final View itemView) {
            super(itemView);
            this.skuPhoto_ImageView = (ImageView) itemView.findViewById(R.id.skuPhoto_ImageView);
            this.skuName_TextView = (TextView) itemView.findViewById(R.id.skuName_TextView);
            this.skuPrice_TextView = (TextView) itemView.findViewById(R.id.skuPrice_TextView);
            this.sku_SO_Attr_TextView = (TextView) itemView.findViewById(R.id.sku_SO_Attr_TextView);
            this.skuQuantity_TextInputEditText = (EditText) itemView.findViewById(R.id.skuQuantity_TextInputEditText);
            this.deleteSKU_ImageButton = (ImageView) itemView.findViewById(R.id.deleteSKU_ImageButton);
            this.skuQuantity_TextInputEditText.addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                public void afterTextChanged(Editable editable) {
                    String skuID = itemView.getTag(R.string.tag_sku_id).toString();
                    String skuPrice = itemView.getTag(R.string.tag_sku_price).toString();
                    String skuQty = editable.toString().trim();
                    if (!skuQty.isEmpty()) {
                        SkuInfoViewHolder.this.updateSkuQtyInSalesOrder(skuID, skuPrice, skuQty);
                        ((SalesOrderSku) SalesOrderAdapter.this.skuList.get(SkuInfoViewHolder.this.getLayoutPosition())).setSkuQty(skuQty);
                        String activeOrderID = DbUtils.getActiveOrderID();
                        SalesOrderAdapter.this.orderSummary_TextView.setText(DbUtils.getItemCount(activeOrderID) + " items \n\nTotal:  Rs. " + DbUtils.getOrderTotal(activeOrderID)+"\n\nOrder for "+DbUtils.getActiveRetailer(activeOrderID));
                    }
                }
            });
            this.deleteSKU_ImageButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    int position = SkuInfoViewHolder.this.getLayoutPosition();
                    String orderDetailID = itemView.getTag(R.string.tag_order_detail_id).toString();
                    if (SkuInfoViewHolder.this.deleteSkuInSalesOrderDetailsTable(orderDetailID, itemView.getTag(R.string.tag_sku_id).toString()) == 1) {
                        SkuInfoViewHolder.this.deleteEntriesInSalesOrderSkuAttributes(orderDetailID);
                        SalesOrderAdapter.this.skuList.remove(position);
                        SalesOrderAdapter.this.notifyItemRemoved(position);
                        SalesOrderAdapter.this.notifyItemRangeChanged(position, SalesOrderAdapter.this.skuList.size());
                        String activeOrderID = DbUtils.getActiveOrderID();
                        SalesOrderAdapter.this.orderSummary_TextView.setText(DbUtils.getItemCount(activeOrderID) + " items \nTotal:  Rs. " + DbUtils.getOrderTotal(activeOrderID)+"\nOrder for "+DbUtils.getActiveRetailer(activeOrderID));
                        if (SalesOrderAdapter.this.skuList.size() <= 0) {
                            SalesOrderAdapter.this.salesOrder_LinearLayout.setVisibility(View.GONE);
                            SalesOrderAdapter.this.salesOrder_LinearLayout_outer.findViewById(R.id.emptyAdapter_TextView).setVisibility(View.VISIBLE);
                            return;
                        }
                        SalesOrderAdapter.this.salesOrder_LinearLayout.setVisibility(View.VISIBLE);
                        SalesOrderAdapter.this.salesOrder_LinearLayout_outer.findViewById(R.id.emptyAdapter_TextView).setVisibility(View.GONE);
                        return;
                    }
                    Utils.showToast(LoginActivity.baseContext, "Error Removing SKU");
                }
            });
            SalesOrderAdapter.this.submitSalesOrder_Button.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    String retailerID = DbUtils.getRetailerID();
                    if (!SalesOrderAdapter.this.setOrderAsRegularOrder_CheckBox.isChecked()) {
                        SkuInfoViewHolder.this.placeTheOrder();
                    } else if (retailerID.equals(ConstantsA.NONE)) {
                        Utils.showErrorDialog(LoginActivity.baseContext, "Order Failed. No Active Order");
                    } else {
                        SkuInfoViewHolder.this.eraseCurrentRegularOrderFor(retailerID);
                        SkuInfoViewHolder.this.setRegularOrderFor(retailerID);
                        SkuInfoViewHolder.this.placeTheOrder();
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



        private void updateSkuFinalPriceInSalesOrder(SQLiteDatabase sqLiteDatabase, String orderID, String skuID, String skuPrice, String skuQty) {
            String skuFinalPrice = String.valueOf(Float.parseFloat(skuPrice) * Integer.parseInt(skuQty));
            String[] selectionArgs = new String[]{orderID, skuID};
            ContentValues salesOrderDetailsValues = new ContentValues();
            salesOrderDetailsValues.put("sku_final_price", skuFinalPrice);
            sqLiteDatabase.update(TBL_SALES_ORDER_DETAILS, salesOrderDetailsValues, "order_id = ? AND sku_id = ?", selectionArgs);
        }

        private void deleteEntriesInSalesOrderSkuAttributes(String orderDetailID) {
            String[] selectionArgs = new String[]{orderDetailID};
            MyDb.getDbHandle(MyDb.openDatabase(dbFileFullPath)).delete(TBL_SALES_ORDER_SKU_ATTRIBUTES, "order_detail_id = ?", selectionArgs);
        }

        private int deleteSkuInSalesOrderDetailsTable(String orderDetailID, String skuID) {
            String[] selectionArgs = new String[]{orderDetailID, skuID};
            return MyDb.getDbHandle(MyDb.openDatabase(dbFileFullPath)).delete(TBL_SALES_ORDER_DETAILS, "order_detail_id = ? AND sku_id = ?", selectionArgs);
        }

        private void eraseCurrentRegularOrderFor(String retailerID) {
            SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(MyDb.openDatabase(dbFileFullPath));
            ContentValues salesOrderValues = new ContentValues();
            salesOrderValues.put("is_regular", "0");
            sqLiteDatabase.update(Constants.TBL_SALES_ORDER, salesOrderValues, "retailer_id = ? AND is_regular = ? AND emp_id = ?", new String[]{retailerID, "1",new MySharedPrefrencesData().getUser_Id(context)});
        }

        private void setRegularOrderFor(String retailerID) {
            SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(MyDb.openDatabase(dbFileFullPath));
            ContentValues salesOrderValues = new ContentValues();
            salesOrderValues.put("is_regular", "1");
            sqLiteDatabase.update(Constants.TBL_SALES_ORDER, salesOrderValues, "retailer_id = ? AND is_active = ? AND emp_id = ?", new String[]{retailerID, "1",new MySharedPrefrencesData().getUser_Id(context)});
        }

        private void placeTheOrder() {
            SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(MyDb.openDatabase(dbFileFullPath));
            ContentValues salesOrderValues = new ContentValues();
            salesOrderValues.put("is_placed", "1");

            String active_orderId=getActiveOrderIdIfHasSalesOrderDetailId();
            int noOfRowsUpdated = sqLiteDatabase.update(Constants.TBL_SALES_ORDER, salesOrderValues, "is_active = ? AND emp_id =? ", new String[]{"1",new MySharedPrefrencesData().getUser_Id(context)});
            if (noOfRowsUpdated == 1) {
                network_call_for_PutSalesOrderdetails();
            } else if (noOfRowsUpdated > 1) {
                Utils.showToast(this.itemView.getContext(), "ERROR! 2 or more rows updated");
            } else {
                Utils.showToast(this.itemView.getContext(), "ERROR! Order Failed");
            }
        }

        private String getActiveOrderIdIfHasSalesOrderDetailId() {

            String activeOrderID = NONE;
            int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
            SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

            String SQL_SELECT_ACTIVE_SALES_ORDER_ID = "select so.order_id from " + TBL_SALES_ORDER +" so "+ " INNER JOIN " +TBL_SALES_ORDER_DETAILS+" sod "+" ON so.order_id = sod.order_id "+ " WHERE " + "so.is_active = ? AND so.emp_id = ?";
            String[] selectionArgs = new String[]{"1",new MySharedPrefrencesData().getUser_Id(LoginActivity.baseContext)};

            Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_ACTIVE_SALES_ORDER_ID, selectionArgs);

            ContentValues salesOrderValues = new ContentValues();
            salesOrderValues.put("is_active", "0");

            if(cursor.moveToFirst())
            {
                activeOrderID = cursor.getString(cursor.getColumnIndexOrThrow("order_id"));

                sqLiteDatabase.update(TBL_SALES_ORDER, salesOrderValues, "is_active = ? AND emp_id = ? AND order_id != ?", new String[]{"1",new MySharedPrefrencesData().getUser_Id(LoginActivity.baseContext),activeOrderID});
            }

            cursor.close();
            sqLiteDatabase.close();

            return activeOrderID;
        }

        private void network_call_for_PutSalesOrderdetails()
        {

            Apimethods apimethods = API_Call_Retrofit.getretrofit(context).create(Apimethods.class);


            ArrayList<IM_PutSalesorderDetails.SalesDatum> salesData_list = new ArrayList<>();

            final ArrayList<String> orderdetailidlist = new ArrayList<>();

            for (int i = 0; i < skuList.size(); i++)
            {

                long orderDetailID = skuList.get(i).getOrderDetailId();

                orderdetailidlist.add(String.valueOf(orderDetailID));

                ArrayList<IM_PutSalesorderDetails.SalesDatum.SkuAttribute> attr_list = new ArrayList<>();

                int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
                SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

                String SQL_SELECT_SALES_ORDER_ATTRIBUTES_SET = "SELECT attribute_id,attribute_name, attribute_value FROM sales_order_sku_attributes WHERE order_detail_id = ? ;";
                String[] selectionArgs = {String.valueOf(orderDetailID)};

                Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_SALES_ORDER_ATTRIBUTES_SET, selectionArgs);

                while (cursor.moveToNext())
                {
                    int attributeId = cursor.getInt(cursor.getColumnIndexOrThrow("attribute_id"));

                    String attributeName = cursor.getString(cursor.getColumnIndexOrThrow("attribute_name"));

                    String attributeValue = cursor.getString(cursor.getColumnIndexOrThrow("attribute_value"));

                    IM_PutSalesorderDetails.SalesDatum.SkuAttribute skuAttribute = new IM_PutSalesorderDetails().new SalesDatum().new SkuAttribute(String.valueOf(attributeId), attributeName, attributeValue);
                    attr_list.add(skuAttribute);
                }

                Log.e("attr_list", new Gson().toJson(attr_list));

                //TODO:required_by_date have to send later

                IM_PutSalesorderDetails.SalesDatum salesDatum = new IM_PutSalesorderDetails().new SalesDatum(skuList.get(i).getSkuID(), skuList.get(i).getSkuPrice(),skuList.get(i).getSkuPrice(), skuList.get(i).getSkuQty(), "", attr_list);
                salesData_list.add(salesDatum);

            }
            IM_PutSalesorderDetails im_putSalesorderDetails = new IM_PutSalesorderDetails(new MySharedPrefrencesData().getEmployee_AuthKey(context), DbUtils.getActiveOrderID(), String.valueOf(DbUtils.getOrderTotal(DbUtils.getActiveOrderID())),"0","1", salesData_list);

            Log.i("putsalesdetails_ip", new Gson().toJson(im_putSalesorderDetails));


            Call<PutSalesOrderDetails> call = apimethods.putSalesOrderDetails(im_putSalesorderDetails);

            call.enqueue(new Callback<PutSalesOrderDetails>()
            {
                @Override
                public void onResponse(Call<PutSalesOrderDetails> call, Response<PutSalesOrderDetails> response)
                {

                    if (response.isSuccessful())
                    {
                        int valueFromOpenDatabase1 = MyDb.openDatabase(dbFileFullPath);
                        SQLiteDatabase sqLiteDatabase1 = MyDb.getDbHandle(valueFromOpenDatabase1);

                        PutSalesOrderDetails putSalesOrderDetails = response.body();

                        Log.i("putsalesdetails_op", new Gson().toJson(putSalesOrderDetails));

                        for (int i=0;i<putSalesOrderDetails.getSalesOrderDetailsId().size();i++)
                        {

                            String  orderDetailID = orderdetailidlist.get(i);

                            ContentValues salesOrderDetailsValues = new ContentValues();
                            salesOrderDetailsValues.put("server_order_detail_id", putSalesOrderDetails.getSalesOrderDetailsId().get(i));

                            sqLiteDatabase1.update(TBL_SALES_ORDER_DETAILS, salesOrderDetailsValues, "order_detail_id = ?", new String[]{String.valueOf(orderDetailID)});
                            sqLiteDatabase1.update(TBL_SALES_ORDER_SKU_ATTRIBUTES, salesOrderDetailsValues, "order_detail_id = ?", new String[]{String.valueOf(orderDetailID)});

                        }

                        Utils.showSuccessDialog(itemView.getContext(), "Success. Order Placed");

                        skuList = new ArrayList<>();

                        notifyDataSetChanged();
                        salesOrder_LinearLayout.setVisibility(View.GONE);

                        makeCurrentActiveOrderInactive();

                       // makePlacedOrderInactive(sqLiteDatabase1);


                    }
                    else
                    {
                        Utils.showErrorDialog(itemView.getContext(),"Order is not placed successfully.");

                    }

                }

                @Override
                public void onFailure(Call<PutSalesOrderDetails> call, Throwable t)
                {
                    Utils.showToast(itemView.getContext(), ConstantsA.NO_INTERNET_CONNECTION);
                }
            });


        }


        private void makePlacedOrderInactive(SQLiteDatabase sqLiteDatabase) {
            ContentValues salesOrderValues2 = new ContentValues();
            salesOrderValues2.put("is_active", "0");
            sqLiteDatabase.update(Constants.TBL_SALES_ORDER, salesOrderValues2, "is_active = ?", new String[]{"1"});
        }
    }

    public SalesOrderAdapter(List<SalesOrderSku> skuList, LinearLayout salesOrder_LinearLayout_outer, CheckBox setOrderAsRegularOrder_CheckBox) {
        this.skuList = skuList;
        this.salesOrder_LinearLayout_outer = salesOrder_LinearLayout_outer;
        this.salesOrder_LinearLayout = (LinearLayout) salesOrder_LinearLayout_outer.findViewById(R.id.salesOrder_LinearLayout);
        this.orderSummary_TextView = ((TextView) salesOrder_LinearLayout.findViewById(R.id.orderSummary_TextView));
        this.submitSalesOrder_Button=((Button) salesOrder_LinearLayout.findViewById(R.id.submitSalesOrder_Button));
        this.setOrderAsRegularOrder_CheckBox = setOrderAsRegularOrder_CheckBox;
    }

    public SkuInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.salesOrder_LinearLayout.setVisibility(View.VISIBLE);
        this.context = parent.getContext();
        return new SkuInfoViewHolder(LayoutInflater.from(this.context).inflate(R.layout.itemview_sales_order_sku, parent, false));
    }

    public void onBindViewHolder(SkuInfoViewHolder skuInfoViewHolder, int position) {
        long orderDetailID = ((SalesOrderSku) this.skuList.get(position)).getOrderDetailId();
        String listString = ConstantsA.NONE;
        for (SalesOrderSku s : this.skuList) {
            listString = "" + s.getOrderDetailId() + "\t" + s.getSkuID() + "\t";
        }
        Log.e("skuList", listString);
        Log.e("orderDetailID", String.valueOf(orderDetailID));
        SpannableString salesOrderSkuAttributes = getSalesOrderAttributesFor(String.valueOf(orderDetailID));
        String skuID = ((SalesOrderSku) this.skuList.get(position)).getSkuID();
        String skuName = ((SalesOrderSku) this.skuList.get(position)).getSkuName();
        String skuPrice = ((SalesOrderSku) this.skuList.get(position)).getSkuPrice();
        String skuQuantity = ((SalesOrderSku) this.skuList.get(position)).getSkuQty();
        String sku_photo_url = DbUtils.getSku_PhotoSource(skuID);
        skuInfoViewHolder.itemView.setTag(R.string.tag_order_detail_id, Long.valueOf(orderDetailID));
        skuInfoViewHolder.itemView.setTag(R.string.tag_sku_id, skuID);
        skuInfoViewHolder.itemView.setTag(R.string.tag_sku_price, skuPrice);
        skuInfoViewHolder.skuName_TextView.setText(skuName);
        skuInfoViewHolder.skuPrice_TextView.setText(ConstantsA.RS + skuPrice);
        skuInfoViewHolder.skuQuantity_TextInputEditText.setText(skuQuantity);
        Glide.with(skuInfoViewHolder.itemView.getContext()).load(sku_photo_url).error(R.drawable.ic_tiffin_box).bitmapTransform(new RoundedCornersTransformation(skuInfoViewHolder.itemView.getContext(), 15, 2)).into(skuInfoViewHolder.skuPhoto_ImageView);
        skuInfoViewHolder.sku_SO_Attr_TextView.setText(salesOrderSkuAttributes);
        Log.e("attributeSet", String.valueOf(salesOrderSkuAttributes));
    }

    public int getItemCount() {
        return this.skuList.size();
    }

    SpannableString getSalesOrderAttributesFor(String orderDetailID)
    {
        SpannableStringBuilder attributesOfSku = new SpannableStringBuilder();

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String SQL_SELECT_SALES_ORDER_ATTRIBUTES_SET = "SELECT attribute_id, attribute_value FROM sales_order_sku_attributes WHERE order_detail_id = ? ;";
        String[] selectionArgs = {String.valueOf(orderDetailID)};

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_SALES_ORDER_ATTRIBUTES_SET, selectionArgs);

        while (cursor.moveToNext())
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
}
