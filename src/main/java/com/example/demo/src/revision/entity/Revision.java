package com.example.demo.src.revision.entity;

import lombok.Getter;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Entity
@RevisionEntity
@Table(name = "TB_REVINFO")
public class Revision implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @RevisionNumber
    @Column(name = "REV")
    private Long id;

    @RevisionTimestamp
    @Column(name = "REVTSTMP")
    private Long timestamp;

    @Transient
    public LocalDateTime getRevisionDate() {
        return LocalDateTime.from(Instant.ofEpochMilli(timestamp));
    }
}