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
import in.etaminepgg.sfa.InputModel_For_Network.IM_GetSkuInfo;
import in.etaminepgg.sfa.InputModel_For_Network.IM_GetSkuListAfter;
import in.etaminepgg.sfa.Models.GetSkuAttribute;
import in.etaminepgg.sfa.Models.GetSkuAttribute.SkuAttr;
import in.etaminepgg.sfa.Models.GetSkuInfo;
import in.etaminepgg.sfa.Models.GetSkuInfo.SkuIds;
import in.etaminepgg.sfa.Models.GetSkuListAfter;
import in.etaminepgg.sfa.Models.GetSkuThumbImage;
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

import static in.etaminepgg.sfa.Utilities.Constants.MOD_TYPE_SKU_MULTIMEDIA_AND_INFO;
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
        this.is_Attribute=DbUtils.getConfigValue(Constants.SKU_ATTRIBUTE_REQUIRED);
        if (!Utils.isNetworkConnected(getActivity()))
        {
            setAdapter(this.layout);
        }
        else if (this.mySharedPrefrencesData.getfirsttimeflagforAll(getActivity()))
        {
            networkcall_for_getSKUlistAfter(new MySharedPrefrencesData().getEmployee_AuthKey(getActivity()));
        }
        else
        {
            networkcall_for_isskulistUpdated();
        }

        setAdapter_Category();

        return this.layout;
    }

    private void setAdapter(View layout)
    {
        /*AllSKUsAdapter allSKUsAdapter = new AllSKUsAdapter(DbUtils.getSkuList("select sku_id, sku_name, sku_price, sku_category,sku_category_description, sku_photo_source from " + Constants.TBL_SKU + " ;", null));
        this.allSKUs_RecyclerView = (RecyclerView) layout.findViewById(R.id.allSKUs_RecyclerView);
        this.allSKUs_RecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.allSKUs_RecyclerView.setItemAnimator(new DefaultItemAnimator());
        this.allSKUs_RecyclerView.setAdapter(allSKUsAdapter);*/
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


        AllSKUsAdapter allSKUsAdapter = new AllSKUsAdapter(DbUtils.getSkuList("select sku_id, sku_name, sku_price, sku_category,sku_category_description, sku_photo_source from " + Constants.TBL_SKU + "  WHERE sku_sub_category = ? OR sku_id = ? ;", new String[]{skuSubCategory,Constants.search_skuId}));
        this.allSKUs_RecyclerView = (RecyclerView) layout.findViewById(R.id.allSKUs_RecyclerView);
        this.allSKUs_RecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.allSKUs_RecyclerView.setItemAnimator(new DefaultItemAnimator());
        this.allSKUs_RecyclerView.setAdapter(allSKUsAdapter);


        cursor.close();
        sqLiteDatabase.close();

        allSKUsAdapter.notifyDataSetChanged();

    }

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
                        AllSKUsFragment.this.networkcall_for_getSKUlistAfter(AllSKUsFragment.this.mySharedPrefrencesData.getEmployee_AuthKey(AllSKUsFragment.this.getActivity()));
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

    /*private void networkcall_for_getSKUlistAfter(final String authToken)
    {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        Utils.startProgressDialog(getActivity(), progressDialog);
        String intentExtraKey_TabToShow = getResources().getString(R.string.key_tab_to_show);
        final Apimethods methods = (Apimethods) API_Call_Retrofit.getretrofit(getActivity()).create(Apimethods.class);
        String date_str = "";
        if (this.mySharedPrefrencesData.getfirsttimeflagforAll(getActivity()))
        {
            date_str = "2016-05-25";
        }
        else
        {
            date_str = this.mySharedPrefrencesData.getSkulistUpdateDate(getActivity());
        }
        IM_GetSkuListAfter IM_getSkuListAfter = new IM_GetSkuListAfter(authToken, this.type, date_str);
        Call<GetSkuListAfter> call = methods.getSkuListAfter(IM_getSkuListAfter);
        Log.i("GetSkuListAfter_ip", new Gson().toJson(IM_getSkuListAfter));
        Log.d("url", "url=" + call.request().url().toString());

        call.enqueue(new Callback<GetSkuListAfter>()
        {
            public void onResponse(Call<GetSkuListAfter> call, Response<GetSkuListAfter> response)
            {
                Log.d("Response", "" + response.code());
                Log.d("respones", "" + response);
                if (response.isSuccessful())
                {
                    GetSkuListAfter getSkuListAfter = (GetSkuListAfter) response.body();
                    Log.i("GetSkuListAfter_op", new Gson().toJson(getSkuListAfter));
                    AllSKUsFragment.this.mySharedPrefrencesData.setSkulistUpdateDate(AllSKUsFragment.this.getActivity(), Utils.getTodayDate());
                    AllSKUsFragment.this.mySharedPrefrencesData.setfirsttimeflagforAll(AllSKUsFragment.this.getActivity(), false);
                    if (getSkuListAfter.getApiStatus().intValue() == 1)
                    {
                        a = getSkuListAfter.getSkuIds().size();
                        for (GetSkuListAfter.SkuInfo skuInfo : getSkuListAfter.getSkuIds())
                        {
                            if (DbUtils.isSKUPresentInDb(skuInfo.getSku_id()))
                            {
                                AllSKUsFragment allSKUsFragment = AllSKUsFragment.this;
                                a--;
                            }
                            else
                            {
                                networkcall_for_sku_info(skuInfo.getSku_id(), authToken);
                            }
                        }
                        Utils.dismissProgressDialog(progressDialog);
                        AllSKUsFragment.this.setAdapter(AllSKUsFragment.this.layout);
                        return;
                    }
                    return;
                }
                Utils.dismissProgressDialog(progressDialog);
                AllSKUsFragment.this.setAdapter(AllSKUsFragment.this.layout);
                Utils.showToast(AllSKUsFragment.this.getActivity(), "ALL : Unsuccessful api call for getSkuListAfter ");
            }

            private void networkcall_for_sku_Attr(String sku_id, String authToken)
            {
                Call<GetSkuAttribute> call = methods.getSkuAttr(new IM_GetSkuInfo(authToken, sku_id));
                Log.d("url", "url=" + call.request().url().toString());
                call.enqueue(new Callback<GetSkuAttribute>()
                {
                    public void onResponse(Call<GetSkuAttribute> call, Response<GetSkuAttribute> response)
                    {
                        Log.d("Response", "" + response.code());
                        Log.d("respones", "" + response);
                        if (response.isSuccessful())
                        {
                            GetSkuAttribute getSkuAttribute = (GetSkuAttribute) response.body();
                            if (getSkuAttribute.getApiStatus().intValue() == 1)
                            {
                                for (int j = 0; j < getSkuAttribute.getSkuAttr().size(); j++)
                                {
                                    SkuAttr skuAttr = (SkuAttr) getSkuAttribute.getSkuAttr().get(j);
                                    ContentValues skuAttrMappingValues = new ContentValues();
                                    skuAttrMappingValues.put(ConstantsA.KEY_SKU_ID, skuAttr.getSkuId());
                                    skuAttrMappingValues.put("attribute_id", skuAttr.getGlobalAttributeId());
                                    skuAttrMappingValues.put("upload_status", Integer.valueOf(0));
                                    AllSKUsFragment.this.sqLiteDatabase.insert(Constants.TBL_SKU_ATTRIBUTE_MAPPING, null, skuAttrMappingValues);
                                    String[] attributevalue_array = skuAttr.getAttributeValue().split(",");
                                    StringBuilder builder = new StringBuilder();
                                    for (String s : attributevalue_array)
                                    {
                                        builder.append(s);
                                        builder.append("`");
                                    }
                                    String attributevalueset = builder.toString();
                                    String attr_value_set_final = attributevalueset.substring(0, attributevalueset.length() - 1);
                                    if (!DbUtils.isAttribute_IDPresentInDb(skuAttr.getAttributeId()))
                                    {
                                        ContentValues globalAttributeValues = new ContentValues();
                                        globalAttributeValues.put("attribute_id", skuAttr.getGlobalAttributeId());
                                        globalAttributeValues.put("attribute_type", skuAttr.getAttributeType());
                                        globalAttributeValues.put("attribute_name", skuAttr.getAttributeName());
                                        globalAttributeValues.put("attribute_value", attr_value_set_final);
                                        globalAttributeValues.put("created_date", skuAttr.getCreatedOn());
                                        globalAttributeValues.put("upload_status", Integer.valueOf(0));
                                        AllSKUsFragment.this.sqLiteDatabase.insert(Constants.TBL_GLOBAL_ATTRIBUTES, null, globalAttributeValues);
                                    }
                                }
                                if (a == b)
                                {
                                    Utils.dismissProgressDialog(progressDialog);
                                    AllSKUsFragment.this.setAdapter(AllSKUsFragment.this.layout);
                                    return;
                                }
                                return;
                            }
                            return;
                        }
                        Utils.dismissProgressDialog(progressDialog);
                        Utils.showToast(AllSKUsFragment.this.getActivity(), "Unsuccessful api call for GetSkuAttribute ");
                        if (a == b)
                        {
                            Utils.dismissProgressDialog(progressDialog);
                            AllSKUsFragment.this.setAdapter(AllSKUsFragment.this.layout);
                        }
                    }

                    public void onFailure(Call<GetSkuAttribute> call, Throwable t)
                    {
                        Utils.dismissProgressDialog(progressDialog);
                        Utils.showToast(AllSKUsFragment.this.getActivity(), ConstantsA.NO_INTERNET_CONNECTION);
                    }
                });
            }

            private void networkcall_for_sku_info(String sku_id, final String authToken)
            {
                Call<GetSkuInfo> call = methods.getSkuInfo(new IM_GetSkuInfo(authToken, sku_id));
                Log.d("url", "url=" + call.request().url().toString());
                call.enqueue(new Callback<GetSkuInfo>()
                {
                    public void onResponse(Call<GetSkuInfo> call, Response<GetSkuInfo> response)
                    {
                        Log.d("Response", "" + response.code());
                        Log.d("respones", "" + response);
                        if (response.isSuccessful())
                        {
                            GetSkuInfo getSkuInfo = (GetSkuInfo) response.body();
                            if (getSkuInfo.getApiStatus().intValue() == 1)
                            {
                                SkuIds skuId_Info = getSkuInfo.getSkuIds();
                                ContentValues skuValues = new ContentValues();
                                skuValues.put(ConstantsA.KEY_SKU_ID, skuId_Info.getSkuId());
                                skuValues.put("sku_name", skuId_Info.getSkuName());
                                skuValues.put("sku_price", skuId_Info.getSkuPrice());
                                skuValues.put("description", skuId_Info.getSkuDescription());
                                skuValues.put("sku_category", skuId_Info.getSkuCategory());
                                skuValues.put("sku_sub_category", skuId_Info.getSkuSubCategory());
                                skuValues.put("sku_category_description", skuId_Info.getCategory_description());
                                skuValues.put("sku_sub_category_description", skuId_Info.getSub_category_description());
                                skuValues.put("created_date", skuId_Info.getSkuCreatedOn());
                                skuValues.put("modified_date", skuId_Info.getSkuModifiedOn());
                                skuValues.put("created_by", skuId_Info.getSkuCreatedBy());
                                skuValues.put("modified_by", skuId_Info.getSkuModifiedBy());
                                skuValues.put("new_sku", skuId_Info.getSkuNewFlag());
                                skuValues.put("upload_status", Integer.valueOf(1));
                                AllSKUsFragment.this.sqLiteDatabase.insert(Constants.TBL_SKU, null, skuValues);
                                networkcall_for_sku_ThumbImage(skuId_Info.getSkuId(), authToken);
                                return;
                            }
                            return;
                        }
                        Utils.dismissProgressDialog(progressDialog);
                        Utils.showToast(AllSKUsFragment.this.getActivity(), "Unsuccessful api call for getskuinfo ");
                    }

                    private void networkcall_for_sku_ThumbImage(final String sku_id, final String authToken)
                    {
                        Call<GetSkuThumbImage> call = methods.getSkuThumbImage(new IM_GetSkuInfo(authToken, sku_id));
                        Log.d("url", "url=" + call.request().url().toString());
                        call.enqueue(new Callback<GetSkuThumbImage>()
                        {
                            public void onResponse(Call<GetSkuThumbImage> call, Response<GetSkuThumbImage> response)
                            {
                                Log.d("Response", "" + response.code());
                                Log.d("respones", "" + response);
                                if (response.isSuccessful())
                                {
                                    GetSkuThumbImage getSkuThumbImage = (GetSkuThumbImage) response.body();
                                    if (getSkuThumbImage.getApiStatus().intValue() == 1)
                                    {
                                        for (SkuMedium skuMedia : getSkuThumbImage.getSkuMedia())
                                        {
                                            String selection = "sku_id = ?";
                                            String[] selectionArgs = new String[]{skuMedia.getSkuId()};
                                            if (skuMedia.getMediaType().equalsIgnoreCase("photo"))
                                            {

                                                if (skuMedia.getMediaFile() != null && skuMedia.getMediaFile().length() > 2)
                                                {


                                                    String sku_photourl_from_server = skuMedia.getMediaFile();
                                                    ContentValues contentValues = new ContentValues();
                                                    contentValues.put("sku_photo_source", ApiUrl.BASEURL_SKUTHUMBIMAGE + DashboardActivity.substringAfterLastSeparator(sku_photourl_from_server, "/"));
                                                    AllSKUsFragment.this.sqLiteDatabase.update(Constants.TBL_SKU, contentValues, selection, selectionArgs);

                                                }
                                                else
                                                {
                                                    ContentValues contentValues = new ContentValues();
                                                    contentValues.put("sku_photo_source", NOT_PRESENT);
                                                    AllSKUsFragment.this.sqLiteDatabase.update(Constants.TBL_SKU, contentValues, selection, selectionArgs);

                                                }


                                            }
                                            else if (skuMedia.getMediaType().equalsIgnoreCase("catalogue"))
                                            {

                                                if (skuMedia.getMediaFile() != null && skuMedia.getMediaFile().length() > 2)
                                                {

                                                    String sku_photourl_from_server = skuMedia.getMediaFile();
                                                    ContentValues contentValues = new ContentValues();
                                                    contentValues.put("sku_catalogue_source", ApiUrl.BASEURL_SKUTHUMBIMAGE + DashboardActivity.substringAfterLastSeparator(sku_photourl_from_server, "/"));
                                                    AllSKUsFragment.this.sqLiteDatabase.update(Constants.TBL_SKU, contentValues, selection, selectionArgs);

                                                }
                                                else
                                                {
                                                    ContentValues contentValues = new ContentValues();
                                                    contentValues.put("sku_catalogue_source", NOT_PRESENT);
                                                    AllSKUsFragment.this.sqLiteDatabase.update(Constants.TBL_SKU, contentValues, selection, selectionArgs);

                                                }



                                            }
                                            else if (skuMedia.getMediaType().equalsIgnoreCase("video"))
                                            {

                                                if (skuMedia.getMediaFile() != null && skuMedia.getMediaFile().length() > 2)
                                                {


                                                    String sku_photourl_from_server = skuMedia.getMediaFile();
                                                    ContentValues contentValues = new ContentValues();
                                                    contentValues.put("sku_video_source", ApiUrl.BASEURL_SKUTHUMBIMAGE + DashboardActivity.substringAfterLastSeparator(sku_photourl_from_server, "/"));
                                                    AllSKUsFragment.this.sqLiteDatabase.update(Constants.TBL_SKU, contentValues, selection, selectionArgs);
                                                }
                                                else
                                                {
                                                    ContentValues contentValues = new ContentValues();
                                                    contentValues.put("sku_video_source", NOT_PRESENT);
                                                    AllSKUsFragment.this.sqLiteDatabase.update(Constants.TBL_SKU, contentValues, selection, selectionArgs);

                                                }

                                            }
                                            else
                                            {

                                            }
                                        }
                                        b++;
                                        networkcall_for_sku_Attr(sku_id, authToken);
                                        return;
                                    }
                                    return;
                                }
                                Utils.dismissProgressDialog(progressDialog);
                                Utils.showToast(AllSKUsFragment.this.getActivity(), "Unsuccessful api call for getskuinfo_thumbimage ");
                            }

                            public void onFailure(Call<GetSkuThumbImage> call, Throwable t)
                            {
                                Utils.dismissProgressDialog(progressDialog);
                                Utils.showToast(AllSKUsFragment.this.getActivity(), ConstantsA.NO_INTERNET_CONNECTION);
                            }
                        });
                    }

                    public void onFailure(Call<GetSkuInfo> call, Throwable t)
                    {
                        Utils.dismissProgressDialog(progressDialog);
                        Utils.showToast(AllSKUsFragment.this.getActivity(), ConstantsA.NO_INTERNET_CONNECTION);
                    }
                });
            }

            public void onFailure(Call<GetSkuListAfter> call, Throwable t)
            {
                Utils.dismissProgressDialog(progressDialog);
                Utils.showToast(AllSKUsFragment.this.getActivity(), ConstantsA.NO_INTERNET_CONNECTION);
            }
        });

    }*/



    private void networkcall_for_getSKUlistAfter(final String authToken) {
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
        Call<GetSkuListAfter> call = methods.getSkuListAfter(IM_getSkuListAfter);
        Log.i("GetSkuListAfter_ip_new", new Gson().toJson(IM_getSkuListAfter));
        Log.d("url", "url=" + call.request().url().toString());


        call.enqueue(new Callback<GetSkuListAfter>() {
            @Override
            public void onResponse(Call<GetSkuListAfter> call, Response<GetSkuListAfter> response) {
                Log.d("Response", "" + response.code());
                Log.d("respones", "" + response);
                if (response.isSuccessful()) {
                    AllSKUsFragment.this.mySharedPrefrencesData.setfirsttimeflagfornew
                            (AllSKUsFragment.this.getActivity(), false);
                    GetSkuListAfter getSkuListAfter = (GetSkuListAfter) response.body();
                    Log.i("GetSkuListAfter_op_new", new Gson().toJson(getSkuListAfter));
                    if (getSkuListAfter.getApiStatus().intValue() == 1) {
                        AllSKUsFragment.this.a = getSkuListAfter.getSkuIds().size();
                        for (GetSkuListAfter.SkuInfo skuInfo : getSkuListAfter.getSkuIds()) {

                            if (skuInfo.getOp_type() == null) {

                                if (!DbUtils.isSKUPresentInDb(skuInfo.getSku_id())) {

                                    networkcall_for_sku_info(skuInfo.getSku_id(), authToken, skuInfo.getMod_type());
                                }else{

                                    a--;
                                }

                            } else if (skuInfo.getOp_type().equalsIgnoreCase(Constants.OP_TYPE_SKU_NEW)) {

                                if (!DbUtils.isSKUPresentInDb(skuInfo.getSku_id())) {

                                    networkcall_for_sku_info(skuInfo.getSku_id(), authToken, skuInfo.getMod_type());
                                }else {
                                    a--;
                                }
                            } else if (skuInfo.getOp_type().equalsIgnoreCase(Constants.OP_TYPE_SKU_MODIFICATION)) {

                                networkcall_for_sku_info(skuInfo.getSku_id(), authToken, skuInfo.getMod_type());


                            } else {

                            }


                           /* if (DbUtils.isSKUPresentInDb(skuInfo.getSku_id()))
                            {
                                updatesku_new(skuInfo.getSku_id());

                                a--;
                            }
                            else
                            {
                                networkcall_for_sku_info(skuInfo.getSku_id(), authToken);
                            }*/
                        }

                        Log.d("ab value",a+" :"+b);
                        if(a==0 && b==0){

                            Utils.dismissProgressDialog(progressDialog);
                        }
                        AllSKUsFragment.this.setAdapter(AllSKUsFragment.this.layout);

                        return;
                    }
                    return;
                }
                Utils.dismissProgressDialog(progressDialog);
                AllSKUsFragment.this.setAdapter(AllSKUsFragment.this.layout);
            }

            private void networkcall_for_sku_Attr(String sku_id, String authToken, final String modificationtype) {
                Call<GetSkuAttribute> call = methods.getSkuAttr(new IM_GetSkuInfo(authToken, sku_id));

                final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                Utils.startProgressDialog(getActivity(), progressDialog);


                Log.d("url", "url=" + call.request().url().toString());
                call.enqueue(new Callback<GetSkuAttribute>() {
                    public void onResponse(Call<GetSkuAttribute> call, Response<GetSkuAttribute>
                            response) {
                        Log.d("Response", "" + response.code());
                        Log.d("respones", "" + response);
                        if (response.isSuccessful()) {

                            Utils.dismissProgressDialog(progressDialog);
                            GetSkuAttribute getSkuAttribute = (GetSkuAttribute) response.body();
                            if (getSkuAttribute.getApiStatus().intValue() == 1) {

                                for (int j = 0; j < getSkuAttribute.getSkuAttr().size(); j++) {
                                    SkuAttr skuAttr = (SkuAttr) getSkuAttribute.getSkuAttr().get(j);

                                    ContentValues skuAttrMappingValues = new ContentValues();
                                    skuAttrMappingValues.put(ConstantsA.KEY_SKU_ID, skuAttr.getSkuId());
                                    skuAttrMappingValues.put("attribute_id", skuAttr.getGlobalAttributeId());
                                    skuAttrMappingValues.put("upload_status", Integer.valueOf(0));

                                    String[] attributevalue_array = skuAttr.getAttributeValue().split(",");
                                    StringBuilder builder = new StringBuilder();
                                    for (String s : attributevalue_array) {
                                        builder.append(s);
                                        builder.append("`");
                                    }
                                    String attributevalueset = builder.toString();
                                    String attr_value_set_final = attributevalueset.substring(0, attributevalueset.length() - 1);

                                    ContentValues globalAttributeValues = new ContentValues();
                                    globalAttributeValues.put("attribute_id", skuAttr.getGlobalAttributeId());
                                    globalAttributeValues.put("attribute_type", skuAttr.getAttributeType());
                                    globalAttributeValues.put("attribute_name", skuAttr.getAttributeName());
                                    globalAttributeValues.put("attribute_value", attr_value_set_final);
                                    globalAttributeValues.put("created_date", skuAttr.getCreatedOn());
                                    globalAttributeValues.put("upload_status", Integer.valueOf(0));

                                    if(modificationtype==null){

                                        AllSKUsFragment.this.sqLiteDatabase.insert(Constants.TBL_SKU_ATTRIBUTE_MAPPING, null, skuAttrMappingValues);
                                        AllSKUsFragment.this.sqLiteDatabase.insert(Constants.TBL_GLOBAL_ATTRIBUTES, null, globalAttributeValues);

                                    }else if(modificationtype.equalsIgnoreCase(Constants.MOD_TYPE_SKU_ATTR) || modificationtype.equalsIgnoreCase(Constants.MOD_TYPE_SKU_ATTR)){


                                        if (DbUtils.isAttribute_IDPresentInDb(skuAttr.getAttributeId())) {

                                            AllSKUsFragment.this.sqLiteDatabase.update(Constants.TBL_SKU_ATTRIBUTE_MAPPING, skuAttrMappingValues, " attribute_id =  ? ", new String[]{skuAttr.getAttributeId()});

                                            AllSKUsFragment.this.sqLiteDatabase.update(Constants.TBL_GLOBAL_ATTRIBUTES, globalAttributeValues, " attribute_id = ?", new String[]{skuAttr.getGlobalAttributeId()});
                                        }else {

                                            AllSKUsFragment.this.sqLiteDatabase.insert(Constants.TBL_SKU_ATTRIBUTE_MAPPING, null, skuAttrMappingValues);
                                            AllSKUsFragment.this.sqLiteDatabase.insert(Constants.TBL_GLOBAL_ATTRIBUTES, null, globalAttributeValues);
                                        }
                                    }else if(modificationtype.equalsIgnoreCase(Constants.MOD_TYPE_SKU_MULTIMEDIA_AND_INFO)|| modificationtype.equalsIgnoreCase(Constants.MOD_TYPE_SKU_INFO_AND_MULTIMEDIA)){

                                    }else if(modificationtype.equalsIgnoreCase(Constants.MOD_TYPE_SKU_INFO_AND_ATTR)|| modificationtype.equalsIgnoreCase(Constants.MOD_TYPE_SKU_ATTR_AND_INFO)){


                                        if (DbUtils.isAttribute_IDPresentInDb(skuAttr.getAttributeId())) {

                                            AllSKUsFragment.this.sqLiteDatabase.update(Constants.TBL_SKU_ATTRIBUTE_MAPPING, skuAttrMappingValues, " attribute_id =  ? ", new String[]{skuAttr.getAttributeId()});

                                            AllSKUsFragment.this.sqLiteDatabase.update(Constants.TBL_GLOBAL_ATTRIBUTES, globalAttributeValues, " attribute_id = ?", new String[]{skuAttr.getGlobalAttributeId()});
                                        }else {

                                            AllSKUsFragment.this.sqLiteDatabase.insert(Constants.TBL_SKU_ATTRIBUTE_MAPPING, null, skuAttrMappingValues);
                                            AllSKUsFragment.this.sqLiteDatabase.insert(Constants.TBL_GLOBAL_ATTRIBUTES, null, globalAttributeValues);
                                        }
                                    }else if(modificationtype.equalsIgnoreCase(Constants.MOD_TYPE_SKU_MULTIMEDIA_AND_ATTR)|| modificationtype.equalsIgnoreCase(Constants.MOD_TYPE_SKU_ATTR_AND_MULTIMEDIA)){

                                        if (DbUtils.isAttribute_IDPresentInDb(skuAttr.getAttributeId())) {

                                            AllSKUsFragment.this.sqLiteDatabase.update(Constants.TBL_SKU_ATTRIBUTE_MAPPING, skuAttrMappingValues, " attribute_id =  ? ", new String[]{skuAttr.getAttributeId()});

                                            AllSKUsFragment.this.sqLiteDatabase.update(Constants.TBL_GLOBAL_ATTRIBUTES, globalAttributeValues, " attribute_id = ?", new String[]{skuAttr.getGlobalAttributeId()});
                                        }else {

                                            AllSKUsFragment.this.sqLiteDatabase.insert(Constants.TBL_SKU_ATTRIBUTE_MAPPING, null, skuAttrMappingValues);
                                            AllSKUsFragment.this.sqLiteDatabase.insert(Constants.TBL_GLOBAL_ATTRIBUTES, null, globalAttributeValues);
                                        }

                                    }else if(modificationtype.contains(Constants.MOD_TYPE_SKU_ATTR) && modificationtype.contains(Constants.MOD_TYPE_SKU_MULTIMEDIA) && modificationtype.contains(Constants.MOD_TYPE_SKU_INFO) ){
                                        if (DbUtils.isAttribute_IDPresentInDb(skuAttr.getAttributeId())) {

                                            AllSKUsFragment.this.sqLiteDatabase.update(Constants.TBL_SKU_ATTRIBUTE_MAPPING, skuAttrMappingValues, " attribute_id =  ? ", new String[]{skuAttr.getAttributeId()});

                                            AllSKUsFragment.this.sqLiteDatabase.update(Constants.TBL_GLOBAL_ATTRIBUTES, globalAttributeValues, " attribute_id = ?", new String[]{skuAttr.getGlobalAttributeId()});
                                        }else {

                                            AllSKUsFragment.this.sqLiteDatabase.insert(Constants.TBL_SKU_ATTRIBUTE_MAPPING, null, skuAttrMappingValues);
                                            AllSKUsFragment.this.sqLiteDatabase.insert(Constants.TBL_GLOBAL_ATTRIBUTES, null, globalAttributeValues);
                                        }
                                    }

                                }

                                Utils.dismissProgressDialog(progressDialog);
                               /* if (NewSKUsFragment.this.a == NewSKUsFragment.this.b) {
                                    Utils.dismissProgressDialog(progressDialog);
                                    NewSKUsFragment.this.setAdapter(NewSKUsFragment.this.layout);
                                    return;
                                }*/

                                return;
                            }
                            return;
                        }
                        Utils.dismissProgressDialog(progressDialog);
                       // Utils.showToast(AllSKUsFragment.this.getActivity(), "Unsuccessful api " + "call for GetSkuAttribute ");
                    }

                    public void onFailure(Call<GetSkuAttribute> call, Throwable t) {
                        Utils.dismissProgressDialog(progressDialog);
                        Utils.showToast(AllSKUsFragment.this.getActivity(), ConstantsA.NO_INTERNET_CONNECTION);
                    }
                });

            }

            private void networkcall_for_sku_info(final String sku_id, final String authToken, final String modificationtype) {



                Call<GetSkuInfo> call = methods.getSkuInfo(new IM_GetSkuInfo(authToken, sku_id));
                Log.d("url", "url=" + call.request().url().toString());
                call.enqueue(new Callback<GetSkuInfo>() {
                    public void onResponse(Call<GetSkuInfo> call, Response<GetSkuInfo> response) {
                        Log.d("Response", "" + response.code());
                        Log.d("respones", "" + response);
                        if (response.isSuccessful()) {

                            GetSkuInfo getSkuInfo = (GetSkuInfo) response.body();
                            if (getSkuInfo.getApiStatus().intValue() == 1) {


                                SkuIds skuId_Info = getSkuInfo.getSkuIds();
                                ContentValues skuValues = new ContentValues();
                                skuValues.put(ConstantsA.KEY_SKU_ID, skuId_Info.getSkuId());
                                skuValues.put("sku_name", skuId_Info.getSkuName());
                                skuValues.put("sku_price", skuId_Info.getSkuPrice());
                                skuValues.put("description", skuId_Info.getSkuDescription());
                                skuValues.put("sku_category", skuId_Info.getSkuCategory());
                                skuValues.put("sku_sub_category", skuId_Info.getSkuSubCategory());
                                skuValues.put("sku_category_description", skuId_Info.getCategory_description());
                                skuValues.put("sku_sub_category_description", skuId_Info.getSub_category_description());
                                skuValues.put("created_date", skuId_Info.getSkuCreatedOn());
                                skuValues.put("modified_date", skuId_Info.getSkuModifiedOn());
                                skuValues.put("created_by", skuId_Info.getSkuCreatedBy());
                                skuValues.put("modified_by", skuId_Info.getSkuModifiedBy());
                                skuValues.put("new_sku", skuId_Info.getSkuNewFlag());
                                skuValues.put("upload_status", Integer.valueOf(1));

                                if (modificationtype == null) {


                                    AllSKUsFragment.this.sqLiteDatabase.insert(Constants.TBL_SKU, null, skuValues);
                                    networkcall_for_sku_ThumbImage(skuId_Info.getSkuId(), authToken, modificationtype);

                                } else if (modificationtype.equalsIgnoreCase(Constants.MOD_TYPE_SKU_INFO)) {

                                    if(DbUtils.isSKUPresentInDb(sku_id)){

                                        a--;
                                        AllSKUsFragment.this.sqLiteDatabase.update(Constants.TBL_SKU, skuValues, " sku_id = ? ", new String[]{skuId_Info.getSkuId()});
                                    }else {

                                        AllSKUsFragment.this.sqLiteDatabase.insert(Constants.TBL_SKU, null, skuValues);
                                        networkcall_for_sku_ThumbImage(skuId_Info.getSkuId(), authToken, modificationtype);

                                    }


                                } else if (modificationtype.equalsIgnoreCase(Constants.MOD_TYPE_SKU_ATTR)) {

                                    if(DbUtils.isSKUPresentInDb(sku_id)){
                                        networkcall_for_sku_ThumbImage(sku_id, authToken, modificationtype);
                                    }else {

                                        AllSKUsFragment.this.sqLiteDatabase.insert(Constants.TBL_SKU, null, skuValues);
                                        networkcall_for_sku_ThumbImage(skuId_Info.getSkuId(), authToken, modificationtype);
                                    }
                                } else if (modificationtype.equalsIgnoreCase(Constants.MOD_TYPE_SKU_MULTIMEDIA)) {

                                    if(DbUtils.isSKUPresentInDb(sku_id)){
                                        networkcall_for_sku_ThumbImage(sku_id, authToken, modificationtype);
                                    }else {

                                        AllSKUsFragment.this.sqLiteDatabase.insert(Constants.TBL_SKU, null, skuValues);
                                        networkcall_for_sku_ThumbImage(skuId_Info.getSkuId(), authToken, modificationtype);
                                    }
                                } else if(modificationtype.equalsIgnoreCase(Constants.MOD_TYPE_SKU_MULTIMEDIA_AND_INFO)|| modificationtype.equalsIgnoreCase(Constants.MOD_TYPE_SKU_INFO_AND_MULTIMEDIA)){

                                    if(DbUtils.isSKUPresentInDb(sku_id)){
                                        AllSKUsFragment.this.sqLiteDatabase.update(Constants.TBL_SKU, skuValues, " sku_id = ? ", new String[]{skuId_Info.getSkuId()});
                                        networkcall_for_sku_ThumbImage(sku_id, authToken, modificationtype);
                                    }else {

                                        AllSKUsFragment.this.sqLiteDatabase.insert(Constants.TBL_SKU, null, skuValues);
                                        networkcall_for_sku_ThumbImage(skuId_Info.getSkuId(), authToken, modificationtype);
                                    }

                                }else if(modificationtype.equalsIgnoreCase(Constants.MOD_TYPE_SKU_INFO_AND_ATTR)|| modificationtype.equalsIgnoreCase(Constants.MOD_TYPE_SKU_ATTR_AND_INFO)){

                                    if(DbUtils.isSKUPresentInDb(sku_id)){
                                        AllSKUsFragment.this.sqLiteDatabase.update(Constants.TBL_SKU, skuValues, " sku_id = ? ", new String[]{skuId_Info.getSkuId()});
                                        networkcall_for_sku_ThumbImage(sku_id, authToken, modificationtype);
                                    }else {

                                        AllSKUsFragment.this.sqLiteDatabase.insert(Constants.TBL_SKU, null, skuValues);
                                        networkcall_for_sku_ThumbImage(skuId_Info.getSkuId(), authToken, modificationtype);
                                    }

                                }else if(modificationtype.equalsIgnoreCase(Constants.MOD_TYPE_SKU_MULTIMEDIA_AND_ATTR)|| modificationtype.equalsIgnoreCase(Constants.MOD_TYPE_SKU_ATTR_AND_MULTIMEDIA)){
                                    if(DbUtils.isSKUPresentInDb(sku_id)){

                                        networkcall_for_sku_ThumbImage(sku_id, authToken, modificationtype);

                                    }else {

                                        AllSKUsFragment.this.sqLiteDatabase.insert(Constants.TBL_SKU, null, skuValues);
                                        networkcall_for_sku_ThumbImage(skuId_Info.getSkuId(), authToken, modificationtype);
                                    }
                                }else if(modificationtype.contains(Constants.MOD_TYPE_SKU_ATTR) && modificationtype.contains(Constants.MOD_TYPE_SKU_MULTIMEDIA) && modificationtype.contains(Constants.MOD_TYPE_SKU_INFO) ){
                                    if(DbUtils.isSKUPresentInDb(sku_id)){
                                        AllSKUsFragment.this.sqLiteDatabase.update(Constants.TBL_SKU, skuValues, " sku_id = ? ", new String[]{skuId_Info.getSkuId()});
                                        networkcall_for_sku_ThumbImage(sku_id, authToken, modificationtype);
                                    }else {

                                        AllSKUsFragment.this.sqLiteDatabase.insert(Constants.TBL_SKU, null, skuValues);
                                        networkcall_for_sku_ThumbImage(skuId_Info.getSkuId(), authToken, modificationtype);
                                    }
                                }


                                return;
                            }
                            return;
                        }else {

                            a--;
                        }
                       // Utils.dismissProgressDialog(progressDialog);
                        Utils.showToast(AllSKUsFragment.this.getActivity(), "NEW : Unsuccessful " +
                                "api call for getskuinfo ");
                    }

                    private void networkcall_for_sku_ThumbImage(final String sku_id, final String
                            authToken, final String modificationtype) {
                        Call<GetSkuThumbImage> call = methods.getSkuThumbImage(new IM_GetSkuInfo(authToken, sku_id));



                        Log.d("url", "url=" + call.request().url().toString());

                        call.enqueue(new Callback<GetSkuThumbImage>() {
                            public void onResponse(Call<GetSkuThumbImage> call,
                                                   Response<GetSkuThumbImage> response) {
                                Log.d("Response", "" + response.code());
                                Log.d("respones", "" + response);
                                if (response.isSuccessful()) {
                                  //  Utils.dismissProgressDialog(progressDialog);
                                    GetSkuThumbImage getSkuThumbImage = (GetSkuThumbImage)
                                            response.body();
                                    if (getSkuThumbImage.getApiStatus().intValue() == 1) {

                                        if (modificationtype == null || modificationtype.equalsIgnoreCase(Constants.MOD_TYPE_SKU_MULTIMEDIA) || modificationtype.equalsIgnoreCase(Constants.MOD_TYPE_SKU_INFO)) {


                                            for (GetSkuThumbImage.SkuMedium skuMedia : getSkuThumbImage.getSkuMedia()) {
                                                String selection = "sku_id = ?";
                                                String[] selectionArgs = new String[]{skuMedia.getSkuId()};
                                                if (skuMedia.getMediaType().equalsIgnoreCase("photo")) {

                                                    if (skuMedia.getMediaFile() != null && skuMedia.getMediaFile().length() > 2) {


                                                        String sku_photourl_from_server = skuMedia.getMediaFile();
                                                        ContentValues contentValues = new ContentValues();
                                                        contentValues.put("sku_photo_source", ApiUrl.BASEURL_SKUTHUMBIMAGE + DashboardActivity.substringAfterLastSeparator(sku_photourl_from_server, "/"));
                                                        AllSKUsFragment.this.sqLiteDatabase.update(Constants.TBL_SKU, contentValues, selection, selectionArgs);

                                                    } else {
                                                        ContentValues contentValues = new ContentValues();
                                                        contentValues.put("sku_photo_source", NOT_PRESENT);
                                                        AllSKUsFragment.this.sqLiteDatabase.update(Constants.TBL_SKU, contentValues, selection, selectionArgs);

                                                    }
                                                } else if (skuMedia.getMediaType().equalsIgnoreCase
                                                        ("catalogue")) {

                                                    if (skuMedia.getMediaFile() != null && skuMedia.getMediaFile().length() > 2) {

                                                        String sku_photourl_from_server = skuMedia.getMediaFile();
                                                        ContentValues contentValues = new ContentValues();
                                                        contentValues.put("sku_catalogue_source", ApiUrl.BASEURL_SKUTHUMBIMAGE + DashboardActivity.substringAfterLastSeparator(sku_photourl_from_server, "/"));
                                                        AllSKUsFragment.this.sqLiteDatabase.update(Constants.TBL_SKU, contentValues, selection, selectionArgs);

                                                    } else {
                                                        ContentValues contentValues = new ContentValues();
                                                        contentValues.put("sku_catalogue_source", NOT_PRESENT);
                                                        AllSKUsFragment.this.sqLiteDatabase.update(Constants.TBL_SKU, contentValues, selection, selectionArgs);

                                                    }


                                                } else if (skuMedia.getMediaType().equalsIgnoreCase
                                                        ("video")) {

                                                    if (skuMedia.getMediaFile() != null && skuMedia.getMediaFile().length() > 2) {


                                                        String sku_photourl_from_server = skuMedia.getMediaFile();
                                                        ContentValues contentValues = new ContentValues();
                                                        contentValues.put("sku_video_source", ApiUrl.BASEURL_SKUTHUMBIMAGE + DashboardActivity.substringAfterLastSeparator(sku_photourl_from_server, "/"));
                                                        AllSKUsFragment.this.sqLiteDatabase.update(Constants.TBL_SKU, contentValues, selection, selectionArgs);

                                                    } else {
                                                        ContentValues contentValues = new ContentValues();
                                                        contentValues.put("sku_video_source", NOT_PRESENT);
                                                        AllSKUsFragment.this.sqLiteDatabase.update(Constants.TBL_SKU, contentValues, selection, selectionArgs);

                                                    }

                                                } else {

                                                }
                                            }

                                            if(is_Attribute){


                                                networkcall_for_sku_Attr(sku_id, authToken, modificationtype);
                                            }


                                        } else if (modificationtype.equalsIgnoreCase(Constants.MOD_TYPE_SKU_ATTR)) {

                                            if(is_Attribute){

                                                networkcall_for_sku_Attr(sku_id, authToken, modificationtype);
                                            }

                                        } else if(modificationtype.equalsIgnoreCase(MOD_TYPE_SKU_MULTIMEDIA_AND_INFO)|| modificationtype.equalsIgnoreCase(Constants.MOD_TYPE_SKU_INFO_AND_MULTIMEDIA)){


                                            for (GetSkuThumbImage.SkuMedium skuMedia : getSkuThumbImage.getSkuMedia()) {
                                                String selection = "sku_id = ?";
                                                String[] selectionArgs = new String[]{skuMedia.getSkuId()};
                                                if (skuMedia.getMediaType().equalsIgnoreCase("photo")) {

                                                    if (skuMedia.getMediaFile() != null && skuMedia.getMediaFile().length() > 2) {


                                                        String sku_photourl_from_server = skuMedia.getMediaFile();
                                                        ContentValues contentValues = new ContentValues();
                                                        contentValues.put("sku_photo_source", ApiUrl.BASEURL_SKUTHUMBIMAGE + DashboardActivity.substringAfterLastSeparator(sku_photourl_from_server, "/"));
                                                        AllSKUsFragment.this.sqLiteDatabase.update(Constants.TBL_SKU, contentValues, selection, selectionArgs);

                                                    } else {
                                                        ContentValues contentValues = new ContentValues();
                                                        contentValues.put("sku_photo_source", NOT_PRESENT);
                                                        AllSKUsFragment.this.sqLiteDatabase.update(Constants.TBL_SKU, contentValues, selection, selectionArgs);

                                                    }
                                                } else if (skuMedia.getMediaType().equalsIgnoreCase
                                                        ("catalogue")) {

                                                    if (skuMedia.getMediaFile() != null && skuMedia.getMediaFile().length() > 2) {

                                                        String sku_photourl_from_server = skuMedia.getMediaFile();
                                                        ContentValues contentValues = new ContentValues();
                                                        contentValues.put("sku_catalogue_source", ApiUrl.BASEURL_SKUTHUMBIMAGE + DashboardActivity.substringAfterLastSeparator(sku_photourl_from_server, "/"));
                                                        AllSKUsFragment.this.sqLiteDatabase.update(Constants.TBL_SKU, contentValues, selection, selectionArgs);

                                                    } else {
                                                        ContentValues contentValues = new ContentValues();
                                                        contentValues.put("sku_catalogue_source", NOT_PRESENT);
                                                        AllSKUsFragment.this.sqLiteDatabase.update(Constants.TBL_SKU, contentValues, selection, selectionArgs);

                                                    }


                                                } else if (skuMedia.getMediaType().equalsIgnoreCase
                                                        ("video")) {

                                                    if (skuMedia.getMediaFile() != null && skuMedia.getMediaFile().length() > 2) {


                                                        String sku_photourl_from_server = skuMedia.getMediaFile();
                                                        ContentValues contentValues = new ContentValues();
                                                        contentValues.put("sku_video_source", ApiUrl.BASEURL_SKUTHUMBIMAGE + DashboardActivity.substringAfterLastSeparator(sku_photourl_from_server, "/"));
                                                        AllSKUsFragment.this.sqLiteDatabase.update(Constants.TBL_SKU, contentValues, selection, selectionArgs);

                                                    } else {
                                                        ContentValues contentValues = new ContentValues();
                                                        contentValues.put("sku_video_source", NOT_PRESENT);
                                                        AllSKUsFragment.this.sqLiteDatabase.update(Constants.TBL_SKU, contentValues, selection, selectionArgs);

                                                    }

                                                } else {

                                                }
                                            }

                                        }else if(modificationtype.equalsIgnoreCase(Constants.MOD_TYPE_SKU_INFO_AND_ATTR)|| modificationtype.equalsIgnoreCase(Constants.MOD_TYPE_SKU_ATTR_AND_INFO)){

                                            if(is_Attribute){

                                                networkcall_for_sku_Attr(sku_id, authToken, modificationtype);
                                            }

                                        }else if(modificationtype.equalsIgnoreCase(Constants.MOD_TYPE_SKU_MULTIMEDIA_AND_ATTR)|| modificationtype.equalsIgnoreCase(Constants.MOD_TYPE_SKU_ATTR_AND_MULTIMEDIA)){



                                            for (GetSkuThumbImage.SkuMedium skuMedia : getSkuThumbImage.getSkuMedia()) {
                                                String selection = "sku_id = ?";
                                                String[] selectionArgs = new String[]{skuMedia.getSkuId()};
                                                if (skuMedia.getMediaType().equalsIgnoreCase("photo")) {

                                                    if (skuMedia.getMediaFile() != null && skuMedia.getMediaFile().length() > 2) {


                                                        String sku_photourl_from_server = skuMedia.getMediaFile();
                                                        ContentValues contentValues = new ContentValues();
                                                        contentValues.put("sku_photo_source", ApiUrl.BASEURL_SKUTHUMBIMAGE + DashboardActivity.substringAfterLastSeparator(sku_photourl_from_server, "/"));
                                                        AllSKUsFragment.this.sqLiteDatabase.update(Constants.TBL_SKU, contentValues, selection, selectionArgs);

                                                    } else {
                                                        ContentValues contentValues = new ContentValues();
                                                        contentValues.put("sku_photo_source", NOT_PRESENT);
                                                        AllSKUsFragment.this.sqLiteDatabase.update(Constants.TBL_SKU, contentValues, selection, selectionArgs);

                                                    }
                                                } else if (skuMedia.getMediaType().equalsIgnoreCase
                                                        ("catalogue")) {

                                                    if (skuMedia.getMediaFile() != null && skuMedia.getMediaFile().length() > 2) {

                                                        String sku_photourl_from_server = skuMedia.getMediaFile();
                                                        ContentValues contentValues = new ContentValues();
                                                        contentValues.put("sku_catalogue_source", ApiUrl.BASEURL_SKUTHUMBIMAGE + DashboardActivity.substringAfterLastSeparator(sku_photourl_from_server, "/"));
                                                        AllSKUsFragment.this.sqLiteDatabase.update(Constants.TBL_SKU, contentValues, selection, selectionArgs);

                                                    } else {
                                                        ContentValues contentValues = new ContentValues();
                                                        contentValues.put("sku_catalogue_source", NOT_PRESENT);
                                                        AllSKUsFragment.this.sqLiteDatabase.update(Constants.TBL_SKU, contentValues, selection, selectionArgs);

                                                    }


                                                } else if (skuMedia.getMediaType().equalsIgnoreCase
                                                        ("video")) {

                                                    if (skuMedia.getMediaFile() != null && skuMedia.getMediaFile().length() > 2) {


                                                        String sku_photourl_from_server = skuMedia.getMediaFile();
                                                        ContentValues contentValues = new ContentValues();
                                                        contentValues.put("sku_video_source", ApiUrl.BASEURL_SKUTHUMBIMAGE + DashboardActivity.substringAfterLastSeparator(sku_photourl_from_server, "/"));
                                                        AllSKUsFragment.this.sqLiteDatabase.update(Constants.TBL_SKU, contentValues, selection, selectionArgs);

                                                    } else {
                                                        ContentValues contentValues = new ContentValues();
                                                        contentValues.put("sku_video_source", NOT_PRESENT);
                                                        AllSKUsFragment.this.sqLiteDatabase.update(Constants.TBL_SKU, contentValues, selection, selectionArgs);

                                                    }

                                                } else {

                                                }
                                            }

                                            if(is_Attribute){

                                                networkcall_for_sku_Attr(sku_id, authToken, modificationtype);
                                            }
                                        }else if(modificationtype.contains(Constants.MOD_TYPE_SKU_ATTR) && modificationtype.contains(Constants.MOD_TYPE_SKU_MULTIMEDIA) && modificationtype.contains(Constants.MOD_TYPE_SKU_INFO) ){



                                            for (GetSkuThumbImage.SkuMedium skuMedia : getSkuThumbImage.getSkuMedia()) {
                                                String selection = "sku_id = ?";
                                                String[] selectionArgs = new String[]{skuMedia.getSkuId()};
                                                if (skuMedia.getMediaType().equalsIgnoreCase("photo")) {

                                                    if (skuMedia.getMediaFile() != null && skuMedia.getMediaFile().length() > 2) {


                                                        String sku_photourl_from_server = skuMedia.getMediaFile();
                                                        ContentValues contentValues = new ContentValues();
                                                        contentValues.put("sku_photo_source", ApiUrl.BASEURL_SKUTHUMBIMAGE + DashboardActivity.substringAfterLastSeparator(sku_photourl_from_server, "/"));
                                                        AllSKUsFragment.this.sqLiteDatabase.update(Constants.TBL_SKU, contentValues, selection, selectionArgs);

                                                    } else {
                                                        ContentValues contentValues = new ContentValues();
                                                        contentValues.put("sku_photo_source", NOT_PRESENT);
                                                        AllSKUsFragment.this.sqLiteDatabase.update(Constants.TBL_SKU, contentValues, selection, selectionArgs);

                                                    }
                                                } else if (skuMedia.getMediaType().equalsIgnoreCase
                                                        ("catalogue")) {

                                                    if (skuMedia.getMediaFile() != null && skuMedia.getMediaFile().length() > 2) {

                                                        String sku_photourl_from_server = skuMedia.getMediaFile();
                                                        ContentValues contentValues = new ContentValues();
                                                        contentValues.put("sku_catalogue_source", ApiUrl.BASEURL_SKUTHUMBIMAGE + DashboardActivity.substringAfterLastSeparator(sku_photourl_from_server, "/"));
                                                        AllSKUsFragment.this.sqLiteDatabase.update(Constants.TBL_SKU, contentValues, selection, selectionArgs);

                                                    } else {
                                                        ContentValues contentValues = new ContentValues();
                                                        contentValues.put("sku_catalogue_source", NOT_PRESENT);
                                                        AllSKUsFragment.this.sqLiteDatabase.update(Constants.TBL_SKU, contentValues, selection, selectionArgs);

                                                    }


                                                } else if (skuMedia.getMediaType().equalsIgnoreCase
                                                        ("video")) {

                                                    if (skuMedia.getMediaFile() != null && skuMedia.getMediaFile().length() > 2) {


                                                        String sku_photourl_from_server = skuMedia.getMediaFile();
                                                        ContentValues contentValues = new ContentValues();
                                                        contentValues.put("sku_video_source", ApiUrl.BASEURL_SKUTHUMBIMAGE + DashboardActivity.substringAfterLastSeparator(sku_photourl_from_server, "/"));
                                                        AllSKUsFragment.this.sqLiteDatabase.update(Constants.TBL_SKU, contentValues, selection, selectionArgs);

                                                    } else {
                                                        ContentValues contentValues = new ContentValues();
                                                        contentValues.put("sku_video_source", NOT_PRESENT);
                                                        AllSKUsFragment.this.sqLiteDatabase.update(Constants.TBL_SKU, contentValues, selection, selectionArgs);

                                                    }

                                                } else {

                                                }
                                            }

                                            if(is_Attribute){

                                                networkcall_for_sku_Attr(sku_id, authToken, modificationtype);
                                            }

                                        }
                                      //  Utils.dismissProgressDialog(progressDialog);

                                        AllSKUsFragment allSKUsFragment = AllSKUsFragment.this;
                                        b++;

                                        Log.d("ab value",a+" :"+b);

                                        if(a==b){

                                            Utils.dismissProgressDialog(progressDialog);

                                            /// search adapter in activity
                                            ((SkuListByGenreActivity) getActivity()).setAdapterForSearch();

                                        }

                                        return;
                                    }
                                    return;
                                }else {
                                    a--;

                                    if(a==b){

                                        Utils.dismissProgressDialog(progressDialog);

                                        /// search adapter in activity
                                        ((SkuListByGenreActivity) getActivity()).setAdapterForSearch();

                                    }
                                }
                               // Utils.dismissProgressDialog(progressDialog);
                              //  Utils.showToast(AllSKUsFragment.this.getActivity(), "Unsuccessful" + " api call for getskuinfo_thumbimage ");
                            }

                            public void onFailure(Call<GetSkuThumbImage> call, Throwable t) {
                                a--;
                                Utils.dismissProgressDialog(progressDialog);
                                Utils.showToast(AllSKUsFragment.this.getActivity(), ConstantsA
                                        .NO_INTERNET_CONNECTION);
                            }
                        });
                    }

                    public void onFailure(Call<GetSkuInfo> call, Throwable t) {
                        a--;
                        Utils.dismissProgressDialog(progressDialog);
                        Utils.showToast(AllSKUsFragment.this.getActivity(), ConstantsA.NO_INTERNET_CONNECTION);
                    }
                });
            }

            @Override
            public void onFailure(Call<GetSkuListAfter> call, Throwable t) {
                Utils.dismissProgressDialog(progressDialog);
                Utils.showToast(AllSKUsFragment.this.getActivity(), ConstantsA
                        .NO_INTERNET_CONNECTION);
            }
        });

    }

    private void delete_sku_row_from_db(String sku_id)
    {
        String selection = "sku_id = ?";
        String[] selectionArgs = new String[]{sku_id};
        Cursor cursor = this.sqLiteDatabase.rawQuery("SELECT attribute_id FROM " + Constants.TBL_SKU_ATTRIBUTE_MAPPING + " WHERE sku_id = ?", selectionArgs);
        while (cursor.moveToNext())
        {
            String[] selectionArgs1 = new String[]{cursor.getString(cursor.getColumnIndexOrThrow("attribute_id"))};
            this.sqLiteDatabase.delete(Constants.TBL_GLOBAL_ATTRIBUTES, "attribute_id = ?", selectionArgs1);
        }
        this.sqLiteDatabase.delete(Constants.TBL_SKU_ATTRIBUTE_MAPPING, selection, selectionArgs);
        this.sqLiteDatabase.delete(Constants.TBL_SKU, selection, selectionArgs);
    }
}
