package com.rey.template.security;

import com.rey.template.dto.MenuDTO;
import com.rey.template.dto.ResponsibilityDTO;
import com.rey.template.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CurrentUserService {

    @Autowired
    private UserSession userSession;

    @Autowired
    private MenuService menuService;

    public void loginAsAdmin() {

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

    public void loginAsMaker() {

        userSession.setUsername("REY Maker");
        userSession.setFullName("Reynard Maker");

        userSession.setResponsibilities(
                List.of(
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

        userSession.setResponsibilities(null);
        userSession.setCurrentResponsibility(null);
    }

    public ResponsibilityDTO getCurrentResponsibility() {
        return userSession.getCurrentResponsibility();
    }

    public String getCurrentUsername() {
        return userSession.getUsername();
    }

    public List<ResponsibilityDTO> getResponsibilities() {
        return userSession.getResponsibilities();
    }

    public void changeResponsibility(String responsibilityCode) {

        System.out.println("selected = " + responsibilityCode);

        userSession.getResponsibilities()
                .forEach(r ->
                        System.out.println(
                                r.getCode() + " | " + r.getName()
                        ));

        ResponsibilityDTO selected =
                userSession.getResponsibilities()
                        .stream()
                        .filter(r -> r.getCode().equals(responsibilityCode))
                        .findFirst()
                        .orElseThrow();

        userSession.setCurrentResponsibility(selected);
    }

    public boolean hasMenu(String menuId) {

        List<MenuDTO> menus =
                menuService.getMenus(
                        getCurrentUsername(),
                        getCurrentResponsibility().getCode()
                );

        return menus.stream()
                .anyMatch(menu ->
                        menu.getId().equals(menuId));
    }
}
