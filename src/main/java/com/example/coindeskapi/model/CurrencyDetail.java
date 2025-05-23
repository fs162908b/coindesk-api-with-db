package com.example.coindeskapi.model;

public class CurrencyDetail {

    private String code;
    private String name;
    private double rate;
    private String updated;

    public CurrencyDetail(String code, String name, double rate, String updated) {
        this.code = code;
        this.name = name;
        this.rate = rate;
        this.updated = updated;
    }

    // 若還沒寫 getter 可加入
    public String getCode() { return code; }
    public String getName() { return name; }
    public double getRate() { return rate; }
    public String getUpdated() { return updated; }

    public boolean isRateAvailable() {
        return rate != 0;
    }
}