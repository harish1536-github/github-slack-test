package com.example.tracker.repository;

import com.example.tracker.entity.Commit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommitRepository extends JpaRepository<Commit, Long> {
    boolean existsByCommitId(String commitId);
    List<Commit> findByAuthorId(Long authorId);
}
