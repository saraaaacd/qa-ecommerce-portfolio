package com.jesus.qa.base;

import com.jesus.qa.api.StoreApiClient;
import com.jesus.qa.config.Config;
import org.junit.jupiter.api.BeforeEach;

public abstract class BaseApiTest {

    protected StoreApiClient client;

    @BeforeEach
    void newApiClient() {
        client = new StoreApiClient(Config.appBaseUrl());
    }
}