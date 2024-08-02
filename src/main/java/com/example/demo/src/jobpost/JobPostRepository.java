package com.example.demo.src.jobpost;

import com.example.demo.src.jobpost.entity.JobPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JobPostRepository extends JpaRepository<JobPost, UUID> {
}
