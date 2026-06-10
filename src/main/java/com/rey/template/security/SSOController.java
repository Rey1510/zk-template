package com.rey.template.security;

import com.rey.template.util.UrlConstant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
public class SSOController {

    @Value("${sso.keycloak.token-url}")
    private String tokenUrl;

    @Value("${sso.keycloak.userinfo-url}")
    private String userinfoUrl;

    @Value("${sso.keycloak.client-id}")
    private String clientId;

    @Value("${sso.keycloak.client-secret:#{null}}")
    private String clientSecret;

    @Value("${sso.keycloak.redirect-uri}")
    private String redirectUri;

    @Value("${sso.keycloak.logout-url}")
    private String logoutUrl;

    private final CurrentUserService currentUserService;
    private final RestTemplate restTemplate = new RestTemplate();

    public SSOController(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    @GetMapping("/login/oauth2/code/keycloak")
    public String callback(
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "error_description", required = false) String errorDescription) {
        if (error != null || code == null) {
            System.err.println("SSO authentication error: " + error + " - " + errorDescription);
            return "redirect:" + UrlConstant.URL_LOGIN_ZUL + "?error=sso_failed";
        }

        try {
            // 1. Exchange Code for Access Token
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "authorization_code");
            body.add("code", code);
            body.add("redirect_uri", redirectUri);
            body.add("client_id", clientId);
            if (clientSecret != null && !clientSecret.trim().isEmpty()) {
                body.add("client_secret", clientSecret);
            }

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                throw new RuntimeException("Failed to exchange code for token");
            }

            Map<String, Object> tokenBody = response.getBody();
            System.out.println("SSO Token Response: " + tokenBody);
            String accessToken = (String) tokenBody.get("access_token");
            if (accessToken == null) {
                throw new RuntimeException("Access token is missing in token response");
            }

            // 2. Retrieve User Details from Userinfo Endpoint
            HttpHeaders userinfoHeaders = new HttpHeaders();
            userinfoHeaders.setBearerAuth(accessToken);

            HttpEntity<Void> userinfoRequest = new HttpEntity<>(userinfoHeaders);
            ResponseEntity<Map> userinfoResponse = restTemplate.exchange(
                    userinfoUrl,
                    HttpMethod.GET,
                    userinfoRequest,
                    Map.class);

            if (userinfoResponse.getStatusCode() != HttpStatus.OK || userinfoResponse.getBody() == null) {
                throw new RuntimeException("Failed to fetch user info from Keycloak");
            }

            Map<String, Object> userinfoBody = userinfoResponse.getBody();
            System.out.println("SSO UserInfo Response: " + userinfoBody.get("access_token"));
            String username = (String) userinfoBody.get("preferred_username");
            if (username == null) {
                username = (String) userinfoBody.get("sub");
            }

            if (username == null) {
                throw new RuntimeException("Username could not be determined from userinfo response");
            }

            // 3. Login using custom CurrentUserService
            currentUserService.loginSso(username);

            return "redirect:" + UrlConstant.URL_MAIN_ZUL;

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:" + UrlConstant.URL_LOGIN_ZUL + "?error=" + e.getMessage();
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        currentUserService.logout();
        if (request.getSession() != null) {
            request.getSession().invalidate();
        }
        try {
            return "redirect:" + logoutUrl
                    + "?client_id=" + clientId
                    + "&post_logout_redirect_uri="
                    + URLEncoder.encode("http://localhost:8080/login.zul", StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "redirect:/";
        }
    }
}
