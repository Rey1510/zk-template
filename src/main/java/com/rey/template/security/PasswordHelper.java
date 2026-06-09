package com.rey.template.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordHelper {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Value("${security.password.pepper:templateSecretPepperKey567!}")
    private String pepper;

    public String encode(String rawPassword) {
        if (rawPassword == null) {
            return null;
        }
        return encoder.encode(rawPassword + pepper);
    }

    public boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        return encoder.matches(rawPassword + pepper, encodedPassword);
    }
}
