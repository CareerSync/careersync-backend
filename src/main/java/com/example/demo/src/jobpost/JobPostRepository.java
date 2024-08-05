package com.example.demo.src.jobpost;

import com.example.demo.common.entity.BaseEntity;
import com.example.demo.src.jobpost.entity.JobPost;
import com.example.demo.src.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.demo.common.entity.BaseEntity.*;

public interface JobPostRepository extends JpaRepository<JobPost, UUID> {
    Optional<List<JobPost>> getJobPostsByUserAndState(User user, State state);
}
