package in.etaminepgg.sfa.Activities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;

import in.etaminepgg.sfa.R;
import in.etaminepgg.sfa.Utilities.MyDb;
import in.etaminepgg.sfa.Utilities.MySharedPrefrencesData;

import static in.etaminepgg.sfa.Utilities.Constants.TBL_LOCATION_HIERARCHY;
import static in.etaminepgg.sfa.Utilities.Constants.dbFileFullPath;

public class ProfileActivity extends AppCompatActivity
{

    private Toolbar toolbar;
    private TextView user_name,name,user_email,user_mobile,user_assigned_area;
    MySharedPrefrencesData mySharedPrefrencesData;


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
        setContentView(R.layout.activity_profile);
        mySharedPrefrencesData=new MySharedPrefrencesData();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile Info");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setFindViewById();
        setListnerToViews();
    }

    private void setFindViewById()
    {

        this.user_name = (TextView)findViewById(R.id.user_profile_name);
        this.name = (TextView)findViewById(R.id.name);
        this.user_mobile = (TextView)findViewById(R.id.mobile);
        this.user_email = (TextView)findViewById(R.id.email);
        this.user_assigned_area = (TextView)findViewById(R.id.assignarea);
    }


    private void setListnerToViews()
    {
        user_name.setText(mySharedPrefrencesData.getUsername(getBaseContext()));


        name.setText(Html.fromHtml(getModifiedkeyValueString("Name : ",mySharedPrefrencesData.getUsername(getBaseContext()))));


        user_mobile.setText(Html.fromHtml(getModifiedkeyValueString("Mobile : " ,mySharedPrefrencesData.get_User_mobile(getBaseContext()))));


        user_email.setText(Html.fromHtml(getModifiedkeyValueString("Email : ",mySharedPrefrencesData.getEmail(getBaseContext()))));


        user_assigned_area.setText(Html.fromHtml(getModifiedkeyValueString("Assigned Area : ",getLocationname())));

    }

    private String getModifiedkeyValueString(String key,String Value)
    {
        String name_string=key+Value;
        String replacedWith1 = "<font color='blue'>" + key + "</font>";
        String modified_string_name = name_string.replaceAll(key,replacedWith1);
        return modified_string_name;
    }

    private String getLocationname()
    {

        int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
        SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

        String location_name = "";

        String userlocationid = new MySharedPrefrencesData().getUser_LocationId(ProfileActivity.this);

        String selected_location_id="";
        if(userlocationid.contains(",")){

            String location_id_arr[]=userlocationid.split(",");


            String loc_name_query = "SELECT " + "loc_name" + " FROM " + TBL_LOCATION_HIERARCHY + " WHERE " + "loc_id = ?";

            String location_name_with_comma="";

            for(String locationid:location_id_arr){

                String[] selectionArgs_for_user = {locationid};

                Cursor cursor = sqLiteDatabase.rawQuery(loc_name_query, selectionArgs_for_user);


                while (cursor.moveToNext())
                {
                    location_name_with_comma = location_name_with_comma+" , "+cursor.getString(cursor.getColumnIndexOrThrow("loc_name"));

                }


            }

            location_name=location_name_with_comma.substring(3);

            //cursor.close();
            sqLiteDatabase.close();


        }else {

            selected_location_id=userlocationid;

            String loc_name_query = "SELECT " + "loc_name" + " FROM " + TBL_LOCATION_HIERARCHY + " WHERE " + "loc_id = ?";

            String[] selectionArgs_for_user = {selected_location_id};

            Cursor cursor = sqLiteDatabase.rawQuery(loc_name_query, selectionArgs_for_user);


            if (cursor.moveToFirst())
            {
                location_name = cursor.getString(cursor.getColumnIndexOrThrow("loc_name"));

            }


            cursor.close();
            sqLiteDatabase.close();
        }


        return location_name;
    }
}
