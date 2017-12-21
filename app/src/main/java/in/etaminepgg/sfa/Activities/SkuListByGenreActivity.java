package in.etaminepgg.sfa.Activities;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import in.etaminepgg.sfa.Adapters.SkuGenreTabsAdapter;
import in.etaminepgg.sfa.Fragments.AllSKUsFragment;
import in.etaminepgg.sfa.Fragments.FrequentlyOrderedSKUsFragment;
import in.etaminepgg.sfa.Fragments.NewSKUsFragment;
import in.etaminepgg.sfa.Fragments.PreviouslyOrderedSKUsFragment;
import in.etaminepgg.sfa.Fragments.PromotionalSKUsFragment;
import in.etaminepgg.sfa.Fragments.SalesOrderFragment;
import in.etaminepgg.sfa.R;

import static in.etaminepgg.sfa.Utilities.ConstantsA.All_SKUs_TAB;
import static in.etaminepgg.sfa.Utilities.ConstantsA.FREQUENT_SKUs_TAB;
import static in.etaminepgg.sfa.Utilities.ConstantsA.NEW_SKUs_TAB;
import static in.etaminepgg.sfa.Utilities.ConstantsA.PROMO_SKUs_TAB;

public class SkuListByGenreActivity extends AppCompatActivity
{
    TabLayout skuGenre_tabLayout;
    ViewPager skuList_ViewPager;
    SkuGenreTabsAdapter skuGenreTabsAdapter;

    Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sku_list_by_genre);
        resources = getResources();

        skuGenre_tabLayout = (TabLayout) findViewById(R.id.skuGenre_tabLayout);
        skuList_ViewPager = (ViewPager) findViewById(R.id.skuList_ViewPager);

        //if launched from SelectSalesOrderTypeActivity, intent extra value would be selectedOrderType
        //if launched from DashboardActivity, intent extra value would be tabToShow
        String intentExtraValue = getIntentExtraValue();

        skuGenreTabsAdapter = new SkuGenreTabsAdapter(getSupportFragmentManager());
        addFragmentsToViewPager(skuGenreTabsAdapter, intentExtraValue);

        skuList_ViewPager.setAdapter(skuGenreTabsAdapter);
        skuGenre_tabLayout.setupWithViewPager(skuList_ViewPager);

        setListenersToViews();

        showRelevantTab(intentExtraValue);
    }

    private void setListenersToViews()
    {
        skuGenre_tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                if(tab.getPosition() == 0)
                {
                    Fragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.skuList_ViewPager + ":" + skuList_ViewPager.getCurrentItem());

                    if(skuList_ViewPager.getCurrentItem() == 0 && fragment != null)
                    {
                        ((SalesOrderFragment) fragment).showCorrectSalesOrder();
                    }
                }
                else
                {
                    //Utils.showPopUp(getBaseContext(), "Tab " + tab.getPosition() + " is selected");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab)
            {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab)
            {

            }
        });
    }

    private String getIntentExtraValue()
    {
        Intent intent = getIntent();

        if(intent != null)
        {
            String intentExtraKey_TabToShow = resources.getString(R.string.key_tab_to_show);
            String intentExtraKey_selectedOrderType = resources.getString(R.string.key_selected_order_type);

            //when launching from Dashboard Activity
            if(intent.hasExtra(intentExtraKey_TabToShow))
            {
                String tabToShow = intent.getStringExtra(intentExtraKey_TabToShow);
                return tabToShow;
            }
            //when launching from SelectSalesOrderType Activity
            else if(intent.hasExtra(intentExtraKey_selectedOrderType))
            {
                String selectedOrderType = intent.getStringExtra(intentExtraKey_selectedOrderType);
                return selectedOrderType;
            }
        }

        return "SkuListByGenreActivity_getIntentExtraValue()";
    }

    private void addFragmentsToViewPager(SkuGenreTabsAdapter skuGenreTabsAdapter, String intentExtraValue)
    {
        String intentExtraKey = resources.getString(R.string.key_selected_order_type);
        SalesOrderFragment salesOrderFragment = new SalesOrderFragment();
        Bundle bundle = new Bundle();
        bundle.putString(intentExtraKey, intentExtraValue);
        salesOrderFragment.setArguments(bundle);

        skuGenreTabsAdapter.addFragment(salesOrderFragment, "SALES ORDER");
        skuGenreTabsAdapter.addFragment(new NewSKUsFragment(), "NEW");
        skuGenreTabsAdapter.addFragment(new PromotionalSKUsFragment(), "PROMOTIONAL");
        skuGenreTabsAdapter.addFragment(new FrequentlyOrderedSKUsFragment(), "FREQUENT");
        skuGenreTabsAdapter.addFragment(new AllSKUsFragment(), "ALL");
        //skuGenreTabsAdapter.addFragment(new RetailerRegularOrderFragment(), "REGULAR ORDER");
        skuGenreTabsAdapter.addFragment(new PreviouslyOrderedSKUsFragment(), "PREVIOUSLY ORDERED");
    }

    private void showRelevantTab(String intentExtraValue)
    {
        switch(intentExtraValue)
        {
            case NEW_SKUs_TAB:
                skuGenre_tabLayout.scrollTo(1, 0);
                skuGenre_tabLayout.getTabAt(1).select();
                skuList_ViewPager.setCurrentItem(1);
                break;
            case PROMO_SKUs_TAB:
                skuGenre_tabLayout.scrollTo(2, 0);
                skuGenre_tabLayout.getTabAt(2).select();
                skuList_ViewPager.setCurrentItem(2);
                break;
            case FREQUENT_SKUs_TAB:
                skuGenre_tabLayout.scrollTo(3, 0);
                skuGenre_tabLayout.getTabAt(3).select();
                skuList_ViewPager.setCurrentItem(3);
                break;
            case All_SKUs_TAB:
                skuGenre_tabLayout.scrollTo(4, 0);
                skuGenre_tabLayout.getTabAt(4).select();
                skuList_ViewPager.setCurrentItem(4);
                break;
        }
    }

}
