package in.etaminepgg.sfa.Adapters;

import android.app.Activity;
import android.content.Context;
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
 * Created by etamine on 8/6/17.
 */

public class SimilarSKUsAdapter extends RecyclerView.Adapter<SimilarSKUsAdapter.SkuInfoViewHolder>
{
    private List<Sku> skuList;
    private LinearLayout container_lay;

    public SimilarSKUsAdapter(List<Sku> skuList, LinearLayout container_lay)
    {
        this.skuList = skuList;
        this.container_lay = container_lay;
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
        String sku_photo_url =  getSku_PhotoSource(skuID);

        skuInfoViewHolder.itemView.setTag(R.string.tag_sku_id, skuID);
        skuInfoViewHolder.itemView.setTag(R.string.tag_sku_price, skuPrice);
        skuInfoViewHolder.itemView.setTag(R.string.tag_sku_name, skuName);

        skuInfoViewHolder.skuName_TextView.setText(skuName);
        skuInfoViewHolder.skuPrice_TextView.setText(RS + skuPrice);
        Glide.with(skuInfoViewHolder.itemView.getContext()).load(sku_photo_url).error(R.drawable.ic_tiffin_box).bitmapTransform(new RoundedCornersTransformation(skuInfoViewHolder.itemView.getContext(),sCorner,sMargin)).into(skuInfoViewHolder.skuPhoto_ImageView);
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
                    Context context=itemView.getContext();
                    getDataAndBind(context,container_lay,skuID);
                  //  Utils.launchActivityWithExtra(view.getContext(), SkuDetailsActivity.class, KEY_SKU_ID, skuID);
                }

                private void getDataAndBind(Context context, LinearLayout container_lay, String skuID)
                {

                    int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
                    SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);
                    String SQL_SELECT_SKU_DATA = "select sku_id, sku_name, sku_price, sku_category, sku_sub_category,sku_category_description,sku_sub_category_description ,description from " + TBL_SKU + " where sku_id=?";
                    String[] selectionArgs = new String[]{skuID};
                    Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_SKU_DATA, selectionArgs);

                    if(cursor.moveToFirst())
                    {
                        //skuPhoto_ImageView = (ImageView) findViewById(R.id.skuPhoto_ImageView);

                        ImageView skuPhoto_ImageView_actlay = (ImageView)container_lay.findViewById(R.id.skuPhoto_ImageView);
                        TextView skuName_TextView_actlay  = (TextView)container_lay. findViewById(R.id.skuName_TextView);
                        TextView skuPrice_TextView_actlay  = (TextView) container_lay.findViewById(R.id.skuPrice_TextView);
                        TextView skuCategory_TextView_actlay  = (TextView) container_lay.findViewById(R.id.sku_SO_Attr_TextView);
                        TextView skuSubCategory_TextView_actlay = (TextView)container_lay. findViewById(R.id.skuSubCategory_TextView);
                        TextView skuDescription_TextView_actlay  = (TextView) container_lay.findViewById(R.id.skuDescription_TextView);


                        String skuCategory = cursor.getString(cursor.getColumnIndexOrThrow("sku_category"));

                        skuName_TextView_actlay.setText(/*skuID + " : " +*/ cursor.getString(cursor.getColumnIndexOrThrow("sku_name")));
                        skuPrice_TextView_actlay.setText(RS + cursor.getString(cursor.getColumnIndexOrThrow("sku_price")));
                        skuCategory_TextView_actlay.setText("Category : "+cursor.getString(cursor.getColumnIndexOrThrow("sku_category_description")));
                        skuSubCategory_TextView_actlay.setText("Sub category : "+cursor.getString(cursor.getColumnIndexOrThrow("sku_sub_category_description")));
                        skuDescription_TextView_actlay.setText("Description : "+cursor.getString(cursor.getColumnIndexOrThrow("description")));
                        Glide.with(context).load(getSku_PhotoSource(skuID)).error(R.drawable.ic_tiffin_box).bitmapTransform(new RoundedCornersTransformation(context,sCorner,sMargin)).into(skuPhoto_ImageView_actlay);
                    }

                    cursor.close();
                    sqLiteDatabase.close();
                }
            });
        }
    }


}
