package com.shoaib.redis;


public final class RedisKeys {

    private RedisKeys() {}

    public static final String OTP_PREFIX = "fixora:otp:";
    public static final String LOGOUT_PREFIX = "fixora:logout:";
    public static final String OTP_RATE_LIMIT = "fixora:otp_rate_limit:";
    public static final String LOGIN_RATE_LIMIT = "fixora:login_rate_limit:";
    public static final String REFRESH_TOKEN_PREFIX =  "fixora:refresh_token:";
}