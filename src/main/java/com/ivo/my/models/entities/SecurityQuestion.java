package com.ivo.my.models.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "security_question")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecurityQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id", unique = true)
    private User user;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "security_question_id")
    private SecurityQuestionDefinition questionDefinition;

    private String answer;
}
