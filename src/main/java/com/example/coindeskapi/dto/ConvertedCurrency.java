package com.example.coindeskapi.dto;

public class ConvertedCurrency {
    private String code;
    private String name;
    private Double rate;

    public ConvertedCurrency(String code, String name, Double rate) {
        this.code = code;
        this.name = name;
        this.rate = rate;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public Double getRate() {
        return rate;
    }
}
