package in.etaminepgg.sfa.Services;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import in.etaminepgg.sfa.Utilities.FileFunctions;
import in.etaminepgg.sfa.Utilities.MyDb;

import static in.etaminepgg.sfa.Activities.LoginActivity.uploadToURL;
import static in.etaminepgg.sfa.Utilities.Constants.IMEI;
import static in.etaminepgg.sfa.Utilities.Constants.dbFileFullPath;
import static in.etaminepgg.sfa.Utilities.Constants.appSpecificDirectoryPath;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_AREA;

public class AreaUploadIntentService extends IntentService {

    public AreaUploadIntentService() {
        super("AreaUploadIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        try{

            int surveyDbi = MyDb.openDatabase(dbFileFullPath);

            SQLiteDatabase dbh = MyDb.getDbHandle(surveyDbi);

            // Create a scopedb file for the data

            SimpleDateFormat fsdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            String fcurTime = fsdf.format(new Date());

            String fprefix = "SFA_" + fcurTime;
            String scopeDbFile = appSpecificDirectoryPath + File.separator + fprefix + ".reddb";

            // If this file exists, delete it
            if( FileFunctions.fileExists(scopeDbFile))
            {
                FileFunctions.deleteFile(scopeDbFile);
            }

            // Prepare the data content into a string
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String curTime = sdf.format(new Date());

            String s = "# Created on " + curTime + "\n\n";

            s += "sfa_header\n\n";

            s += "login`imei\n";

            s += "l`"+IMEI+"\n";

            s +=".\n\n";

            s += TBL_AREA +"\n";

            s +="state`district`city`village`area`pincode`landmark";

            s+="\n";

//            String qrr = "Select state, district, city, village, area, pincode, landmark from "+TBL_AREA+" where upload_status ='0'";

            String qrr = "Select state, district, city, village, area, pincode, landmark from "+ TBL_AREA;

            Cursor cur = dbh.rawQuery(qrr, null);

            cur.moveToFirst();

            while (!cur.isAfterLast())
            {

                s+=cur.getString(0)+"`"+cur.getString(1)+"`"+cur.getString(2)+"`"+cur.getString(3)
                        +"`"+cur.getString(4)+"`"+cur.getString(5)+"`"+cur.getString(6);

                s += "\n";

                cur.moveToNext();
            }

            cur.close();

            s += ".\n\n";

            Log.d("String Data",s);

            FileFunctions.writeTextFile(s, scopeDbFile );

            File f1 = new File( scopeDbFile );

            long fileSize = f1.length();

            Log.i("Data upload ", "File size is : "+fileSize);

            String newName = appSpecificDirectoryPath + File.separator + fprefix + "_" + fileSize + ".reddb";
            FileFunctions.move(scopeDbFile, newName);
            File f = new File( newName );
            try
            {
                int ret = FileFunctions.uploadFile( newName, uploadToURL);

                if( ret == f.length())
                {
                    Log.i("Data Uploaded : ", "Data Uploaded Successfully and Replyed Lenght is : "+ret);

                    FileFunctions.deleteFile(newName);

                    String updQrr = "Update "+ TBL_AREA +" set upload_status ='1'";

                    dbh.execSQL(updQrr);


                }
                else {
                    Log.i("Error on Data Upload : ", "Error uploading survey data. Check network connection and try again.");
                }
            }
            catch( Exception e )
            {

                Log.i("Error on Data Upload : ", ""+e.getMessage());

            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

}
