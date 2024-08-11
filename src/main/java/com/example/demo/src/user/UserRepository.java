package com.example.demo.src.user;

import com.example.demo.src.user.entity.User;
import com.google.common.collect.Collections2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.demo.common.entity.BaseEntity.*;

public interface UserRepository extends JpaRepository<User, UUID>{

    Optional<User> findByIdAndStatus(Long id, Status status);
    Optional<User> findByIdAndStatus(UUID id, Status status);
    Optional<User> findByUserIdAndStatus(String userId, Status status);
    Optional<User> findByUserIdAndStatus(UUID userId, Status status);
    Optional<User> findByUserNameAndStatus(String userName, Status status);

    Optional<User> findAllByUserIdAndStatus(String userId, Status status);

    List<User> findAllByStatus(Status status);

}
