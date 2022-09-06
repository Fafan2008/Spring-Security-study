package com.ivo.my.services;

import com.ivo.my.models.entities.SecurityQuestionDefinition;
import com.ivo.my.repositories.SecurityQuestionDefinitionRepository;
import com.ivo.my.repositories.SecurityQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SecurityQuestionService {

    @Autowired
    SecurityQuestionDefinitionRepository securityQuestionDefinitionRepository;

    @Autowired
    SecurityQuestionRepository securityQuestionRepository;

    public List<SecurityQuestionDefinition> getQuestionDefinitions() {
        return securityQuestionDefinitionRepository.findAll();
    }

    public Boolean checkAnswer(Long questionId, Long userId, String answer) {
        return securityQuestionRepository.findByQuestionDefinitionIdAndUserIdAndAnswer(questionId, userId, answer).isPresent();
    }
}
