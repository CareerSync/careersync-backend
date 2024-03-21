package com.example.demo.src.subscription.model;

import com.example.demo.src.item.entity.Item;
import com.example.demo.src.item.model.GetItemRes;
import com.example.demo.src.subscription.entity.Subscription;
import com.example.demo.src.subscription.entity.Subscription.SubscriptionState;
import com.example.demo.src.user.entity.User;
import com.example.demo.src.user.model.GetUserRes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetSubscriptionRes {
    private Long id;
    private GetUserRes user;
    private GetItemRes item;
    private LocalDate nextPaymentDate;
    private SubscriptionState subscriptionState;

    public GetSubscriptionRes(Subscription subscription, User user, Item item) {
        this.id = subscription.getId();
        this.user = new GetUserRes(user);
        this.item = new GetItemRes(item);
        this.nextPaymentDate = subscription.getNextPaymentDate();
        this.subscriptionState = subscription.getSubscriptionState();
    }

    public GetSubscriptionRes(Subscription subscription) {
    }
}
