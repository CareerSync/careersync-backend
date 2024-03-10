package com.example.demo.common.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SchedulerService {
    @Scheduled(fixedDelay = 31536000)
    public void privacyTermAgree() {
        System.out.println("개인정보처리방침 동의가 필요합니다.");
    }
}
