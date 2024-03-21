package com.example.demo.src.payment;

import com.example.demo.src.payment.entity.Payment;
import com.example.demo.src.item.entity.Item;
import com.example.demo.src.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import static com.example.demo.common.entity.BaseEntity.*;
import static com.example.demo.src.payment.entity.Payment.*;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByIdAndState(Long id, State state);

    Optional<Payment> findByImpUidAndState(String impUid, State state);
    Optional<Payment> findByMerchantUidAndState(String merchantUid, State state);

    List<Payment> findAllByPaymentState(PaymentState paymentState);

    Optional<Payment> findByUser(User user);
    Optional<Payment> findByItem(Item item);

    Optional<Payment> findByUserAndItem(User user, Item item);
    Optional<Payment> findByUserAndItemAndState(User user, Item item, State state);
    Optional<Payment> findByUserAndItemAndPaymentState(User user, Item item, PaymentState paymentState);
    Optional<Payment> findByUserAndItemAndStateAndPaymentState(User user, Item item, State state, PaymentState paymentState);
    Optional<Payment> findByUserAndState(User user, State state);
    Optional<Payment> findByItemAndState(Item item, State state);
    List<Payment> findAllByState(State State);
    List<Payment> findAllByUserAndPaymentState(User user, PaymentState paymentState);
    List<Payment> findAllByUser(User user);

}
