package com.cannshine.Fortune.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.cannshine.Fortune.Adapters.Database;
import com.cannshine.Fortune.Entities.AdsManager;
import com.cannshine.Fortune.Entities.CheckInternet;
import com.cannshine.Fortune.Entities.Global;
import com.cannshine.Fortune.Entities.Hexegram;
import com.cannshine.Fortune.Entities.InteractiveScrollView;
import com.cannshine.Fortune.Entities.Utils;
import com.cannshine.Fortune.R;
import com.cannshine.Fortune.VolleyRequest.ApplicationController;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.startapp.android.publish.adsCommon.StartAppAd;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DetailActivity extends AppCompatActivity {
    InteractiveScrollView svContent;
    ImageView imgBack, btnShare, btnClose, bgBanner;
    NetworkImageView imgBanner;
    ImageLoader imageLoader;
    TextView txvTitle;
    TextView txvContent, txvTieuDe;
    ImageView hao1, hao2, hao3, hao4, hao5, hao6;
    ConstraintLayout constraintLayout;
    Database data = new Database(this);
    Hexegram hexegram = new Hexegram();
    private StartAppAd startAppAd = new StartAppAd(this);
    public static Intent intent = new Intent(Intent.ACTION_VIEW);

    AdsManager adsManager = AdsManager.getInstance();
    InterstitialAd ad =  adsManager.getAd();

    public static int checkAds = -1;
    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Map<String, String> admobInfo = Utils.getAdsInfo(this, Global.KEY_ADMOB);
        String appId = admobInfo.get(Global.ADMOB_APP_ID);
        String interstitialId = admobInfo.get(Global.ADMOB_INTERSTITIAL_ID);
        if(CheckInternet.isConnected(this)){
            // quảng cáo banner của admob
            if(appId != null){
                adsAdmobBanner(appId, admobInfo.get(Global.ADMOB_BANNER_ID));
            }
        }

        txvContent = (TextView) findViewById(R.id.txv_giaixam);
        imgBack = (ImageView) findViewById(R.id.btn_back);
        txvTitle = (TextView) findViewById(R.id.txv_title_dl);
        txvTieuDe = (TextView) findViewById(R.id.txv_tieude);
        btnShare = (ImageView) findViewById(R.id.btn_share_dl);
        hao1 = (ImageView) findViewById(R.id.img_h1);
        hao2 = (ImageView) findViewById(R.id.img_h2);
        hao3 = (ImageView) findViewById(R.id.img_h3);
        hao4 = (ImageView) findViewById(R.id.img_h4);
        hao5 = (ImageView) findViewById(R.id.img_h5);
        hao6 = (ImageView) findViewById(R.id.img_h6);
        imgBanner = (NetworkImageView) findViewById(R.id.img_ads_banner);
        btnClose = (ImageView) findViewById(R.id.btn_close_banner);
        constraintLayout = (ConstraintLayout) findViewById(R.id.constrainLayout);
        bgBanner = (ImageView) findViewById(R.id.bgBanner);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                showAlertMessage(intent.getStringExtra("body"), intent.getStringExtra("title"));
            }
        };

        if(CheckInternet.isConnected(this) == false){
            imgBanner.setVisibility(View.GONE);
            btnClose.setVisibility(View.GONE);
            imgBack.setVisibility(View.VISIBLE);
            btnShare.setVisibility(View.VISIBLE);
        }

        //Lấy thông tin của quảng cáo
        getBanner();


        // click vào btnclose
        clickCloseAds();

        // click vào quảng cáo
        clickAdsBanner();

        Typeface fontTitle = Typeface.createFromAsset(this.getAssets(), "UTM Azuki.ttf");
        txvTitle.setTypeface(fontTitle);

        Intent intent = getIntent();
        String idHexegram = intent.getStringExtra("key_1");

        String h1 = intent.getStringExtra("hao_1");
        String h2 = intent.getStringExtra("hao_2");
        String h3 = intent.getStringExtra("hao_3");
        String h4 = intent.getStringExtra("hao_4");
        String h5 = intent.getStringExtra("hao_5");
        String h6 = intent.getStringExtra("hao_6");

        //setLine
        setLineHexegram(h1, h2, h3, h4, h5, h6);

        hexegram = data.getValues(idHexegram);

        String title = hexegram.getH_name();

        txvTitle.setTextSize(15);
        txvTitle.setText(title);

        String tieuDe = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
                + "<html><head>"
                + "<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\"  />"
                + "<head><body>";

        tieuDe +=   hexegram.getH_mean()+ "<body><html>";
        txvTieuDe.setText(Html.fromHtml(tieuDe));

        String customHtml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
                + "<html><head>"
                + "<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\"  />"
                + "<head><body>";

        customHtml +=   hexegram.getH_content()+
                        hexegram.getH_wao1()+ hexegram.getH_wao2()+
                        hexegram.getH_wao3()+ hexegram.getH_wao4()+
                        hexegram.getH_wao5()+ hexegram.getH_wao6()+
                "<body><html>";

        txvContent.setText(Html.fromHtml(customHtml));

        svContent = findViewById(R.id.sv_content);

        if(CheckInternet.isConnected(this)){
            svContent.setOnBottomReachedListener(new InteractiveScrollView.OnBottomReachedListener() {
                @Override
                public void onBottomReached() {
//                int check = 0;
                    if(checkAds == -1){
                        // ramdom quảng cáo admob vs startapp
                        ramdomAds();
//                    check = 1;
                    }else {
                        if(checkAds == 0) {
                            if(ad != null){
                                if (ad.isLoaded()) {
                                    ad.show();
                                }else {
                                    ad.loadAd(new AdRequest.Builder().addTestDevice("B852C1784AC94383A068EC6C168A15F8").build());
                                    StartAppAd.disableAutoInterstitial();
                                    startAppAd.showAd(); // show the ad
                                    startAppAd.loadAd(); // load the next ad
                                }
                            }
                        }
                        if(checkAds == 1) {
                            //Quảng cáo StartApp
                            StartAppAd.disableAutoInterstitial();
                            startAppAd.showAd(); // show the ad
                            startAppAd.loadAd(); // load the next ad
                        }
                    }
                }
            });
        }

        //Load quảng cáo mỗi khi tắt
        if(ad != null){
            if(CheckInternet.isConnected(DetailActivity.this)){
                ad.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        if(CheckInternet.isConnected(DetailActivity.this)){
                            ad.loadAd(new AdRequest.Builder().addTestDevice("B852C1784AC94383A068EC6C168A15F8").build());
                        }
                    }
                });
            }
        }

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        setBtnShare();
    }

    // Lấy quảng cáo
    private void getBanner() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Global.URL_DETAIL_GET_ADS_BANNER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("reponseGetBanner", "onResponse: " + response);
                String[] info = new String[2];
                try {
                    info = readBanner(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                loadImage(info[1]);
                intent.setData(Uri.parse(info[0]));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("reponseGetBannerError", "onResponse: " + error.toString());
            }
        }){
            @Override
            public byte[] getBody() throws AuthFailureError {
                final String bodys = "&action=ads&type=1&size=400x400";
                try {
                    return bodys.getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return null;
                }
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

    // sự kiện khi click vào adsbanner
    private void clickAds(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global.URL_DETAIL_CLICK_ADS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("responseClickAds", "onResponse: " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("responseClickAdsError", "onResponse: " + error.toString());

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("apikey", Global.APIKEY);
                return headers;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                final String bodys = "&act=clickads&bannerid=1&appid=1&os=android";
                try {
                    return bodys.getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
        ApplicationController.getInstance(this).addToRequestQueue(stringRequest);
    }

    //button share
    public void setBtnShare(){
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "https://sites.google.com/view/boidich-policy/loi-tua";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Quan Thánh Linh Xăm");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });
    }

    // setImageLine on Top
    public void setLineHexegram(String h1, String h2, String h3, String h4, String h5, String h6){
        if(h1.equals("dynamic_1")){
            hao1.setImageResource(R.mipmap.line_dynamic_1);
        }else if(h1.equals("normal_1")){
            hao1.setImageResource(R.mipmap.line_normal_1);
        }else if(h1.equals("dynamic_0")){
            hao1.setImageResource(R.mipmap.line_dynamic_0);
        }else{
            hao1.setImageResource(R.mipmap.line_normal_0);
        }

        if(h2.equals("dynamic_1")){
            hao2.setImageResource(R.mipmap.line_dynamic_1);
        }else if(h2.equals("normal_1")){
            hao2.setImageResource(R.mipmap.line_normal_1);
        }else if(h2.equals("dynamic_0")){
            hao2.setImageResource(R.mipmap.line_dynamic_0);
        }else{
            hao2.setImageResource(R.mipmap.line_normal_0);
        }

        if(h3.equals("dynamic_1")){
            hao3.setImageResource(R.mipmap.line_dynamic_1);
        }else if(h3.equals("normal_1")){
            hao3.setImageResource(R.mipmap.line_normal_1);
        }else if(h3.equals("dynamic_0")){
            hao3.setImageResource(R.mipmap.line_dynamic_0);
        }else {
            hao3.setImageResource(R.mipmap.line_normal_0);
        }

        if(h4.equals("dynamic_1")){
            hao4.setImageResource(R.mipmap.line_dynamic_1);
        }else if(h4.equals("normal_1")){
            hao4.setImageResource(R.mipmap.line_normal_1);
        }else if(h4.equals("dynamic_0")){
            hao4.setImageResource(R.mipmap.line_dynamic_0);
        }else {
            hao4.setImageResource(R.mipmap.line_normal_0);
        }

        if(h5.equals("dynamic_1")){
            hao5.setImageResource(R.mipmap.line_dynamic_1);
        }else if(h5.equals("normal_1")){
            hao5.setImageResource(R.mipmap.line_normal_1);
        }else if(h5.equals("dynamic_0")){
            hao5.setImageResource(R.mipmap.line_dynamic_0);
        }else {
            hao5.setImageResource(R.mipmap.line_normal_0);
        }

        if(h6.equals("dynamic_1")){
            hao6.setImageResource(R.mipmap.line_dynamic_1);
        }else if(h6.equals("normal_1")){
            hao6.setImageResource(R.mipmap.line_normal_1);
        }else if(h6.equals("dynamic_0")){
            hao6.setImageResource(R.mipmap.line_dynamic_0);
        }else {
            hao6.setImageResource(R.mipmap.line_normal_0);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        startAppAd.onResume();
        IntentFilter filter = new IntentFilter("sendMessageBroadcast");
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
//        startAppAd.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    //hienthiQuangCao
    public void adsAdmobBanner(String appId, String bannerId){
        RelativeLayout relativeLayout  = findViewById(R.id.admobBanner2);
        AdView mAdView = new AdView(this);
        MobileAds.initialize(this, appId);
        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setAdUnitId(bannerId);
        relativeLayout.addView(mAdView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("B852C1784AC94383A068EC6C168A15F8").build();
        mAdView.loadAd(adRequest);
    }

    //random quảng cáo khi mà kép xuống
    public void ramdomAds(){
        Random rd = new Random();
        int x ;
        x = rd.nextInt(10);
        if(x % 2 == 0){
            if (ad.isLoaded()) {
                ad.show();
                checkAds = 1;
            }else {
                ad.loadAd(new AdRequest.Builder().addTestDevice("B852C1784AC94383A068EC6C168A15F8").build());
                StartAppAd.disableAutoInterstitial();
                startAppAd.showAd(); // show the ad
                startAppAd.loadAd(); // load the next ad
                checkAds = 0;
            }
        }else{
            //Quảng cáo StartApp
            StartAppAd.disableAutoInterstitial();
            startAppAd.showAd(); // show the ad
            startAppAd.loadAd(); // load the next ad
            checkAds = 0;
        }
    }



    private String[] readBanner(String response) throws JSONException {
        String[] array = new String[2];
        JSONObject jsonObject = new JSONObject(response);

        JSONArray payload = jsonObject.getJSONArray("payload");
        for(int i = 0; i < payload.length(); i++){
            JSONObject info = (JSONObject) payload.get(i);

            String link = info.getString("link");
            String phto_link = info.getString("photo_link");

            array[0] = link;
            array[1] = phto_link;
        }

        return array;
    }

    @SuppressLint("ResourceAsColor")
    private void loadImage(String url){
        if(url != null){
            RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.RelLayout);
            int widthOfRel = relativeLayout.getWidth();
            int widthOfBanner = imgBanner.getWidth();
            if(widthOfRel <= widthOfBanner){
                imgBanner.getLayoutParams().height = widthOfRel - 20;
                imgBanner.getLayoutParams().width = widthOfRel - 20;
                imgBanner.requestLayout();
            }
            imageLoader = ApplicationController.getInstance(this).getImageLoader();
            imageLoader.get(url, ImageLoader.getImageListener(imgBanner,0, 0));
            imgBanner.setImageUrl("url", imageLoader);
            btnClose.setImageResource(R.mipmap.btn_close);
            bgBanner.setBackgroundColor(R.color.black);
            imgBack.setEnabled(false);
            btnShare.setEnabled(false);
        }else {
            btnClose.setVisibility(View.GONE);
            imgBanner.setVisibility(View.GONE);
            bgBanner.setVisibility(View.GONE);
        }
    }

    private void clickCloseAds(){
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgBanner.setVisibility(View.GONE);
                btnClose.setVisibility(View.GONE);
                bgBanner.setVisibility(View.GONE);
                imgBack.setEnabled(true);
                btnShare.setEnabled(true);
            }
        });
    }

    private void clickAdsBanner(){
        imgBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
                //request api
                clickAds();
                imgBanner.setVisibility(View.GONE);
                btnClose.setVisibility(View.GONE);
                bgBanner.setVisibility(View.GONE);
                imgBack.setEnabled(true);
                btnShare.setEnabled(true);
            }
        });
    }

    private void showAlertMessage(String body, String title){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(body)
                .setTitle(title)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}