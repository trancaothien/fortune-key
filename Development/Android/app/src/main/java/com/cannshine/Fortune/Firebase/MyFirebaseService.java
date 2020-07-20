package com.cannshine.Fortune.Firebase;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cannshine.Fortune.Activities.SplashActivity;
import com.cannshine.Fortune.Entities.Global;
import com.cannshine.Fortune.Entities.Utils;
import com.cannshine.Fortune.R;
import com.cannshine.Fortune.VolleyRequest.ApplicationController;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyFirebaseService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // handle a notification payload.
        if (remoteMessage.getNotification() != null) {
            if(isAppRunning(this, "com.cannshine.Fortune")){
                Log.d(TAG, "Message Notification Body: " + remoteMessage.toString());
                String body = remoteMessage.getNotification().getBody();
                String title = remoteMessage.getNotification().getTitle();
                Intent intent = new Intent("sendMessageBroadcast");
                intent.putExtra("body", body);
                intent.putExtra("title", title);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            }else {
                sendNotification(remoteMessage);
            }
        }
    }

    @Override
    public void onNewToken(@NonNull String s) {
        Utils.setFlagToken(this, "1");
        Utils.saveNewToken(this, s);
        String deviceId = Utils.getDeviceId(MyFirebaseService.this);
        SharedPreferences sharedPreferences = MyFirebaseService.this.getSharedPreferences(Global.KEY_USER, MODE_PRIVATE);
        String userKey = sharedPreferences.getString(Global.K_USERKEY, "");
        if(userKey.equals("") == false)
            updateFCM(deviceId, userKey, s);
    }

    private void sendNotification(RemoteMessage remoteMessage){
        Intent intent = new Intent(this, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon_notification)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

    public boolean isAppRunning(final Context context, final String packageName) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null)
        {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                if (processInfo.processName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void updateFCM(final String deviceId, final String userKey, final String token){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global.URL_UPDATE_FCM, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("newToken", "onResponse: " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int errorRequest = jsonObject.getInt("error");
                    if(errorRequest == 0){
                        Utils.setFlagToken(MyFirebaseService.this, "0");
                    }else
                        Utils.setFlagToken(MyFirebaseService.this, "1");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("errorNewToken", "onErrorResponse: " + error.toString());
            }
        }){
            @Override
            public byte[] getBody() throws AuthFailureError {

                final String body = "&act=updatefcm&userkey="+ userKey +"&deviceid=" + deviceId + "&fcm=" + token;
                try {
                    return body.getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("apikey", Global.APIKEY);
                return headers;
            }
        };

        ApplicationController.getInstance(MyFirebaseService.this).addToRequestQueue(stringRequest);
    }
}