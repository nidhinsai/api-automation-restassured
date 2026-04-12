package com.nidhinsai.api.tests;

import com.nidhinsai.api.utils.RequestSpecFactory;
import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;

public abstract class BaseApiTest {
    @BeforeClass
    public void setUp() {
        RestAssured.requestSpecification = RequestSpecFactory.defaultSpec();
    }
}