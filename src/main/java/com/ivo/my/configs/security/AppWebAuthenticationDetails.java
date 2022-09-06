package com.ivo.my.configs.security;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

public class AppWebAuthenticationDetails extends WebAuthenticationDetails {
    private static final long serialVersionUID = 1L;

    // using for Google 2f auth
    private final String verificationCode;

    public AppWebAuthenticationDetails(HttpServletRequest request) {
        super(request);
        verificationCode = request.getParameter("code");
    }

    public String getVerificationCode() {
        return verificationCode;
    }
}
