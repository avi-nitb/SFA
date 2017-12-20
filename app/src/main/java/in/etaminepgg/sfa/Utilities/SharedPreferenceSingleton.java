package in.etaminepgg.sfa.Utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import static android.content.Context.MODE_PRIVATE;


public class SharedPreferenceSingleton {
    private static SharedPreferenceSingleton mInstance;
    private Context mContext;
    private SharedPreferences myPreference;

    public static String MY_PREF = "MyPref";

    private void SharedPrefSingleton() {
    }

    public static SharedPreferenceSingleton getInstance(){
        if(mInstance == null){
            mInstance	=	new SharedPreferenceSingleton();
        }
        return mInstance;
    }

    public void init(Context context){
        mContext	=	context;
        myPreference=	PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public void writeStringPreference(String key, String value){
        SharedPreferences.Editor mEditor = myPreference.edit();
        mEditor.putString(key, value);
        mEditor.commit();
    }



    public void writeBoolPreference(String key, boolean value){
        SharedPreferences.Editor mEditor = myPreference.edit();
        mEditor.putBoolean(key, value);
        mEditor.commit();
    }
    public void clearPreference(){
        SharedPreferences.Editor mEditor = myPreference.edit();
        mEditor.clear();
        mEditor.commit();
    }
    public void writeIntPreference(String key, int value){
        SharedPreferences.Editor mEditor = myPreference.edit();
        mEditor.putInt(key, value);
        mEditor.commit();
    }
    public void writeFloatPreference(String key, Float value){
        SharedPreferences.Editor mEditor = myPreference.edit();
        mEditor.putFloat(key, value);
        mEditor.commit();
    }


    public float getFloatPreference(String key){
        return myPreference.getFloat(key,0);
    }
    public int getIntPreference(String key){
        return myPreference.getInt(key, -1);
    }
    public int getIntPreferenceNull(String key){
        return myPreference.getInt(key,-1000);
    }
    public String getStringPreferenceNull(String key){
        return myPreference.getString(key, null);
    }
    public String getStringPreference(String key){
        return myPreference.getString(key,"");
    }
    public boolean getBoolPreference(String key){
        return myPreference.getBoolean(key, false);
    }

    public void removePreference(String key){
        SharedPreferences.Editor mEditor = myPreference.edit();
        mEditor.remove(key);
        mEditor.commit();
    }

}
