package com.rey.template.security;

import com.rey.template.dto.ResponsibilityDTO;
import com.rey.template.service.MenuService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CurrentUserService {

    @Autowired
    private UserSession userSession;

    public void loginDummy() {

        userSession.setUsername("REY");
        userSession.setFullName("Reynard A");

        userSession.setResponsibilities(
                List.of(
                        new ResponsibilityDTO("ADMIN", "Administrator"),
                        new ResponsibilityDTO("MAKER", "Maker")
                )
        );

        userSession.setCurrentResponsibility(
                userSession.getResponsibilities().getFirst()
        );

    }

    public boolean isLoggedIn() {
        return userSession.isLoggedIn();
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void logout() {
        userSession.setUsername(null);
        userSession.setFullName(null);
    }

    public ResponsibilityDTO getCurrentResponsibility() {
        return userSession.getCurrentResponsibility();
    }

    public String getCurrentUsername() {
        return userSession.getUsername();
    }
}
