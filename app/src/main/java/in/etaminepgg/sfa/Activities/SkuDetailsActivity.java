package in.etaminepgg.sfa.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import in.etaminepgg.sfa.Adapters.SimilarSKUsAdapter;
import in.etaminepgg.sfa.Models.Sku;
import in.etaminepgg.sfa.R;
import in.etaminepgg.sfa.Utilities.DbUtils;
import in.etaminepgg.sfa.Utilities.MyDb;

import static in.etaminepgg.sfa.Utilities.Constants.TBL_SKU;
import static in.etaminepgg.sfa.Utilities.Constants.dbFileFullPath;
import static in.etaminepgg.sfa.Utilities.ConstantsA.KEY_SKU_ID;
import static in.etaminepgg.sfa.Utilities.ConstantsA.RS;

public class SkuDetailsActivity extends AppCompatActivity
{
    ImageView skuPhoto_ImageView;
    TextView skuName_TextView, skuPrice_TextView, skuCategory_TextView, skuSubCategory_TextView, skuDescription_TextView;
    RecyclerView similarSKUs_RecyclerView;

    String skuID = null;
    String skuCategory = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sku_details);

        findViewsByIDs();

        Intent intent = getIntent();

        if (intent != null)
        {
            skuID = getIntent().getStringExtra(KEY_SKU_ID);
            if (skuID != null)
            {
                getDataAndBind(skuID);

                similarSKUs_RecyclerView.setAdapter(new SimilarSKUsAdapter(getSimilarSKUsList()));
                similarSKUs_RecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
                similarSKUs_RecyclerView.setItemAnimator(new DefaultItemAnimator());
            }
        }
    }

    private void findViewsByIDs()
    {
        skuPhoto_ImageView = (ImageView) findViewById(R.id.skuPhoto_ImageView);
        skuName_TextView = (TextView) findViewById(R.id.skuName_TextView);
        skuPrice_TextView = (TextView) findViewById(R.id.skuPrice_TextView);
        skuCategory_TextView = (TextView) findViewById(R.id.sku_SO_Attr_TextView);
        skuSubCategory_TextView = (TextView) findViewById(R.id.skuSubCategory_TextView);
        skuDescription_TextView = (TextView) findViewById(R.id.skuDescription_TextView);
        similarSKUs_RecyclerView = (RecyclerView) findViewById(R.id.similarSKUs_RecyclerView);
    }

    private void getDataAndBind(String skuID)
    {
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);
        String SQL_SELECT_SKU_DATA = "select sku_id, sku_name, sku_price, sku_category, sku_sub_category, description from " + TBL_SKU + " where sku_id=?";
        String[] selectionArgs = new String[]{skuID};
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_SKU_DATA, selectionArgs);

        if (cursor.moveToFirst())
        {
            //skuPhoto_ImageView = (ImageView) findViewById(R.id.skuPhoto_ImageView);
            skuCategory = cursor.getString(cursor.getColumnIndexOrThrow("sku_category"));

            skuName_TextView.setText(skuID + " : " + cursor.getString(cursor.getColumnIndexOrThrow("sku_name")));
            skuPrice_TextView.setText(RS + cursor.getString(cursor.getColumnIndexOrThrow("sku_price")));
            skuCategory_TextView.setText(skuCategory);
            skuSubCategory_TextView.setText(cursor.getString(cursor.getColumnIndexOrThrow("sku_sub_category")));
            skuDescription_TextView.setText(cursor.getString(cursor.getColumnIndexOrThrow("description")));
        }

        cursor.close();
        sqLiteDatabase.close();
    }

    private List<Sku> getSimilarSKUsList()
    {
        String SQL_SELECT_SIMILAR_SKUs = "select sku_id, sku_name, sku_price from " + TBL_SKU + " where sku_category=?";
        String[] selectionArgs = new String[]{skuCategory};
        return DbUtils.getSkuList2(SQL_SELECT_SIMILAR_SKUs, selectionArgs);
    }
}
