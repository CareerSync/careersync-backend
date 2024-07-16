package com.example.demo.src.user;

import com.example.demo.src.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

import static com.example.demo.common.entity.BaseEntity.*;

public interface UserRepository extends JpaRepository<User, Long>,
        RevisionRepository<User, Long, Long> {

    Optional<User> findByIdAndState(Long id, State state);

    Optional<User> findByUserIdAndState(String userId, State state);
    Optional<User> findAllByUserIdAndState(String userId, State state);

    List<User> findAllByState(State state);

}
