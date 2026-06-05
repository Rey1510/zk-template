package com.rey.template.service.impl;

import com.rey.template.service.AuthorizationService;
import com.rey.template.util.UrlConstant;
import org.springframework.stereotype.Service;

@Service("authorizationService")
public class AuthorizationServiceImpl
        implements AuthorizationService {

    @Override
    public boolean canAccessPage(
            String username,
            String responsibility,
            String page
    ) {

        if ("ADMIN".equals(responsibility)) {
            return true;
        }

        if ("MAKER".equals(responsibility)) {

            return !UrlConstant.URL_REPORT_ZUL.equals(page);
        }

        return false;
    }
}