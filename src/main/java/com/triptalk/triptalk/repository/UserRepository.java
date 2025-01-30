package com.triptalk.triptalk.repository;

import com.triptalk.triptalk.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);
  Optional<User> findByNickname(String nickname);
  Optional<User> findByUsername(String username);
  boolean existsByEmail(String email);
  boolean existsByUsername(String username);
  boolean existsByNickname(String nickname);
}
