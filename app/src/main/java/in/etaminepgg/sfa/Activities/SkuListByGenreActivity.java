package in.etaminepgg.sfa.Activities;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.etaminepgg.sfa.Adapters.ExpandableListAdapter;
import in.etaminepgg.sfa.Adapters.SkuGenreTabsAdapter;
import in.etaminepgg.sfa.Fragments.AllSKUsFragment;
import in.etaminepgg.sfa.Fragments.FrequentlyOrderedSKUsFragment;
import in.etaminepgg.sfa.Fragments.NewSKUsFragment;
import in.etaminepgg.sfa.Fragments.PreviouslyOrderedSKUsFragment;
import in.etaminepgg.sfa.Fragments.PromotionalSKUsFragment;
import in.etaminepgg.sfa.Fragments.SalesOrderFragment;
import in.etaminepgg.sfa.Models.CategoryHeader;
import in.etaminepgg.sfa.Models.SkuGroupHistory;
import in.etaminepgg.sfa.Models.SubCategoryForCategoryHeader;
import in.etaminepgg.sfa.R;
import in.etaminepgg.sfa.Utilities.Constants;
import in.etaminepgg.sfa.Utilities.DbUtils;
import in.etaminepgg.sfa.Utilities.MyDb;

import static in.etaminepgg.sfa.Utilities.Constants.TBL_SKU;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SKU_CATEGORY;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_SKU_SUBCATEGORY;
import static in.etaminepgg.sfa.Utilities.Constants.dbFileFullPath;
import static in.etaminepgg.sfa.Utilities.ConstantsA.All_SKUs_TAB;
import static in.etaminepgg.sfa.Utilities.ConstantsA.FREQUENT_SKUs_TAB;
import static in.etaminepgg.sfa.Utilities.ConstantsA.NEW_ORDER;
import static in.etaminepgg.sfa.Utilities.ConstantsA.NEW_SKUs_TAB;
import static in.etaminepgg.sfa.Utilities.ConstantsA.PROMO_SKUs_TAB;
import static in.etaminepgg.sfa.Utilities.ConstantsA.REGULAR_ORDER;

public class SkuListByGenreActivity extends AppCompatActivity
{
    private static ExpandableListAdapter adapter;
    TabLayout skuGenre_tabLayout;
    ViewPager skuList_ViewPager;
    SkuGenreTabsAdapter skuGenreTabsAdapter;
    Resources resources;
    Toolbar toolbar;
    ExpandableListView el_category;

    Button btn_category;

    AutoCompleteTextView act_searchsku;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sku_list_by_genre);
        resources = getResources();

        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
        getSupportActionBar().setTitle((CharSequence) "Cart & SKUs");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btn_category = (Button) findViewById(R.id.btn_category);
        act_searchsku = (AutoCompleteTextView) findViewById(R.id.act_searchsku);
        el_category = (ExpandableListView) findViewById(R.id.el_category);
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

        setAdapterForSearch();

        /*ArrayAdapter<SkuGroupHistory> skuAdapter = new ArrayAdapter<SkuGroupHistory>(this, android.R.layout.simple_dropdown_item_1line, getCustomSkuList());
        act_searchsku.setThreshold(1);
        act_searchsku.setAdapter(skuAdapter);*/

        // Setting group indicator null for custom indicator
        el_category.setGroupIndicator(null);

        setItems();
        setListener();

        showRelevantTab(intentExtraValue);



    }

    public  void setAdapterForSearch()
    {

        ArrayAdapter<SkuGroupHistory> skuAdapter = new ArrayAdapter<SkuGroupHistory>(this, android.R.layout.simple_dropdown_item_1line, DbUtils.getCustomSkuList());
        act_searchsku.setThreshold(1);
        act_searchsku.setAdapter(skuAdapter);
    }

    @Override
    public void onBackPressed()
    {
        categoryInactive();
        Constants.search_skuId="";
        super.onBackPressed();
    }


  /*  private List<SkuGroupHistory> getCustomSkuList()
    {
        String SQL_SELECT_SKUs=null;
     *//*  Fragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.skuList_ViewPager + ":" + skuList_ViewPager.getCurrentItem());

        if(skuList_ViewPager.getCurrentItem()==1){
            SQL_SELECT_SKUs = "select sku_id, sku_name from " + TBL_SKU + " WHERE new_sku=1;";
        }
        else if(skuList_ViewPager.getCurrentItem()==3 )
        {*//*
            SQL_SELECT_SKUs = "select sku_id, sku_name from " + TBL_SKU + " ;";
//        }
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_SKUs, null);

        List<SkuGroupHistory> skuList = new ArrayList<>();

        while (cursor.moveToNext())
        {
            String skuID = cursor.getString(cursor.getColumnIndexOrThrow("sku_id"));
            String skuName = cursor.getString(cursor.getColumnIndexOrThrow("sku_name"));

            skuList.add(new SkuGroupHistory(skuID,skuName));
        }

        cursor.close();
        sqLiteDatabase.close();

        return skuList;
    }*/

    private void categoryInactive()
    {
        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);


        ContentValues subcategoryActivevalue_inactive = new ContentValues();
        subcategoryActivevalue_inactive.put("is_active", 0);

        sqLiteDatabase.update(TBL_SKU_SUBCATEGORY, subcategoryActivevalue_inactive, "is_active = ? ", new String[]{"1"});
        sqLiteDatabase.close();

    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            categoryInactive();
            Constants.search_skuId="";
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setListenersToViews()
    {


        act_searchsku.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                el_category.setVisibility(View.GONE);
            }
        });




        act_searchsku.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                SkuGroupHistory sgh= (SkuGroupHistory) adapterView.getAdapter().getItem(i);

                Log.i("sku",new Gson().toJson(sgh));

                Constants.search_skuId=sgh.sku_id;


                Fragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.skuList_ViewPager + ":" + skuList_ViewPager.getCurrentItem());

                if (skuList_ViewPager.getCurrentItem() == 1 && fragment != null)
                {

                    ((NewSKUsFragment) fragment).setAdapter_Category();
                }
                else if (skuList_ViewPager.getCurrentItem() == 2 && fragment != null)
                {


                }
                else if (skuList_ViewPager.getCurrentItem() == 3 && fragment != null)
                {

                    ((AllSKUsFragment) fragment).setAdapter_Category();

                }
                else
                {

                }
            }
        });



        skuGenre_tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {

                if (tab.getPosition() == 0)
                {

                    el_category.setVisibility(View.GONE);
                    btn_category.setVisibility(View.GONE);
                    act_searchsku.setVisibility(View.GONE);


                    Fragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.skuList_ViewPager + ":" + skuList_ViewPager.getCurrentItem());

                    if (skuList_ViewPager.getCurrentItem() == 0 && fragment != null)
                    {
                        ((SalesOrderFragment) fragment).showCorrectSalesOrder();
                    }
                }
                else if (tab.getPosition() == 1)
                {

                    el_category.setVisibility(View.GONE);
                    btn_category.setVisibility(View.VISIBLE);
                    act_searchsku.setVisibility(View.VISIBLE);

                    Fragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.skuList_ViewPager + ":" + skuList_ViewPager.getCurrentItem());

                    if (skuList_ViewPager.getCurrentItem() == 1 && fragment != null)
                    {
                        ((NewSKUsFragment) fragment).setAdapter_Category();
                    }
                }
                else if (tab.getPosition() == 2)
                {
                    el_category.setVisibility(View.GONE);
                    btn_category.setVisibility(View.GONE);
                    act_searchsku.setVisibility(View.GONE);

                    Fragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.skuList_ViewPager + ":" + skuList_ViewPager.getCurrentItem());

                    if (skuList_ViewPager.getCurrentItem() == 2 && fragment != null)
                    {

                    }
                }
                else if (tab.getPosition() == 3)
                {
                    el_category.setVisibility(View.GONE);
                    btn_category.setVisibility(View.VISIBLE);
                    act_searchsku.setVisibility(View.VISIBLE);

                    Fragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.skuList_ViewPager + ":" + skuList_ViewPager.getCurrentItem());

                    if (skuList_ViewPager.getCurrentItem() == 3 && fragment != null)
                    {

                        ((AllSKUsFragment) fragment).setAdapter_Category();

                    }
                }
                else if (tab.getPosition() == 4)
                {
                    el_category.setVisibility(View.GONE);
                    btn_category.setVisibility(View.GONE);
                    act_searchsku.setVisibility(View.GONE);

                    Fragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.skuList_ViewPager + ":" + skuList_ViewPager.getCurrentItem());

                    if (skuList_ViewPager.getCurrentItem() == 4 && fragment != null)
                    {

                    }
                }
                else
                {
                    //Utils.showPopUp(getBaseContext(), "Tab " + tab.getPosition() + " is selected");
                   /* el_category.setVisibility(View.GONE);
                    btn_category.setVisibility(View.GONE);*/
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

        btn_category.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                el_category.setVisibility(View.VISIBLE);
                act_searchsku.setText("");
                Constants.search_skuId="";
            }
        });
    }

    private String getIntentExtraValue()
    {
        Intent intent = getIntent();

        if (intent != null)
        {
            String intentExtraKey_TabToShow = resources.getString(R.string.key_tab_to_show);
            String intentExtraKey_selectedOrderType = resources.getString(R.string.key_selected_order_type);

            //when launching from Dashboard Activity
            if (intent.hasExtra(intentExtraKey_TabToShow))
            {
                String tabToShow = intent.getStringExtra(intentExtraKey_TabToShow);
                return tabToShow;
            }
            //when launching from SelectSalesOrderType Activity
            else if (intent.hasExtra(intentExtraKey_selectedOrderType))
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
        skuGenreTabsAdapter.addFragment(new FrequentlyOrderedSKUsFragment(), "FREQUENT");
        skuGenreTabsAdapter.addFragment(new AllSKUsFragment(), "ALL");
        //skuGenreTabsAdapter.addFragment(new RetailerRegularOrderFragment(), "REGULAR ORDER");
        skuGenreTabsAdapter.addFragment(new PreviouslyOrderedSKUsFragment(), "PREVIOUSLY ORDERED");

        boolean isPromotion = DbUtils.getConfigValue(Constants.PROMOTION_REQUIRED);

        if (isPromotion)
        {

            skuGenreTabsAdapter.addFragment(new PromotionalSKUsFragment(), "PROMOTIONAL");
        }
    }

    private void showRelevantTab(String intentExtraValue)
    {
        switch (intentExtraValue)
        {
            case NEW_SKUs_TAB:
                skuGenre_tabLayout.scrollTo(1, 0);
                skuGenre_tabLayout.getTabAt(1).select();
                skuList_ViewPager.setCurrentItem(1);
                el_category.setVisibility(View.GONE);
                btn_category.setVisibility(View.VISIBLE);
                act_searchsku.setVisibility(View.VISIBLE);
                break;
            case FREQUENT_SKUs_TAB:
               /* el_category.setVisibility(View.GONE);
                btn_category.setVisibility(View.GONE);*/
                skuGenre_tabLayout.scrollTo(2, 0);
                skuGenre_tabLayout.getTabAt(2).select();
                skuList_ViewPager.setCurrentItem(2);
                el_category.setVisibility(View.GONE);
                btn_category.setVisibility(View.GONE);
                act_searchsku.setVisibility(View.GONE);
                break;
            case All_SKUs_TAB:
                skuGenre_tabLayout.scrollTo(3, 0);
                skuGenre_tabLayout.getTabAt(3).select();
                skuList_ViewPager.setCurrentItem(3);
                el_category.setVisibility(View.GONE);
                btn_category.setVisibility(View.VISIBLE);
                act_searchsku.setVisibility(View.VISIBLE);
                break;
            case PROMO_SKUs_TAB:
                skuGenre_tabLayout.scrollTo(5, 0);
                skuGenre_tabLayout.getTabAt(5).select();
                skuList_ViewPager.setCurrentItem(5);
                break;
            case NEW_ORDER:
                skuGenre_tabLayout.scrollTo(1, 0);
                skuGenre_tabLayout.getTabAt(1).select();
                skuList_ViewPager.setCurrentItem(1);
                el_category.setVisibility(View.GONE);
                btn_category.setVisibility(View.VISIBLE);
                act_searchsku.setVisibility(View.VISIBLE);
                break;
            case REGULAR_ORDER:
                skuGenre_tabLayout.scrollTo(0, 0);
                skuGenre_tabLayout.getTabAt(0).select();
                skuList_ViewPager.setCurrentItem(0);
                el_category.setVisibility(View.GONE);
                btn_category.setVisibility(View.GONE);
                act_searchsku.setVisibility(View.GONE);
                break;


        }
    }


    // Setting headers and childs to expandable listview
    void setItems()
    {

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String sku_category_query = "SELECT * from " + TBL_SKU_CATEGORY;

        Cursor cursor = sqLiteDatabase.rawQuery(sku_category_query, null);


        // Array list for header
        ArrayList<CategoryHeader> header = new ArrayList<CategoryHeader>();


        // Hash map for both header and child
        HashMap<String, List<SubCategoryForCategoryHeader>> hashMap = new HashMap<String, List<SubCategoryForCategoryHeader>>();

        while (cursor.moveToNext())
        {

            int category_id = cursor.getInt(cursor.getColumnIndexOrThrow("category_id"));
            String category_name = cursor.getString(cursor.getColumnIndexOrThrow("category_name"));

            header.add(new CategoryHeader(String.valueOf(category_id), category_name));

            String sku_subCategory_query = "SELECT * from " + TBL_SKU_SUBCATEGORY + " WHERE category_id = " + category_id;

            //String[] selectionArgs=new String[category_id];

            Cursor cursor_subCat = sqLiteDatabase.rawQuery(sku_subCategory_query, null);

            // Array list for child items
            List<SubCategoryForCategoryHeader> child1 = new ArrayList<SubCategoryForCategoryHeader>();


            while (cursor_subCat.moveToNext())
            {


                int subCategoryId = cursor_subCat.getInt(cursor_subCat.getColumnIndexOrThrow("sub_category_id"));
                String subCategoryName = cursor_subCat.getString(cursor_subCat.getColumnIndexOrThrow("sub_category_name"));

                child1.add(new SubCategoryForCategoryHeader(String.valueOf(subCategoryId), subCategoryName));
            }

            hashMap.put(String.valueOf(category_id), child1);
        }


        adapter = new ExpandableListAdapter(SkuListByGenreActivity.this, header, hashMap);

        // Setting adpater over expandablelistview
        el_category.setAdapter(adapter);

       /* // Array list for header
        ArrayList<String> header = new ArrayList<String>();

        // Array list for child items
        List<String> child1 = new ArrayList<String>();
        List<String> child2 = new ArrayList<String>();
        List<String> child3 = new ArrayList<String>();
        List<String> child4 = new ArrayList<String>();

        // Hash map for both header and child
        HashMap<String, List<String>> hashMap = new HashMap<String, List<String>>();

        // Adding headers to list
        for (int i = 1; i < 5; i++)
        {
            header.add("Group " + i);

        }
        // Adding child data
        for (int i = 1; i < 5; i++)
        {
            child1.add("Group 1  - " + " : Child" + i);

        }
        // Adding child data
        for (int i = 1; i < 5; i++)
        {
            child2.add("Group 2  - " + " : Child" + i);

        }
        // Adding child data
        for (int i = 1; i < 6; i++)
        {
            child3.add("Group 3  - " + " : Child" + i);

        }
        // Adding child data
        for (int i = 1; i < 7; i++)
        {
            child4.add("Group 4  - " + " : Child" + i);

        }

        // Adding header and childs to hash map
        hashMap.put(header.get(0), child1);
        hashMap.put(header.get(1), child2);
        hashMap.put(header.get(2), child3);
        hashMap.put(header.get(3), child4);*/


    }

    // Setting different listeners to expandablelistview
    void setListener()
    {

        // This listener will show toast on group click
        el_category.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener()
        {

            @Override
            public boolean onGroupClick(ExpandableListView listview, View view,
                                        int group_pos, long id)
            {

                /*Toast.makeText(SkuListByGenreActivity.this,
                        "You clicked : " + adapter.getGroup(group_pos),
                        Toast.LENGTH_SHORT).show();*/
                return false;
            }
        });

        // This listener will expand one group at one time
        // You can remove this listener for expanding all groups
        el_category
                .setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener()
                {

                    // Default position
                    int previousGroup = -1;

                    @Override
                    public void onGroupExpand(int groupPosition)
                    {
                        if (groupPosition != previousGroup)

                        // Collapse the expanded group
                        {
                            el_category.collapseGroup(previousGroup);
                        }
                        previousGroup = groupPosition;
                    }

                });

        // This listener will show toast on child click
        el_category.setOnChildClickListener(new ExpandableListView.OnChildClickListener()
        {

            @Override
            public boolean onChildClick(ExpandableListView listview, View view,
                                        int groupPos, int childPos, long id)
            {
                /*Toast.makeText(
                        SkuListByGenreActivity.this,
                        "You clicked : " + adapter.getChild(groupPos, childPos),
                        Toast.LENGTH_SHORT).show();*/

                updateSubcategoryTable((int) adapter.getGroupId(groupPos), (int) adapter.getChildId(groupPos, childPos));

                el_category.setVisibility(View.GONE);


                Fragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.skuList_ViewPager + ":" + skuList_ViewPager.getCurrentItem());

                if (skuList_ViewPager.getCurrentItem() == 1 && fragment != null)
                {

                    ((NewSKUsFragment) fragment).setAdapter_Category();
                }
                else if (skuList_ViewPager.getCurrentItem() == 2 && fragment != null)
                {


                }
                else if (skuList_ViewPager.getCurrentItem() == 3 && fragment != null)
                {

                    ((AllSKUsFragment) fragment).setAdapter_Category();

                }
                else
                {

                }
                return false;
            }
        });
    }

    private void updateSubcategoryTable(int groupPos, int childPos)
    {

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);


        ContentValues subcategoryActivevalue_inactive = new ContentValues();
        subcategoryActivevalue_inactive.put("is_active", 0);

        sqLiteDatabase.update(TBL_SKU_SUBCATEGORY, subcategoryActivevalue_inactive, "is_active = ? ", new String[]{"1"});

        ContentValues subcategoryActivevalue = new ContentValues();
        subcategoryActivevalue.put("is_active", 1);


        sqLiteDatabase.update(TBL_SKU_SUBCATEGORY, subcategoryActivevalue, "category_id = ? AND sub_category_id = ?", new String[]{String.valueOf(groupPos), String.valueOf(childPos)});
        sqLiteDatabase.close();
    }

}
