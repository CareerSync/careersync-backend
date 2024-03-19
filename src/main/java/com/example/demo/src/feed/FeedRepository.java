package com.example.demo.src.feed;

import com.example.demo.src.feed.entity.Feed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.List;
import java.util.Optional;

import static com.example.demo.common.entity.BaseEntity.*;

public interface FeedRepository extends JpaRepository<Feed, Long>,
        RevisionRepository<Feed, Long, Long> {
    Optional<Feed> findByIdAndState(Long id, State state);

    List<Feed> findAllByState(State state);
    List<Feed> findAllByUserIdAndState(Long userId, State state);
}
