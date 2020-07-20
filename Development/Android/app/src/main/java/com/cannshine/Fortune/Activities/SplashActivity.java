package com.cannshine.Fortune.Activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cannshine.Fortune.Entities.AdsInfo;
import com.cannshine.Fortune.Entities.Global;
import com.cannshine.Fortune.Entities.Utils;
import com.cannshine.Fortune.R;
import com.cannshine.Fortune.VolleyRequest.ApplicationController;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SplashActivity extends AppCompatActivity {

    private CountDownTimer myTimer;
    boolean finishSplashTime = false;
    boolean finishGetAdsInfo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //chuyển qua MainMenuActivity
        starActivity();

        // getAdsinfo
        getAdsinfo();

    }

    public void starActivity() {
        myTimer = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                if(finishGetAdsInfo){
                    goToMainMenu();
                }else {
                    finishSplashTime = true;
                }
            }
        }.start();
    }

    private void getAdsinfo(){

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Global.URL_SPLASH_GET_ADS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("responseSplash", "onResponse: " + response);
                try {
                    readRequest(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(finishSplashTime){
                    goToMainMenu();
                }else {
                    finishGetAdsInfo = true;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("responseSplashError", "onResponse: " + error.toString());
                if(finishSplashTime){
                    goToMainMenu();
                }else {
                    finishGetAdsInfo = true;
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("act", "requestAdNets");
                params.put("appid", "1");
                params.put("os", "android");

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("apikey", Global.APIKEY);

                return headers;
            }
        };
        ApplicationController.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void readRequest(String response) throws JSONException {
        ArrayList<AdsInfo> arrayAds = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(response);
        JSONArray jsonArray = jsonObject.getJSONArray("payload");
        for(int i = 0; i < jsonArray.length(); i++){
            JSONObject jsonObjectAds = (JSONObject) jsonArray.get(i);

            int id = jsonObjectAds.getInt("id");
            String code = jsonObjectAds.getString("code");
            String ads_info = jsonObjectAds.getString("ads_info").trim();
            AdsInfo ads = new AdsInfo(id, code, ads_info);

            arrayAds.add(ads);
        }

        for (int i = 0; i<arrayAds.size(); i++){
            String code = arrayAds.get(i).getCode().trim();
            if(code.equals("admob")){
                JSONObject object = new JSONObject(arrayAds.get(i).getAds_info());
                String abmobAppId = object.getString("app_id");
                String admobBannerId = object.getString("banner_id");
                String admobIntertitalId = object.getString("interstitial_id");
                if(abmobAppId != null && admobBannerId != null && admobIntertitalId != null){
                    Utils.admobSaveKey(this, Global.KEY_ADMOB, abmobAppId, admobBannerId, admobIntertitalId);
                }
            }
            if(code.equals("startapp")){
                JSONObject object = new JSONObject(arrayAds.get(i).getAds_info());
                String startAppId = object.getString("app_id");
                String startAppDevId = object.getString("dev_id");
                if(startAppId != null && startAppDevId != null){
                    Utils.startappSaveKey(this, Global.KEY_STARTAPP, startAppId, startAppDevId);
                }
            }
        }
    }

    public void goToMainMenu(){
        Intent intent = new Intent(SplashActivity.this, MainMenuActivity.class);
        startActivity(intent);
        finish();
    }

}
