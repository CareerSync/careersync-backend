package com.example.demo.src.answer;

import com.example.demo.src.answer.entity.Answer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface AnswerRepository extends JpaRepository<Answer, UUID> {
    @Query("SELECT a FROM Answer a " +
            "JOIN FETCH a.chat c " +
            "JOIN FETCH c.user u " +
            "WHERE u.id = :userId " +
            "ORDER BY a.createdAt DESC")
    List<Answer> findTop5LatestAnswersByUserId(@Param("userId") UUID userId, Pageable pageable);
}
