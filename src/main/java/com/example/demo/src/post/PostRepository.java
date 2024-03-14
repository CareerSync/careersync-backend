package com.example.demo.src.post;

import com.example.demo.src.post.entity.Post;
import com.example.demo.src.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.List;
import java.util.Optional;

import static com.example.demo.common.entity.BaseEntity.*;

public interface PostRepository extends JpaRepository<Post, Long>,
        RevisionRepository<Post, Long, Long> {
    Optional<Post> findByIdAndState(Long id, State state);

    List<Post> findAllByState(State state);
    List<Post> findAllByUserIdAndState(Long userId, State state);
}
