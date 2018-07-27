package org.sdrc.scslmobile.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by SDRC_DEV on 06-05-2017.
 */

public class PreferenceData {

    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    public PreferenceData(Context context) {
        mContext = context;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mEditor = mSharedPreferences.edit();
    }

    public void setPreferenceData(String data,String key) {
        mEditor.putString(key, data);
        mEditor.commit();

    }

    public void setPreferenceData(boolean data,String key) {
        mEditor.putBoolean(key, data);
        mEditor.commit();

    }

    public void setPreferenceData(int data,String key) {
        mEditor.putInt(key, data);
        mEditor.commit();

    }

    public boolean getPreferenceBooleanData(String key) {
        return mSharedPreferences.getBoolean(key, false);
    }
    public String getPreferenceData(String key) {
        return mSharedPreferences.getString(key, null);
    }

    public int getPreferenceIntData(String key){
        return mSharedPreferences.getInt(key, 0);
    }

    public boolean deletePreferenceData(String key){
        return mEditor.remove(key).commit();
    }
}
