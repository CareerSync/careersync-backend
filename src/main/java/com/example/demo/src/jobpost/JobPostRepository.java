package com.example.demo.src.jobpost;

import com.example.demo.common.entity.BaseEntity;
import com.example.demo.src.jobpost.entity.JobPost;
import com.example.demo.src.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.demo.common.entity.BaseEntity.*;

public interface JobPostRepository extends JpaRepository<JobPost, UUID> {

    @Query("SELECT jp FROM JobPost jp WHERE jp.user.id = :userId AND jp.answer.chat.id = :chatId")
    List<JobPost> findTop3ByChatAndUserOrderByCreatedAtDesc(@Param("chatId") UUID chatId, @Param("userId") UUID userId, Pageable pageable);
}
