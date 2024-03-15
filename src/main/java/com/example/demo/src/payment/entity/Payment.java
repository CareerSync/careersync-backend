package com.example.demo.src.payment.entity;

import com.example.demo.common.entity.BaseEntity;
import com.example.demo.src.service.entity.Item;
import com.example.demo.src.user.entity.User;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static javax.persistence.FetchType.LAZY;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@JsonAutoDetect(fieldVisibility = ANY)
@Table(name = "TB_PAYMENT")
public class Payment extends BaseEntity {

    @Id
    @Column(name = "paymentId", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "serviceId")
    private Item item;

    @Column(nullable = false)
    private String impUid;

    @Column(nullable = false)
    private String merchantUid;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private PaymentState paymentState;

    public enum PaymentState {
        SUCCESS, FAIL;
    }

    @Builder
    public Payment(Long id, User user, Item item, String impUid, String merchantUid, PaymentState paymentState) {
        this.id = id;
        this.user = user;
        this.item = item;
        this.impUid = impUid;
        this.merchantUid = merchantUid;
        this.paymentState = paymentState;
    }

    public void updateState(State state) {
        this.state = state;
    }

}
