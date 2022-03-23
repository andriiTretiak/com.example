package com.example;

public enum OrderedBy {
    START_TIME("startTime"),
    UPDATED("updated"),
    ;
    private final String value;

    OrderedBy(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
