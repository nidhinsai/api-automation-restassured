package com.nidhinsai.api.utils;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public final class RequestSpecFactory {
    private RequestSpecFactory() {
    }

    public static RequestSpecification defaultSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(ConfigManager.get("base.url", "https://reqres.in"))
                .setContentType(ContentType.JSON)
                .build();
    }

    public static RequestSpecification authorizedSpec(String token) {
        return new RequestSpecBuilder()
                .setBaseUri(ConfigManager.get("base.url", "https://reqres.in"))
                .setContentType(ContentType.JSON)
                .addHeader("Authorization", "Bearer " + token)
                .build();
    }
}