package in.etaminepgg.sfa.Services;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import in.etaminepgg.sfa.Utilities.FileFunctions;
import in.etaminepgg.sfa.Utilities.MyDb;

import static in.etaminepgg.sfa.Activities.LoginActivity.uploadToURL;
import static in.etaminepgg.sfa.Utilities.Constants.IMEI;
import static in.etaminepgg.sfa.Utilities.Constants.dbFileFullPath;
import static in.etaminepgg.sfa.Utilities.Constants.DBH;
import static in.etaminepgg.sfa.Utilities.Constants.appSpecificDirectoryPath;
import static in.etaminepgg.sfa.Utilities.Constants.TBL_RETAILER;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class RetailerUploaderIntentService extends IntentService {

    public RetailerUploaderIntentService() {
        super("RetailerUploaderIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        uploadNewRetailer();

        uploadNewRetailerPhotos();

    }

    public static void uploadNewRetailer() {

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

            s += TBL_RETAILER +"\n";

            s +="ret_id`shop_name`shop_address`retailer_name`mobile_no`email`district`city`area`LONGITUDE`LATITUDE`media_name`saved_date";

            s+="\n";

//            String qrr = "Select ret_id, shop_name, shop_address, retailer_name, " +
//                    "mobile_no, email, district, city, area, LONGITUDE, LATITUDE, media_name, saved_date " +
//                    " From "+TBL_RETAILER+" where upload_status ='0'";

            String qrr = "Select ret_id, shop_name, shop_address, retailer_name, " +
                    "mobile_no, email, district, city, area, LONGITUDE, LATITUDE, media_name, saved_date " +
                    " From "+ TBL_RETAILER;

            Cursor cur = dbh.rawQuery(qrr, null);

            cur.moveToFirst();

            while (!cur.isAfterLast())
            {

                s+= cur.getString(0)+"`"+cur.getString(1)+"`"+cur.getString(2)+"`"+cur.getString(3)+"`"
                        +cur.getString(4)+"`"+cur.getString(5)+"`"+cur.getString(6)+"`"+cur.getString(7)+"`"
                        +cur.getString(8)+"`"+cur.getString(9)+"`"+cur.getString(10)+"`";

                String imgName ="";

                if (cur.getString(11).length()>0 && !cur.getString(11).equals(null)) {

                    String imgPath = cur.getString(11);

                    String[] imgArr = imgPath.split("Images/");

                    imgName = imgArr[1];

                }

                s+= imgName+"`"+cur.getString(12);

                s += "\n";

                cur.moveToNext();

            }
            cur.close();

            s += ".\n\n";

            Log.d("Data",s);

            FileFunctions.writeTextFile(s, scopeDbFile );

            File f1 = new File( scopeDbFile );

            long fileSize = f1.length();

            Log.i("Data upload ", "File size is : "+fileSize);

            String newName = appSpecificDirectoryPath + File.separator + fprefix + "_" + fileSize + ".reddb";
            FileFunctions.move(scopeDbFile, newName);
            File f = new File( newName );
            try
            {

//                String tempUrl = uploadToURL+"?login="+"imei="+IMEI;

                String tempUrl = uploadToURL +"?login="+"l"+"&imei="+IMEI;

//                int ret = FileFunctions.uploadFile( newName, uploadToURL);

                int ret = 0;

                ret = FileFunctions.uploadFile( newName, tempUrl);

                if( ret == f.length())
                {
                    Log.i("Data Uploaded : ", "Data Uploaded Successfully and Replyed Lenght is : "+ret);

                    FileFunctions.deleteFile(newName);

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

    public static void uploadNewRetailerPhotos() {

        try
        {
//            String insQ1 = "SELECT media_name, ret_id FROM "+TBL_RETAILER+" where upload_status ='0'" ;
//
            String insQ1 = "SELECT media_name, ret_id FROM "+ TBL_RETAILER;

            Cursor insC1 = DBH.rawQuery(insQ1, null);
            insC1.moveToFirst();


            while(!insC1.isAfterLast())
            {
                final String getImage = insC1.getString(0);

                String ret_id = insC1.getString(1);

                Log.d("File Name ",getImage);

                try {

                    Uri myUri = Uri.parse("file://" + getImage); // complete path of audio file.

                    File f1 = new File(myUri.getPath());

                    long fileSize = f1.length();

                    Log.d("File Size",""+fileSize);
//
//                    String curDateTime = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
//
//                    String fullName = "SFA_RetPhoto_" + ret_id + "_" + curDateTime + "_" + fileSize + ".jpeg";
//
//                    String newName = appSpecificDirectoryPath + File.separator + fullName;
//
//
//                    File ff = new File(myUri.getPath());
//                    File ft = new File(newName);
//
                    try {

//                        FileFunctions.copy(ff, ft);
//
//
//                        int uploadStatus = FileFunctions.uploadFile(newName, uploadToURL);

                        int uploadStatus = FileFunctions.uploadFile(getImage, uploadToURL);

                        Log.d("Response File Size",""+uploadStatus);

                        if (uploadStatus == fileSize) {

                            Log.d("uploadStatus", "Image Uploaded Successfully");

                            String updQrr = "Update "+ TBL_RETAILER +" set upload_status ='1' where ret_id ='"+ret_id+"'";

                            DBH.execSQL(updQrr);

                        } else {

                            Log.d("uploadStatus", "Error In Image Uploaded");

                        }

//                        ft.delete();

                    } catch (Exception e) {

                        Log.d("uploadStatus", "Check.. File May Not Be Present.");

                    }


                } catch (IllegalArgumentException
                        | IllegalStateException e) {

                    	e.printStackTrace();

                } catch (Exception e)
                {

                    e.printStackTrace();

                }

                insC1.moveToNext();
            }

            insC1.close();

            Log.d("End Of Images Upload","True");

        }
        catch(Exception e)
        {
            	e.printStackTrace();
        }

    }

}
