package com.ivo.my.repositories;

import com.ivo.my.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLoginName(String loginName);
    Optional<User> findByEmail(String email);
}
