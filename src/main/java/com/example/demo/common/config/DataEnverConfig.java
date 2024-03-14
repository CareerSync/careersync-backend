package com.example.demo.common.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataEnverConfig {

    private final EntityManagerFactory entityManagerFactory;

    @Bean
    AuditReader auditReader() {
        log.info("auditReader 실행");
        return AuditReaderFactory.get(entityManagerFactory.createEntityManager());
    }
}
