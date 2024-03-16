package com.example.demo.src.subscription;

import com.example.demo.common.entity.BaseEntity;
import com.example.demo.src.service.entity.Item;
import com.example.demo.src.subscription.entity.Subscription;
import com.example.demo.src.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import static com.example.demo.common.entity.BaseEntity.*;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByUserAndItem(User user, Item item);
    List<Subscription> findAllByState(State state);
}
