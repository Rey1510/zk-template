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

    @Init
    public void init() {

        if (currentUserService.isLoggedIn()) {

            Executions.getCurrent().sendRedirect(UrlConstant.URL_MAIN_ZUL);

        }
    }

    @Command
    public void loginAsAdmin() {

        currentUserService.loginAsAdmin();

        Executions.sendRedirect(UrlConstant.URL_MAIN_ZUL);
    }

    @Command
    public void loginAsMaker() {

        currentUserService.loginAsMaker();

        Executions.sendRedirect(UrlConstant.URL_MAIN_ZUL);
    }
}