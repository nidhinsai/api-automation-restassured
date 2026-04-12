package com.nidhinsai.api.utils;

import com.nidhinsai.api.filters.RequestResponseLoggingFilter;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class RequestSpecFactory {

    private static final Logger LOG = LogManager.getLogger(RequestSpecFactory.class);

    private RequestSpecFactory() {
    }

    public static RequestSpecification defaultSpec() {
        String baseUri = ConfigManager.get("base.url", "https://reqres.in");
        LOG.debug("Building default request spec for base URI: {}", baseUri);
        return new RequestSpecBuilder()
                .setBaseUri(baseUri)
                .setContentType(ContentType.JSON)
                .addFilter(new RequestResponseLoggingFilter())
                .build();
    }

    public static RequestSpecification authorizedSpec(String token) {
        String baseUri = ConfigManager.get("base.url", "https://reqres.in");
        LOG.debug("Building authorized request spec for base URI: {}", baseUri);
        return new RequestSpecBuilder()
                .setBaseUri(baseUri)
                .setContentType(ContentType.JSON)
                .addHeader("Authorization", "Bearer " + token)
                .addFilter(new RequestResponseLoggingFilter())
                .build();
    }
}