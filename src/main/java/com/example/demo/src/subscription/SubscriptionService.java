package com.example.demo.src.subscription;

import com.example.demo.common.entity.BaseEntity;
import com.example.demo.common.entity.BaseEntity.State;
import com.example.demo.common.exceptions.BaseException;
import com.example.demo.src.payment.PaymentRepository;
import com.example.demo.src.payment.entity.Payment;
import com.example.demo.src.item.ItemRepository;
import com.example.demo.src.item.entity.Item;
import com.example.demo.src.subscription.model.GetSubscriptionRes;
import com.example.demo.src.subscription.entity.Subscription;
import com.example.demo.src.subscription.model.PatchSubscriptionReq;
import com.example.demo.src.subscription.model.PostSubscriptionReq;
import com.example.demo.src.subscription.model.PostSubscriptionRes;
import com.example.demo.src.user.UserRepository;
import com.example.demo.src.user.entity.User;
import com.example.demo.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.demo.common.entity.BaseEntity.State.*;
import static com.example.demo.common.response.BaseResponseStatus.*;
import static com.example.demo.src.payment.entity.Payment.PaymentState.FAIL;
import static com.example.demo.src.subscription.entity.Subscription.SubscriptionState.*;
import static com.example.demo.src.subscription.entity.Subscription.SubscriptionState.SUCCESS;


@Transactional
@RequiredArgsConstructor
@Service
@Slf4j
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final PaymentRepository paymentRepository;
    private final MessageUtils messageUtils;

    // POST
    @Transactional(noRollbackFor = BaseException.class)
    public PostSubscriptionRes createSubscription(Long userId, PostSubscriptionReq req) {

        // 존재하지 않는 유저 예외처리
        Optional<User> findUser = userRepository.findByIdAndState(userId, ACTIVE);
        if (!findUser.isPresent()) {
            throw new BaseException(NOT_FIND_USER, messageUtils.getMessage("NOT_FIND_USER"));
        }

        // 존재하지 않는 상품 예외처리
        Optional<Item> findItem = itemRepository.findByIdAndState(req.getItemId(), ACTIVE);
        if (!findItem.isPresent()) {
            throw new BaseException(NOT_FIND_ITEM, messageUtils.getMessage("NOT_FIND_ITEM"));
        }

        User user = findUser.get();
        Item item = findItem.get();

        // 이미 구독 테이블에 있는 성공 이력이면 예외처리
        Optional<Subscription> findSubscription = subscriptionRepository.findByUserAndItemAndSubscriptionState(user, item, SUCCESS);
        if (findSubscription.isPresent()) {
            throw new BaseException(DUPLICATED_SUBSCRIPTION, messageUtils.getMessage("DUPLICATED_SUBSCRIPTION"));
        }

        // 결제 내역에 없는 유저 예외처리
        Optional<Payment> findPaymentUser = paymentRepository.findByUser(user);
        if (!findPaymentUser.isPresent()) {
            throw new BaseException(INVALID_PAYMENT_USER, messageUtils.getMessage("INVALID_PAYMENT_USER"));
        }

        // 결제 내역에 없는 상품 예외처리
        Optional<Payment> findPaymentItem  = paymentRepository.findByItem(item);
        if (!findPaymentItem.isPresent()) {
            throw new BaseException(INVALID_PAYMENT_ITEM, messageUtils.getMessage("INVALID_PAYMENT_ITEM"));
        }

        // 구독 성공 - 결제 성공한 상품(SUCCESS)이면서, 환불 안한 상품(ACTIVE) 구독하려 할때
        // 구독 실패 - 결제 실패한 상품 구독하려 할때, 혹은 이미 환불된 결제에 대한 상품 구독하려 할떄
        Optional<Payment> findPayment = paymentRepository.findByUserAndItemAndStateAndPaymentState(user, item, ACTIVE, Payment.PaymentState.SUCCESS);
        if (findPayment.isPresent()) {
            PostSubscriptionRes postSubscriptionRes = saveSubscription(user, item, SUCCESS);
            return postSubscriptionRes;
        } else {
            saveSubscription(user, item, Subscription.SubscriptionState.FAIL);
            throw new BaseException(SUBSCRIPTION_ERROR, messageUtils.getMessage("SUBSCRIPTION_ERROR"));
        }
    }

    private PostSubscriptionRes saveSubscription(User user, Item item,
                                                 Subscription.SubscriptionState subscriptionState) {

        LocalDate now = LocalDate.now();
        LocalDate oneMonthAfterDate = now.plusMonths(1);

        Subscription subscription = Subscription.builder()
                .user(user)
                .item(item)
                .nextPaymentDate(oneMonthAfterDate)
                .subscriptionState(subscriptionState)
                .build();

        Subscription saveSubscription = subscriptionRepository.save(subscription);
        return new PostSubscriptionRes(saveSubscription.getId());
    }

    // GET

    public List<GetSubscriptionRes> getSubscriptions(Long userId) {

        User user = userRepository.findByIdAndState(userId, ACTIVE)
                .orElseThrow(() -> new BaseException(INVALID_USER));

        List<GetSubscriptionRes> subscriptions = subscriptionRepository.findAllByUserAndState(user, ACTIVE).stream()
                .map((subscription) -> {
                    Item item = subscription.getItem();
                    return new GetSubscriptionRes(subscription, user, item);
                })
                .collect(Collectors.toList());
        return subscriptions;
    }

    public GetSubscriptionRes getSubscription(Long subscriptionId) {
        Optional<Subscription> findSubscription = subscriptionRepository.findByIdAndState(subscriptionId, ACTIVE);
        if (findSubscription.isPresent()) {
            Subscription subscription = findSubscription.get();
            return new GetSubscriptionRes(subscription);
        } else {
            return null;
        }
    }

    // PATCH
    public void modifyNextSubscriptionDate(Long subscriptionId, PatchSubscriptionReq req) {
        Subscription subscription = subscriptionRepository.findByIdAndState(subscriptionId, ACTIVE)
                .orElseThrow(() -> new BaseException(INVALID_SUBSCRIPTION, messageUtils.getMessage("INVALID_SUBSCRIPTION")));

        LocalDate nextPaymentDate = req.getNextPaymentDate();
        subscription.updateNextPaymentDate(nextPaymentDate);
    }

    public void modifySubscriptionState(Long subscriptionId, State state) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new BaseException(INVALID_SUBSCRIPTION, messageUtils.getMessage("INVALID_SUBSCRIPTION")));

        subscription.updateState(state);
    }

    // DELETE
    public void deleteSubscription(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findByIdAndState(subscriptionId, ACTIVE)
                    .orElseThrow(() -> new BaseException(INVALID_SUBSCRIPTION, messageUtils.getMessage("INVALID_SUBSCRIPTION")));
        subscriptionRepository.delete(subscription);
    }
}
