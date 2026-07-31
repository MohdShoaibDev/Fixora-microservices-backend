package com.shoaib.authservice.utility;

public enum UserStatus {
    ACTIVE(1),
    INACTIVE(0);

    private final int value;

    UserStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static UserStatus fromValue(int value) {
        for (UserStatus status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }
}
