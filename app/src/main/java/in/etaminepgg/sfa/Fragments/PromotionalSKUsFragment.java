package in.etaminepgg.sfa.Fragments;


import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.res.Resources;
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

import in.etaminepgg.sfa.Activities.SkuListByGenreActivity;
import in.etaminepgg.sfa.Adapters.NewSKUsAdapter;
import in.etaminepgg.sfa.Adapters.PromotionalSKUsAdapter;
import in.etaminepgg.sfa.InputModel_For_Network.IM_GetSkuInfo;
import in.etaminepgg.sfa.InputModel_For_Network.IM_GetSkuListAfter;
import in.etaminepgg.sfa.Models.GetSkuAttribute;
import in.etaminepgg.sfa.Models.GetSkuInfo;
import in.etaminepgg.sfa.Models.GetSkuListAfter;
import in.etaminepgg.sfa.Models.GetSkuThumbImage;
import in.etaminepgg.sfa.Network.API_Call_Retrofit;
import in.etaminepgg.sfa.Network.ApiUrl;
import in.etaminepgg.sfa.Network.Apimethods;
import in.etaminepgg.sfa.R;
import in.etaminepgg.sfa.Utilities.ConstantsA;
import in.etaminepgg.sfa.Utilities.DbUtils;
import in.etaminepgg.sfa.Utilities.MyDb;
import in.etaminepgg.sfa.Utilities.MySharedPrefrencesData;
import in.etaminepgg.sfa.Utilities.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static in.etaminepgg.sfa.Activities.DashboardActivity.substringAfterLastSeparator;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_GLOBAL_ATTRIBUTES;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SKU;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SKU_ATTRIBUTE_MAPPING;
import static in.etaminepgg.sfa.Utilities.Constants.dbFileFullPath;
import static in.etaminepgg.sfa.Utilities.ConstantsA.NEW_SKUs_TAB;

/**
 * A simple {@link Fragment} subclass.
 */
public class PromotionalSKUsFragment extends Fragment
{
    RecyclerView promotionalSKUs_RecyclerView;

    String type="2";

    int valueFromOpenDatabase;
    SQLiteDatabase sqLiteDatabase;


    View layout;

    public PromotionalSKUsFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        layout = inflater.inflate(R.layout.fragment_promotional_skus, container, false);

        valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        if(Utils.isNetworkConnected(getActivity())){

           // networkcall_for_getSKUlistAfter(new MySharedPrefrencesData().getEmployee_AuthKey(getActivity()));

        }else {
            setAdapter(layout);
        }


        return layout;
    }

    private void setAdapter(View layout)
    {

        String SQL_SELECT_PROMO_SKUs = "select sku_id, sku_name, sku_price, sku_category, sku_photo_source from " + TBL_SKU + " WHERE promotional_sku = ?" + " ;";
        String[] selectionArgs = new String[]{"1"};

        PromotionalSKUsAdapter promotionalSKUsAdapter = new PromotionalSKUsAdapter(DbUtils.getSkuList(SQL_SELECT_PROMO_SKUs, selectionArgs));

        promotionalSKUs_RecyclerView = (RecyclerView) layout.findViewById(R.id.promotionalSKUs_RecyclerView);
        promotionalSKUs_RecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        promotionalSKUs_RecyclerView.setItemAnimator(new DefaultItemAnimator());
        promotionalSKUs_RecyclerView.setAdapter(promotionalSKUsAdapter);
    }

   /* private void networkcall_for_getSKUlistAfter(final String authToken)
    {
        final ProgressDialog progressDialog=new ProgressDialog(getActivity());
        Utils.startProgressDialog(getActivity(),progressDialog);

        Resources resources = getResources();
        final String intentExtraKey_TabToShow = resources.getString(R.string.key_tab_to_show);

        final Apimethods methods = API_Call_Retrofit.getretrofit(getActivity()).create(Apimethods.class);

        IM_GetSkuListAfter IM_getSkuListAfter = new IM_GetSkuListAfter(authToken, type, "2016-05-25");

        Call<GetSkuListAfter> call = methods.getSkuListAfter(IM_getSkuListAfter);
        Log.d("url", "url=" + call.request().url().toString());

        call.enqueue(new Callback<GetSkuListAfter>()
        {
            @Override
            public void onResponse(Call<GetSkuListAfter> call, Response<GetSkuListAfter> response)
            {
                int statusCode = response.code();
                Log.d("Response", "" + statusCode);
                Log.d("respones", "" + response);
                if(response.isSuccessful())
                {
                    GetSkuListAfter getSkuListAfter = response.body();
                    if(getSkuListAfter.getApiStatus() == 1)
                    {


                        for(GetSkuListAfter.SkuInfo skuInfo : getSkuListAfter.getSkuIds())
                        {

                            if(!DbUtils.isSKUPresentInDb(skuInfo.getSku_id())){

                                networkcall_for_sku_info(skuInfo.getSku_id(), authToken);
                                networkcall_for_sku_Attr(skuInfo.getSku_id(), authToken);
                            }

                        }
                        setAdapter(layout);

                        Utils.dismissProgressDialog(progressDialog);

                    }
                }
                else
                {
                    Utils.dismissProgressDialog(progressDialog);
                    Utils.showToast(getActivity(), "Unsuccessful api call for getSkuListAfter ");
                }

            }

            private void networkcall_for_sku_ThumbImage(String sku_id, String authToken)
            {
                IM_GetSkuInfo im_getSkuInfo = new IM_GetSkuInfo(authToken, sku_id);

                Call<GetSkuThumbImage> call = methods.getSkuThumbImage(im_getSkuInfo);
                Log.d("url", "url=" + call.request().url().toString());

                call.enqueue(new Callback<GetSkuThumbImage>()
                {
                    @Override
                    public void onResponse(Call<GetSkuThumbImage> call, Response<GetSkuThumbImage> response)
                    {
                        int statusCode = response.code();
                        Log.d("Response", "" + statusCode);
                        Log.d("respones", "" + response);
                        if(response.isSuccessful())
                        {
                            GetSkuThumbImage getSkuThumbImage = response.body();
                            if(getSkuThumbImage.getApiStatus() == 1)
                            {

                                for(GetSkuThumbImage.SkuMedium skuMedia : getSkuThumbImage.getSkuMedia())
                                {
                                    String selection = "sku_id = ?";
                                    String[] selectionArgs = {skuMedia.getSkuId()};

                                    String sku_photourl_from_server = skuMedia.getMediaFile();

                                    ContentValues contentValues = new ContentValues();

                                    String log_photo_url = substringAfterLastSeparator(sku_photourl_from_server, "/");

                                    contentValues.put("sku_photo_source", ApiUrl.BASEURL_SKUTHUMBIMAGE + log_photo_url);

                                    sqLiteDatabase.update(TBL_SKU, contentValues, selection, selectionArgs);

                                }

                            }
                        }
                        else
                        {
                            Utils.dismissProgressDialog(progressDialog);
                            Utils.showToast(getActivity(), "Unsuccessful api call for getskuinfo_thumbimage ");
                        }
                    }

                    @Override
                    public void onFailure(Call<GetSkuThumbImage> call, Throwable t)
                    {

                        Utils.dismissProgressDialog(progressDialog);
                        Utils.showToast(getActivity(), ConstantsA.NO_INTERNET_CONNECTION);
                    }
                });

            }

            private void networkcall_for_sku_Attr(String sku_id, String authToken)
            {
                IM_GetSkuInfo im_getSkuInfo = new IM_GetSkuInfo(authToken, sku_id);

                Call<GetSkuAttribute> call = methods.getSkuAttr(im_getSkuInfo);
                Log.d("url", "url=" + call.request().url().toString());

                call.enqueue(new Callback<GetSkuAttribute>()
                {
                    @Override
                    public void onResponse(Call<GetSkuAttribute> call, Response<GetSkuAttribute> response)
                    {
                        int statusCode = response.code();
                        Log.d("Response", "" + statusCode);
                        Log.d("respones", "" + response);
                        if(response.isSuccessful())
                        {
                            GetSkuAttribute getSkuAttribute = response.body();
                            if(getSkuAttribute.getApiStatus() == 1)
                            {
                                for(int j = 0; j < getSkuAttribute.getSkuAttr().size(); j++)
                                {
                                    GetSkuAttribute.SkuAttr skuAttr = getSkuAttribute.getSkuAttr().get(j);

                                    ContentValues skuAttrMappingValues = new ContentValues();
                                    skuAttrMappingValues.put("sku_id", skuAttr.getSkuId());
                                    skuAttrMappingValues.put("attribute_id", skuAttr.getGlobalAttributeId());
                                    skuAttrMappingValues.put("upload_status", 0);
                                    sqLiteDatabase.insert(TBL_SKU_ATTRIBUTE_MAPPING, null, skuAttrMappingValues);


                                    String attrvalue_str = skuAttr.getAttributeValue();

                                    String attributevalue_array[] = attrvalue_str.split(",");

                                    StringBuilder builder = new StringBuilder();
                                    for(String s : attributevalue_array)
                                    {
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
                                    globalAttributeValues.put("upload_status", 0);
                                    sqLiteDatabase.insert(TBL_GLOBAL_ATTRIBUTES, null, globalAttributeValues);

                                }

                            }
                        }
                        else
                        {
                            Utils.dismissProgressDialog(progressDialog);
                            Utils.showToast(getActivity(), "Unsuccessful api call for GetSkuAttribute ");
                        }
                    }

                    @Override
                    public void onFailure(Call<GetSkuAttribute> call, Throwable t)
                    {
                        Utils.dismissProgressDialog(progressDialog);
                        Utils.showToast(getActivity(), ConstantsA.NO_INTERNET_CONNECTION);
                    }
                });
            }

            private void networkcall_for_sku_info(final String sku_id, final String authToken)
            {
                IM_GetSkuInfo im_getSkuInfo = new IM_GetSkuInfo(authToken, sku_id);

                Call<GetSkuInfo> call = methods.getSkuInfo(im_getSkuInfo);
                Log.d("url", "url=" + call.request().url().toString());

                call.enqueue(new Callback<GetSkuInfo>()
                {
                    @Override
                    public void onResponse(Call<GetSkuInfo> call, Response<GetSkuInfo> response)
                    {
                        int statusCode = response.code();
                        Log.d("Response", "" + statusCode);
                        Log.d("respones", "" + response);
                        if(response.isSuccessful())
                        {
                            GetSkuInfo getSkuInfo = response.body();
                            if(getSkuInfo.getApiStatus() == 1)
                            {
                                GetSkuInfo.SkuIds skuId_Info = getSkuInfo.getSkuIds();
                                ContentValues skuValues = new ContentValues();
                                skuValues.put("sku_id", skuId_Info.getSkuId());
                                skuValues.put("sku_name", skuId_Info.getSkuName());
                                skuValues.put("sku_price", skuId_Info.getSkuPrice());
                                skuValues.put("description", skuId_Info.getSkuDescription());
                                skuValues.put("sku_category", skuId_Info.getSkuCategory());
                                skuValues.put("sku_sub_category", skuId_Info.getSkuSubCategory());
                                skuValues.put("sku_category_description", skuId_Info.getCategory_description());
                                skuValues.put("sku_sub_category_description", skuId_Info.getSub_category_description());
                                if(type.equalsIgnoreCase("1"))
                                {

                                }
                                else if(type.equalsIgnoreCase("2"))
                                {
                                    skuValues.put("promotional_sku", 1);
                                }
                                else if(type.equalsIgnoreCase("3"))
                                {

                                }
                                else if(type.equalsIgnoreCase("4"))
                                {
                                    skuValues.put("new_sku", 1);
                                }


                                skuValues.put("upload_status", 0);
                                sqLiteDatabase.insert(TBL_SKU, null, skuValues);

                                networkcall_for_sku_ThumbImage(skuId_Info.getSkuId(), authToken);

                            }
                        }
                        else
                        {
                            Utils.dismissProgressDialog(progressDialog);
                            Utils.showToast(getActivity(), "Unsuccessful api call for getskuinfo ");
                        }
                    }

                    @Override
                    public void onFailure(Call<GetSkuInfo> call, Throwable t)
                    {
                        Utils.dismissProgressDialog(progressDialog);
                        Utils.showToast(getActivity(), ConstantsA.NO_INTERNET_CONNECTION);
                    }
                });

            }

            @Override
            public void onFailure(Call<GetSkuListAfter> call, Throwable t)
            {
                Utils.dismissProgressDialog(progressDialog);
                Utils.showToast(getActivity(), ConstantsA.NO_INTERNET_CONNECTION);

            }
        });


    }*/

}
