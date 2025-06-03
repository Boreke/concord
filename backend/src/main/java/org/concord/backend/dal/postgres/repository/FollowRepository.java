package org.concord.backend.dal.postgres.repository;

import org.concord.backend.dal.model.postgres.Follow;
import org.concord.backend.dal.model.postgres.FollowId;
import org.concord.backend.dal.model.postgres.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    void deleteByFollowerIdAndFolloweeId(Long followerId, Long followeeId);

    void deleteById(FollowId id);

    List<Follow> findByFollowerId(Long followerId);

    List<Follow> findByFolloweeId(Long followeeId);

    List<Follow> findAllByFollower(User user);
}
