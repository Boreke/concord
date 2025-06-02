package org.concord.backend.dal.postgres.repository;

import io.swagger.v3.oas.annotations.Hidden;
import org.concord.backend.dal.model.postgres.Post;
import org.concord.backend.dal.model.postgres.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Hidden
public interface PostRepository extends JpaRepository<Post, Long> {
    <Optional> Post findByUserId(Long userId);
}
