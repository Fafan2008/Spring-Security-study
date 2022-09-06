package com.ivo.my.repositories;

import com.ivo.my.models.entities.SecurityQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SecurityQuestionRepository extends JpaRepository<SecurityQuestion, Long> {
    Optional<SecurityQuestion> findByQuestionDefinitionIdAndUserIdAndAnswer(Long questionId, Long userId, String answer);
}
