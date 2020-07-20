package com.cannshine.Fortune.Entities;

public class Hexegram {
    private String h_ID;
    private int number;
    private String h_name;
    private String h_mean;
    private String h_description;
    private String h_content;
    private String h_wao1;
    private String h_wao2;
    private String h_wao3;
    private String h_wao4;
    private String h_wao5;
    private String h_wao6;

    public Hexegram(String h_ID, String h_name, String h_mean, String h_description,
                    String h_content, String h_wao1, String h_wao2, String h_wao3,
                    String h_wao4, String h_wao5, String h_wao6) {
        this.h_ID = h_ID;
        this.h_name = h_name;
        this.h_mean = h_mean;
        this.h_description = h_description;
        this.h_content = h_content;
        this.h_wao1 = h_wao1;
        this.h_wao2 = h_wao2;
        this.h_wao3 = h_wao3;
        this.h_wao4 = h_wao4;
        this.h_wao5 = h_wao5;
        this.h_wao6 = h_wao6;
    }

    public Hexegram() {
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getH_ID() {
        return h_ID;
    }

    public void setH_ID(String h_ID) {
        this.h_ID = h_ID;
    }

    public String getH_name() {
        return h_name;
    }

    public void setH_name(String h_name) {
        this.h_name = h_name;
    }

    public String getH_mean() {
        return h_mean;
    }

    public void setH_mean(String h_mean) {
        this.h_mean = h_mean;
    }

    public String getH_description() {
        return h_description;
    }

    public void setH_description(String h_description) {
        this.h_description = h_description;
    }

    public String getH_content() {
        return h_content;
    }

    public void setH_content(String h_content) {
        this.h_content = h_content;
    }

    public String getH_wao1() {
        return h_wao1;
    }

    public void setH_wao1(String h_wao1) {
        this.h_wao1 = h_wao1;
    }

    public String getH_wao2() {
        return h_wao2;
    }

    public void setH_wao2(String h_wao2) {
        this.h_wao2 = h_wao2;
    }

    public String getH_wao3() {
        return h_wao3;
    }

    public void setH_wao3(String h_wao3) {
        this.h_wao3 = h_wao3;
    }

    public String getH_wao4() {
        return h_wao4;
    }

    public void setH_wao4(String h_wao4) {
        this.h_wao4 = h_wao4;
    }

    public String getH_wao5() {
        return h_wao5;
    }

    public void setH_wao5(String h_wao5) {
        this.h_wao5 = h_wao5;
    }

    public String getH_wao6() {
        return h_wao6;
    }

    public void setH_wao6(String h_wao6) {
        this.h_wao6 = h_wao6;
    }
}
