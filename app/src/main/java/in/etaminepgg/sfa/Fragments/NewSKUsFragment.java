package in.etaminepgg.sfa.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.etaminepgg.sfa.Adapters.AllSKUsAdapter;
import in.etaminepgg.sfa.Adapters.NewSKUsAdapter;
import in.etaminepgg.sfa.R;
import in.etaminepgg.sfa.Utilities.DbUtils;

import static in.etaminepgg.sfa.Utilities.Constants.TBL_SKU;

/**
 * A simple {@link Fragment} subclass.
 */

public class NewSKUsFragment extends Fragment
{
    RecyclerView newSKUs_RecyclerView;

    public NewSKUsFragment()
    {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        String SQL_SELECT_NEW_SKUs = "select sku_id, sku_name, sku_price, sku_category from " + TBL_SKU +  " WHERE new_sku = ?" + " ;";
        String[] selectionArgs = new String[]{"1"};

        NewSKUsAdapter newSKUsAdapter = new NewSKUsAdapter(DbUtils.getSkuList(SQL_SELECT_NEW_SKUs, selectionArgs));

        View layout =  inflater.inflate(R.layout.fragment_new_skus, container, false);
        newSKUs_RecyclerView = (RecyclerView)layout.findViewById(R.id.newSKUs_RecyclerView);
        newSKUs_RecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        newSKUs_RecyclerView.setItemAnimator(new DefaultItemAnimator());
        newSKUs_RecyclerView.setAdapter(newSKUsAdapter);
        return layout;
    }
}
