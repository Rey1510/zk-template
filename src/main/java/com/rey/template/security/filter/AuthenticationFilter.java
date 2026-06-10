package com.rey.template.security.filter;

import com.rey.template.security.CurrentUserService;
import com.rey.template.util.UrlConstant;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class AuthenticationFilter implements Filter {

    @Value("${sso.keycloak.auth-url}")
    private String authUrl;

    @Value("${sso.keycloak.client-id}")
    private String clientId;

    @Value("${sso.keycloak.redirect-uri}")
    private String redirectUri;

    private final CurrentUserService currentUserService;

    public AuthenticationFilter(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String uri = req.getRequestURI();

        boolean callbackPage = uri.contains("/login/oauth2/code/keycloak");
        boolean logoutPage = uri.contains("/logout");

        boolean staticResource =
                uri.contains("/zkau")
                        || uri.contains("/zkres")
                        || uri.contains("/css/")
                        || uri.contains("/js/")
                        || uri.contains("/images/");

        if (uri.contains("/zkau")
                || uri.contains("/zkres")) {

            chain.doFilter(request, response);
            return;
        }

        if (staticResource) {
            chain.doFilter(request, response);
            return;
        }

        if (callbackPage || logoutPage) {
            chain.doFilter(request, response);
            return;
        }

        if (!currentUserService.isLoggedIn()) {
            String targetRedirect = authUrl
                    + "?response_type=code"
                    + "&client_id=" + clientId
                    + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
                    + "&scope=openid";
            resp.sendRedirect(targetRedirect);

            return;
        }

        chain.doFilter(request, response);
    }
}
