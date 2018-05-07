package in.etaminepgg.sfa.Activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import in.etaminepgg.sfa.Adapters.SimilarSKUsAdapter;
import in.etaminepgg.sfa.InputModel_For_Network.IM_GetSkuInfo;
import in.etaminepgg.sfa.Models.GetSkuInfo;
import in.etaminepgg.sfa.Models.GetSkuListAfter;
import in.etaminepgg.sfa.Models.Sku;
import in.etaminepgg.sfa.Network.API_Call_Retrofit;
import in.etaminepgg.sfa.Network.Apimethods;
import in.etaminepgg.sfa.R;
import in.etaminepgg.sfa.Utilities.ConstantsA;
import in.etaminepgg.sfa.Utilities.DbUtils;
import in.etaminepgg.sfa.Utilities.MyDb;
import in.etaminepgg.sfa.Utilities.MySharedPrefrencesData;
import in.etaminepgg.sfa.Utilities.RoundedCornersTransformation;
import in.etaminepgg.sfa.Utilities.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static in.etaminepgg.sfa.Utilities.Constants.TBL_SKU;
import static in.etaminepgg.sfa.Utilities.Constants.dbFileFullPath;
import static in.etaminepgg.sfa.Utilities.ConstantsA.KEY_SKU_ID;
import static in.etaminepgg.sfa.Utilities.ConstantsA.RS;
import static in.etaminepgg.sfa.Utilities.ConstantsA.sCorner;
import static in.etaminepgg.sfa.Utilities.ConstantsA.sMargin;
import static in.etaminepgg.sfa.Utilities.DbUtils.getSku_PhotoSource;

public class SkuDetailsActivity extends AppCompatActivity
{
    ImageView skuPhoto_ImageView;
    TextView skuName_TextView, skuPrice_TextView, skuCategory_TextView, skuSubCategory_TextView, skuDescription_TextView;
    RecyclerView similarSKUs_RecyclerView;

    String skuID = null;
    String skuCategory = null;

    MySharedPrefrencesData mySharedPrefrencesData;
    LinearLayout container_lay;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sku_details);

        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sku Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mySharedPrefrencesData=new MySharedPrefrencesData();


        findViewsByIDs();

        Intent intent = getIntent();

        if(intent != null)
        {
            skuID = getIntent().getStringExtra(KEY_SKU_ID);
            if(skuID != null)
            {
                getDataAndBind(skuID);

              //  network_call_for_getSimilarSKUsList(mySharedPrefrencesData.getEmployee_AuthKey(SkuDetailsActivity.this),skuID);

                similarSKUs_RecyclerView.setAdapter(new SimilarSKUsAdapter(getSimilarSKUsList(),container_lay));
                similarSKUs_RecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
                similarSKUs_RecyclerView.setItemAnimator(new DefaultItemAnimator());
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
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
        container_lay=(LinearLayout)findViewById(R.id.skuDetails_ScrollView);
    }

    private  void getDataAndBind(String skuID)
    {
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);
        String SQL_SELECT_SKU_DATA = "select sku_id, sku_name, sku_price, sku_category, sku_sub_category,sku_category_description,sku_sub_category_description, description from " + TBL_SKU + " where sku_id=?";
        String[] selectionArgs = new String[]{skuID};
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_SKU_DATA, selectionArgs);

        if(cursor.moveToFirst())
        {
            //skuPhoto_ImageView = (ImageView) findViewById(R.id.skuPhoto_ImageView);
            skuCategory = cursor.getString(cursor.getColumnIndexOrThrow("sku_category"));
            skuName_TextView.setText(/*skuID + " : " +*/ cursor.getString(cursor.getColumnIndexOrThrow("sku_name")));
            skuPrice_TextView.setText(RS + cursor.getString(cursor.getColumnIndexOrThrow("sku_price")));


            skuCategory_TextView.setText(Html.fromHtml(new Utils().getModifiedkeyValueString("Category : ",cursor.getString(cursor.getColumnIndexOrThrow("sku_category_description")))));
            skuSubCategory_TextView.setText(Html.fromHtml(new Utils().getModifiedkeyValueString("Sub category : ",cursor.getString(cursor.getColumnIndexOrThrow("sku_sub_category_description")))));
            skuDescription_TextView.setText(Html.fromHtml(new Utils().getModifiedkeyValueString("Description : ",cursor.getString(cursor.getColumnIndexOrThrow("description")))));
           // skuCategory_TextView.setText("Category : "+cursor.getString(cursor.getColumnIndexOrThrow("sku_category_description")));
            //skuSubCategory_TextView.setText("Sub category : "+cursor.getString(cursor.getColumnIndexOrThrow("sku_sub_category_description")));
           // skuDescription_TextView.setText("Description : "+cursor.getString(cursor.getColumnIndexOrThrow("description")));
            Glide.with(SkuDetailsActivity.this).load(getSku_PhotoSource(skuID)).error(R.drawable.ic_tiffin_box).bitmapTransform(new RoundedCornersTransformation(this,sCorner,sMargin)).into(skuPhoto_ImageView);
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

    private void network_call_for_getSimilarSKUsList(String authtoken,String skuID){

        Apimethods methods= API_Call_Retrofit.getretrofit(SkuDetailsActivity.this).create(Apimethods.class);

        IM_GetSkuInfo im_getSkuInfo = new IM_GetSkuInfo(authtoken, skuID);

        Call<GetSkuListAfter> call = methods.getSimilarSkuList(im_getSkuInfo);
        Log.d("url", "url=" + call.request().url().toString());

        call.enqueue(new Callback<GetSkuListAfter>()
        {
            @Override
            public void onResponse(Call<GetSkuListAfter> call, Response<GetSkuListAfter> response)
            {

            }

            @Override
            public void onFailure(Call<GetSkuListAfter> call, Throwable t)
            {

            }
        });



    }

}
