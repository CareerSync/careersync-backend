package com.example.demo.src.chat;

import com.example.demo.src.chat.entity.Chat;
import com.example.demo.src.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChatRepository extends JpaRepository<Chat, UUID> {

}
