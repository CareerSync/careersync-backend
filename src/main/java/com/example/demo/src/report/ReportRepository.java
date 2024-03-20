package com.example.demo.src.report;

import com.example.demo.common.entity.BaseEntity.State;
import com.example.demo.src.report.entity.Report;
import com.example.demo.src.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long>,
        RevisionRepository<Report, Long, Long> {
    Optional<Report> findByIdAndState(Long id, State state);
    Optional<Report> findByUserIdAndFeedId(Long userId, Long feedId);
    List<Report> findAllByState(State state);
    List<Report> findAllByUserAndState(User user, State state);
}