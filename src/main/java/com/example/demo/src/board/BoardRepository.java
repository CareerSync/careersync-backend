package com.example.demo.src.board;

import com.example.demo.src.board.entity.Board;
import com.example.demo.src.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.List;
import java.util.Optional;

import static com.example.demo.common.entity.BaseEntity.*;

public interface BoardRepository extends JpaRepository<Board, Long>,
        RevisionRepository<Board, Long, Long> {
    Optional<Board> findByIdAndState(Long id, State state);

    List<Board> findAllByState(State state);
    List<Board> findAllByUserAndState(User user, State state);
    List<Board> findAllByUserAndState(User user, State state, Pageable pageable);
    List<Board> findAllByUserIdAndState(Long userId, State state);
}
