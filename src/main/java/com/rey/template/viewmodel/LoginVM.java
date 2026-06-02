package com.rey.template.viewmodel;

import com.rey.template.security.CurrentUserService;
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

    @Init
    public void init() {

        if (currentUserService.isLoggedIn()) {

            Executions.sendRedirect("/main.zul");

        }
    }

    @Command
    public void login() {

        currentUserService.loginDummy();

        Executions.sendRedirect("/main.zul");
    }
}