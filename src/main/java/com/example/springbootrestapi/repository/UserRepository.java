package com.example.springbootrestapi.repository;

import com.example.springbootrestapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    // BAD: native SQL query with string building risk (used in service via concatenation)
    @Query(value = "SELECT * FROM users WHERE r = ?1", nativeQuery = true)
    List<User> findByRole(String role);

    // BAD: exposes all users with a given status - no pagination
    List<User> findByS(int s);

    boolean existsByUsername(String username);

    boolean existsByE(String e);
}
