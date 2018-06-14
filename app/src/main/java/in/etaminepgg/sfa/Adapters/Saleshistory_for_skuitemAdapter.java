package in.etaminepgg.sfa.Adapters;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import in.etaminepgg.sfa.Models.MySalesHistory;
import in.etaminepgg.sfa.R;
import in.etaminepgg.sfa.Utilities.Constants;

/**
 * Created by etamine on 15/1/18.
 */

public class Saleshistory_for_skuitemAdapter extends RecyclerView.Adapter<Saleshistory_for_skuitemAdapter.MySalesHistoryItemViewHolder>
{


    ArrayList<MySalesHistory.SkuDetails_Ordered> skuDetails_ordereds;
    View itemView;

    public Saleshistory_for_skuitemAdapter(ArrayList<MySalesHistory.SkuDetails_Ordered> skuDetails_ordereds)
    {
        this.skuDetails_ordereds = skuDetails_ordereds;
    }

    @Override
    public MySalesHistoryItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {


        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemrow_saleshistory_skuitem, parent, false);

        return new Saleshistory_for_skuitemAdapter.MySalesHistoryItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MySalesHistoryItemViewHolder holder, int position)
    {
        if(!Constants.selected_sku_from_history.equalsIgnoreCase("ANY")){

            if(skuDetails_ordereds.get(position).getSku_id().equalsIgnoreCase(Constants.selected_sku_from_history)){

                String replacedWith = "<font color='maroon'>" + skuDetails_ordereds.get(position).getSku_name() + "</font>";
                String modifiedString = skuDetails_ordereds.get(position).getSku_name().replaceAll(skuDetails_ordereds.get(position).getSku_name(),replacedWith);
                holder.sku_name.setText(Html.fromHtml(modifiedString));
            }else {
                holder.sku_name.setText(skuDetails_ordereds.get(position).getSku_name());
            }

        }else {

            holder.sku_name.setText(skuDetails_ordereds.get(position).getSku_name());
        }
        holder.sku_name.setCompoundDrawablesWithIntrinsicBounds(itemView.getContext().getResources().getDrawable(R.drawable.dot),null,null,null);
        holder.sku_qty.setText("SKU Qty : "+skuDetails_ordereds.get(position).getSku_qty() +" Items");
        holder.sku_order_item_freeQty.setText("SKU Free Qty : "+skuDetails_ordereds.get(position).getSku_free_qty());
        holder.sku_final_price.setText("SKU Price : Rs. "+skuDetails_ordereds.get(position).getSku_finalprice());
        holder.sku_order_item_discount.setText("SKU Discount : Rs. "+skuDetails_ordereds.get(position).getSku_discount());

    }

    @Override
    public int getItemCount()
    {
        return skuDetails_ordereds.size();
    }

    public class MySalesHistoryItemViewHolder extends RecyclerView.ViewHolder
    {
        TextView sku_name,sku_qty,sku_final_price,sku_order_item_freeQty,sku_order_item_discount;

        public MySalesHistoryItemViewHolder(View itemView)
        {
            super(itemView);
            sku_name=(TextView)itemView.findViewById(R.id.sku_order_item_name);
            sku_qty=(TextView)itemView.findViewById(R.id.sku_order_item_qty);
            sku_order_item_freeQty=(TextView)itemView.findViewById(R.id.sku_order_item_freeQty);
            sku_final_price=(TextView)itemView.findViewById(R.id.sku_order_item_price);
            sku_order_item_discount=(TextView)itemView.findViewById(R.id.sku_order_item_discount);
        }
    }
}
