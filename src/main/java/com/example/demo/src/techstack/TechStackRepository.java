package com.example.demo.src.techstack;

import com.example.demo.src.techstack.entity.TechStack;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TechStackRepository extends JpaRepository<TechStack, Long> {
}
