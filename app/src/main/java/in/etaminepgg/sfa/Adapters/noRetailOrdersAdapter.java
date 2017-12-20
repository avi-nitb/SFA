package in.etaminepgg.sfa.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import in.etaminepgg.sfa.R;

/**
 * Created by etamine on 8/6/17.
 */

public class noRetailOrdersAdapter extends RecyclerView.Adapter
{
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemview_no_retail_order, parent, false);
        return new SkuInfoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
    }

    @Override
    public int getItemCount()
    {
        return 4;
    }

    private class SkuInfoViewHolder extends RecyclerView.ViewHolder
    {
        TextView retailerName_TextView, retailerShopName_TextView, whyNoOrder_TextView;

        private SkuInfoViewHolder(View itemView)
        {
            super(itemView);
            retailerName_TextView = (TextView) itemView.findViewById(R.id.retailerName_TextView);
            retailerShopName_TextView = (TextView) itemView.findViewById(R.id.retailerShopName_TextView);
            whyNoOrder_TextView = (TextView) itemView.findViewById(R.id.whyNoOrder_TextView);
        }
    }
}
