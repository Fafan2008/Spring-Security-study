package com.ivo.my.configs.security;

import com.ivo.my.models.entities.User;
import com.ivo.my.repositories.UserRepository;
import org.jboss.aerogear.security.otp.Totp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class Google2FAuthProvider implements AuthenticationProvider {

    @Autowired
    private UserRepository userRepository;

    @Override
    //TODO: Need more complicated logic, for realisation!!!
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final String username = authentication.getName();
        final String verificationCode = ((AppWebAuthenticationDetails) authentication.getDetails()).getVerificationCode();
        final Optional<User> user = userRepository.findByLoginName(username);
        if (user.isEmpty()) {
            throw new BadCredentialsException("Invalid username or password");
        }

        final Totp totp = new Totp(user.get().getSecret());
        // Stupid idea how stop ProviderManager
        if (!totp.verify(verificationCode)) {
            throw new DisabledException("Invalid verfication code");
        }

        return authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
