package com.example.coindeskapi.service;

import com.example.coindeskapi.dto.ConvertedCurrency;
import com.example.coindeskapi.dto.ConvertedResponse;
import com.example.coindeskapi.model.Currency;
import com.example.coindeskapi.model.CurrencyDetail;
import com.example.coindeskapi.repository.CurrencyRepository;
import org.json.JSONObject;
import org.json.JSONException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class CoindeskService {
    private static final String COINDESK_API_URL = "https://kengp3.github.io/blog/coindesk.json";
    private final RestTemplate restTemplate = new RestTemplate();
    private final CurrencyRepository currencyRepository;

    public CoindeskService(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    public String getRawData() {
        return restTemplate.getForObject(COINDESK_API_URL, String.class);
    }

    public ConvertedResponse getConvertedData() {
        String jsonString = getRawData();
        JSONObject json = new JSONObject(jsonString);
        String updatedISO = json.getJSONObject("time").getString("updatedISO");

        ZonedDateTime time = ZonedDateTime.parse(updatedISO);
        String formattedTime = time.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));

        JSONObject bpi = json.getJSONObject("bpi");

        List<Currency> allCurrencies = currencyRepository.findAll();
        List<ConvertedCurrency> resultList = new ArrayList<>();

        for (Currency currency : allCurrencies) {
            String code = currency.getCode();
            String name = currency.getName();
            Double rate = null;

            if (bpi.has(code)) {
                JSONObject item = bpi.getJSONObject(code);
                rate = item.getDouble("rate_float");
            }

            resultList.add(new ConvertedCurrency(code, name, rate));
        }

        return new ConvertedResponse(formattedTime, resultList);
    }

}
