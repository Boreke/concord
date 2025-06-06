package org.concord.backend.dal.postgres.repository;

import org.concord.backend.dal.model.postgres.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes")
    List<Post> findAllWithLikes();

    List<Post> findTop20ByOrderByCreatedAtDesc();

    List<Post> findByUserIdOrderByCreatedAtDesc(Long id);
}