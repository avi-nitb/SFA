package in.etaminepgg.sfa.Fragments;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import in.etaminepgg.sfa.Activities.DashboardActivity;
import in.etaminepgg.sfa.Activities.SkuListByGenreActivity;
import in.etaminepgg.sfa.Adapters.AllSKUsAdapter;
import in.etaminepgg.sfa.InputModel_For_Network.IM_GetSkuListAfter;
import in.etaminepgg.sfa.Models.GetSkuListAfterNew;
import in.etaminepgg.sfa.Models.IsSkuListUpdate;
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

import static in.etaminepgg.sfa.Utilities.Constants.TBL_SKU_SUBCATEGORY;
import static in.etaminepgg.sfa.Utilities.Constants.dbFileFullPath;
import static in.etaminepgg.sfa.Utilities.ConstantsA.NOT_PRESENT;

public class AllSKUsFragment extends Fragment
{
    int a = 0;
    RecyclerView allSKUs_RecyclerView;
    int b = 0;
    boolean isUpdated = false;
    View layout;
    MySharedPrefrencesData mySharedPrefrencesData;
    SQLiteDatabase sqLiteDatabase;
    String type = "1";
    int valueFromOpenDatabase;

    boolean is_Attribute;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        this.layout = inflater.inflate(R.layout.fragment_all_skus, container, false);
        this.valueFromOpenDatabase = MyDb.openDatabase(Constants.dbFileFullPath);
        this.sqLiteDatabase = MyDb.getDbHandle(this.valueFromOpenDatabase);
        this.mySharedPrefrencesData = new MySharedPrefrencesData();
        this.is_Attribute = DbUtils.getConfigValue(Constants.SKU_ATTRIBUTE_REQUIRED);
        if (!Utils.isNetworkConnected(getActivity()))
        {
            setAdapter(this.layout);
        }
        else if (this.mySharedPrefrencesData.getfirsttimeflagforAll(getActivity()))
        {
            if (DbUtils.getSkuRowsSize() < 1)
            {
                this.mySharedPrefrencesData.setSkulistUpdateDate(getActivity(), Utils.getTodayDate());
                networkcall_for_getSKUlistAfter(new MySharedPrefrencesData().getEmployee_AuthKey(getActivity()), false);
            }
        }
        else
        {
            // networkcall_for_isskulistUpdated();
        }

        setAdapter_Category();

        return this.layout;
    }

    private void setAdapter(View layout)
    {

    }


    public void setAdapter_Category()
    {
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String category_query = "SELECT sub_category_id from " + TBL_SKU_SUBCATEGORY + " WHERE is_active = ?";
        String[] selectionArgs = new String[]{"1"};
        Cursor cursor = sqLiteDatabase.rawQuery(category_query, selectionArgs);
        String skuSubCategory = "";
        if (cursor.moveToFirst())
        {

            skuSubCategory = cursor.getString(cursor.getColumnIndexOrThrow("sub_category_id"));

        }


        AllSKUsAdapter allSKUsAdapter = new AllSKUsAdapter(DbUtils.getSkuList("select sku_id, sku_name, sku_price, sku_category,sku_category_description, sku_photo_source from " + Constants.TBL_SKU + "  WHERE sku_sub_category = ? OR sku_id = ? ;", new String[]{skuSubCategory, Constants.search_skuId}));
        this.allSKUs_RecyclerView = (RecyclerView) layout.findViewById(R.id.allSKUs_RecyclerView);
        this.allSKUs_RecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.allSKUs_RecyclerView.setItemAnimator(new DefaultItemAnimator());
        this.allSKUs_RecyclerView.setAdapter(allSKUsAdapter);


        cursor.close();
        sqLiteDatabase.close();

        allSKUsAdapter.notifyDataSetChanged();

    }


    //api call for updated list
    private void networkcall_for_isskulistUpdated()
    {


        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        Utils.startProgressDialog(getActivity(), progressDialog);

        Apimethods apimethods = (Apimethods) API_Call_Retrofit.getretrofit(getActivity()).create(Apimethods.class);
        IM_GetSkuListAfter IM_getSkuListAfter = new IM_GetSkuListAfter(this.mySharedPrefrencesData.getEmployee_AuthKey(getActivity()), this.type, this.mySharedPrefrencesData.getSkulistUpdateDate(getActivity()));
        Call<IsSkuListUpdate> call = apimethods.isSkuListUpdated(IM_getSkuListAfter);
        Log.i("isSkulistUpdate_ip", new Gson().toJson(IM_getSkuListAfter));

        call.enqueue(new Callback<IsSkuListUpdate>()
        {
            public void onResponse(Call<IsSkuListUpdate> call, Response<IsSkuListUpdate> response)
            {
                Log.d("Response", "" + response.code());
                Log.d("respones", "" + response);
                if (response.isSuccessful())
                {
                    IsSkuListUpdate isSkuListUpdate = (IsSkuListUpdate) response.body();
                    Log.i("isSkulistUpdate_op", new Gson().toJson(isSkuListUpdate));
                    if (isSkuListUpdate.getIsUpdated().intValue() == 1)
                    {
                        Utils.dismissProgressDialog(progressDialog);
                        AllSKUsFragment.this.isUpdated = true;
                        AllSKUsFragment.this.networkcall_for_getSKUlistAfter(AllSKUsFragment.this.mySharedPrefrencesData.getEmployee_AuthKey(AllSKUsFragment.this.getActivity()), true);
                        return;
                    }
                    return;
                }
                Utils.dismissProgressDialog(progressDialog);
                AllSKUsFragment.this.setAdapter(AllSKUsFragment.this.layout);
            }

            public void onFailure(Call<IsSkuListUpdate> call, Throwable t)
            {
                Utils.dismissProgressDialog(progressDialog);
                Utils.showToast(AllSKUsFragment.this.getActivity(), ConstantsA.NO_INTERNET_CONNECTION);
            }
        });

    }


    //api call for all sku
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

                    mySharedPrefrencesData.setfirsttimeflagforAll(getActivity(), false);

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

                            if (!DbUtils.isSKUPresentInDb(skuId_Info.getSkuId()) && !isUpdated)
                            {

                                AllSKUsFragment.this.sqLiteDatabase.insert(Constants.TBL_SKU, null, skuValues);

                            }
                            else if (DbUtils.isSKUPresentInDb(skuId_Info.getSkuId()) && isUpdated)
                            {

                                delete_sku_row_from_db(skuId_Info.getSkuId());

                                AllSKUsFragment.this.sqLiteDatabase.insert(Constants.TBL_SKU, null, skuValues);
                            }


                        }

                        ((SkuListByGenreActivity) getActivity()).setAdapterForSearch();

                        Utils.dismissProgressDialog(progressDialog);

                    }


                }

                Utils.dismissProgressDialog(progressDialog);
                AllSKUsFragment.this.setAdapter(AllSKUsFragment.this.layout);
            }

            @Override
            public void onFailure(Call<GetSkuListAfterNew> call, Throwable t)
            {
                Utils.dismissProgressDialog(progressDialog);
                Utils.showToast(AllSKUsFragment.this.getActivity(), ConstantsA.NO_INTERNET_CONNECTION);
            }
        });
    }


    private void delete_sku_row_from_db(String sku_id)
    {
        String selection = "sku_id = ?";
        String[] selectionArgs = new String[]{sku_id};
        this.sqLiteDatabase.delete(Constants.TBL_SKU, selection, selectionArgs);
    }
}
