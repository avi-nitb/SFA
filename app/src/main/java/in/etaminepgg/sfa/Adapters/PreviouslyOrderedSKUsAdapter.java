package in.etaminepgg.sfa.Adapters;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import in.etaminepgg.sfa.Activities.SkuDetailsActivity;
import in.etaminepgg.sfa.Activities.SkuListByGenreActivity;
import in.etaminepgg.sfa.Models.Sku;
import in.etaminepgg.sfa.R;
import in.etaminepgg.sfa.Utilities.MyDb;
import in.etaminepgg.sfa.Utilities.RoundedCornersTransformation;
import in.etaminepgg.sfa.Utilities.Utils;

import static in.etaminepgg.sfa.Utilities.Constants.TBL_SKU;
import static in.etaminepgg.sfa.Utilities.Constants.dbFileFullPath;
import static in.etaminepgg.sfa.Utilities.ConstantsA.KEY_SKU_ID;
import static in.etaminepgg.sfa.Utilities.ConstantsA.RS;
import static in.etaminepgg.sfa.Utilities.ConstantsA.sCorner;
import static in.etaminepgg.sfa.Utilities.ConstantsA.sMargin;
import static in.etaminepgg.sfa.Utilities.DbUtils.getSku_PhotoSource;

/**
 * Created by etamine on 6/6/17.
 */

public class PreviouslyOrderedSKUsAdapter extends RecyclerView.Adapter<PreviouslyOrderedSKUsAdapter.SkuInfoViewHolder>
{
    private List<Sku> skuList;

    int read = 1;

    public PreviouslyOrderedSKUsAdapter(List<Sku> skuList)
    {
        this.skuList = skuList;
    }

    @Override
    public SkuInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemview_sku, parent, false);
        return new SkuInfoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SkuInfoViewHolder skuInfoViewHolder, int position)
    {
        String skuID = skuList.get(position).getSkuId();
        String skuName = skuList.get(position).getSkuName();
        String skuPrice = skuList.get(position).getSkuPrice();
        String sku_photo_url =  getSku_PhotoSource(skuID);
        //String skuCategory = skuList.get(position).getSkuCategory();

        skuInfoViewHolder.itemView.setTag(R.string.tag_sku_id, skuID);
        skuInfoViewHolder.itemView.setTag(R.string.tag_sku_price, skuPrice);
        skuInfoViewHolder.itemView.setTag(R.string.tag_sku_name, skuName);

        skuInfoViewHolder.skuName_TextView.setText(skuName);
        skuInfoViewHolder.skuPrice_TextView.setText(RS + skuPrice);
        skuInfoViewHolder.skuCategory_TextView.setText("Category : "+getSKU_category(skuID));
        Glide.with(skuInfoViewHolder.itemView.getContext()).load(sku_photo_url).error(R.drawable.ic_tiffin_box).bitmapTransform(new RoundedCornersTransformation(skuInfoViewHolder.itemView.getContext(),sCorner,sMargin)).into(skuInfoViewHolder.skuPhoto_ImageView);
       // Glide.with(skuInfoViewHolder.itemView.getContext()).load(sku_photo_url).into(skuInfoViewHolder.skuPhoto_ImageView);
        //skuInfoViewHolder.sku_SO_Attr_TextView.setVisibility(View.GONE);
    }

    private String getSKU_category(String skuID)
    {
        String sku_category = null;

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String SKU_CATEGORY_QUERY = "select  sku_category_description from " + TBL_SKU + " WHERE sku_id=? ;";
        String[] selectionArgs = {skuID};

        Cursor cursor = sqLiteDatabase.rawQuery(SKU_CATEGORY_QUERY, selectionArgs);

        if(cursor.moveToFirst())
        {
            sku_category = cursor.getString(cursor.getColumnIndexOrThrow("sku_category_description"));
        }

        cursor.close();
        sqLiteDatabase.close();

        return sku_category;
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

        LinearLayout linlayVC;


        SkuInfoViewHolder(final View itemView)
        {
            super(itemView);
            skuPhoto_ImageView = (ImageView) itemView.findViewById(R.id.skuPhoto_ImageView);
            addSkuToCart_ImageView = (ImageView) itemView.findViewById(R.id.addSkuToCart_ImageView);
            skuName_TextView = (TextView) itemView.findViewById(R.id.skuName_TextView);
            skuPrice_TextView = (TextView) itemView.findViewById(R.id.skuPrice_TextView);
            skuCategory_TextView = (TextView) itemView.findViewById(R.id.sku_SO_Attr_TextView);

            linlayVC=(LinearLayout)itemView.findViewById(R.id.linlayVC);

            linlayVC.setVisibility(View.GONE);
            itemView.findViewById(R.id.viewline2).setVisibility(View.GONE);


            addSkuToCart_ImageView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    String skuID = itemView.getTag(R.string.tag_sku_id).toString();
                    String skuPrice = itemView.getTag(R.string.tag_sku_price).toString();
                    String skuName = itemView.getTag(R.string.tag_sku_name).toString();

                    new Utils().pickAttributeValuesOrSelectRetailer(skuID, skuName, skuPrice, itemView.getContext(), SkuListByGenreActivity.retailer_id_from_SOT,SkuListByGenreActivity.mobile_retailer_id_from_SOT,SkuListByGenreActivity.isNewRegular_from_SOT);
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
