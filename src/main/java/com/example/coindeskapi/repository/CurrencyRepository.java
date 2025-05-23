package com.example.coindeskapi.repository;

import com.example.coindeskapi.model.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRepository extends JpaRepository<Currency, String> {
}
