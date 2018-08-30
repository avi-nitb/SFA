package in.etaminepgg.sfa.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import in.etaminepgg.sfa.Adapters.RetailerViewAdapter;
import in.etaminepgg.sfa.R;
import in.etaminepgg.sfa.Utilities.DbUtils;

public class ViewRetailerListActivity extends AppCompatActivity
{
    private Toolbar toolbar;
    private RecyclerView rv_view_retailer_list;
    private LinearLayoutManager lm;

    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_retailer_list);

        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
        getSupportActionBar().setTitle((CharSequence) "View Retailers");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        //get retailer list and set adapter
        rv_view_retailer_list=(RecyclerView)findViewById(R.id.rv_view_retailer_list);
        lm=new LinearLayoutManager(ViewRetailerListActivity.this);
        rv_view_retailer_list.setLayoutManager(lm);
        rv_view_retailer_list.setAdapter(new RetailerViewAdapter(DbUtils.getRetailerAllList(ViewRetailerListActivity.this),ViewRetailerListActivity.this));


    }
}
