package org.concord.backend.dal.postgres.repository;

import org.concord.backend.dal.model.postgres.Like;
import org.concord.backend.dal.model.postgres.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {
    void deleteByUserIdAndPostId(Long userId, Long postId);

    @Query("SELECT l.post FROM Like l WHERE l.user.id = :userId")
    List<Post> findPostsLikedByUser(@Param("userId") Long userId);

    List<Like> findAllByUserIdNot(Long userId);

    @Query("SELECT l.post.id FROM Like l WHERE l.user.id = :userId")
    List<Long> findPostIdsByUserId(@Param("userId") Long userId);

    Boolean existsByUserIdAndPostId(Long userId, Long postId);
}
