package in.etaminepgg.sfa.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.etaminepgg.sfa.Adapters.NewSKUsAdapter;
import in.etaminepgg.sfa.Adapters.PromotionalSKUsAdapter;
import in.etaminepgg.sfa.R;
import in.etaminepgg.sfa.Utilities.DbUtils;

import static in.etaminepgg.sfa.Utilities.Constants.TBL_SKU;

/**
 * A simple {@link Fragment} subclass.
 */
public class PromotionalSKUsFragment extends Fragment
{
    RecyclerView promotionalSKUs_RecyclerView;

    public PromotionalSKUsFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        String SQL_SELECT_PROMO_SKUs = "select sku_id, sku_name, sku_price, sku_category from " + TBL_SKU + " WHERE promotional_sku = ?" + " ;";
        String[] selectionArgs = new String[]{"1"};

        PromotionalSKUsAdapter promotionalSKUsAdapter = new PromotionalSKUsAdapter(DbUtils.getSkuList(SQL_SELECT_PROMO_SKUs, selectionArgs));

        View layout =  inflater.inflate(R.layout.fragment_promotional_skus, container, false);
        promotionalSKUs_RecyclerView = (RecyclerView)layout.findViewById(R.id.promotionalSKUs_RecyclerView);
        promotionalSKUs_RecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        promotionalSKUs_RecyclerView.setItemAnimator(new DefaultItemAnimator());
        promotionalSKUs_RecyclerView.setAdapter(promotionalSKUsAdapter);
        return layout;
    }

}
