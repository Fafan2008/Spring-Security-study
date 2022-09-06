package com.ivo.my.services;

import com.ivo.my.models.entities.PasswordResetToken;
import com.ivo.my.models.entities.User;
import com.ivo.my.models.entities.VerificationToken;
import com.ivo.my.repositories.PasswordResetTokenRepository;
import com.ivo.my.repositories.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TokenService {

    @Autowired
    VerificationTokenRepository verificationTokenRepository;
    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private Environment env;

    public VerificationToken createVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = VerificationToken.builder().token(token).user(user).build();
        return verificationTokenRepository.save(verificationToken);
    }

    public void sendVerificationToken(VerificationToken token, String appUrl) {
        final SimpleMailMessage email = constructVerificationEmailMessage(token, appUrl);
        mailSender.send(email);
    }

    private SimpleMailMessage constructVerificationEmailMessage(VerificationToken token, String appUrl) {
        final String recipientAddress = token.getUser().getEmail();
        final String subject = "Registration Confirmation";
        final String confirmationUrl = appUrl + "/registrationConfirm?token=" + token.getToken();
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText("Please open the following URL to verify your account: \r\n" + confirmationUrl);
        email.setFrom(env.getProperty("support.email"));
        return email;
    }

    public VerificationToken findVerificationToken(String token) {
        return verificationTokenRepository.findVerificationTokenByToken(token);
    }

    public PasswordResetToken findPasswordResetToken(String token) {
        return passwordResetTokenRepository.findPasswordResetTokenByToken(token);
    }

    public void deleteVerificationToken(Long id) {
        verificationTokenRepository.deleteById(id);
    }

    public void deleteVerificationTokenByUserId(Long userId) {
        VerificationToken token = verificationTokenRepository.findVerificationTokenByUserId(userId);
        verificationTokenRepository.delete(token);
    }

    public PasswordResetToken createPasswordResetToken(User user) {
        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = PasswordResetToken.builder().token(token).user(user).build();
        return passwordResetTokenRepository.save(passwordResetToken);
    }

    public void sendPasswordResetToken(PasswordResetToken token, String appUrl) {
        final SimpleMailMessage email = constructResetTokenEmail(token, appUrl);
        mailSender.send(email);
    }

    private SimpleMailMessage constructResetTokenEmail(PasswordResetToken token, String appUrl) {
        final String recipientAddress = token.getUser().getEmail();
        final String subject = "Reset Password";
        final String resetUrl = appUrl + "/user/changePassword?id="+ token.getUser().getId() + "&token=" + token.getToken();
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText("Please open the following URL to reset your password: \r\n" + resetUrl);
        email.setFrom(env.getProperty("support.email"));
        return email;
    }
}
