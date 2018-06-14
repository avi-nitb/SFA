package in.etaminepgg.sfa.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Selection;
import android.text.SpanWatcher;
import android.text.Spannable;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import in.etaminepgg.sfa.Activities.DashboardActivity;
import in.etaminepgg.sfa.Activities.SkuListByGenreActivity;
import in.etaminepgg.sfa.Adapters.NewSKUsAdapter;
import in.etaminepgg.sfa.InputModel_For_Network.IM_GetSkuInfo;
import in.etaminepgg.sfa.InputModel_For_Network.IM_GetSkuListAfter;
import in.etaminepgg.sfa.Models.GetSkuAttribute;
import in.etaminepgg.sfa.Models.GetSkuAttribute.SkuAttr;
import in.etaminepgg.sfa.Models.GetSkuInfo;
import in.etaminepgg.sfa.Models.GetSkuInfo.SkuIds;
import in.etaminepgg.sfa.Models.GetSkuListAfter;
import in.etaminepgg.sfa.Models.GetSkuListAfterNew;
import in.etaminepgg.sfa.Models.GetSkuThumbImage;
import in.etaminepgg.sfa.Models.IsSkuListUpdate;
import in.etaminepgg.sfa.Models.SkuGroupHistory;
import in.etaminepgg.sfa.Network.API_Call_Retrofit;
import in.etaminepgg.sfa.Network.ApiUrl;
import in.etaminepgg.sfa.Network.Apimethods;
import in.etaminepgg.sfa.R;
import in.etaminepgg.sfa.Utilities.Constants;
import in.etaminepgg.sfa.Utilities.ConstantsA;
import in.etaminepgg.sfa.Utilities.DbUtils;
import in.etaminepgg.sfa.Utilities.MyDb;
import in.etaminepgg.sfa.Utilities.MySharedPrefrencesData;
import in.etaminepgg.sfa.Utilities.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static in.etaminepgg.sfa.Utilities.Constants.MOD_TYPE_SKU_MULTIMEDIA_AND_INFO;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SKU_SUBCATEGORY;
import static in.etaminepgg.sfa.Utilities.Constants.dbFileFullPath;
import static in.etaminepgg.sfa.Utilities.ConstantsA.NOT_PRESENT;

public class NewSKUsFragment extends Fragment {
    int a = 0;
    int b = 0;
    boolean isUpdated = false;
    boolean isskuAttrloaded;
    boolean isskuinfoloaded;
    View layout;
    MySharedPrefrencesData mySharedPrefrencesData;
    RecyclerView newSKUs_RecyclerView;
    SQLiteDatabase sqLiteDatabase;
    String type = "4";
    int valueFromOpenDatabase;

    HashMap<String, String> getcatsubcat = new HashMap<>();
    SkuListByGenreActivity skuListByGenreActivity;

    boolean is_Attribute;


    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        this.layout = inflater.inflate(R.layout.fragment_new_skus, container, false);
        this.valueFromOpenDatabase = MyDb.openDatabase(Constants.dbFileFullPath);
        this.sqLiteDatabase = MyDb.getDbHandle(this.valueFromOpenDatabase);
        this.mySharedPrefrencesData = new MySharedPrefrencesData();

        this.is_Attribute=DbUtils.getConfigValue(Constants.SKU_ATTRIBUTE_REQUIRED);

        this.skuListByGenreActivity = (SkuListByGenreActivity) getActivity();


        if (!Utils.isNetworkConnected(getActivity())) {
            setAdapter(this.layout);
        } else if (this.mySharedPrefrencesData.getfirsttimeflagfornew(getActivity())) {
            this.mySharedPrefrencesData.setSkulistUpdateDate(getActivity(), Utils.getTodayDate());

            if(DbUtils.getSkuRowsSize()<1){

                networkcall_for_getSKUlistAfter(new MySharedPrefrencesData().getEmployee_AuthKey(getActivity()),false);
            }

        } else {
            //networkcall_for_isskulistUpdated();
        }

        setAdapter_Category();




        return this.layout;
    }

    private void networkcall_for_isskulistUpdated() {

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        Utils.startProgressDialog(getActivity(), progressDialog);

        Apimethods apimethods = (Apimethods) API_Call_Retrofit.getretrofit(getActivity()).create
                (Apimethods.class);
        IM_GetSkuListAfter IM_getSkuListAfter = new IM_GetSkuListAfter(this
                .mySharedPrefrencesData.getEmployee_AuthKey(getActivity()), this.type, this
                .mySharedPrefrencesData.getSkulistUpdateDate(getActivity()));
        Call<IsSkuListUpdate> call = apimethods.isSkuListUpdated(IM_getSkuListAfter);
        Log.i("isSkulistUpdate_ip_new", new Gson().toJson(IM_getSkuListAfter));


        call.enqueue(new Callback<IsSkuListUpdate>() {
            public void onResponse(Call<IsSkuListUpdate> call, Response<IsSkuListUpdate> response) {
                Log.d("Response", "" + response.code());
                Log.d("respones", "" + response);
                if (response.isSuccessful()) {
                    IsSkuListUpdate isSkuListUpdate = (IsSkuListUpdate) response.body();
                    Log.i("isSkulistUpdate_op_new", new Gson().toJson(isSkuListUpdate));
                    if (isSkuListUpdate.getIsUpdated().intValue() == 1) {

                        Utils.dismissProgressDialog(progressDialog);
                        NewSKUsFragment.this.isUpdated = true;
                        NewSKUsFragment.this.networkcall_for_getSKUlistAfter(NewSKUsFragment.this.mySharedPrefrencesData.getEmployee_AuthKey(NewSKUsFragment.this.getActivity()),true);
                        return;
                    }
                    return;
                }
                Utils.dismissProgressDialog(progressDialog);
                NewSKUsFragment.this.setAdapter(NewSKUsFragment.this.layout);
            }

            public void onFailure(Call<IsSkuListUpdate> call, Throwable t) {
                Utils.dismissProgressDialog(progressDialog);
                Utils.showToast(getContext(), ConstantsA.NO_INTERNET_CONNECTION);
            }
        });

    }


    public void setAdapter_Category() {
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String category_query = "SELECT sub_category_id from " + TBL_SKU_SUBCATEGORY + " WHERE " +
                "is_active = ?";
        String[] selectionArgs = new String[]{"1"};
        Cursor cursor = sqLiteDatabase.rawQuery(category_query, selectionArgs);
        String skuSubCategory = "";
        if (cursor.moveToFirst()) {

            skuSubCategory = cursor.getString(cursor.getColumnIndexOrThrow("sub_category_id"));

        }

        Log.d("SKU ID : ", ""+Constants.search_skuId);


        NewSKUsAdapter newSKUsAdapter = new NewSKUsAdapter(DbUtils.getSkuList_Category("select " +
                "sku_id, sku_name, sku_price, sku_category,sku_category_description, " +
                "sku_photo_source from " + Constants.TBL_SKU + " WHERE new_sku = ? AND " +
                "(sku_sub_category = ? OR sku_id = ?) ;", new String[]{"1", skuSubCategory,Constants.search_skuId}));
        this.newSKUs_RecyclerView = (RecyclerView) layout.findViewById(R.id.newSKUs_RecyclerView);
        this.newSKUs_RecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.newSKUs_RecyclerView.setItemAnimator(new DefaultItemAnimator());
        this.newSKUs_RecyclerView.setAdapter(newSKUsAdapter);


        cursor.close();
        sqLiteDatabase.close();

        newSKUsAdapter.notifyDataSetChanged();

    }

    private void setAdapter(View layout) {
       /* NewSKUsAdapter newSKUsAdapter = new NewSKUsAdapter(DbUtils.getSkuList("select sku_id,
       sku_name, sku_price, sku_category,sku_category_description, sku_photo_source from " +
       Constants.TBL_SKU + " WHERE new_sku = ? ;", new String[]{"1"}));
        this.newSKUs_RecyclerView = (RecyclerView) layout.findViewById(R.id.newSKUs_RecyclerView);
        this.newSKUs_RecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.newSKUs_RecyclerView.setItemAnimator(new DefaultItemAnimator());
        this.newSKUs_RecyclerView.setAdapter(newSKUsAdapter);

        newSKUsAdapter.notifyDataSetChanged();*/
    }

    private void networkcall_for_getSKUlistAfter(final String authToken, final boolean isUpdated)
    {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        Utils.startProgressDialog(getActivity(), progressDialog);
        progressDialog.setCancelable(false);
        String intentExtraKey_TabToShow = getResources().getString(R.string.key_tab_to_show);
        final Apimethods methods = (Apimethods) API_Call_Retrofit.getretrofit(getActivity())
                .create(Apimethods.class);
        String date_str = "";
        if (this.mySharedPrefrencesData.getfirsttimeflagforAll(getActivity()))
        {
            date_str = "2016-05-25";
        }
        else
        {
            date_str = this.mySharedPrefrencesData.getSkulistUpdateDate(getActivity());
        }
        IM_GetSkuListAfter IM_getSkuListAfter = new IM_GetSkuListAfter(authToken, this.type,
                date_str);
        Call<GetSkuListAfterNew> call = methods.getSkuListAfter(IM_getSkuListAfter);
        Log.i("GetSkuListAfter_ip_new", new Gson().toJson(IM_getSkuListAfter));
        Log.d("url", "url=" + call.request().url().toString());

        call.enqueue(new Callback<GetSkuListAfterNew>()
        {
            @Override
            public void onResponse(Call<GetSkuListAfterNew> call, Response<GetSkuListAfterNew> response)
            {


                Log.d("Response", "" + response.code());
                Log.d("respones", "" + response);

                if (response.isSuccessful())
                {

                    GetSkuListAfterNew getSkuListAfter = (GetSkuListAfterNew) response.body();
                    Log.i("GetSkuListAfter_op_new", new Gson().toJson(getSkuListAfter));

                    mySharedPrefrencesData.setfirsttimeflagfornew(getActivity(),false);

                    if (getSkuListAfter.getApiStatus().intValue() == 1)
                    {

                        for (GetSkuListAfterNew.SkuId skuId_Info : getSkuListAfter.getSkuIds())
                        {

                            ContentValues skuValues = new ContentValues();
                            skuValues.put(ConstantsA.KEY_SKU_ID, skuId_Info.getSkuId());
                            skuValues.put("sku_name", skuId_Info.getSkuName());
                            skuValues.put("sku_price", skuId_Info.getSkuPrice());
                            skuValues.put("description", skuId_Info.getSkuDescription());
                            skuValues.put("sku_category", skuId_Info.getSkuCategory());
                            skuValues.put("sku_sub_category", skuId_Info.getSkuSubCategory());
                            skuValues.put("sku_category_description", skuId_Info.getCategoryDescription());
                            skuValues.put("sku_sub_category_description", skuId_Info.getSubCategoryDescription());
                            skuValues.put("new_sku", skuId_Info.getSkuNewFlag());
                            skuValues.put("upload_status", Integer.valueOf(1));


                            for (GetSkuListAfterNew.SkuId.SkuMedium skuMedia : skuId_Info.getSkuMedia())
                            {
                                String selection = "sku_id = ?";
                                String[] selectionArgs = new String[]{skuMedia.getSkuId()};
                                if (skuMedia.getMediaType().equalsIgnoreCase("photo"))
                                {

                                    if (skuMedia.getMediaFile() != null && skuMedia.getMediaFile().length() > 2)
                                    {


                                        String sku_photourl_from_server = skuMedia.getMediaFile();

                                        skuValues.put("sku_photo_source", ApiUrl.BASEURL_SKUTHUMBIMAGE + DashboardActivity.substringAfterLastSeparator(sku_photourl_from_server, "/"));


                                    }
                                    else
                                    {

                                        skuValues.put("sku_photo_source", NOT_PRESENT);


                                    }
                                }
                                else if (skuMedia.getMediaType().equalsIgnoreCase
                                        ("catalogue"))
                                {

                                    if (skuMedia.getMediaFile() != null && skuMedia.getMediaFile().length() > 2)
                                    {

                                        String sku_photourl_from_server = skuMedia.getMediaFile();

                                        skuValues.put("sku_catalogue_source", ApiUrl.BASEURL_SKUTHUMBIMAGE + DashboardActivity.substringAfterLastSeparator(sku_photourl_from_server, "/"));


                                    }
                                    else
                                    {

                                        skuValues.put("sku_catalogue_source", NOT_PRESENT);


                                    }


                                }
                                else if (skuMedia.getMediaType().equalsIgnoreCase
                                        ("video"))
                                {

                                    if (skuMedia.getMediaFile() != null && skuMedia.getMediaFile().length() > 2)
                                    {


                                        String sku_photourl_from_server = skuMedia.getMediaFile();

                                        skuValues.put("sku_video_source", ApiUrl.BASEURL_SKUTHUMBIMAGE + DashboardActivity.substringAfterLastSeparator(sku_photourl_from_server, "/"));


                                    }
                                    else
                                    {

                                        skuValues.put("sku_video_source", NOT_PRESENT);


                                    }

                                }
                                else
                                {

                                }
                            }

                            if(!DbUtils.isSKUPresentInDb(skuId_Info.getSkuId()) && !isUpdated){

                                NewSKUsFragment.this.sqLiteDatabase.insert(Constants.TBL_SKU, null, skuValues);

                            }else if(DbUtils.isSKUPresentInDb(skuId_Info.getSkuId()) && isUpdated){

                                delete_sku_row_from_db(skuId_Info.getSkuId());

                                NewSKUsFragment.this.sqLiteDatabase.insert(Constants.TBL_SKU, null, skuValues);
                            }


                        }


                        ((SkuListByGenreActivity)getActivity()).setAdapterForSearch();

                        Utils.dismissProgressDialog(progressDialog);

                    }


                }

                Utils.dismissProgressDialog(progressDialog);
                NewSKUsFragment.this.setAdapter(NewSKUsFragment.this.layout);
            }

            @Override
            public void onFailure(Call<GetSkuListAfterNew> call, Throwable t)
            {
                Utils.dismissProgressDialog(progressDialog);
                Utils.showToast(NewSKUsFragment.this.getActivity(), ConstantsA.NO_INTERNET_CONNECTION);
            }
        });
    }



    private void delete_sku_row_from_db(String sku_id) {
        String selection = "sku_id = ?";
        String[] selectionArgs = new String[]{sku_id};
       /* Cursor cursor = this.sqLiteDatabase.rawQuery("SELECT attribute_id FROM " + Constants
                .TBL_SKU_ATTRIBUTE_MAPPING + " WHERE sku_id = ?", selectionArgs);
        while (cursor.moveToNext()) {
            String[] selectionArgs1 = new String[]{cursor.getString(cursor.getColumnIndexOrThrow
                    ("attribute_id"))};
            this.sqLiteDatabase.delete(Constants.TBL_GLOBAL_ATTRIBUTES, "attribute_id = ?",
                    selectionArgs1);
        }
        this.sqLiteDatabase.delete(Constants.TBL_SKU_ATTRIBUTE_MAPPING, selection, selectionArgs);*/
        this.sqLiteDatabase.delete(Constants.TBL_SKU, selection, selectionArgs);
    }
}
