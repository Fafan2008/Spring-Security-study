package com.ivo.my.utils.validations.errors;

public class LoginNameExistsException extends Throwable {

    public LoginNameExistsException(final String message) {
        super(message);
    }
}
