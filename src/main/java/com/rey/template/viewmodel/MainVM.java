package com.rey.template.viewmodel;

import com.rey.template.security.CurrentUserService;
import com.rey.template.service.GreetingService;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;

@VariableResolver(DelegatingVariableResolver.class)
public class MainVM {

    @WireVariable
    private CurrentUserService currentUserService;

    private String username;

    @Init
    public void init() {

        if (!currentUserService.isLoggedIn()) {
            Executions.sendRedirect("/login.zul");
            return;
        }

        username =
                currentUserService
                        .getUserSession()
                        .getUsername();
    }

    public String getUsername() {
        return username;
    }
}