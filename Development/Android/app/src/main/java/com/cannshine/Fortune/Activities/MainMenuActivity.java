package com.cannshine.Fortune.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cannshine.Fortune.BuildConfig;
import com.cannshine.Fortune.Entities.AdsManager;
import com.cannshine.Fortune.Entities.CheckInternet;
import com.cannshine.Fortune.Entities.Global;
import com.cannshine.Fortune.Entities.Utils;
import com.cannshine.Fortune.VolleyRequest.ApplicationController;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.cannshine.Fortune.Adapters.Database;
import com.cannshine.Fortune.Entities.Hexegram;
import com.cannshine.Fortune.R;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.StartAppSDK;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainMenuActivity extends AppCompatActivity {

    ImageView   imgTortoise, btnStart, imgCoin1, imgCoin2, imgCoin3, imgLine1, imgLine2,
                imgLine3, imgLine4, imgLine5, imgLine6, btnShare, imgDisk, btnSound;
    TextView    txvCount, txvTitle;
    MediaPlayer mediaShakeTortoise, mediaUpCoin;
    AnimationDrawable shakeT;
    Animation downTortoise, upCoin1, upCoin2, upCoin3, upTortoise;
    private final String SOUND_INFO = "info";
    private final String KEY_SOUND = "keySound";
    CountDownTimer myCountDownTimer, myCountDownTimer2;
    int count = 1;
    String temp;
    String h1, h2, h3, h4, h5, h6;
    String iDHexegram = "";
    String flag = "";
    String idHexe = "";
    int [] arrayLimitCoin = new int[6];
    int sound = 1;
    Hexegram data = new Hexegram();
    Database dataHexegram = new Database(this);
    ArrayList<Integer> arrayList = new ArrayList<>();
    private static final int REQUEST_ID_WRITE_PERMISSION = 2;
    BroadcastReceiver broadcastReceiver;
    public static boolean connected = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        // Admob Banner
        if(CheckInternet.isConnected(this)){
            Map<String, String> admobInfo = Utils.getAdsInfo(this, Global.KEY_ADMOB);
            String appId = admobInfo.get(Global.ADMOB_APP_ID).trim();
            Log.d("admob", "onCreate: " + appId);
            if(appId != null){
                RelativeLayout relativeLayout  = findViewById(R.id.admobBanner);
                AdView mAdView = new AdView(this);
                MobileAds.initialize(this, appId);
                mAdView.setAdSize(AdSize.BANNER);
                mAdView.setAdUnitId(admobInfo.get(Global.ADMOB_BANNER_ID));
                relativeLayout.addView(mAdView);
                AdRequest adRequest = new AdRequest.Builder().addTestDevice("B852C1784AC94383A068EC6C168A15F8").build();
                mAdView.loadAd(adRequest);
            }

            //Quảng cáo StartApp
            Map<String, String> startappInfo = Utils.getAdsInfo(this, Global.KEY_STARTAPP);
            String startappId = startappInfo.get(Global.STARTAPP_APP_ID);
            Log.d("startapp", "onCreate: " + startappId);
            if(startappId != null){
                StartAppSDK.init(this, startappId, true);
                StartAppAd.disableSplash();
            }

            //Load quảng cáo show full ở DetailActivity
            Map<String, String> admobIntertitialId = Utils.getAdsInfo(this, Global.KEY_ADMOB);
            String interstitialId = admobIntertitialId.get(Global.ADMOB_INTERSTITIAL_ID);
            if(interstitialId != null){
                AdsManager adsManager = AdsManager.getInstance();
                adsManager.createAd(this, interstitialId);
            }
        }

        // Hỏi cấp quyền Write
        askPermissionAndWrite();

        Boolean checkUser = Utils.getUserInfo(this, Global.KEY_USER);
        if(checkUser == false){
            Map<String, String> isneedUpdate = Utils.getFlagToken(this);
            String needUpdate = isneedUpdate.get(Global.K_TOKEN);
            if(needUpdate == null){
                createUser("0");
            }else {
                createUser(needUpdate);
            }
        }else {
            Map<String, String> token = Utils.getFlagToken(this);
            String updateFlag = token.get(Global.K_TOKEN);
            String deviceId = Utils.getDeviceId(this);
//            if(updateFlag == null){
//                Utils.setFlagToken(this, "1");
//                SharedPreferences s = this.getSharedPreferences(Global.KEY_USER, MODE_PRIVATE);
//                String userKey = s.getString(Global.K_USERKEY, "");
//                updateFCM(deviceId, userKey, "");
//            }
//            else
            if(updateFlag != null){
                if(updateFlag.equals("1")){
                    SharedPreferences s = this.getSharedPreferences(Global.KEY_USER, MODE_PRIVATE);
                    String userKey = s.getString(Global.K_USERKEY, "");
                    String newToken = Utils.getNewToken(this);
                    updateFCM(deviceId, userKey, newToken);
                }
            }
        }

        getAppVersion();

        mediaShakeTortoise = mediaShakeTortoise.create(MainMenuActivity.this, R.raw.sowhexagram);
        mediaUpCoin = mediaUpCoin.create(MainMenuActivity.this, R.raw.coin);

        btnStart    = (ImageView) findViewById(R.id.btn_done);
        btnSound    = (ImageView) findViewById(R.id.btn_sound);
        imgDisk     = (ImageView) findViewById(R.id.img_disk);
        imgTortoise = (ImageView) findViewById(R.id.img_tortoise);
        imgCoin1    = (ImageView) findViewById(R.id.img_coin1);
        imgCoin2    = (ImageView) findViewById(R.id.img_coin2);
        imgCoin3    = (ImageView) findViewById(R.id.img_coin3);
        imgLine1    = (ImageView) findViewById(R.id.img_line1);
        imgLine2    = (ImageView) findViewById(R.id.img_line2);
        imgLine3    = (ImageView) findViewById(R.id.img_line3);
        imgLine4    = (ImageView) findViewById(R.id.img_line4);
        imgLine5    = (ImageView) findViewById(R.id.img_line5);
        imgLine6    = (ImageView) findViewById(R.id.img_line6);
        btnShare    = (ImageView) findViewById(R.id.btn_share);
        txvCount    = (TextView)  findViewById(R.id.txv_count);
        txvTitle    = (TextView)  findViewById(R.id.txv_title);

        Typeface fontTxv = Typeface.createFromAsset(this.getAssets(), "UTM Azuki.ttf");
        txvTitle.setTypeface(fontTxv);
        txvCount.setTypeface(fontTxv);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                showAlertMessage(intent.getStringExtra("body"), intent.getStringExtra("title"));
            }
        };

        setInvisibleCoin();

        // Kiểm tra xem đã có setting trước khi đó chưa
        SharedPreferences shared = getSharedPreferences(SOUND_INFO,MODE_PRIVATE);
        String string_temp = shared.getString(KEY_SOUND, "").trim();
        if(string_temp.equals("1") == false && string_temp.equals("0") == false){
            setImageSound();
        }else if(string_temp.equals("1")){
            btnSound.setImageResource(R.mipmap.btn_unmute);
            mediaUpCoin.setVolume(1, 1);
            mediaShakeTortoise.setVolume(1, 1);
        }else{
            btnSound.setImageResource(R.mipmap.btn_mute);
            mediaUpCoin.setVolume(0, 0);
            mediaShakeTortoise.setVolume(0, 0);
        }

        // sự kiện nhấn nút mute
        clickBtnSound();

        btnStart.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View view) {
                boolean check = askPermission(REQUEST_ID_WRITE_PERMISSION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE );
                initAnimation();

                if(check == true){
                    arrayList = arrayNumber();
                    count+=1;
                    if(count <= 6){
                        imgTortoise.startAnimation(downTortoise);
                    }else if(count == 7){
                        imgTortoise.startAnimation(downTortoise);
                    }else if(count == 8){
                        Intent intent = new Intent(MainMenuActivity.this, DetailActivity.class);
                        intent.putExtra("key_1", (String) idHexe);
                        intent.putExtra("hao_1",(String) h1);
                        intent.putExtra("hao_2",(String) h2);
                        intent.putExtra("hao_3",(String) h3);
                        intent.putExtra("hao_4",(String) h4);
                        intent.putExtra("hao_5",(String) h5);
                        intent.putExtra("hao_6",(String) h6);
                        startActivity(intent);
                    }
                }else {
                    askPermissionAndWrite();
                }
            }
        });
        buttomShare();
    }


    public void setInvisibleCoin(){
        imgCoin1.setVisibility(View.INVISIBLE);
        imgCoin2.setVisibility(View.INVISIBLE);
        imgCoin3.setVisibility(View.INVISIBLE);
    }

    public void setInvisibleCoin_UpCoin(){
        imgCoin1.clearAnimation();
        imgCoin2.clearAnimation();
        imgCoin3.clearAnimation();
        imgCoin1.setVisibility(View.INVISIBLE);
        imgCoin2.setVisibility(View.INVISIBLE);
        imgCoin3.setVisibility(View.INVISIBLE);
    }

    public void setVisibleCoin(){
        imgCoin1.setVisibility(View.VISIBLE);
        imgCoin2.setVisibility(View.VISIBLE);
        imgCoin3.setVisibility(View.VISIBLE);
    }

    public void setImageCoin(){

        if(arrayList.get(0) == 0){
            imgCoin1.setImageResource(R.mipmap.icon_coin_0);
            if(arrayList.get(1) == 0){
                imgCoin2.setImageResource(R.mipmap.icon_coin_0);
                if(arrayList.get(2) == 0){
                    imgCoin3.setImageResource(R.mipmap.icon_coin_0);
                }else
                    imgCoin3.setImageResource(R.mipmap.icon_coin_1);
            }else {
                imgCoin2.setImageResource(R.mipmap.icon_coin_1);
                if(arrayList.get(2) == 0){
                    imgCoin3.setImageResource(R.mipmap.icon_coin_0);
                }else
                    imgCoin3.setImageResource(R.mipmap.icon_coin_1);
            }
        }else {
            imgCoin1.setImageResource(R.mipmap.icon_coin_1);
            if(arrayList.get(1) == 0){
                imgCoin2.setImageResource(R.mipmap.icon_coin_0);
                if(arrayList.get(2) == 0){
                    imgCoin3.setImageResource(R.mipmap.icon_coin_0);
                }else
                    imgCoin3.setImageResource(R.mipmap.icon_coin_1);
            }else {
                imgCoin2.setImageResource(R.mipmap.icon_coin_1);
                if (arrayList.get(2) == 0) {
                    imgCoin3.setImageResource(R.mipmap.icon_coin_0);
                } else
                    imgCoin3.setImageResource(R.mipmap.icon_coin_1);
            }
        }
    }

    public void setLine(int line){

        int c1 = arrayList.get(0);
        int c2 = arrayList.get(1);
        int c3 = arrayList.get(2);
        int yin = 0, yang = 1;
        int type;
        boolean isDynamic;

        if(c1 == c2 && c2 ==c3){
            isDynamic = true;
            if(c1 == yang){
                type = yang;
            }else {
                type = yin;
            }
        }else {
            isDynamic = false;
            if(c1 == yang && c2 ==yang || c1 == yang && c3 == yang || c2 ==yang && c3 ==yang){
                type = yang;
            }else
                type = yin;
        }
        //idhexegram
        if(isDynamic == true){
            flag = flag.concat("1");
            if(type == 1){
                iDHexegram = iDHexegram.concat("1");
            }else {
                iDHexegram = iDHexegram.concat("0");
            }
        }else {
            flag = flag.concat("0");
            if(type == 1){
                iDHexegram = iDHexegram.concat("1");
            }else {
                iDHexegram = iDHexegram.concat("0");
            }
        }

        //setImageLine
        if(line == 1){
            if(isDynamic == true){
                if(type == yang){
                    imgLine1.setImageResource(R.mipmap.line_dynamic_1);
                    h1 = "dynamic_1";
                }else {
                    imgLine1.setImageResource(R.mipmap.line_dynamic_0);
                    h1 = "dynamic_0";
                }
            }else {
                if(type == yang){
                    imgLine1.setImageResource(R.mipmap.line_normal_1);
                    h1 = "normal_1";
                }else {
                    imgLine1.setImageResource(R.mipmap.line_normal_0);
                    h1 = "normal_0";
                }
            }
        }else if(line == 2){
            if(isDynamic == true){
                if(type == yang){
                    imgLine2.setImageResource(R.mipmap.line_dynamic_1);
                    h2 = "dynamic_1";
                }else {
                    imgLine2.setImageResource(R.mipmap.line_dynamic_0);
                    h2 = "dynamic_0";
                }
            }else {
                if(type == yang){
                    imgLine2.setImageResource(R.mipmap.line_normal_1);
                    h2 = "normal_1";
                }else {
                    imgLine2.setImageResource(R.mipmap.line_normal_0);
                    h2 = "normal_0";
                }
            }
        }else if(line == 3){
            if(isDynamic == true){
                if(type == yang){
                    imgLine3.setImageResource(R.mipmap.line_dynamic_1);
                    h3 = "dynamic_1";
                }else {
                    imgLine3.setImageResource(R.mipmap.line_dynamic_0);
                    h3 = "dynamic_0";
                }
            }else {
                if(type == yang){
                    imgLine3.setImageResource(R.mipmap.line_normal_1);
                    h3 = "normal_1";
                }else {
                    imgLine3.setImageResource(R.mipmap.line_normal_0);
                    h3 = "normal_0";
                }
            }
        }else if(line == 4){
            if(isDynamic == true){
                if(type == yang){
                    imgLine4.setImageResource(R.mipmap.line_dynamic_1);
                    h4 = "dynamic_1";
                }else {
                    imgLine4.setImageResource(R.mipmap.line_dynamic_0);
                    h4 = "dynamic_0";
                }
            }else {
                if(type == yang){
                    imgLine4.setImageResource(R.mipmap.line_normal_1);
                    h4 = "normal_1";
                }else {
                    imgLine4.setImageResource(R.mipmap.line_normal_0);
                    h4 = "normal_0";
                }
            }
        }else if(line == 5){
            if(isDynamic == true){
                if(type == yang){
                    imgLine5.setImageResource(R.mipmap.line_dynamic_1);
                    h5 = "dynamic_1";
                }else {
                    imgLine5.setImageResource(R.mipmap.line_dynamic_0);
                    h5 = "dynamic_0";
                }
            }else {
                if(type == yang){
                    imgLine5.setImageResource(R.mipmap.line_normal_1);
                    h5 = "normal_1";
                }else {
                    imgLine5.setImageResource(R.mipmap.line_normal_0);
                    h5 = "normal_0";
                }
            }
        }else{
            if(isDynamic == true){
                if(type == yang){
                    imgLine6.setImageResource(R.mipmap.line_dynamic_1);
                    h6 = "dynamic_1";
                }else {
                    imgLine6.setImageResource(R.mipmap.line_dynamic_0);
                    h6 = "dynamic_0";
                }
            }else {
                if(type == yang){
                    imgLine6.setImageResource(R.mipmap.line_normal_1);
                    h6 = "normal_1";
                }else {
                    imgLine6.setImageResource(R.mipmap.line_normal_0);
                    h6 = "normal_0";
                }
            }
        }

    //return IDHexegram

    }

    public void setTitle(){

        String idDaoNguoc = reverseID(iDHexegram);
        String flagHD = reverseID(flag);

        String kyTu;
        String flag;
        for(int i = 0 ; i <idDaoNguoc.length(); i++){
            flag = String.valueOf(flagHD.charAt(i));
            kyTu = String.valueOf(idDaoNguoc.charAt(i));
            if(flag.equals("1")){
                if(kyTu.equals("0")){
                    kyTu = "1";
                }else {
                    kyTu = "0";
                }
            }
            idHexe = idHexe + kyTu;
        }


        data = dataHexegram.getValues(idHexe);

        String name = data.getH_name();
        txvTitle.setTextSize(15);
        txvTitle.setText(name);


    }

    //dao nguoc chuoi
    public String reverseID(String id){
        String rv = new StringBuffer(id).reverse().toString();
        return  rv;
    }

    public void buttomShare(){
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

    private void askPermissionAndWrite() {
        boolean canWrite = askPermission(REQUEST_ID_WRITE_PERMISSION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        //
        if (canWrite) {
            createDB();
        }
    }

    // Với Android Level >= 23 bạn phải hỏi người dùng cho phép các quyền với thiết bị
    private boolean askPermission(int requestId, String permissionName) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {

            // Kiểm tra quyền
            int permission = ActivityCompat.checkSelfPermission(MainMenuActivity.this, permissionName);

            if (permission != PackageManager.PERMISSION_GRANTED) {

                // Nếu không có quyền, cần nhắc người dùng cho phép.
                ActivityCompat.requestPermissions(MainMenuActivity.this, new String[]{permissionName}, requestId);
                return false;
            }
        }
        return true;
    }

    // Khi yêu cầu hỏi người dùng được trả về (Chấp nhận hoặc không chấp nhận).
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Chú ý: Nếu yêu cầu bị hủy, mảng kết quả trả về là rỗng.
        if (grantResults.length > 0) {
            switch (requestCode) {
                case REQUEST_ID_WRITE_PERMISSION: {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        createDB();
                    }
                }
            }
        } else {
        }
    }

    public void createDB(){
        dataHexegram.createDB();
    }

    public ArrayList<Integer> arrayNumber (){
        Random rD = new Random();
        ArrayList<Integer> list = new ArrayList<>();
        ArrayList<Integer> listCoin = new ArrayList<>();
        int even = 50;
        int odd = 50;
        for(int i=0; i<100; i++){
            int rand = random();
            if(even == 0){
                rand = 0;
                even -= 1;
            } else if(odd == 0) {
                rand = 1;
                odd -= 1;
            } else {
                if(rand == 0){
                    even -= 1;
                } else {
                    odd -= 1;
                }
            }
            list.add(rand);
        }

        int i1 = rD.nextInt(100);
        int i2 = rD.nextInt(100);
        int i3 = rD.nextInt(100);

        int c1 = list.get(i1);
        int c2 = list.get(i2);
        int c3 = list.get(i3);

        listCoin.add(c1);
        listCoin.add(c2);
        listCoin.add(c3);

        return  listCoin;
    }

    private int random(){
        int temp;
        Random random = new Random();
        temp = random.nextInt(10);
        if(temp % 2 == 0) {
            return 0;
        }
        else{
            return 1;
        }
    }

    public void initAnimationDownTortoise(int distance){
        downTortoise = new TranslateAnimation(0, 0,0, distance);
        downTortoise.setFillAfter(true);
        downTortoise.setDuration(1000);

        downTortoise.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                btnStart.setEnabled(false);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imgTortoise.setImageResource(R.drawable.shaketortoise);
                shakeT = (AnimationDrawable) imgTortoise.getDrawable();
                shakeT.start();
                mediaShakeTortoise.start();
//                mediaShakeTortoise.prepareAsync();
                myCountDownTimer = new CountDownTimer(2000,1000) {
                    @Override
                    public void onTick(long l) {

                    }

                    @Override
                    public void onFinish() {
                        shakeT.stop();
                        mediaShakeTortoise.stop();
                        try {
                            mediaShakeTortoise.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        setImageCoin();
                        setVisibleCoin();
                        imgCoin1.startAnimation(upCoin1);
                        imgCoin2.startAnimation(upCoin2);
                        imgCoin3.startAnimation(upCoin3);

                        temp = Integer.toString(count);
                        if(count <= 6){
                            txvCount.setText("Hào "+temp);
                        }
                        if(count == 7){
                            txvCount.setText("");
                            btnStart.setImageResource(R.mipmap.button_show);
                        }
                        myCountDownTimer2 = new CountDownTimer(3000,1000) {
                            @Override
                            public void onTick(long l) {
                            }

                            @Override
                            public void onFinish() {
                                imgTortoise.startAnimation(upTortoise);

                            }
                        };
                        myCountDownTimer2.start();
                    }
                };
                myCountDownTimer.start();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void initAnimationUpTortoise(int distance){
        upTortoise = new TranslateAnimation(0,0,distance,0);
        upTortoise.setDuration(1000);
        upTortoise.setFillAfter(true);

        upTortoise.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setInvisibleCoin_UpCoin();
                btnStart.setEnabled(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


    public void initAnimationUpCoin(int arrayLimitCoin[], int widthDisk, int distanceCoin1_Disk,
                                    int distanceCoin2_Disk, int distanceCoin3_Disk){

        upCoin1 = new TranslateAnimation(0, - widthDisk/6 + arrayLimitCoin[0],0,
                 - distanceCoin2_Disk + arrayLimitCoin[1] );
        upCoin2 = new TranslateAnimation(0, 0 + arrayLimitCoin[2],0,
                 - distanceCoin1_Disk + arrayLimitCoin[3]);
        upCoin3 = new TranslateAnimation(0, widthDisk/6 + arrayLimitCoin[4],0,
                 - distanceCoin3_Disk + arrayLimitCoin[5]);

        upCoin1.setDuration(1000);
        upCoin2.setDuration(1000);
        upCoin3.setDuration(1000);

        upCoin1.setFillAfter(true);
        upCoin2.setFillAfter(true);
        upCoin3.setFillAfter(true);


        upCoin1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mediaUpCoin.start();
//                mediaUpCoin.prepareAsync();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mediaUpCoin.stop();
                try {
                    mediaUpCoin.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                setLine(count - 1);
                if(count == 7){
                    setTitle();
                }

            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

        public int[] randomLimitCoin(){
            int[] arrayLimit = new int[6];

        Random rd = new Random();
        int i;
        for(i = 0; i<=5;i++){
            int temp = rd.nextInt(30);
            arrayLimit[i] = temp;
        }
        for(i = 0; i <= 5; i++){
            if(arrayLimit[i] % 2 == 0){
                arrayLimit[i] = arrayLimit[i]*1;
            }else
                arrayLimit[i] = arrayLimit[i]*(-1);
        }
        return arrayLimit;
    }

    public void initAnimation(){
        arrayLimitCoin = randomLimitCoin();

        // lấy tọa độ y của imgDisk
        int[] location = new int[2];
        imgDisk.getLocationOnScreen(location);
        int yImgDisk = location[1];

        // Lấy tọa độ y của BtnStart
        int[] location2 = new int[2];
        btnStart.getLocationOnScreen(location2);
        int yBtnStart = location2[1];

        int heightDisk = imgDisk.getHeight();
        int widthDisk = imgDisk.getWidth();
        int heightCoin = imgCoin1.getHeight();

        // Lấy tọa độ y của ImgCoin
        int yImgCoin = yBtnStart - heightCoin;

        // Khoảng cách của các đồng xu đến ImgDisk( Điểm cố định)
        int distanceCoin1_Disk = yImgCoin - yImgDisk - heightDisk/5 ;
        int distanceCoin2_Disk = yImgCoin - yImgDisk - 3*heightDisk/6;
        int distanceCoin3_Disk = yImgCoin - yImgDisk - 3*heightDisk/6;

        // Khoảng cách khi di chuyển mai rùa
        int distance = yBtnStart - yImgDisk - heightDisk/2;

        initAnimationDownTortoise(distance);

        initAnimationUpTortoise(distance);

        initAnimationUpCoin(arrayLimitCoin,widthDisk, distanceCoin1_Disk, distanceCoin2_Disk, distanceCoin3_Disk );
    }

    // click Button Sound
    public void clickBtnSound(){
        btnSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences share = getSharedPreferences(SOUND_INFO,MODE_PRIVATE);
                String string_temp = share.getString(KEY_SOUND, "");
                if(string_temp.equals("1")){
                    sound = 0;
                    mediaShakeTortoise.setVolume(0,0);
                    mediaUpCoin.setVolume(0, 0);
                    SharedPreferences pref;
                    pref = getSharedPreferences(SOUND_INFO, MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString(KEY_SOUND,"" + sound);
                    editor.commit();
                    btnSound.setImageResource(R.mipmap.btn_mute);
                }else if(string_temp.equals("0")){
                    sound = 1;
                    mediaShakeTortoise.setVolume(1,1);
                    mediaUpCoin.setVolume(1, 1);
                    btnSound.setImageResource(R.mipmap.btn_unmute);
                    SharedPreferences pref;
                    pref = getSharedPreferences(SOUND_INFO, MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString(KEY_SOUND,"" + sound);
                    editor.commit();
                }

            }
        });
    }

    // set default image Btn Sound
    public void setImageSound(){
        SharedPreferences pref;
        pref = getSharedPreferences(SOUND_INFO, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_SOUND,"" + sound);
        editor.commit();
        btnSound.setImageResource(R.mipmap.btn_unmute);
    }

    public void createUser(final String needUpdateFCM){
        final String country = this.getResources().getConfiguration().locale.getCountry();
        final String deviceId = Utils.getDeviceId(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global.URL_MAIN_CREATE_USER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("voll", "onResponse: " + response);
                try {
                    readRequestCreateUser(response, needUpdateFCM);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.setFlagToken(MainMenuActivity.this, "1");
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("apikey", Global.APIKEY);
//                params.put("Content-Type", "application/json");
                return params;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                final String body = "&act=newuser&os=android&deviceid=" + deviceId + "&location=" + country + "&appid=1" ;
                try {
                    return body.getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };

        ApplicationController.getInstance(this).addToRequestQueue(stringRequest);
    }

    public void getAppVersion(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Global.URL_MAIN_GET_VERSION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("responseGetAppVersion", "onResponse: " + response);
                try {
                    readGetAppVersion(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("responseGetAppVersion", "onResponse: " + error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("apikey", Global.APIKEY);
                headers.put("Content-Type", "application/x-www-form-urlencoded");

                return headers;
            }
        };

        ApplicationController.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void readRequestCreateUser(String response, String needUpdateFCM) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        JSONObject info_user = jsonObject.getJSONObject("payload");

        String user_id = info_user.getString("user_id");
        String user_key = info_user.getString("user_key");

        if(user_id != null && user_key != null){
            Utils.saveUserInfo(this, Global.KEY_USER, user_id, user_key);
            if(needUpdateFCM.equals("1")){
                String newToken = Utils.getNewToken(this);
                updateFCM(Utils.getDeviceId(this), user_key, newToken);
            }
        }
    }

    private void readGetAppVersion(String response) throws JSONException, PackageManager.NameNotFoundException {
            JSONObject json = new JSONObject(response);
            JSONObject info = json.getJSONObject("payload");
            String versionName = info.optString("version_name");
            String versionCode = info.optString("version_check");
            String linkUpdate = info.optString("store_url");
            int need_update = info.getInt("need_update");
            if (!versionCode.equals(String.valueOf(BuildConfig.VERSION_CODE)))
                showAlertUpdate(versionName, linkUpdate, need_update);
    }

    private void showAlertUpdate(String versionName, final String link, int update) throws PackageManager.NameNotFoundException {
        PackageManager manager = this.getPackageManager();
        PackageInfo info = manager.getPackageInfo(
                this.getPackageName(), 0);
        String version = info.versionName;

//        if(!versionName.equals(version)){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(false);
                builder.setMessage("Ứng dụng đang có phiên bản " + versionName + ". Vui lòng cập nhật!")
                        .setTitle("Cập Nhật Ứng Dụng")
                        .setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(link));
                                startActivity(i);
                            }
                        });
                if(update == 0){
                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                }
                Dialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
//        }
    }

    private void showAlertMessage(String message, String title){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(message)
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

    public void updateFCM(final String deviceId, final String userKey, final String newToken){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global.URL_UPDATE_FCM, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("fcm", "onResponse: " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int requestError = jsonObject.getInt("error");
                    if(requestError == 0){
                        Utils.setFlagToken(MainMenuActivity.this, "0");
                    }else
                        Utils.setFlagToken(MainMenuActivity.this, "1");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("fcmerror", "onErrorResponse: " + error.toString());
                Utils.setFlagToken(MainMenuActivity.this, "1");
            }
        }){
            @Override
            public byte[] getBody() throws AuthFailureError {
                final String body = "&act=updatefcm&userkey="+ userKey +"&deviceid=" + deviceId + "&fcm=" + newToken;
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
        ApplicationController.getInstance(this).addToRequestQueue(stringRequest);
    }

    public void restoreMain(){
        count = 1;
        imgLine1.setImageResource(R.mipmap.line_bg);
        imgLine2.setImageResource(R.mipmap.line_bg);
        imgLine3.setImageResource(R.mipmap.line_bg);
        imgLine4.setImageResource(R.mipmap.line_bg);
        imgLine5.setImageResource(R.mipmap.line_bg);
        imgLine6.setImageResource(R.mipmap.line_bg);
        btnStart.setImageResource(R.mipmap.button_done);
        txvTitle.setText("Thành tâm khấn nguyện!");
        txvTitle.setTextSize(20);
        txvCount.setText("Hào 1");
        txvCount.setTextSize(15);
        idHexe = "";
        flag = "";
        iDHexegram = "";

    }

    @Override
    protected void onResume() {
        super.onResume();
        restoreMain();
        IntentFilter filter = new IntentFilter("sendMessageBroadcast");
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

}