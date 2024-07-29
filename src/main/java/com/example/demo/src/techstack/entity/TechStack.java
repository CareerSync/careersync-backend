package com.example.demo.src.techstack.entity;

import com.example.demo.common.entity.BaseEntity;
import com.example.demo.src.user.entity.User;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static javax.persistence.FetchType.LAZY;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Entity
@JsonAutoDetect(fieldVisibility = ANY)
@Table(name = "TB_TECH_STACK")
public class TechStack extends BaseEntity {

    @Id // PK를 의미하는 어노테이션
    @Column(name = "tb_tech_stack_id", nullable = false, updatable = false, columnDefinition = "binary(16)")
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private UUID id;

    @Column(nullable = false, columnDefinition = "text")
    private String name;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "tb_user_id")
    private User user;
}
