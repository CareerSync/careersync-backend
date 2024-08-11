package com.example.demo.src.user;

import com.example.demo.src.user.entity.TechStack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TechStackRepository extends JpaRepository<TechStack, UUID> {
    Optional<List<TechStack>> findAllByUserId(UUID userId);
}

