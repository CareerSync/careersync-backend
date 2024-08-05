package com.example.demo.src.jobpost.entity;

import com.example.demo.common.entity.BaseEntity;
import com.example.demo.src.answer.entity.Answer;
import com.example.demo.src.chat.entity.Chat;
import com.example.demo.src.user.entity.TechStack;
import com.example.demo.src.user.entity.User;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import kotlin.Lazy;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@JsonAutoDetect(fieldVisibility = ANY)
@Entity
@Table(name = "TB_JOB_POST")
public class JobPost extends BaseEntity {

    @Id // PK를 의미하는 어노테이션
    @Column(name = "tb_job_post_id", nullable = false, updatable = false, columnDefinition = "binary(16)")
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private UUID id;

    @Column(nullable = false, columnDefinition = "text")
    private String title;

    @Column(columnDefinition = "nvarchar(2)")
    private String career;

    @Column(name = "company_name", nullable = false, columnDefinition = "nvarchar(30)")
    private String companyName;

    @Column(name = "end_date", nullable = false, columnDefinition = "datetime2")
    private LocalDateTime endDate;

    @Column(name = "site_url", columnDefinition = "text")
    private String siteUrl;

    @Column(name = "img_url", columnDefinition = "text")
    private String imageUrl;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "tb_answer_id")
    private Answer answer;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "tb_user_id")
    private User user;

    @OneToMany(mappedBy = "jobPost", fetch = LAZY, cascade = ALL)
    List<JobPostTechStack> jobPostTechStacks = new ArrayList<>();

    @Builder
    public JobPost(String title, String career, String companyName, LocalDateTime endDate, String siteUrl, String imageUrl) {
        this.title = title;
        this.career = career;
        this.companyName = companyName;
        this.endDate = endDate;
        this.siteUrl = siteUrl;
        this.imageUrl = imageUrl;
    }

    public void addJobPostTechStacks(JobPostTechStack techStack) {
        jobPostTechStacks.add(techStack);
        techStack.setJobPost(this);
    }

}

