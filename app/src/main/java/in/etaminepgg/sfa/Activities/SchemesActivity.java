package in.etaminepgg.sfa.Activities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.etaminepgg.sfa.Adapters.SchemesAdapter;
import in.etaminepgg.sfa.Models.Scheme;
import in.etaminepgg.sfa.R;
import in.etaminepgg.sfa.Utilities.MyDb;

import static in.etaminepgg.sfa.Utilities.Constants.TBL_SCHEME;
import static in.etaminepgg.sfa.Utilities.Constants.dbFileFullPath;

public class SchemesActivity extends AppCompatActivity
{
    RecyclerView schemes_RecyclerView;
    TextView emptyAdapter_TextView;

    private Toolbar toolbar;

    //not implemented till now

    @Override
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
        // this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_schemes);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Schemes");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        emptyAdapter_TextView = (TextView) findViewById(R.id.emptyAdapter_TextView);
        schemes_RecyclerView = (RecyclerView) findViewById(R.id.schemes_RecyclerView);

        schemes_RecyclerView.setLayoutManager(new LinearLayoutManager(this));
        schemes_RecyclerView.setItemAnimator(new DefaultItemAnimator());

        List<Scheme> schemeList = getSchemesList();

        if (schemeList.size() < 1)
        {
            emptyAdapter_TextView.setVisibility(View.VISIBLE);
            schemes_RecyclerView.setVisibility(View.GONE);
        }
        else
        {
            emptyAdapter_TextView.setVisibility(View.GONE);
            schemes_RecyclerView.setVisibility(View.VISIBLE);
        }

        SchemesAdapter schemesAdapter = new SchemesAdapter(schemeList);
        schemes_RecyclerView.setAdapter(schemesAdapter);
    }

    private List<Scheme> getSchemesList()
    {
        List<Scheme> schemeList = new ArrayList<>();

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

      /*  String SQL_SELECT_SCHEMES = "SELECT" + " * " + "FROM " + TBL_SCHEME + " WHERE " + "is_viewed " + "= ? AND " + "end_date " + ">= ?";
        String[] selectionArgs = {"0", getTodayDate()};

        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_SCHEMES, selectionArgs);*/

        String SQL_SELECT_SCHEMES = "SELECT" + " * " + "FROM " + TBL_SCHEME;
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_SELECT_SCHEMES, null);


        while (cursor.moveToNext())
        {
            String schemeID = cursor.getString(cursor.getColumnIndex("scheme_id"));
            String schemeName = cursor.getString(cursor.getColumnIndex("scheme_name"));
            String schemeDescription = cursor.getString(cursor.getColumnIndex("scheme_description"));
            String startDate = cursor.getString(cursor.getColumnIndex("start_date"));
            String endDate = cursor.getString(cursor.getColumnIndex("end_date"));

            schemeList.add(new Scheme(schemeID, schemeName, schemeDescription, startDate, endDate));
        }

        cursor.close();
        sqLiteDatabase.close();

        return schemeList;
    }
}
