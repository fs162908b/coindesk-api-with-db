package com.example.coindeskapi.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Currency {

    @Id
    private String code; // 幣別代碼，例如 USD、EUR

    private String name; // 幣別中文名稱

    public Currency() {
    }

    public Currency(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
