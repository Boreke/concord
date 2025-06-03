package org.concord.backend.dal.postgres.repository;

import org.concord.backend.dal.model.postgres.User;
import org.concord.backend.dal.model.postgres.UserPP;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserPPRepository extends JpaRepository<UserPP, Long> {
    Optional<UserPP> findByUser(User user);
}
