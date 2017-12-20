package in.etaminepgg.sfa.Adapters;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import in.etaminepgg.sfa.Activities.SkuDetailsActivity;
import in.etaminepgg.sfa.Models.Sku;
import in.etaminepgg.sfa.R;
import in.etaminepgg.sfa.Utilities.DbUtils;
import in.etaminepgg.sfa.Utilities.MyDb;
import in.etaminepgg.sfa.Utilities.Utils;

import static in.etaminepgg.sfa.Utilities.Constants.TBL_SKU;
import static in.etaminepgg.sfa.Utilities.Constants.dbFileFullPath;
import static in.etaminepgg.sfa.Utilities.ConstantsA.KEY_SKU_ID;
import static in.etaminepgg.sfa.Utilities.ConstantsA.RS;

/**
 * Created by etamine on 8/6/17.
 */

public class SimilarSKUsAdapter extends RecyclerView.Adapter<SimilarSKUsAdapter.SkuInfoViewHolder>
{
    private List<Sku> skuList;

    public SimilarSKUsAdapter(List<Sku> skuList)
    {
        this.skuList = skuList;
    }

    @Override
    public SkuInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemview_similar_sku, parent, false);
        return new SkuInfoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SkuInfoViewHolder skuInfoViewHolder, int position)
    {
        String skuID = skuList.get(position).getSkuId();
        String skuName = skuList.get(position).getSkuName();
        String skuPrice = skuList.get(position).getSkuPrice();

        skuInfoViewHolder.itemView.setTag(R.string.tag_sku_id, skuID);
        skuInfoViewHolder.itemView.setTag(R.string.tag_sku_price, skuPrice);
        skuInfoViewHolder.itemView.setTag(R.string.tag_sku_name, skuName);

        skuInfoViewHolder.skuName_TextView.setText(skuName);
        skuInfoViewHolder.skuPrice_TextView.setText(RS + skuPrice);
    }

    @Override
    public int getItemCount()
    {
        return skuList.size();
    }

    class SkuInfoViewHolder extends RecyclerView.ViewHolder
    {
        ImageView skuPhoto_ImageView, addSkuToCart_ImageView;
        TextView skuName_TextView, skuPrice_TextView, skuCategory_TextView;

        SkuInfoViewHolder(final View itemView)
        {
            super(itemView);
            skuPhoto_ImageView = (ImageView) itemView.findViewById(R.id.skuPhoto_ImageView);
            addSkuToCart_ImageView = (ImageView) itemView.findViewById(R.id.addSkuToCart_ImageView);
            skuName_TextView = (TextView) itemView.findViewById(R.id.skuName_TextView);
            skuPrice_TextView = (TextView) itemView.findViewById(R.id.skuPrice_TextView);

            addSkuToCart_ImageView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    String skuID = itemView.getTag(R.string.tag_sku_id).toString();
                    String skuPrice = itemView.getTag(R.string.tag_sku_price).toString();
                    String skuName = itemView.getTag(R.string.tag_sku_name).toString();
                    //new DbUtils().addToSalesOrderOrPickRetailer(skuID, skuName, skuPrice, itemView.getContext());

                    new Utils().pickAttributeValuesOrSelectRetailer(skuID, skuName, skuPrice, itemView.getContext());
                }
            });

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    //Utils.showToast(view.getContext(), itemView.getTag(R.string.tag_sku_id).toString());

                    String skuID = itemView.getTag(R.string.tag_sku_id).toString();
                    Utils.launchActivityWithExtra(view.getContext(), SkuDetailsActivity.class, KEY_SKU_ID, skuID);
                }
            });
        }
    }
}
