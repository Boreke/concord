package org.concord.backend.dal.postgres.repository;

import org.concord.backend.dal.model.postgres.Post;
import org.concord.backend.dal.model.postgres.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUser(User user);

    List<Post> findByUserId(Long userId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likedBy")
    List<Post> findAllWithLikes();
}