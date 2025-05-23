package com.example.coindeskapi.controller;

import com.example.coindeskapi.model.Currency;
import com.example.coindeskapi.service.CurrencyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/currencies")
public class CurrencyController {

    private final CurrencyService currencyService;

    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @PostMapping("/action")
    public ResponseEntity<?> handleAction(@RequestBody Map<String, Object> payload) {
        String action = (String) payload.get("action");
        Map<String, Object> data = (Map<String, Object>) payload.get("data");

        if (action == null || data == null) {
            return ResponseEntity.badRequest().body("缺少 action 或 data 欄位");
        }

        switch (action.toLowerCase()) {
            case "create":
                return handleCreate(data);
            case "read":
                return handleRead(data);
            case "update":
                return handleUpdate(data);
            case "delete":
                return handleDelete(data);
            case "list":
                return ResponseEntity.ok(currencyService.findAll());
            default:
                return ResponseEntity.badRequest().body("未知的 action");
        }
    }

    private ResponseEntity<?> handleCreate(Map<String, Object> data) {
        String code = (String) data.get("code");
        String name = (String) data.get("name");

        if (code == null || name == null) {
            return ResponseEntity.badRequest().body("缺少 code 或 name");
        }

        if (currencyService.findById(code).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("幣別代碼已存在");
        }

        Currency currency = new Currency(code, name);
        return ResponseEntity.ok(currencyService.save(currency));
    }

    private ResponseEntity<?> handleRead(Map<String, Object> data) {
        String code = (String) data.get("code");
        if (code == null) return ResponseEntity.badRequest().body("缺少 code");

        return currencyService.findById(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private ResponseEntity<?> handleUpdate(Map<String, Object> data) {
        String code = (String) data.get("code");
        String name = (String) data.get("name");

        if (code == null || name == null) {
            return ResponseEntity.badRequest().body("缺少 code 或 name");
        }

        return currencyService.findById(code)
                .map(existing -> {
                    existing.setName(name);
                    return ResponseEntity.ok(currencyService.save(existing));
                }).orElse(ResponseEntity.notFound().build());
    }

    private ResponseEntity<?> handleDelete(Map<String, Object> data) {
        String code = (String) data.get("code");
        if (code == null) return ResponseEntity.badRequest().body("缺少 code");

        if (currencyService.findById(code).isPresent()) {
            currencyService.deleteById(code);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}