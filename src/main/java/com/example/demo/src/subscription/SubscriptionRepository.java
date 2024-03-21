package com.example.demo.src.subscription;

import com.example.demo.src.item.entity.Item;
import com.example.demo.src.subscription.entity.Subscription;
import com.example.demo.src.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

import static com.example.demo.common.entity.BaseEntity.*;
import static com.example.demo.src.subscription.entity.Subscription.*;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findByIdAndState(Long id, State state);
    Optional<Subscription> findByUserAndState(User user, State state);
    Optional<Subscription> findByUserAndItem(User user, Item item);
    Optional<Subscription> findByUserAndItemAndSubscriptionState(User user, Item item, SubscriptionState subscriptionState);
    List<Subscription> findAllByState(State state);
    List<Subscription> findAllByUserAndState(User user, State state);
}
