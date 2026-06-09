package com.rey.template.viewmodel;

import com.rey.template.security.CurrentUserService;
import com.rey.template.util.UrlConstant;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;

@VariableResolver(DelegatingVariableResolver.class)
public class LoginVM {

    @WireVariable
    private CurrentUserService currentUserService;

    private String username;
    private String password;

    @Init
    public void init() {
        if (currentUserService.isLoggedIn()) {
            Executions.getCurrent().sendRedirect(UrlConstant.URL_MAIN_ZUL);
        }
    }

    @Command
    public void login() {
        System.out.println("username = " + username);
        System.out.println("password = " + password);

        try {
            currentUserService.login(username, password);
            Executions.sendRedirect(UrlConstant.URL_MAIN_ZUL);
        } catch (Exception e) {
            org.zkoss.zk.ui.util.Clients.showNotification(
                    e.getMessage(),
                    "error",
                    null,
                    "middle_center",
                    3000
            );
        }
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}