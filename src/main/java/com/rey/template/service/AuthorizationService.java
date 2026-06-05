package com.rey.template.service;

public interface AuthorizationService {
    boolean canAccessPage(
            String username,
            String responsibility,
            String page
    );
}
