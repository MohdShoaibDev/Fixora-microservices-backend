package com.shoaib.aiservice.util;

public enum FixoraCategory {
    PLUMBER("Plumber"), ELECTRICIAN("Electrician"), CARPENTER("Carpenter"),
    PAINTER("Painter"), CLEANER("Cleaner"), WATER_PURIFIER("Water Purifier"),
    AC("AC"), WOMEN_SALON("Women Salon"), MEN_SALON("Men Salon"),
    GENERAL_INFORMATION("General Information"), UNSUPPORTED("Unsupported");

    private final String friendlyName;

    FixoraCategory(String friendlyName) { this.friendlyName = friendlyName; }
    public String friendlyName() { return friendlyName; }
}
