package com.example.demo.src.chat;

import com.example.demo.common.entity.BaseEntity;
import com.example.demo.common.entity.BaseEntity.State;
import com.example.demo.src.chat.entity.Chat;
import com.example.demo.src.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatRepository extends JpaRepository<Chat, UUID> {
    Optional<Chat> findByIdAndState(UUID id, State state);
    Page<Chat> findByUserAndStateOrderByUpdatedAtDesc(User user, State state, Pageable pageable);
    Page<Chat> findByUserAndState(User user, State state, Pageable pageable);

}
