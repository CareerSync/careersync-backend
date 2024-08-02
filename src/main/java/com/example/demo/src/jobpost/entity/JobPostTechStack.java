package com.example.demo.src.jobpost.entity;

import com.example.demo.src.user.entity.User;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.*;
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
@Table(name = "TB_JOB_POST_TECH_STACK")
public class JobPostTechStack {

    @Id // PK를 의미하는 어노테이션
    @Column(name = "tb_tech_stack_id", nullable = false, updatable = false, columnDefinition = "binary(16)")
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private UUID id;

    @Column(nullable = false, columnDefinition = "text")
    private String name;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "tb_job_post_id")
    private JobPost jobPost;

    @Builder
    public JobPostTechStack(String name) {
        this.name = name;
    }
}
