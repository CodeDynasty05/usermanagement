package com.looyt.usermanagement.repository;

import com.looyt.usermanagement.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(value = "User.full", type = EntityGraph.EntityGraphType.LOAD)
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @EntityGraph(value = "User.full", type = EntityGraph.EntityGraphType.LOAD)
    Optional<User> findById(Long id);

    @EntityGraph(value = "User.full", type = EntityGraph.EntityGraphType.LOAD)
    Page<User> findAll(Pageable pageable);

    @EntityGraph(value = "User.full", type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT u FROM User u WHERE u.role = :role")
    Page<User> findByRole(@Param("role") User.UserRole role, Pageable pageable);

    @EntityGraph(value = "User.full", type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT u FROM User u WHERE u.active = :active")
    Page<User> findByActive(@Param("active") Boolean active, Pageable pageable);

    @EntityGraph(value = "User.full", type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<User> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);
}