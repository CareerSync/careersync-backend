package com.example.demo.src.recjobpost.entity;

import com.example.demo.common.entity.BaseEntity;
import com.example.demo.src.jobpost.entity.JobPost;
import com.example.demo.src.user.entity.User;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static javax.persistence.FetchType.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@JsonAutoDetect(fieldVisibility = ANY)
@Table(name = "TB_REC_JOB_POST")
public class RecJobPost extends BaseEntity {

    @Id // PK를 의미하는 어노테이션
    @Column(name = "tb_rec_job_post_id", nullable = false, updatable = false, columnDefinition = "binary(16)")
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private UUID id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "tb_user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "tb_job_post_id")
    private JobPost jobPost;
}
