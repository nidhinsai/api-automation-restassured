package com.nidhinsai.api.utils;

public final class TokenManager {
    private static String token;

    private TokenManager() {
    }

    public static String getToken() {
        if (token == null) {
            token = "demo-token";
        }
        return token;
    }
}