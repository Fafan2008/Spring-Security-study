package com.ivo.my.repositories;

import com.ivo.my.models.entities.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository  extends JpaRepository<VerificationToken, Long> {

    VerificationToken findVerificationTokenByToken(String token);
    VerificationToken findVerificationTokenByUserId(Long userId);
}
