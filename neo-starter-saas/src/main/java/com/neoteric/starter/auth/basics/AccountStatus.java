package com.neoteric.starter.auth.basics;

public enum AccountStatus {

    INACTIVE("INACTIVE"),
    TERMS_OF_SERVICE_LACK("TERMS_OF_SERVICE_LACK"),
    ACTIVE("ACTIVE"),
    BLOCKED("BLOCKED"),
    UNKNOWN("UNKNOWN");

    private String value;

    private AccountStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
