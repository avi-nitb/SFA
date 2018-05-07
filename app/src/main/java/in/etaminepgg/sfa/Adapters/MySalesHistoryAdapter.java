package in.etaminepgg.sfa.Adapters;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.etaminepgg.sfa.Models.MySalesHistory;
import in.etaminepgg.sfa.R;

import static in.etaminepgg.sfa.Utilities.DbUtils.getRetailerNameAccordingToRetailerId;

public class MySalesHistoryAdapter extends RecyclerView.Adapter<MySalesHistoryAdapter.MySalesHistoryViewHolder>
{
    private List<MySalesHistory> mySalesHistoryList;

    public MySalesHistoryAdapter(List<MySalesHistory> mySalesHistoryList)
    {
        this.mySalesHistoryList = mySalesHistoryList;
    }

    @Override
    public MySalesHistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView;

        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemview_my_sales_history, parent, false);

        return new MySalesHistoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MySalesHistoryViewHolder viewHolder, int position)
    {
        String orderId = mySalesHistoryList.get(position).getOrderId();
        String retailerId = mySalesHistoryList.get(position).getRetailerId();
        String orderDate = mySalesHistoryList.get(position).getOrderDate();
        String totalItems = mySalesHistoryList.get(position).getSkuCount();
        String orderTotal = mySalesHistoryList.get(position).getOrderTotal();

        viewHolder.itemView.setTag(R.string.tag_order_id, orderId);

        viewHolder.orderId_TextView.setText("Order No. : "+"#"+orderId);
        viewHolder.retailerId_TextView.setText("Retailer name : "+getRetailerNameAccordingToRetailerId(retailerId));
        viewHolder.orderDate_TextView.setText("Ordered Date : "+orderDate);
        viewHolder.skuCount_TextView.setText(totalItems + " items");
        viewHolder.orderTotal_TextView.setText("Total Price : Rs. " + orderTotal);

        Saleshistory_for_skuitemAdapter saleshistory_for_skuitemAdapter=new Saleshistory_for_skuitemAdapter((ArrayList<MySalesHistory.SkuDetails_Ordered>) mySalesHistoryList.get(position).getSku_Details_ordered());
        viewHolder.rv_child_for_skuitem.setAdapter(saleshistory_for_skuitemAdapter);
    }

    @Override
    public int getItemCount()
    {
        return mySalesHistoryList.size();
    }

    public class MySalesHistoryViewHolder extends RecyclerView.ViewHolder
    {
        TextView orderId_TextView, retailerId_TextView, orderDate_TextView, skuCount_TextView, orderTotal_TextView;
        RecyclerView rv_child_for_skuitem;

        public MySalesHistoryViewHolder(View itemView)
        {
            super(itemView);

            orderId_TextView = (TextView) itemView.findViewById(R.id.orderId_TextView);
            retailerId_TextView = (TextView) itemView.findViewById(R.id.retailerId_TextView);
            orderDate_TextView = (TextView) itemView.findViewById(R.id.orderDate_TextView);
            skuCount_TextView = (TextView) itemView.findViewById(R.id.skuCount_TextView);
            orderTotal_TextView = (TextView) itemView.findViewById(R.id.orderTotal_TextView);
            rv_child_for_skuitem=(RecyclerView)itemView.findViewById(R.id.rv_child_for_skuitem);
            rv_child_for_skuitem.setLayoutManager(new LinearLayoutManager(itemView.getContext()));

        }
    }
}
