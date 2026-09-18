package com.sara.qa.base;

import com.sara.qa.api.StoreApiClient;
import com.sara.qa.config.Config;
import org.junit.jupiter.api.BeforeEach;

public abstract class BaseApiTest {

    protected StoreApiClient client;

    @BeforeEach
    void newApiClient() {
        client = new StoreApiClient(Config.appBaseUrl());
    }
}