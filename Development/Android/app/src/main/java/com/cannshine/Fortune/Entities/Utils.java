package com.cannshine.Fortune.Entities;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;

import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class Utils {
    public static void saveUserInfo(Context context, String key, String userId, String userKey){
        Log.d("user", "saveUserInfo: " + userId + " " + userKey);
        SharedPreferences sharedPreferences = context.getSharedPreferences(key, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Global.K_USERID, userId);
        editor.putString(Global.K_USERKEY, userKey);
        editor.commit();
    }

    public static boolean getUserInfo(Context context, String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences(key, MODE_PRIVATE);
        String userId = sharedPreferences.getString(Global.K_USERID, "");
        String userKey = sharedPreferences.getString(Global.K_USERKEY, "");
        Log.d("userCheck", "getUserInfo: " + userId + " " + userKey);
        if(userId.equals("") && userKey.equals("")){
            return false;
        }
        return true;
    }

    public static void admobSaveKey(Context context, String adsKey, String appId, String bannerId, String interstitialId){
        SharedPreferences.Editor editor = context.getSharedPreferences(adsKey, Context.MODE_PRIVATE).edit();
        editor.putString(Global.ADMOB_APP_ID, appId);
        editor.putString(Global.ADMOB_BANNER_ID, bannerId);
        editor.putString(Global.ADMOB_INTERSTITIAL_ID, interstitialId);
        editor.commit();
    }

    public static void startappSaveKey(Context context, String adsKey, String appId, String devId){
        SharedPreferences.Editor editor = context.getSharedPreferences(adsKey, Context.MODE_PRIVATE).edit();
        editor.putString(Global.STARTAPP_APP_ID, appId);
        editor.putString(Global.STARTAPP_DEV_ID, devId);
        editor.commit();
    }

    public static Map<String, String> getAdsInfo(Context context, String adsKey){
        SharedPreferences preferences = context.getSharedPreferences(adsKey, Context.MODE_PRIVATE);
        return (Map<String, String>) preferences.getAll();
    }

    public static String getDeviceId(Context context){
        String m_androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        SharedPreferences preferences = context.getSharedPreferences(Global.KEY_DEVICE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        String deviceId = preferences.getString(Global.K_DEVICE_ID,"");
        if(deviceId.equals("")){
            editor.putString(Global.K_DEVICE_ID, m_androidId);
            editor.commit();
            return m_androidId;
        }else {
            return deviceId;
        }
    }

    public static Map<String, String> getFlagToken(Context context){
        SharedPreferences preferences = context.getSharedPreferences(Global.FLAG_TOKEN, Context.MODE_PRIVATE);
        return (Map<String, String>) preferences.getAll();
    }

    public static void setFlagToken(Context context, String value){
        Log.d("flagtoken", "setFlagToken: " + value);
        SharedPreferences.Editor editor = context.getSharedPreferences(Global.FLAG_TOKEN, Context.MODE_PRIVATE).edit();
        editor.putString(Global.K_TOKEN, value);
        editor.commit();
    }

    public static void saveNewToken(Context context, String token){
        SharedPreferences.Editor editor = context.getSharedPreferences(Global.FLAG_TOKEN, Context.MODE_PRIVATE).edit();
        editor.putString(Global.FCM, token);
        editor.commit();
    }

    public static String getNewToken(Context context){
        SharedPreferences preferences = context.getSharedPreferences(Global.FLAG_TOKEN, Context.MODE_PRIVATE);
        String token = preferences.getString(Global.FCM, "");
        Log.d("newtoken", "getNewToken: " + token);
        return token;
    }

}
