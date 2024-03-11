package com.example.demo.common.scheduler;

import com.example.demo.src.user.UserRepository;
import com.example.demo.src.user.UserService;
import com.example.demo.src.user.model.GetUserRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
