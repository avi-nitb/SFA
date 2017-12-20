package in.etaminepgg.sfa.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.etaminepgg.sfa.Adapters.FrequentlyOrderedSKUsAdapter;
import in.etaminepgg.sfa.R;
import in.etaminepgg.sfa.Utilities.DbUtils;

import static in.etaminepgg.sfa.Utilities.Utils.loggedInUserID;

/**
 * A simple {@link Fragment} subclass.
 */
public class FrequentlyOrderedSKUsFragment extends Fragment
{
    RecyclerView frequentlyOrderedSKUs_RecyclerView;
    
    public FrequentlyOrderedSKUsFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //helpful for testing actual query
        /*SELECT so.order_id, so.emp_id, so.is_placed, sod.sku_id, sod.sku_name, sod.sku_price, sod.sku_qty FROM sales_orders so INNER JOIN sales_order_details sod
        ON so.order_id=sod.order_id WHERE so.emp_id="emp_id1" AND so.is_placed="1";*/

        //actual query - long
        /*SELECT so.order_id, so.emp_id, so.is_placed, sod.sku_id, sod.sku_name, sod.sku_price, SUM(sod.sku_qty)
        FROM sales_orders so INNER JOIN sales_order_details sod
        ON so.order_id=sod.order_id WHERE so.emp_id="emp_id1" AND so.is_placed="1" GROUP BY sod.sku_id ORDER BY  SUM(sod.sku_qty) DESC;*/

        //actual query - short
        /*SELECT sod.sku_id, sod.sku_name, sod.sku_price, SUM(sod.sku_qty) FROM sales_orders so INNER JOIN sales_order_details sod
        ON so.order_id=sod.order_id WHERE so.emp_id="emp_id1" AND so.is_placed="1" GROUP BY sod.sku_id ORDER BY  SUM(sod.sku_qty) DESC;*/

        String SQL_SELECT_FREQUENT_SKUs = "SELECT sod.sku_id, sod.sku_name, sod.sku_price, SUM(sod.sku_qty) FROM sales_orders so INNER JOIN sales_order_details sod"
                + " ON so.order_id=sod.order_id WHERE so.emp_id=? AND so.is_placed=? GROUP BY sod.sku_id ORDER BY SUM(sod.sku_qty) DESC";

        String[] selectionArgs = {loggedInUserID, "1"};

        FrequentlyOrderedSKUsAdapter frequentlyOrderedSKUsAdapter = new FrequentlyOrderedSKUsAdapter(DbUtils.getSkuList2(SQL_SELECT_FREQUENT_SKUs, selectionArgs));

        View layout =  inflater.inflate(R.layout.fragment_frequently_ordered_skus, container, false);
        frequentlyOrderedSKUs_RecyclerView = (RecyclerView)layout.findViewById(R.id.frequentlyOrderedSKUs_RecyclerView);
        frequentlyOrderedSKUs_RecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        frequentlyOrderedSKUs_RecyclerView.setItemAnimator(new DefaultItemAnimator());

        frequentlyOrderedSKUs_RecyclerView.setAdapter(frequentlyOrderedSKUsAdapter);

        return layout;
    }
}
