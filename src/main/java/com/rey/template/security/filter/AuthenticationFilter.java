package com.rey.template.security.filter;

import com.rey.template.security.CurrentUserService;
import com.rey.template.util.UrlConstant;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthenticationFilter implements Filter {

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

        System.out.println(
                "URI=" + uri
                        + ", loggedIn=" + currentUserService.isLoggedIn()
        );

        boolean loginPage = uri.endsWith(UrlConstant.URL_LOGIN_ZUL);

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

        if (!currentUserService.isLoggedIn() && !loginPage) {
            resp.sendRedirect(
                    req.getContextPath() + UrlConstant.URL_LOGIN_ZUL
            );

            return;
        }

        if (currentUserService.isLoggedIn() && loginPage) {
            resp.sendRedirect(
                    req.getContextPath() + UrlConstant.URL_MAIN_ZUL);

            return;
        }

        chain.doFilter(request,response);
    }
}
