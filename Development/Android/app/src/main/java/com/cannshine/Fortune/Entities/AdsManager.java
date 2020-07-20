package com.cannshine.Fortune.Entities;

import android.content.Context;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class AdsManager {
    private static AdsManager singleton;
    static InterstitialAd interstitialAd;

    public AdsManager() {
    }
    public static AdsManager getInstance() {
        if (singleton == null) {
            singleton = new AdsManager();
        }
        return singleton;
    }
    public void createAd(Context context, String myInterstitialId) {
        interstitialAd = new InterstitialAd(context);
        interstitialAd.setAdUnitId(myInterstitialId);
        interstitialAd.loadAd(new AdRequest.Builder().addTestDevice("B852C1784AC94383A068EC6C168A15F8").build());
    }
    public static InterstitialAd getAd() {
        return interstitialAd;
    }
}
