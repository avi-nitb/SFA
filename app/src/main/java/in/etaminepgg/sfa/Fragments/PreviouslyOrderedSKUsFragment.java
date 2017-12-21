package in.etaminepgg.sfa.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.etaminepgg.sfa.Adapters.PreviouslyOrderedSKUsAdapter;
import in.etaminepgg.sfa.R;
import in.etaminepgg.sfa.Utilities.DbUtils;

import static in.etaminepgg.sfa.Utilities.Utils.loggedInUserID;

/**
 * A simple {@link Fragment} subclass.
 */

public class PreviouslyOrderedSKUsFragment extends Fragment
{
    RecyclerView previouslyOrderedSKUs_RecyclerView;

    public PreviouslyOrderedSKUsFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //TODO: need to ask rama about distinct not showing recent SKUs on top, if they are already present in previous order
        String SQL_SELECT_PREVIOUSLY_ORDERED_SKUs = "SELECT DISTINCT sod.sku_id, sod.sku_name, sod.sku_price FROM sales_orders so INNER JOIN sales_order_details sod"
                + " ON so.order_id=sod.order_id WHERE so.emp_id=? AND so.is_placed=? ORDER BY datetime(so.order_date) DESC";

        String[] selectionArgs = {loggedInUserID, "1"};

        PreviouslyOrderedSKUsAdapter previouslyOrderedSKUsAdapter = new PreviouslyOrderedSKUsAdapter(DbUtils.getSkuList2(SQL_SELECT_PREVIOUSLY_ORDERED_SKUs, selectionArgs));

        View layout = inflater.inflate(R.layout.fragment_previously_ordered_skus, container, false);
        previouslyOrderedSKUs_RecyclerView = (RecyclerView) layout.findViewById(R.id.previouslyOrderedSKUs_RecyclerView);
        previouslyOrderedSKUs_RecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        previouslyOrderedSKUs_RecyclerView.setItemAnimator(new DefaultItemAnimator());

        previouslyOrderedSKUs_RecyclerView.setAdapter(previouslyOrderedSKUsAdapter);

        return layout;
    }
}
