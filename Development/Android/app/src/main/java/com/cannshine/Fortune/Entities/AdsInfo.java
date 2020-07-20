package com.cannshine.Fortune.Entities;

public class AdsInfo {
    int id;
    String code;
    String ads_info;

    public AdsInfo(int id, String code, String ads_info) {
        this.id = id;
        this.code = code;
        this.ads_info = ads_info;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAds_info() {
        return ads_info;
    }

    public void setAds_info(String ads_info) {
        this.ads_info = ads_info;
    }
}
