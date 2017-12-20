package com.example.ks.mobileverificationsystem;

class Country {
    private String name;
    private String dial_code;
    private String code;

    Country(String name, String dial_code, String code) {
        this.name = name;
        this.dial_code = dial_code;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    String getDial_code() {
        return dial_code;
    }
}
