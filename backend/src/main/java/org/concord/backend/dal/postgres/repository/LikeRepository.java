package org.concord.backend.dal.postgres.repository;

import org.concord.backend.dal.model.postgres.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {
    void deleteByUserIdAndPostId(Long userId, Long postId);
}
