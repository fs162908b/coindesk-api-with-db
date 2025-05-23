package com.example.coindeskapi.controller;

import com.example.coindeskapi.service.CoindeskService;
import com.example.coindeskapi.dto.ConvertedResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CoindeskController {

    private final CoindeskService coindeskService;

    public CoindeskController(CoindeskService coindeskService) {
        this.coindeskService = coindeskService;
    }

    @GetMapping("/coindesk/raw")
    public String getRawData() {
        return coindeskService.getRawData();
    }

    @GetMapping("/coindesk/converted")
    public ConvertedResponse getConvertedData() {
        return coindeskService.getConvertedData();
    }
}
