package com.example.demo.common.scheduler;

import com.example.demo.common.payment.IamportClientInitializer;
import com.example.demo.src.payment.PaymentRepository;
import com.example.demo.src.service.ItemRepository;
import com.example.demo.src.service.entity.Item;
import com.example.demo.src.user.UserRepository;
import com.example.demo.src.user.UserService;
import com.example.demo.src.user.entity.User;
import com.example.demo.src.user.model.GetUserRes;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.demo.common.entity.BaseEntity.State.ACTIVE;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SchedulerService {

    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private IamportClient iamportClient;


    @Value("${spring.imp.api-key}")
    private String apiKey;

    @Value("${spring.imp.api-secret}")
    private String secretKey;

    @PostConstruct
    public void init() {
        IamportClientInitializer clientInitializer = new IamportClientInitializer(apiKey, secretKey);
        this.iamportClient = clientInitializer.initializeIamportClient();
    }


    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void privacyTermAgree() {
        log.info("개인정보처리방침 동의가 필요한 유저를 체크합니다.");

        LocalDate now = LocalDate.now();
        LocalDate oneYearAfterDate = now.plusYears(1);

        userRepository.findAllByState(ACTIVE)
                .forEach((user) -> {
                    log.info("today date: {}", now);
                    log.info("user privacydate: {}", user.getPrivacyDate());
                    if (user.getPrivacyDate().equals(now)) {
                        log.info("{} 회원님, 개인정보처리방침 동의가 새로 필요합니다.", user.getName());
                        user.updatePrivacyDate(oneYearAfterDate);
                    }
                });
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void validatePayment() {
        log.info("결제된 금액을 검증 하여, 다른 금액에 대한 예외 처리 진행");

        paymentRepository.findAllByState(ACTIVE)
                .forEach((payment) -> {

                    Item item = payment.getItem();
                    User user = payment.getUser();
                    String impUid = payment.getImpUid();

                    // 결제된 금액
                    IamportResponse<Payment> paymentIamportResponse = iamportClient.paymentByImpUid(impUid);
                    BigDecimal amount = paymentIamportResponse.getResponse().getAmount();

                    // db에 저장된 상품 금액
                    int price = item.getPrice();

                    // 결제된 금액과 db에 저장된 상품 금액 다를 시 -> 환불 처리
                    if (amount.compareTo(BigDecimal.valueOf(price)) != 0) {
                        log.info("{} 회원님, 결제하신 금액과 실제 상품 금액이 다르므로 환불 처리 하겠습니다.", user.getName());
                        cancelReservation(impUid);
                    }
                });
    }

    public void cancelReservation(String impUid){
        IamportResponse<Payment> response = iamportClient.paymentByImpUid(impUid);
        //cancelData 생성
        CancelData cancelData = new CancelData(response.getResponse().getImpUid(), true);
        //결제 취소
        iamportClient.cancelPaymentByImpUid(cancelData);
    }

}
