package com.ivo.my.services;

import com.ivo.my.models.entities.*;
import com.ivo.my.repositories.SecurityQuestionDefinitionRepository;
import com.ivo.my.repositories.SecurityQuestionRepository;
import com.ivo.my.repositories.UserRepository;
import com.ivo.my.utils.validations.errors.EmailExistsException;
import com.ivo.my.utils.validations.errors.LoginNameExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.ivo.my.utils.generate.AncillaryDataGenerator.generateApplicationUrl;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    TokenService tokenService;

    @Autowired
    AsyncComponent asyncComponent;

    @Autowired
    SecurityQuestionDefinitionRepository securityQuestionDefinitionRepository;
    @Autowired
    SecurityQuestionRepository securityQuestionRepository;

    public List<User> findAll() {
        asyncComponent.asyncMethod();
        return userRepository.findAll();
    }

    public User findUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent())
            return user.get();
        else
            throw new HttpClientErrorException(NOT_FOUND, MessageFormat.format("Not found user with id = {0}", id));
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new HttpClientErrorException(NOT_FOUND,
                        MessageFormat.format("Not found user with email = {0}", email)));
    }

    public User findUserByLoginName(String loginName) {
        return userRepository.findByLoginName(loginName)
                .orElseThrow(() -> new HttpClientErrorException(NOT_FOUND,
                        MessageFormat.format("Not found user with login name = {0}", loginName)));
    }

    public void deleteUser(Long id) {
        tokenService.deleteVerificationTokenByUserId(id);
        userRepository.deleteById(id);
    }

    public User registerNewUser(User user, HttpServletRequest request, Long securityQuestionId, String answer) throws EmailExistsException, LoginNameExistsException {
        if (emailExist(user.getEmail())) {
            throw new EmailExistsException("There is an account with that email address: " + user.getEmail());
        }

        if (loginNameExist(user.getLoginName())) {
            throw new LoginNameExistsException("There is an account with that login name: " + user.getLoginName());
        }

        user.setEnabled(false);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        final User savedUser = userRepository.save(user);

        VerificationToken token = tokenService.createVerificationToken(user);
        tokenService.sendVerificationToken(token, generateApplicationUrl(request));

        securityQuestionDefinitionRepository.findById(securityQuestionId).ifPresent(
                        questionDefinition -> securityQuestionRepository.save(SecurityQuestion.builder()
                                .user(savedUser)
                                .questionDefinition(questionDefinition)
                                .answer(answer)
                                .build())
                );
        return savedUser;
    }

    private boolean emailExist(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    private boolean loginNameExist(String loginName) {
        return userRepository.findByLoginName(loginName).isPresent();
    }

    public User updateExistingUser(User user) throws EmailExistsException, LoginNameExistsException {
        Long id = user.getId();
        final String email = user.getEmail();
        final String loginName = user.getLoginName();

        if (userRepository.findByEmail(email).map(owner -> !owner.getId().equals(id)).orElse(false))
            throw new EmailExistsException("Email not available.");
        if (userRepository.findByLoginName(loginName).map(owner -> !owner.getId().equals(id)).orElse(false))
            throw new LoginNameExistsException("Login name not available.");

        if (user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    public void enableUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new HttpClientErrorException(NOT_FOUND, MessageFormat.format("Not found user with id = {0}", userId)));
        user.setEnabled(true);
        userRepository.save(user);
    }

    public void resetPassword(String userEmail, HttpServletRequest request) {
        final User user = findUserByEmail(userEmail);
        PasswordResetToken token = tokenService.createPasswordResetToken(user);
        tokenService.sendPasswordResetToken(token, generateApplicationUrl(request));
    }

    public void changeUserPassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }
}
