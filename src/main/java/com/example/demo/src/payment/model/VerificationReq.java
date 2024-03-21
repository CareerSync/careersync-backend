package com.example.demo.src.payment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VerificationReq {
    private String impUid;
    private String merchantUid;
    private String name;
    private String buyerEmail;
    private int amount;
}
