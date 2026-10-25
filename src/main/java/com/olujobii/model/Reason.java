package com.olujobii.model;

public enum Reason {
    FORBIDDEN_WORDS ("Forbidden words"),
    UNPROFESSIONAL ("Unprofessional"),
    DISRESPECTFUL ("Disrespectful"),
    POLITICAL ("Political"),
    NO_ISSUE ("No issue");

    private final String value;

    Reason(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
