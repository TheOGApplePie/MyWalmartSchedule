package com.theogapplepie.mywalmartschedule;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceManager {
    private static final String SHARED_PREFERENCE_NAME = "fcmdevicetoken";
    private static final String KEY_ACCESS_TOKEN = "token";
    private static Context mContext;
    private static SharedPreferenceManager mInstance;
    private SharedPreferenceManager(Context context){
        mContext = context;
    }

    public static synchronized SharedPreferenceManager getInstance(Context context){
        if (mInstance == null){
            mInstance = new SharedPreferenceManager(context);
        }
        return mInstance;
    }

    public boolean storeToken(String token){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREFERENCE_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ACCESS_TOKEN, token);
        editor.apply();
        return true;
    }

    public String getToken(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
    }

}
