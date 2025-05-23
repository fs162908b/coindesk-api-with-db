package com.example.coindeskapi.service;

import com.example.coindeskapi.model.Currency;
import com.example.coindeskapi.repository.CurrencyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CurrencyService {

    private final CurrencyRepository currencyRepository;

    public CurrencyService(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    public List<Currency> findAll() {
        return currencyRepository.findAll();
    }

    public Optional<Currency> findById(String code) {
        return currencyRepository.findById(code);
    }

    public Currency save(Currency currency) {
        return currencyRepository.save(currency);
    }

    public void deleteById(String code) {
        currencyRepository.deleteById(code);
    }
    
    public Currency create(String code, String name) {
        return save(new Currency(code, name));
    }

    public Optional<Currency> read(String code) {
        return findById(code);
    }

    public Optional<Currency> update(String code, String name) {
        return findById(code).map(currency -> {
            currency.setName(name);
            return save(currency);
        });
    }

    public boolean delete(String code) {
        if (findById(code).isPresent()) {
            deleteById(code);
            return true;
        }
        return false;
    }
    
}
