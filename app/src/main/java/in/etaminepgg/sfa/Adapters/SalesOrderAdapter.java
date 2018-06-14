package in.etaminepgg.sfa.Adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Editable;
import android.text.InputFilter;
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
import in.etaminepgg.sfa.Utilities.AllValidation;
import in.etaminepgg.sfa.Utilities.Constants;
import in.etaminepgg.sfa.Utilities.ConstantsA;
import in.etaminepgg.sfa.Utilities.DbUtils;
import in.etaminepgg.sfa.Utilities.MyDb;
import in.etaminepgg.sfa.Utilities.MySharedPrefrencesData;
import in.etaminepgg.sfa.Utilities.PrefixEditText;
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
import static in.etaminepgg.sfa.Utilities.ConstantsA.NO_INTERNET_CONNECTION;
import static in.etaminepgg.sfa.Utilities.DbUtils.makeCurrentActiveOrderInactive;

public class SalesOrderAdapter extends Adapter<SalesOrderAdapter.SkuInfoViewHolder>
{
    Context context;
    ProgressDialog progressDialog;
    private TextView orderSummary_TextView, tv_grandtoalitems, tv_grandtotal;
    private LinearLayout salesOrder_LinearLayout;
    private LinearLayout salesOrder_LinearLayout_outer;
    private LinearLayout linlay_price;
    private CheckBox setOrderAsRegularOrder_CheckBox;
    private List<SalesOrderSku> skuList;
    private Button submitSalesOrder_Button;
    private PrefixEditText order_overalldiscount_value;


    public SalesOrderAdapter(List<SalesOrderSku> skuList, LinearLayout salesOrder_LinearLayout_outer, CheckBox setOrderAsRegularOrder_CheckBox)
    {
        this.skuList = skuList;

        this.salesOrder_LinearLayout_outer = salesOrder_LinearLayout_outer;

        this.salesOrder_LinearLayout = (LinearLayout) salesOrder_LinearLayout_outer.findViewById(R.id.salesOrder_LinearLayout);

        this.linlay_price = (LinearLayout) salesOrder_LinearLayout.findViewById(R.id.linlay_price);

        this.orderSummary_TextView = ((TextView) salesOrder_LinearLayout.findViewById(R.id.orderSummary_TextView));
        this.tv_grandtoalitems = ((TextView) salesOrder_LinearLayout.findViewById(R.id.tv_grandtoalitems));
        this.tv_grandtotal = ((TextView) salesOrder_LinearLayout.findViewById(R.id.tv_grandtotal));
        this.order_overalldiscount_value = ((PrefixEditText) salesOrder_LinearLayout.findViewById(R.id.order_overalldiscount_value));
        this.submitSalesOrder_Button = ((Button) salesOrder_LinearLayout.findViewById(R.id.submitSalesOrder_Button));
        this.setOrderAsRegularOrder_CheckBox = setOrderAsRegularOrder_CheckBox;

    }

    public SkuInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        this.salesOrder_LinearLayout.setVisibility(View.VISIBLE);
        this.context = parent.getContext();
        this.progressDialog = new ProgressDialog(context);
        return new SkuInfoViewHolder(LayoutInflater.from(this.context).inflate(R.layout.itemview_sales_order_sku, parent, false));
    }

    public void onBindViewHolder(SkuInfoViewHolder skuInfoViewHolder, int position)
    {
        long orderDetailID = ((SalesOrderSku) this.skuList.get(position)).getOrderDetailId();
        String listString = ConstantsA.NONE;
        for (SalesOrderSku s : this.skuList)
        {
            listString = "" + s.getOrderDetailId() + "\t" + s.getSkuID() + "\t";
        }
        Log.e("skuList", listString);
        Log.e("orderDetailID", String.valueOf(orderDetailID));
        SpannableString salesOrderSkuAttributes = getSalesOrderAttributesFor(String.valueOf(orderDetailID));
        String skuID = ((SalesOrderSku) this.skuList.get(position)).getSkuID();
        String skuName = ((SalesOrderSku) this.skuList.get(position)).getSkuName();
        String skuPrice = ((SalesOrderSku) this.skuList.get(position)).getSkuPrice();
        String skuQuantity = ((SalesOrderSku) this.skuList.get(position)).getSkuQty();
        String sku_discount = ((SalesOrderSku) this.skuList.get(position)).getSku_discount();
        String sku_free_qty = ((SalesOrderSku) this.skuList.get(position)).getSku_free_qty();
        String sku_final_price_discount = ((SalesOrderSku) this.skuList.get(position)).getSku_final_price();
        String sku_photo_url = DbUtils.getSku_PhotoSource(skuID);
        skuInfoViewHolder.itemView.setTag(R.string.tag_order_detail_id, Long.valueOf(orderDetailID));
        skuInfoViewHolder.itemView.setTag(R.string.tag_sku_id, skuID);
        skuInfoViewHolder.itemView.setTag(R.string.tag_sku_price, skuPrice);
        skuInfoViewHolder.skuName_TextView.setText(skuName);
        skuInfoViewHolder.skuPrice_TextView.setText(ConstantsA.RS + skuPrice);
        skuInfoViewHolder.skuDiscount_TextInputEditText.setText(ConstantsA.RS + sku_discount);
        skuInfoViewHolder.freeskuQuantity_TextInputEditText.setText(sku_free_qty);
//        float sku_final_price = Float.parseFloat(skuPrice) * Float.parseFloat(skuQuantity);
        skuInfoViewHolder.sku_totalAfterDiscount_TextView.setText(ConstantsA.RS + sku_final_price_discount);
        skuInfoViewHolder.skuQuantity_TextInputEditText.setText(skuQuantity);
        Glide.with(skuInfoViewHolder.itemView.getContext()).load(sku_photo_url).error(R.drawable.ic_tiffin_box).bitmapTransform(new RoundedCornersTransformation(skuInfoViewHolder.itemView.getContext(), 15, 2)).into(skuInfoViewHolder.skuPhoto_ImageView);
        skuInfoViewHolder.sku_SO_Attr_TextView.setText(salesOrderSkuAttributes);
        Log.e("attributeSet", String.valueOf(salesOrderSkuAttributes));

    }

    public int getItemCount()
    {
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

    class SkuInfoViewHolder extends ViewHolder
    {
        ImageView deleteSKU_ImageButton;
        TextView skuName_TextView;
        ImageView skuPhoto_ImageView;
        TextView skuPrice_TextView;
        TextView skuQuantity_TextInputEditText, skuDiscount_TextInputEditText, freeskuQuantity_TextInputEditText;
        TextView sku_SO_Attr_TextView, sku_totalAfterDiscount_TextView;

        TextView tv_popupskuname, tv_skuunitprice_d, tv_skuamount_d;
        EditText et_skuqty_d, et_skufreeqty_d, et_skudiscount_d;
        Button btn_update_d, btn_cancel_d;


        //for dialog calculation
        String skuUnitPrice, skuQty, skuAmount_beforeDiscount, skuFreeQty, skuDiscount, skuAmount;

        SkuInfoViewHolder(final View itemView)
        {
            super(itemView);
            this.skuPhoto_ImageView = (ImageView) itemView.findViewById(R.id.skuPhoto_ImageView);
            this.skuName_TextView = (TextView) itemView.findViewById(R.id.skuName_TextView);
            this.skuPrice_TextView = (TextView) itemView.findViewById(R.id.skuPrice_TextView);
            this.sku_totalAfterDiscount_TextView = (TextView) itemView.findViewById(R.id.sku_total_after_discount);
            this.sku_SO_Attr_TextView = (TextView) itemView.findViewById(R.id.sku_SO_Attr_TextView);

            this.skuQuantity_TextInputEditText = (TextView) itemView.findViewById(R.id.skuQuantity_TextInputEditText);
            this.skuDiscount_TextInputEditText = (TextView) itemView.findViewById(R.id.skuDiscount_TextInputEditText);
            this.freeskuQuantity_TextInputEditText = (TextView) itemView.findViewById(R.id.freeskuQuantity_TextInputEditText);
            this.deleteSKU_ImageButton = (ImageView) itemView.findViewById(R.id.deleteSKU_ImageButton);


            SalesOrderAdapter.this.order_overalldiscount_value.addTextChangedListener(new TextWatcher()
            {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
                {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
                {
                    String overalldiscount = SalesOrderAdapter.this.order_overalldiscount_value.getText().toString();

                    String activeOrderID = DbUtils.getActiveOrderID();

                    //Set Length filter. Restricting to 10 characters only
                    order_overalldiscount_value.setFilters(new InputFilter[]{new InputFilter.LengthFilter(String.valueOf(DbUtils.getOrderTotal(activeOrderID)).length() + 1)});


                    if (!overalldiscount.isEmpty() && (DbUtils.getOrderTotal(activeOrderID) > Float.parseFloat(overalldiscount)))
                    {


                    }
                    else
                    {

                        Utils.showToast(itemView.getContext(), "Discount can n't be greater or equal to total.");
                    }

                }

                @Override
                public void afterTextChanged(Editable editable)
                {
                    /*SalesOrderAdapter.this.order_overalldiscount_value.getText().clear();
                    SalesOrderAdapter.this.order_overalldiscount_value.setText("-"+ConstantsA.RS + editable.toString());*/

                    // SalesOrderAdapter.this.order_overalldiscount_value.setText(editable.toString());
                    String overalldiscount = SalesOrderAdapter.this.order_overalldiscount_value.getText().toString();
                    String activeOrderID = DbUtils.getActiveOrderID();

                    if (!editable.toString().isEmpty() && (DbUtils.getOrderTotal(activeOrderID) > Float.parseFloat(overalldiscount)))
                    {

                        SalesOrderAdapter.this.tv_grandtotal.setText(ConstantsA.RS + " " + (DbUtils.getOrderTotal(activeOrderID) - Float.parseFloat(overalldiscount)));

                    }
                    else
                    {

                        SalesOrderAdapter.this.tv_grandtotal.setText(ConstantsA.RS + " 0.00");
                    }


                }
            });


            this.deleteSKU_ImageButton.setOnClickListener(new OnClickListener()
            {
                public void onClick(View view)
                {
                    int position = SkuInfoViewHolder.this.getLayoutPosition();
                    String orderDetailID = itemView.getTag(R.string.tag_order_detail_id).toString();
                    if (SkuInfoViewHolder.this.deleteSkuInSalesOrderDetailsTable(orderDetailID, itemView.getTag(R.string.tag_sku_id).toString()) == 1)
                    {
                        SkuInfoViewHolder.this.deleteEntriesInSalesOrderSkuAttributes(orderDetailID);

                        SalesOrderAdapter.this.notifyItemRangeChanged(position, SalesOrderAdapter.this.skuList.size());
                        String activeOrderID = DbUtils.getActiveOrderID();
                        //SalesOrderAdapter.this.orderSummary_TextView.setText(DbUtils.getItemCount(activeOrderID) + " items \nTotal:  Rs. " + DbUtils.getOrderTotal(activeOrderID)+"\nOrder for "+DbUtils.getActiveRetailer(activeOrderID));
                        SalesOrderAdapter.this.orderSummary_TextView.setText("Rs. " + DbUtils.getOrderTotal(activeOrderID));
                        SalesOrderAdapter.this.tv_grandtoalitems.setText("Grand Total ( " + DbUtils.getItemCount(activeOrderID) + " items )");
                      /*  if (SalesOrderAdapter.this.skuList.size() <= 0)
                        {
                            SalesOrderAdapter.this.salesOrder_LinearLayout.setVisibility(View.GONE);
                            SalesOrderAdapter.this.salesOrder_LinearLayout_outer.findViewById(R.id.emptyAdapter_TextView).setVisibility(View.VISIBLE);
                            return;
                        }
                        SalesOrderAdapter.this.salesOrder_LinearLayout.setVisibility(View.VISIBLE);
                        SalesOrderAdapter.this.salesOrder_LinearLayout_outer.findViewById(R.id.emptyAdapter_TextView).setVisibility(View.GONE);*/

                        if (SalesOrderAdapter.this.skuList.size() <= 0)
                        {
                            SalesOrderAdapter.this.linlay_price.setVisibility(View.GONE);
                            return;
                        }
                        SalesOrderAdapter.this.linlay_price.setVisibility(View.VISIBLE);


                        String overalldiscount = SalesOrderAdapter.this.order_overalldiscount_value.getText().toString();


                        SalesOrderAdapter.this.tv_grandtotal.setText(ConstantsA.RS + " " + (DbUtils.getOrderTotal(activeOrderID) - Float.parseFloat(overalldiscount)));
                        return;
                    }
                    Utils.showToast(LoginActivity.baseContext, "Error Removing SKU");
                }
            });
            SalesOrderAdapter.this.submitSalesOrder_Button.setOnClickListener(new OnClickListener()
            {
                public void onClick(View v)
                {
                    String retailerID = DbUtils.getRetailerID();

                    if (Utils.isNetworkConnected(context))
                    {
                        if (!SalesOrderAdapter.this.setOrderAsRegularOrder_CheckBox.isChecked())
                        {
                            Utils.startProgressDialog(context, progressDialog);
                            SkuInfoViewHolder.this.placeTheOrder();
                        }
                        else if (retailerID.equals(ConstantsA.NONE))
                        {
                            Utils.showErrorDialog(LoginActivity.baseContext, "Order Failed. No Active Order");
                        }
                        else
                        {
                            Utils.startProgressDialog(context, progressDialog);
                            SkuInfoViewHolder.this.eraseCurrentRegularOrderFor(retailerID);
                            SkuInfoViewHolder.this.setRegularOrderFor(retailerID);
                            SkuInfoViewHolder.this.placeTheOrder();
                        }

                    }
                    else
                    {

                        Utils.showErrorDialog(context, NO_INTERNET_CONNECTION);
                    }

                }
            });


            itemView.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    String skuID = itemView.getTag(R.string.tag_sku_id).toString();
                    String skuPrice = itemView.getTag(R.string.tag_sku_price).toString();
                    String skuQty = skuList.get(getLayoutPosition()).getSkuQty();

                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(itemView.getContext());

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    {
                        dialogBuilder.setView(R.layout.skueditentrypopup);
                    }

                   /* EditText editText = (EditText) dialogBuilder.findViewById(R.id.label_field);
                    editText.setText("test label");*/
                    AlertDialog alertDialog = dialogBuilder.create();

                    showDialog(alertDialog);


                }
            });
        }

        private void showDialog(final AlertDialog alertDialog)
        {
            alertDialog.show();

            int position = getLayoutPosition();

            tv_popupskuname = (TextView) alertDialog.findViewById(R.id.tv_popupskuname);
            tv_skuunitprice_d = (TextView) alertDialog.findViewById(R.id.tv_skuunitprice_d);
            tv_skuamount_d = (TextView) alertDialog.findViewById(R.id.tv_skuamount_d);

            et_skudiscount_d = (EditText) alertDialog.findViewById(R.id.et_skudiscount_d);
            et_skuqty_d = (EditText) alertDialog.findViewById(R.id.et_skuqty_d);
            et_skufreeqty_d = (EditText) alertDialog.findViewById(R.id.et_skufreeqty_d);


            btn_update_d = (Button) alertDialog.findViewById(R.id.btn_update_d);
            btn_cancel_d = (Button) alertDialog.findViewById(R.id.btn_cancel_d);


            tv_popupskuname.setText(skuList.get(position).getSkuName());
            tv_skuunitprice_d.setText(ConstantsA.RS + skuList.get(position).getSkuPrice());
            tv_skuamount_d.setText(ConstantsA.RS + skuList.get(position).getSku_final_price());

            et_skudiscount_d.setText(skuList.get(position).getSku_discount());
            et_skuqty_d.setText(skuList.get(position).getSkuQty());
            et_skufreeqty_d.setText(skuList.get(position).getSku_free_qty());

            final String skuID = itemView.getTag(R.string.tag_sku_id).toString();

            skuUnitPrice = skuList.get(position).getSkuPrice();
            skuAmount_beforeDiscount = skuList.get(position).getSku_price_before_discount();
            skuAmount = skuList.get(position).getSku_final_price();
            skuQty = skuList.get(position).getSkuQty();
            skuFreeQty = skuList.get(position).getSku_free_qty();
            skuDiscount = skuList.get(position).getSku_discount();


            et_skuqty_d.addTextChangedListener(new TextWatcher()
            {
                public void beforeTextChanged(CharSequence s, int start, int count, int after)
                {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count)
                {
                }

                public void afterTextChanged(Editable editable)
                {
                    skuQty = editable.toString().trim();

                    if (!skuQty.isEmpty() && !et_skudiscount_d.getText().toString().isEmpty())
                    {

                        skuAmount_beforeDiscount = String.valueOf(Float.parseFloat(skuUnitPrice) * Float.parseFloat(skuQty));

                        float sku_final_price = (Float.parseFloat(skuUnitPrice) * Float.parseFloat(skuQty)) - Float.parseFloat(skuDiscount);

                        skuAmount = String.valueOf(sku_final_price);

                        tv_skuamount_d.setText(ConstantsA.RS + sku_final_price);

                    }

                    /*String skuID = itemView.getTag(R.string.tag_sku_id).toString();
                    String skuPrice = itemView.getTag(R.string.tag_sku_price).toString();
                    String skuQty = editable.toString().trim();
                    if (!skuQty.isEmpty())
                    {
                        float sku_final_price = (Float.parseFloat(skuPrice) * Float.parseFloat(skuQty)) - Float.parseFloat(skuList.get(getLayoutPosition()).getSku_discount());
                        tv_skuamount_d.setText(ConstantsA.RS + sku_final_price);
                        SkuInfoViewHolder.this.updateSkuQtyInSalesOrder(skuID, skuPrice, skuQty, skuList.get(getLayoutPosition()).getSku_discount());
                        ((SalesOrderSku) SalesOrderAdapter.this.skuList.get(SkuInfoViewHolder.this.getLayoutPosition())).setSkuQty(skuQty);
                        ((SalesOrderSku) SalesOrderAdapter.this.skuList.get(SkuInfoViewHolder.this.getLayoutPosition())).setSku_price_before_discount(String.valueOf(Float.parseFloat(skuPrice) * Float.parseFloat(skuQty)));
                        ((SalesOrderSku) SalesOrderAdapter.this.skuList.get(SkuInfoViewHolder.this.getLayoutPosition())).setSku_final_price(String.valueOf(sku_final_price));
                        String activeOrderID = DbUtils.getActiveOrderID();
                        // SalesOrderAdapter.this.orderSummary_TextView.setText(DbUtils.getItemCount(activeOrderID) + " items \n\nTotal:  Rs. " + DbUtils.getOrderTotal(activeOrderID)+"\n\nOrder for "+DbUtils.getActiveRetailer(activeOrderID));

                        SalesOrderAdapter.this.orderSummary_TextView.setText("Rs. " + DbUtils.getOrderTotal(activeOrderID));

                        String overalldiscount = SalesOrderAdapter.this.order_overalldiscount_value.getText().toString();


                        SalesOrderAdapter.this.tv_grandtotal.setText(ConstantsA.RS + " " + (DbUtils.getOrderTotal(activeOrderID) - Float.parseFloat(overalldiscount)));
                    }*/
                }
            });


            et_skudiscount_d.addTextChangedListener(new TextWatcher()
            {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
                {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
                {

                    String entered_discount = et_skudiscount_d.getText().toString();

                    //Set Length filter. Restricting to 10 characters only
                    et_skudiscount_d.setFilters(new InputFilter[]{new InputFilter.LengthFilter(skuAmount.length())});


                    if (!entered_discount.isEmpty())
                    {


                        if (Float.parseFloat(entered_discount) >= Float.parseFloat(skuAmount_beforeDiscount))
                        {

                            et_skudiscount_d.setError("Discount can n't be greater or equal to sku total price.");
                        }
                        else
                        {
                            et_skudiscount_d.setError(null);
                        }
                    }

                    /*String entered_discount = et_skudiscount_d.getText().toString();

                    //Set Length filter. Restricting to 10 characters only
                    et_skudiscount_d.setFilters(new InputFilter[]{new InputFilter.LengthFilter(skuList.get(getLayoutPosition()).getSku_final_price().length() + 1)});


                    if (!entered_discount.isEmpty())
                    {


                        if (Float.parseFloat(entered_discount) >= Float.parseFloat(skuList.get(getLayoutPosition()).getSku_price_before_discount()))
                        {

                            et_skudiscount_d.setError("Discount can n't be greater or equal to sku total price.");
                        }
                        else
                        {
                            et_skudiscount_d.setError(null);
                        }
                    }*/

                }

                @Override
                public void afterTextChanged(Editable editable)
                {

                    skuDiscount = editable.toString();

                    if (!skuDiscount.isEmpty())
                    {

                        if (Float.parseFloat(skuDiscount) < Float.parseFloat(skuAmount_beforeDiscount))
                        {

                            float sku_final_price = Float.parseFloat(skuAmount_beforeDiscount) - Float.parseFloat(skuDiscount);

                            skuAmount = String.valueOf(sku_final_price);

                            tv_skuamount_d.setText(ConstantsA.RS + skuAmount);


                        }


                    }

                   /* String skuID = itemView.getTag(R.string.tag_sku_id).toString();
                    String skuPrice = itemView.getTag(R.string.tag_sku_price).toString();
                    String skuQty = skuList.get(getLayoutPosition()).getSkuQty();

                    String discount_amount = editable.toString();
                    if (!discount_amount.isEmpty())
                    {

                        if (Float.parseFloat(editable.toString()) < Float.parseFloat(skuList.get(getLayoutPosition()).getSku_price_before_discount()))
                        {


                            String skuprice_before_discount = skuList.get(getLayoutPosition()).getSku_price_before_discount();
                            float sku_final_price = Float.parseFloat(skuprice_before_discount) - Float.parseFloat(discount_amount);
                            ((SalesOrderSku) SalesOrderAdapter.this.skuList.get(SkuInfoViewHolder.this.getLayoutPosition())).setSku_final_price(String.valueOf(sku_final_price));
                            ((SalesOrderSku) SalesOrderAdapter.this.skuList.get(SkuInfoViewHolder.this.getLayoutPosition())).setSku_discount(String.valueOf(discount_amount));
                            tv_skuamount_d.setText(ConstantsA.RS + sku_final_price);

                            SkuInfoViewHolder.this.updateSkuQtyInSalesOrder(skuID, skuPrice, skuQty, discount_amount);

                            String activeOrderID = DbUtils.getActiveOrderID();
                            // SalesOrderAdapter.this.orderSummary_TextView.setText(DbUtils.getItemCount(activeOrderID) + " items \n\nTotal:  Rs. " + DbUtils.getOrderTotal(activeOrderID)+"\n\nOrder for "+DbUtils.getActiveRetailer(activeOrderID));

                            SalesOrderAdapter.this.orderSummary_TextView.setText("Rs. " + DbUtils.getOrderTotal(activeOrderID));

                            String overalldiscount = SalesOrderAdapter.this.order_overalldiscount_value.getText().toString();


                            SalesOrderAdapter.this.tv_grandtotal.setText(ConstantsA.RS + " " + (DbUtils.getOrderTotal(activeOrderID) - Float.parseFloat(overalldiscount)));
                        }


                    }
*/

                }
            });

            et_skufreeqty_d.addTextChangedListener(new TextWatcher()
            {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
                {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
                {

                }

                @Override
                public void afterTextChanged(Editable editable)
                {

                    skuFreeQty = editable.toString().trim();
                    /*String skuID = itemView.getTag(R.string.tag_sku_id).toString();

                    String skufreeQty = editable.toString().trim();
                    if (!skufreeQty.isEmpty())
                    {

                        SkuInfoViewHolder.this.updateSkuFreeQtyInSalesOrder(skuID, skufreeQty);
                        ((SalesOrderSku) SalesOrderAdapter.this.skuList.get(SkuInfoViewHolder.this.getLayoutPosition())).setSku_free_qty(skufreeQty);


                    }*/


                }
            });


            btn_cancel_d.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    alertDialog.dismiss();
                }
            });

            btn_update_d.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View view)
                {

                    //notifyDataSetChanged();

                    Log.w("skuUnitPrice", skuUnitPrice);
                    Log.w("skuQty", skuQty);
                    Log.w("skuFreeQty", skuFreeQty);
                    Log.w("skuDiscount", skuDiscount);
                    Log.w("skuAmountBeforeDiscount", skuAmount_beforeDiscount);
                    Log.w("skuAmount", skuAmount);

                    if (AllValidation.salesOrderUpdateValidation(skuUnitPrice, skuQty, skuFreeQty, skuDiscount, skuAmount_beforeDiscount, skuAmount, alertDialog.getContext()))
                    {

                        updateSkuQtyInSalesOrder(skuID, skuUnitPrice, skuQty, skuDiscount);
                        updateSkuFreeQtyInSalesOrder(skuID, skuFreeQty);

                        skuList.get(getLayoutPosition()).setSkuQty(skuQty);
                        skuList.get(getLayoutPosition()).setSku_discount(skuDiscount);
                        skuList.get(getLayoutPosition()).setSku_free_qty(skuFreeQty);
                        skuList.get(getLayoutPosition()).setSku_price_before_discount(skuAmount_beforeDiscount);
                        skuList.get(getLayoutPosition()).setSku_final_price(skuAmount);

                        String activeOrderID = DbUtils.getActiveOrderID();

                        float totalvalue = DbUtils.getOrderTotal(activeOrderID);


                        SalesOrderAdapter.this.orderSummary_TextView.setText("Rs. " + totalvalue);

                        String overalldiscount = SalesOrderAdapter.this.order_overalldiscount_value.getText().toString();


                        SalesOrderAdapter.this.tv_grandtotal.setText(ConstantsA.RS + " " + (totalvalue - Float.parseFloat(overalldiscount)));

                        notifyDataSetChanged();

                        alertDialog.dismiss();
                    }


                }
            });


        }


        private void updateSkuQtyInSalesOrder(String skuID, String skuPrice, String skuQty, String sku_discount)
        {
            int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
            SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

            String activeOrderID = DbUtils.getActiveOrderID();
            String selection = "order_id = ? AND sku_id = ?";
            String[] selectionArgs = new String[]{activeOrderID, skuID};

            ContentValues salesOrderDetailsValues = new ContentValues();
            salesOrderDetailsValues.put("sku_qty", skuQty);
            sqLiteDatabase.update(TBL_SALES_ORDER_DETAILS, salesOrderDetailsValues, selection, selectionArgs);

            updateSkuFinalPriceInSalesOrder(sqLiteDatabase, activeOrderID, skuID, skuPrice, skuQty, sku_discount);
        }

        private void updateSkuFreeQtyInSalesOrder(String skuID, String skufreeQty)
        {
            int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
            SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

            String activeOrderID = DbUtils.getActiveOrderID();
            String selection = "order_id = ? AND sku_id = ?";
            String[] selectionArgs = new String[]{activeOrderID, skuID};

            ContentValues salesOrderDetailsValues = new ContentValues();
            salesOrderDetailsValues.put("sku_free_qty", skufreeQty);
            sqLiteDatabase.update(TBL_SALES_ORDER_DETAILS, salesOrderDetailsValues, selection, selectionArgs);


        }


        private void updateSkuFinalPriceInSalesOrder(SQLiteDatabase sqLiteDatabase, String orderID, String skuID, String skuPrice, String skuQty, String sku_discount)
        {
            String skubeforediscountPrice = String.valueOf(Float.parseFloat(skuPrice) * Integer.parseInt(skuQty));
            Float skufinalPrice = Float.parseFloat(skubeforediscountPrice) - Float.parseFloat(sku_discount);
            String[] selectionArgs = new String[]{orderID, skuID};
            ContentValues salesOrderDetailsValues = new ContentValues();
            salesOrderDetailsValues.put("sku_price_before_discount", skubeforediscountPrice);
            salesOrderDetailsValues.put("sku_final_price", "" + skufinalPrice);
            salesOrderDetailsValues.put("sku_discount", "" + sku_discount);
            sqLiteDatabase.update(TBL_SALES_ORDER_DETAILS, salesOrderDetailsValues, "order_id = ? AND sku_id = ?", selectionArgs);
        }

        private void deleteEntriesInSalesOrderSkuAttributes(String orderDetailID)
        {
            String[] selectionArgs = new String[]{orderDetailID};
            MyDb.getDbHandle(MyDb.openDatabase(dbFileFullPath)).delete(TBL_SALES_ORDER_SKU_ATTRIBUTES, "order_detail_id = ?", selectionArgs);
        }

        private int deleteSkuInSalesOrderDetailsTable(String orderDetailID, String skuID)
        {
            String[] selectionArgs = new String[]{orderDetailID, skuID};
            return MyDb.getDbHandle(MyDb.openDatabase(dbFileFullPath)).delete(TBL_SALES_ORDER_DETAILS, "order_detail_id = ? AND sku_id = ?", selectionArgs);
        }

        private void eraseCurrentRegularOrderFor(String retailerID)
        {
            SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(MyDb.openDatabase(dbFileFullPath));
            ContentValues salesOrderValues = new ContentValues();
            salesOrderValues.put("is_regular", "0");
            sqLiteDatabase.update(Constants.TBL_SALES_ORDER, salesOrderValues, "retailer_id = ? AND is_regular = ? AND emp_id = ?", new String[]{retailerID, "1", new MySharedPrefrencesData().getUser_Id(context)});
        }

        private void setRegularOrderFor(String retailerID)
        {
            SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(MyDb.openDatabase(dbFileFullPath));
            ContentValues salesOrderValues = new ContentValues();
            salesOrderValues.put("is_regular", "1");
            sqLiteDatabase.update(Constants.TBL_SALES_ORDER, salesOrderValues, "retailer_id = ? AND is_active = ? AND emp_id = ?", new String[]{retailerID, "1", new MySharedPrefrencesData().getUser_Id(context)});
        }

        private void placeTheOrder()
        {
            SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(MyDb.openDatabase(dbFileFullPath));
            ContentValues salesOrderValues = new ContentValues();
            salesOrderValues.put("is_placed", "1");
           /* salesOrderValues.put("total_order_value", "1");
            salesOrderValues.put("total_discount", "1");*/

            String active_orderId = getActiveOrderIdIfHasSalesOrderDetailId();

            int noOfRowsUpdated = sqLiteDatabase.update(Constants.TBL_SALES_ORDER, salesOrderValues, "is_active = ? AND emp_id =? ", new String[]{"1", new MySharedPrefrencesData().getUser_Id(context)});

            if (noOfRowsUpdated == 1)
            {

                String overalldiscount = SalesOrderAdapter.this.order_overalldiscount_value.getText().toString();


                String activeOrderID = DbUtils.getActiveOrderID();

                float totalvalue = DbUtils.getOrderTotal(activeOrderID);

                if (!overalldiscount.isEmpty())
                {


                    if (totalvalue > Float.parseFloat(overalldiscount))
                    {

                        float grand_total = (DbUtils.getOrderTotal(activeOrderID) - Float.parseFloat(overalldiscount));
                        network_call_for_PutSalesOrderdetails(activeOrderID, String.valueOf(totalvalue), overalldiscount, String.valueOf(grand_total));
                    }
                    else
                    {

                        Utils.dismissProgressDialog(progressDialog);
                        Utils.showToast(itemView.getContext(), "Overall discount can n't be greater or equal to total.");
                    }
                }
                else
                {

                    Utils.dismissProgressDialog(progressDialog);
                    Utils.showToast(itemView.getContext(), "Please enter overall discount.");
                }


            }
            else if (noOfRowsUpdated > 1)
            {
                Utils.dismissProgressDialog(progressDialog);
                Utils.showToast(this.itemView.getContext(), "ERROR! 2 or more rows updated");
            }
            else
            {

                Utils.dismissProgressDialog(progressDialog);
                Utils.showToast(this.itemView.getContext(), "ERROR! Order Failed");
            }
        }

        private String getActiveOrderIdIfHasSalesOrderDetailId()
        {

            String activeOrderID = NONE;
            int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
            SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

            String SQL_SELECT_ACTIVE_SALES_ORDER_ID = "select so.order_id from " + TBL_SALES_ORDER + " so " + " INNER JOIN " + TBL_SALES_ORDER_DETAILS + " sod " + " ON so.order_id = sod.order_id " + " WHERE " + "so.is_active = ? AND so.emp_id = ?";
            String[] selectionArgs = new String[]{"1", new MySharedPrefrencesData().getUser_Id(LoginActivity.baseContext)};

            Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_ACTIVE_SALES_ORDER_ID, selectionArgs);

            ContentValues salesOrderValues = new ContentValues();
            salesOrderValues.put("is_active", "0");

            if (cursor.moveToFirst())
            {
                activeOrderID = cursor.getString(cursor.getColumnIndexOrThrow("order_id"));

                sqLiteDatabase.update(TBL_SALES_ORDER, salesOrderValues, "is_active = ? AND emp_id = ? AND order_id != ?", new String[]{"1", new MySharedPrefrencesData().getUser_Id(LoginActivity.baseContext), activeOrderID});
            }

            cursor.close();
            sqLiteDatabase.close();

            return activeOrderID;
        }

        private void network_call_for_PutSalesOrderdetails(final String activeOrderID, String total_value, final String overalldiscount, final String grand_total)
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

                IM_PutSalesorderDetails.SalesDatum salesDatum = new IM_PutSalesorderDetails().new SalesDatum(skuList.get(i).getSkuID(), skuList.get(i).getSkuPrice(), skuList.get(i).getSku_final_price(), skuList.get(i).getSkuQty(), "", attr_list, skuList.get(i).getSku_free_qty(), skuList.get(i).getSku_discount());
                salesData_list.add(salesDatum);

            }


            IM_PutSalesorderDetails im_putSalesorderDetails = new IM_PutSalesorderDetails(new MySharedPrefrencesData().getEmployee_AuthKey(context), activeOrderID, total_value, overalldiscount, "1", salesData_list, grand_total);

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

                        for (int i = 0; i < putSalesOrderDetails.getSalesOrderDetailsId().size(); i++)
                        {

                            String orderDetailID = orderdetailidlist.get(i);

                            ContentValues salesOrderDetailsValues = new ContentValues();

                            ContentValues salesOrderValues = new ContentValues();

                            salesOrderValues.put("total_order_value", grand_total);
                            salesOrderValues.put("total_discount", overalldiscount);
                            sqLiteDatabase1.update(TBL_SALES_ORDER, salesOrderValues, "order_id = ?", new String[]{activeOrderID});


                            salesOrderDetailsValues.put("server_order_detail_id", putSalesOrderDetails.getSalesOrderDetailsId().get(i));

                            sqLiteDatabase1.update(TBL_SALES_ORDER_DETAILS, salesOrderDetailsValues, "order_detail_id = ?", new String[]{String.valueOf(orderDetailID)});
                            sqLiteDatabase1.update(TBL_SALES_ORDER_SKU_ATTRIBUTES, salesOrderDetailsValues, "order_detail_id = ?", new String[]{String.valueOf(orderDetailID)});

                        }

                        Utils.dismissProgressDialog(progressDialog);

                        Utils.showSuccessDialog(itemView.getContext(), "Success. Order Placed");

                        skuList = new ArrayList<>();

                        notifyDataSetChanged();
                        salesOrder_LinearLayout.setVisibility(View.GONE);

                        makeCurrentActiveOrderInactive();

                        // makePlacedOrderInactive(sqLiteDatabase1);


                    }
                    else
                    {
                        Utils.dismissProgressDialog(progressDialog);
                        Utils.showErrorDialog(itemView.getContext(), "Order is not placed successfully.");

                    }

                }

                @Override
                public void onFailure(Call<PutSalesOrderDetails> call, Throwable t)
                {
                    Utils.dismissProgressDialog(progressDialog);
                    Utils.showToast(itemView.getContext(), ConstantsA.NO_INTERNET_CONNECTION);
                }
            });


        }


        private void makePlacedOrderInactive(SQLiteDatabase sqLiteDatabase)
        {
            ContentValues salesOrderValues2 = new ContentValues();
            salesOrderValues2.put("is_active", "0");
            sqLiteDatabase.update(Constants.TBL_SALES_ORDER, salesOrderValues2, "is_active = ?", new String[]{"1"});
        }
    }
}
