package com.ivo.my.repositories;

import com.ivo.my.models.entities.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    PasswordResetToken findPasswordResetTokenByToken(String token);
}
