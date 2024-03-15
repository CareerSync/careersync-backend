package com.example.demo.src.payment.model;

import com.example.demo.src.payment.entity.Payment;
import com.example.demo.src.service.model.GetItemRes;
import com.example.demo.src.user.model.GetUserRes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.example.demo.src.payment.entity.Payment.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetPaymentRes {
    private Long id;
    private String impUid;
    private String merchantUid;
    private PaymentState paymentState;
    private GetUserRes user;
    private GetItemRes item;

    public GetPaymentRes(Payment payment) {
        this.id = payment.getId();
        this.impUid = payment.getImpUid();
        this.merchantUid = payment.getMerchantUid();
        this.paymentState = payment.getPaymentState();
        this.user = new GetUserRes(payment.getUser());
        this.item = new GetItemRes(payment.getItem());
    }
}
