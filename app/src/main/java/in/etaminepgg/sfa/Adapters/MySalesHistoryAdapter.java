package in.etaminepgg.sfa.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import in.etaminepgg.sfa.Models.MySalesHistory;
import in.etaminepgg.sfa.R;

import static in.etaminepgg.sfa.Utilities.ConstantsA.RS;

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

        viewHolder.orderId_TextView.setText(orderId);
        viewHolder.retailerId_TextView.setText(retailerId);
        viewHolder.orderDate_TextView.setText(orderDate);
        viewHolder.skuCount_TextView.setText(totalItems + " items");
        viewHolder.orderTotal_TextView.setText("Rs. " + orderTotal);
    }

    @Override
    public int getItemCount()
    {
        return mySalesHistoryList.size();
    }

    public class MySalesHistoryViewHolder extends RecyclerView.ViewHolder
    {
        TextView orderId_TextView, retailerId_TextView, orderDate_TextView, skuCount_TextView, orderTotal_TextView;

        public MySalesHistoryViewHolder(View itemView)
        {
            super(itemView);

            orderId_TextView = (TextView)itemView.findViewById(R.id.orderId_TextView);
            retailerId_TextView = (TextView)itemView.findViewById(R.id.retailerId_TextView);
            orderDate_TextView = (TextView)itemView.findViewById(R.id.orderDate_TextView);
            skuCount_TextView = (TextView)itemView.findViewById(R.id.skuCount_TextView);
            orderTotal_TextView = (TextView)itemView.findViewById(R.id.orderTotal_TextView);
        }
    }
}
