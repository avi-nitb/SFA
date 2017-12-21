package in.etaminepgg.sfa.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import in.etaminepgg.sfa.Models.Retailer;
import in.etaminepgg.sfa.R;

/**
 * Created by etamine on 14/9/17.
 */

public class RetailerListAdapter extends RecyclerView.Adapter<RetailerListAdapter.RetailerVH>
{
    private List<Retailer> retailerList;

    public RetailerListAdapter(List<Retailer> retailerList)
    {
        this.retailerList = retailerList;
    }

    @Override
    public RetailerVH onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemview_retailer, parent, false);
        return new RetailerVH(itemView);
    }

    @Override
    public void onBindViewHolder(RetailerVH viewHolder, int position)
    {
        Retailer retailer = retailerList.get(position);
        String retailerID = retailer.getRetailerID();
        String retailerName = retailer.getRetailerName();
        String shopName = retailer.getShopName();
        String mobileNumber = retailer.getMobileNumber();

        //viewHolder.itemView.setTag(R.string.tag_order_id, orderID);

        viewHolder.retailerId_TextView.setText("Retailer Id: " + retailerID);
        viewHolder.retailerName_TextView.setText("Retailer Name: " + retailerName);
        viewHolder.shopName_TextView.setText("Shop Name: " + shopName);
        viewHolder.mobileNumber_TextView.setText("Mobile Number: " + mobileNumber);
    }

    @Override
    public int getItemCount()
    {
        return retailerList.size();
    }

    class RetailerVH extends RecyclerView.ViewHolder
    {
        TextView retailerId_TextView, retailerName_TextView, shopName_TextView, mobileNumber_TextView;

        public RetailerVH(View itemView)
        {
            super(itemView);

            retailerId_TextView = (TextView) itemView.findViewById(R.id.retailerId_TextView);
            retailerName_TextView = (TextView) itemView.findViewById(R.id.retailerName_TextView);
            shopName_TextView = (TextView) itemView.findViewById(R.id.shopName_TextView);
            mobileNumber_TextView = (TextView) itemView.findViewById(R.id.mobileNumber_TextView);
        }
    }
}
