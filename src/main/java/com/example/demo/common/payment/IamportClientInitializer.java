package com.example.demo.common.payment;

import com.siot.IamportRestClient.IamportClient;

public class IamportClientInitializer {

    private final String apiKey;
    private final String secretKey;

    public IamportClientInitializer(String apiKey, String secretKey) {
        this.apiKey = apiKey;
        this.secretKey = secretKey;
    }

    public IamportClient initializeIamportClient() {
        return new IamportClient(apiKey, secretKey);
    }

}
