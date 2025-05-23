package com.example.coindeskapi.dto;

import java.util.List;

public class ConvertedResponse {
    private String updated;
    private List<ConvertedCurrency> currencyList;

    public ConvertedResponse(String updated, List<ConvertedCurrency> currencyList) {
        this.updated = updated;
        this.currencyList = currencyList;
    }

    public String getUpdated() {
        return updated;
    }

    public List<ConvertedCurrency> getCurrencyList() {
        return currencyList;
    }
}
