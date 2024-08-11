package com.example.demo.src.chat;

import com.example.demo.common.entity.BaseEntity;
import com.example.demo.common.entity.BaseEntity.Status;
import com.example.demo.src.answer.entity.Answer;
import com.example.demo.src.chat.entity.Chat;
import com.example.demo.src.jobpost.entity.JobPost;
import com.example.demo.src.question.entity.Question;
import com.example.demo.src.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatRepository extends JpaRepository<Chat, UUID> {
    Optional<Chat> findByIdAndStatus(UUID id, Status status);
    Page<Chat> findByUserAndStatusOrderByUpdatedAtDesc(User user, Status status, Pageable pageable);
    Page<Chat> findByUserAndStatus(User user, Status status, Pageable pageable);
    @Query("SELECT q FROM Question q WHERE q.chat.id = :chatId ORDER BY q.createdAt DESC")
    List<Question> findTop5LatestQuestionsByChatId(@Param("chatId") UUID chatId, Pageable pageable);

    @Query("SELECT a FROM Answer a WHERE a.chat.id = :chatId ORDER BY a.createdAt DESC")
    List<Answer> findTop5LatestAnswersByChatId(@Param("chatId") UUID chatId, Pageable pageable);

}
