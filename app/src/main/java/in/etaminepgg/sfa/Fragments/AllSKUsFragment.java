package in.etaminepgg.sfa.Fragments;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import in.etaminepgg.sfa.Adapters.AllSKUsAdapter;
import in.etaminepgg.sfa.Models.Sku;
import in.etaminepgg.sfa.R;
import in.etaminepgg.sfa.Utilities.DbUtils;
import in.etaminepgg.sfa.Utilities.MyDb;

import static in.etaminepgg.sfa.Utilities.Constants.TBL_SKU;
import static in.etaminepgg.sfa.Utilities.Constants.dbFileFullPath;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllSKUsFragment extends Fragment
{
    RecyclerView allSKUs_RecyclerView;
    
    public AllSKUsFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        String SQL_SELECT_ALL_SKUs = "select sku_id, sku_name, sku_price, sku_category from " + TBL_SKU + " ;";

        AllSKUsAdapter allSKUsAdapter = new AllSKUsAdapter(DbUtils.getSkuList(SQL_SELECT_ALL_SKUs, null));

        View layout =  inflater.inflate(R.layout.fragment_all_skus, container, false);
        allSKUs_RecyclerView = (RecyclerView)layout.findViewById(R.id.allSKUs_RecyclerView);
        allSKUs_RecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        allSKUs_RecyclerView.setItemAnimator(new DefaultItemAnimator());
        allSKUs_RecyclerView.setAdapter(allSKUsAdapter);
        return layout;
    }

}
