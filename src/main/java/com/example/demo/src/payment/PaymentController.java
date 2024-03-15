package com.example.demo.src.payment;

import com.siot.IamportRestClient.IamportClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;

@Controller
@Slf4j
@RequiredArgsConstructor
public class PaymentController {

    private IamportClient iamportClient;

    @Value("${spring.imp.api-key}")
    private String apiKey;

    @Value("${spring.imp.api-secret-key}")
    private String secretKey;

    @PostConstruct
    public void init() {
        this.iamportClient = new IamportClient(apiKey, secretKey);
    }

}
