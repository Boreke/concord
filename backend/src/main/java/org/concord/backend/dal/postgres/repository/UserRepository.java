package org.concord.backend.dal.postgres.repository;

import org.concord.backend.dal.model.postgres.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUserTag(String userTag);

    boolean existsByEmail(String email);
    boolean existsByUserTag(String userTag);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.followers LEFT JOIN FETCH u.following WHERE u.id = :id")
    Optional<User> findByIdWithRelations(@Param("id") Long id);

}