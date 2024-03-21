package com.example.demo.src.subscription.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatchSubscriptionReq {
    private LocalDate nextPaymentDate;
}
