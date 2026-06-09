package com.rey.template.security;

import com.rey.template.dto.MenuDTO;
import com.rey.template.dto.ResponsibilityDTO;
import com.rey.template.repository.MstUserRepository;
import com.rey.template.repository.RelUserRoleRepository;
import com.rey.template.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CurrentUserService {

    @Autowired
    private UserSession userSession;

    @Autowired
    private MstUserRepository userRepository;

    @Autowired
    private RelUserRoleRepository userRoleRepository;

    @Autowired
    private MenuService menuService;

    public void login(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new RuntimeException("Username and password cannot be empty");
        }

        var mstUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!mstUser.getPassword().equals(password)) {
            throw new RuntimeException("Incorrect password");
        }

        if (mstUser.getActive() != null && !mstUser.getActive()) {
            throw new RuntimeException("User is inactive");
        }

        var userRoles = userRoleRepository.findByUserUsername(username);
        System.out.println(
                "roles = " + userRoles.size()
        );

        if (userRoles.isEmpty()) {
            throw new RuntimeException("User has no roles/responsibilities assigned");
        }

        List<ResponsibilityDTO> responsibilities = userRoles.stream()
                .map(ur -> new ResponsibilityDTO(ur.getRole().getRoleCode(), ur.getRole().getRoleName()))
                .collect(Collectors.toList());

        userSession.setUsername(mstUser.getUsername());
        userSession.setFullName(mstUser.getFullName());
        userSession.setResponsibilities(responsibilities);
        userSession.setCurrentResponsibility(responsibilities.getFirst());
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
