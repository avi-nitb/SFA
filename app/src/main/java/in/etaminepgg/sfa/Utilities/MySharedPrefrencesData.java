package in.etaminepgg.sfa.Utilities;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class MySharedPrefrencesData {
    public static final String PREFS_NAME = "MyPrefsFile";


    public void setEmployee_AuthKey(Context mContext, String authToken){
        SharedPreferences pref = mContext.getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("authToken", authToken);
        editor.commit(); // commit changes
    }
    public String getEmployee_AuthKey(Context mContext){
        SharedPreferences pref = mContext.getSharedPreferences("MyPref", MODE_PRIVATE);
        return pref.getString("authToken","");
    }

    public void setAuthTokenExpiryDate(Context mContext, String authTokenExpiryDate){
        SharedPreferences pref = mContext.getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("authTokenExpiryDate", authTokenExpiryDate);
        editor.commit(); // commit changes
    }
    public String getAuthTokenExpiryDate(Context mContext){
        SharedPreferences pref = mContext.getSharedPreferences("MyPref", MODE_PRIVATE);
        return pref.getString("authTokenExpiryDate","");
    }

    public void setUsername(Context mContext, String party_name){
        SharedPreferences pref = mContext.getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("Username", party_name);
        editor.commit(); // commit changes
    }
    public String getUsername(Context mContext){
        SharedPreferences pref = mContext.getSharedPreferences("MyPref", MODE_PRIVATE);
        return pref.getString("Username","");
    }


    public void setUser_pwd(Context mContext, String party_password){
        SharedPreferences pref = mContext.getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("user_password", party_password);
        editor.commit(); // commit changes
    }
    public String getUser_pwd(Context mContext){
        SharedPreferences pref = mContext.getSharedPreferences("MyPref", MODE_PRIVATE);
        return pref.getString("user_password","");
    }

    public void setUser_Id(Context mContext, String user_id){
        SharedPreferences pref = mContext.getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("user_id", user_id);
        editor.commit(); // commit changes
    }
    public String getUser_Id(Context mContext){
        SharedPreferences pref = mContext.getSharedPreferences("MyPref", MODE_PRIVATE);
        return pref.getString("user_id","");
    }


    public void setEmail(Context mContext, String party_email){
        SharedPreferences pref = mContext.getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("email", party_email);
        editor.commit(); // commit changes
    }
    public String getEmail(Context mContext){
        SharedPreferences pref = mContext.getSharedPreferences("MyPref", MODE_PRIVATE);
        return pref.getString("email","");
    }

    public void set_User_mobile(Context mContext, String party_mobile){
        SharedPreferences pref = mContext.getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("mobile", party_mobile);
        editor.commit(); // commit changes
    }
    public String get_User_mobile(Context mContext){
        SharedPreferences pref = mContext.getSharedPreferences("MyPref", MODE_PRIVATE);
        return pref.getString("mobile","");
    }



    public void clearPreference(Context context){
        SharedPreferences.Editor mEditor = context.getSharedPreferences("MyPref",MODE_PRIVATE).edit();
        mEditor.clear();
        mEditor.commit();
    }


    public void setDeviceId(Context mContext, String party_name){
        SharedPreferences pref = mContext.getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("DeviceId", party_name);
        editor.commit(); // commit changes
    }
    public String getDeviceId(Context mContext){
        SharedPreferences pref = mContext.getSharedPreferences("MyPref", MODE_PRIVATE);
        return pref.getString("DeviceId","");
    }



}
