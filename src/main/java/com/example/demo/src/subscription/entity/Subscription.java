package com.example.demo.src.subscription.entity;

import com.example.demo.common.entity.BaseEntity;
import com.example.demo.src.payment.entity.Payment;
import com.example.demo.src.service.entity.Item;
import com.example.demo.src.user.entity.User;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static javax.persistence.FetchType.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@JsonAutoDetect(fieldVisibility = ANY)
@Table(name = "TB_SUBSCRIPTION")
public class Subscription extends BaseEntity {

    @Id
    @Column(name = "subscriptionId", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "serviceId")
    private Item item;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private SubscriptionState subscriptionState;

    public enum SubscriptionState {
        SUCCESS, FAIL
    }

    @Builder
    public Subscription(Long id, User user, Item item, SubscriptionState subscriptionState) {
        this.id = id;
        this.user = user;
        this.item = item;
        this.subscriptionState = subscriptionState;
    }

}
