package com.example.demo.src.post;

import com.example.demo.src.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import static com.example.demo.common.entity.BaseEntity.*;

public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findByIdAndState(Long id, State state);

    List<Post> findAllByState(State state);
    List<Post> findAllByUserIdAndState(Long userId, State state);
}
