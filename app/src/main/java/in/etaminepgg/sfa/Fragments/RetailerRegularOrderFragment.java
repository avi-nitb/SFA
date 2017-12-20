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
import in.etaminepgg.sfa.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RetailerRegularOrderFragment extends Fragment
{
    RecyclerView retailerRegularOrder_RecyclerView;
    
    public RetailerRegularOrderFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View layout =  inflater.inflate(R.layout.fragment_retailer_regular_order, container, false);
        retailerRegularOrder_RecyclerView = (RecyclerView)layout.findViewById(R.id.retailerRegularOrder_RecyclerView);
        retailerRegularOrder_RecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        retailerRegularOrder_RecyclerView.setItemAnimator(new DefaultItemAnimator());
        //retailerRegularOrder_RecyclerView.setAdapter(new NewSKUsAdapter());
        return layout;
    }
}
